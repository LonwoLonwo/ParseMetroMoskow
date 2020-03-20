import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.Line;
import core.Station;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HTMLtoJSON {
    private static final String filePath = "src\\main\\resources\\mapMetro5.json";
    private static HashMap<String, Line> lines = new HashMap();
    private static ArrayList<Station> stations;
    private static PojoForJson pFJ = new PojoForJson();

    public static void main(String[] args) throws IOException {
        Document page = getPage();

        Elements tableStandard = page.select("table[class~=(standard)+]");

        stations = new ArrayList<>();

        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        for (Element element : tableStandard) {
            Element tBodyUnderground = element.select("tbody").first();
            Elements trUnderground = tBodyUnderground.select("tr");
            for (int i = 1; i < trUnderground.size(); i++) {
                String stationName;
                if (!(trUnderground.get(i).select("td").select("span").isEmpty())) {
                    String lineNumber = trUnderground.get(i).select("td").select("span").get(0).text();
                    String lineName = trUnderground.get(i).select("td").get(0).select("a[href]").attr("title");
                    Line line;
                    if (lines.isEmpty() || !lines.containsKey(lineNumber)) {
                        line = new Line(lineNumber, lineName);
                        lines.put(lineNumber, line);
                        String colorNameAttr = trUnderground.get(i).select("td").get(0).attr("style");
                        if (!colorNameAttr.isEmpty()) {
                            String colorName = colorNameAttr.substring(colorNameAttr.indexOf("#"));
                            line.setColor(colorName);
                        }
                    } else {
                        line = lines.get(lineNumber);
                    }
                    pFJ.addLines(lineNumber, line);
                    if (trUnderground.get(i).select("td").get(1).select("span").size() == 1) {
                        stationName = trUnderground.get(i).select("td").get(1).select("span").text();
                    } else {
                        stationName = trUnderground.get(i).select("td").get(1).text();
                    }
                    Station station = new Station(stationName, line);
                    stations.add(station);
                    line.addStation(station);
                    if (trUnderground.get(i).select("td").get(0).select("span").size() > 3) {
                        String lnNumber = trUnderground.get(i).select("td").get(0).select("span").get(2).text();
                        if (lines.containsKey(lineNumber)) {
                            Line anotherLine = lines.get(lnNumber);
                            Station dopStation = new Station(stationName, lines.get(lnNumber));
                            stations.add(dopStation);
                            anotherLine.addStation(dopStation);
                        }
                    }
                }
            }
            //addConnections(trUnderground);
        }
        stations.sort(Comparator.comparing(Station::getName));

        for(Map.Entry<String, Line> lns: lines.entrySet()){
            List<Station> stat = lns.getValue().getStations();
            String[] stationsNames = new String[stat.size()];
            for(int i = 0; i < stat.size(); i++){
                stationsNames[i] = stat.get(i).getName();
            }
            pFJ.addStations(lns.getKey(), stationsNames);
        }

        //запись в JSON
        try(Writer writer = new FileWriter(filePath)){
            gson.toJson(pFJ, writer);
        }

        System.out.println("Done");
    }

    private static Document getPage() throws IOException {
        //Вариант с подключение к сайту напрямую
        //String url = "https://clck.ru/MCHxP";
        //Document page = Jsoup.parse(new URL(url), 3000);

        String filePath = "src\\main\\resources\\data\\site.html";
        String htmlFile = parseFile(filePath);

        return Jsoup.parse(htmlFile);
    }

    private static String parseFile(String path){
        StringBuilder builder = new StringBuilder();
        try{
            List<String> lines = Files.readAllLines(Paths.get(path));
            lines.forEach(line -> builder.append(line).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static void addConnections(Elements trElement){
        for(int i = 1; i < trElement.size(); i++) {
            if(!(trElement.get(i).select("td").select("span").isEmpty())) {
                String stationName;
                if (trElement.get(i).select("td").get(1).select("span").size() == 1) {
                    stationName = trElement.get(i).select("td").get(1).select("span").text();
                } else {
                    stationName = trElement.get(i).select("td").get(1).text();
                }
                Station station = null;
                for (Station st : stations) {
                    if (st.getName().equals(stationName)) {
                        station = st;
                    }
                }
                ArrayList<Station> connectStations = new ArrayList<>();
                if (!(trElement.get(i).select("td").get(3).attr("data-sort-value").equals("Infinity"))) {
                    Elements elements = trElement.get(i).select("td").get(3).select("span");
                    if (elements.size() == 2) {
                        String stationConnectionNumber = elements.get(0).text();
                        String attr = elements.get(1).select("a[href]").attr("href");
                        String connectionStationName = attrToName(attr);
                        Station connectStation = null;
                        for (Station st : stations) {
                            if (st.getName().equals(connectionStationName) && st.getLine().getNumber().equals(stationConnectionNumber)) {
                                connectStation = st;
                            }
                        }
                        connectStations.add(connectStation);
                        connectStations.add(station);
                        pFJ.addConnections(connectStations);
                    }
                    //else if(elements.size() == 4)/else if(elements.size() == 6)
                }
            }
        }
    }

    //вспомогательный метод для определения названий пересадок
    private static String attrToName(String attr){
        String attrCrop = attr.substring(attr.lastIndexOf("/")+1, attr.indexOf("(")-1);
        String decoderString = "";
        try {
            decoderString = java.net.URLDecoder.decode(attrCrop, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String connectionStationName;
        if(decoderString.contains("_")){
            connectionStationName = decoderString.replace("_", " ");
        }
        else {
            connectionStationName = decoderString;
        }
        return connectionStationName;
    }
}

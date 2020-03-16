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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static final String filePath = "F:\\Java Projects\\ParseMetroMoskow\\src\\main\\resources\\mapMetro4.json";
    private static ArrayList<Line> lines = new ArrayList<>();
    private static TreeSet<Station> stations;
    private static PojoForJson pFJ = new PojoForJson();

    public static void main(String[] args) throws IOException {
        Document page = getPage();

        Element tableStandard = page.select("table[class~=(standard)+]").first();
        Element tBody = tableStandard.select("tbody").first();
        Elements tr = tBody.select("tr");

        stations = new TreeSet<>(Comparator.comparing(Station::getName));

        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        for(int i = 1; i < tr.size(); i++){
            String stationName;
            String lineNumber = tr.get(i).select("td").select("span").get(0).text();
            String lineName = tr.get(i).select("td").get(0).select("a[href]").attr("title");
            Line line;
            if(!lines.isEmpty()){
                if (lines.stream().anyMatch(element -> element.getName().equals(lineName))){
                    line = lines.stream().filter(element -> element.getName().equals(lineName)).findFirst().get();
                }
                else{
                    line = new Line(lineNumber, lineName);
                    lines.add(line);
                    String colorNameAttr = tr.get(i).select("td").get(0).attr("style");
                    if(!colorNameAttr.isEmpty()) {
                        String colorName = colorNameAttr.substring(colorNameAttr.indexOf("#"));
                        System.out.println(line.getName() + " " + colorName);
                        line.setColor(colorName);
                    }
                    pFJ.addLines(line);
                }
            }
            else{
                line = new Line(lineNumber, lineName);
                lines.add(line);
                String colorNameAttr = tr.get(i).select("td").get(0).attr("style");
                if(!colorNameAttr.isEmpty()) {
                    String colorName = colorNameAttr.substring(colorNameAttr.indexOf("#"));
                    System.out.println(line.getName() + " " + colorName);
                    line.setColor(colorName);
                    pFJ.addLines(line);
                }
            }
            if(tr.get(i).select("td").get(1).select("span").size() == 1){
                stationName = tr.get(i).select("td").get(1).select("span").text();
            }
            else{
                stationName = tr.get(i).select("td").get(1).text();
            }
            Station station = new Station(stationName, line);
            stations.add(station);
            line.addStation(station);
        }

        Map<String, ArrayList<Line>> map = new HashMap<>();
        map.put("lines", lines);

        for(Line lns : lines){
            List<Station> stat = lns.getStations();
            String[] stationsNames = new String[stat.size()];
            for(int i = 0; i < stat.size(); i++){
                stationsNames[i] = stat.get(i).getName();
            }
            pFJ.addStations(lns.getNumber(), stationsNames);
        }

        addConnections(tr);

        try(Writer writer = new FileWriter(filePath)){
            gson.toJson(pFJ, writer);
        }

        //lines.forEach(System.out::println);
        //stations.forEach(System.out::println);
    }

    private static Document getPage() throws IOException {
        //String url = "https://ru.wikipedia.org/wiki/%D0%A1%D0%BF%D0%B8%D1%81%D0%BE%D0%BA_%D1%81%D1%82%D0%B0%D0%BD%D1%86%D0%B8%D0%B9_%D0%9C%D0%BE%D1%81%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_%D0%BC%D0%B5%D1%82%D1%80%D0%BE%D0%BF%D0%BE%D0%BB%D0%B8%D1%82%D0%B5%D0%BD%D0%B0";

        //Document page = Jsoup.parse(new URL(url), 3000);

        String filePath = "src\\main\\resources\\data\\site.html";
        String htmlFile = parseFile(filePath);
        Document page = Jsoup.parse(htmlFile);

        return page;
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
            String stationName;
            if(trElement.get(i).select("td").get(1).select("span").size() == 1){
                stationName = trElement.get(i).select("td").get(1).select("span").text();
            }
            else{
                stationName = trElement.get(i).select("td").get(1).text();
            }
            Station station = null;
            for(Station st : stations){
                if(st.getName().equals(stationName)){
                    station = st;
                }
            }
            ArrayList<Station> connectStations = new ArrayList<>();
            if (!(trElement.get(i).select("td").get(3).attr("data-sort-value").equals("Infinity"))) {
                Elements elements = trElement.get(i).select("td").get(3).select("span");
                if (elements.size() == 1) {
                    String attr = elements.get(1).attr("a[href]");
                    String connectionStationName = attrToName(attr);
                    Station connectStation = null;
                    for(Station st : stations){
                        if(st.getName().equals(connectionStationName)){
                            connectStation = st;
                        }
                    }
                    connectStations.add(connectStation);
                    connectStations.add(station);
                }
            }
            pFJ.addConnections(connectStations);
        }
    }

    private static String attrToName(String attr){
        String attrCrop = attr.substring(attr.lastIndexOf("/")+1, attr.indexOf("(")-1);
        String connectionStationName = null;
        try {
            connectionStationName = java.net.URLDecoder.decode(attrCrop, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return connectionStationName;
    }
}

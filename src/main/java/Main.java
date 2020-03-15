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
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static final String filePath = "F:\\Java Projects\\ParseMetroMoskow\\src\\main\\resources\\mapMetro.json";

    public static void main(String[] args) throws IOException {
        Document page = getPage();

        Element tableStandard = page.select("table[class~=(standard)+]").first();
        Element tBody = tableStandard.select("tbody").first();
        Elements tr = tBody.select("tr");

        ArrayList<Line> lines = new ArrayList<>();
        ArrayList<Station> stations = new ArrayList<>();

        GsonBuilder builder = new GsonBuilder();
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
                }
            }
            else{
                line = new Line(lineNumber, lineName);
                lines.add(line);
            }
            //gson.toJson(line, new FileWriter("src\\main\\resources\\mapMetro.json"));
            if(tr.get(i).select("td").get(1).select("span").size() == 1){
                stationName = tr.get(i).select("td").get(1).select("span").text();
            }
            else{
                stationName = tr.get(i).select("td").get(1).text();
            }
            stations.add(new Station(stationName, line));
        }

        Map<String, ArrayList<Line>> map = new HashMap<>();
        map.put("lines", lines);


        try(Writer writer = new FileWriter(filePath, true)){
            gson.toJson(map, writer);
        }
        //String jString = gson.toJson(lines);

        //System.out.println(jString);
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
}

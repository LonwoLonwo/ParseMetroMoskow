import com.google.gson.annotations.Expose;
import core.Line;
import core.Station;

import java.util.*;

public class PojoForJson {
    @Expose
    private Map<String, String[]> stations = new HashMap<>();

    //@Expose
    private ArrayList<ArrayList<Station>> connections = new ArrayList<>();

    @Expose
    private HashMap<String, Line> lines = new HashMap<>();

    public void addStations(String lineNumber, String[] stationsName){
        stations.put(lineNumber, stationsName);
    }

    public List<Station> getStationsOneLine(String lineNumber){
        if(stations.containsKey(lineNumber)){
            List<Station> stationsOneLine = new ArrayList<>();
            Line line = lines.get(lineNumber);
            String[] stationsName = stations.get(lineNumber);
            for(String name : stationsName){
                stationsOneLine.add(new Station(name, line));
            }
            return stationsOneLine;
        }
        else{
            System.out.println("Wrong line number");
            return null;
        }
    }

    public ArrayList<Station> getStationSet(){
        ArrayList<Station> allStations = new ArrayList<>();
        for(Map.Entry<String, String[]> map : stations.entrySet()){
            String lineNumber = map.getKey();
            Line line = lines.get(lineNumber);
            String[] stationsName = map.getValue();
            for(String name : stationsName){
                allStations.add(new Station(name, line));
            }
        }
        allStations.sort(Comparator.comparing(Station::getName));
        return allStations;
    }

    public void addConnections(ArrayList<Station> connect){
        connections.add(connect);
    }

    public void addLines(String lineNumber, Line line){
        lines.put(lineNumber, line);
    }

    public HashMap<String, Line> getLinesList(){
        return lines;
    }

}

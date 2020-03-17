import com.google.gson.annotations.Expose;
import core.Line;
import core.Station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PojoForJson {
    @Expose
    private Map<String, String[]> stations = new HashMap<>();

    //@Expose
    private ArrayList<ArrayList<Station>> connections = new ArrayList<>();

    @Expose
    private ArrayList<Line> lines = new ArrayList<>();

    public void addStations(String lineNumber, String[] stationsName){
        stations.put(lineNumber, stationsName);
    }

    public void addConnections(ArrayList<Station> connect){
        connections.add(connect);
    }

    public void addLines(Line line){
        lines.add(line);
    }

}

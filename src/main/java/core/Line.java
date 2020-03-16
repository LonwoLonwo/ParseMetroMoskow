package core;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Line{
    @Expose
    private String number;
    @Expose
    private String name;

    private List<Station> stations;

    @Expose
    private String color;

    public Line(String number, String name) {
        this.number = number;
        this.name = name;
        stations = new ArrayList<>();
    }

    public String getNumber() {
        return number;
    }


    public String getName() {
        return name;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public List<Station> getStations() {
        return stations;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }

}

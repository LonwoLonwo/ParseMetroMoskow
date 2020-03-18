package core;

import com.google.gson.annotations.Expose;

public class Station
{

    private Line line;
    @Expose
    private String name;

    public Station(String name, Line line)
    {
        this.name = name;
        this.line = line;
    }

    public Line getLine()
    {
        return line;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Station station = (Station) obj;

        return getName().equals(station.getName()) && getLine().getNumber().equals(station.getLine().getNumber());
    }
}

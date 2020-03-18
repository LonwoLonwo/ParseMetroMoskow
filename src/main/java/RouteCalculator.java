import core.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteCalculator {
    private static double interStationDuration = 2.5;

    public static double calculateDuration(List<Station> route)
    {
        double duration = 0;

        for(int i = 1; i < route.size(); i++)
        {
            duration += interStationDuration;
        }
        return duration;
    }

    //=========================================================================

    protected List<Station> getRoute(Station from, Station to, PojoForJson pojo)
    {
        if(!from.getLine().equals(to.getLine())) {
            return null;
        }
        ArrayList<Station> route = new ArrayList<>();
        String lineNumber = from.getLine().getNumber();
        List<Station> stations = pojo.getStationsOneLine(lineNumber);
        int direction = 0;
        for(Station station : stations)
        {
            if(direction == 0)
            {
                if(station.equals(from)) {
                    direction = 1;
                } else if(station.equals(to)) {
                    direction = -1;
                }
            }

            if(direction != 0) {
                route.add(station);
            }

            if((direction == 1 && station.equals(to)) ||
                    (direction == -1 && station.equals(from))) {
                break;
            }
        }
        if(direction == -1) {
            Collections.reverse(route);
        }
        return route;
    }

}

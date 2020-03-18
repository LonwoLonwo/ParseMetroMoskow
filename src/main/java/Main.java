import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.Line;
import core.Station;

import java.io.*;
import java.util.*;

public class Main {
    private static String dataFile = "F:\\Java Projects\\ParseMetroMoskow\\src\\main\\resources\\mapMetro5.json";
    private static Scanner scanner;
    private static ArrayList<Station> stationList;
    private static PojoForJson pojo;

    public static void main(String[] args) {

        RouteCalculator calculator = new RouteCalculator();

        getObjectFromJson();

        stationList = pojo.getStationSet();

        System.out.println("Программа расчёта маршрутов метрополитена Москвы\n");
        scanner = new Scanner(System.in);
        try {
            for(;;)
            {
                Station from = takeStation("Введите станцию отправления:");
                Station to = takeStation("Введите станцию назначения:");

                if(!(from.getLine().getNumber().equals(to.getLine().getNumber()))){
                    System.out.println("Станция отправления и станция назначения находятся на разных линиях. Построения машрута с пересадками возможно только в платной версии программы.");
                    break;
                }

                List<Station> route = calculator.getRoute(from, to, pojo);
                System.out.println("Маршрут:");
                printRoute(route);

                System.out.println("Длительность: " +
                        RouteCalculator.calculateDuration(route) + " минут");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printRoute(List<Station> route) {
        for(Station station : route){
            System.out.println("\t" + station.getName());
        }
    }

    private static Station takeStation(String message)
    {
        for(;;)
        {
            System.out.println(message);
            String line = scanner.nextLine().trim();
            if(stationList.stream().anyMatch(elem -> elem.getName().equals(line))){
                if(stationList.stream().filter(elem -> elem.getName().equals(line)).count() > 1)
                {
                    System.out.print("Уточните номер линии. Список номеров линий: ");
                    ArrayList<Line> lines = pojo.getLinesList();
                    lines.forEach(el -> System.out.print(el.getNumber() + "  "));
                    String lineNumber = scanner.nextLine().trim();
                    if(lines.stream().anyMatch(elem -> elem.getNumber().equals(lineNumber))){
                        return stationList.stream().filter(elem -> elem.getName().equals(line)).filter(elem -> elem.getLine().getNumber().equals(lineNumber)).findFirst().get();
                    }
                    else{
                        System.out.println("Номер линии указано некорректно.");
                    }
                }
                else return stationList.stream().filter(elem -> elem.getName().equals(line)).findFirst().get();
            }
            System.out.println("Станция не найдена :(");
        }
    }

    public static void getObjectFromJson() {
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        try(Reader reader = new FileReader(dataFile)){
            pojo = gson.fromJson(reader, PojoForJson.class);
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

}

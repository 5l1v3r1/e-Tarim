package sametileri.project.objects;

import java.util.ArrayList;
import java.util.List;

public class Tarla {

    private String name;
    private LatLng location;
    private int tarlaID;
    private List<Sensor> sensorList = new ArrayList<>();

    public Tarla(String name, LatLng location,  int tarlaID) {
        this.name = name;
        this.location = location;
        this.tarlaID = tarlaID;
    }

    public Tarla(){}

    public int getTarlaID() {
        return tarlaID;
    }

    public void setTarlaID(int tarlaID) {
        this.tarlaID = tarlaID;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }
}

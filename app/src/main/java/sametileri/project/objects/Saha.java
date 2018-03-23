package sametileri.project.objects;

import java.util.List;

/**
 * Created by Samet on 3/27/2017.
 */

public class Saha {

    private String name;
    LatLng location;
    List<Tarla> tarlaList;

    public Saha(String name, LatLng location, List<Tarla> tarlaList) {
        this.name = name;
        this.location = location;
        this.tarlaList = tarlaList;
    }

    public Saha() {

    }

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public List<Tarla> getTarlaList() {
        return tarlaList;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTarlaList(List<Tarla> tarlaList) {
        this.tarlaList = tarlaList;
    }
}

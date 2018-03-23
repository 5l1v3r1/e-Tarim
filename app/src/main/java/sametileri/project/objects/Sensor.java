package sametileri.project.objects;

/**
 * Created by samet on 6.05.2017.
 */

public class Sensor {

    private int id;
    private String name;
    private int value;

    public Sensor(){}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

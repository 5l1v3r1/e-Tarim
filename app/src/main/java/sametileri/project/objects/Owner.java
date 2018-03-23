package sametileri.project.objects;

/**
 * Created by Samet on 3/27/2017.
 */

public class Owner {

    private String name;
    private String surname;
    private Boolean statu;

    public Owner(String name, String surname, Boolean statu) {
        this.name = name;
        this.surname = surname;
        this.statu = statu;
    }

    public String getName() {
        return name;
    }

    public Boolean getStatu() {
        return statu;
    }

    public String getSurname() {
        return surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatu(Boolean statu) {
        this.statu = statu;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}

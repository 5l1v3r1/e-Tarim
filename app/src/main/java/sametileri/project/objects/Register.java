package sametileri.project.objects;

public class Register {

    private String name;
    private String surname;
    private boolean statu;
    private String username;
    private String password;
    private Tarla[] tarlas;

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isStatu() {
        return statu;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Tarla[] getTarlas() {
        return tarlas;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setStatu(boolean statu) {
        this.statu = statu;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTarlas(Tarla[] tarlas) {
        this.tarlas = tarlas;
    }
}

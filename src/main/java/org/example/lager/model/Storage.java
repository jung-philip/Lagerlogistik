package org.example.lager.model;

public class Storage {
    private int id;
    private String location;
    private String name;

    public Storage() {
    }

    public Storage(String location, String name) {
        this.location = location;
        this.name = name;
    }

    public Storage(int id, String location, String name) {
        this.id = id;
        this.location = location;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Storage{id=" + id +
                ", location='" + location + '\'' +
                ", name='" + name + '\'' + '}';
    }
}
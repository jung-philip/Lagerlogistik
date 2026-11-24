package org.example.lager.model;

public class Item {
    private int id;
    private String name;
    private int quantity;
    private Storage storage;
    private int MinInventory;

    public Item() {
    }

    public Item(String name, int quantity, Storage storage, int MinInventory) {
        this.name = name;
        this.quantity = quantity;
        this.storage = storage;
        this.MinInventory = MinInventory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public int getMinInventory() {
        return MinInventory;
    }

    public void setMinInventory(int minInventory) {
        MinInventory = minInventory;
    }

    @Override
    public String toString() {
        return "Item{id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", storage=" + storage +
                ", MinInventory=" + MinInventory + '}';
    }
}
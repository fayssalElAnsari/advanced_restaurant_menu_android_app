package com.alansaridev.streetburger.model;

public class VariantModel {
    private String name;
    private boolean checked;
    private double price;
    private int maxOptions;

    public VariantModel() {
    }

    public VariantModel(String name, boolean checked, double price) {
        this.name = name;
        this.checked = checked;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxOptions() {
        return maxOptions;
    }

    public void setMaxOptions(int maxOptions) {
        this.maxOptions = maxOptions;
    }
}

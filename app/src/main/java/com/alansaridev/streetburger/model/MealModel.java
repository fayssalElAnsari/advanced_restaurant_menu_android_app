package com.alansaridev.streetburger.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MealModel implements Serializable {
    private String name;
    private ArrayList<String> categorie;
    private String description;
    private boolean disponible;
    private String image;
    private double prix;

    public MealModel() {
    }

    public MealModel(ArrayList<String> categorie, String description, boolean disponible, String image, double prix) {
        this.categorie = categorie;
        this.description = description;
        this.disponible = disponible;
        this.image = image;
        this.prix = prix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getCategorie() {
        return categorie;
    }

    public void setCategorie(ArrayList<String> categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }
}

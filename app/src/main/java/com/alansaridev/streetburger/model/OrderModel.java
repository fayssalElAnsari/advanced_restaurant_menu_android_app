package com.alansaridev.streetburger.model;

import android.widget.CheckBox;

import java.util.ArrayList;

public class OrderModel {
    String orderName;
    String clientName;
    String storeName;
    ArrayList<CheckBox> checkCheckboxesList;

    public OrderModel() {
    }

    public OrderModel(String orderName, String clientName, String storeName, ArrayList<CheckBox> checkCheckboxesList) {
        this.orderName = orderName;
        this.clientName = clientName;
        this.storeName = storeName;
        this.checkCheckboxesList = checkCheckboxesList;
    }


}

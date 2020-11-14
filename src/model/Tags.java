package model;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class Tags {
    private SimpleStringProperty name;
    private SimpleStringProperty value;

    public Tags(String name,String value){
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleStringProperty(value);
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public void setValues(String value) {
        this.value.set(value) ;
    }

    public String getName() {
        return name.get();
    }

    public String getValue(){
        return value.get();
    }
}

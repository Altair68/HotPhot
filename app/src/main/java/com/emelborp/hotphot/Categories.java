package com.emelborp.hotphot;

/**
 * Created by Manuel on 10.02.2015.
 */
public enum Categories {
    PANORAMA(0, "Panorama"),
    SELFIE(1, "Selfie"),
    LANDSCAPE(2, "Landschaft"),
    ARCHITECTURE(3, "Architektur"),
    CULTURE(4, "Kultur");

    private int id = 0;
    private String name = null;

    Categories(int anID, String aName) {
        this.id = anID;
        this.name = aName;
    }

    public String getName() {return name;}

    public int getId() {return id;}
}

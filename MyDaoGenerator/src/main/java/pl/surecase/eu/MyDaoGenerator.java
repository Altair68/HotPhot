package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(3, "hotphot");
//        Entity box = schema.addEntity("Box");
//        box.addIdProperty();
//        box.addStringProperty("name");
//        box.addIntProperty("slots");
//        box.addStringProperty("description");

        Entity marker = schema.addEntity("Marker");
        marker.addIdProperty();
        marker.addStringProperty("name");
        marker.addDoubleProperty("lat");
        marker.addDoubleProperty("lon");
        marker.addStringProperty("cat");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}

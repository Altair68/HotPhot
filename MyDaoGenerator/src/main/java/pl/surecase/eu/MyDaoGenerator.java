package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(3, "com.emelborp.hotphot.gen");
        schema.enableKeepSectionsByDefault();
//        Entity box = schema.addEntity("Box");
//        box.addIdProperty();
//        box.addStringProperty("name");
//        box.addIntProperty("slots");
//        box.addStringProperty("description");

        Entity marker = schema.addEntity("Marker");
        marker.addIdProperty().autoincrement().primaryKey();
        marker.addStringProperty("name");
        marker.addDoubleProperty("lat");
        marker.addDoubleProperty("lon");
        marker.addStringProperty("cat");

        Entity profile = schema.addEntity("Profile");
        profile.addIdProperty().autoincrement().primaryKey();
        profile.addStringProperty("name");
        profile.addStringProperty("email");
        profile.addDateProperty("birthdate");
        profile.addByteArrayProperty("picture");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}

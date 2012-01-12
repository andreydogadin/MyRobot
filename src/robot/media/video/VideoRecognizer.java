package robot.media.video;

import de.offis.faint.controller.MainController;
import de.offis.faint.model.FaceDatabase;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;
import utils.RobotConsts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/15/11
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoRecognizer {
    private MainController faintController;
    private FaceDatabase faceDB;

    public VideoRecognizer() {
        faintController = MainController.getInstance();
        try {
            faceDB = FaceDatabase.recoverFromDisk();
        } catch (IOException ex) {
            faceDB = new FaceDatabase();
        }
    }
    // return Person name or null
    public String recognizeFace(String picturePath) {
        HashMap result = null;
        ImageModel model = new ImageModel(picturePath);
        model.initThumbnail();
        Region[] faces = faintController.detectFaces(model, false);
        if (faces == null || faces.length == 0) return null;

        result = faintController.recognizeFace(faces[0]);
        ArrayList<String> c = new ArrayList(result.keySet());
        int maxValue = 0;
        String recognizedPerson = null;
        for (int i = 0; i < c.size(); i++) {
            String person = c.get(i);
            Integer res = Integer.parseInt(result.get(person).toString());
            if (res > maxValue) {
                maxValue = res;
                recognizedPerson = person;
            }
        }
        return recognizedPerson;
    }

    private void writeFacesDB() {
        try {
            faceDB.writeToDisk();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void learnFace(String personName) {
        faceDB.deleteAnnotation(personName);
        writeFacesDB();

        // Get all pictures from appropriate folder
        File filesList = new File(RobotConsts.LEARN_PATH + File.separator + personName);
        String[] personPictures = filesList.list();
        // Get all pictures from appropriate folder
        for (int j = 0; j < personPictures.length; j++) {
            ImageModel model = new ImageModel(RobotConsts.LEARN_PATH + File.separator + personName + File.separator + personPictures[j]);
            model.initThumbnail();
            Region face = faintController.detectFaces(model, false)[0];
            faceDB.put(face, personName);
        }
        writeFacesDB();
    }


    public void learnFaces() {
        File dataDir = new File(RobotConsts.LEARN_PATH);
        String[] personsNames = dataDir.list();

        // Get all persons to learn from db\\learn folder
        for (int i = 0; i < personsNames.length; i++) {
            learnFace(personsNames[i]);
        }
    }
}

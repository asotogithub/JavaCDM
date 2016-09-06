package trueffect.truconnect.api.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by richard.jaldin on 12/1/2015.
 */
public class SerializationUtil {

    private static Logger log = LoggerFactory.getLogger(SerializationUtil.class);

    public static <T> void serialize(T object, String pathFileName) {
        FileOutputStream file = null;
        ObjectOutputStream out = null;
        try {
            file = new FileOutputStream(pathFileName);
            out = new ObjectOutputStream(file);
            out.writeObject(object);
        } catch (FileNotFoundException e) {
            log.warn("File not found.", e);
        } catch(IOException ex){
            log.warn("Cannot perform output.", ex);
        } finally {
            try {
                if (out != null){
                    out.close();
                }
                if (file != null) {
                    file.close();
                }
            } catch(IOException e) {
                log.warn("Error trying to close streams.", e);
            }
        }
    }

    public static <T> T deserialize(String pathFileName) {
        T result = null;
        FileInputStream file = null;
        ObjectInputStream in = null;
        try {
            file = new FileInputStream(pathFileName);
            in = new ObjectInputStream(file);
            result = (T)in.readObject();
        } catch(FileNotFoundException ex) {
            log.warn("Could not find file '{}'", pathFileName);
        } catch(IOException ex) {
            log.warn("Cannot perform input.", ex);
        } catch (ClassNotFoundException ex) {
            log.warn("Cannot perform input. Class not found.", ex);
        } finally {
            try {
                if (in != null){
                    in.close();
                }
                if (file != null) {
                    file.close();
                }
            } catch(IOException e) {
                log.warn("Error trying to close streams.", e);
            }
        }
        return result;
    }
}

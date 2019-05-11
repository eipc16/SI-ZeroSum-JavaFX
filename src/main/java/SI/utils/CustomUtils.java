package SI.utils;

import java.io.*;

public class CustomUtils {

    // Method for creating deep copy of a given object
    // Original author: ddmills
    // Link to origin code: https://gist.github.com/ddmills/5a892c358e511b924c63
    public static Object deepCopy(Object original) {
        Object copy = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(original);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));

            copy = in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return copy;
    }
}

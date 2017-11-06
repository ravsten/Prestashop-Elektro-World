/**
 * Created by Aleksander on 30.10.2017.
 */
import java.io.*;
import java.nio.charset.StandardCharsets;

public class File {
    private OutputStreamWriter writer;

    public File(String path, boolean appendToFile){
        try {
            writer = new OutputStreamWriter(new FileOutputStream(path, appendToFile), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(String textLine) throws IOException {
        writer.write(textLine + "\r\n");
    }

    public void closeFile() throws IOException {
        writer.close();
    }

}
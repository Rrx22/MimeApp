package nl.roex.service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class JavaResourceService {

    public static final Logger LOG = Logger.getLogger("JavaResoruceService");

    public File getFile(String fileName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        Path path = Paths.get(resource.getPath().substring(1));
        File file = new File(path.toString());
        return file;
    }

    public String getContent(String fileName) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LOG.severe("Error parsing resource file: " + fileName);
            e.printStackTrace();
        }
        return sb.toString();
    }
}

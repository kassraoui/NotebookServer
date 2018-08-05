package interpreter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

class FilePath {
    private String path;

    private FilePath(String path) {
        this.path = path;
    }

    String getPath() {
        return path;
    }

    void writeLine(String line, boolean append) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, append));
            writer.write(line);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static FilePath getUniqueFilePath() {
        String uniqueFileName = String.valueOf(System.currentTimeMillis());
        String path = Paths.get(System.getProperty("user.dir"), "scripts", uniqueFileName).toString();
        return new FilePath(path);
    }

    void createIfNotExists() throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile();
    }
}

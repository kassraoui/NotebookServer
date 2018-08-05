package interpreter;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

class Interpreter {
    private static final long PROCESS_TIMEOUT_SEC = 5;
    private String name;
    private String command;
    private FilePath scriptFile;


    private Interpreter(String name, String path) {
        this.name = name;
        this.command = path;
        scriptFile = FilePath.getUniqueFilePath();
    }

    String getName() {
        return name;
    }



    static Interpreter Load(String name) throws IOException, ScriptParseException {
        return new Interpreter(name, loadPath(name));
    }

    Result execute(String code) {
        Process process = null;
        try {
            scriptFile.createIfNotExists();
            scriptFile.writeLine(code, true);
            process = startInterpreterProcess();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor(PROCESS_TIMEOUT_SEC, TimeUnit.SECONDS);
            String line, last = null;
            while ((line = stdout.readLine()) != null) {
                last = line;
            }
            return new Result(last == null ? "" : last);
        } catch (IOException | InterruptedException e) {
            return new Result(e.getMessage());
        } finally {
            if (process != null)
                process.destroy();
        }
    }

    void clearScriptFile() {
        scriptFile.writeLine("", false);
    }

    private static String loadPath(String name) throws IOException, ScriptParseException {
        InputStream input = Interpreter.class.getResourceAsStream("/interpreters.properties");

        Properties prop = new Properties();
        prop.load(input);
        String path = prop.getProperty(name);
        if (path == null || path.isEmpty()) throw new ScriptParseException("Unsupported interpreter " + name);
        return prop.getProperty(name);
    }

    private Process startInterpreterProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command, scriptFile.getPath());
        processBuilder.directory(new File(System.getProperty("user.home")));
        return processBuilder.start();
    }
}

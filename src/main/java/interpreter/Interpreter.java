package interpreter;

import java.io.*;
import java.util.Properties;

public class Interpreter {
    private String name;
    private String path;
    private Process process;


    private Interpreter(String name, String path) throws IOException {
        this.name = name;
        this.path = path;
        process = buildProcess();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }


    public static Interpreter Load(String name) throws IOException, ScriptParseException {
        return new Interpreter(name, loadPath(name));
    }

    public Result execute(String code) {
        try {
            BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            p_stdin.write(code + "\n");
            p_stdin.flush();

            BufferedReader p_stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = p_stdout.readLine();

            return new Result(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result("");
    }

    public void shutdown() {
        process.destroy();
    }

    private static String loadPath(String name) throws IOException, ScriptParseException {
        InputStream input = Interpreter.class.getResourceAsStream("/interpreters.properties");

        Properties prop = new Properties();
        prop.load(input);
        String path = prop.getProperty(name);
        if (path == null || path.isEmpty()) throw new ScriptParseException("Unsupported interpreter " + name);
        return prop.getProperty(name);
    }

    private Process buildProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(getPath().split(" "));
        processBuilder.directory(new File(System.getProperty("user.home")));
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}

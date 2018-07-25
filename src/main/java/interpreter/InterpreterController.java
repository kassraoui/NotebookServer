package interpreter;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interpreter")
public class InterpreterController {

    private Map<String, List<Interpreter>> sessions = new HashMap<>();


    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    @ResponseBody
    public Result execute(@RequestParam("sessionId") String sessionId, @RequestBody Script script) {
        try {
            if (sessionId == null || sessionId.isEmpty()) throw new IllegalArgumentException("A sessionId is required");
            Interpreter interpreter = CreateOrGetInterpreter(sessionId, script.getInterpreterName());
            return interpreter.execute(script.getCode());
        } catch ( ScriptParseException | IOException e) {
            return new Result(e.getMessage());
        }
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
    @ResponseBody
    public Result shutdown(@RequestParam("sessionId") String sessionId, @RequestParam("interpreter") String interpreterName) throws IOException, ScriptParseException {
        if (sessionId == null || sessionId.isEmpty() || interpreterName == null || interpreterName.isEmpty())
            throw new IllegalArgumentException("A sessionId and/or an interpreter name is required");
        Interpreter interpreter = CreateOrGetInterpreter(sessionId, interpreterName);
        interpreter.shutdown();
        return new Result("Interpreter : " + interpreterName + " stopped successfully");
    }

    private Interpreter CreateOrGetInterpreter(String sessionId, String interpreterName) throws IOException, ScriptParseException {
        Interpreter interpreter;
        List<Interpreter> interpreters = sessions.get(sessionId);
        if (interpreters == null) {
            interpreters = new LinkedList<>();
            interpreter = Interpreter.Load(interpreterName);
            interpreters.add(interpreter);
            sessions.put(sessionId, interpreters);
            return interpreter;
        }
        interpreter = interpreters.stream().filter(i -> i.getName().equals(interpreterName)).findFirst().orElse(null);
        if (interpreter == null) {
            interpreter = Interpreter.Load(interpreterName);
            interpreters.add(interpreter);
        }
        sessions.put(sessionId, interpreters);
        return interpreter;
    }
}

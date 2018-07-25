package interpreter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Script {
    @JsonIgnore
    private String code;
    @JsonIgnore
    private String interpreterName;

    public String getCode() {
        return code;
    }
    public String getInterpreterName() {
        return interpreterName;
    }

    @JsonCreator
    public Script(@JsonProperty("code") String line) throws ScriptParseException {
        Parse(line);
    }

    private void Parse(String line) throws ScriptParseException {
        Pattern pattern = Pattern.compile("^%(\\S+)(\\s+)(.+)$");
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) throw new ScriptParseException("Invalid script line");
        this.interpreterName = matcher.group(1);
        this.code = matcher.group(3);
    }
}


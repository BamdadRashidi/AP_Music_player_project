package API_messages;
import com.google.gson.*;


public class Request {
    private String action;
    private JsonObject payload;
    public Request(String action, JsonObject payload) {
        this.action = action;
        this.payload = payload;
    }

    public String getAction() {
        return action;
    }

    public JsonObject getPayload() {
        return payload;
    }


}

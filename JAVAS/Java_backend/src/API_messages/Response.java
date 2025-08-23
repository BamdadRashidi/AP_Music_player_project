package API_messages;


import com.google.gson.*;

public class Response {
    public String status;
    public String message;
    public JsonObject payload;
    public Response(String status, String message, JsonObject payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
    @Override
    public String toString() {
        return "Response{status='" + status + "', message='" + message + "', payload=" + payload + "}";
    }

}

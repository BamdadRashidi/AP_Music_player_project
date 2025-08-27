import static org.junit.jupiter.api.Assertions.*;
import static services.TrackServicer.settingGenre;

import API_messages.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;
import server.DataBase;
import services.TrackServicer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TrackServiceTest {

    private final String testFilePath = "/tmp/test.mp3";

    @BeforeEach
    void setup() {

        DataBase db = DataBase.getInstance();



        File f = new File(testFilePath);
        f.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write("test".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Fail to create test.mp3 file: " + e.getMessage());
        }
    }

    @Test
    public void testAddTrackMobilePayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("userId", "user1");

        JsonObject trackObj = new JsonObject();
        trackObj.addProperty("id", "track123");
        trackObj.addProperty("title", "MySong");
        trackObj.addProperty("artist", "MyArtist");
        trackObj.addProperty("genre", "rock");
        trackObj.addProperty("isExplicit", true);
        trackObj.addProperty("songUrl", testFilePath);
        payload.add("track", trackObj);

        Response res = TrackServicer.addTrack(payload);
        assertEquals("success", res.getStatus());
        assertNotNull(res.getPayload().get("trackId"));
        assertNotNull(res.getPayload().get("songUrl"));
    }

    @Test
    public void testFileCopy() {
        JsonObject payload = new JsonObject();
        payload.addProperty("userId", "user4");

        JsonObject trackObj = new JsonObject();
        trackObj.addProperty("id", "trackFile");
        trackObj.addProperty("title", "FileSong");
        trackObj.addProperty("artist", "FileArtist");
        trackObj.addProperty("songUrl", testFilePath);
        payload.add("track", trackObj);

        Response res = TrackServicer.addTrack(payload);
        String url = res.getPayload().get("songUrl").getAsString();
        assertTrue(url.contains("trackFile"));
        assertTrue(new File(url).exists());
    }

}



package fr.damdam.gamecast.callback;

import org.json.JSONObject;

/**
 * Created by Poste on 11/08/2016.
 */
public interface MyCastCallback {
    void disconnection();
    void messageReceived(JSONObject mJsonObject);
}

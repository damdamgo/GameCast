package fr.damdam.gamecast.InterfaceCall;

import android.app.Activity;

import org.json.JSONObject;

/**
 * Created by Poste on 20/09/2016.
 */
public interface ActivityApp {

    void sendAnswer(JSONObject jsonObject);
    Activity getActivity();
}

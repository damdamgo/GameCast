package fr.damdam.gamecast.Games;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


import fr.damdam.gamecast.InterfaceCall.ActivityApp;
import fr.damdam.gamecast.InterfaceCall.FragmentApp;
import fr.damdam.gamecast.R;



/**
 * Created by Poste on 11/08/2016.
 */
public class FindMe extends Fragment implements FragmentApp {

    private static String TAG = "FindMe";
    private LinearLayout container;
    private EditText editText;
    private Button button;
    private static ActivityApp activityApp;

    public static void FindMe(ActivityApp activityApp) {
        FindMe.activityApp=activityApp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate =  inflater.inflate(R.layout.activity_game_findme,container,false);
        this.container = (LinearLayout) inflate.findViewById(R.id.containerGameFindMe);
        editText = (EditText) inflate.findViewById(R.id.editTextAnswerFindMe);
        button = (Button) inflate.findViewById(R.id.buttonSendAnswerFindMe);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("answer",editText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                InputMethodManager imm = (InputMethodManager)activityApp.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                activityApp.sendAnswer(jsonObject);
            }
        });
        return inflate;
    }

    @Override
    public void messageReceived(JSONObject mJsonObject) {

    }
}

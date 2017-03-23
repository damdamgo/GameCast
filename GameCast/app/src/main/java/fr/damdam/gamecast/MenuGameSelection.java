package fr.damdam.gamecast;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.damdam.gamecast.InterfaceCall.ActivityApp;
import fr.damdam.gamecast.InterfaceCall.FragmentApp;


/**
 * Created by Poste on 11/08/2016.
 */
public class MenuGameSelection extends Fragment implements FragmentApp {

    private static String TAG = "MenuGameSelection";
    private LinearLayout container;
    private static ActivityApp activityApp;

    public static void MenuGameSelection(ActivityApp activityApp){
        MenuGameSelection.activityApp=activityApp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View inflate =  inflater.inflate(R.layout.activity_game_selection,container,false);
        this.container = (LinearLayout) inflate.findViewById(R.id.containerGameMenu);
        return inflate;
    }

    @Override
    public void messageReceived(JSONObject mJsonObject) {
        try {

                final JSONArray jsonArray = mJsonObject.getJSONArray("games");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    LinearLayout view = (LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.button_inflate, null);
                    Button b = (Button) view.findViewById(R.id.buttonInflate);
                    b.setText(jsonObject.getString("game"));
                    TextView text = (TextView) view.findViewById(R.id.textViewtextGame);
                    text.setText(jsonObject.getString("text"));
                    final int id = jsonObject.getInt("id");
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("idGame",id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            container.removeAllViews();
                            activityApp.sendAnswer(jsonObject);
                        }
                    });
                    container.addView(view);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

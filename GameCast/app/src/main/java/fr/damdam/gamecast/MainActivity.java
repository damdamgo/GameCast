package fr.damdam.gamecast;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import fr.damdam.gamecast.Cast.ManagerCast;
import fr.damdam.gamecast.Games.FindMe;
import fr.damdam.gamecast.Games.Mastermind;
import fr.damdam.gamecast.InterfaceCall.ActivityApp;
import fr.damdam.gamecast.InterfaceCall.FragmentApp;
import fr.damdam.gamecast.Verification.VerificationFragment;
import fr.damdam.gamecast.callback.MyCastCallback;
import fr.damdam.gamecast.callback.MyCastConnectionCallback;
import fr.damdam.gamecast.settings.Preferences;

public class MainActivity extends AppCompatActivity implements MyCastConnectionCallback,MyCastCallback,ActivityApp{

    private static String TAG = "MainActivity";
    private FrameLayout containerFragment;
    private android.support.v4.app.FragmentManager fragmentManager;
    private FragmentApp onGoingFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        containerFragment = (FrameLayout) findViewById(R.id.fragment_container);
        fragmentManager = getSupportFragmentManager();

        MediaRouteButton mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        ManagerCast.getInstance().setCastButton(this,mMediaRouteButton,this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ManagerCast.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        ManagerCast.getInstance().onStop();
        super.onStop();
    }


    @Override
    protected void onPause() {
        containerFragment.removeAllViews();
        if(ManagerCast.getInstance().mManagerGame!=null)ManagerCast.getInstance().mManagerGame.sendIdle(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ManagerCast.getInstance().isApiClientConnected())ManagerCast.getInstance().mManagerGame.sendPlaying(null);
    }



    @Override
    public void connectionSuccess() {
        ManagerCast.getInstance().mManagerGame.setMyCastCallback(this);
        String lang = Locale.getDefault().getLanguage();
        Log.w(TAG,lang);
        JSONObject json = new JSONObject();
        try {
            json.put("lang",lang);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ManagerCast.getInstance().mManagerGame.sendAvailable(json);
    }

    @Override
    public void connectionError() {

    }

    @Override
    public void disconnection() {

    }

    @Override
    public void messageReceived(JSONObject mJsonObject) {
        Log.w(TAG,"receive :"+mJsonObject.toString());
        try {
            if(mJsonObject.has("games")){
                MenuGameSelection.MenuGameSelection(this);
                MenuGameSelection gameSelection = new MenuGameSelection();
                onGoingFrag = gameSelection;
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_container,gameSelection).commit();
                getSupportFragmentManager().executePendingTransactions();
                gameSelection.messageReceived(mJsonObject);
            }
            else if(mJsonObject.has("appVersion")){
                if(!mJsonObject.getString("appVersion").equals(Preferences.AppVersion)){
                    JSONObject res = new JSONObject();
                    res.put("errorVersion","disconnection");
                    ManagerCast.getInstance().mManagerGame.sendMessage(res);
                    ManagerCast.getInstance().disconnectFromCast();
                    Toast.makeText(getActivity(),getString(R.string.errorVersion),
                            Toast.LENGTH_LONG).show();
                }
                else{
                    final Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.setting_dialog);
                    dialog.setTitle(getString(R.string.setting__title));
                    Button bSetting = (Button) dialog.findViewById(R.id.buttonSetting);
                    bSetting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String nickname = ((EditText)dialog.findViewById(R.id.settingNickname)).getText().toString();
                            try {
                                ManagerCast.getInstance().mManagerGame.sendReady(new JSONObject().put("pseudo",nickname));
                                ManagerCast.getInstance().mManagerGame.sendPlaying(null);
                                containerFragment.setVisibility(View.VISIBLE);
                                dialog.hide();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
            else if(mJsonObject.has("errorNbPlayer")){
                ManagerCast.getInstance().disconnectFromCast();
                Toast.makeText(getActivity(),getString(R.string.errorLimitPLayer),
                        Toast.LENGTH_LONG).show();
            }
            else{
                if(mJsonObject.has("idGame")){
                    switch (mJsonObject.getInt("idGame")){
                        case 1 :
                            FindMe.FindMe(this);
                            FindMe fragmentFindMe = new FindMe();
                            onGoingFrag = fragmentFindMe;
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.fragment_container,fragmentFindMe).commit();
                            break;
                        case 2 :
                            break;
                    }
                }
                else if(mJsonObject.has("verification")){
                    VerificationFragment.VerificationFragment(this);
                    VerificationFragment verificationFragment = new VerificationFragment();
                    onGoingFrag = verificationFragment;
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.fragment_container,verificationFragment).commit();
                    getSupportFragmentManager().executePendingTransactions();
                    verificationFragment.messageReceived(mJsonObject);
                }
                else if(mJsonObject.has("mastermind")){
                    Mastermind.Mastermind(this);
                    Mastermind fragmentMastermind = new Mastermind();
                    onGoingFrag = fragmentMastermind;
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.fragment_container,fragmentMastermind).commit();
                }
                else if(mJsonObject.has("action")){
                    switch (mJsonObject.getInt("action")){
                        case Preferences.STOP_GAME:
                            containerFragment.removeAllViews();
                            break;
                    }
                }
                else{
                    onGoingFrag.messageReceived(mJsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAnswer(JSONObject jsonObject) {
        containerFragment.removeAllViews();
        Log.w(TAG,"message sent"+jsonObject.toString());
        ManagerCast.getInstance().mManagerGame.sendMessage(jsonObject);


    }

    @Override
    public Activity getActivity() {
        return this;
    }
}

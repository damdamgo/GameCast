package fr.damdam.gamecast.Verification;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import fr.damdam.gamecast.InterfaceCall.ActivityApp;
import fr.damdam.gamecast.InterfaceCall.FragmentApp;
import fr.damdam.gamecast.InterfaceCall.VerificationCall;
import fr.damdam.gamecast.MainActivity;
import fr.damdam.gamecast.R;


/**
 * Created by Poste on 20/09/2016.
 */
public class VerificationFragment extends Fragment implements FragmentApp,VerificationCall {

    private static ActivityApp activityApp;
    private int heightCardProposition;
    private int heightButtonProposition;
    private RelativeLayout containe;
    private View inflate;
    private boolean isAlreadySent = false;

    private String pseudo =null;
    private String answer=null;
    private String id=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.layout_verification,container,false);
        containe = (RelativeLayout) inflate.findViewById(R.id.container);
        return inflate;
    }

    public void afficherProposition(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightCardProposition = size.y;
        ///initialise vue pour proposition

        final LayoutProposition layout = (LayoutProposition)getActivity().getLayoutInflater().inflate(R.layout.layout_proposition,containe, false);
        layout.setVerificationCall(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.topMargin=heightCardProposition;
        layout.setLayoutParams(params);
        ((TextView)layout.findViewById(R.id.textViewPremiereLettre)).setText(pseudo);
        ((TextView)layout.findViewById(R.id.textViewProposition)).setText(answer);
        containe.addView(layout);



        //initialise clique sur bouton
        RelativeLayout r = (RelativeLayout)(inflate.findViewById(R.id.layoutRefuserButton));
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = -size.x;
                LayoutProposition layout = (LayoutProposition) inflate.findViewById(R.id.viewProposition);
                AnimatorSet animSetXY = new AnimatorSet();
                ObjectAnimator x = ObjectAnimator.ofFloat(layout,
                        "x", layout.getX(), width - 100);
                animSetXY.playTogether(x);
                animSetXY.setDuration(100);
                animSetXY.start();
                sendVerificationResult(false);
            }
        });
        r = (RelativeLayout)(inflate.findViewById(R.id.layoutAccepterButton));
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                LayoutProposition layout = (LayoutProposition) inflate.findViewById(R.id.viewProposition);
                AnimatorSet animSetXY = new AnimatorSet();
                ObjectAnimator x = ObjectAnimator.ofFloat(layout,
                        "x", layout.getX(), width + 100);
                animSetXY.playTogether(x);
                animSetXY.setDuration(100);
                animSetXY.start();
                sendVerificationResult(true);
            }
        });

        //animation apparition
        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                params.topMargin = (int) (heightCardProposition-(interpolatedTime *heightCardProposition))+50;
                layout.setLayoutParams(params);
            }
        };
        a.setDuration(800); // in ms
        layout.startAnimation(a);

        Animation b = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) inflate.findViewById(R.id.layoutButtonProposition).getLayoutParams();
                params.bottomMargin = (int) (-heightButtonProposition+(interpolatedTime *heightButtonProposition));
                inflate.findViewById(R.id.layoutButtonProposition).setLayoutParams(params);
            }
        };
        b.setDuration(800); // in ms
        inflate.findViewById(R.id.layoutButtonProposition).startAnimation(b);
    }

    @Override
    public Activity getActivityFrag() {
        return activityApp.getActivity();
    }

    public static void VerificationFragment(ActivityApp activityApp) {
        VerificationFragment.activityApp = activityApp;
    }

    @Override
    public void messageReceived(JSONObject mJsonObject) {
        try {
        if(mJsonObject.has("pseudo"))this.pseudo=mJsonObject.getString("pseudo");
            if(mJsonObject.has("answer"))this.answer=mJsonObject.getString("answer");
            if(mJsonObject.has("verification"))this.id=mJsonObject.getString("verification");
            afficherProposition();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendVerificationResult(boolean verification){
        if(isAlreadySent)return;
        isAlreadySent=!isAlreadySent;
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("playerID",this.id);
            if(verification)mJsonObject.put("verification","1");
            else mJsonObject.put("verification","0");
            activityApp.sendAnswer(mJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
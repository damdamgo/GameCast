package fr.damdam.gamecast.Games;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fr.damdam.gamecast.InterfaceCall.ActivityApp;
import fr.damdam.gamecast.InterfaceCall.FragmentApp;
import fr.damdam.gamecast.R;

/**
 * Created by Poste on 15/10/2016.
 */

public class Mastermind extends Fragment implements FragmentApp {

    private static ActivityApp activityApp;

    private int redColor = Color.RED;
    private int yellowColor = Color.YELLOW;
    private int greenColor = Color.GREEN;
    private int whiteColor = Color.WHITE;
    private int blueColor = Color.BLUE;
    private int fuchsiaColor = Color.parseColor("#FF0080");
    private int violetColor = Color.parseColor("#57427c");
    private int orangeColor = Color.parseColor("#f79e38");


    private LinearLayout bYellow;
    private LinearLayout bBlue;
    private LinearLayout bWhite;
    private LinearLayout bViolet;
    private LinearLayout bRed;
    private LinearLayout bOrange;
    private LinearLayout bFuchsia;
    private LinearLayout bGreen;
    private String[] answer = new String[4];
    private Map<String, Integer> color = new HashMap<String, Integer>();
    private LinearLayout pionContain;

    public static void Mastermind(ActivityApp activityApp) {
        Mastermind.activityApp=activityApp;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate =  inflater.inflate(R.layout.activity_game_mastermind,container,false);

        answer[0]=null;
        answer[1]=null;
        answer[2]=null;
        answer[3]=null;

        color.put("r",redColor);
        color.put("v",violetColor);
        color.put("g",greenColor);
        color.put("w",whiteColor);
        color.put("y",yellowColor);
        color.put("f",fuchsiaColor);
        color.put("o",orangeColor);
        color.put("b",blueColor);

        initCercle(inflate);
        initColorCercle(inflate);
        initClick();

        pionContain = (LinearLayout) inflate.findViewById(R.id.pionContain);

        initPionClick(pionContain);

        ((Button)inflate.findViewById(R.id.bSendColor)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    String ans = "";
                    for(int i = 0;i<4;i++){
                        if(answer[i]==null)ans+="0,";
                        else ans+=answer[i]+",";
                    }
                    jsonObject.put("answer", ans);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activityApp.sendAnswer(jsonObject);
            }
        });

        return inflate;
    }

    private void initPionClick(LinearLayout pionContain) {
        ((LinearLayout)(pionContain.findViewWithTag("pion1"))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePionColor(0);
            }
        });
        ((LinearLayout)(pionContain.findViewWithTag("pion2"))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePionColor(1);
            }
        });
        ((LinearLayout)(pionContain.findViewWithTag("pion3"))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePionColor(2);
            }
        });
        ((LinearLayout)(pionContain.findViewWithTag("pion4"))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePionColor(3);
            }
        });
        changePionColor(0,null);
        changePionColor(1,null);
        changePionColor(2,null);
        changePionColor(3,null);
    }

    private void removePionColor(int indexPion){
        answer[indexPion]=null;
        changePionColor(indexPion,null);
    }

    private void changePionColor(int index,String color){
        LayerDrawable shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        GradientDrawable gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);

        if(color!=null)gradientDrawable.setColor(this.color.get(color));
        else gradientDrawable.setColor(Color.BLACK);
        String p = "pion"+(index+1);
        LinearLayout pion = (LinearLayout) pionContain.findViewWithTag(p);
        pion.setBackground(shape);
    }

    private void addColor(String color){
        int i = 0;
        for(i=0;i<answer.length;i++){
            if(answer[i]==null){
                answer[i]=color;
                changePionColor(i,color);
                break;
            }
        }
    }

    private void initClick(){
        bYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColor("y");
            }
        });
        bBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColor("b");
            }
        });
        bRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColor("r");
            }
        });
        bFuchsia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColor("f");
            }
        });
        bOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColor("o");
            }
        });
        bGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColor("g");
            }
        });
        bViolet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColor("v");
            }
        });
        bWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColor("w");
            }
        });
    }

    private void initCercle(View inflate){
        bYellow = (LinearLayout) inflate.findViewById(R.id.bYellow);
        bBlue = (LinearLayout) inflate.findViewById(R.id.bBlue);
        bFuchsia = (LinearLayout) inflate.findViewById(R.id.bFuchsia);
        bGreen = (LinearLayout) inflate.findViewById(R.id.bGreen);
        bRed = (LinearLayout) inflate.findViewById(R.id.bRed);
        bViolet = (LinearLayout) inflate.findViewById(R.id.bViolet);
        bWhite = (LinearLayout) inflate.findViewById(R.id.bWhite);
        bOrange = (LinearLayout) inflate.findViewById(R.id.bOrange);
    }

    private void initColorCercle(View inflate){
        LayerDrawable shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        GradientDrawable gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);

        gradientDrawable.setColor(yellowColor);
        bYellow.setBackground(shape);

        shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);
        gradientDrawable.setColor(redColor);
        bRed.setBackground(shape);

        shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);
        gradientDrawable.setColor(greenColor);
        bGreen.setBackground(shape);

        shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);
        gradientDrawable.setColor(violetColor);
        bViolet.setBackground(shape);

        shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);
        gradientDrawable.setColor(orangeColor);
        bOrange.setBackground(shape);

        shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);
        gradientDrawable.setColor(whiteColor);
        bWhite.setBackground(shape);

        shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);
        gradientDrawable.setColor(fuchsiaColor);
        bFuchsia.setBackground(shape);

        shape = (LayerDrawable) ContextCompat.getDrawable(activityApp.getActivity(),R.drawable.round_layout);
        gradientDrawable = (GradientDrawable) shape.findDrawableByLayerId(R.id.shape_id);
        gradientDrawable.setColor(blueColor);
        bBlue.setBackground(shape);

    }

    @Override
    public void messageReceived(JSONObject mJsonObject) {
        
    }
}

package fr.damdam.gamecast.Verification;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.damdam.gamecast.InterfaceCall.VerificationCall;
import fr.damdam.gamecast.R;

/**
 * Created by Poste on 20/09/2016.
 */
public class LayoutProposition extends RelativeLayout {
    private float xtBefore=-1,ytBefore=-1,xEncrage=-1,yEncrage=-1,widthScreen=-1,xEncrageElement,yEncrageElement,widthScreenTotal=-1;
    private int[] tablocation = new int[2];
    private VelocityTracker mVelocityTracker = null;
    private int heightButtonProposition;
    private RelativeLayout ButtonProposition ;
    private VerificationCall verificationcall;

    public LayoutProposition(Context context) {
        super(context);
    }

    public LayoutProposition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LayoutProposition(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVerificationCall(VerificationCall verificationcall){
        this.verificationcall=verificationcall;
    }

    public void initialiseWidthScreen() {
        Display display = verificationcall.getActivityFrag().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreenTotal = size.x/4;
        ButtonProposition= (RelativeLayout) verificationcall.getActivityFrag().findViewById(R.id.layoutButtonProposition);
        heightButtonProposition= ButtonProposition.getHeight();

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true; // With this i tell my layout to consume all the touch events from its childs
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(verificationcall!=null) {
            int action = event.getActionMasked();
            float xt = event.getX();
            float yt = event.getY();
            if (xtBefore == -1) xtBefore = xt;
            if (ytBefore == -1) ytBefore = yt;
            if (xEncrage == -1) {
                this.getLocationOnScreen(tablocation);
                xEncrage = tablocation[0];
                yEncrage = tablocation[1];
                xEncrageElement = this.getX();
                yEncrageElement = this.getY();
            }
            if (widthScreen == -1) widthScreen = this.getX();
            if (widthScreenTotal == -1) initialiseWidthScreen();
            this.setX(this.getX() + xt - xtBefore);
            this.setY(this.getY() + yt - ytBefore);
            Log.w("ee","event");

            float p = 0;
            if (widthScreen < this.getX()) {
                p = this.getX() - widthScreen;
                p = ((p * 100) / (widthScreenTotal)) / 100;
                RelativeLayout r = (RelativeLayout) verificationcall.getActivityFrag().findViewById(R.id.accepte);
                if(r!=null){
                    r.setAlpha(p);
                    TextView txt = (TextView) verificationcall.getActivityFrag().findViewById(R.id.textviewaccept);
                    txt.setTextColor(Color.parseColor("#4CAF50"));
                    txt = (TextView) verificationcall.getActivityFrag().findViewById(R.id.textviewrefuse);
                    txt.setTextColor(Color.parseColor("#212121"));
                }
            } else if (widthScreen > this.getX()) {
                p = widthScreen - this.getX();
                p = ((p * 100) / (widthScreenTotal)) / 100;
                RelativeLayout r = (RelativeLayout) verificationcall.getActivityFrag().findViewById(R.id.refuse);
                if(r!=null){
                    r.setAlpha(p);
                    TextView txt = (TextView) verificationcall.getActivityFrag().findViewById(R.id.textviewaccept);
                    txt.setTextColor(Color.parseColor("#212121"));
                    txt = (TextView) verificationcall.getActivityFrag().findViewById(R.id.textviewrefuse);
                    txt.setTextColor(Color.parseColor("#F44336"));
                }
            }


            RelativeLayout.LayoutParams re = (RelativeLayout.LayoutParams) ButtonProposition.getLayoutParams();
            re.bottomMargin = (int) (0 - p * heightButtonProposition);
            ButtonProposition.setLayoutParams(re);

            int index = event.getActionIndex();
            int pointerId = event.getPointerId(index);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    mVelocityTracker.addMovement(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000);
                    Log.w("aze", "X velocity: " +
                            VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                                    pointerId));
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    p = this.getX() - widthScreen;
                    p = ((p * 100) / (widthScreenTotal)) / 100;
                    if (mVelocityTracker.getXVelocity() > 2000 || p > 1) {
                        RelativeLayout r = (RelativeLayout) findViewById(R.id.accepte);
                        r.setAlpha(1);
                        Display display = verificationcall.getActivityFrag().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        Log.w("ee", "ok true");
                        RelativeLayout r2 = (RelativeLayout) findViewById(R.id.accepte);
                        r2.setAlpha(1);
                        AnimatorSet animSetXY = new AnimatorSet();
                        ObjectAnimator x = ObjectAnimator.ofFloat(this,
                                "x", this.getX(), width + 100);
                        animSetXY.playTogether(x);
                        animSetXY.setDuration(100);
                        animSetXY.start();
                        re = (RelativeLayout.LayoutParams) ButtonProposition.getLayoutParams();
                        re.bottomMargin = -heightButtonProposition;
                        ButtonProposition.setLayoutParams(re);

                        if(verificationcall!=null)verificationcall.sendVerificationResult(true);
                        verificationcall = null;

                    } else if (p < -1 || mVelocityTracker.getXVelocity() < -2000) {
                        RelativeLayout r = (RelativeLayout) findViewById(R.id.refuse);
                        r.setAlpha(p);
                        Display display = verificationcall.getActivityFrag().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = -size.x;
                        Log.w("ee", "ok");
                        RelativeLayout r2 = (RelativeLayout) findViewById(R.id.refuse);
                        r2.setAlpha(1);
                        AnimatorSet animSetXY = new AnimatorSet();
                        ObjectAnimator x = ObjectAnimator.ofFloat(this,
                                "x", this.getX(), width - 100);
                        animSetXY.playTogether(x);
                        animSetXY.setDuration(100);
                        animSetXY.start();
                        re = (RelativeLayout.LayoutParams) ButtonProposition.getLayoutParams();
                        re.bottomMargin = -heightButtonProposition;
                        ButtonProposition.setLayoutParams(re);

                        Log.w("ee", "okfalse");
                        if(verificationcall!=null)verificationcall.sendVerificationResult(false);
                        verificationcall = null;
                    } else {
                        this.getLocationOnScreen(tablocation);
                        xt = xEncrage - tablocation[0];
                        yt = yEncrage - tablocation[1];

                        AnimatorSet animSetXY = new AnimatorSet();
                        ObjectAnimator y = ObjectAnimator.ofFloat(this,
                                "y", this.getY(), yEncrageElement);
                        ObjectAnimator x = ObjectAnimator.ofFloat(this,
                                "x", this.getX(), xEncrageElement);
                        animSetXY.playTogether(x, y);
                        animSetXY.setDuration(200);
                        animSetXY.start();
                        animSetXY.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                Log.w("ee", "ok1");
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Log.w("ee", "ok2");
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                Log.w("ee", "ok3");
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                Log.w("ee", "ok4");
                            }
                        });

                        xtBefore = -1;
                        ytBefore = -1;
                        widthScreen = -1;
                        RelativeLayout r = (RelativeLayout) findViewById(R.id.accepte);
                        r.setAlpha(0);
                        r = (RelativeLayout) findViewById(R.id.refuse);
                        r.setAlpha(0);
                        re = (RelativeLayout.LayoutParams) ButtonProposition.getLayoutParams();
                        final int l = -re.bottomMargin;
                        Animation b = new Animation() {

                            @Override
                            protected void applyTransformation(float interpolatedTime, Transformation t) {
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ButtonProposition.getLayoutParams();
                                params.bottomMargin = -l + (int) (interpolatedTime * l);
                                ButtonProposition.setLayoutParams(params);
                            }
                        };
                        b.setDuration(400); // in ms
                        ButtonProposition.startAnimation(b);

                    }

                    if(verificationcall!=null){
                        TextView txt = (TextView) verificationcall.getActivityFrag().findViewById(R.id.textviewaccept);
                        if(txt!=null){
                            txt.setTextColor(Color.parseColor("#212121"));
                            txt = (TextView) verificationcall.getActivityFrag().findViewById(R.id.textviewrefuse);
                            txt.setTextColor(Color.parseColor("#212121"));
                        }
                    }
                    break;
            }

        }
        return true;
    }

}

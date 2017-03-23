package fr.damdam.gamecast.InterfaceCall;

import android.app.Activity;

/**
 * Created by Poste on 21/09/2016.
 */
public interface VerificationCall {
    Activity getActivityFrag();
    void sendVerificationResult(boolean verification);

}

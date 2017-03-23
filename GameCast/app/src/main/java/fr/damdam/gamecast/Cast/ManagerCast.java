package fr.damdam.gamecast.Cast;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import fr.damdam.gamecast.callback.MyCastCallback;
import fr.damdam.gamecast.callback.MyCastConnectionCallback;
import fr.damdam.gamecast.settings.Preferences;

/**
 * Created by Poste on 11/08/2016.
 */
public class ManagerCast {

    private static String TAG = "ManagerCast";
    private static ManagerCast mManagerCast = null;
    private MediaRouter mMediaRouter;
    private MediaRouteButton mMediaRouteButton;
    private MediaRouteSelector mMediaRouteSelector;
    private CastDevice mSelectedDevice;
    private Context mContext;
    private GoogleApiClient mApiClient;
    private String mCastSessionId;
    private MediaRouteCallback mMediaRouterCallback;
    private GameManagerClient mGameManagerClient;
    public ManagerGame mManagerGame;
    private MyCastConnectionCallback callbackConnection = null;

    private ManagerCast(){

    }

    public static ManagerCast getInstance(){
        if(mManagerCast==null)mManagerCast = new ManagerCast();
        return mManagerCast;
    }

    public void setCastButton(Context context,MediaRouteButton mMediaRouteButton,MyCastConnectionCallback callbackConnection){
        this.mContext = context;
        this.callbackConnection = callbackConnection;
        mMediaRouter = MediaRouter.getInstance(mContext);
        mMediaRouterCallback = new MediaRouteCallback();
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(Preferences.ID_APP))
                .build();

        this.mMediaRouteButton = mMediaRouteButton;
        this.mMediaRouteButton.setRouteSelector(mMediaRouteSelector);
    }

    private void connectApiClient() {
        Cast.CastOptions apiOptions = Cast.CastOptions.builder(mSelectedDevice, new CastListener())
                .build();
        GoogleApiClientConnectionCallback callback = new GoogleApiClientConnectionCallback();
        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Cast.API, apiOptions)
                .addConnectionCallbacks(callback)
                .addOnConnectionFailedListener(callback)
                .build();
        mApiClient.connect();
    }

    private void disconnectApiClient() {
        if (mGameManagerClient != null) {
            mGameManagerClient.dispose();
            mGameManagerClient = null;
        }
        if (mApiClient != null && mApiClient.isConnected()) {
            mApiClient.disconnect();
        }
        mApiClient = null;
    }

    private void setSelectedDevice(CastDevice device) {
        Log.d(TAG, "setSelectedDevice: " + device);
        mSelectedDevice = device;

        // This will notify observers, so no need to explicitly do it in this method.
        disconnectApiClient();

        if (mSelectedDevice != null) {
            try {
                connectApiClient();
            } catch (IllegalStateException e) {
                Log.w(TAG, "Exception while connecting Google API client.", e);
                disconnectApiClient();
            }
        } else {
            mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
        }
    }

    /**
     * Media router callbacks.
     */
    private class MediaRouteCallback extends MediaRouter.Callback {
        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
        }

        @Override
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
        }

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            Log.d(TAG, "MediaRouteCallback.onRouteSelected: info=" + info);
            CastDevice device = CastDevice.getFromBundle(info.getExtras());
            setSelectedDevice(device);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            Log.d(TAG, "MediaRouteCallback.onRouteUnselected: info=" + info);
            setSelectedDevice(null);
        }
    }


    /**
     * Google API Client callbacks.
     */
    private class GoogleApiClientConnectionCallback implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "GoogleApiClient disconnected. Cause: " + cause);
            setSelectedDevice(null);
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "GoogleApiClient connected.");
            if (!isApiClientConnected()) {
                Log.w(TAG, "Got GoogleApiClient.onConnected callback but the Google API client is "
                        + "disconnected.");
                setSelectedDevice(null);
                return;
            }
            Cast.CastApi.launchApplication(mApiClient, Preferences.ID_APP)
                    .setResultCallback(new LaunchReceiverApplicationResultCallback());
        }

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.d(TAG, "Failed to connect the Google API client " + result);
            setSelectedDevice(null);
        }
    }


    /**
     * Cast API callbacks.
     */
    private class CastListener extends Cast.Listener {
        @Override
        public void onApplicationDisconnected(int statusCode) {
            Log.d(TAG, "Cast.Listener.onApplicationDisconnected: " + statusCode);
            setSelectedDevice(null);
        }
    }

    private final class LaunchReceiverApplicationResultCallback implements
            ResultCallback<Cast.ApplicationConnectionResult> {
        @Override
        public void onResult(Cast.ApplicationConnectionResult result) {
            Status status = result.getStatus();
            ApplicationMetadata appMetaData = result.getApplicationMetadata();
            if (status.isSuccess()) {
                Log.d(TAG, "Launching app: " + appMetaData.getName());
                mCastSessionId = result.getSessionId();
                GameManagerClient.getInstanceFor(mApiClient, mCastSessionId).setResultCallback(
                        new GameManagerGetInstanceCallback());
            } else {
                Log.d(TAG, "Unable to launch the the app. statusCode: " + result.getStatus());
                setSelectedDevice(null);
            }
        }
    }

    /**
     * GameManagerClient initialization callback.
     */
    private final class GameManagerGetInstanceCallback implements
            ResultCallback<GameManagerClient.GameManagerInstanceResult> {
        @Override
        public void onResult(GameManagerClient.GameManagerInstanceResult gameManagerResult) {
            if (!gameManagerResult.getStatus().isSuccess()) {
                Log.d(TAG, "Unable to initialize the GameManagerClient: "
                        + gameManagerResult.getStatus().getStatusMessage()
                        + " Status code: " + gameManagerResult.getStatus().getStatusCode());
                setSelectedDevice(null);
            }
            else{
                mGameManagerClient = gameManagerResult.getGameManagerClient();
                mManagerGame = new ManagerGame(mGameManagerClient);
                callbackConnection.connectionSuccess();
            }
        }
    }

    /**
     * @return True if the Google Api Client is connected.
     */
    public boolean isApiClientConnected() {
        return (mApiClient != null) && (mApiClient.isConnected());
    }

    /**
     * start listener
     */
    public void onStart() {
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    /**
     * stop listener
     */
    public void onStop() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    public void disconnectFromCast(){
        setSelectedDevice(null);
    }
}

package fr.damdam.gamecast.Cast;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.cast.games.GameManagerState;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONObject;

import fr.damdam.gamecast.callback.MyCastCallback;

/**
 * Created by Poste on 11/08/2016.
 */
public class ManagerGame implements GameManagerClient.Listener {

    private static String TAG = "ManagerGame";
    private GameManagerClient mGameManagerClient;
    private String playerId = null;
    private String pseudo;
    private MyCastCallback myCastCallback = null;

    public ManagerGame(GameManagerClient mGameManagerClient) {
        this.mGameManagerClient = mGameManagerClient;
        this.mGameManagerClient.setListener(this);
    }

    @Override
    public void onStateChanged(GameManagerState gameManagerState, GameManagerState gameManagerState1) {

    }

    @Override
    public void onGameMessageReceived(String s, JSONObject jsonObject) {
        if(myCastCallback!=null)myCastCallback.messageReceived(jsonObject);
    }

    public void sendMessage(JSONObject mJsonObject){
        if(checkConnection())mGameManagerClient.sendGameRequest(mJsonObject).setResultCallback(new GameResult());
    }

    public void sendAvailable(JSONObject mJsonObject){
        if(checkConnection())mGameManagerClient.sendPlayerAvailableRequest(mJsonObject).setResultCallback(new GameResult());
    }

    public void sendReady(JSONObject mJsonObject){
        if(checkConnection())mGameManagerClient.sendPlayerReadyRequest(mJsonObject).setResultCallback(new GameResult());
    }

    public void sendPlaying(JSONObject mJsonObject){
        if(checkConnection())mGameManagerClient.sendPlayerPlayingRequest(mJsonObject).setResultCallback(new GameResult());
    }

    public void sendIdle(JSONObject mJsonObject){
        if(checkConnection())mGameManagerClient.sendPlayerIdleRequest(mJsonObject).setResultCallback(new GameResult());
    }

    public void sendQuit(JSONObject mJsonObject){
        if(checkConnection())mGameManagerClient.sendPlayerQuitRequest(mJsonObject).setResultCallback(new GameResult());
    }


    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public MyCastCallback getMyCastCallback() {
        return myCastCallback;
    }

    public void setMyCastCallback(MyCastCallback myCastCallback) {
        this.myCastCallback = myCastCallback;
    }

    private boolean checkConnection(){
        return ManagerCast.getInstance().isApiClientConnected();
    }

    private class GameResult implements ResultCallback<GameManagerClient.GameManagerResult> {
        @Override
        public void onResult(@NonNull GameManagerClient.GameManagerResult gameManagerResult) {
            if(gameManagerResult.getStatus().isSuccess()){
                if(playerId==null)playerId=gameManagerResult.getPlayerId();
            }
            //TODO implement error manage
        }
    }
}

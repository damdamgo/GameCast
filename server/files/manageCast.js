/**
permet d'initialiser au niveau de la chormecast l'ensemble des fonctions qui relient les clients au serveur
*/
function CastManager(ind,callback){
	/**
	*cast manager
	*/
	var index = ind;
	console.log(index);
	/**
	*get instance
	*/
	window.castReceiverManager = cast.receiver.CastReceiverManager.getInstance();

	/**
	*set up listener
	*/
	//sender connect
	window.castReceiverManager.onSenderConnected = function(event){
	}
	//sender disconect
	window.castReceiverManager.onSenderDisconnected = function(event){
	}
	/**
	*set up configuration
	*/
	var appConfig = new cast.receiver.CastReceiverManager.Config();
	appConfig.statusText = 'Ready to play';
	appConfig.maxInactivity = 6000;
	/**
	*set up game manager
	*/
	var gameConfig = new cast.receiver.games.GameManagerConfig();
	gameConfig.applicationName = 'GameCast';
	gameConfig.maxPlayers = 8;
	gameManager = new cast.receiver.games.GameManager(gameConfig);
	//available listener
	gameManager.addEventListener(cast.receiver.games.EventType.PLAYER_AVAILABLE,
	    function(event) {
	    	console.log("available");
	    	console.log(event);
	    	if(event.requestExtraMessageData.lang && window.preferences.LANGJSON==null){
	    		if(event.requestExtraMessageData.lang=="FR" || event.requestExtraMessageData.lang=="fr")window.preferences.LANGJSON = window.langFR;
	    		else window.preferences.LANGJSON = window.langEN;
	    	}
	        index.createPlayer(event.playerInfo.playerId);
	});
	//ready listener
	gameManager.addEventListener(cast.receiver.games.EventType.PLAYER_READY,
	    function(event) {
	    	console.log("ready");
	        index.readyPlayer(event);
	});
	//playing listener
	gameManager.addEventListener(cast.receiver.games.EventType.PLAYER_PLAYING,
	    function(event) {
	        index.playingPlayer(event);
	});
	//idle listener
	gameManager.addEventListener(cast.receiver.games.EventType.PLAYER_IDLE,
	    function(event) {
	        index.idlePlayer(event);
	});
	gameManager.addEventListener(cast.receiver.games.EventType.PLAYER_QUIT,
	    function(event) {
	       // index.quitPlayer(event);
	});
	gameManager.addEventListener(cast.receiver.games.EventType.PLAYER_DROPPED,
	    function(event) {
	        index.quitPlayer(event);
	});
	//message listener
	gameManager.addEventListener(cast.receiver.games.EventType.GAME_MESSAGE_RECEIVED,
	    function(event) {
	        index.messageDistributor(event);
	});
	//
	/**
	*start cast
	*/
	window.castReceiverManager.start(appConfig);

	callback(gameManager);
}

/**
*
*
* fonction qui permet de définir les principales méthodes et qui permet le fonctionnement des modules de jeu
*
*
*
*/
/**obligation function for game module:
*getName : return name
*startGame : launch game with callback
*messageManager : receive message
*getId() : game id (number)
*verification() : launch verification
*setMaster() : allow to listen master changement during verification
*loadTemplateGame : load html for game
*playerNotAvailable : inform player is not available;
*playerAvailable : inform player available
*getText : small text which describe game
*/
$(window).on("load", function() {

	var self = this;
	var gameManager;

	/*
		permet de creer un joueur au niveau de l'affichage
	*/
	self.createPlayer = function(id){
		if(window.preferences.MAX_PLAYER>playerList.length){
			var indexLayoutAvailable=1;
			for(j=0;j<=window.preferences.MAX_PLAYER;j++){
					if(layoutIsAvailable($("#mainMenu .flexibleMenu #LP"+indexLayoutAvailable+""))){
						layout = $("#mainMenu .flexibleMenu #LP"+indexLayoutAvailable+"");
						createPlayerData(layout,id);
						break;
					}else{
						indexLayoutAvailable++;
					}
			}
		}
		else{
			setTimeout(function(){
				sendMessageToAll({"errorNbPlayer":"error"},id);
			},500);
		}
	}

	/*
		permet de créer un joueur en données (classe)
	*/
	self.createPlayerData = function(layout,id){
			player = new window.Player();
			player.constructor(id,layout,playerList.length);
			player.changeState(1);
			playerList.push(player);
			setTimeout(function(){
				sendMessageToAll({"appVersion":window.preferences.APP_VERSION},id);
			},500);
	}

	self.layoutIsAvailable = function(layout){
		for(u=0;u<playerList.length;u++){
			if(playerList[u].getLayout().is(layout)){
				return false;
			}
		}
		return true;
	}

	/**
		permet de définir un pseudo
	*/
	self.readyPlayer = function(event){
		for(i=0;i<playerList.length;i++){
			if(playerList[i].getId()==event.playerInfo.playerId){
				playerList[i].setPseudo(event.requestExtraMessageData.pseudo);
				break;
			}
		}
	}

	/**
		permet de définir qu'un joueur joue
	*/
	self.playingPlayer = function(event){
		for(i=0;i<playerList.length;i++){
			if(playerList[i].getId()==event.playerInfo.playerId){
				playerList[i].changeState(3);
				break;
			}
		}
		self.chooseMaster();
		self.checkStartGameRequest();
		if(gameLaunched!=null){
			gameLaunched.playerAvailable(playerList[i]);
		}
	}

	/*
		permet de définir qu'un joueur est en pause
	*/
	self.idlePlayer = function(event){
		for(i=0;i<playerList.length;i++){
			if(playerList[i].getId()==event.playerInfo.playerId){
				playerList[i].changeState(4);
				if(gameLaunched!=null){
					gameLaunched.playerNotAvailable(playerList[i]);
				}
				if(playerList[i].getMaster()){
					if(gameLaunched!=null)gameLaunched.setMaster(null);
					playerList[i].setMaster(false);
					chooseMaster();
				}
				break;
			}
		}
	}

	/**
		permet de définir qu'un joueur a quitté
	*/
	self.quitPlayer = function(event){
		var indPlayer;
		for(i=0;i<playerList.length;i++){
			if(playerList[i].getId()==event.playerInfo.playerId){
				playerList[i].changeState(2);
				if(gameLaunched!=null){
					gameLaunched.playerNotAvailable(playerList[i]);
				}
				if(playerList.length!=0){
					if(playerList[i].getMaster()){
						if(gameLaunched!=null)gameLaunched.setMaster(null);
						playerList[i].setMaster(false);
						chooseMaster();
						indPlayer = i;
					}
					self.checkRemovePlayer();
				}
				break;
			}
		}
	}

	/**
		met à jour l'interface
	*/
	self.checkRemovePlayer = function(){
		if(gameState==1 || gameState == 2){
			for(i=0;i<playerList.length;i++){
				if(playerList[i].getState()==2){
					playerList[i].changeState(5);
					var layoutDel = playerList[i].getLayout();
					playerList.splice(i, 1);
					for(y=i;y<playerList.length;y++){
						playerList[y].place = playerList[y].place-1;
						playerList[i].getLayout().animate({
				       		top: heightLayoutPlayer*(playerList[y].place)+"px"
				    	}, { duration: 100, queue: false });
					}
					layoutDel.animate({
				       		top: heightLayoutPlayer*(playerList.length)+"px"
				    	}, { duration: 100, queue: false });
					i--;
				}
			}
		}
	}

	/**
		permet de distribuer les messages au module en cours d'éxecution
	*/
	self.messageDistributor = function(event){
		console.log("message");
		console.log(event);
		if(event.requestExtraMessageData.errorVersion){
			quitPlayer(event);
		}
		else if(gameState==2){
			idGame = event.requestExtraMessageData.idGame;
			for(i=0;i<gameList.length;i++){
				if(gameList[i].getId()==idGame){
					gameLaunched = gameList[i];
					break;
				}
			}
			gameState = 3;
			self.sendMessageToAll({"idGame":idGame},null);
			gameLaunched.loadTemplateGame(function(){
				gameLaunched.startGame(self.sendMessageToAll,function(needVerification){
					if(existMaster()){
						for(i=0;i<playerList.length;i++){
							if(playerList[i].getMaster()){
								gameLaunched.setMaster(playerList[i]);
								gameLaunched.verification($("#mainVerification"),function(playerTab){
									gameLaunched=null;
									updateTable(playerTab);
								});
								break;
							}
						}
					}
					else{
						gameLaunched.verification($("#mainVerification"),function(playerTab){
									self.gameLaunched=null;
									self.updateTable(playerTab);
						});
					}
				},playerList);
			});
		}
		else if(gameState==3){
			for(i=0;i<playerList.length;i++){
				if(playerList[i].getId()==event.playerInfo.playerId){
					gameLaunched.messageManager(playerList[i],event);
					break;
				}
			}
		}
	}

	/**
		permet de gérer le lancement des jeux
	*/
	self.checkStartGameRequest = function(){
		self.checkRemovePlayer();
		if(gameState==1){
			nbPlayer = 0;
			for(i=0;i<playerList.length;i++){
				if(playerList[i].getState() == 3)nbPlayer++;
			}
			if(nbPlayer>0){//minimum joueur
				var myJson = {};
				var arr = [];
				for(i=0;i<gameList.length;i++){
					arr.push({"game":gameList[i].getName(),"id":gameList[i].getId(),"text":gameList[i].getText()});
				}
				myJson["games"] = arr;
				for(i=0;i<playerList.length;i++){
					if(playerList[i].getMaster()){
						gameManager.sendGameMessageToPlayer(playerList[i].getId(), myJson);
						gameState = 2;
						break;
					}
				}
			}
		}
	}

	/*
		permet de choisir un master pour les jeux (celui qui acceptera ou refusera les réponses)
	*/
	self.chooseMaster = function(){
		if(!existMaster()){
			for(i=0;i<playerList.length;i++){
				if(playerList[i].getState()==3){
					playerList[i].setMaster(true);
					if(gameState==2)gameState=1;
					checkStartGameRequest();
					if(gameLaunched!=null)gameLaunched.setMaster(playerList[i]);
					break;
				}
			}
		}
	}

	/**
		verifie qu'un master est bien choisi
	*/
	self.existMaster = function(){
		for(i=0;i<playerList.length;i++){
			if(playerList[i].getMaster()){
				return true;
			}
		}
		return false;
	}

	/**
		permet d'ordonner les joueurs en fonction des scores
	*/
	self.orderPlayerList = function(callback,param){
		var tempTab = [];
		if(playerList.length>0){
			tempTab.push(playerList[0]);
			for(i=1;i<playerList.length;i++){
				y=0;
				indPlace=0;
				while(y<tempTab.length){
					if(tempTab[y].getScore()<playerList[i].getScore()){
						indPlace=y;
						break;
					}
					y++;
				}
				if(y==tempTab.length){
					tempTab.push(playerList[i]);
				}
				else{
					for(y=tempTab.length;y>indPlace;y--){
						tempTab[y]=tempTab[y-1];
					}
					tempTab[indPlace]=playerList[i];
				}
			}
		}
		playerList=tempTab;
		callback(param);
	}

	/*
		met à jour l'affichage
	*/
	self.updateDisplayingTablePlayer = function(playerObject,targetIndex,indexLayout,callback,param){
			for(i=0;i<playerList.length;i++){
				if(playerList[i]!=playerObject && playerList[i].place >=targetIndex && playerList[i].place < indexLayout){
					playerList[i].place=playerList[i].place+1;
					playerList[i].getLayout().animate({
		       		top: heightLayoutPlayer*(playerList[i].place)+"px"
		    	}, { duration: 2000, queue: false });
				}
			}
			playerObject.place=targetIndex;
			playerObject.getLayout().animate({
				top: targetIndex*heightLayoutPlayer+"px"
			},{ duration: 4000, queue: false }).promise().then(function() {
				callback(param);
			});

	}

	/**
		met à jour l'affichage
	*/
	self.updateDisplayingScore = function(playerInd){
		if(playerInd<playerList.length){
			var playerObject =playerList[playerInd];
			if((playerInd) != playerObject.place){
				self.updateDisplayingTablePlayer(playerObject,playerInd,playerObject.place,self.updateDisplayingScore,(++playerInd));
			}
			else {
				self.updateDisplayingScore((++playerInd));
			}
		}
		else{
			gameState=1;
			self.checkStartGameRequest();
		}
	}

	/**
		permet de gérer la verification de la mise à jour de l'affichage
	*/
	self.updateTable = function(tab){
		console.log("verification");
		console.log(tab);
		if(tab.length==0){
			gameState=1;
			self.checkStartGameRequest();
		}
		else{
			for(i=0;i<playerList.length;i++){
				for(y=0;y<tab.length;y++){
					if(playerList[i].getId()==tab[y].player){
						playerList[i].addScore(tab[y].score);
					}
				}
			}
			self.orderPlayerList(self.updateDisplayingScore,0);
		}

	}

	self.sendMessageToAll = function(message,id){
		if(id==null){
			for(i=0;i<playerList.length;i++){
				if(playerList[i].getState()==3){
					gameManager.sendGameMessageToPlayer(playerList[i].getId(),message);
				}
			}
		}
		else{
			gameManager.sendGameMessageToPlayer(id,message);
		}
	}

	/**
	*set up var
	*/
	//cast manager
	var castManager;

	//player array
	var playerList = [];

	//play

	var gameLaunched = null;
	var gameList = [];
	findMe = new FindMe();
	findMe.constructor();
	gameList.push(findMe);

	mastermind = new Mastermind();
	mastermind.constructor();
	gameList.push(mastermind);


	//state
	/*game state
	* 1 : waiting to send game request
	* 2 : game request have been send
	* 3 : game is playing
	*/
	var gameState = 1;

	//init players layout
	var heightLayoutPlayer = $("#mainMenu").height()/8;
	for(i=1;i<=8;i++){
		$("#mainMenu .flexibleMenu").append("<div class='layoutPlayer' style='height:"+heightLayoutPlayer+"px;top:"+(i-1)*heightLayoutPlayer+"px' id='LP"+i+"'></div");
		$("#mainMenu .fixMenu").append("<div class='fixLayoutPlayer' style='height:"+heightLayoutPlayer+"px'></div");
	}

	//init cast manager
	castManager = new CastManager(self,function(gameMa){
		gameManager = gameMa
	});


});

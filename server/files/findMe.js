/*
	Permet la définition de la classe du jeu FindMe
*/
window.FindMe = function(){

	var self = this;

	this.wordList ;
	this.letterList = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
	this.wordSelected = null;
	this.letterSelected = null;

	this.layout;
	this.layoutLetter;
	this.layoutWord;
	this.layoutTime;

	this.sendMessageToAll;
	this.callback;
	this.letterInd = 0;
	this.indCheck=-1;

	this.layoutVerification;
	this.verificationCallBack;
	this.gameMaster;

	this.playerTab=[];
	this.nbJoueurFindAnswer = 0;
	this.constructor=function(){
	}

	/*
		permet de récuperer le nom du jeu pour les clients
	*/
	this.getName = function(){
		return "find me";
	}

	/*
		permet de lancer le jeu
	*/
	this.startGame = function(sendMessageToAll,callback,playerList){

		this.initLayout();

		this.nbJoueurFindAnswer = 0;

		this.sendMessageToAll=sendMessageToAll;
		this.callback=callback;

		wordInd = Math.floor(Math.random() * ((this.wordList.length-1) - 0 + 1)) + 0;
		this.letterInd = Math.floor(Math.random() * ((this.letterList.length-1) - 0 + 1)) + 0;

		this.layoutWord.css({"opacity":"1"});
		this.layoutWord.html(this.wordList[wordInd]);
		this.layoutLetter.css({"opacity":"1"});
		this.layoutLetter.html(this.letterList[this.letterInd]);

		function time(t){
			if(t==0){
				sendMessageToAll({"action":window.preferences.STOP_GAME},null);
				self.layout.css({"z-index":"80"});
				callback(true);
			}
			else{
				t = t-1;
				self.layoutTime.html(t);
				setTimeout(function(){time(t)},1000);
			}
		}
		setTimeout(function(){time(16)},1000);
	}

	/*
		permet de gérer les messages des clients pendant l'éxecution du jeu
	*/
	this.messageManager = function(player,event){
		if(event.requestExtraMessageData.answer){
			answer = event.requestExtraMessageData.answer;
			this.playerTab.push({player:player.getId(),answer:answer,pseudo:player.getPseudo()});
			console.log("answer");
		}
		else if(event.requestExtraMessageData.verification){
			player=null;
			var size;
			for(i=0;i<this.playerTab.length;i++){
				if(this.playerTab[i].player==event.requestExtraMessageData.playerID){
					player=this.playerTab[i];
				}
			}
			if(event.requestExtraMessageData.verification=="1"){
				if(player!=null){
					size="100%";
					this.layoutVerification.find("#verificationAccept").show();
					switch(this.nbJoueurFindAnswer){
						case 0 : ///faster player answer
							player.score =5;
							this.nbJoueurFindAnswer ++;
							break;
						case 1 :
							player.score =3;
							this.nbJoueurFindAnswer ++;
							break;
						default :
							player.score = 1;
							this.nbJoueurFindAnswer ++;
							break;
					}
				}
			}
			else{
				if(player!=null){
					this.layoutVerification.find("#verificationRefuse").show();
					size="-200%";
					player.score=0;
				}
			}
			var self = this;
			setTimeout(function(){
				$(self.layoutVerification.find(".verification")).animate({
		       		left: size,opacity:0
		    	}, { duration: 800, queue: false }).promise().then(function() {
					self.indCheck ++;//we increase because if the master is idle we recall verification
					self.layoutVerification.find("#verificationAccept").hide();
					self.layoutVerification.find("#verificationRefuse").hide();
					self.verification( self.layoutVerification,self.verificationCallBack);
				 });

			},500);
		}
	}

	/*
		permet d'initialiser le layout du jeu
	*/
	this.initLayout = function(){
		this.wordList=window.preferences.LANGJSON.findMeWord;
		this.wordSelected = null;
		this.letterSelected = null;
		this.playerTab=[];
		this.layoutWord.css({"opacity":"0"});
		this.layoutLetter.css({"opacity":"0"});
		this.layoutTime.html("10");
		this.layout.css({"z-index":"10000"});
	}

	/*
		permet de recuperer le code du jeu
	*/
	this.getId = function(){
		return 1;
	}

	/**
		permet de renvoyer une vérification de réponse au nouveau master
	*/
	this.setMaster=function(player){
		this.gameMaster = player;
		if(player!=null){
			if(this.indCheck>-1){
				this.sendMessageToAll({"verification":this.playerTab[this.indCheck].player,pseudo:this.playerTab[this.indCheck].pseudo,answer:this.playerTab[this.indCheck].answer},this.gameMaster.getId());
			}
		}
	}

	/*
		permet de demander et d'afficher le verificateur
	*/
	this.verification=function(layout,callback){
		console.log("veririfcation");
		console.log(this.indCheck);
		if(this.indCheck == -1){
			this.indCheck = 0;
			this.layoutVerification=layout;
			this.verificationCallBack=callback;
			layout.empty();
			layout.css({"z-index":"10000"});
		}
		if(this.playerTab.length>this.indCheck){
			layout.empty();
			layout.append("<div class='verification'><div class='verificationIconContent'><div class='verificationIcon' id='verificationAccept'><img src='assets/accept.svg' width='200px'/></div><div class='verificationIcon' id='verificationRefuse'><img src='assets/refuse.svg' width='200px'/></div></div><table><tr><td><div class='pseudo'>"+this.playerTab[this.indCheck].pseudo+"</div></td></tr><tr><td><div class='answer'>"+this.playerTab[this.indCheck].answer+"</div></td></tr></div>");
			$(layout.find(".verification")).animate({
		       		top: "50%",opacity:1
		    	}, { duration: 800, queue: false });
			if(this.gameMaster!=null)this.sendMessageToAll({"verification":this.playerTab[this.indCheck].player,pseudo:this.playerTab[this.indCheck].pseudo,answer:this.playerTab[this.indCheck].answer},this.gameMaster.getId());
		}
		else{
			this.indCheck = -1;
			this.verificationCallBack(this.playerTab);
		}
	}


	/**
		permet de charger le template du jeu
	*/
	this.loadTemplateGame = function(callback){
		$( ".containGame" ).load( "templates/findme.html", function() {
			self.layout = $( ".mainGame" );
			self.layoutTime = self.layout.find("#findMeTime");
			self.layoutWord = self.layout.find("#findMeWord");
			self.layoutLetter = self.layout.find("#findMeLetter");
		  callback();
		});
	}

	/**
		permet de connaitre quand un joueur est non disponible
	*/
	this.playerNotAvailable = function(player){

	}
	/**
		permet de connaitre quand un joueur est disponible
	*/
	this.playerAvailable = function(player){

	}

	this.getText = function(){
		return window.preferences.LANGJSON.textFindMe;
	}

}

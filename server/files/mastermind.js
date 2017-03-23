/*
	Permet la définition de la classe du jeu Mastermind
*/
window.Mastermind = function(){

	var self=this;

	this.arrayColor = {"r":"red","y":"yellow","g":"green","b":"blue","o":"#f79e38","w":"white","v":"#57427c","f":"#FF0080"};
	this.arrayAnswer = [];
	this.indPlayer=0;
	this.indLine=0;
	this.playerList;
	this.callback;
	this.arrayPionFinal;

	this.constructor=function(){
	}

	/*
		permet de placer aléatoirement les élements dans une liste
	*/
	this.shuffle = function(a) {
	    var j, x, i;
	    for (i = a.length; i; i--) {
	        j = Math.floor(Math.random() * i);
	        x = a[i - 1];
	        a[i - 1] = a[j];
	        a[j] = x;
	    }
	}

	/*
		permet d'afficher les pions blanc ou rouge
	*/
	this.printLine=function(arrayAnswer,Line){
		table = $("#gameBoard table").eq(Line);
		for(i=0;i<arrayAnswer.length;i++){
			div = (table.find($(".cercleCouleur"))).eq(i);
			div.css("background-color",self.arrayColor[arrayAnswer[i]]);
		}
	}

	/**
		permet d'afficher une ligne de pions
	*/
	this.printLinePion=function(arrayPion,indLine){
		table = $("#gameBoard table").eq(indLine);
		arr = Object.keys(arrayPion);
		for(i=0;i<arr.length;i++){
			div = (table.find($(".pion"))).eq(i);
			div.css("background-color",self.arrayColor[arrayPion[arr[i]]]);
		}
	}

	/**
		verifie si le jeu est terminé
	*/
	this.checkFinish = function(arrayPion){
		for(i=0;i<4;i++){
			if(arrayPion[i]!="r")return false;
		}
		return true;
	}

	/*
		permet de verifier le choix d'un client, de l'afficher et de passer au joueur suivant
	*/
	this.checkLine = function(arrayAnswer,indLine,arrayToFind,player){
		var arrayPion = [];
		arrayPion.push(null);
		arrayPion.push(null);
		arrayPion.push(null);
		arrayPion.push(null);

		self.printLine(arrayAnswer,indLine);

		for(i=0;i<arrayToFind.length;i++){
			for(y=0;y<arrayAnswer.length;y++){
				if(arrayToFind[i]==arrayAnswer[y] && i==y && arrayPion[y]==null){
					arrayPion[y]="r";
					break;
				}
				else if(arrayToFind[i]==arrayAnswer[y] && i!=y && arrayPion[y]==null){
					arrayPion[y]="w";
					break;
				}
			}
		}
		console.log(arrayPion);


		self.shuffle(arrayPion);

		self.printLinePion(arrayPion,indLine);

		this.indLine++;

		if(self.checkFinish(arrayPion) || this.indLine==12){
			this.arrayPionFinal=arrayPion;
			this.callback(true);
		}
		else{
			var stop = this.indPlayer;
			this.indPlayer++;
			if(this.playerList.length<=this.indPlayer){
				this.indPlayer=0;
			}
			while(this.playerList[this.indPlayer].state != 3 && this.indPlayer!=stop){
				this.indPlayer++;
				if(this.indPlayer==this.playerList.length)this.indPlayer=0;
			}
			if(this.indPlayer!=stop || this.playerList[stop].state == 3){
				sendMessageToAll({"mastermind":"1"},self.playerList[this.indPlayer].getId());
			}
		}
	}

	/**
		permet de connaitre quand un joueur est non disponible
	*/
	this.playerNotAvailable = function(player){
		console.log(this.playerList);
		console.log(player);
		var indidleAvailable=0;
		var autho=false;
		var indPLayerNotAvailbe;
		console.log("not playerAvailable");
		for(y=0;y<this.playerList.length;y++){
			if(player.getId()==this.playerList[y].getId()){
				autho=true;
				indPLayerNotAvailbe=y;
				console.log("find in "+y);
				if(player.state==4)this.playerList[y].state=4;
				else this.playerList[y].state=2;
			}
			if(this.playerList[y].state==4 || this.playerList[y].state==3){
				indidleAvailable++;
			}
			console.log("sate "+this.playerList[y].state);
		}
		if(indidleAvailable==0){
			this.indLine=12;
			this.callback(true);
		}
		else if(autho){
			if(this.indPlayer==indPLayerNotAvailbe){
				this.indPlayer++;
				if(this.indPlayer==this.playerList.length)this.indPlayer=0;
				while(this.playerList[this.indPlayer].state != 3 && this.indPlayer!=indPLayerNotAvailbe){
					this.indPlayer++;
					if(this.indPlayer==this.playerList.length)this.indPlayer=0;
				}
				if(this.indPlayer!=indPLayerNotAvailbe){
					sendMessageToAll({"mastermind":"1"},self.playerList[this.indPlayer].getId());
				}
			}
		}
		else console.log(this.playerList);
	}
	/**
		permet de connaitre quand un joueur est disponible
	*/
	this.playerAvailable = function(player){
		console.log(this.playerList);
		for(y=0;y<this.playerList.length;y++){
			if(player.getId()==this.playerList[y].getId()){
				console.log("player playerAvailable");
				console.log("satte "+this.playerList[this.indPlayer].state);
				if(this.playerList[this.indPlayer].state!=3 || this.indPlayer==y){
					this.playerList[y].state=3;
					this.indPlayer=y;
					console.log("in "+this.indPlayer);
					sendMessageToAll({"mastermind":"1"},self.playerList[this.indPlayer].getId());
					break;
				}
				else{
					console.log("wait");
					this.playerList[y].state=3;
					break;
				}
			}
		}
	}

	/**
		permet de charger le template du jeu
	*/
	this.loadTemplateGame = function(callback){
		$( ".containGame" ).load( "templates/mastermind.html", function() {
		  callback();
		});
	}
	/*
		permet de récuperer le nom du jeu pour les clients
	*/
	this.getName=function(){
		return "mastermind";
	}
	/*
		permet de recuperer le code du jeu
	*/
	this.getId = function(){
		return 2;
	}
	/*
		permet de lancer le jeu
	*/
	this.startGame = function(sendMessageToAll,callback,playerList){
		this.initGame();
		this.playerList=playerList;
		sendMessageToAll({"mastermind":"1"},playerList[this.indPlayer].getId());
		this.callback=callback;
	}

	/**
		initialise l'environnement de jeu
	*/
	this.initGame=function(){
		$( ".mainGame" ).css({"z-index":"10000"});
		$(".cercleCouleur").width($(".cercleCouleur").height());
		$(".pion").width($(".pion").height());
		this.arrayAnswer = [];
		arr = Object.keys(this.arrayColor);
		for(i=0;i<4;i++){
			j = Math.floor((Math.random() * 7));
			this.arrayAnswer.push(arr[j]);
		}
		this.indPlayer=0;
		this.indLine=0;
	}

	/**
		permet de gérer les messages des clients
	*/
	this.messageManager=function(player,event){
		if(event.requestExtraMessageData.answer){
			arr = (event.requestExtraMessageData.answer).split(",");
			arr.splice(arr.length-1,1);
			this.checkLine(arr,this.indLine,this.arrayAnswer,player);
		}
	}

	/**
		permet de faire la verification
	*/
	this.verification=function(layout,callbackVerification){
		$( ".mainGame" ).css({"z-index":"80"});
		layout.empty();
		layout.css({"z-index":"10000"});
		layout.append("<div class='verification'><table><tr><td><div class='mastermindVerification' style='background:"+this.arrayColor[this.arrayAnswer[0]]+"'></div></td><td><div class='mastermindVerification' style='background:"+this.arrayColor[this.arrayAnswer[1]]+"'></div></td><td><div class='mastermindVerification' style='background:"+this.arrayColor[this.arrayAnswer[2]]+"'></div></td><td><div class='mastermindVerification' style='background:"+this.arrayColor[this.arrayAnswer[3]]+"'></div></td></tr></table></div>")
		$(layout.find(".verification")).animate({
		       		top: "50%",opacity:1
		    	}, { duration: 800, queue: false });
		    	setTimeout(function(){
		    			layout.css({"z-index":"60"});
		    			layout.empty();
						if(self.checkFinish(self.arrayPionFinal))callbackVerification([{player:self.playerList[self.indPlayer].getId(),score:5}]);
						else callbackVerification([]);
		    		},5000)
	}

	this.setMaster=function(){

	}

	this.getText = function(){
		return window.preferences.LANGJSON.textmastermind;
	}

}

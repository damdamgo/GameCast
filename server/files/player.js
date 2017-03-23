/*
	Permet la définition de la classe d'un joueur
*/
window.Player = function(){
	var self = this;
	this.pseudo;
	this.playerId;
	this.score = 0;
	/**
	* 1 : player is connecting
	* 2 : player is disconnecting // 1 min to reconnect
	* 3 : player is playing
	* 4 : player is idle
	*/
	this.state = 1;
	this.layout;
	this.master = false;
	this.place;
	this.constructor = function(playerId,layout,place){
		this.playerId = playerId;
		this.layout = layout;
		this.place=place;
	}
	this.setPseudo = function(pseudo){
		this.pseudo = pseudo;
	}
	this.getPseudo = function(){
		return this.pseudo;
	}
	this.addScore = function(addSco){
		this.score = this.score + addSco;
		this.refresh()
	}
	this.getScore = function(){
		return this.score;
	}
	this.getLayout = function(){
		return this.layout;
	}
	this.getId = function(){
		return this.playerId;
	}
	this.getState = function(){
		return this.state;
	}
	this.getMaster = function(){
		return this.master;
	}
	this.setMaster = function(mas){
		this.master = mas;
		this.refresh();
	}
	this.refresh = function(){
		this.changeState(this.state);
	}
	this.changeState=function(state){
		this.state = state;
		switch(this.state){
			case 1 :
				this.showStateConnecting();
				break;
			case 2 :
				this.showStateDisconnecting();
				break;
			case 3 :
				this.showStatePlaying();
				break;
			case 4 :
				this.showStateIdle();
				break;
			case 5 :
				this.destroyDisplaying();
				break;
		}
	}
	this.destroyDisplaying = function(){
		var layout = this.layout;
		$.get('templates/templatePlayerState5.mst', function(template) {
		    var rendered = Mustache.render(template,  null);
		    layout.html(rendered);
		});
	}
	this.showStateConnecting = function(){
		var layout = this.layout;
		$.get('templates/templatePlayerState1.mst', function(template) {
		    var rendered = Mustache.render(template, {mes:window.preferences.LANGJSON.connexionMessage});
		    layout.html(rendered);
		});
	}
	this.showStateDisconnecting = function(){
		var layout = this.layout;
		$.get('templates/templatePlayerState2.mst', function(template) {
		    var rendered = Mustache.render(template, {mes:window.preferences.LANGJSON.disconnectionMessage});
		    layout.html(rendered);
		});
	}
	this.showStatePlaying = function(){
		var layout = this.layout;
		$.get('templates/templatePlayerState3.mst', function(template) {
		    if(self.master)var rendered = Mustache.render(template, {nickname:self.pseudo,score:self.score,state:window.preferences.LANGJSON.master});
		    else var rendered = Mustache.render(template, {nickname:self.pseudo,score:self.score,state:window.preferences.LANGJSON.play});
		    layout.html(rendered);
		});
	}
	this.showStateIdle = function(){
		var layout = this.layout;
		$.get('templates/templatePlayerState4.mst', function(template) {
		    var rendered = Mustache.render(template, {nickname:self.pseudo,score:self.score,state:window.preferences.LANGJSON.idle});
		    layout.html(rendered);
		});
	}
}

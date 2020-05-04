var app = new Vue({
    el: '#app',
    data: {
        numbers: [0 , 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        letters:["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        jsonComplete: [],
        ships: [],
        players: [],
        me: null,
        otherPlayer: null,
        salvoes: [],
    },
    methods: {
        paintShips (){
                this.ships.forEach( ship => {ship.shipPositions.forEach( position => {

                        var turn = this.isHit(position);

                            if (turn != -1){
                                    document.getElementById(position).style.backgroundColor = "#ff0000";
                                    document.getElementById(position).innerHTML = turn;
                            } else{
                                if (ship.type === "Cruiser"){
                                    document.getElementById(position).style.backgroundColor = "#ffff00";
                                } else{
                                    document.getElementById(position).style.backgroundColor = "#55ff33";
                                }
                            }
                        })
                })
        },
        isHit(position){

            var turn = -1;

                 this.salvoes.forEach( salvo => {
                           if(salvo.playerID != this.me.id){
                               if (salvo.salvoLocations.includes(position)){
                                    turn = salvo.turnNumber;
                               }
                           }
                 })
                 return turn;
        },
        printGameInfo(){
                this.jsonComplete.gamePlayer.forEach( value => this.players.push(value.player))
        },
        printPlayers(){
                for (let i=0; i<this.jsonComplete.gamePlayer.length; i++){
                            if (this.jsonComplete.gamePlayer[i].id == params.gp){
                                this.me = this.jsonComplete.gamePlayer[i].player
                            } else{
                                this.otherPlayer = this.jsonComplete.gamePlayer[i].player;
                            }
                }
        },
        paintMySalvoesWithTurn(){
                this.salvoes.forEach ( salvo => { salvo.salvoLocations.forEach( location => {
                            if (salvo.playerID == this.me.id){
                                document.getElementById(location + "S").style.backgroundColor = "#ffa533";
                                document.getElementById(location + "S").innerHTML = salvo.turnNumber;
                            }
                       })
                })
        },
    },
})


var params = paramObj(location.search)


fetch("/api/game_view/" + params.gp)
    .then((Response) => {
        return Response.json();
    })
    .then((myJson) => {

        app.jsonComplete = myJson;
        app.ships = myJson.ships;
        app.salvoes = myJson.salvoes

        app.printPlayers();
        app.paintShips();
        app.printGameInfo();
        app.paintMySalvoesWithTurn();
    })


function paramObj(search) {
  var obj = {};
  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

  search.replace(reg, function(match, param, val) {
    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
  });
  return obj;
}


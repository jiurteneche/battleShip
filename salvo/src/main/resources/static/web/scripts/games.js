var app = new Vue({
    el: '#app',
    data: {
        games: [],
        player: [],
        playersData: [],
        username: "",
        password: "",
        firstname: "",
        lastname: "",
//        location: "",
    },
    methods: {
        getDataByPlayerOrdered(){

            this.games.forEach( game => { game.gamePlayers.forEach( gamePlayer => {

                 var playerData = {
                                userName: "",
                                totalScore: 0,
                                won: 0,
                                tied: 0,
                                lost: 0,
                                };

                 var objectInPlayersData = this.playersData.find(value => value.userName == gamePlayer.player.userName);

                            if (objectInPlayersData == undefined){

                                 playerData.userName = gamePlayer.player.userName;

                                 playerData.totalScore = gamePlayer.score;

                                     if(gamePlayer.score == 1.0){
                                         playerData.won = 1;
                                     }else if(gamePlayer.score == 0.5){
                                         playerData.tied = 1;
                                     }else if(gamePlayer.score == 0){
                                         playerData.lost = 1;
                                     }
                                 this.playersData.push(playerData);
                            }else{
                                 objectInPlayersData.totalScore += gamePlayer.score;

                                     if(gamePlayer.score == 1.0){
                                          objectInPlayersData.won += 1;
                                      }else if(gamePlayer.score == 0.5){
                                          objectInPlayersData.tied += 1;
                                      }else if(gamePlayer.score == 0){
                                          objectInPlayersData.lost += 1;
                                      }
                            }
                        }
                    )}
                 )

            this.playersData.sort(function(a, b){
                            if(a.totalScore > b.totalScore){
                                return -1;
                            }
                            if(a.totalScore < b.totalScore){
                                return 1;
                            }
                            return 0;
                        })

            console.log(this.playersData);
        },
        login(){
            $.post("/api/login", { username: this.username, password: this.password })
            .done(function() {
                request();
                console.log("logged in!");
                alert("You are logged!");
                app.username = "";
                app.password = "";
            })
            .fail(function(){
                alert("User name or password incorrect")
            })
        },
        logout(){
            $.post("/api/logout")
            .done(function() {
                console.log("logged out");
                alert("You are dislogged");
                app.player = null;
            })
        },
        signup(){
            $.post("/api/players", { firstname: this.firstname, lastname: this.lastname,
            username: this.username, password: this.password })
            .done(function() {
                console.log("signed in!");
                alert("Signed succesfully. Let´s go play!");
                app.login();
                app.firstname = "";
                app.lastname = "";
            })
            .fail(function(error) {
                console.log(error);
                alert("You must complete all fields");
            })
        },
        enterGame(gamePlayerID){
            location.href = 'http://localhost:8080/web/game_view.html?gp='+gamePlayerID;
        },
        createNewGame(){
            $.post("/api/games")
            .done(function(Response){
                console.log("Game created successfully");
                location.href = 'http://localhost:8080/web/game_view.html?gp='+Response.GpId;
            })
            .fail(function(error){
                console.log(error)
                alert("You must be logged to create a game")
            })
        },
    },
});

function request(){
    fetch("http://localhost:8080/api/games")
        .then((Response) => {
            return Response.json();
        })
        .then((myJson) => {
            console.log(myJson);
            app.games = myJson.games;
            app.player = myJson.player;
            app.getDataByPlayerOrdered();
        })
}
request();

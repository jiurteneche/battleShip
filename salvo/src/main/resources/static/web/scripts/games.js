var app = new Vue({
    el: '#app',
    data: {
        games: [],
        playersData: [],
        username: "",
        password: "",
        firstname: "",
        lastname: "",
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
            $.post("/api/login", { username: this.username, password: this.password }).done(function() {
            console.log("logged in!");
            alert("You are logged!");
            })

            this.username = "";
            this.password = "";
        },
        logout(){
            $.post("/api/logout").done(function() {
            console.log("logged out");
            alert("You are dislogged")
            })
        },
        signup(){
            $.post("/api/players", { firstname: this.firstname, lastname: this.lastname,
            username: this.username, password: this.password }).done(function() {
                console.log("signed in!");
                alert("Signed succesfully. Now, you can log in to play!");
                app.login();
            }).fail(function(error) {
                console.log(error);
            })

            this.firstname = "";
            this.lastname = "";
        },
    },
});


fetch("http://localhost:8080/api/games")
    .then((Response) => {
        return Response.json();
    })
    .then((myJson) => {

        console.log(myJson);
        app.games = myJson;
        app.getDataByPlayerOrdered();
    })


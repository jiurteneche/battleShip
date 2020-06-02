var app = new Vue({
    el: '#app',
    data: {
        numbers: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        letters: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        jsonComplete: [],
        ships: [],
        players: [],
        me: null,
        otherPlayer: null,
        salvoes: [],
        placedShips: [],
        shipType: "aircraft",
        shipOrientation: "vertical",
        gamePlayerId: null,
        tdsSelectedToFire: [],
        mySunkenShips: 0,
        //Acá tengo los salvoes del oponente ordenados por turnNumber. El primero del array[0] es siempre el último disparo, es decir, el que tiene el turnNumber más alto
        opponentSalvoes: [],
    },
    methods: {
        paintShips() {
            this.ships.forEach(ship => {
                ship.shipPositions.forEach(position => {

                    var turn = this.isHit(position);

                    if (turn != -1) {
                        document.getElementById(position).style.backgroundColor = "#ff0000";
                        document.getElementById(position).innerHTML = turn;
                    } else {
                        document.getElementById(position).style.backgroundColor = "#55ff33";
                    }
                })
            })
        },
        isHit(position) {

            var turn = -1;

            this.salvoes.forEach(salvo => {
                if (salvo.playerID != this.me.id) {
                    if (salvo.salvoLocations.includes(position)) {
                        turn = salvo.turnNumber;
                    }
                }
            })
            return turn;
        },
        printGameInfo() {
            this.jsonComplete.gamePlayer.forEach(value => this.players.push(value.player))
        },
        printPlayers() {
            for (let i = 0; i < this.jsonComplete.gamePlayer.length; i++) {
                if (this.jsonComplete.gamePlayer[i].id == this.gamePlayerId) {
                    this.me = this.jsonComplete.gamePlayer[i].player
                } else {
                    this.otherPlayer = this.jsonComplete.gamePlayer[i].player;
                }
            }
        },
        paintMySalvoesWithTurn() {
            this.salvoes.forEach(salvo => {
                salvo.salvoLocations.forEach(location => {
                    if (salvo.playerID == this.me.id) {
                        document.getElementById(location + "S").style.backgroundColor = "#ffa533";
                        document.getElementById(location + "S").innerHTML = salvo.turnNumber;
                    }
                })
            })
        },
        addShips(cell) {

            //Acá pregunta si la ship que quiero colocar fue colocada anteriormente. Si lo fue, me tira el alert del final. Sino, entra
            if (this.placedShips.filter(ship => ship.type == this.shipType).length == 0) {

                if (this.shipType == "aircraft") {
                    var color = "#FF3339";
                    var totalCells = 5;
                } else if (this.shipType == "battleship") {
                    var color = "#33A5FF";
                    var totalCells = 4;
                } else if (this.shipType == "destroyer") {
                    var color = "#33FF8D";
                    var totalCells = 3;
                } else if (this.shipType == "submarine") {
                    var color = "#FFF333";
                    var totalCells = 3;
                } else {
                    var color = "#FF9933";
                    var totalCells = 2;
                }

                //Acá pregunta por la orientación que elegí en el radio. Si elegí vertical, entra; sino, pasa al else de horizontal
                if (this.shipOrientation == "vertical") {

                    //letter en realidad almacena un número (que es la letra pasada a número según no sé qué tabla)
                    var letter = cell[0].charCodeAt(0);
                    var number = cell.slice(1);

                    //Acá se termina la grilla verticalmente
                    var verticalEnd = "J".charCodeAt(0);

                    //Variable auxiliar
                    var free = true;

                    //Acá verifico si la ship que eligió entra o no (por su longitud) en donde la quiere poner
                    if (verticalEnd - letter < totalCells - 1) {

                        alert("You cannot place a ship out of range");

                    } else {

                        //Este for recorre el total de celdas a pintar (teniendo en cuenta la longitud de la ship elegida y la orientación seleccionada)
                        for (let i = 0; i < totalCells - 1; i++) {

                            //Acá pregunta si alguna de las naves ya colocadas incluye en su posición a la celda donde estoy haciendo el on:click en este momento + 1
                            if (this.placedShips.filter(placedShip => placedShip.shipPositions.includes(String.fromCharCode(letter + (i + 1)) + number)).length > 0) {

                                free = false;

                            }

                        }

                        //Acá pregunto en base al valor de la variable auxiliar
                        if (!free) {

                            alert("Be carefull! Your ships cannot be overlapped");

                        } else {

                            //Este objeto se va a crear y rellenar por cada ship que coloque
                            var placedShip = {
                                type: this.shipType,
                                shipPositions: [],
                            };

                            //Agrego esta cell al array shipPositions de placedShip
                            placedShip.shipPositions.push(cell);

                            //Este for es para pintar el resto de las celdas dependiende de la longitud del ship (la clickeada ya la pinté, por eso
                            //el totalCells - 1)
                            for (let i = 0; i < totalCells - 1; i++) {

                                //Pinta la celda clickeada
                                document.getElementById(cell).style.backgroundColor = color;
                                //Pinta las celdas restantes (dependiendo de la longitud de la ship)
                                document.getElementById(String.fromCharCode(letter + (i + 1)) + number).style.backgroundColor = color;
                                //Agrega esas mismas celdas restantes a placedShip.shipPositions
                                placedShip.shipPositions.push(String.fromCharCode(letter + (i + 1)) + number);
                            }

                            this.placedShips.push(placedShip);

                        }
                    }

                    //Horizontal    
                } else {

                    var letter = cell[0];
                    var number = cell.slice(1);

                    //Acá se termina la grilla horizontalmente
                    var horizontalEnd = 10;

                    var free = true;

                    if (horizontalEnd - number < totalCells - 1) {

                        alert("You cannot place a ship out of range");

                    } else {

                        for (let i = 0; i < totalCells - 1; i++) {

                            if (this.placedShips.filter(placedShip => placedShip.shipPositions.includes(letter + (parseInt(number) + (i + 1)))).length > 0) {

                                free = false;

                            }

                        }

                        if (!free) {

                            alert("Be carefull! Your ships cannot be overlapped");

                        } else {

                            var placedShip = {
                                type: this.shipType,
                                shipPositions: [],
                            };

                            placedShip.shipPositions.push(cell);

                            for (let i = 0; i < totalCells - 1; i++) {

                                document.getElementById(cell).style.backgroundColor = color;
                                //parseInt(number) es necesario porque, de entrada, C1 (por ej) es un String. ParseInt me lo pasa a número para que después le pueda hacer ese + 1
                                document.getElementById(letter + (parseInt(number) + (i + 1))).style.backgroundColor = color;
                                placedShip.shipPositions.push(letter + (parseInt(number) + (i + 1)));

                            }

                            this.placedShips.push(placedShip);

                        }
                    }

                }

            } else {
                alert("You cannot place the same ship twice!")
            }
        },
        sendShips() {

            if (this.placedShips.length < 5) {
                alert("You must place all ships")
            } else {
                $.post({
                        url: "/api/games/players/" + app.gamePlayerId + "/ships",
                        data: JSON.stringify(this.placedShips),
                        dataType: "text",
                        contentType: "application/json"
                    })
                    .done(function (response, status, jqXHR) {
                        alert("Ships added successfully");
                        document.getElementById("add-ships").classList.add("d-none");
                        location.reload(true);
                    })
                    .fail(function (jqXHR, status, httpError) {
                        alert("Failed to add ships: " + textStatus + " " + httpError);
                    })
            }
        },
        selectTdToFire(cell) {

            var letter = cell[0];
            var number = "";

            if (cell.length == 3) {
                number = cell.slice(1, 2);
            } else {
                number = cell.slice(1, 3);
            }

            var salvoesFired = [];

            this.salvoes.forEach(salvo => {
                salvo.salvoLocations.forEach(location => {
                    if (salvo.playerID == this.me.id) {
                        salvoesFired.push(location);
                    }
                })
            })

            if (this.tdsSelectedToFire.length >= (5 - this.mySunkenShips)) {
                alert("You cannot fire more salvoes!")
            } else if (salvoesFired.includes(letter + number) || this.tdsSelectedToFire.includes(letter + number)) {
                alert("You cannot fire the same position twice!")
            } else {

                this.tdsSelectedToFire.push(letter + number);

                document.getElementById(cell).style.backgroundColor = "#FF3333";

            }

        },
        sendSalva() {

            $.post({
                    url: "/api/games/players/" + app.gamePlayerId + "/salvoes",
                    data: JSON.stringify(this.tdsSelectedToFire),
                    dataType: "text",
                    contentType: "application/json"
                })
                .done(function (response, status, jqXHR) {
                    alert("Salva fire successfully");
                    location.reload(true);
                })
                .fail(function (jqXHR, status, httpError) {
                    alert("Failed to fire salva: " + textStatus + " " + httpError);
                })

        },
        salvoesOrdered() {

            this.salvoes.sort(function (a, b) {
                if (a.turnNumber > b.turnNumber) {
                    return -1;
                }
                if (a.turnNumber < b.turnNumber) {
                    return 1;
                }
                return 0;
            })
        },
        changeClassForTables() {

            this.salvoes.forEach(salvo => {
                if (salvo.hits.length == 0) {
                    console.log(this.salvoes);
                } else {
                    document.getElementById("tables").classList.remove("d-none");
                }
            })

        },
        calculateMySunkenShips() {

            if (this.salvoes.length != 0){

                for (let i=0; i<this.salvoes.length; i++){

                    if(this.salvoes[i].playerID != this.me.id){
    
                        this.opponentSalvoes.push(this.salvoes[i]);
    
                    }
    
                }

            }
            
            this.mySunkenShips = this.opponentSalvoes[0].sunken.length;

        },
    },
})


app.gamePlayerId = paramObj(location.search).gp;

fetch("/api/game_view/" + app.gamePlayerId)
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
        app.salvoesOrdered();
        app.changeClassForTables();
        app.calculateMySunkenShips();
    })


function paramObj(search) {
    var obj = {};
    var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

    search.replace(reg, function (match, param, val) {
        obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });
    return obj;
}



var timerId;

function startTimer(){

    timerId = setInterval(() => {
        console.log("Juan capo"); 
        fetch("/api/game_view/" + app.gamePlayerId); 
        stopTimer(timerId);
    }, 50000);  

}
startTimer();

function stopTimer(timerId){

    app.salvoes.forEach(salvo => {

        if(salvo.sunken.length == 5){

            clearInterval(timerId);

        }
    })
}
     
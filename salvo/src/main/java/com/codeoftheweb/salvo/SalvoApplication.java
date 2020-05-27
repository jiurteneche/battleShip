package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    //Esto lo hacemos para encriptar las constraseñas en la base de datos por si nos hackean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository players, GameRepository games, GamePlayerRepository gamePlayers,
                                      ShipRepository ships, SalvoRepository salvoes, ScoreRepository scores) {
        return (args) -> {

        	//Jugadores creados y guardados en la base de datos
            Player player1 = new Player("Jack", "Bauer", "j.bauer@ctu.gov", passwordEncoder().encode("Jack123"));
            players.save(player1);

			Player player2 = new Player("Chloe", "O'Brian", "c.obrian@ctu.gov", passwordEncoder().encode("Chloe123"));
			players.save(player2);

            Player player3 = new Player("Kim", "Bauer", "kim_bauer@gmail.com", passwordEncoder().encode("Kim123"));
			players.save(player3);

            Player player4 = new Player("Toni", "Almeida", "t.almeida@ctu.gov", passwordEncoder().encode("Toni123"));
			players.save(player4);

			//PLAYER MÍO QUE QUIERO QUE SEA ADMIN
            Player player5 = new Player("Juan", "Urteneche", "jiurteneche@gmail.com", passwordEncoder().encode("mindhub123"));
            players.save(player5);

			//Juegos creados y guardados en la base de datos
            Game game1 = new Game();
			games.save(game1);

            Game game2 = new Game(LocalDateTime.now().plusHours(1));
            games.save(game2);

            Game game3 = new Game(LocalDateTime.now().plusHours(2));
            games.save(game3);

            //GamePlayer creados y guardados en la base de datos
            //GamePlayer relaciona a los Players con los Games
            GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
            gamePlayers.save(gamePlayer1);
            GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
            gamePlayers.save(gamePlayer2);

            GamePlayer gamePlayer3 = new GamePlayer(game2, player3);
            gamePlayers.save(gamePlayer3);
            GamePlayer gamePlayer4 = new GamePlayer(game2, player4);
            gamePlayers.save(gamePlayer4);

            GamePlayer gamePlayer5 = new GamePlayer(game3, player1);
            gamePlayers.save(gamePlayer5);

            //Ships creados y guardados en la base de datos (para cada GamePlayer)
            Ship ship1 = new Ship ("Submarine", Arrays.asList("A1","A2","A3"), gamePlayer1);
            ships.save(ship1);
            Ship ship2 = new Ship ("Destroyer", Arrays.asList("H1","I1","J1"), gamePlayer1);
            ships.save(ship2);
            Ship ship3 = new Ship ("Aircraft-Carrier", Arrays.asList("B1","B2","B3","B4","B5"), gamePlayer1);
            ships.save(ship3);
            Ship ship4 = new Ship ("Battleship", Arrays.asList("E2","E3","E4","E5"), gamePlayer1);
            ships.save(ship4);
            Ship ship21 = new Ship ("Patrol-Boat", Arrays.asList("J5", "J6"), gamePlayer1);
            ships.save(ship21);

            Ship ship5 = new Ship ("Destroyer", Arrays.asList("C1","C2","C3"), gamePlayer2);
            ships.save(ship5);
            Ship ship6 = new Ship ("Submarine", Arrays.asList("D1","E1","F1"), gamePlayer2);
            ships.save(ship6);
            Ship ship7 = new Ship ("Aircraft-Carrier", Arrays.asList("G1","G2","G3","G4","G5"), gamePlayer2);
            ships.save(ship7);
            Ship ship8 = new Ship ("Battleship", Arrays.asList("J5","J6","J7","J8"), gamePlayer2);
            ships.save(ship8);
            Ship ship22 = new Ship ("Patrol-Boat", Arrays.asList("A5", "A6"), gamePlayer2);
            ships.save(ship22);

            Ship ship9 = new Ship ("Destroyer", Arrays.asList("H1","I1","J1"), gamePlayer3);
            ships.save(ship9);
            Ship ship10 = new Ship ("Submarine", Arrays.asList("D1","E1","F1"), gamePlayer3);
            ships.save(ship10);
            Ship ship11 = new Ship ("Aircraft-Carrier", Arrays.asList("G1","G2","G3","G4","G5"), gamePlayer3);
            ships.save(ship11);
            Ship ship12 = new Ship ("Battleship", Arrays.asList("A10","B10","C10","D10"), gamePlayer3);
            ships.save(ship12);
            Ship ship23 = new Ship ("Patrol-Boat", Arrays.asList("A5", "B5"), gamePlayer3);
            ships.save(ship23);

            Ship ship13 = new Ship ("Submarine", Arrays.asList("D1","E1","F1"), gamePlayer4);
            ships.save(ship13);
            Ship ship14 = new Ship ("Destroyer", Arrays.asList("F8","F9","F10"), gamePlayer4);
            ships.save(ship14);
            Ship ship15 = new Ship ("Battleship", Arrays.asList("A1","A2","A3","A4"), gamePlayer4);
            ships.save(ship15);
            Ship ship16 = new Ship ("Aircraft-Carrier", Arrays.asList("I6","I7","I8","I9","I10"), gamePlayer4);
            ships.save(ship16);
            Ship ship24 = new Ship ("Patrol-Boat", Arrays.asList("C1", "C2"), gamePlayer4);
            ships.save(ship24);

            Ship ship17 = new Ship ("Destroyer", Arrays.asList("I7","I8","I9"), gamePlayer5);
            ships.save(ship17);
            Ship ship18 = new Ship ("Submarine", Arrays.asList("F8","F9","F10"), gamePlayer5);
            ships.save(ship18);
            Ship ship19 = new Ship ("Aircraft-Carrier", Arrays.asList("A1","B1","C1","D1","E1"), gamePlayer5);
            ships.save(ship19);
            Ship ship20 = new Ship ("Battleship", Arrays.asList("A7","A8","A9","A10"), gamePlayer5);
            ships.save(ship20);
            Ship ship25 = new Ship ("Patrol-Boat", Arrays.asList("J9", "J10"), gamePlayer5);
            ships.save(ship25);

            //Salvoes creados y guardados en la base de datos (para cada GamePlayer x turno)
            Salvo salvo1 = new Salvo ( 1, Arrays.asList("A1", "C3"), gamePlayer1);
            salvoes.save(salvo1);
            Salvo salvo2 = new Salvo (1, Arrays.asList("D1", "J1"), gamePlayer2);
            salvoes.save(salvo2);
            Salvo salvo3 = new Salvo (1, Arrays.asList("A10", "H1"), gamePlayer3);
            salvoes.save(salvo3);
            Salvo salvo4 = new Salvo (1, Arrays.asList("A5", "H1"), gamePlayer4);
            salvoes.save(salvo4);
            Salvo salvo5 = new Salvo (1, Arrays.asList("F10", "A10"), gamePlayer5);
            salvoes.save(salvo5);

            Salvo salvo6 = new Salvo ( 2, Arrays.asList("I10", "C4"), gamePlayer1);
            salvoes.save(salvo6);
            Salvo salvo7 = new Salvo (2, Arrays.asList("D9", "J3"), gamePlayer2);
            salvoes.save(salvo7);
            Salvo salvo8 = new Salvo (2, Arrays.asList("A1", "H10"), gamePlayer3);
            salvoes.save(salvo8);
            Salvo salvo9 = new Salvo (2, Arrays.asList("A3", "C3"), gamePlayer4);
            salvoes.save(salvo9);
            Salvo salvo10 = new Salvo (2, Arrays.asList("F1", "B9"), gamePlayer5);
            salvoes.save(salvo10);

            //Scores creados y guardados en la base de datos (para cada player x partida)
            Score score1 = new Score(LocalDateTime.now(), 1, game1, player1);
            scores.save(score1);
            Score score2 = new Score(LocalDateTime.now(), 0, game1, player2);
            scores.save(score2);

            Score score3 = new Score(LocalDateTime.now(), 0.5, game2, player3);
            scores.save(score3);
            Score score4 = new Score(LocalDateTime.now(), 0.5, game2, player4);
            scores.save(score4);

            Score score5 = new Score(LocalDateTime.now(), 1, game3, player1);
            scores.save(score5);
        };
    }
}

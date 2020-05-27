package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//Esta clase es la que une Game con Player y sus respectivas Ship (por eso tiene relaciones con las 3 clases mencionadas)
@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gamePlayer", cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gamePlayer", cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();

    public GamePlayer() {}

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.joinDate = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public Set<Ship> getShips() { return ships; }
    public void setShips(Set<Ship> ships) { this.ships = ships; }

    public void addShip(Ship ship){ this.ships.add(ship); ship.setGamePlayer(this); }

    public Set<Salvo> getSalvoes() { return salvoes; }
    public void setSalvoes(Set<Salvo> salvoes) {this.salvoes = salvoes;}

    public void addSalvo(Salvo salvo){ this.salvoes.add(salvo); salvo.setGamePlayer(this); }

    public Score getScore(){ return this.player.getScoreByGame(this.game); }

    public GamePlayer getOpponent(){
        return this.getGame().getGamePlayers()
            .stream().filter(gp -> gp.getId() != this.getId())
            .findFirst()
            .orElse(null);
    }

}


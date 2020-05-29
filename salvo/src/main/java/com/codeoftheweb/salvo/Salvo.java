package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turnNumber;

    @ElementCollection
    @CollectionTable(name = "salvoLocations")
    private List<String> salvoLocations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo() {
    }

    public Salvo(int turnNumber, List<String> salvoLocations, GamePlayer gamePlayer) {
        this.turnNumber = turnNumber;
        this.salvoLocations = salvoLocations;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getHits() {

        Set<Ship> opponentShips = this.gamePlayer.getOpponent().getShips();
        List<String> allEnemyLocations = new ArrayList<>();

        opponentShips.forEach(ship -> allEnemyLocations.addAll(ship.getShipPositions()));

        return this.salvoLocations
                .stream()
                .filter(shot -> allEnemyLocations
                        .stream()
                        .anyMatch(location -> location.equals(shot)))
                .collect(Collectors.toList());

    }

    public List<Ship> getSunkenShips() {

        List<String> allShots = new ArrayList<>();
        Set<Ship> opponentShips = this.gamePlayer.getOpponent().getShips();
        Set<Salvo> mySalvoes = this.getGamePlayer()
                .getSalvoes()
                .stream()
                .filter(sal -> sal.getTurnNumber() <= this.turnNumber)
                .collect(Collectors.toSet());

        mySalvoes.forEach(salvo -> allShots.addAll(salvo.getSalvoLocations()));

        return opponentShips
                .stream()
                .filter(ship -> allShots.containsAll(ship.getShipPositions()))
                .collect(Collectors.toList());

    }

}

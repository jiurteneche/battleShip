package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime finishDate;

    private double score;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    public Score() {}

    public Score(LocalDateTime finishDate, double score, Game game, Player player){
        this.finishDate = finishDate;
        this.score = score;
        this.game = game;
        this.player = player;
    }

    public long getId() { return id; }

    public LocalDateTime getFinishDate() { return finishDate; }
    public void setFinishDate(LocalDateTime finishDate) { this.finishDate = finishDate; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }









}

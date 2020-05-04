//Paquete (carpeta) donde tengo guardada la clase (que luego se podría import desde otro proyecto)
package com.codeoftheweb.salvo;

//Imports de los paquetes que utilicé en esta clase (paquetes que vienen predeterminados en Spring y demás)
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;

//Anotación para crear una tabla con entidad propia en la base de datos
@Entity
public class Player {

    //Atributos/Propiedades
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String firstName;

    private String lastName;

    private String userName;

    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    //Constructores
    //Este constructor vacío es requisito de la JPA
    public Player() {}

    public Player(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }

    //Métodos
    //Primero los getter y setter
    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) { this.userName = userName; }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }
    public void setGamePlayers(Set<GamePlayer> gamePlayers) { this.gamePlayers = gamePlayers; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Score getScoreByGame(Game game){
        return this.scores.stream().filter(score -> score.getGame().getId() == game.getId()).findFirst().orElse(null);
    }


    //Método que retorna todos los games relacionados con el player
    @JsonIgnore //Esta anotacion sirve para que no se haga un bucle infinito
    public List<Game> getGames() { return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList()); }
}
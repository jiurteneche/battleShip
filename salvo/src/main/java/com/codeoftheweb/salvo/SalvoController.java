package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping("/games")
    public List<Map<String, Object>> getAll() {
        return gameRepository.findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> gamePlayerId (@PathVariable Long gamePlayerId){
        GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if(gp != null){
            return this.makeGameViewDTO(gp);
        } else{
            return null;
        }
    }

    //Este @RequestMapping del tipo POST sirve para darle acceso a los usuarios a la base de datos (y, de esa forma, puedan crear sus usuarios)
    //Este método devuelve ResponseEntity que no son otra cosa que objetos que muestran un código de error o de éxito en el front
    //cuando el usuario quiere crear su propio usuario
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String firstname, @RequestParam String lastname,
                                                          @RequestParam String username, @RequestParam String password){
        ResponseEntity<Map<String, Object>> response;
        Player player = playerRepository.findByUserNameIgnoreCase(username);
        if(firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || password.isEmpty()){
            response = new ResponseEntity<>(makeMap("Error", "You must complete all fields"), HttpStatus.FORBIDDEN);
        } else if (player != null){
            response = new ResponseEntity<>(makeMap("Error", "Username already exists"), HttpStatus.FORBIDDEN);
        } else {
            Player newPlayer = playerRepository.save(new Player(firstname, lastname, username, passwordEncoder.encode(password)));
            response = new ResponseEntity<>(makeMap("Id", newPlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }




    //FUNCIONES NECESARIAS PARA HACER EL @RequestMapping("/games") ---------------------------------------------------------------------
        private Map<String, Object> makeGameDTO(Game game) {
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("id", game.getId());
            dto.put("creationDate", game.getCreationDate());
            dto.put("gamePlayers", game.getGamePlayers().stream().map(gamePlayer -> makeGamePlayerDTO(gamePlayer)).collect(toList()));
            return dto;
        }

        private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("id", gamePlayer.getId());
            dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));

            Score score = gamePlayer.getScore();
            if (score != null){
                dto.put("score", score.getScore());
            } else {
                dto.put("score", null);
            }

            return dto;
        }

        private Map<String, Object> makePlayerDTO(Player player) {
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("id", player.getId());
            dto.put("userName", player.getUserName());
            return dto;
        }
    //---------------------------------------------------------------------------------------------------------------------------------




    //FUNCIONES NECESARIAS PARA HACER EL @RequestMapping("/game_view/{gamePlayerId}") ---------------------------------------------------------------------
        private Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer){
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", gamePlayer.getGame().getId());
            dto.put("creationDate", gamePlayer.getGame().getCreationDate());
            dto.put("gamePlayer", gamePlayer.getGame().getGamePlayers().stream().map(gp -> makeGamePlayerDTO(gp)).collect(toList()));
            dto.put("ships", gamePlayer.getShips().stream().map(this::makeShipDTO));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(this::makeSalvoDTO)));

            return dto;
        }

        private Map<String, Object> makeShipDTO(Ship ship){
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", ship.getId());
            dto.put("type", ship.getType());
            dto.put("shipPositions", ship.getShipPositions());

            return dto;
        }

        private Map<String, Object> makeSalvoDTO(Salvo salvo){
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", salvo.getId());
            dto.put("turnNumber", salvo.getTurnNumber());
            dto.put("salvoLocations", salvo.getSalvoLocations());
            dto.put("playerID", salvo.getGamePlayer().getPlayer().getId());

            return dto;
        }
    //-----------------------------------------------------------------------------------------------------------------------------------




    //FUNCIÓN NECESARIA PARA HACER EL @RequestMapping("/players")--------------------------------------------------------------------------
        private Map<String, Object> makeMap(String key, Object value){
            Map<String, Object> map = new HashMap<>();
            map.put(key, value);
            return map;
        }
    //-------------------------------------------------------------------------------------------------------------------------------------








}

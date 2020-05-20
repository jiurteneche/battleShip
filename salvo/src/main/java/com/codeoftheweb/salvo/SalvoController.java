package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (!isGuest(authentication)) {
            dto.put("player", makePlayerDTO(playerRepository.findByUserNameIgnoreCase(authentication.getName())));
        } else {
            dto.put("player", null);
        }
        dto.put("games", gameRepository.findAll().stream().map(game -> makeGameDTO(game)).collect(toList()));

        return dto;
    }

    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("Error", "You must be logged in first"), HttpStatus.UNAUTHORIZED);
        } else {
            GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserNameIgnoreCase(authentication.getName());
            if (gp == null) {
                response = new ResponseEntity<>(makeMap("Error", "No such game"), HttpStatus.NOT_FOUND);
            } else if (gp.getPlayer().getId() != player.getId()) {
                response = new ResponseEntity<>(makeMap("Error", "This is not your game"), HttpStatus.UNAUTHORIZED);
            } else {
                response = new ResponseEntity<>(this.makeGameViewDTO(gp), HttpStatus.OK);
            }
        }
        return response;
    }

    //Este @RequestMapping del tipo POST sirve para darle acceso a los usuarios a la base de datos (y, de esa forma, puedan crear sus usuarios)
    //Este método devuelve ResponseEntity que no son otra cosa que objetos que muestran un código de error o de éxito en el front
    //cuando el usuario quiere crear su propio usuario
    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String firstname, @RequestParam String lastname,
                                                            @RequestParam String username, @RequestParam String password) {
        ResponseEntity<Map<String, Object>> response;
        Player player = playerRepository.findByUserNameIgnoreCase(username);
        if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            response = new ResponseEntity<>(makeMap("Error", "You must complete all fields"), HttpStatus.FORBIDDEN);
        } else if (player != null) {
            response = new ResponseEntity<>(makeMap("Error", "Username already exists"), HttpStatus.FORBIDDEN);
        } else {
            Player newPlayer = playerRepository.save(new Player(firstname, lastname, username, passwordEncoder.encode(password)));
            response = new ResponseEntity<>(makeMap("Id", newPlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    //Este método es equivalente a hacer un @RequestMapping del tipo post como el de arriba
    //Sirve para que un usuario logueado pueda crear un game que deje una vacante para que se una otro player
    //Pero hay que tener en cuenta que cuando crea un game, crea, a su vez, un gamePlayer. Faltaría entonces que otro jugador (que va a
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("Error", "You must be logged to create a game"), HttpStatus.FORBIDDEN);
        } else {
            Game newGame = new Game();
            Player player = playerRepository.findByUserNameIgnoreCase(authentication.getName());
            GamePlayer newGamePlayer = new GamePlayer(newGame, player);
            gameRepository.save(newGame);
            gamePlayerRepository.save(newGamePlayer);
            response = new ResponseEntity<>(makeMap("GpId", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    //Este método sirve para que un player se pueda joinear a un game que le falte un player para comenzar
    @PostMapping("games/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joingGame(Authentication authentication, @PathVariable Long gameId) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("Error", "You must be logged to join a game"), HttpStatus.FORBIDDEN);
        } else {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                response = new ResponseEntity<>(makeMap("Error", "No such game"), HttpStatus.NOT_FOUND);
            } else if (game.getGamePlayers().size() > 1) {
                response = new ResponseEntity<>(makeMap("Error", "The game is full"), HttpStatus.FORBIDDEN);
            } else {
                Player player = playerRepository.findByUserNameIgnoreCase(authentication.getName());
                if (game.getGamePlayers().stream().anyMatch(gp -> gp.getPlayer().getId() == player.getId())) {
                    response = new ResponseEntity<>(makeMap("Error", "You can´t play against yourself"), HttpStatus.FORBIDDEN);
                } else {
                    GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(game, player));
                    response = new ResponseEntity<>(makeMap("GpId", newGamePlayer.getId()), HttpStatus.CREATED);
                }
            }
        }
        return response;
    }


    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShips(Authentication authentication, @PathVariable Long gamePlayerId, @RequestBody List<Ship> ships){
        ResponseEntity<Map<String, Object>> response;
        if(isGuest(authentication)){
            response = new ResponseEntity<>(makeMap("Error", "You must be logged first"), HttpStatus.UNAUTHORIZED);
        }else{
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserNameIgnoreCase(authentication.getName());
            if(gamePlayer == null){
                response = new ResponseEntity<>(makeMap("Error", "No such game"), HttpStatus.NOT_FOUND);
            }else if(gamePlayer.getPlayer().getId() != player.getId()){
                response = new ResponseEntity<>(makeMap("Error", "This is not your game"), HttpStatus.UNAUTHORIZED);
            }else if(gamePlayer.getShips().size() > 0){
                response = new ResponseEntity<>(makeMap("Error", "You already have ships"), HttpStatus.FORBIDDEN);
            }else if(ships == null || ships.size() !=5){
                response = new ResponseEntity<>(makeMap("Error", "You must add five ships"), HttpStatus.FORBIDDEN);
            }else{
                if(ships.stream().anyMatch(ship -> this.isOutOfRange(ship))){
                    response = new ResponseEntity<>(makeMap("Error", "You have ships out of range"), HttpStatus.FORBIDDEN);
                }else if(ships.stream().anyMatch(ship -> this.isNotConsecutive(ship))){
                    response = new ResponseEntity<>(makeMap("Error", "Your ships are not consecutive"), HttpStatus.FORBIDDEN);
                }else if(this.areOverlapped(ships)){
                    response = new ResponseEntity<>(makeMap("Error", "Your ships are overlapped"), HttpStatus.FORBIDDEN);
                }else{
                    ships.forEach(ship -> gamePlayer.addShip(ship));

                    gamePlayerRepository.save(gamePlayer);

                    response = new ResponseEntity<>(makeMap("Success", "Ships added"), HttpStatus.CREATED);
                }
            }
        }
        return response;
    }


    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> addSalvo(Authentication authentication, @PathVariable Long gamePlayerId, @RequestBody List<String> shots){
        ResponseEntity<Map<String, Object>> response;
        if(isGuest(authentication)){
            response = new ResponseEntity<>(makeMap("Error", "You must be logged first"), HttpStatus.UNAUTHORIZED);
        }else{
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserNameIgnoreCase(authentication.getName());
            if(gamePlayer == null){
                response = new ResponseEntity<>(makeMap("Error", "No such game"), HttpStatus.NOT_FOUND);
            }else if(gamePlayer.getPlayer().getId() != player.getId()) {
                response = new ResponseEntity<>(makeMap("Error", "This is not your game"), HttpStatus.UNAUTHORIZED);
            }else if(shots.size() != 5){
                response = new ResponseEntity<>(makeMap("Error", "Wrong number of shots"), HttpStatus.FORBIDDEN);
            }else{
                int turn = gamePlayer.getSalvoes().size() + 1;

                Salvo salvo = new Salvo(turn, shots, gamePlayer);
                gamePlayer.addSalvo(salvo);

                gamePlayerRepository.save(gamePlayer);

                response = new ResponseEntity<>(makeMap("Success", "Salvo added"), HttpStatus.CREATED);
            }
        }
        return response;
    }




    //Esta función isGuest la uso en todos los Mapping anteriores porque me sirve para saber si el usuario que está haciendo la petición a tal o cual url
    //está logueado o no (guest = invitado)
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    //Esta función la uso en todos los Mapping anteriores que devuelven una ResponseEntity<Map<String, Object>> porque justamente lo que hace es
    //armar la estructura de ese Map<String, Object> que luego se va a rellenar con los distintos tipos de respuesta posibles
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------

    //FUNCIONES NECESARIAS PARA HACER EL @POSTMAPPING DE addShips
    //Esta función anyMatch lo que hace es chequear que cada Ship esté posicionada dentro de un rango correcto (de A1 a J10) antes de agregarla a la grilla
    private boolean isOutOfRange(Ship ship) {
        for (String cell : ship.getShipPositions()) {
            if (!(cell instanceof String) || cell.length() < 2) {
                return true;
            }
            char y = cell.substring(0, 1).charAt(0);
            Integer x;
            try {
                x = Integer.parseInt(cell.substring(1));
            } catch (NumberFormatException e) {
                x = 99;
            }
            ;
            if (x < 1 || x > 10 || y < 'A' || y > 'J') {
                return true;
            }
        }
        return false;
    }

    //Esta función se encarga de corroborar que las posiciones que se le quieren asignar a la Ship sean o no consecutivas
    private boolean isNotConsecutive(Ship ship) {

        List<String> cells = ship.getShipPositions();

        boolean isVertical = cells.get(0).charAt(0) != cells.get(1).charAt(0);

        for (int i=0 ; i < cells.size(); i++) {
            if (i < cells.size() - 1) {
                if(isVertical){
                    char yChar = cells.get(i).substring(0, 1).charAt(0);
                    char compareChar = cells.get(i + 1).substring(0, 1).charAt(0);
                    if (compareChar - yChar != 1) {
                        return true;
                    }
                } else {
                        Integer xInt = Integer.parseInt(cells.get(i).substring(1));
                        Integer compareInt = Integer.parseInt(cells.get(i + 1).substring(1));
                        if (compareInt - xInt != 1) {
                            return true;
                        }
                    }
            }
            for (int j = i + 1; j < cells.size(); j++) {
                if (isVertical) {
                    if (!cells.get(i).substring(1).equals(cells.get(j).substring(1))) {
                        return true;
                    }
                } else {
                    if (!cells.get(i).substring(0, 1).equals(cells.get(j).substring(0, 1))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Esta función lo que hace es corroborar que las posiciones que se le quiere dar a las distintas ships se superponen o no
    private boolean areOverlapped(List<Ship> ships){

        List<String> allCells = new ArrayList<>();

        ships.forEach(ship -> allCells.addAll(ship.getShipPositions()));

        for(int i=0; i<allCells.size(); i++){
            for(int j = i + 1; j<allCells.size(); j++){
                if(allCells.get(i).equals(allCells.get(j))){
                    return true;
                }
            }
        }
        return false;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------




    //FUNCIONES NECESARIAS PARA HACER LOS DTO DE @GetMapping("/games") ---------------------------------------------------------------------
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




    //FUNCIONES NECESARIAS PARA HACER LOS DTO DE @GetMapping("/game_view/{gamePlayerId}") ---------------------------------------------------------------------
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













}

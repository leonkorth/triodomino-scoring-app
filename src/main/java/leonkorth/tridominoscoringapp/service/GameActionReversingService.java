package leonkorth.tridominoscoringapp.service;


import leonkorth.tridominoscoringapp.model.Player;
import leonkorth.tridominoscoringapp.model.PlayerAction;
import leonkorth.tridominoscoringapp.model.PlayerDraw;
import leonkorth.tridominoscoringapp.model.PlayerMove;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameActionReversingService {

    GameService gameService;

    public GameActionReversingService(GameService gameService) {
        this.gameService = gameService;
    }

    private List<PlayerAction> allActions = new ArrayList<>();

    public GameActionReversingService addAction(PlayerAction playerAction){
        allActions.add(playerAction);
        return this;
    }

    public GameActionReversingService reverseLastAction() {

        if(allActions.isEmpty()) return this;

        int lastActionIndex = allActions.size() - 1;

        PlayerAction lastAction = allActions.get(lastActionIndex);

        String playerName = lastAction.getPlayerName();
        int number = lastAction.getNumber();

        allActions.remove(lastActionIndex);

        Player player = gameService.getAllPlayers().stream().filter(x -> x.getName().equals(playerName)).findAny().orElse(null);

        if(lastAction.getClass().equals(PlayerMove.class)){

            Map<Player, Integer> totalPoints = gameService.getPlayerAndPoints(ListType.TOTAL);

            if(totalPoints.isEmpty()) return this;

            totalPoints.put(player, totalPoints.get(player) - number);
            gameService.setAllPlayersTotalPoints(totalPoints);


            Map<Player, List<Integer>> allPointsAllPlayers = new  LinkedHashMap<>(gameService.getAllPlayersAllPoints(GameService.SortType.NORMAL));
            List<Integer> allPointsForLastPlayer = gameService.getAllPlayersAllPoints(GameService.SortType.NORMAL).get(player);

            if(allPointsForLastPlayer.isEmpty()) return this;

            allPointsForLastPlayer.remove(allPointsForLastPlayer.size() -1);

            allPointsAllPlayers.put(player, allPointsForLastPlayer);

            gameService.setAllPlayersAllPoints(allPointsAllPlayers);

        }
        else if(lastAction.getClass().equals(PlayerDraw.class)){

                Map<Player, List<Integer>> allPlayersSpecialPoints =  new LinkedHashMap<>(gameService.getAllPlayersSpecialPoints());
                List<Integer> lastPlayerSpecialPoints = allPlayersSpecialPoints.get(player);
                lastPlayerSpecialPoints.set(0, lastPlayerSpecialPoints.get(0) - number);

                gameService.setAllPlayersSpecialPoints(allPlayersSpecialPoints);

        }
        return this;
    }

    public GameActionReversingService reverseAllActionsAllPlayers(){

        allActions.clear();

        List<Player> allPlayers = new ArrayList<>(gameService.getAllPlayers());
        allPlayers.clear();

        Map<Player, List<Integer>> allPlayersAllPoints = new LinkedHashMap<>(gameService.getAllPlayersAllPoints(GameService.SortType.NORMAL));
        allPlayersAllPoints.clear();

        Map<Player, Integer> allPlayersTotalPoints = new LinkedHashMap<>(gameService.getPlayerAndPoints(ListType.TOTAL));
        allPlayersTotalPoints.clear();

        Map<Player, List<Integer>> allPlayersSpecialPoints = new LinkedHashMap<>(gameService.getAllPlayersSpecialPoints());
        allPlayersSpecialPoints.clear();

        gameService
                .setLastPlayer(null)
                .setAllPlayers(allPlayers)
                .setAllPlayersAllPoints(allPlayersAllPoints)
                .setAllPlayersTotalPoints(allPlayersTotalPoints)
                .setAllPlayersSpecialPoints(allPlayersSpecialPoints);
        return this;
    };

    public List<PlayerAction> getAllActions() {
        return this.allActions;
    }
}

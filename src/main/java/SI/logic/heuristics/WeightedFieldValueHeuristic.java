package SI.logic.heuristics;

import SI.enums.Color;
import SI.logic.game.GameInterface;
import SI.models.GameModel;

import java.util.Set;

public class WeightedFieldValueHeuristic extends GameHeuristic {

    private double num_of_neigh_weight;
    private double ally_field_weight;
    private double empty_field_weight;

    public WeightedFieldValueHeuristic(double neigh_weight, double ally_weight, double empty_weight) {
        this.num_of_neigh_weight = neigh_weight;
        this.ally_field_weight = ally_weight;
        this.empty_field_weight = empty_weight;
    }

    @Override
    public double getResultCoefficient(GameInterface game) {
        return getFieldsWeight(game, Color.WHITE) - getFieldsWeight(game, Color.BLACK);
    }

    private double getFieldsWeight(GameInterface game, Color playerColor) {
        GameModel model = game.getGameModel();
        Set<String> playerFields = model.getFields(playerColor);
        Set<String> neighbours;
        double result = 0.0;

        for(String field : playerFields) {
            neighbours = model.getNeighbours(field);
            result += num_of_neigh_weight * neighbours.size();

            for(String neighbour : neighbours) {
                if(model.getFieldColor(neighbour).equals(playerColor))
                    result += ally_field_weight;
                else if(model.getFieldColor(neighbour).equals(Color.NONE))
                    result += empty_field_weight;
            }
        }

        return result;
    }
}

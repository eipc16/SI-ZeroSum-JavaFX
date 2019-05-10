package SI.enums;

public enum GamePhase {
    PLACING,    //placing mens on the board
    MOVING,     //moving mens across the board
    FLYING,     //active when player has only 3 mens left
    REMOVING,   //state active when one of the players
                //gets to choose which one of the opponent's mens
                //to remove
    FINISHED    //game finished
}

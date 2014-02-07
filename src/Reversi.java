import java.io.ObjectInputStream.GetField;
import java.util.Arrays;
import java.util.Scanner;

public class Reversi {
	public StringBuilder printer;
	public int rowLength = 20;
	public int[] board = new int[100];
	private int nextTurnCounter = 0;
	public static final int OUTSIDE 			= -1;
	public static final int EMPTY 			= 0;
	public static final int PLAYER_YOU		= 1;
	public static final int PLAYER_OPPONENT 	= 2;
	
	public static final int DIR_UPLEFT 	= -11;
	public static final int DIR_UP 		= -10;
	public static final int DIR_UPRIGHT 	= -9;
	public static final int DIR_LEFT 		= -1;
	public static final int DIR_RIGHT 	= 1;
	public static final int DIR_DOWNLEFT = 9;
	public static final int DIR_DOWN 		= 10;
	public static final int DIR_DOWNRIGHT = 11;
	
	private static final int MAX_DEPTH = 4;
	
	public Reversi(){
		for(int i = 0; i<board.length; i++){
			if(i <= 10 || i % 10 == 0 || i % 10 == 9 || i >= 90){
				board[i] = -1;
			}else{
				board[i] = 0;
			}
		}
		set(4,4,2);
		set(5,4,1);
		set(4,5,1);
		set(5,5,2);
		
		printer = new StringBuilder(
				"#-A-B-C-D-E-F-G-H-#\n"
			+	"1                 1\n"
			+	"2                 2\n"
			+	"3                 3\n"
			+	"4                 4\n"
			+	"5                 5\n"
			+	"6                 6\n"
			+	"7                 7\n"
			+	"8                 8\n"
			+	"#-A-B-C-D-E-F-G-H-#");
	}
	
	public void yourMove(){
		System.out.print("Your turn, legal moves: ");
		int[] moves = getValidMoves(PLAYER_YOU);
		if(moves.length == 0){
			nextTurnCounter++;
			return;
		}
		nextTurnCounter = 0;
		for(int move : moves){
			printMove(move);
			System.out.print(", ");
		}
		/*Scanner scan = new Scanner(System.in);
		String move = scan.nextLine();
		int col = (int) move.toUpperCase().charAt(0) - 'A' + 1;
		int row = (int) Integer.parseInt(move.substring(1,2));
		System.out.println("col: "+col+", row: "+row);
		*/
		if(move(moves[moves.length - 1], PLAYER_YOU)){
			
		}else{
			System.out.println("Illegal move");
			yourMove();
		}
	}
	
	public void opponentMove(){
		/*try {
			//Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		int move = getBestMove();
		if(move == -1){
			nextTurnCounter++;
			return;
		}
		nextTurnCounter = 0;
		
		move(move, PLAYER_OPPONENT, false, board);
		System.out.print("Opponent choses ");
		printMove(move);
		System.out.println();
	}
	
	public int getBestMove(){
		int[] moves = getValidMoves(PLAYER_OPPONENT);
		int bestChoice = Integer.MIN_VALUE;
		int bestMove = -1;
		
		for(int move : moves){
			int[] copiedBoard = new int[board.length];
			System.arraycopy(board, 0, copiedBoard, 0, board.length );
			move(move, PLAYER_OPPONENT, false, copiedBoard);
			int value = minimax(copiedBoard, PLAYER_YOU, 2, Integer.MAX_VALUE);
			if(value > bestChoice){ //max
				bestChoice = value;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	public int minimax(int[] currentBoard, int player, int depth, int alphaBeta){
		if(hasEnded()){
			return evaluate(currentBoard);
		}
		if(depth == MAX_DEPTH){
			return evaluate(currentBoard);
		}
		
		int[] moves = getValidMoves(player);
		
		int bestChoice;
		int nextPlayer;
		if(player == PLAYER_OPPONENT){
			nextPlayer = PLAYER_YOU;
			bestChoice = Integer.MIN_VALUE;
		}else{
			nextPlayer = PLAYER_OPPONENT;
			bestChoice = Integer.MAX_VALUE;
		}
		
		for(int move : moves){
			int[] copiedBoard = new int[currentBoard.length];
			System.arraycopy(currentBoard, 0, copiedBoard, 0, currentBoard.length );
			move(move, player, false, copiedBoard);
			int value = minimax(copiedBoard, nextPlayer, depth+1, alphaBeta);
			if(player == PLAYER_YOU && alphaBeta < value){
				break;
			}else if(player == PLAYER_OPPONENT && alphaBeta > value){
				break;
			}
			if(value > bestChoice && player == PLAYER_OPPONENT){ //max
				bestChoice = value;
				alphaBeta = value;
			}else if(value < bestChoice){ //min
				bestChoice = value;
				alphaBeta = value;
			}
			print(currentBoard);
			move = move;
		}
		return bestChoice;
	}
	
	public int evaluate(int[] board){
		return getScore() * -1;
	}
	
	public int[] getPoints(){
		int you = 0;
		int ai = 0;
		for(int i=11; i<89; i++){
			if(board[i] == PLAYER_OPPONENT){
				ai++;
			}else if(board[i] == PLAYER_YOU){
				you++;
			}
		}
		return new int[]{you, ai};
	}
	
	public int getScore(){ // player points minus opponent points
		int[] points = getPoints();
		return points[0] - points[1];
	}
	
	public boolean hasEnded(){
		if(nextTurnCounter >= 2){
			return true;
		}
		for(int i=0; i<100; i++){
			if(board[i] == EMPTY){
				return false;
			}
		}
		return true;
	}
	
	public int[] getValidMoves(int player){
		int[] validMoves1 = new int[100];
		int length = 0;
		for(int i=0; i<board.length; i++){
			if(isValidMove(i, player)){
				validMoves1[length] = i;
				++length;
			}
		}
		int[] validMoves2 = new int[length];
		for(int i=0; i<length; i++){
			validMoves2[i] = validMoves1[i];
		}
		return validMoves2;
	}
	
	public boolean isValidMove(int move, int player){
		if(board[move] != EMPTY){
			return false;
		}
		if(isValidDirection(move, DIR_UPLEFT, player)) return true;
		if(isValidDirection(move, DIR_UP, player)) return true;
		if(isValidDirection(move, DIR_UPRIGHT, player)) return true;
		if(isValidDirection(move, DIR_LEFT, player)) return true;
		if(isValidDirection(move, DIR_RIGHT, player)) return true;
		if(isValidDirection(move, DIR_DOWNLEFT, player)) return true;
		if(isValidDirection(move, DIR_DOWN, player)) return true;
		if(isValidDirection(move, DIR_DOWNRIGHT, player)) return true;
		return false;
	}
	
	public boolean isValidDirection(int pos, int dir, int player){
		return isValidDirection(pos, dir, player, true);
	}
	
	public boolean isValidDirection(int pos, int dir, int player, boolean first){
		int newPos = pos + dir;
		switch(board[newPos]){
		case PLAYER_OPPONENT: 
			if(player == PLAYER_YOU){
				return isValidDirection(newPos, dir, player, false);
			}else if(player == PLAYER_OPPONENT && !first){
				return true;
			}
			break;
		case PLAYER_YOU: 
			if(player == PLAYER_OPPONENT){
				return isValidDirection(newPos, dir, player, false);
			}else if(player == PLAYER_YOU && !first){
				return true;
			}
			break;
		}
		return false;
	}
	
	
	
	public int[] getNeighbours(int position){
		int[] result = new int[8];
		result[0] = board[position-9];
		result[1] = board[position-8];
		result[2] = board[position-7];
		result[3] = board[position-1];
		result[4] = board[position+1];
		result[5] = board[position+7];
		result[6] = board[position+8];
		result[7] = board[position+9];
		return result;
	}
	
	public void set(int move, int player){
		board[move] = player;
	}
	
	public void set(int row, int col, int player){
		set(row * 10 + col, player);
	}
	
	
	public boolean move(int row, int col, int player){
		return move(row * 10 + col, player);
	}
	public boolean move(int move, int player){
		return move(move, player, true, board);
	}
	public boolean move(int move, int player, boolean check, int[] board){
		if(!check || isValidMove(move, player)){
			if(isValidDirection(move, DIR_UPLEFT, player)) flip(move, DIR_UPLEFT, player, board);
			if(isValidDirection(move, DIR_UP, player)) flip(move, DIR_UP, player, board);
			if(isValidDirection(move, DIR_UPRIGHT, player)) flip(move, DIR_UPRIGHT, player, board);
			if(isValidDirection(move, DIR_LEFT, player)) flip(move, DIR_LEFT, player, board);
			if(isValidDirection(move, DIR_RIGHT, player)) flip(move, DIR_RIGHT, player, board);
			if(isValidDirection(move, DIR_DOWNLEFT, player)) flip(move, DIR_DOWNLEFT, player, board);
			if(isValidDirection(move, DIR_DOWN, player)) flip(move, DIR_DOWN, player, board);
			if(isValidDirection(move, DIR_DOWNRIGHT, player)) flip(move, DIR_DOWNRIGHT, player, board);
			board[move] = player;
			return true;
		}else{
			return false;
		}
	}
	
	
	public void flip(int pos, int dir, int player, int[] board){
		int newPos = pos + dir;
		switch(board[newPos]){
		case PLAYER_OPPONENT: 
			if(player == PLAYER_YOU){
				board[newPos] = player;
				flip(newPos, dir, player, board);
			}
			break;
		case PLAYER_YOU: 
			if(player == PLAYER_OPPONENT){
				board[newPos] = player;
				flip(newPos, dir, player, board);
			}
			break;
		}
	}

	public void print(){
		print(board);
	}
	
	public void print(int[] board){
		// TODO: Dont do this each time
		//printer = new StringBuilder();
		//System.out.println(board);
		for(int i=0; i< board.length; i++){
			int row = (i) / 10;
			int col = (i) % 10;
			
			switch(board[i]){
			case 0: break;
			case 1: printer.setCharAt(rowLength*row + col*2, 'O'); break;
			case 2: printer.setCharAt(rowLength*row + col*2, 'X'); break; 
			}
		}
		System.out.println(printer);
	}
	public void printMove(int move){
		int row = move / 10;
		char col = (char) ((move % 10) + 'A' -1);
		System.out.print(""+col+row);
	}
	
	public static void main(String[] args){
		Reversi game = new Reversi();
		game.print();
		while(true){
			game.yourMove();
			game.print();
			
			if(game.hasEnded()){
				break;
			}
			game.opponentMove();
			game.print();
			if(game.hasEnded()){
				break;
			}
		}
		System.out.println(game.getWinnerText());
	}

	private String getWinnerText() {
		int[] points = getPoints();
		if(!hasEnded()){
			return "Game has not ended";
		}else if(getScore() > 0){
			return "You won with score: " + points[0] + " - AI score: " + points[1] ;
		}else if(getScore() < 0){
			return "AI won with score: " + points[1] +" - player score: " + points[0];
		}else{
			return "It's a draw";
		}
	}
}

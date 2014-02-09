import java.util.Arrays;
import java.util.Scanner;

public class Reversi {
	public StringBuilder printer;
	public int rowLength = 20;
	public int[] board = new int[100];
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

	private int maxDepth = 9;
	private int maxTime = 2000;
	
	public Reversi(int maxDepth, int maxTime){
		this.maxDepth = maxDepth;
		this.maxTime = maxTime;
		for(int i = 0; i<board.length; i++){
			if(i <= 10 || i % 10 == 0 || i % 10 == 9 || i >= 90){
				board[i] = -1;
			}else{
				board[i] = 0;
			}
		}
		set(4,4,2, board);
		set(5,4,1, board);
		set(4,5,1, board);
		set(5,5,2, board);
		
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
		int[] moves = getValidMoves(PLAYER_YOU, board);
		if(moves.length == 0){
			return;
		}
		for(int move : moves){
			printMove(move);
			System.out.print(", ");
		}
		System.out.println();
		Scanner scan = new Scanner(System.in);
		try{
			String move = scan.nextLine();
			int col = (int) move.toUpperCase().charAt(0) - 'A' + 1;
			int row = (int) Integer.parseInt(move.substring(1,2));
			
			//int randMove = (int)(Math.random() * moves.length);
			
			//if(move(moves[randMove], PLAYER_YOU, board)){
			if(move(row, col, PLAYER_YOU, board)){
			
			}else{
				System.out.println("Illegal move");
				yourMove();
			}
		}catch(Exception e){
			System.out.println("Invalid input");
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
		log("Opponents turn");
		int move = getBestMove();
		if(move == -1){
			return;
		}
		
		move(move, PLAYER_OPPONENT, false, board);
		System.out.print("Opponent choses ");
		printMove(move);
		System.out.println();
	}
	
	public int getBestMove(){
		long time = System.currentTimeMillis();
		int[] moves = getValidMoves(PLAYER_OPPONENT, board);
		int bestChoice = Integer.MIN_VALUE;
		int bestMove = -1;
		
		log("AI moves: "+Arrays.toString(moves));
		
		for(int move : moves){
			int[] copiedBoard = new int[board.length];
			System.arraycopy(board, 0, copiedBoard, 0, board.length );
			move(move, PLAYER_OPPONENT, false, copiedBoard);
			int value = minimax(copiedBoard, PLAYER_YOU, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, time);
			if(value > bestChoice){ //max
				bestChoice = value;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	public void log(String str){
		//System.out.println(str);
	}
	
	public int minimax(int[] currentBoard, int player, int depth, int alpha, int beta, long startTime){
		log("Depth "+depth+".");
		if(hasEnded(currentBoard)){
			int value = evaluate(currentBoard);
			log("Game ended. Value: "+value);
			return value;
		}
		if(depth == maxDepth){
			int value = evaluate(currentBoard);
			log("Reached max depth. Value: "+value);
			//print(currentBoard);
			return value;
		}
		if(System.currentTimeMillis() > startTime + maxTime){
			int value = evaluate(currentBoard);
			log("Time's up");
			return value;
		}
		
		int nextPlayer;
		if(player == PLAYER_OPPONENT){
			nextPlayer = PLAYER_YOU;
		}else{
			nextPlayer = PLAYER_OPPONENT;
		}
		
		int[] moves = getValidMoves(player, currentBoard);
		if(moves.length == 0){
			return minimax(currentBoard, nextPlayer, depth+1, alpha, beta, startTime);
		}
		
		log("Valid moves: "+Arrays.toString(moves));
		
		for(int move : moves){
			log("Examining move: "+move);
			int[] copiedBoard = new int[currentBoard.length];
			System.arraycopy(currentBoard, 0, copiedBoard, 0, currentBoard.length );
			move(move, player, false, copiedBoard);
			
			int value;
			if(player == PLAYER_OPPONENT){ // wants to maximize
				alpha = Math.max(alpha, minimax(copiedBoard, nextPlayer, depth+1, alpha, beta, startTime));
				if(beta <= alpha){
					//System.out.println("Maximize PRUNING! alpha: "+alpha+", beta: "+beta);
					break;
				}
			}else{ // wants to minimize
				beta = Math.min(beta, minimax(copiedBoard, nextPlayer, depth+1, alpha, beta, startTime));
				if(beta <= alpha){
					//System.out.println("Minimize PRUNING! alpha: "+alpha+", beta: "+beta);
					break;
				}
			}
		}
		if(player == PLAYER_OPPONENT){
			return alpha;
		}else{
			return beta;
		}
	}
	
	public int evaluate(int[] board){
		return getScore(board) * -1;
	}
	
	public int[] getPoints(int[] board){
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
	
	public int getScore(int[] board){ // player points minus opponent points
		int[] points = getPoints(board);
		return points[0] - points[1];
	}
	
	public boolean hasEnded(int[] board){
		return (getValidMoves(PLAYER_YOU, board).length == 0 && getValidMoves(PLAYER_OPPONENT, board).length == 0);
		
		/*if(nextTurnCounter >= 2){
			return true;
		}
		for(int i=0; i<100; i++){
			if(board[i] == EMPTY){
				return false;
			}
		}
		return true;*/
	}
	
	public int[] getValidMoves(int player, int[] board){
		int[] validMoves1 = new int[100];
		int length = 0;
		for(int i=0; i<board.length; i++){
			if(isValidMove(i, player, board)){
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
	
	public boolean isValidMove(int move, int player, int[] board){
		if(board[move] != EMPTY){
			return false;
		}
		if(isValidDirection(move, DIR_UPLEFT, player, board)) return true;
		if(isValidDirection(move, DIR_UP, player, board)) return true;
		if(isValidDirection(move, DIR_UPRIGHT, player, board)) return true;
		if(isValidDirection(move, DIR_LEFT, player, board)) return true;
		if(isValidDirection(move, DIR_RIGHT, player, board)) return true;
		if(isValidDirection(move, DIR_DOWNLEFT, player, board)) return true;
		if(isValidDirection(move, DIR_DOWN, player, board)) return true;
		if(isValidDirection(move, DIR_DOWNRIGHT, player, board)) return true;
		return false;
	}
	
	public boolean isValidDirection(int pos, int dir, int player, int[] board){
		return isValidDirection(pos, dir, player, true, board);
	}
	
	public boolean isValidDirection(int pos, int dir, int player, boolean first, int[] board){
		int newPos = pos + dir;
		switch(board[newPos]){
		case PLAYER_OPPONENT: 
			if(player == PLAYER_YOU){
				return isValidDirection(newPos, dir, player, false, board);
			}else if(player == PLAYER_OPPONENT && !first){
				return true;
			}
			break;
		case PLAYER_YOU: 
			if(player == PLAYER_OPPONENT){
				return isValidDirection(newPos, dir, player, false, board);
			}else if(player == PLAYER_YOU && !first){
				return true;
			}
			break;
		}
		return false;
	}
	
	
	
	public int[] getNeighbours(int position, int[] board){
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
	
	public void set(int move, int player, int[] board){
		board[move] = player;
	}
	
	public void set(int row, int col, int player, int board[]){
		set(row * 10 + col, player, board);
	}
	
	
	public boolean move(int row, int col, int player, int[] board){
		return move(row * 10 + col, player, board);
	}
	public boolean move(int move, int player, int[] board){
		return move(move, player, true, board);
	}
	public boolean move(int move, int player, boolean check, int[] board){
		if(!check || isValidMove(move, player, board)){
			if(isValidDirection(move, DIR_UPLEFT, player, board)) flip(move, DIR_UPLEFT, player, board);
			if(isValidDirection(move, DIR_UP, player, board)) flip(move, DIR_UP, player, board);
			if(isValidDirection(move, DIR_UPRIGHT, player, board)) flip(move, DIR_UPRIGHT, player, board);
			if(isValidDirection(move, DIR_LEFT, player, board)) flip(move, DIR_LEFT, player, board);
			if(isValidDirection(move, DIR_RIGHT, player, board)) flip(move, DIR_RIGHT, player, board);
			if(isValidDirection(move, DIR_DOWNLEFT, player, board)) flip(move, DIR_DOWNLEFT, player, board);
			if(isValidDirection(move, DIR_DOWN, player, board)) flip(move, DIR_DOWN, player, board);
			if(isValidDirection(move, DIR_DOWNRIGHT, player, board)) flip(move, DIR_DOWNRIGHT, player, board);
			board[move] = player;
			//print(board, ((player == PLAYER_YOU) ? "You" : "AI")+" chose "+move);
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
		print(board, "");
	}

	public void print(int[] board, String comment){
		if(!Arrays.equals(this.board, board)){
			return;
		}
		
		// TODO: Dont do this each time
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
		System.out.print(printer);
		System.out.println(" "+comment);
	}
	public void printMove(int move){
		int row = move / 10;
		char col = (char) ((move % 10) + 'A' -1);
		System.out.print(""+col+row);
	}
	
	private String getWinnerText() {
		int[] points = getPoints(board);
		if(!hasEnded(board)){
			return "Game has not ended";
		}else if(getScore(board) > 0){
			return "You won with score: " + points[0] + " - AI score: " + points[1] ;
		}else if(getScore(board) < 0){
			return "AI won with score: " + points[1] +" - player score: " + points[0];
		}else{
			return "It's a draw";
		}
	}
	
	public static void main(String[] args){
		Reversi game;
		try{
			int maxDepth = 8;
			int maxTime = 2000;
			
			if(args.length >= 1){
				maxDepth = Integer.parseInt(args[0]);
			}
			if(args.length >= 2){
				maxTime = Integer.parseInt(args[1]);
			}
			game = new Reversi(maxDepth, maxTime);
		}catch(Exception e){
			System.out.println("Syntax error");
			return;
		}
		game.print();
		while(true){
			game.yourMove();
			game.print();
			
			if(game.hasEnded(game.board)){
				break;
			}
			game.opponentMove();
			game.print();
			if(game.hasEnded(game.board)){
				break;
			}
		}
		System.out.println(game.getWinnerText());
	}
}

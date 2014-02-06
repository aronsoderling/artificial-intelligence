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
		Scanner scan = new Scanner(System.in);
		String move = scan.nextLine();
		int col = (int) move.toUpperCase().charAt(0) - 'A' + 1;
		int row = (int) Integer.parseInt(move.substring(1,2));
		System.out.println("col: "+col+", row: "+row);
		if(move(row, col, PLAYER_YOU)){
			
		}else{
			System.out.println("Illegal move");
			yourMove();
		}
	}
	
	public void opponentMove(){
		Scanner scan = new Scanner(System.in);
		String move = scan.nextLine();
		int col = (int) move.toUpperCase().charAt(0) - 'A' + 1;
		int row = (int) Integer.parseInt(move.substring(1,2));
		System.out.println("col: "+col+", row: "+row);
		if(move(row, col, PLAYER_OPPONENT)){
			
		}else{
			System.out.println("Illegal move");
			yourMove();
		}
	}
	
	public void hasEnded(){
		
	}
	
	public int[] getValidMoves(){
		return new int[0];
	}
	
	public boolean isValidMove(int move, int player){
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
		case PLAYER_YOU: 
			if(player == PLAYER_OPPONENT){
				return isValidDirection(newPos, dir, player, false);
			}else if(player == PLAYER_YOU && !first){
				return true;
			}
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

	public void set(int row, int col, int player){
		set(row * 10 + col, player);
	}
	
	public void set(int move, int player){
		board[move] = player;
	}
	
	public boolean move(int row, int col, int player){
		return move(row * 10 + col, player);
	}
	
	public boolean move(int move, int player){
		if(isValidMove(move, player)){
			if(isValidDirection(move, DIR_UPLEFT, player)) flip(move, DIR_UPLEFT, player);
			if(isValidDirection(move, DIR_UP, player)) flip(move, DIR_UP, player);
			if(isValidDirection(move, DIR_UPRIGHT, player)) flip(move, DIR_UPRIGHT, player);
			if(isValidDirection(move, DIR_LEFT, player)) flip(move, DIR_LEFT, player);
			if(isValidDirection(move, DIR_RIGHT, player)) flip(move, DIR_RIGHT, player);
			if(isValidDirection(move, DIR_DOWNLEFT, player)) flip(move, DIR_DOWNLEFT, player);
			if(isValidDirection(move, DIR_DOWN, player)) flip(move, DIR_DOWN, player);
			if(isValidDirection(move, DIR_DOWNRIGHT, player)) flip(move, DIR_DOWNRIGHT, player);
			board[move] = player;
			return true;
		}else{
			return false;
		}
	}
	
	public void flip(int pos, int dir, int player){
		int newPos = pos + dir;
		switch(board[newPos]){
		case PLAYER_OPPONENT: 
			if(player == PLAYER_YOU){
				board[newPos] = player;
				flip(newPos, dir, player);
			}
		case PLAYER_YOU: 
			if(player == PLAYER_OPPONENT){
				board[newPos] = player;
				flip(newPos, dir, player);
			}
		}
	}
	
	public void print(){
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
	
	public static void main(String[] args){
		Reversi game = new Reversi();
		game.print();
		while(true){
			game.yourMove();
			game.print();
			game.opponentMove();
			game.print();
		}
	}
}

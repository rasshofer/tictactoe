package com.no_magic.tictactoe;

import java.util.*;

import com.no_magic.tictactoe.R;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class MainActivity extends Activity implements OnClickListener {

	private static final int BOARD_SIZE = 3;
	private static final int PLAYER = 1;
	private static final int EMPTY = 0;
	private static final int BOT = -1;
	private static final String[] CHARACTERS = { "X" , "O" };
	private static final Random RANDOM = new Random();;
	private static final String HIGHSCORES = "Highscores";
	private SharedPreferences preferences;
	private int[] tiles = new int[BOARD_SIZE * BOARD_SIZE];
	private int[] board = new int[BOARD_SIZE * BOARD_SIZE];
	private int characterPlayer;
	private int characterBot;
	private TextView labelPlayer;
	private TextView labelBot;
	private TextView result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Initialize activity
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Initialize shared preferences
		this.preferences = getSharedPreferences(HIGHSCORES, Context.MODE_PRIVATE);
		
		// Initialize board
		for (int i = 0; i < this.tiles.length; i++) {
			this.tiles[i] = getResources().getIdentifier("tile" + (i + 1), "id", getPackageName());
			View tile = findViewById(this.tiles[i]);
			tile.setOnClickListener(this);
		}
		
		// Initialize text views
		this.labelPlayer = (TextView) findViewById(R.id.player);
		this.labelBot = (TextView) findViewById(R.id.bot);
		this.result = (TextView) findViewById(R.id.result);

		// Initially reset the game
		this.resetGame();

	}

	@Override
	public void onClick(View view) {
		
		Button tile = (Button) view;		
		
		// Search clicked tile
		for (int i = 0; i < this.tiles.length; i++) {
			if (tile.equals(findViewById(this.tiles[i]))) {
				// Ensure the tile is empty
				if (this.board[i] == EMPTY) {
					// Capture the tile
					this.board[i] = PLAYER;
					this.styleTile(i, R.drawable.tile_player, CHARACTERS[this.characterPlayer]);
					if (!this.checkWinner()) {
						// Now it’s the bot’s turn
						this.ai();
					}
				}
				break;
			}
		}
		
	}
		
	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
		
	// Returns all available tiles
	private ArrayList<Integer> getAvailableTiles() {
		ArrayList<Integer> available = new ArrayList<Integer>();		
		for (int i = 0; i < this.tiles.length; i++) {
			if (this.board[i] == EMPTY) {
				available.add(i);
			}
		}
		return available;
	}
	
	private void ai() {
		
		ArrayList<Integer> available = this.getAvailableTiles();
				
		if (available.size() > 0) {
			
			// Back up current board
			int[] backup = this.board.clone();
			int next = 0;
			
			// Try all available tiles to win
			for (Integer i : available) {
				this.board[i] = BOT;
				if (this.isWinner(BOT)) {
					next = i;
					break;
				}
				this.board = backup.clone();
			}
			
			if (next == 0) {
				// Try all available tiles to prevent the player to win
				for (Integer i : available) {
					this.board[i] = PLAYER;
					if (this.isWinner(PLAYER)) {
						next = i;
						break;
					}
					this.board = backup.clone();
				}
			}
			
			// Restore board to undo the tests above
			this.board = backup.clone();
			
			// Use random tile if there’s no winning tile at the moment
			if (next == 0) {
				next = available.get(RANDOM.nextInt(available.size()));
			}
			
			// Capture the tile
			this.board[next] = BOT;
			this.styleTile(next, R.drawable.tile_bot, CHARACTERS[this.characterBot]);
			this.checkWinner();
			
		}
		
	}
	
	// Checks whether a player has won the game or not
	private boolean isWinner(int player) {
		
		// Cache
		int sum;
		
		// Horizontal (Rows)
		for (int i = 0; i < tiles.length; i += BOARD_SIZE) {
			sum = 0;
			for (int j = 0; j < BOARD_SIZE; j++) {
				sum += this.board[i + j] == player ? 1 : 0;
			}
			if (sum == BOARD_SIZE) {
				return true;
			}
		}
		
		// Vertical (Columns)
		for (int i = 0; i < tiles.length / BOARD_SIZE; i++) {
			sum = 0;
			for (int j = 0; j < BOARD_SIZE; j++) {
				sum += this.board[i + j * BOARD_SIZE] == player ? 1 : 0;
			}
			if (sum == BOARD_SIZE) {
				return true;
			}
		}
				
		// Diagonal (Top Left -> Bottom Right)
		sum = 0;
		for (int i = 0; i < BOARD_SIZE; i++) {
			sum += this.board[(i * BOARD_SIZE) + i] == player ? 1 : 0; 
		}
		if (sum == BOARD_SIZE) {
			return true;
		}
		
		// Diagonal (Bottom Left -> Top Right)
		sum = 0;
		for (int i = BOARD_SIZE; i > 0; i--) {
			sum += this.board[i * (BOARD_SIZE - 1)] == player ? 1 : 0; 
		}
		if (sum == BOARD_SIZE) {
			return true;
		}		
				
		// Everything else
		return false;
		
	}	
	
	// Checks for a winner
	private boolean checkWinner() {
		if (this.getAvailableTiles().size() < 1) {
			this.gameOver(getString(R.string.splash_nobody));
			return true;
		}
		SharedPreferences.Editor editor = this.preferences.edit();
		if (this.isWinner(PLAYER)) {
			editor.putInt("Player", (preferences.getInt("Player", 0) + 1)).commit();
			this.gameOver(getString(R.string.splash_player, this.getMoves(PLAYER)));
			return true;
		}
		if (this.isWinner(BOT)) {
			editor.putInt("Bot", (preferences.getInt("Bot", 0) + 1)).commit();
			this.gameOver(getString(R.string.splash_bot, this.getMoves(BOT)));
			return true;
		}
		return false;
	}
	
	private void gameOver(String text) {
		Bundle bundle = new Bundle();
		Intent intent = new Intent(this, GameOverActivity.class);
		bundle.putString("text", text);
		intent.putExtras(bundle);
		startActivityForResult(intent, Identifiers.GAME_OVER);
	}
	
	// Returns the count of interactions of a certain player
	private int getMoves(int player) {
		int count = 0;
		for (int i = 0; i < this.board.length; i++) {
			count += this.board[i] == player ? 1 : 0;
		}
		return count;
	}
	
	// Resets the game
	private void resetGame() {
	
		// Shuffle X and O
		this.characterPlayer = RANDOM.nextInt(2);
		this.characterBot = 1 - this.characterPlayer;
		
		// Set labels
		this.labelPlayer.setText(CHARACTERS[this.characterPlayer]);
		this.labelBot.setText(CHARACTERS[this.characterBot]);
		
		// Reset tiles
		for (int i = 0; i < this.tiles.length; i++) {
			this.board[i] = 0;
			Button tile = (Button) findViewById(this.tiles[i]);
			tile.setEnabled(true);
			this.styleTile(i, R.drawable.tile, "");
		}
		
		// Updates result
		this.result.setText(preferences.getInt("Player", 0) + ":" + preferences.getInt("Bot", 0));
		
	}
	
	// Styles a tile
	private void styleTile(int index, int drawable, String text) {
		Button tile = (Button) findViewById(this.tiles[index]);
		tile.setBackgroundDrawable(getResources().getDrawable(drawable));
		tile.setText(text);
	}

}

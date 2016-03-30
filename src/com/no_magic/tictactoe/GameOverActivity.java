package com.no_magic.tictactoe;

import com.no_magic.tictactoe.R;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class GameOverActivity extends Activity implements OnClickListener {
	
	private TextView splash;
	private Button new_game;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Initialize activity
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_over);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		// Initialize splash text
		this.splash = (TextView) findViewById(R.id.splash);
		this.splash.setText(bundle.getString("text"));
		
		// Initialize
		this.new_game = (Button) findViewById(R.id.new_game);
		new_game.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View view) {
		this.newGame();
	}

	@Override
	public void onBackPressed() {
		this.newGame();
	}
	
	private void newGame() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivityForResult(intent, Identifiers.NEW_GAME);
	}
	
}

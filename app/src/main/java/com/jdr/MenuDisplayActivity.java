package com.jdr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

/**
 * Created by Seawolf on 22/11/2017.
 */

public class MenuDisplayActivity extends Activity {

    private Button btnNewGame;
    private Button btnContinue;
    final String NEW_GAME =  "NEW_GAME";
    final String LOAD_CHAPTER_ID = "LOAD_CHAPTER_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.jdr.R.layout.main_menu);

        btnNewGame = findViewById(R.id.btnNewGame);
        btnContinue = findViewById(R.id.btnContinue);

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToGame(true);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToGame(false);
            }
        });
    }


    private void switchToGame(boolean newgame){
        Intent intent = new Intent(MenuDisplayActivity.this, MainActivity.class);
        intent.putExtra(NEW_GAME, newgame);
        intent.putExtra(LOAD_CHAPTER_ID, newgame);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

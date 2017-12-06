package com.jdr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by Seawolf on 22/11/2017.
 */

public class MenuDisplayActivity extends Activity {

    private Button btnNewGame;
    private Button btnContinue;
    private Menu menu;
    final String NEW_GAME =  "NEW_GAME";
    final String LOAD_CHAPTER_ID = "LOAD_CHAPTER_ID";
    final String TTS_ACTIVATED = "TTS_ACTIVATED";
    final String STT_ACTIVATED = "STT_ACTIVATED";
    private boolean tts = true;
    private boolean stt = true;

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


    private void switchToGame(boolean newGame){
        Intent intent = new Intent(MenuDisplayActivity.this, MainActivity.class);
        intent.putExtra(NEW_GAME, newGame);
        intent.putExtra(LOAD_CHAPTER_ID, newGame);
        intent.putExtra(TTS_ACTIVATED, tts);
        intent.putExtra(STT_ACTIVATED, stt);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.STT:
                switchSTT();
                return true;
            case R.id.TTS:
                switchTTS();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void switchTTS(){
        MenuItem item = menu.findItem(R.id.TTS);
        this.tts = !this.tts;
        if(tts){
            item.setTitle("Narrateur ON");
        }
        else{
            item.setTitle("Narrateur OFF");
        }
    }

    private void switchSTT(){
        MenuItem item = menu.findItem(R.id.STT);
        this.stt = !this.stt;
        if(stt){
            item.setTitle("Reconnaissance vocale ON");
        }
        else{
            item.setTitle("Reconnaissance vocale OFF");
        }
    }
}

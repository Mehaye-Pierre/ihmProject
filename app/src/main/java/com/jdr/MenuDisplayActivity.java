package com.jdr;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

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
    private TextToSpeech TTS;
    private boolean tts = true;
    private boolean stt = true;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.jdr.R.layout.main_menu);

        btnNewGame = findViewById(R.id.btnNewGame);
        btnContinue = findViewById(R.id.btnContinue);

        TTS=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.FRANCE);
                }
            }
        });


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

        final Handler handlerPrompt = new Handler();
        handlerPrompt.postDelayed(new Runnable() {
            @Override
            public void run() {
                promptSpeechInput();
            }
        }, 500);
    }


    private void switchToGame(boolean newGame){
        Intent intent = new Intent(MenuDisplayActivity.this, MainActivity.class);
        intent.putExtra(NEW_GAME, newGame);
        intent.putExtra(LOAD_CHAPTER_ID, newGame);
        intent.putExtra(TTS_ACTIVATED, tts);
        intent.putExtra(STT_ACTIVATED, stt);
        startActivity(intent);

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(com.jdr.R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String stringResult = result.get(0);
                    Toast.makeText(getApplicationContext(),
                            stringResult,
                            Toast.LENGTH_SHORT).show();
                    if(stringResult.contains("narrateur")){
                        switchTTS();
                    }
                    if(stringResult.contains("reconnaissance")){
                        switchSTT();
                    }
                    else{
                        if (stt)
                            promptSpeechInput();
                    }

                }
                break;
            }

        }
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

    private void readTextInput(final String s){
        if(tts){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TTS.speak(s, TextToSpeech.QUEUE_FLUSH, null,null);
                }
            }, 500);
        }
    }


    private void switchTTS(){
        MenuItem item = menu.findItem(R.id.TTS);
        this.tts = !this.tts;
        if(tts){
            item.setTitle("Narrateur ON");
            readTextInput("Narrateur activé");
        }
        else{
            item.setTitle("Narrateur OFF");
            readTextInput("Narrateur désactivé");
        }
    }

    private void switchSTT(){
        MenuItem item = menu.findItem(R.id.STT);
        this.stt = !this.stt;
        if(stt){
            item.setTitle("Reconnaissance vocale ON");
            readTextInput("Reconnaissance vocale activé");
        }
        else{
            item.setTitle("Reconnaissance vocale OFF");
            readTextInput("Reconnaissance vocale désactivée");
        }
    }
}

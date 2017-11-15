package com.jdr;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.Button;

public class MainActivity extends Activity {

	private TextView txtSpeechInput;
	private EditText textRead;
	private TextToSpeech TTS;
	private ImageButton btnSpeak;
	private Button btnRead;
	private final int REQ_CODE_SPEECH_INPUT = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.jdr.R.layout.activity_main);

		txtSpeechInput = (TextView) findViewById(com.jdr.R.id.txtSpeechInput);
		btnSpeak = (ImageButton) findViewById(com.jdr.R.id.btnSpeak);
		btnRead = (Button) findViewById(com.jdr.R.id.btnRead);
		textRead = (EditText) findViewById(com.jdr.R.id.readText);


		// hide the action bar
		getActionBar().hide();

		btnSpeak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				promptSpeechInput();
			}
		});

		btnRead.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				readTextInput();
			}
		});

		TTS=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					TTS.setLanguage(Locale.FRANCE);
				}
			}
		});

	}

	/**
	 * Read the content of textRead
	 */
	private void readTextInput(){
		String toSpeak = textRead.getText().toString();
		Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
		TTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null,null);
	}


	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(com.jdr.R.string.speech_prompt));
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(com.jdr.R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				txtSpeechInput.setText(result.get(0));
			}
			break;
		}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.jdr.R.menu.main, menu);
		return true;
	}


}

package com.jdr;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.view.View;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends Activity {

	private TextView textRead;
	private TextToSpeech TTS;
	private LinearLayout LL;
	private SharedPreferences sharedPref;
	private Button buttonChoices[];
	private int currentChapter;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	final String NEW_GAME =  "NEW_GAME";
	final String SAVED_CHAPTER = "SAVED_CHAPTER";
	final String TTS_ACTIVATED = "TTS_ACTIVATED";
	final String STT_ACTIVATED = "STT_ACTIVATED";
	protected boolean ttsActivated = true;
	protected boolean sttActivated =true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.jdr.R.layout.activity_main);

		textRead =  findViewById(com.jdr.R.id.readText);
		LL = findViewById(R.id.choiceLayout);
		buttonChoices = new Button[3];


		// hide the action bar
		getActionBar().hide();

		TTS=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					TTS.setLanguage(Locale.FRANCE);
				}
			}
		});
		sharedPref = getPreferences(Context.MODE_PRIVATE);
		Intent intent = getIntent();

		if (intent != null){


			boolean newGame = intent.getBooleanExtra(NEW_GAME,true);
			if (newGame) {
				currentChapter = 0;
				saveGame();
			}
			else {
				currentChapter = getLoadChapter();
			}
			ttsActivated = intent.getBooleanExtra(TTS_ACTIVATED,true);
			sttActivated = intent.getBooleanExtra(STT_ACTIVATED,true);
		}



		goToChapter(currentChapter);

	}

	private void readTextInput(){
		String toSpeak = textRead.getText().toString();
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
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(com.jdr.R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void saveGame(){
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(SAVED_CHAPTER, currentChapter);
		editor.commit();

	}

	private int getLoadChapter(){
		return sharedPref.getInt(SAVED_CHAPTER, 0);
	}

	private void goToChapter(int chapterValue){
		try {
			LL.removeAllViews();
			resetAllButtons();
			InputStream is = getResources().openRawResource(R.raw.livre);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(is);

			Element element=doc.getDocumentElement();
			element.normalize();

			NodeList nList = doc.getElementsByTagName("paragraphe");
			for (int i=0; i<nList.getLength(); i++) {

				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element2 = (Element) node;
					if (chapterValue == Integer.parseInt(getValue("titre", element2).trim())){
						textRead.setText(getValue("contenu", element2));
						NodeList nodeListChoix = element2.getElementsByTagName("choix");

						for(int j = 0; j < nodeListChoix.getLength();j++){
							Node node2 = nodeListChoix.item(j);
							if (node2.getNodeType() == Node.ELEMENT_NODE ) {
								Element element4 = (Element) node2;
								String tmpStr = getValue("cible",element4).trim();
								if (tmpStr.equals("?")){
									addReturnToMenuButton();
								}
								else {
									int tmp = Integer.parseInt(getValue("cible", element4).trim());
									String tmp2 = getValue("contenu", element4);
									addChoiceButton(tmp2, tmp, j);
								}
							}
						}
					}


				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveGame();
		if(ttsActivated){
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					readTextInput();
				}
			}, 800);

		}
		if(sttActivated){
			final Handler handler2 = new Handler();
			handler2.postDelayed(new Runnable() {
				@Override
				public void run() {
					promptSpeechInput();
				}
			}, 800);
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
				String stringResult = result.get(0);
				Toast.makeText(getApplicationContext(),
						stringResult,
						Toast.LENGTH_SHORT).show();
				if(stringResult.contains("un") || stringResult.contains("hein") || stringResult.contains("1")){
					if (buttonChoices[0] != null)
						buttonChoices[0].performClick();
				}
				else if(stringResult.contains("deux") || stringResult.contains("de")|| stringResult.contains("d'eux") || stringResult.contains("2")){
					if (buttonChoices[1] != null)
						buttonChoices[1].performClick();
				}
				else if(stringResult.contains("trois") || stringResult.contains("troie")|| stringResult.contains("toi")|| stringResult.contains("toit") || stringResult.contains("groix") || stringResult.contains("3") || stringResult.contains("droit")){
					if (buttonChoices[2] != null)
						buttonChoices[2].performClick();
				}
				else if(stringResult.contains("menu")){
					Intent intent = new Intent( MainActivity.this, MenuDisplayActivity.class);
					startActivity(intent);
				}
				else{
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
		getMenuInflater().inflate(com.jdr.R.menu.main, menu);
		return true;
	}

	private static String getValue(String tag, Element element) {
		NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = nodeList.item(0);
		return node.getNodeValue();
	}



	private void addChoiceButton(String text, final int target,int index){
		Button test = new Button(this);
		test.setText(text);
		test.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToChapter(target);
			}
		});
		buttonChoices[index] = test;
		LL.addView(test);
	}

	private void addReturnToMenuButton(){
		Button menuBtn = new Button(this);
		menuBtn.setText("Retourner au menu principal");
		menuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TTS.stop();
				Intent intent = new Intent( MainActivity.this, MenuDisplayActivity.class);
				startActivity(intent);
			}
		});
		buttonChoices[0] = menuBtn;
		LL.addView(menuBtn);
	}

	private void resetAllButtons(){
		for (int i = 0; i < 3; i++)
			buttonChoices[i] = null;
	}
}




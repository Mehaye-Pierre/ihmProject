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
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

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
	private SharedPreferences sharedPref;
	private int currentChapter;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	final String NEW_GAME =  "NEW_GAME";
	final String SAVED_CHAPTER = "SAVED_CHAPTER";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.jdr.R.layout.activity_main);

		textRead =  findViewById(com.jdr.R.id.readText);


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
		}
		try {

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
					if (currentChapter == Integer.parseInt(getValue("titre", element2).trim())){
						textRead.setText(getValue("contenu", element2));
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

	private void saveGame(){
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(SAVED_CHAPTER, currentChapter);
		editor.commit();

	}

	private int getLoadChapter(){
		return sharedPref.getInt(SAVED_CHAPTER, 0);
	}

	private void goToChapter(int i){

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








}

/*
 * Copyright 2013 Simon Willeke
 * contact: hamstercount@hotmail.com
 */

/*
    This file is part of Blockinger.

    Blockinger is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Blockinger is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Blockinger.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Blockinger.

    Blockinger ist Freie Software: Sie k�nnen es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
    ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

    Blockinger wird in der Hoffnung, dass es n�tzlich sein wird, aber
    OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License f�r weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package org.blockinger.game.components;

import org.blockinger.game.R;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.preference.PreferenceManager;

public class Sound implements OnAudioFocusChangeListener {

	private Activity host;
	private AudioManager audioCEO;
	private int soundID_tetrisSoundPlayer;
	private int soundID_dropSoundPlayer;
	private int soundID_clearSoundPlayer;
	private int soundID_gameOverPlayer;
	private int soundID_buttonSoundPlayer;
	private MediaPlayer musicPlayer;
	private boolean noFocus;
	private IntentFilter intentFilter;
	private NoiseBroadcastReceiver noisyAudioStreamReceiver;
	private SoundPool soundPool;
	private int songtime;

	public static final int NO_MUSIC = 0x0;
	public static final int MENU_MUSIC = 0x1;
	public static final int GAME_MUSIC = 0x2;
	
	public Sound(Activity c) {
		host = c;
		
		audioCEO = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		c.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		int result = audioCEO.requestAudioFocus(this,
	                // Use the music stream.
	                AudioManager.STREAM_MUSIC,
	                // Request permanent focus.
	                AudioManager.AUDIOFOCUS_GAIN);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			noFocus = false;
		} else
			noFocus = true;

		intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		noisyAudioStreamReceiver = new NoiseBroadcastReceiver();
		c.registerReceiver(noisyAudioStreamReceiver, intentFilter);
		
		soundPool = new SoundPool(c.getResources().getInteger(R.integer.audio_streams),AudioManager.STREAM_MUSIC,0);

		soundID_tetrisSoundPlayer = -1;
		soundID_dropSoundPlayer = -1;
		soundID_clearSoundPlayer = -1;
		soundID_gameOverPlayer = -1;
		soundID_buttonSoundPlayer = -1;
		
		songtime = 0;
	}
	
	public void loadEffects() {
		soundID_tetrisSoundPlayer = soundPool.load(host, R.raw.seqlong, 1);
		soundID_dropSoundPlayer = soundPool.load(host, R.raw.drop2, 1);
		soundID_buttonSoundPlayer = soundPool.load(host, R.raw.keypressstandard, 1);
		soundID_clearSoundPlayer = soundPool.load(host, R.raw.synthaccord, 1);
		soundID_gameOverPlayer = soundPool.load(host, R.raw.gameover, 1);
	}
	
	public void loadMusic(int type, int startTime) {
		songtime = startTime;
		switch(type) {
			case MENU_MUSIC :
				musicPlayer = MediaPlayer.create(host, R.raw.lemmings03);
				break;
			case GAME_MUSIC :
				musicPlayer = MediaPlayer.create(host, R.raw.sadrobot01);
				break;
			default :
				musicPlayer = new MediaPlayer();
				break;
		}
		musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		musicPlayer.setLooping(true);
		musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
		musicPlayer.seekTo(songtime);
		musicPlayer.start();
	}
	
	public void clearSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_clearSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}
	
	public void buttonSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		if(!PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_button_sound", true))
			return;
		soundPool.play(
			soundID_buttonSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}
	
	public void dropSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_dropSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}

	public void tetrisSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		soundPool.play(
			soundID_tetrisSoundPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}

	public void gameOverSound() {
		if(noFocus)
			return;
		if(audioCEO.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		pause(); // pause music to make the end of the game feel more dramatic. hhheheh.
		soundPool.play(
			soundID_gameOverPlayer,
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 
			1, 
			0, 
			1.0f
		);
	}

	public void resume() {
		soundPool.autoResume();
		if(musicPlayer != null)
			try{
				musicPlayer.start();
			} catch(IllegalStateException e) {
				
			}
	}

	public void pause() {
		soundPool.autoPause();
		if(musicPlayer != null)
			try{
				musicPlayer.pause();
			} catch(IllegalStateException e) {
				
			}
	}
	
	public void release() {
		soundPool.autoPause();
		soundPool.release();
		soundPool = null;
		if(musicPlayer != null)
			musicPlayer.release();
		musicPlayer = null;

		host.unregisterReceiver(noisyAudioStreamReceiver);
		audioCEO.abandonAudioFocus(this);
		audioCEO = null;
		host = null;
		noFocus = true;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
    		if(musicPlayer != null)
    			try{
    				musicPlayer.setVolume(0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
    	        } catch(IllegalStateException e) {
    				
    			}
        	soundPool.setVolume(soundID_tetrisSoundPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_dropSoundPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_clearSoundPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_gameOverPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_buttonSoundPlayer, 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.0025f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
    		if(musicPlayer != null)
    			try{
    				musicPlayer.setVolume(0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_musicvolume", 60));
    	        } catch(IllegalStateException e) {
    				
    			}
    		soundPool.setVolume(soundID_tetrisSoundPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_dropSoundPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_clearSoundPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_gameOverPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	soundPool.setVolume(soundID_buttonSoundPlayer, 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60), 0.01f * PreferenceManager.getDefaultSharedPreferences(host).getInt("pref_soundvolume", 60));
        	resume();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
        	pause();
        }
	}

	public int getSongtime() {
		if(musicPlayer != null)
			try{
				return musicPlayer.getCurrentPosition();
			} catch(IllegalStateException e) {
				
			}
		return 0;
	}
}

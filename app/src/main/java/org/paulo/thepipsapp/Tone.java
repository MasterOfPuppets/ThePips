package org.paulo.thepipsapp;

import java.math.BigDecimal;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;

public class Tone {

	private final int SAMPLE_RATE = 8000;
	private final int MAX_BUFFER_SIZE = 160000; // max 10 secs.
    private final BigDecimal MAX_PIP_LENGTH = new BigDecimal("2");
	private BigDecimal duration; // seconds
	private int frequency; // in hz

    //private AudioTrack audioTrack;
    private byte[] actualTone;
	
	public Tone(){
        this.frequency = 1000;
        this.duration = BigDecimal.ONE;
        actualTone = getTone();
        //this.audioTrack = getAudioTrack();

//        audioTrack = new AudioTrack(AudioManager. STREAM_MUSIC, 8000,
//                AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, getNumSamples(),
//                AudioTrack.MODE_STATIC);
		}
	
	private byte[] getTone(){
		int numSamples = getNumSamples();
		byte generatedSnd[] = new byte[2 * numSamples];
		double sample[] = new double[numSamples];
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (SAMPLE_RATE / getFrequency()));
		}

		// convert to 16 bit pcm sound array
		// assumes the sample floatbuffer is normalised.
		int idx = 0;
		for (double dVal : sample) {
			short val = (short) (dVal * 32767);
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			}
		return generatedSnd;
	}

	public void play() {
        AudioTrack audioTrack = getAudioTrack();
        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause();
            //audioTrack.flush();
        }
        audioTrack.write(actualTone, 0, getNumSamples());
        audioTrack.play();
        //track.flush();

        Log.d(ThePips.TAG, "peeep");
	}

	public void bbcPips(){
        setTone(1000, new BigDecimal("0.1"));
		play();
		delay(900);
		play();
		delay(900);
		play();
		delay(900);
		play();
		delay(900);
		play();
		delay(900);
        setTone(1000, new BigDecimal("0.5"));
		play();
	}
	
	public void hourPips(){
        setTone(1000, new BigDecimal("0.5"));
		play();
        setTone(1000, new BigDecimal("0.1"));
		delay(900);
		play();
		delay(900);
		play();
		delay(900);
		play();
	}
	
	public void firstQuarterPips(){
        setTone(1000, new BigDecimal("0.5"));
		play();
		delay(900);
        setTone(1000, new BigDecimal("0.1"));
		play();
	}

	public void secondQuerterPips(){
        setTone(1000, new BigDecimal("0.5"));
		play();
		delay(900);
        setTone(1000, new BigDecimal("0.1"));
		play();
		delay(900);
		play();
	}
	public void thirdQuarterPips(){
        setTone(1000, new BigDecimal("0.5"));
		play();
		delay(900);
        setTone(1000, new BigDecimal("0.1"));
		play();
		delay(900);
		play();
		delay(900);
		play();
	}
	
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

    private void setTone(int frequency, BigDecimal duration){
        setFrequency(frequency);
        setDuration(duration);
        actualTone = getTone();
    }
	public void setDuration(BigDecimal duration) {
		this.duration = duration;
	}
	
	public int getNumSamples() {
		int numSamples = duration.multiply(new BigDecimal(SAMPLE_RATE)).intValue();
		if (numSamples * 2 > MAX_BUFFER_SIZE) numSamples = MAX_BUFFER_SIZE / 2;
		return numSamples;
	}

    public int getMaxNumSamples() {
        int numSamples = MAX_PIP_LENGTH.multiply(new BigDecimal(SAMPLE_RATE)).intValue();
        if (numSamples * 2 > MAX_BUFFER_SIZE) numSamples = MAX_BUFFER_SIZE / 2;
        return numSamples;
    }



    public void delay(int millis){
        try {
            Thread.sleep(millis, 0);
        } catch (InterruptedException x){
            Log.e(ThePips.TAG, "Error pausing between beeps");
        }
    }
    public AudioTrack getAudioTrack(){
        return new AudioTrack(AudioManager.STREAM_MUSIC,
                8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                getMaxNumSamples(),
                AudioTrack.MODE_STATIC);
    }
}

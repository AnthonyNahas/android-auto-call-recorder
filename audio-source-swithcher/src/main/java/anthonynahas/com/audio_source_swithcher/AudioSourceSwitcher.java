package anthonynahas.com.audio_source_swithcher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by A on 18.05.17.
 */

public class AudioSourceSwitcher implements SensorEventListener {

    public static final String TAG = AudioSourceSwitcher.class.getSimpleName();

    private static final int SENSOR_SENSITIVITY = 4;
    private AudioManager mAudioManager;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private MediaPlayer mMediaPlayer;

    public AudioSourceSwitcher(Context context, MediaPlayer mediaPlayer) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mMediaPlayer = mediaPlayer;
    }


    public void playOnEarpiece() {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        mAudioManager.setSpeakerphoneOn(false);
    }

    public void playOnSpeaker() {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        mAudioManager.setSpeakerphoneOn(true);
    }

    public void destroy(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                //near
                Log.d(TAG, "near");
                playOnEarpiece();
            } else {
                //far
                Log.d(TAG, "far");
                playOnSpeaker();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "accuracy = " + accuracy);
    }

}

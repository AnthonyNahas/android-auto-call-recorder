package anthonynahas.com.audio_source_swithcher;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by A on 18.05.17.
 */

public class AudioSourceSwitcher {

    private AudioManager mAudioManager;

    public AudioSourceSwitcher(Context context){
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }


    public  void playOnEarpiece(MediaPlayer mediaPlayer){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        mAudioManager.setSpeakerphoneOn(false);
    }

    public void playOnSpeaker(MediaPlayer mediaPlayer){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        mAudioManager.setSpeakerphoneOn(true);
    }

}

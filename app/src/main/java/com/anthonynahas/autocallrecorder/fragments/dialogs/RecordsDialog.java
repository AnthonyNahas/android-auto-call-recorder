package com.anthonynahas.autocallrecorder.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.AudioSourceSwitcher;
import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.activities.MainOldActivity;
import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.classes.Res;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.providers.RecordDbHelper;
import com.anthonynahas.autocallrecorder.utilities.asyncTasks.AudioFileAsyncTask;
import com.anthonynahas.autocallrecorder.utilities.asyncTasks.ContactPhotosAsyncTask;
import com.anthonynahas.autocallrecorder.utilities.asyncTasks.FileDeleterTask;
import com.anthonynahas.autocallrecorder.utilities.helpers.FileHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.UploadAudioFile;
import com.anthonynahas.autocallrecorder.utilities.helpers.WindowHelper;
import com.anthonynahas.ui_animator.AnimationLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by A on 04.05.16.
 *
 * @author Anthony Nahas
 * @version 1.5
 * @since 04.05.2016
 */
public class RecordsDialog extends DialogFragment implements
        View.OnClickListener,
        MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener {

    public static final String TAG = RecordsDialog.class.getSimpleName();

    @BindView(R.id.toolbar_rec_dialog)
    Toolbar toolbar;

    @BindView(R.id.img_profile_recDialog)
    ImageView iv_profile;

    @BindView(R.id.contact_name_number_recDialog)
    TextView tv_number_or_name;

    @BindView(R.id.seekbar_rec)
    SeekBar seekBar;

    @BindView(R.id.tv_duration_rec)
    TextView tv_duration;

    @BindView(R.id.button_rec_dialog_close)
    Button b_close;

    @BindView(R.id.fab_play_pause)
    FloatingActionButton fab_play_pause;

    @BindView(R.id.fab_share)
    FloatingActionButton fab_share;

    @BindView(R.id.fab_delete)
    FloatingActionButton fab_delete;

    @BindView(R.id.fab_call)
    FloatingActionButton fab_call;

    private Unbinder mUnbinder;

    private MediaPlayer mMediaPlayer;

    private AudioSourceSwitcher mAudioSourceSwitcher;
    private Handler mSeekHandler = new Handler();
    private boolean isPaused;
    private boolean isDurationTextPressed;

    private Record mRecord;

    private String mPathFile;
    private String mFileName;

    private Context mContext;

    public static void show(Context context, Record record) {
        RecordsDialog recordsDialog = new RecordsDialog();
        Bundle args = new Bundle();
        args.putParcelable(Res.REC_PARC_KEY, record);
        recordsDialog.setArguments(args);
        recordsDialog.show(((Activity) context).getFragmentManager(), RecordsDialog.TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //-----** extracting sent arguments **------------
        Bundle arguments = getArguments();
        mRecord = arguments.getParcelable(Res.REC_PARC_KEY);

        //---------------------------------------------------
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.record_dialog_layout, null);
        mUnbinder = ButterKnife.bind(this, view);

        mContext = view.getContext();
        builder.setView(view);

        init(view);
        updateSeekBar();

        Log.d(TAG, "id = " + mRecord.get_ID());

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowHelper.init(getDialog().getWindow());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void init(View view) {

        //toolbar setup
        toolbar.inflateMenu(R.menu.menu_record_dialog);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        b_close.setOnClickListener(this);

        new ContactPhotosAsyncTask(mContext,mRecord, iv_profile).execute(0);

        if (mRecord.getName() != null) {
            tv_number_or_name.setText(mRecord.getName());
            tv_number_or_name.setOnClickListener(this);
        } else {
            tv_number_or_name.setText(mRecord.getNumber());
        }

        seekBar.setOnSeekBarChangeListener(this);
        isDurationTextPressed = false;

        tv_duration.setOnClickListener(this);

        getAudioFilePath(String.valueOf(mRecord.get_ID()));
        mMediaPlayer = new MediaPlayer();
        mAudioSourceSwitcher = new AudioSourceSwitcher(getActivity(), mMediaPlayer);

        setAndPrepareMediaPlayer();
        tv_duration.setText(getTimeString(mMediaPlayer.getDuration()));
        seekBar.setMax(mMediaPlayer.getDuration());
        isPaused = true;

        /**
         * The mediaplayer will be released, when the mediaplayer is done with playing the audio file
         */

        mMediaPlayer.setOnCompletionListener(null);

        /**
         * Play/Pause the media player
         */
        fab_play_pause.setOnClickListener(this);

        /**
         * Share the audio file
         */
        fab_share.setOnClickListener(this);

        /**
         * Delete the audio file
         */
        fab_delete.setOnClickListener(this);

        /**
         * Call the contact
         */
        fab_call.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact_name_number_recDialog:
                tv_number_or_name.startAnimation(AnimationLoader.get(mContext, R.anim.sample_animation));
                tv_number_or_name.setText(tv_number_or_name.getText().equals(mRecord.getNumber())
                        ? mRecord.getName() : mRecord.getNumber());
                break;
            case R.id.button_rec_dialog_close:
                dismiss();
                break;
            case R.id.tv_duration_rec:
                isDurationTextPressed = !isDurationTextPressed;
                break;
            case R.id.fab_play_pause:
                if (isPaused) {
                    playMediaPlayer();
                } else {
                    stopMediaPlayer();
                }
                break;
            case R.id.fab_share:
                mRecord.share(mContext);
                break;
            case R.id.fab_delete:
                deleteRecord();
                dismiss();
                break;

            case R.id.fab_call:
                mRecord.call(mContext);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_record_dialog, menu);
        updateIsLockedMenuItemTitle(menu.findItem(R.id.action_update_isLocked));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        //handle item action
        switch (item.getItemId()) {
            case R.id.action_update_isLocked:
                mRecord.updateIsLocked();
                RecordDbHelper.updateIsLockedColumn(mContext, mRecord.get_ID(), mRecord.isLockedAsInt());
                updateIsLockedMenuItemTitle(item);
                return true;
            case R.id.action_share:
                FileHelper.share(getActivity(), mRecord.get_ID());
                return true;
            case R.id.action_upload:
                uploadRecord();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateIsLockedMenuItemTitle(MenuItem item) {
        item.setTitle(mRecord.isLocked() ?
                getResources().getString(R.string.action_unlock)
                :
                getResources().getString(R.string.action_lock));
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
//            updateSeekBar();
        }
    };

    private void updateSeekBar() {
        if (mMediaPlayer != null) {
            seekBar.setProgress(mMediaPlayer.getCurrentPosition());
        }
        mSeekHandler.postDelayed(run, 100);
    }

    public static String getTimeString(int duration) {
        int minutes = (int) Math.floor(duration / 1000 / 60);
        int seconds = (duration / 1000) - (minutes * 60);
        return minutes + ":" + String.format("%02d", seconds);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isDurationTextPressed) {
            tv_duration.setText(getTimeString(progress) + "/" + getTimeString(this.seekBar.getMax()));
        } else {
            tv_duration.setText(getTimeString(this.seekBar.getMax() - progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        stopMediaPlayer();
        mSeekHandler.removeCallbacks(run);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaPlayer.seekTo(this.seekBar.getProgress());
        playMediaPlayer();
        mSeekHandler.postDelayed(run, 100);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "onCancel()");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mSeekHandler.removeCallbacks(run);
            mSeekHandler.removeCallbacksAndMessages(null);
        }
        mAudioSourceSwitcher.destroy();
    }

    private void setAndPrepareMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.setDataSource(mRecord.getPath());
                mMediaPlayer.prepare();
//                mMediaPlayer.setDataSource(getActivity(), ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.valueOf(mRecord.get_ID())));
            } catch (IOException | NullPointerException e) {
                Log.e(TAG, "error - IOException", e);
                // TODO: 12.06.17 enhance catching error with firebase
                //todo - 26.04 - create dialog fragment --> no source file found 404 - id now found in the media store
            }
        }
    }

    private void playMediaPlayer() {
        mMediaPlayer.setOnCompletionListener(this);
        isPaused = false;
        fab_play_pause.setImageResource(android.R.drawable.ic_media_pause);
        mMediaPlayer.start();
    }

    private void stopMediaPlayer() {
        mMediaPlayer.setOnCompletionListener(null);
        isPaused = true;
        fab_play_pause.setImageResource(android.R.drawable.ic_media_play);
        mMediaPlayer.pause();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!isPaused) {
            isPaused = true;
            fab_play_pause.setImageResource(android.R.drawable.ic_media_play);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            fab_play_pause.setImageResource(android.R.drawable.ic_media_play);
            isPaused = true;
            setAndPrepareMediaPlayer();
            seekBar.setProgress(0);
            //mMediaPlayer.release();
            //isReleased = true;
            //mSeekHandler.removeCallbacks(run);
        }
    }

    private void deleteRecord() {
        //getActivity().getContentResolver().delete(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),null,null);
        //getActivity().getContentResolver().delete(ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, id),null,null);
        ArrayList<String> filesToDelete = new ArrayList<>();
//        filesToDelete.add(getAudioFilePath(String.valueOf(mRecord.get_ID())));
        filesToDelete.add(mRecord.getPath());
        AsyncTask<ArrayList<String>, Void, Boolean> task = new FileDeleterTask().execute(filesToDelete);
        getActivity().getContentResolver().delete(RecordDbContract.CONTENT_URL, RecordDbContract.RecordItem.COLUMN_ID
                + "= '" + mRecord.get_ID() + "'", null);
    }

    private String getAudioFilePath(String id) {
        AudioFileAsyncTask audioFileAsyncTask = new AudioFileAsyncTask(id, getActivity().getApplicationContext());
        try {
            Cursor audioCursor = audioFileAsyncTask.execute().get();
            String id2 = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media._ID));
            mPathFile = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            mFileName = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            Log.d(TAG, "id original = " + id);
            Log.d(TAG, " id = " + id2 + " | data = " + mPathFile + " name = " + mFileName);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error : ", e);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Error: IndexOutOfBound", e);
        }
        return mPathFile;
    }

    private void uploadRecord() {
        // TODO: 04.05.2017 : dropbox - fdp server - google drive ...
        Log.d(TAG, "onUpload()");
        UploadAudioFile uploadAudioFile = new UploadAudioFile(getActivity()
                .getApplication()
                .getApplicationContext(),
                MainOldActivity.mApi, MainOldActivity.DROPBOX_FILE_DIR, mPathFile, mFileName);
        uploadAudioFile.execute();
    }
}

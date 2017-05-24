package anthonynahas.com.autocallrecorder.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import anthonynahas.com.audio_source_swithcher.AudioSourceSwitcher;
import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.activities.MainOldActivity;
import anthonynahas.com.autocallrecorder.classes.Record;
import anthonynahas.com.autocallrecorder.classes.Resources;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.utilities.helpers.AudioFileAsyncTask;
import anthonynahas.com.autocallrecorder.utilities.helpers.ContactHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.FileDeleterTask;
import anthonynahas.com.autocallrecorder.utilities.helpers.ImageHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.UploadAudioFile;

/**
 * Created by A on 04.05.16.
 *
 * @author Anthony Nahas
 * @version 1.5
 * @since 04.05.2016
 */
public class RecordsDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    public static final String TAG = RecordsDialog.class.getSimpleName();

    private ImageView mImageProfile;
    private TextView mTV_Number_CN;
    private SeekBar mSeekbarRec;
    private TextView mTV_Duration;
    private FloatingActionButton mFloatingActionButton_play_pause;

    private MediaPlayer mMediaPlayer;
    private AudioSourceSwitcher mAudioSourceSwitcher;
    private Handler mSeekHandler = new Handler();
    private boolean isPaused;
    private boolean iSDurationTextPressed;

    private Record mRecord;

    private String mPathFile;
    private String mFileName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //-----** extracting sent arguments **------------
        Bundle arguments = getArguments();
        mRecord = arguments.getParcelable(Resources.REC_PARC_KEY);

        //---------------------------------------------------

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.record_dialog_layout, null);
        builder.setView(view);

        init(view);
        updateSeekBar();

        mTV_Number_CN.setText(mRecord.getName());
        Log.d(TAG, "id = " + mRecord.get_ID());

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setDimAmount(0.9f);
        }
        //window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void init(View view) {

        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar_rec_dialog);
        mToolbar.inflateMenu(R.menu.menu_record_dialog);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        Button closeButton = (Button) view.findViewById(R.id.button_rec_dialog_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mImageProfile = (ImageView) view.findViewById(R.id.img_profile_recDialog);
        //setContactBitMap();
        //mImageProfile.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getContactBitMap(),100));
        Bitmap contactPhoto = ImageHelper.getRoundedCroppedBitmap(getContactBitMap(), convert_dp_To_px(150));
        if (contactPhoto != null) {
            mImageProfile.setImageBitmap(contactPhoto);
        } else {
            mImageProfile.setImageResource(R.drawable.custmtranspprofpic);
        }

        mTV_Number_CN = (TextView) view.findViewById(R.id.contact_name_number_recDialog);
        mSeekbarRec = (SeekBar) view.findViewById(R.id.seekbar_rec);
        mSeekbarRec.setOnSeekBarChangeListener(this);
        iSDurationTextPressed = false;
        mTV_Duration = (TextView) view.findViewById(R.id.tv_duration_rec);
        mTV_Duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iSDurationTextPressed = !iSDurationTextPressed;
            }
        });
        mFloatingActionButton_play_pause = (FloatingActionButton) view.findViewById(R.id.floating_action_button_play_pause);
        FloatingActionButton floatingActionButton_share = (FloatingActionButton) view.findViewById(R.id.floating_action_button_share);
        FloatingActionButton floatingActionButton_delete = (FloatingActionButton) view.findViewById(R.id.floating_action_button_delete);
        FloatingActionButton floatingActionButton_call = (FloatingActionButton) view.findViewById(R.id.floating_action_button_call);
        getAudioFilePath(String.valueOf(mRecord.get_ID()));
        mMediaPlayer = new MediaPlayer();
        mAudioSourceSwitcher = new AudioSourceSwitcher(getActivity(), mMediaPlayer);

        setAndPrepareMediaPlayer();
        mTV_Duration.setText(getTimeString(mMediaPlayer.getDuration()));
        mSeekbarRec.setMax(mMediaPlayer.getDuration());
        isPaused = true;
        mFloatingActionButton_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) {
                    playMediaPlayer();
                } else {
                    stopMediaPlayer();
                }
            }
        });

        /**
         * The mediaplayer will be released, when the mediaplayer is done with playing the audio file
         */
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!isPaused) {
                    isPaused = true;
                    mFloatingActionButton_play_pause.setImageResource(android.R.drawable.ic_media_play);
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.reset();
                    mFloatingActionButton_play_pause.setImageResource(android.R.drawable.ic_media_play);
                    isPaused = true;
                    setAndPrepareMediaPlayer();
                    mSeekbarRec.setProgress(0);
                    //mMediaPlayer.release();
                    //isReleased = true;
                    //mSeekHandler.removeCallbacks(run);

                }
            }
        });


        /**
         * Share the audio file
         */
        floatingActionButton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/*");
                share.putExtra(Intent.EXTRA_STREAM,
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                Long.valueOf(mRecord.get_ID())));
                startActivity(Intent.createChooser(share, "Share Sound File"));
            }
        });

        /**
         * Delete the audio file
         */
        floatingActionButton_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord();
                dismiss();
            }
        });
        floatingActionButton_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_the_contact();
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_record_dialog, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        //handle item action
        switch (item.getItemId()) {
            case R.id.action_share:
                shareRecord();
                return true;
            case R.id.action_upload:
                uploadRecord();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }
    };

    private void updateSeekBar() {
        if (mMediaPlayer != null) {
            mSeekbarRec.setProgress(mMediaPlayer.getCurrentPosition());
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
        if (iSDurationTextPressed) {
            mTV_Duration.setText(getTimeString(progress) + "/" + getTimeString(mSeekbarRec.getMax()));
        } else {
            mTV_Duration.setText(getTimeString(mSeekbarRec.getMax() - progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        stopMediaPlayer();
        mSeekHandler.removeCallbacks(run);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaPlayer.seekTo(mSeekbarRec.getProgress());
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
                mMediaPlayer.setDataSource(getActivity(),
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                Long.valueOf(mRecord.get_ID())));
                mMediaPlayer.prepare();
            } catch (IOException | NullPointerException e) {
                Log.e(TAG, "error - IOException", e);
                //todo - 26.04 - create dialog fragment --> no source file found 404 - id now found in the media store
            }
        }
    }

    private void playMediaPlayer() {
        isPaused = false;
        mFloatingActionButton_play_pause.setImageResource(android.R.drawable.ic_media_pause);
        mMediaPlayer.start();
    }

    private void stopMediaPlayer() {
        isPaused = true;
        mFloatingActionButton_play_pause.setImageResource(android.R.drawable.ic_media_play);
        mMediaPlayer.pause();
    }

    private void deleteRecord() {
        //getActivity().getContentResolver().delete(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),null,null);
        //getActivity().getContentResolver().delete(ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, id),null,null);
        ArrayList<String> filesToDelete = new ArrayList<>();
        filesToDelete.add(getAudioFilePath(mRecord.get_ID()));
        AsyncTask<ArrayList<String>, Void, Boolean> task = new FileDeleterTask().execute(filesToDelete);
        getActivity().getContentResolver().delete(RecordDbContract.CONTENT_URL, RecordDbContract.RecordItem.COLUMN_ID
                + "= '" + mRecord.get_ID() + "'", null);
    }

    private void call_the_contact() {
        Intent in = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + mRecord.getNumber())); //"tel: " + "+46 (999) 44 55 66"
        try {
            startActivity(in);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
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
            e.printStackTrace();
            Log.e(TAG, "Error : ", e);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Error: IndexOutOfBound", e);
        }
        return mPathFile;
    }

    private void setContactBitMap() {
        InputStream in;
        BufferedInputStream buf;
        try {
            in = ContactHelper.openLargeDisplayPhoto(getActivity().getContentResolver(), mRecord.getContactID());
            buf = new BufferedInputStream(in);
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            mImageProfile.setImageBitmap(bMap);
            in.close();
            buf.close();
        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }
    }

    private Bitmap getContactBitMap() {
        InputStream in;
        BufferedInputStream buf;
        try {
            in = ContactHelper.openLargeDisplayPhoto(getActivity().getContentResolver(), mRecord.getContactID());
            buf = new BufferedInputStream(in);
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            in.close();
            buf.close();
            return bMap;
        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }
        return null;
    }

    private int convert_dp_To_px(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics()
        );
    }

    private void shareRecord() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM,
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Long.valueOf(mRecord.get_ID())));
        startActivity(Intent.createChooser(share, "Share Sound File"));
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

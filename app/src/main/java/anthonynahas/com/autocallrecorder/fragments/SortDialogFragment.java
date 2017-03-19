package anthonynahas.com.autocallrecorder.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.activities.SettingsActivity;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;

/**
 * Created by A on 14.06.16.
 */
public class SortDialogFragment extends DialogFragment {

    public final static String TAG = SortDialogFragment.class.getSimpleName();

    private String mSelect;
    private String mArrange;
    private SharedPreferences mSharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sort_dialog_layout, null);

        mSelect = mSharedPreferences.getString(SettingsActivity.KEY_SORT_SELECTION,RecordDbContract.RecordItem.COLUMN_DATE);
        mArrange = mSharedPreferences.getString(SettingsActivity.KEY_SORT_ARRANGE," DESC");

        RadioGroup select_radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup_sort_select);
        checkSelectRadioGroup(select_radiogroup,mSelect);
        select_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_select_contact:
                        mSelect = RecordDbContract.RecordItem.COLUMN_NUMBER;
                        break;
                    case R.id.rb_select_date:
                        mSelect = RecordDbContract.RecordItem.COLUMN_DATE;
                        break;
                    case R.id.rb_select_duration:
                        mSelect = RecordDbContract.RecordItem.COLUMN_DURATION;
                        break;
                    default:
                        mSelect = RecordDbContract.RecordItem.COLUMN_DATE;
                        break;
                }
            }
        });
        RadioGroup arrange_radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup_sort_arrange);
        checkArrangeRadioGroup(arrange_radiogroup,mArrange);
        arrange_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_arrange_asc:
                        mArrange = " ASC";
                        break;
                    case R.id.rb_arrange_desc:
                        mArrange = " DESC";
                        break;
                    default:
                        mArrange = " DESC";
                        break;
                }
            }
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.sort_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mSharedPreferences
                                .edit()
                                .putString(SettingsActivity.KEY_SORT_SELECTION,mSelect)
                                .putString(SettingsActivity.KEY_SORT_ARRANGE,mArrange)
                                .apply();
                        notifyTargetFragment(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton(R.string.sort_dialog_negative_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notifyTargetFragment(Activity.RESULT_CANCELED);
                    }
                })
                .setTitle(R.string.sort_dialog_title);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity().getBaseContext(),R.color.colorPrimary));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity().getBaseContext(),R.color.colorPrimary));
    }

    private void checkSelectRadioGroup(RadioGroup group,String select) {
        switch (select) {
            case RecordDbContract.RecordItem.COLUMN_NUMBER:
                group.check(R.id.rb_select_contact);
                break;
            case RecordDbContract.RecordItem.COLUMN_DATE:
                group.check(R.id.rb_select_date);
                break;
            case RecordDbContract.RecordItem.COLUMN_DURATION:
                group.check(R.id.rb_select_duration);
                break;
              }
    }

    private void checkArrangeRadioGroup(RadioGroup group,String arrange){
        switch (arrange){
            case " DESC":
                group.check(R.id.rb_arrange_desc);
                break;
            case " ASC":
                group.check(R.id.rb_arrange_asc);
                break;
        }
    }

    private void notifyTargetFragment(int code) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), code, null);
        }
    }
}

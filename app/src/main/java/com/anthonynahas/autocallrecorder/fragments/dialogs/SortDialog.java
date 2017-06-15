package com.anthonynahas.autocallrecorder.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by A on 14.06.16.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 14.06.2016
 */
public class SortDialog extends DialogFragment {

    public final static String TAG = SortDialog.class.getSimpleName();

    @Inject
    PreferenceHelper mPreferenceHelper;

    @BindView(R.id.radiogroup_sort_arrange)
    RadioGroup radiogroup_arrange;

    @BindColor(R.color.colorPrimary)
    int mPrimaryColor;

    private Unbinder mUnbinder;

    private String mSelect;
    private String mArrange;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sort_dialog_layout, null);
        mUnbinder = ButterKnife.bind(this, view);

        mSelect = mPreferenceHelper.getSortSelection();
        mArrange = mPreferenceHelper.getSortArrange();

        RadioGroup select_radio_group = (RadioGroup) view.findViewById(R.id.radiogroup_sort_select);
        checkSelectRadioGroup(select_radio_group, mSelect);
        select_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
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

        checkArrangeRadioGroup(radiogroup_arrange, mArrange);
        radiogroup_arrange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
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

                        mPreferenceHelper.setSortSelection(mSelect);
                        mPreferenceHelper.setSortArrange(mArrange);

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
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity().getBaseContext(), mPrimaryColor));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorPrimary));
    }

    private void checkSelectRadioGroup(RadioGroup group, String select) {
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

    private void checkArrangeRadioGroup(RadioGroup group, String arrange) {
        switch (arrange) {
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

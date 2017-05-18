package anthonynahas.com.autocallrecorder.utilities.decorators;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import anthonynahas.com.autocallrecorder.R;

/**
 * Created by A on 18.05.17.
 */

public class ActionBarDecorator {

    private ActionBar mActionBar;

    public void setup(AppCompatActivity appCompatActivity){
        ViewGroup rootView = (ViewGroup) appCompatActivity.findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = appCompatActivity.getLayoutInflater().inflate(R.layout.material_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(R.id.toolbar);
            appCompatActivity.setSupportActionBar(toolbar);
        }

        mActionBar = appCompatActivity.getSupportActionBar();

    }

    public ActionBar getActionBar() {
        return mActionBar;
    }
}

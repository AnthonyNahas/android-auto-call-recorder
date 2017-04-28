package anthonynahas.com.autocallrecorder.activities;

import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import anthonynahas.com.autocallrecorder.R;

public class RecordsInActionModeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    public static final String TAG = RecordsInActionModeActivity.class.getSimpleName();


    int counter = 0;

    private final int mLimit = 30;
    private int mOffset = 0;
    private String mSearchKey = "";

    private Handler handlerToWait = new Handler();
    private RecyclerView mRecyclerView;
    private TextView mCounterTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_in_action_mode);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}

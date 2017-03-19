package anthonynahas.com.autocallrecorder.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.providers.RecentSuggestionProvider;

/**
 * Created by A on 16.06.16.
 */
public class SearchableActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private static final String TAG = SearchableActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate()");
        setContentView(R.layout.activity_search);

        /* toolbar*/
        mToolbar = (Toolbar) findViewById(R.id.toolbar_search);
        //mToolbar.setLogo(R.drawable.custmtranspprofpic);
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.action_search);
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        /* toolbar*/
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG,"onNewIntent()");
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //save recent queries
            saveRecentQueries(query);
            //use the query to search your data somehow
            TextView tv = (TextView) findViewById(R.id.seach_tv);
            tv.setText(query);
            mToolbar.setSubtitle(query);
            //doMySearch(query);
        }
    }

    private void saveRecentQueries(String query){
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                RecentSuggestionProvider.AUTHORITY, RecentSuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);
    }
    private void clearRecentQueries(){
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                RecentSuggestionProvider.AUTHORITY, RecentSuggestionProvider.MODE);
        suggestions.clearHistory();
    }
}

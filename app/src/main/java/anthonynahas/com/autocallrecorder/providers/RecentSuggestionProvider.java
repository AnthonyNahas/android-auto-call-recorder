package anthonynahas.com.autocallrecorder.providers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by A on 17.06.16.
 */
public class RecentSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "anthonynahas.com.autocallrecorder.providers.RecentSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;
    //public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public RecentSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}

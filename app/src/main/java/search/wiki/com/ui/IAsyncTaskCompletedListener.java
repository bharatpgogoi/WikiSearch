package search.wiki.com.ui;

import java.util.ArrayList;

import search.wiki.com.model.data.SearchResult;

public interface IAsyncTaskCompletedListener {

    void onTaskComplete(ArrayList<SearchResult> searchResults, boolean isTimedOut);
}

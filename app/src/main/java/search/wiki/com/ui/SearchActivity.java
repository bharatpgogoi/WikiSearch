package search.wiki.com.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import search.wiki.com.adapter.ListViewAdapter;
import search.wiki.com.model.WikiSearch;
import search.wiki.com.model.data.SearchResult;
import search.wiki.com.util.ConnectionUtil;
import search.wiki.com.wikisearch.R;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener,IAsyncTaskCompletedListener {

    private ProgressBar mProgressBar;
    private ListViewAdapter mAdapter;
    private int mOffest = 0;
    private String mQuery;
    private TextView mTextView;
    private Button mRetry;
    private static final String TAG = "Wiki.SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint(this.getString(R.string.hint));
        searchView.setOnQueryTextListener(this);
        ListView listView = findViewById(R.id.listView);
        mTextView = findViewById(R.id.info_text);
        mRetry = findViewById(R.id.retry);
        mAdapter = new ListViewAdapter(this,new ArrayList<SearchResult>());
        mProgressBar = findViewById(R.id.progress_bar);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

    }



    @Override
    public void onBackPressed() {
         moveTaskToBack(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mOffest = 0;
        mQuery = query;
        if(ConnectionUtil.isConnected(this)) {
            if (mQuery != null && !mQuery.isEmpty()) {
                String url = getSearchUrl();
                try {
                    mTextView.setVisibility(View.INVISIBLE);
                    mRetry.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    new WikiSearch(SearchActivity.this,this).execute(new URL(url));
                } catch (MalformedURLException e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                mAdapter.clear();

            }
        }
        else {
            mTextView.setVisibility(View.VISIBLE);
            mRetry.setVisibility(View.VISIBLE);
            mRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.retry:
                            if (ConnectionUtil.isConnected(SearchActivity.this)) {
                                mTextView.setVisibility(View.INVISIBLE);
                                mRetry.setVisibility(View.INVISIBLE);
                            }
                            break;
                    }


                }
            });
            mTextView.setText(R.string.no_internet_message);
        }
        return false;
    }

    @NonNull
    private String getSearchUrl() {
        String url = this.getString(R.string.wiki_search_url);
        String configuredParams = "action=query&format=json&errorformat=bc&prop=pageimages%7Cpageterms&continue=gpsoffset%7C%7C&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50&pilimit=10&wbptterms=description&gpslimit=10";
        String gpsOffset = "&gpsoffset=" + mOffest;
        String gpsSearch = "&gpssearch=" + mQuery;
        url = url + configuredParams + gpsOffset + gpsSearch;
        return url;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
        SearchResult selectedResult = (SearchResult) adapterView.getItemAtPosition(index);
        if(selectedResult != null){
            String pageId = selectedResult.getPageId();
            String url = this.getString(R.string.wiki_page_url);
            url = url + pageId;
            loadWebViewActivity(url);
        }

    }

    @Override
    public void onTaskComplete(ArrayList<SearchResult> searchResults,boolean isTimedOut) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if(isTimedOut){
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(R.string.timed_out);
            return;
        }
        if(searchResults.size() > 0) {
            mAdapter.clear();
            mAdapter.addAll(searchResults);
            mAdapter.notifyDataSetChanged();
        }
    }
}

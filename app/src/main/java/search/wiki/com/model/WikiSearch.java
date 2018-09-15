package search.wiki.com.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import search.wiki.com.adapter.ListViewAdapter;
import search.wiki.com.model.data.SearchResult;

public class WikiSearch extends AsyncTask<URL,Integer,ArrayList<SearchResult>> {

    private Context mContext;
    private final ProgressBar mProgressBar;
    private ListViewAdapter mAdapter;

    public WikiSearch(Context context, ListViewAdapter adapter, ProgressBar bar){
        mContext = context;
        mProgressBar = bar;
        mAdapter = adapter;
    }

    @Override
    protected void onPostExecute(ArrayList<SearchResult> result) {
        super.onPostExecute(result);
        mProgressBar.setVisibility(View.INVISIBLE);
        if(result.size() > 0) {
            mAdapter.clear();
            mAdapter.addAll(result);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected ArrayList<SearchResult> doInBackground(URL... urls) {
        ArrayList<SearchResult> result = new ArrayList<>();
        try {
            URL u = urls[0];
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream is = conn.getInputStream();

            // Read the stream
            byte[] b = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
            while ( is.read(b) != -1)
                byteArrayOutputStream.write(b);

            String jsonResponse = new String(byteArrayOutputStream.toByteArray());
            JSONObject object = new JSONObject(jsonResponse);
            JSONArray pages = object.getJSONObject("query").getJSONArray("pages");
            for(int i = 0; i<pages.length();i++){
                String title = "";
                String thumbnail = "";
                String description = "";
                String pageId = "";
                long index = -1;
                if(pages.getJSONObject(i).has("title")) {
                    title = pages.getJSONObject(i).getString("title");
                }
                if(pages.getJSONObject(i).has("thumbnail") && pages.getJSONObject(i).getJSONObject("thumbnail").has("source")) {
                    thumbnail = pages.getJSONObject(i).getJSONObject("thumbnail").getString("source");
                }
                if( pages.getJSONObject(i).has("terms") && pages.getJSONObject(i).getJSONObject("terms").has("description")) {
                    description = pages.getJSONObject(i).getJSONObject("terms").getJSONArray("description").getString(0);
                }
                if(pages.getJSONObject(i).has("pageid")){
                    pageId = pages.getJSONObject(i).getString("pageid");
                }
                if(pages.getJSONObject(i).has("index")){
                    index = pages.getJSONObject(i).getLong("index");
                }
                result.add(new SearchResult(title,thumbnail,description,pageId,index));

            }
            Collections.sort(result, new IndexComperator());

        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private class IndexComperator implements Comparator<SearchResult> {

        @Override
        public int compare(SearchResult r1, SearchResult r2) {
            if(r1.getIndex() == r2.getIndex())
                return 0;
            else if(r1.getIndex() > r2.getIndex())
                return 1;
            return -1;
        }
    }
}

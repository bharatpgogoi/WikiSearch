package search.wiki.com.model;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import search.wiki.com.model.data.SearchResult;
import search.wiki.com.ui.IAsyncTaskCompletedListener;
import search.wiki.com.ui.SearchActivity;

public class WikiSearch extends AsyncTask<URL,Integer,ArrayList<SearchResult>> {

    private WeakReference<SearchActivity> mSearchActivityReferences;
    private IAsyncTaskCompletedListener mListener;
    private static final String TAG = "Wiki.WikiSearch";
    private static final int CONNECTION_TIMEOUT = 10 * 1000; //ms
    private static final int READ_TIMEOUT = 10 * 1000; //ms
    private boolean mIsTimedOut;

    public WikiSearch(SearchActivity activityReferences,IAsyncTaskCompletedListener listener){
        mSearchActivityReferences = new WeakReference<>(activityReferences);
        mListener = listener;
    }

    @Override
    protected void onPostExecute(ArrayList<SearchResult> result) {
        super.onPostExecute(result);
        if(mListener != null && mSearchActivityReferences.get() != null){
            mListener.onTaskComplete(result,mIsTimedOut);
        }

    }

    @Override
    protected ArrayList<SearchResult> doInBackground(URL... urls) {
        ArrayList<SearchResult> result = new ArrayList<>();
        mIsTimedOut = false;
        if(mSearchActivityReferences.get() == null)
            return result;
        try {
            URL u = urls[0];
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();

                // Read the stream
                byte[] b = new byte[1024];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                while (is.read(b) != -1)
                    byteArrayOutputStream.write(b);

                String jsonResponse = new String(byteArrayOutputStream.toByteArray());
                JSONObject object = new JSONObject(jsonResponse);
                JSONArray pages = object.getJSONObject("query").getJSONArray("pages");
                for (int i = 0; i < pages.length(); i++) {
                    String title = "";
                    String thumbnail = "";
                    String description = "";
                    String pageId = "";
                    long index = -1;
                    if (pages.getJSONObject(i).has("title")) {
                        title = pages.getJSONObject(i).getString("title");
                    }
                    if (pages.getJSONObject(i).has("thumbnail") && pages.getJSONObject(i).getJSONObject("thumbnail").has("source")) {
                        thumbnail = pages.getJSONObject(i).getJSONObject("thumbnail").getString("source");
                    }
                    if (pages.getJSONObject(i).has("terms") && pages.getJSONObject(i).getJSONObject("terms").has("description")) {
                        description = pages.getJSONObject(i).getJSONObject("terms").getJSONArray("description").getString(0);
                    }
                    if (pages.getJSONObject(i).has("pageid")) {
                        pageId = pages.getJSONObject(i).getString("pageid");
                    }
                    if (pages.getJSONObject(i).has("index")) {
                        index = pages.getJSONObject(i).getLong("index");
                    }
                    result.add(new SearchResult(title, thumbnail, description, pageId, index));

                }
                Collections.sort(result, new IndexComperator());
            }
            else {
                Log.i(TAG,"Connection failed");
            }

        }
        catch (JSONException e){
            Log.e(TAG,e.getMessage());
        }
        catch (SocketTimeoutException e) {
            Log.e(TAG,e.getMessage());
            mIsTimedOut = true;
        }
        catch (ConnectTimeoutException e) {
            Log.e(TAG,e.getMessage());
            mIsTimedOut = true;
        }
        catch(UnknownHostException e){
            Log.e(TAG,e.getMessage());
            mIsTimedOut = true;
        }
        catch (IOException e){
            Log.e(TAG,e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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

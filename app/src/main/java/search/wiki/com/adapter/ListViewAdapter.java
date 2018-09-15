package search.wiki.com.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import search.wiki.com.model.data.SearchResult;
import search.wiki.com.wikisearch.R;

public class ListViewAdapter extends ArrayAdapter<SearchResult> {

    private Context mContext;

    private ArrayList<SearchResult> mSearchResult;


    public ListViewAdapter(Context context, ArrayList<SearchResult> result){
        super(context, R.layout.list_item_view, result);
        mContext = context;
        mSearchResult = result;
    }

    @Override
    public int getCount() {
        return mSearchResult.size();
    }

    @Override
    public SearchResult getItem(int index) {
       return mSearchResult.get(index);
    }

    @Override
    public long getItemId(int index) {
        return -1;
    }

    @NonNull
    @Override
    public View getView(int index, View view, @NonNull ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final ViewHolder holder;
        final ViewGroup parent = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item_view,parent);;
            holder.mDescription = view.findViewById(R.id.item_description);
            holder.mImage = view.findViewById(R.id.item_image);
            holder.mTitle = view.findViewById(R.id.item_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        SearchResult result = mSearchResult.get(index);

        if(result != null) {

            String description = result.getDescription();
            String title = result.getTitle();
            String thumbnail = result.getThumbnail();

            if (!description.isEmpty()) {
                holder.mDescription.setText(description);
            }

            if(!title.isEmpty()){
                holder.mTitle.setText(title);
            }

            if(!thumbnail.isEmpty()){
                Picasso.get().load(thumbnail)
                        .resize(50,50)
                        .into(holder.mImage);
            }
        }
        return view;
    }
    private static class ViewHolder
    {
        private ImageView mImage;
        private TextView mDescription;
        private TextView mTitle;
    }
}

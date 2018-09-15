package search.wiki.com.model.data;

public class SearchResult {

    private String mTitle;

    private String mThumbnail;

    private String mDescription;

    private String mPageId;

    private long mIndex;

    public SearchResult(String title, String thumbnail, String description,String pageId,long index){
        mTitle = title;
        mThumbnail = thumbnail;
        mDescription = description;
        mPageId = pageId;
        mIndex = index;
    }

    public String getPageId() {
        return mPageId;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getThumbnail(){
        return mThumbnail;
    }

    public long getIndex(){
        return mIndex;
    }

    public String getDescription(){
        return mDescription;
    }

}

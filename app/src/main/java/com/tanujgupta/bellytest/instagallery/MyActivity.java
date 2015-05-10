package com.tanujgupta.bellytest.instagallery;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import com.tanujgupta.bellytest.instagallery.Adapter.GalleryAdapter;
import com.tanujgupta.bellytest.instagallery.model.ImageItem;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyActivity extends Activity {

    private GridView gridView;
    private GalleryAdapter gridAdapter;
    private ArrayList<ImageItem> imageItems;

    public static String tag = "Spring";      // default tag
    private String pagingURL;           // Store the paging url
    private int current_page = 1;       // Flag for current page
    private boolean loadingMore = true; // Flag to check if new fields are loading
    private boolean stopLoadingData = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);



        // references to xml
        gridView = (GridView) findViewById(R.id.gridView);

        new getPhotosData().execute();   // load initial set of photos

        // clicking on any image leads to new activity with a single image
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(MyActivity.this, DetailsActivity.class);

                intent.putExtra("url", item.getUrlStandard());

                //Start details activity
                startActivity(intent);
            }
        });


        // load more images on scrolling down
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {

                    if (!stopLoadingData) {

                        new loadMorePhotosData().execute(); //fetch the next set of images
                    }

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);

        final MenuItem menuItem = menu.findItem(R.id.search); // search menu item

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Set HashTag");

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
        {
            public boolean onQueryTextChange(String newText)
            {
                return true;
            }

            public boolean onQueryTextSubmit(String query)
            {

                searchView.clearFocus();       // once the search is done, then clear the search field
                menuItem.collapseActionView(); // collapse the search window
                setHashTagAndLoadImages(query);// and load new images with the new hashtag
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }


    // called when a search is made
    public  void setHashTagAndLoadImages(String hashtag){

        tag = hashtag;
        new getPhotosData().execute();
    }

    
    public class getPhotosData extends AsyncTask<Void, Void, Void> {

        private String Url;

        private Exception mException;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            imageItems =  new ArrayList<ImageItem>();

            loadingMore = true;    // change the loading more to be true to prevent duplicate calls to the same batch of data
            Url = Resources.URL_START + tag + Resources.URL_AFTER_TAG + Resources.CLIENT_ID;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                HttpClient hc = new DefaultHttpClient();
                HttpGet get = new HttpGet(Url);
                HttpResponse response = hc.execute(get);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    String resp = EntityUtils.toString(response.getEntity());

                    JSONObject jTemp = new JSONObject(resp);

                    JSONObject jPagination =  jTemp.getJSONObject("pagination");
                    pagingURL = jPagination.getString("next_url");

                    JSONArray jData = jTemp.getJSONArray("data");

                    if (jData.length() == 0) { badHashTagSetDefault(); }

                    for (int i = 0 ; i < jData.length(); i++) {

                        JSONObject jElement = jData.getJSONObject(i);

                        JSONObject jImages = jElement.getJSONObject("images");

                        JSONObject jThumbnail = jImages.getJSONObject("thumbnail");
                        JSONObject jStandard = jImages.getJSONObject("standard_resolution");

                        String urlThumbnail = jThumbnail.getString("url");
                        String urlStandard = jStandard.getString("url");

                        ImageItem item = new ImageItem(urlThumbnail, urlStandard);

                        imageItems.add(item);
                        //  Log.d("response from internet", urlThumbnail);

                    }
                } else { badHashTagSetDefault();}

            }catch (Exception e) { mException = e; }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadingMore = false;      // change the loading more status

            if (mException != null) {

                badHashTagSetDefault();

            }else {

                gridAdapter = new GalleryAdapter(MyActivity.this, R.layout.gallery_item, imageItems);
                gridView.setAdapter(gridAdapter);
            }


        }
    }


    public class loadMorePhotosData extends AsyncTask<Void, Void, Void>{


        private String Url;

        private Exception mException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingMore = true;    // change the loading more to be true to prevent duplicate calls to the same batch of data
            current_page++;

            Url = pagingURL;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                HttpClient hc = new DefaultHttpClient();
                HttpGet get = new HttpGet(Url);
                HttpResponse response = hc.execute(get);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    String resp = EntityUtils.toString(response.getEntity());

                    JSONObject jTemp = new JSONObject(resp);

                    JSONObject jPagination =  jTemp.getJSONObject("pagination");
                    pagingURL = jPagination.getString("next_url");

                    JSONArray jData = jTemp.getJSONArray("data");

                    if (jData.length() == 0) { badHashTagSetDefault(); }

                    for (int i = 0 ; i < jData.length(); i++) {

                        JSONObject jElement = jData.getJSONObject(i);

                        JSONObject jImages = jElement.getJSONObject("images");

                        JSONObject jThumbnail = jImages.getJSONObject("thumbnail");
                        JSONObject jStandard = jImages.getJSONObject("standard_resolution");

                        String urlThumbnail = jThumbnail.getString("url");
                        String urlStandard = jStandard.getString("url");

                        ImageItem item = new ImageItem(urlThumbnail, urlStandard);

                        imageItems.add(item);
                        //  Log.d("response from internet", urlThumbnail);

                    }
                } else { badHashTagSetDefault();}

            }catch (Exception e) { mException = e; }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // get listview current position - used to maintain scroll position
            int currentPosition = gridView.getFirstVisiblePosition();

            // append the data to the  arraylist and set the adapter to the the listview
            gridAdapter = new GalleryAdapter(MyActivity.this, R.layout.gallery_item, imageItems);
            gridView.setAdapter(gridAdapter);

            // Setting new scroll position
            gridView.setSelection(currentPosition + 1);

            loadingMore = false;   // set it to false, so that we can load next set of images

        }
    }

    // bad hashtag was entered, so display the default page
    private void badHashTagSetDefault() {

        tag = "spring";

        new getPhotosData().execute();
    }

}

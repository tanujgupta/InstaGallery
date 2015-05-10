package com.tanujgupta.bellytest.instagallery.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.loopj.android.image.SmartImageView;
import com.tanujgupta.bellytest.instagallery.R;
import com.tanujgupta.bellytest.instagallery.model.ImageItem;
import java.util.ArrayList;

public class GalleryAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

    public GalleryAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.gallery_item, parent, false);

            holder = new ViewHolder();
            holder.image = (SmartImageView) row.findViewById(R.id.image);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        String imagrUrl = data.get(position).getUrlThumbnail();
        holder.image.setImageUrl(imagrUrl);

        return row;
    }

    static class ViewHolder {
        SmartImageView image;
    }


}

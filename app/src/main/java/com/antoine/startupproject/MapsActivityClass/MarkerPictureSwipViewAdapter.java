package com.antoine.startupproject.MapsActivityClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.antoine.startupproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Antoine on 04/01/2016.
 */
public class MarkerPictureSwipViewAdapter extends PagerAdapter {

    private ArrayList<String> images;
    private Context context;
    private LayoutInflater layoutInflater;
    private static final String GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=";
    private static final String PLACES_API_KEY = "AIzaSyBDa-0x-M16wB4J_1-hNOH66JvU1QJeCX4";
    private boolean isNewMarker;

    public MarkerPictureSwipViewAdapter(Context context, ArrayList<String> images){

        this.context = context;
        this.images = images;
        this.isNewMarker = true;

    }



    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.marker_pictures_swip_view, container, false);


        final ImageView img = (ImageView) item_view.findViewById(R.id.markerPictureSwipView);
        Picasso.with(context)
                .load(images.get(position))
                .placeholder(R.drawable.ic_photo_camera_black_48dp)
                .error(R.drawable.ic_photo_camera_black_48dp)
                .fit()
                .into(img);


        container.addView(item_view);

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);


    }
}

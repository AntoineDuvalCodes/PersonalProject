package com.antoine.startupproject.MapsActivityClass;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.startupproject.MyPlacesActivity.MarkerInfos;
import com.antoine.startupproject.R;

import java.util.ArrayList;

/**
 * Created by Antoine on 19/01/2016.
 */
public class RecyclerViewTextSearch extends RecyclerView.Adapter<RecyclerViewTextSearch.RecyclerViewTextSearchViewHolder>{

    private ArrayList<MarkerInfos> markerList;
    private Context context;
    private Location location;

    public RecyclerViewTextSearch(Context context, ArrayList<MarkerInfos> markerList, Location location){

        this.markerList = markerList;
        this.context = context;
        this.location = location;
    }

    @Override
    public RecyclerViewTextSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_text_search, parent, false);
        RecyclerViewTextSearchViewHolder viewHolder = new RecyclerViewTextSearchViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewTextSearchViewHolder holder, int position) {

        holder.tvNamePlaceTextSearch.setText(markerList.get(position).getName());
        holder.tvVicinityPlaceTextSearch.setText(markerList.get(position).getVicinity());
        holder.tvDistancePlaceTextSearch.setText(String.valueOf(markerList.get(position).getDistance()/ 1000 + " km"));



    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }

    public class RecyclerViewTextSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvNamePlaceTextSearch,tvVicinityPlaceTextSearch,tvDistancePlaceTextSearch;

        public RecyclerViewTextSearchViewHolder(View itemView) {
            super(itemView);

            tvNamePlaceTextSearch = (TextView) itemView.findViewById(R.id.tvNamePlaceTextSearch);
            tvVicinityPlaceTextSearch = (TextView) itemView.findViewById(R.id.tvVicinityPlaceTextSearch);
            tvDistancePlaceTextSearch = (TextView) itemView.findViewById(R.id.tvDistancePlaceTextSearch);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


            Intent intent = new Intent(context, PlacesDetailsActivity.class);
            intent.putExtra("marker", markerList.get(getPosition()));
            intent.putExtra("location", location);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }
    }

}

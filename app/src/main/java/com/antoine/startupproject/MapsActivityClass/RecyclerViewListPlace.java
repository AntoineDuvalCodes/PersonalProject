package com.antoine.startupproject.MapsActivityClass;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoine.startupproject.MyPlacesActivity.MarkerInfos;
import com.antoine.startupproject.R;

import java.util.ArrayList;

/**
 * Created by Antoine on 20/01/2016.
 */
public class RecyclerViewListPlace extends RecyclerView.Adapter<RecyclerViewListPlace.RecyclerViewListPlaceViewHolder> {

    private ArrayList<MarkerInfos> markerList;
    private Location location;
    private Context context;


    public RecyclerViewListPlace(ArrayList<MarkerInfos> markerList, Location location, Context context){

        this.markerList = markerList;
        this.location = location;
        this.context = context;

    }

    @Override
    public RecyclerViewListPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_list_place, parent, false);
        RecyclerViewListPlaceViewHolder holder = new RecyclerViewListPlaceViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewListPlaceViewHolder holder, int position) {

        int compteur = 0;

        for (int i = 0; i< holder.ivTab.length; i++){

            holder.ivTab[i].setImageResource(android.R.color.transparent);
            compteur = 0;
        }

        for (int i = 0; i < markerList.get(position).getCategorie().length; i++) {



            switch (markerList.get(position).getCategorie()[i]){

                case "restaurant":

                    holder.ivTab[compteur].setImageResource(R.drawable.ic_restaurant_menu_black_24dp);
                    holder.ivTab[compteur].setVisibility(View.VISIBLE);

                    compteur++;

                    break;

                case"bar":

                    holder.ivTab[compteur].setImageResource(R.drawable.ic_local_bar_black_24dp);
                    holder.ivTab[compteur].setVisibility(View.VISIBLE);

                    compteur++;

                    break;

                case"night_club":

                    holder.ivTab[compteur].setImageResource(R.drawable.ic_music_note_black_24dp);
                    holder.ivTab[compteur].setVisibility(View.VISIBLE);

                    compteur++;

                    break;

                case "cafe":

                    holder.ivTab[compteur].setImageResource(R.drawable.ic_free_breakfast_black_24dp);
                    holder.ivTab[compteur].setVisibility(View.VISIBLE);

                    compteur++;

                    break;

            }


        }

        holder.tvNamePlaceTextSearch.setText(markerList.get(position).getName());
        holder.tvVicinityPlaceTextSearch.setText(markerList.get(position).getVicinity());
        holder.tvDistancePlaceTextSearch.setText(String.valueOf(markerList.get(position).getDistance() / 1000)+ " km");

    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }


    public class RecyclerViewListPlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        private TextView tvNamePlaceTextSearch,tvVicinityPlaceTextSearch,tvDistancePlaceTextSearch;
        private ImageView ivCategorieInfoWindow1,ivCategorieInfoWindow2,ivCategorieInfoWindow3,ivCategorieInfoWindow4;
        private ImageView[] ivTab;


        public RecyclerViewListPlaceViewHolder(View itemView) {
            super(itemView);

            tvNamePlaceTextSearch = (TextView) itemView.findViewById(R.id.tvNamePlaceTextSearch);
            tvVicinityPlaceTextSearch = (TextView) itemView.findViewById(R.id.tvVicinityPlaceTextSearch);
            tvDistancePlaceTextSearch = (TextView) itemView.findViewById(R.id.tvDistancePlaceTextSearch);

            ivCategorieInfoWindow1 = (ImageView) itemView.findViewById(R.id.ivCategorieInfoWindow1);
            ivCategorieInfoWindow2 = (ImageView) itemView.findViewById(R.id.ivCategorieInfoWindow2);
            ivCategorieInfoWindow3 = (ImageView) itemView.findViewById(R.id.ivCategorieInfoWindow3);
            ivCategorieInfoWindow4 = (ImageView) itemView.findViewById(R.id.ivCategorieInfoWindow4);

            ivTab = new ImageView[4];
            ivTab[0] = ivCategorieInfoWindow1;
            ivTab[1] = ivCategorieInfoWindow2;
            ivTab[2] = ivCategorieInfoWindow3;
            ivTab[3] = ivCategorieInfoWindow4;


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

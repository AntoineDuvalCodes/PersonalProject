package com.antoine.startupproject.MapsActivityClass;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.antoine.startupproject.MyPlacesActivity.MarkerInfos;
import com.antoine.startupproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Antoine on 07/01/2016.
 */
public class RecyclerViewCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private  ArrayList<Commentaire> commentaires;
    private  Context context;
    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private PlacesDetailsActivity.DataToSendToRecyclerView dataForHeader;

    public RecyclerViewCommentsAdapter(ArrayList<Commentaire> commentaires, Context context, PlacesDetailsActivity.DataToSendToRecyclerView dataForHeader){

        this.commentaires = commentaires;
        this.context = context;
        this.dataForHeader = dataForHeader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        switch (viewType){

            case HEADER:

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_details_header, parent, false);

                return  new RecyclerHeaderViewHolder(view);

            case ITEM:

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_commentaires, parent, false);

                return  new RecyclerViewCommentsViewHolder(v);

            default:

                return null;
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if(holder instanceof RecyclerHeaderViewHolder){

            RecyclerHeaderViewHolder recyclerHeaderViewHolder = (RecyclerHeaderViewHolder) holder;

            int compteur = 0;

            for (int i = 0; i < dataForHeader.categories.length; i++) {


                switch (dataForHeader.categories[i]){

                    case "restaurant":

                        recyclerHeaderViewHolder.ivTab[compteur].setImageResource(R.drawable.ic_restaurant_menu_black_24dp);
                        recyclerHeaderViewHolder.ivTab[compteur].setVisibility(View.VISIBLE);

                        compteur++;

                        break;

                    case"bar":

                        recyclerHeaderViewHolder.ivTab[compteur].setImageResource(R.drawable.ic_local_bar_black_24dp);
                        recyclerHeaderViewHolder.ivTab[compteur].setVisibility(View.VISIBLE);

                        compteur++;

                        break;

                    case"night_club":

                        recyclerHeaderViewHolder.ivTab[compteur].setImageResource(R.drawable.ic_music_note_black_24dp);
                        recyclerHeaderViewHolder.ivTab[compteur].setVisibility(View.VISIBLE);

                        compteur++;

                        break;

                    case "cafe":

                        recyclerHeaderViewHolder.ivTab[compteur].setImageResource(R.drawable.ic_free_breakfast_black_24dp);
                        recyclerHeaderViewHolder.ivTab[compteur].setVisibility(View.VISIBLE);

                        compteur++;

                        break;

                }


            }

            switch (dataForHeader.isOpen){

                case "true":

                    recyclerHeaderViewHolder.bIsOpen.setVisibility(View.VISIBLE);

                    recyclerHeaderViewHolder.bIsOpen.setBackgroundColor(context.getResources().getColor(R.color.green));
                    recyclerHeaderViewHolder.bIsOpen.setText("Ouvert");

                    break;

                case "false":

                    recyclerHeaderViewHolder.bIsOpen.setVisibility(View.VISIBLE);

                    recyclerHeaderViewHolder.bIsOpen.setBackgroundColor(context.getResources().getColor(R.color.red));
                    recyclerHeaderViewHolder.bIsOpen.setText("Fermé");

                    break;

                default:

                    recyclerHeaderViewHolder.bIsOpen.setVisibility(View.INVISIBLE);


                    break;

            }

            recyclerHeaderViewHolder.tvPlaceVicinity.setText(dataForHeader.vicinity);
            recyclerHeaderViewHolder.distanceToPlace.setText(String.valueOf(dataForHeader.distanceToPlace / 1000) + "km");
            recyclerHeaderViewHolder.tvPhoneOfPlace.setText(dataForHeader.phoneNumber);
            recyclerHeaderViewHolder.tvNbLikeOfPlace.setText(dataForHeader.nbLike+"");


            MarkerPictureSwipViewAdapter adapterSwip = new MarkerPictureSwipViewAdapter(context,dataForHeader.photoReferences);
            recyclerHeaderViewHolder.viewPager.setAdapter(adapterSwip);

        }else if (holder instanceof RecyclerViewCommentsViewHolder){


            RecyclerViewCommentsViewHolder recyclerViewCommentsViewHolder = (RecyclerViewCommentsViewHolder) holder;


            if (commentaires.get(position-1).getAuthorName().equals("") && commentaires.size()==1){

                recyclerViewCommentsViewHolder.tvNoCommentaire.setVisibility(View.VISIBLE);
                recyclerViewCommentsViewHolder.ivProfilePictureComment.setVisibility(View.INVISIBLE);
                recyclerViewCommentsViewHolder.ratingBarComment.setVisibility(View.INVISIBLE);
                recyclerViewCommentsViewHolder.tvCommentFromUser.setVisibility(View.INVISIBLE);
                recyclerViewCommentsViewHolder.tvAuthorName.setVisibility(View.INVISIBLE);

            }else {

                recyclerViewCommentsViewHolder.ivProfilePictureComment.setImageResource(R.drawable.ic_person_black_48dp);
                recyclerViewCommentsViewHolder.ratingBarComment.setNumStars(commentaires.get(position - 1).getNote());
                recyclerViewCommentsViewHolder.tvCommentFromUser.setText(commentaires.get(position - 1).getCommentaire());
                recyclerViewCommentsViewHolder.tvAuthorName.setText(commentaires.get(position - 1).getAuthorName());

                String profil_picture = commentaires.get(position - 1).getAuthorPictureUrl();

                if(profil_picture!= null){

                    Picasso.with(context)
                            .load(profil_picture)
                            .placeholder(R.drawable.ic_person_black_48dp)
                            .error(R.drawable.ic_person_black_48dp)
                            .fit()
                            .into(recyclerViewCommentsViewHolder.ivProfilePictureComment);

                }


                String photo_comment = commentaires.get(position - 1).getPictureUrl();



                if (profil_picture!= null){
                    recyclerViewCommentsViewHolder.ivPhotoComment.setVisibility(View.VISIBLE);


                    Picasso.with(context)
                            .load(photo_comment)
                            .placeholder(R.drawable.ic_photo_camera_black_48dp)
                            .fit()
                            .into(recyclerViewCommentsViewHolder.ivPhotoComment);

                }else{

                    //recyclerViewCommentsViewHolder.ivPhotoComment.setImageResource(R.drawable.ic_person_black_48dp);
                    recyclerViewCommentsViewHolder.ivPhotoComment.setVisibility(View.INVISIBLE);

                }
            }
        }

    }


    @Override
    public int getItemCount() {


        return commentaires.size() + 1;
    }


    @Override
    public int getItemViewType(int position) {

        switch (position){

            case 0:

                return HEADER;

            default:

                return ITEM;

        }


    }



    public class RecyclerViewCommentsViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfilePictureComment,ivPhotoComment;
        TextView tvAuthorName, tvCommentFromUser, tvNoCommentaire;
        RatingBar ratingBarComment;

        public RecyclerViewCommentsViewHolder(View itemView) {
            super(itemView);

            ivProfilePictureComment = (ImageView) itemView.findViewById(R.id.ivProfilePictureComment);
            ivPhotoComment = (ImageView) itemView.findViewById(R.id.ivPhotoComment);
            tvAuthorName = (TextView) itemView.findViewById(R.id.tvAuthorName);
            tvCommentFromUser = (TextView) itemView.findViewById(R.id.tvCommentFromUser);
            ratingBarComment = (RatingBar) itemView.findViewById(R.id.ratingBarComment);
            tvNoCommentaire = (TextView) itemView.findViewById(R.id.tvNoCommentaire);
            tvNoCommentaire.setVisibility(View.INVISIBLE);
        }
    }

    public class RecyclerHeaderViewHolder extends RecyclerView.ViewHolder{

        private ImageView[] ivTab;
        private TextView tvPlaceVicinity, distanceToPlace, tvPhoneOfPlace, tvNbLikeOfPlace;
        private ViewPager viewPager;
        private Button bIsOpen;


        public RecyclerHeaderViewHolder(View itemView) {
            super(itemView);

            ImageView ivCategorie1 = (ImageView) itemView.findViewById(R.id.ivCategorie1);
            ImageView ivCategorie2 = (ImageView) itemView.findViewById(R.id.ivCategorie2);
            ImageView ivCategorie3 = (ImageView) itemView.findViewById(R.id.ivCategorie3);
            ImageView ivCategorie4 = (ImageView) itemView.findViewById(R.id.ivCategorie4);

            ivCategorie1.setVisibility(View.INVISIBLE);
            ivCategorie2.setVisibility(View.INVISIBLE);
            ivCategorie3.setVisibility(View.INVISIBLE);
            ivCategorie4.setVisibility(View.INVISIBLE);

            ivTab = new ImageView[4];
            ivTab[0] = ivCategorie1;
            ivTab[1] = ivCategorie2;
            ivTab[2] = ivCategorie3;
            ivTab[3] = ivCategorie4;

            tvPlaceVicinity = (TextView) itemView.findViewById(R.id.tvPlaceVicinity);
            distanceToPlace = (TextView) itemView.findViewById(R.id.distanceToPlace);
            tvPhoneOfPlace = (TextView) itemView.findViewById(R.id.tvPhoneOfPlace);
            tvNbLikeOfPlace = (TextView) itemView.findViewById(R.id.tvNbLikeOfPlace);
            bIsOpen = (Button) itemView.findViewById(R.id.bIsOpen);

            viewPager = (ViewPager) itemView.findViewById(R.id.markerPictureViewPager);

        }
    }
}

package com.nerdgeeks.foodmap.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.model.PlaceModel;
import com.nerdgeeks.foodmap.view.OnItemClickListener;
import com.squareup.picasso.Picasso;

public class GMapsAdapterWithAds extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<PlaceModel> mPosts;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private boolean isEnabled;
    private int lastPosition = -1;

    private int POST_TYPE = 1;
    private int AD_TYPE = 2;

    public GMapsAdapterWithAds(ArrayList<PlaceModel> posts, Context context) {
        mPosts = posts;
        mContext = context;
    }

    private class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tName;
        private TextView tVicnity;
        private TextView tRate;
        private TextView tOpen;
        private OnItemClickListener onItemClickListener;
        private CardView cardView;
        private ImageView iconView;
        private RatingBar ratingBar;
        private PlaceModel mPost;

        private PostHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            this.onItemClickListener = onItemClickListener;

            cardView = itemView.findViewById(R.id.card);
            tName = itemView.findViewById(R.id.nName);
            tVicnity = itemView.findViewById(R.id.nVicnity);
            tRate = itemView.findViewById(R.id.nRate);
            tOpen = itemView.findViewById(R.id.nOpen);
            iconView = itemView.findViewById(R.id.icon);
            ratingBar = itemView.findViewById(R.id.rate);
        }

        private void bindView(PlaceModel post, Context mContext, int index) {
            mPost = post;

            //adding custom font
            final Typeface ThemeFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/HelveticaNeue.ttf");
            tName.setTypeface(ThemeFont);
            tVicnity.setTypeface(ThemeFont);
            tRate.setTypeface(ThemeFont);
            tOpen.setTypeface(ThemeFont);

            String ratings = ""+mPost.getRating().toString();

            if (ratings.equals("0.0")) {
                ratingBar.setRating(0);
                tRate.setText("N/A");
            } else {
                ratingBar.setRating(Float.parseFloat(ratings));
                tRate.setText(ratings);
            }

            try {
                if (mPost.getOpeningHours().getOpenNow()) {
                    tOpen.setText("OPEN");
                } else {
                    tOpen.setText("Closed");
                }
            } catch (Exception e){
                tOpen.setText("N/A");
            }

            // Get the menu item image resource ID.
            tName.setText(mPost.getName());
            tVicnity.setText(mPost.getVicinity());

            Picasso.get().load(mPost.getIcon()).into(iconView);

            cardView.setCardBackgroundColor(Color.WHITE);

            if (isEnabled){
                cardView.setOnClickListener(view -> onItemClickListener.onClick(view, index));
            }

            setAnimation(itemView, index);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(v, getAdapterPosition());
        }
    }

    public void isOnItemClickListener(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private static class AdHolder extends RecyclerView.ViewHolder {
        private AdView mAdView;

        private AdHolder(View view) {
            super(view);
            mAdView = view.findViewById(R.id.adView);
        }

        private void bindView(int position) {

            if (position == 0) {
                mAdView.setVisibility(View.GONE);
                return;
            }

            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    mAdView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    mAdView.setVisibility(View.VISIBLE);
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 7 == 0) {
            return AD_TYPE;
        } else {
            return POST_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View inflatedView;
            inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ad_unit, parent, false);
            return new AdHolder(inflatedView);
        }
        else {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new PostHolder(inflatedView, onItemClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == AD_TYPE) {
            ((AdHolder)holder).bindView(position);
        }
        else {
            int index = position;
            if (index != 0) {
                index--;
            }
            PlaceModel post = mPosts.get(index);
            ((PostHolder)holder).bindView(post,mContext,index);
        }
    }

    @Override
    public int getItemCount() {
        if (mPosts.size() == 0) {
            return mPosts.size();
        }
        else {
            return mPosts.size()+1;
        }
    }
}

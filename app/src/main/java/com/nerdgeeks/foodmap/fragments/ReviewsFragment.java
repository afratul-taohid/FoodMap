package com.nerdgeeks.foodmap.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nerdgeeks.foodmap.R;
import com.nerdgeeks.foodmap.adapter.ReviewsAdapter;
import com.nerdgeeks.foodmap.app.AppData;
import com.nerdgeeks.foodmap.model.Review;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ReviewsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private TextView mTextView;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance() {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        mRecyclerView = rootView.findViewById(R.id.rView);
        mTextView = rootView.findViewById(R.id.notifier);
        mTextView.setVisibility(View.INVISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
        mRecyclerView.setHasFixedSize(true);

        loadReviewInformation();

        return rootView;
    }

    private void loadReviewInformation() {

        ArrayList<Review> reviews = AppData.placeDetails.getReviews();

        if (reviews != null){
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews, getActivity());
            reviewsAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(reviewsAdapter);
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText("No Reviews Found");
        }

    }
}

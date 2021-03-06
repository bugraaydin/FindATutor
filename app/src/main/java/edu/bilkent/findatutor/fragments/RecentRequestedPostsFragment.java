package edu.bilkent.findatutor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;

import edu.bilkent.findatutor.ChatActivity;
import edu.bilkent.findatutor.PostDetailActivity;
import edu.bilkent.findatutor.R;
import edu.bilkent.findatutor.misc.CircleTransform;
import edu.bilkent.findatutor.model.Post;
import edu.bilkent.findatutor.viewholders.RequestedPostViewHolder;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class RecentRequestedPostsFragment extends Fragment {

    private static final String TAG = "PostListFragment";
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddmmss");

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Post, RequestedPostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public RecentRequestedPostsFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.posts_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Post, RequestedPostViewHolder>(Post.class, R.layout.item_requested_post,
                RequestedPostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final RequestedPostViewHolder viewHolder, final Post model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Launch ChatActivity
                        //Intent intent = new Intent(getActivity(), ChatActivity.class);
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_POST_KEY, postKey);
                        intent.putExtra(ChatActivity.EXTRA_POST_TITLE, model.title);
                        intent.putExtra(ChatActivity.EXTRA_POST_USER, getUid());
                        intent.putExtra(ChatActivity.EXTRA_POST_AUTHOR, model.authorUID);
                        intent.putExtra("photoURL", model.authorPhotoUrl);
                        intent.putExtra("price", model.price);
                        intent.putExtra("language", model.language);
                        intent.putExtra("school", model.school);
                        intent.putExtra("author", model.authorName);
                        startActivity(intent);
                    }
                });


                // Bind Post to ViewHolder,
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
//                        // Neeed to write to both places the post is stored
//                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
//                        DatabaseReference userPostRef = mDatabase.child("modelUser-posts").child(model.authorUID).child(postRef.getKey());
                    }
                });

                String url = model.authorPhotoUrl;
                Glide
                        .with(RecentRequestedPostsFragment.this)
                        .load(url)
                        .transform(new CircleTransform(getContext()))
                        .into(viewHolder.imageView);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public String getName() {
        String email = getInstance().getCurrentUser().getEmail();
        return usernameFromEmail(email);
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    public Query getQuery(DatabaseReference databaseReference){
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        return databaseReference.child("posts").orderByChild("isRequested").equalTo(true)
                .limitToFirst(100);
    }

}
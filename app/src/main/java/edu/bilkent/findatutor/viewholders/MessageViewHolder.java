package edu.bilkent.findatutor.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.bilkent.findatutor.R;
import edu.bilkent.findatutor.model.Chat;

/**
 * Created by linus on 09.07.2016.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {


    public TextView titleView;
    public TextView senderView;
    public ImageView imageView;



    public MessageViewHolder(View itemView) {
        super(itemView);
        titleView = (TextView) itemView.findViewById(R.id.post_school);
        senderView = (TextView) itemView.findViewById(R.id.post_author);
        imageView = (ImageView) itemView.findViewById((R.id.post_author_photo));
    }


    public void bindToPost(Chat chat, View.OnClickListener clickListener) {
        titleView.setText(chat.getTitle());
        senderView.setText(chat.getSender());
    }
}

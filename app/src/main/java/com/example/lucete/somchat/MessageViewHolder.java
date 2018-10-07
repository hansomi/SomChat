package com.example.lucete.somchat;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.appindexing.Action;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView messageTextView;
    ImageView messageImageView;
    TextView messengerTextView;
    TextView messageSendTime;
    CircleImageView messengerImageView;
    LinearLayout container;

    public MessageViewHolder(View v) {
        super(v);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        messageSendTime = (TextView) itemView.findViewById(R.id.messageSendTime);
        messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        container = (LinearLayout) itemView.findViewById(R.id.container);
    }

    public void setData(ChatMessage chatMessage) {
        if (chatMessage.getText() != null) {
            messageTextView.setText(chatMessage.getText());
            messageTextView.setVisibility(TextView.VISIBLE);
            messageImageView.setVisibility(ImageView.GONE);
        } else if (chatMessage.getImageUrl() != null) {
            String imageUrl = chatMessage.getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(messageImageView.getContext())
                                            .load(downloadUrl)
                                            .into(messageImageView);
                                } else {
                                    Log.w("MesssageViewHolder", "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Glide.with(messageImageView.getContext())
                        .load(chatMessage.getImageUrl())
                        .into(messageImageView);
            }
            messageImageView.setVisibility(ImageView.VISIBLE);
            messageTextView.setVisibility(TextView.GONE);
        }


        messengerTextView.setText(chatMessage.getName());
        if (chatMessage.getPhotoUrl() == null) {
            messengerImageView.setImageDrawable(ContextCompat.getDrawable(messageImageView.getContext(),
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(messageImageView.getContext())
                    .load(chatMessage.getPhotoUrl())
                    .into(messengerImageView);
        }

        messageSendTime.setText(MyUtils.convertTime(chatMessage.getTime()));

    }
}
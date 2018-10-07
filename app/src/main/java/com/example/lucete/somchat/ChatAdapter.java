package com.example.lucete.somchat;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChatAdapter extends FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> {
    private static final String TAG = "[ChatAdapter]";

    public ChatAdapter(FirebaseRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        Log.d(TAG, "onCreateViewHolder");

        return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                    int position,
                                    ChatMessage chatMessage) {

        viewHolder.setData(chatMessage);

        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) viewHolder.container.getLayoutParams();

        if(chatMessage.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            Log.d(TAG, "chatMessage.getid="+chatMessage.getId());
            Log.d(TAG, "FirebaseAuth.getInstance().getCurrentUser().getUid()="+FirebaseAuth.getInstance().getCurrentUser().getUid());

            layoutParams.gravity = Gravity.RIGHT;
        }else{
            layoutParams.gravity = Gravity.LEFT;
        }

        if (chatMessage.getText() != null) {
            viewHolder.messageTextView.setText(chatMessage.getText());
            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
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
                                    Glide.with(viewHolder.messageImageView.getContext())
                                            .load(downloadUrl)
                                            .into(viewHolder.messageImageView);
                                } else {
                                    Log.w(TAG, "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Glide.with(viewHolder.messageImageView.getContext())
                        .load(chatMessage.getImageUrl())
                        .into(viewHolder.messageImageView);
            }
            viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
            viewHolder.messageTextView.setVisibility(TextView.GONE);
        }
        viewHolder.messengerTextView.setText(chatMessage.getName());
        if (chatMessage.getPhotoUrl() == null) {
            viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(viewHolder.messageImageView.getContext(),
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(viewHolder.messageImageView.getContext())
                    .load(chatMessage.getPhotoUrl())
                    .into(viewHolder.messengerImageView);
        }

        viewHolder.messageSendTime.setText(MyUtils.convertTime(chatMessage.getTime()));

    }
}

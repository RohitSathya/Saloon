package com.example.haircut_admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChatAdapterAdmin extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<ChatMessage> chatList;

    public ChatAdapterAdmin(Context context, List<ChatMessage> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatList.get(position);
        if (message != null && "Admin".equals(message.getName())) {
            return VIEW_TYPE_RECEIVED;
        } else {
            return VIEW_TYPE_SENT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView mediaImageView;
        VideoView mediaVideoView;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_view_message);
            mediaImageView = itemView.findViewById(R.id.image_view_media);
            mediaVideoView = itemView.findViewById(R.id.video_view_media);
        }

        void bind(ChatMessage message) {
            if (message != null) {
                if ("text".equals(message.getMediaType())) {
                    messageTextView.setText(message.getMessage());
                    messageTextView.setVisibility(View.VISIBLE);
                    mediaImageView.setVisibility(View.GONE);
                    mediaVideoView.setVisibility(View.GONE);
                } else if ("image".equals(message.getMediaType())) {
                    mediaImageView.setVisibility(View.VISIBLE);
                    Glide.with(mediaImageView.getContext()).load(message.getMediaUrl()).into(mediaImageView);
                    messageTextView.setVisibility(View.GONE);
                    mediaVideoView.setVisibility(View.GONE);
                    mediaImageView.setOnClickListener(v -> openFullScreenMedia(message.getMediaUrl(), "image"));
                } else if ("video".equals(message.getMediaType())) {
                    mediaVideoView.setVisibility(View.VISIBLE);
                    mediaVideoView.setVideoURI(Uri.parse(message.getMediaUrl()));
                    mediaVideoView.start();
                    messageTextView.setVisibility(View.GONE);
                    mediaImageView.setVisibility(View.GONE);
                    mediaVideoView.setOnClickListener(v -> openFullScreenMedia(message.getMediaUrl(), "video"));
                }
            }
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView mediaImageView;
        VideoView mediaVideoView;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_view_message);
            mediaImageView = itemView.findViewById(R.id.image_view_media);
            mediaVideoView = itemView.findViewById(R.id.video_view_media);
        }

        void bind(ChatMessage message) {
            if (message != null) {
                if ("text".equals(message.getMediaType())) {
                    messageTextView.setText(message.getMessage());
                    messageTextView.setVisibility(View.VISIBLE);
                    mediaImageView.setVisibility(View.GONE);
                    mediaVideoView.setVisibility(View.GONE);
                } else if ("image".equals(message.getMediaType())) {
                    mediaImageView.setVisibility(View.VISIBLE);
                    Glide.with(mediaImageView.getContext()).load(message.getMediaUrl()).into(mediaImageView);
                    messageTextView.setVisibility(View.GONE);
                    mediaVideoView.setVisibility(View.GONE);
                    mediaImageView.setOnClickListener(v -> openFullScreenMedia(message.getMediaUrl(), "image"));
                } else if ("video".equals(message.getMediaType())) {
                    mediaVideoView.setVisibility(View.VISIBLE);
                    mediaVideoView.setVideoURI(Uri.parse(message.getMediaUrl()));
                    mediaVideoView.start();
                    messageTextView.setVisibility(View.GONE);
                    mediaImageView.setVisibility(View.GONE);
                    mediaVideoView.setOnClickListener(v -> openFullScreenMedia(message.getMediaUrl(), "video"));
                }
            }
        }
    }

    private void openFullScreenMedia(String mediaUrl, String mediaType) {
        Intent intent = new Intent(context, FullScreenMediaActivity.class);
        intent.putExtra("mediaUrl", mediaUrl);
        intent.putExtra("mediaType", mediaType);
        context.startActivity(intent);
    }
}

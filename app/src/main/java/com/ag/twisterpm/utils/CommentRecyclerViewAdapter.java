package com.ag.twisterpm.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ag.twisterpm.R;
import com.ag.twisterpm.models.Comment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {

    @SuppressWarnings("FieldMayBeFinal")
    private List<Comment> data;
    private String twistUsername;
    private final LayoutInflater inflater;
    private ItemClickListener clickListener;

    public CommentRecyclerViewAdapter(Context context, List<Comment> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.twistUsername = twistUsername;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comment_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = data.get(position);
        holder.commentUsername.setText(comment.getUser());
        holder.commentContent.setText(comment.getContent());

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(comment.getUser())) {
            holder.commentDeleteBTN.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Comment getItem(int id) {
        return data.get(id);
    }

    public void SetClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView commentContent;
        final TextView commentUsername;
        public final ImageButton commentDeleteBTN;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentContent = itemView.findViewById(R.id.commentContent);
            commentUsername = itemView.findViewById(R.id.commentUsername);
            commentDeleteBTN = itemView.findViewById(R.id.commentDeleteBTN);
            commentDeleteBTN.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
        }
    }
}

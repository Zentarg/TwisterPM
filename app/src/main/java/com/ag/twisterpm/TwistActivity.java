package com.ag.twisterpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.twisterpm.models.Comment;
import com.ag.twisterpm.models.Twist;
import com.ag.twisterpm.services.TwisterService;
import com.ag.twisterpm.utils.CommentRecyclerViewAdapter;
import com.ag.twisterpm.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class TwistActivity extends AppCompatActivity {

    private Twist twist;

    private RecyclerView commentRecyclerView;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout progress;

    private List<Comment> allComments;

    private EditText commentText;
    private Button commentBTN;
    private ImageButton postDeleteBTN;
    private TextView commentLength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twist);
        progress = findViewById(R.id.progress);

        commentText = findViewById(R.id.newCommentText);
        commentBTN = findViewById(R.id.newCommentBTN);

        postDeleteBTN = findViewById(R.id.postDeleteBTN);

        commentLength = findViewById(R.id.commentLength);

        refreshLayout = findViewById(R.id.commentRefreshLayout);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        refreshLayout.setOnRefreshListener(() -> {
            GetAndShowAllComments();
        });

        DisplayTwist();

        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                setCommentLength(s.length());
                if (s.length() > 0) {
                    if (commentBTN.getVisibility() == View.GONE)
                        commentBTN.setVisibility(View.VISIBLE);
                } else {
                    if (commentBTN.getVisibility() == View.VISIBLE)
                        commentBTN.setVisibility(View.GONE);
                }
            }
        });

        commentBTN.setOnClickListener(v -> NewComment());

        postDeleteBTN.setOnClickListener(v -> deletePost());

    }

    private void setCommentLength(int length) {
        commentLength.setText(length + "/255");
    }

    private void deletePost() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TwisterService service = retrofit.create(TwisterService.class);

        Call<Twist> twistCall = service.deleteTwist(twist.getId().toString());
        twistCall.enqueue(new Callback<Twist>() {
            @Override
            public void onResponse(Call<Twist> call, Response<Twist> response) {
                Log.d(Constants.LOGTAG, response.raw().toString());
                if (response.isSuccessful()) {
                    Toast.makeText(TwistActivity.this, "Successfully Deleted Twist", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    TwistActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    OnDoneLoading();
                } else {
                    Log.d(Constants.LOGTAG, "Problem " + response.code() + " " + response.message());
                    Toast.makeText(TwistActivity.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Twist> call, Throwable t) {
                Log.e(Constants.LOGTAG, t.getMessage());
                Toast.makeText(TwistActivity.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteComment(Comment comment) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TwisterService service = retrofit.create(TwisterService.class);

        Call<Comment> twistCall = service.deleteComment(twist.getId().toString(), comment.getId().toString());
        twistCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.d(Constants.LOGTAG, response.body().toString());
                if (response.isSuccessful()) {
                    Toast.makeText(TwistActivity.this, "Successfully Deleted Comment", Toast.LENGTH_SHORT).show();
                    allComments.remove(comment);
                    PopulateRecyclerView(allComments);
                } else {
                    Log.d(Constants.LOGTAG, "Problem " + response.code() + " " + response.message());
                    Toast.makeText(TwistActivity.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e(Constants.LOGTAG, t.getMessage());
                Toast.makeText(TwistActivity.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allComments == null) {
            OnStartLoading();
            GetAndShowAllComments();
        }
    }


    private void NewComment() {
        String text = commentText.getText().toString();

        if (text.length() == 0) {
            commentText.setError("Please write more than 0 characters.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TwisterService service = retrofit.create(TwisterService.class);

        Comment comment = new Comment(0, twist.getId(), text, FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Call<Comment> postCommentCall = service.postComment(twist.getId().toString(), comment);

        postCommentCall.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.e(Constants.LOGTAG, call.request().toString());
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Successfully posted Comment.", Toast.LENGTH_SHORT).show();
                    OnCommentPosted(response.body());
                } else {
                    Log.d(Constants.LOGTAG, "Problem occurred: " + response.code() + " " + response.message());
                }

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

                Log.e(Constants.LOGTAG, t.getCause().getMessage());
                Log.e(Constants.LOGTAG, call.request().toString());
                Toast.makeText(getApplicationContext(), "An error occurred while posting Comment. Please try again later..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void OnCommentPosted(Comment newComment) {
        commentText.setText("");
        commentBTN.setVisibility(View.GONE);
        allComments.add(newComment);
        PopulateRecyclerView(allComments);

    }


    private void OnStartLoading() {
        progress.setVisibility(View.VISIBLE);
    }

    private void OnDoneLoading() {
        refreshLayout.setRefreshing(false);
        findViewById(R.id.progress).setVisibility(View.GONE);
        Log.i(Constants.LOGTAG, "Done Loading");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(Constants.STATE_COMMENTS, new ArrayList(allComments));

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        allComments = (List<Comment>) savedInstanceState.getSerializable(Constants.STATE_COMMENTS);
        PopulateRecyclerView(allComments);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void DisplayTwist() {
        Intent intent = getIntent();
        twist = (Twist) intent.getSerializableExtra(Constants.INTENT_TWIST);

        TextView twistUsername = findViewById(R.id.postUsername);
        TextView twistCommentCount = findViewById(R.id.postCommentCount);
        TextView twistContent = findViewById(R.id.postContent);

        twistUsername.setText(twist.getUser());
        twistCommentCount.setText(twist.getTotalComments() == 1 ?twist.getTotalComments() + " Comment" : twist.getTotalComments() + " Comments");
        twistContent.setText(twist.getContent());

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(twist.getUser())) {
            postDeleteBTN.setVisibility(View.VISIBLE);
        }

    }

    private void GetAndShowAllComments() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TwisterService service = retrofit.create(TwisterService.class);

        Call<List<Comment>> commentCall = service.getAllComments(twist.getId().toString());
        commentCall.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                Log.d(Constants.LOGTAG, response.raw().toString());
                Log.d(Constants.LOGTAG, response.body().toString());
                if (response.isSuccessful()) {
                    allComments = response.body();
                    PopulateRecyclerView(allComments);
                    OnDoneLoading();
                } else {
                    Log.d(Constants.LOGTAG, "Problem " + response.code() + " " + response.message());
                    Toast.makeText(TwistActivity.this, "Could not receive comments, please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e(Constants.LOGTAG, t.getMessage());
                Toast.makeText(TwistActivity.this, "Could not receive comments, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PopulateRecyclerView(List<Comment> comments) {
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommentRecyclerViewAdapter commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(this, comments);
        commentRecyclerViewAdapter.SetClickListener((View v, int position) -> {
            deleteComment(commentRecyclerViewAdapter.getItem(position));
        });

        commentRecyclerView.setAdapter(commentRecyclerViewAdapter);

    }

}
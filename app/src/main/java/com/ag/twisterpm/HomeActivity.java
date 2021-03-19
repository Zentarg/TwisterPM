package com.ag.twisterpm;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ag.twisterpm.models.Twist;
import com.ag.twisterpm.services.TwisterService;
import com.ag.twisterpm.utils.Constants;
import com.ag.twisterpm.utils.TwistRecyclerViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView twistersRecyclerView;
    private TwistRecyclerViewAdapter twistRecyclerViewAdapter;
    private LinearLayout progress;

    private List<Twist> allTwisters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progress = findViewById(R.id.progress);
        refreshLayout = findViewById(R.id.twistersRefreshLayout);
        twistersRecyclerView = findViewById(R.id.twistersRecyclerView);
        refreshLayout.setOnRefreshListener(() -> {
                GetAndShowAllTwisters();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allTwisters == null) {
            OnStartLoading();
            GetAndShowAllTwisters();
        }
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
        outState.putSerializable(Constants.STATE_TWISTERS, new ArrayList(allTwisters));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        allTwisters = (List<Twist>) savedInstanceState.getSerializable(Constants.STATE_TWISTERS);
        PopulateRecyclerView(allTwisters);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void GetAndShowAllTwisters() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TwisterService service = retrofit.create(TwisterService.class);

        Call<List<Twist>> twistCall = service.getAllTwists();
        twistCall.enqueue(new Callback<List<Twist>>() {
            @Override
            public void onResponse(Call<List<Twist>> call, Response<List<Twist>> response) {
                Log.d(Constants.LOGTAG, response.raw().toString());
                if (response.isSuccessful()) {
                    allTwisters = response.body();
                    PopulateRecyclerView(allTwisters);
                    OnDoneLoading();
                } else {
                    Log.d(Constants.LOGTAG, "Problem " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Twist>> call, Throwable t) {
                Log.e(Constants.LOGTAG, t.getMessage());
            }
        });

    }



    private void PopulateRecyclerView(List<Twist> twists) {
        twistersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        twistRecyclerViewAdapter = new TwistRecyclerViewAdapter(this, twists);
        twistRecyclerViewAdapter.SetClickListener((View view, int position) -> {
        Twist twist = twistRecyclerViewAdapter.getItem(position);
            Log.i(Constants.LOGTAG, "Clicked " + position);
            Intent intent = new Intent(this, TwistActivity.class);
            intent.putExtra(Constants.INTENT_TWIST, (Serializable) twist);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, "homeToTwist");
            startActivity(intent, options.toBundle());
        });
        twistersRecyclerView.setAdapter(twistRecyclerViewAdapter);
    }

}
package com.ag.twisterpm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.twisterpm.R;
import com.ag.twisterpm.TwistActivity;
import com.ag.twisterpm.models.Twist;
import com.ag.twisterpm.services.TwisterService;
import com.ag.twisterpm.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewTwistActivity extends AppCompatActivity {

    private EditText twistBody;
    private ImageButton profilePicture;
    private Button postTwistButton;
    private ImageButton cancelNewTwist;
    private TextView twistLength;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_twist);

        twistBody = findViewById(R.id.twistBody);
        profilePicture = findViewById(R.id.newTwistProfilePicture);
        postTwistButton = findViewById(R.id.postTwistBTN);
        cancelNewTwist = findViewById(R.id.cancelNewTwist);

        twistLength = findViewById(R.id.twistLength);

        postTwistButton.setOnClickListener((View v) -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.APIURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TwisterService service = retrofit.create(TwisterService.class);

            Twist newTwist = new Twist(0, twistBody.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), 0);

            Call<Twist> postTwistCall = service.postTwist(newTwist);



            postTwistCall.enqueue(new Callback<Twist>() {
                @Override
                public void onResponse(Call<Twist> call, Response<Twist> response) {
                    Log.e(Constants.LOGTAG, call.request().toString());
                    Log.i(Constants.LOGTAG, response.body().toString());
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Successfully created twist.", Toast.LENGTH_SHORT).show();
                        TwistIntent(response.body());
                    } else {
                        Log.d(Constants.LOGTAG, "Problem occurred: " + response.code() + " " + response.message());
                    }

                }

                @Override
                public void onFailure(Call<Twist> call, Throwable t) {

                    Log.e(Constants.LOGTAG, t.getCause().getMessage());
                    Log.e(Constants.LOGTAG, call.request().toString());
                    Toast.makeText(getApplicationContext(), "An error occurred while posting Twist. Please try again later..", Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelNewTwist.setOnClickListener(v -> {
            this.onBackPressed();
        });

        twistBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                setTwistLength(s.length());
            }
        });

    }

    private void setTwistLength(int length) {
        twistLength.setText(length + "/255");
    }

    private void TwistIntent(Twist twist) {

        Intent intent = new Intent(this, TwistActivity.class);
        intent.putExtra(Constants.INTENT_TWIST, (Serializable) twist);
        ActivityOptions o = ActivityOptions.makeSceneTransitionAnimation(this, twistBody, "newTwistText");
        startActivity(intent, o.toBundle());
    }

}
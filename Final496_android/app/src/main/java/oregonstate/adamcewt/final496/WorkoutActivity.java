package oregonstate.adamcewt.final496;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.FormBody;
import okhttp3.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static oregonstate.adamcewt.final496.R.id.warmup;

public class WorkoutActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient mOkHttpClient;

    public WorkoutActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        GoogleSignInResult result = opr.get();
        final GoogleSignInAccount acct = result.getSignInAccount();


        mOkHttpClient = new OkHttpClient();

        HttpUrl reqUrl = HttpUrl.parse("https://final496-161720.appspot.com/workouts?userId=" + acct.getId());
        final Request userDataReq = new Request.Builder()
                .url(reqUrl)
                .build();

        mOkHttpClient.newCall(userDataReq).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String r = response.body().string();

                try {
                    JSONArray workouts = new JSONArray(r);
                    List<Map<String,String>> workoutsH = new ArrayList<Map<String,String>>();
                    for(int i = 0; i < workouts.length(); i++){
                        JSONObject jsonobject = workouts.getJSONObject(i);
                        HashMap<String, String> m = new HashMap<String, String>();
                        m.put("warmup", jsonobject.getString("warmup"));
                        m.put("first", jsonobject.getString("first"));
                        m.put("second", jsonobject.getString("second"));
                        m.put("third", jsonobject.getString("third"));
                        m.put("cooldown", jsonobject.getString("cooldown"));
                        m.put("id", jsonobject.getString("id"));
                        workoutsH.add(m);
                    }

                    final SimpleAdapter postAdapter = new SimpleAdapter(
                            WorkoutActivity.this,
                            workoutsH,
                            R.layout.my_list_layout,
                            new String[]{"id", "warmup","first", "second", "third", "cooldown"},
                            new int[]{R.id.workoutId, warmup, R.id.first, R.id.second, R.id.third, R.id.cooldown})
                    {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);

                            Button b = (Button) v.findViewById(R.id.delete_button);
                            b.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    String wId = ((TextView) findViewById(R.id.workoutId)).getText().toString();

                                    HttpUrl delUrl = HttpUrl.parse("https://final496-161720.appspot.com/workouts/" + wId);
                                    final Request userDelReq = new Request.Builder()
                                            .url(delUrl)
                                            .delete()
                                            .build();

                                    mOkHttpClient.newCall(userDelReq).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {

                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(getIntent());
                                            overridePendingTransition(0, 0);
                                        }

                                    });
                                }
                            });

                            Button b2 = (Button) v.findViewById(R.id.edit_button);
                            b2.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    ((EditText) findViewById(R.id.new_warmup)).setText(((TextView) findViewById(R.id.warmup)).getText());
                                    (findViewById(R.id.warmup)).setVisibility(GONE);
                                    ((EditText) findViewById(R.id.new_warmup)).setVisibility(VISIBLE);

                                    ((EditText) findViewById(R.id.new_first)).setText(((TextView) findViewById(R.id.first)).getText());
                                    (findViewById(R.id.first)).setVisibility(GONE);
                                    ((EditText) findViewById(R.id.new_first)).setVisibility(VISIBLE);

                                    ((EditText) findViewById(R.id.new_second)).setText(((TextView) findViewById(R.id.second)).getText());
                                    (findViewById(R.id.second)).setVisibility(GONE);
                                    ((EditText) findViewById(R.id.new_second)).setVisibility(VISIBLE);

                                    ((EditText) findViewById(R.id.new_third)).setText(((TextView) findViewById(R.id.third)).getText());
                                    (findViewById(R.id.third)).setVisibility(GONE);
                                    ((EditText) findViewById(R.id.new_third)).setVisibility(VISIBLE);

                                    ((EditText) findViewById(R.id.new_cooldown)).setText(((TextView) findViewById(R.id.cooldown)).getText());
                                    (findViewById(R.id.cooldown)).setVisibility(GONE);
                                    ((EditText) findViewById(R.id.new_cooldown)).setVisibility(VISIBLE);

                                    (findViewById(R.id.edit_button)).setVisibility(GONE);
                                    (findViewById(R.id.save_button)).setVisibility(VISIBLE);

                                }
                            });

                            Button b3 = (Button) v.findViewById(R.id.save_button);
                            b3.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    String wId = ((TextView) findViewById(R.id.workoutId)).getText().toString();
                                    String newWarmup = ((EditText) findViewById(R.id.new_warmup)).getText().toString();
                                    String newFirst = ((EditText) findViewById(R.id.new_first)).getText().toString();
                                    String newSecond = ((EditText) findViewById(R.id.new_second)).getText().toString();
                                    String newThird = ((EditText) findViewById(R.id.new_third)).getText().toString();
                                    String newCooldown = ((EditText) findViewById(R.id.new_cooldown)).getText().toString();

                                    RequestBody formBody = FormBody.create(JSON, "{\"warmup\":\"" + newWarmup +
                                            "\", \"first\":\"" + newFirst +"\", \"second\": \"" + newSecond + "\", \"third\":\"" + newThird + "\"," +
                                            " \"cooldown\":\"" + newCooldown + "\"}");
                                    HttpUrl delUrl = HttpUrl.parse("https://final496-161720.appspot.com/workouts/" + wId);
                                    final Request userDelReq = new Request.Builder()
                                            .url(delUrl)
                                            .patch(formBody)
                                            .build();

                                    mOkHttpClient.newCall(userDelReq).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(getIntent());
                                            overridePendingTransition(0, 0);

                                        }

                                    });
                                }
                            });


                            return v;
                        }
                    };

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ListView)findViewById(R.id.activity_list_view)).setAdapter(postAdapter);
                        }
                    });

                    //postAdapter.s
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }



            }

        });

        Button addButton = (Button) findViewById(R.id.add_workout);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newWarmup = ((EditText) findViewById(R.id.warmup_input)).getText().toString();
                String newFirst = ((EditText) findViewById(R.id.first_input)).getText().toString();
                String newSecond = ((EditText) findViewById(R.id.second_input)).getText().toString();
                String newThird = ((EditText) findViewById(R.id.third_input)).getText().toString();
                String newCooldown = ((EditText) findViewById(R.id.cooldown_input)).getText().toString();
                String userId = acct.getId().toString();

                RequestBody formBody = FormBody.create(JSON, "{\"userId\":\"" + acct.getId() + "\", \"warmup\":\"" + newWarmup +
                "\", \"first\":\"" + newFirst +"\", \"second\": \"" + newSecond + "\", \"third\":\"" + newThird + "\"," +
                        " \"cooldown\":\"" + newCooldown + "\"}");
                final Request userPostReq = new Request.Builder()
                        .url("https://final496-161720.appspot.com/workouts")
                        .post(formBody)
                        .build();

                mOkHttpClient.newCall(userPostReq).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }

                });


            }
        });





    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handleSignInResult(result);
        }
    }

    // [END onActivityResult]

}

package gmedia.net.id.osbond;

import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends AppCompatActivity {
    private ImageView imgBackground, imgProfile;
    private TextView nama, email, noTelp;
    private SessionManager session;
    private Proses proses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        proses = new Proses(Profile.this);
        session = new SessionManager(Profile.this);
        RelativeLayout utama = findViewById(R.id.layoutProfileUtama);
        imgBackground = (ImageView) findViewById(R.id.imgBackground);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        nama = (TextView) findViewById(R.id.isiNama);
        email = (TextView) findViewById(R.id.isiEmail);
        noTelp = (TextView) findViewById(R.id.isiNoTelp);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PROFILE");
        actionBar.setDisplayHomeAsUpEnabled(true);
        utama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideSoftKeyboard(Profile.this);
            }
        });
        prepareDataProfile();
    }

    private void prepareDataProfile() {
        proses.ShowDialog();
        ApiVolley request = new ApiVolley(Profile.this, new JSONObject(), "GET", URL.ViewProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONObject response = object.getJSONObject("response");
                        if (!response.getString("foto").equals("")) {
                            Picasso.with(Profile.this).load(response.getString("foto"))
                                    .resize(512, 512)
                                    .placeholder(R.drawable.user)
                                    .centerCrop()
                                    .transform(new CircleTransform())
                                    .into(imgProfile);
                        }
                        if (!response.getString("background").equals("")) {
                            Picasso.with(Profile.this).load(response.getString("background"))
                                    .resize(720, 512)
                                    .centerCrop()
                                    .placeholder(R.drawable.ekupon)
                                    .into(imgBackground);
                        }
                        nama.setText(response.getString("profile_name"));
                        email.setText(response.getString("email"));
                        noTelp.setText(response.getString("no_telp"));
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                proses.DismissDialog();
                Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareDataProfile();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit_profile:
                Intent intentEditProfile = new Intent(Profile.this, EditProfile.class);
                startActivity(intentEditProfile);
                return true;
            case R.id.sign_out:
                Intent intent = new Intent(Profile.this, Login.class);
                intent.putExtra("logout", true);
                session.logoutUser(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

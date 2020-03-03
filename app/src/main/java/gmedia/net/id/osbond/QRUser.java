package gmedia.net.id.osbond;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class QRUser extends AppCompatActivity {
    private ImageView imgQRUser;
    private Proses proses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qruser);
        proses = new Proses(QRUser.this);
        imgQRUser = findViewById(R.id.imgQRUser);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR USER");
        actionBar.setDisplayHomeAsUpEnabled(true);
        prepareDataQRUser();
    }

    private void prepareDataQRUser() {
        proses.ShowDialog();
        ApiVolley request = new ApiVolley(QRUser.this, new JSONObject(), "POST", URL.QRCode, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")){
                        JSONObject response = object.getJSONObject("response");
                        Picasso.with(QRUser.this).load(response.getString("url")).resize(512, 512)
                                .into(imgQRUser);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            /*case R.id.settings:
                onBackPressed();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

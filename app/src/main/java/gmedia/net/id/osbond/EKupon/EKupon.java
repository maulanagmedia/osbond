package gmedia.net.id.osbond.EKupon;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.osbond.ApiVolley;
import gmedia.net.id.osbond.MainActivity;
import gmedia.net.id.osbond.Proses;
import gmedia.net.id.osbond.R;
import gmedia.net.id.osbond.URL;

public class EKupon extends AppCompatActivity {
	private ListView listView;
	private ListAdapterEKupon adapterEKupon;
	private ArrayList<CustomListAdapterEKupon> listEKupon;
	private ArrayList<CustomListAdapterEKupon> moreListEKupon;
	private int background[] =
			{
					R.drawable.kupon_merah,
					R.drawable.kupon_hitam,
					R.drawable.kupon_merah,
					R.drawable.kupon_hitam,
					R.drawable.kupon_merah,
					R.drawable.kupon_hitam,
					R.drawable.kupon_merah,
					R.drawable.kupon_hitam,
					R.drawable.kupon_merah,
					R.drawable.kupon_hitam,
			};
	private String kupon[] =
			{
					"KUPON 5 JAM",
					"KUPON 10 JAM",
					"KUPON 5 JAM",
					"KUPON 10 JAM",
					"KUPON 5 JAM",
					"KUPON 10 JAM",
					"KUPON 5 JAM",
					"KUPON 10 JAM",
					"KUPON 5 JAM",
					"KUPON 10 JAM",
			};
	private String lokasiKupon[] =
			{
					"kupon ini hanya bisa di gunakan di Mangga Dua Square",
					"kupon ini bisa digunakan di cabang lain",
					"kupon ini hanya bisa di gunakan di Mangga Dua Square",
					"kupon ini bisa digunakan di cabang lain",
					"kupon ini hanya bisa di gunakan di Mangga Dua Square",
					"kupon ini bisa digunakan di cabang lain",
					"kupon ini hanya bisa di gunakan di Mangga Dua Square",
					"kupon ini bisa digunakan di cabang lain",
					"kupon ini hanya bisa di gunakan di Mangga Dua Square",
					"kupon ini bisa digunakan di cabang lain",
			};
	private boolean isLoading = false;
	private int startIndex = 0;
	private int count = 10;
	private View footerList;
	private Dialog dialog;
	private Proses proses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ekupon);
		proses = new Proses(EKupon.this);
		listView = (ListView) findViewById(R.id.lvEKupon);
		LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footerList = li.inflate(R.layout.footer_list, null);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("KUPON");
		actionBar.setDisplayHomeAsUpEnabled(true);
		prepareDataEKupon();
//        bcgEKupon = (ImageView)findViewById(R.id.)
//        listEKupon = prepareDataEKupon();

	}

	@Override
	protected void onResume() {
		super.onResume();
		startIndex = 0;
		prepareDataEKupon();
	}

	private void prepareDataEKupon() {
		isLoading = true;
		proses.ShowDialog();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("start", String.valueOf(startIndex));
			jBody.put("count", String.valueOf(count));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(EKupon.this, jBody, "POST", URL.EKupon, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				isLoading = false;
				proses.DismissDialog();
				listEKupon = new ArrayList<>();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					if (status.equals("404")) {

						JSONArray response = object.getJSONArray("response");
						for (int i = 0; i < response.length(); i++) {

							JSONObject isi = response.getJSONObject(i);
							listEKupon.add(new CustomListAdapterEKupon(
									isi.getString("id"),
									isi.getString("id_cabang"),
									isi.getString("paket"),
									isi.getString("cabang"),
									isi.getString("jenis"),
									isi.getString("jumlah_scan"),
									isi.getString("end"),
									isi.getString("event")
							));
						}

						listView.setAdapter(null);
						adapterEKupon = new ListAdapterEKupon(EKupon.this, listEKupon);
						listView.setAdapter(adapterEKupon);
						Toast.makeText(getApplicationContext(), "Anda tidak memiliki kupon", Toast.LENGTH_LONG).show();
					} else if (status.equals("200")) {
						JSONArray response = object.getJSONArray("response");
						for (int i = 0; i < response.length(); i++) {
							JSONObject isi = response.getJSONObject(i);
							listEKupon.add(new CustomListAdapterEKupon(
									isi.getString("id"),
									isi.getString("id_cabang"),
									isi.getString("paket"),
									isi.getString("cabang"),
									isi.getString("jenis"),
									isi.getString("jumlah_scan"),
									isi.getString("end"),
									isi.getString("event")
							));
						}
						listView.setAdapter(null);
						adapterEKupon = new ListAdapterEKupon(EKupon.this, listEKupon);
						listView.setAdapter(adapterEKupon);
						listView.setOnScrollListener(new AbsListView.OnScrollListener() {
							@Override
							public void onScrollStateChanged(AbsListView view, int i) {
                                /*int threshold = 1;
                                int countMerchant = listView.getCount();

                                if (i == SCROLL_STATE_IDLE) {
                                    if (listView.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                                        isLoading = true;
                                        listView.addFooterView(footerList);
                                        startIndex += count;
//                                        startIndex = 0;
                                        getMoreData();
                                        //Log.i(TAG, "onScroll: last ");
                                    }
                                }*/
							}

							@Override
							public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
								if (view.getLastVisiblePosition() == totalItemCount - 1 && listView.getCount() > (count - 1) && !isLoading) {
									isLoading = true;
									listView.addFooterView(footerList);
									startIndex += count;
//                                        startIndex = 0;
									getMoreData();
									//Log.i(TAG, "onScroll: last ");
								}
							}
						});

                        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getApplicationContext(), QRCode.class);
                                *//*CustomListAdapterEKupon item = (CustomListAdapterEKupon) parent.getItemAtPosition(position);
                                intent.putExtra("id", item.getIdCabang());*//*
                                startActivityForResult(intent,0);
                            }
                        });*/
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				isLoading = false;
				proses.DismissDialog();
				Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void getMoreData() {
		isLoading = true;
		moreListEKupon = new ArrayList<>();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("start", String.valueOf(startIndex));
			jBody.put("count", String.valueOf(count));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(EKupon.this, jBody, "POST", URL.EKupon, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				moreListEKupon = new ArrayList<>();
				listView.removeFooterView(footerList);
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					if (status.equals("200")) {
						JSONArray array = object.getJSONArray("response");
						for (int i = 0; i < array.length(); i++) {
							JSONObject isi = array.getJSONObject(i);
							moreListEKupon.add(new CustomListAdapterEKupon(
									isi.getString("id"),
									isi.getString("id_cabang"),
									isi.getString("paket"),
									isi.getString("cabang"),
									isi.getString("jenis"),
									isi.getString("jumlah_scan"),
									isi.getString("end"),
									isi.getString("event")
							));
						}
						isLoading = false;
						listView.removeFooterView(footerList);
						if (adapterEKupon != null) adapterEKupon.addMoreData(moreListEKupon);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				isLoading = false;
				proses.DismissDialog();
				Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

	/*private ArrayList<CustomListAdapterEKupon> prepareDataEKupon() {
		ArrayList<CustomListAdapterEKupon> rvData = new ArrayList<>();
		for (int i = 0; i < background.length; i++) {
			CustomListAdapterEKupon custom = new CustomListAdapterEKupon();
			custom.setBackground(background[i]);
			custom.setKupon(kupon[i]);
			custom.setLokasiKupon(lokasiKupon[i]);
			rvData.add(custom);
		}
		return rvData;
	}*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {

		if (MainActivity.fromNotifToEKupon) {
			Intent intent = new Intent(EKupon.this, MainActivity.class);
			startActivity(intent);
			finish();
		} else {
			super.onBackPressed();
		}
	}
}

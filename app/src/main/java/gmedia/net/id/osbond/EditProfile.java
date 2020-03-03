package gmedia.net.id.osbond;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static gmedia.net.id.osbond.CompressBitmap.scaleDown;

public class EditProfile extends AppCompatActivity {
	private RelativeLayout btnCancel, btnSave, changeBackground;
	private ImageView imgFotoProfile, imgFotoBackground;
	private EditText nama, email, noTelp;
	private LinearLayout changeProfile;
	private int PICK_IMAGE = 1, PICK_BACKGROUND = 2;
	private Bitmap bitmap, photo;
	private int maxImageSize = 256;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		btnCancel = (RelativeLayout) findViewById(R.id.btnCancel);
		btnSave = (RelativeLayout) findViewById(R.id.btnSave);
		imgFotoProfile = (ImageView) findViewById(R.id.imgFotoProfile);
		imgFotoBackground = (ImageView) findViewById(R.id.imgFotoBackground);
		nama = (EditText) findViewById(R.id.editNama);
		email = (EditText) findViewById(R.id.editEmail);
		noTelp = (EditText) findViewById(R.id.editNoTelp);
		changeProfile = (LinearLayout) findViewById(R.id.changeImgProfile);
		changeBackground = (RelativeLayout) findViewById(R.id.changeImgBackground);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("PROFILE");
		prepareDataProfile();
		changeProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
			}
		});
		changeBackground.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_BACKGROUND);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				prepareCreateDataProfile();
			}
		});
	}

	private void prepareCreateDataProfile() {
		final Dialog dialog = new Dialog(EditProfile.this);
		dialog.setContentView(R.layout.loading);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		bitmap = ((BitmapDrawable) imgFotoProfile.getDrawable()).getBitmap();
		photo = ((BitmapDrawable) imgFotoBackground.getDrawable()).getBitmap();
		final JSONObject jBody = new JSONObject();
		try {
			jBody.put("background", EncodeBitmapToString.convert(photo));
			jBody.put("foto", EncodeBitmapToString.convert(bitmap));
			jBody.put("profile_name", nama.getText().toString());
			jBody.put("email", email.getText().toString());
			jBody.put("no_telp", noTelp.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(EditProfile.this, jBody, "POST", URL.EditProfile, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				dialog.dismiss();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
						onBackPressed();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
			Uri filePathProfile = data.getData();
			InputStream imageStream = null, copyStream = null;
			try {
				imageStream = getContentResolver().openInputStream(
						filePathProfile);
				copyStream = getContentResolver().openInputStream(
						filePathProfile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			//options.inDither = true;

			// Get bitmap dimensions before reading...
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(copyStream, null, options);
			int width = options.outWidth;
			int height = options.outHeight;
			int largerSide = Math.max(width, height);
			options.inJustDecodeBounds = false; // This time it's for real!
			int sampleSize = 1; // Calculate your sampleSize here
			if (largerSide <= 1000) {
				sampleSize = 1;
			} else if (largerSide > 1000 && largerSide <= 2000) {
				sampleSize = 2;
			} else if (largerSide > 2000 && largerSide <= 3000) {
				sampleSize = 3;
			} else if (largerSide > 3000 && largerSide <= 4000) {
				sampleSize = 4;
			} else {
				sampleSize = 6;
			}
			options.inSampleSize = sampleSize;
			//options.inDither = true;

			Bitmap bmp = BitmapFactory.decodeStream(imageStream, null, options);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);
			byte[] byteArray = stream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			bitmap = scaleDown(bitmap, 360, true);
			try {
				stream.close();
				stream = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			Picasso.with(EditProfile.this).load(filePathProfile).resize(512, 512).centerCrop()
					.transform(new CircleTransform()).into(imgFotoProfile);
            /*try {
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imgFotoProfile.setImageBitmap(CompressBitmap.scaleDown(photo,maxImageSize,true));
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = ((BitmapDrawable)imgFotoProfile.getDrawable()).getBitmap();*/

            /*BitmapDrawable drawable = (BitmapDrawable) imgFotoProfile.getDrawable();
            bitmap = drawable.getBitmap();*/
		} else if (requestCode == PICK_BACKGROUND && resultCode == Activity.RESULT_OK) {
			Uri filePathBackground = data.getData();
			InputStream imageStream = null, copyStream = null;
			try {
				imageStream = getContentResolver().openInputStream(
						filePathBackground);
				copyStream = getContentResolver().openInputStream(
						filePathBackground);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			//options.inDither = true;

			// Get bitmap dimensions before reading...
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(copyStream, null, options);
			int width = options.outWidth;
			int height = options.outHeight;
			int largerSide = Math.max(width, height);
			options.inJustDecodeBounds = false; // This time it's for real!
			int sampleSize = 1; // Calculate your sampleSize here
			if (largerSide <= 1000) {
				sampleSize = 1;
			} else if (largerSide > 1000 && largerSide <= 2000) {
				sampleSize = 2;
			} else if (largerSide > 2000 && largerSide <= 3000) {
				sampleSize = 3;
			} else if (largerSide > 3000 && largerSide <= 4000) {
				sampleSize = 4;
			} else {
				sampleSize = 6;
			}
			options.inSampleSize = sampleSize;
			//options.inDither = true;

			Bitmap bmp = BitmapFactory.decodeStream(imageStream, null, options);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);
			byte[] byteArray = stream.toByteArray();
			photo = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			photo = scaleDown(photo, 512, true);

			try {
				stream.close();
				stream = null;
			} catch (IOException e) {

				e.printStackTrace();
			}
			Picasso.with(EditProfile.this).load(filePathBackground).resize(720, 512).centerCrop()
					.into(imgFotoBackground);
		}
	}

	private void prepareDataProfile() {
		final Dialog dialog = new Dialog(EditProfile.this);
		dialog.setContentView(R.layout.loading);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		ApiVolley request = new ApiVolley(EditProfile.this, new JSONObject(), "GET", URL.ViewProfile, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				dialog.dismiss();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONObject response = object.getJSONObject("response");
						if (!response.getString("foto").equals("")) {
							Picasso.with(EditProfile.this).load(response.getString("foto"))
									.resize(512, 512)
									.centerCrop()
									.placeholder(R.drawable.user)
									.transform(new CircleTransform())
									.into(imgFotoProfile);
						}
						if (!response.getString("background").equals("")) {
							Picasso.with(EditProfile.this).load(response.getString("background"))
									.resize(720, 512)
									.centerCrop()
									.placeholder(R.drawable.ekupon)
									.into(imgFotoBackground);
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
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}

package gmedia.net.id.osbond.ImageSlider;

/**
 * Created by Bayu on 12/04/2018.
 */

import android.content.Context;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gmedia.net.id.osbond.R;

public class ImageSliderAdapter extends PagerAdapter {

    private ArrayList<Custom> images;
//    private LayoutInflater inflater;
    private Context context;
    private Custom custom;

    public ImageSliderAdapter(Context context, ArrayList<Custom> images) {
        this.context = context;
        this.images = images;
//        inflater = LayoutInflater.from(context);
//        inflater = ((Activity) context).getLayoutInflater();
//        OwnScroller scroller = new OwnScroller(context,2000);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = LayoutInflater.from(context).inflate(R.layout.slide, view, false);
//        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.image);
        custom = images.get(position);
        /*Picasso.with(context).load(custom.getImage())
                .resize(720, 720).centerInside()
                .into(myImage);*/
        Glide.with(context).load(custom.getImage()).into(myImage);
//        myImage.setImageResource(R.drawable.logo_osbond);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}

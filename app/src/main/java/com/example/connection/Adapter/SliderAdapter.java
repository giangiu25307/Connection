package com.example.connection.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.example.connection.R;

import org.xmlpull.v1.XmlPullParser;

public class SliderAdapter extends PagerAdapter {

    Context context;
    ProgressBar progressBar;
    int[] fragments = {
            R.layout.signup_slide_fragment1,
            R.layout.signup_slide_fragment2
    };

    public SliderAdapter(Context context,ProgressBar progressBar) { //progress bar passata per cambiare lo stato di essa, tuttavia magari non serve farlo così
        this.context = context;
        progressBar.setMax(fragments.length);
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        progressBar.setProgress(position+1);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(fragments[position], container, false);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}

package com.yuyu.utaitebox.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyu.utaitebox.R;
import com.yuyu.utaitebox.fragment.MusicInfoFragment;
import com.yuyu.utaitebox.fragment.UtaiteInfoFragment;
import com.yuyu.utaitebox.rest.RestUtils;
import com.yuyu.utaitebox.utils.MainVO;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Context context;
    private RequestManager requestManager;
    private FragmentManager fragmentManager;
    private ArrayList<MainVO> vo;

    public MainAdapter(Context context, RequestManager requestManager, FragmentManager fragmentManager, ArrayList<MainVO> vo) {
        this.context = context;
        this.requestManager = requestManager;
        this.fragmentManager = fragmentManager;
        this.vo = vo;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        requestManager.load(RestUtils.BASE + (vo.get(position).getBg() == null ?
                context.getString(R.string.rest_images_cover) : context.getString(R.string.rest_cover) + vo.get(position).getBg()))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.bg);
        holder.bg.setOnClickListener(v -> onClickMethod(position, true));

        requestManager.load(RestUtils.BASE + (vo.get(position).getImg() == null ?
                context.getString(R.string.rest_images_artist) : context.getString(R.string.rest_artist_image) + vo.get(position).getImg()))
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.img);
        holder.img.setOnClickListener(v -> onClickMethod(position, false));

        holder.title.setText(vo.get(position).getTitle());
        holder.title.bringToFront();
        holder.utaite.setText(vo.get(position).getUtaite());
    }

    public void onClickMethod(int position, boolean bg) {
        Fragment fragment = bg ?
                new MusicInfoFragment() : new UtaiteInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(bg ?
                R.string.rest_sid : R.string.rest_aid), Integer.parseInt(bg ?
                vo.get(position).get_sid() : vo.get(position).get_aid()));
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return vo.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bg, img;
        TextView title, utaite;

        ViewHolder(View view) {
            super(view);
            bg = (ImageView) view.findViewById(R.id.bg);
            img = (ImageView) view.findViewById(R.id.img);
            title = (TextView) view.findViewById(R.id.title);
            utaite = (TextView) view.findViewById(R.id.utaite);
        }
    }

}
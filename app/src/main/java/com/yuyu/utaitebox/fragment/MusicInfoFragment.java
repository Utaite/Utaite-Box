package com.yuyu.utaitebox.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyu.utaitebox.R;
import com.yuyu.utaitebox.activity.MainActivity;
import com.yuyu.utaitebox.rest.Comment;
import com.yuyu.utaitebox.rest.Other;
import com.yuyu.utaitebox.rest.Repo;
import com.yuyu.utaitebox.rest.Ribbon;
import com.yuyu.utaitebox.rest.Song;
import com.yuyu.utaitebox.utils.MainVO;
import com.yuyu.utaitebox.utils.Task;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MusicInfoFragment extends Fragment {

    private static final String TAG = MusicInfoFragment.class.getSimpleName();

    private Context context;
    private RequestManager glide;
    private Repo repo;
    private Task task;
    private MediaPlayer mediaPlayer;
    private ArrayList<MainVO> mainVOSet;
    private String str2Check;
    private boolean ribbonCheck, text1Check, img1Check, text2Check, img2Check, text3Check, img3Check;

    @BindView(R.id.musicinfo_recyclerview)
    RecyclerView musicinfo_recyclerview;
    @BindView(R.id.musicinfo_cover)
    ImageView musicinfo_cover;
    @BindView(R.id.musicinfo_utaite)
    TextView musicinfo_utaite;
    @BindView(R.id.musicinfo_song)
    TextView musicinfo_song;
    @BindView(R.id.musicinfo_songkr)
    TextView musicinfo_songkr;
    @BindView(R.id.musicinfo_ribbon)
    TextView musicinfo_ribbon;
    @BindView(R.id.musicinfo_addlist)
    TextView musicinfo_addlist;
    @BindView(R.id.musicinfo_ribbonsrc)
    ImageView musicinfo_ribbonsrc;
    @BindView(R.id.musicinfo_text2src)
    ImageView musicinfo_text2src;
    @BindView(R.id.musicinfo_ribbonright)
    TextView musicinfo_ribbonright;
    @BindView(R.id.musicinfo_playedright)
    TextView musicinfo_playedright;
    @BindView(R.id.musicinfo_commentright)
    TextView musicinfo_commentright;
    @BindView(R.id.musicinfo_status)
    RelativeLayout musicinfo_status;
    @BindView(R.id.musicinfo_text1)
    TextView musicinfo_text1;
    @BindView(R.id.musicinfo_text2)
    TextView musicinfo_text2;
    @BindView(R.id.musicinfo_ribbonimg)
    LinearLayout musicinfo_ribbonimg;
    @BindView(R.id.musicinfo_play)
    ImageView musicinfo_play;
    @BindView(R.id.musicinfo_timeline)
    LinearLayout musicinfo_timeline;
    @BindView(R.id.musicinfo_avatar)
    ImageView musicinfo_avatar;
    @BindView(R.id.musicinfo_text3)
    TextView musicinfo_text3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_musicinfo, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        glide = Glide.with(context);
        task = new Task(context, 1);
        task.onPreExecute();
//        custom.viewVisibilities(false, musicinfo_text2src, musicinfo_text2src, musicinfo_ribbonimg, musicinfo_timeline, musicinfo_status);
        requestRetrofit("song", getArguments().getInt("sid"));
        return view;
    }

    @Override
    public void onStart() {
        ribbonCheck = text1Check = img1Check = text2Check = img2Check = text3Check = img3Check = false;
        super.onStart();
    }

    @OnClick(R.id.musicinfo_text1)
    public void musicInfo1() {
        if (!text1Check && !img1Check) {
            for (Other e : repo.getSidebar().getOther()) {
                requestRetrofit("song", Integer.parseInt(e.get_sid()));
            }
            img1Check = true;
        }
        musicinfo_recyclerview.setVisibility(!text1Check ? View.VISIBLE : View.GONE);
        musicinfo_text1.setText(getString(R.string.musicinfo_txt1, !text1Check ? "▲" : "▼"));
        text1Check = !text1Check;
    }

    @OnClick(R.id.musicinfo_text2)
    public void musicInfo2() {
        if (!text2Check && !img2Check) {
            ArrayList<Ribbon> ribbon1 = repo.getSidebar().getRibbon();
            int size = ribbon1.size();
            if (size != 0) {
                ImageView img[] = new ImageView[size];
                TextView iv[] = new TextView[size];
                LinearLayout rAbsolute, rHorizontal = null;
                RelativeLayout rRelative;
                for (int i = 0; i < size; i++) {
                    if (i % 4 == 0) {
                        rHorizontal = new LinearLayout(context);
                        rHorizontal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        rHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                        musicinfo_ribbonimg.addView(rHorizontal);
                    }

                    img[i] = new ImageView(context);
                    img[i].setScaleType(ImageView.ScaleType.FIT_XY);
                    String avatar = ribbon1.get(i).getAvatar();
                    glide.load(avatar == null ? MainActivity.BASE + "/res/profile/image/" + MainActivity.PROFILE : MainActivity.BASE + "/res/profile/image/" + avatar)
                            .bitmapTransform(new CropCircleTransformation(context))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(img[i]);

                    iv[i] = new TextView(context);
                    String nickname = ribbon1.get(i).getNickname();
                    iv[i].setText(nickname.length() <= 10 ? nickname : nickname.substring(0, 10) + "...");
                    iv[i].setTextColor(Color.BLACK);
                    rAbsolute = new LinearLayout(context);
                    rAbsolute.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    rAbsolute.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    param1.setMargins(23, 10, 23, 10);
                    rAbsolute.addView(img[i], param1);
                    rAbsolute.setId(i + 1);

                    rRelative = new RelativeLayout(context);
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    p.addRule(RelativeLayout.BELOW, rAbsolute.getId());
                    rRelative.setLayoutParams(p);
                    rRelative.addView(iv[i]);
                    rAbsolute.addView(rRelative);
                    rAbsolute.setGravity(Gravity.CENTER);
                    rHorizontal.addView(rAbsolute);
                }
            } else {
                TextView iv = new TextView(context);
                iv.setText(R.string.not_ribbon);
                iv.setTextColor(Color.BLACK);
                iv.setTextSize(20);
                iv.setGravity(Gravity.CENTER);
                musicinfo_ribbonimg.addView(iv);
            }
            img2Check = true;
        }
        musicinfo_ribbonimg.setVisibility(!text2Check ? View.VISIBLE : View.GONE);
        musicinfo_text2src.setVisibility(!text2Check ? View.VISIBLE : View.GONE);
        musicinfo_text2.setText((!text2Check ? "▲" : "▼") + str2Check);
        text2Check = !text2Check;
    }

    @OnClick(R.id.musicinfo_text3)
    public void musicInfo3() {
        int nickInt = 1, contInt = 1001, dateInt = 2001;
        if (!text3Check && !img3Check) {
            glide.load(MainActivity.BASE + (MainActivity.tempCover == null ? "/res/profile/cover/" + MainActivity.PROFILE : "/res/profile/image/" + MainActivity.tempCover))
                    .bitmapTransform(new CropCircleTransformation(context))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(300, 300)
                    .into(musicinfo_avatar);
            ArrayList<Comment> comment = repo.getComment();
            int size = comment.size();
            if (size != 0) {
                LinearLayout rAbsolute;
                rAbsolute = new LinearLayout(context);
                rAbsolute.setOrientation(LinearLayout.VERTICAL);
                RelativeLayout rRelativeNick, rRelativeCont, rRelativeImg, rRelativeDate;
                ImageView img[] = new ImageView[size];
                TextView nick[] = new TextView[size];
                TextView cont[] = new TextView[size];
                TextView date[] = new TextView[size];
                for (int i = 0; i < size; i++) {
                    img[i] = new ImageView(context);
                    img[i].setScaleType(ImageView.ScaleType.FIT_XY);
                    String avatar = comment.get(i).getAvatar();
                    glide.load(avatar == null ? MainActivity.BASE + "/res/profile/image/" + MainActivity.PROFILE : MainActivity.BASE + "/res/profile/image/" + avatar)
                            .bitmapTransform(new CropCircleTransformation(context))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(img[i]);
                    rRelativeImg = new RelativeLayout(context);
                    RelativeLayout.LayoutParams pImg = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    rRelativeImg.setLayoutParams(pImg);
                    rRelativeImg.setPadding(10, 10, 0, 10);
                    rRelativeImg.addView(img[i]);
                    rAbsolute.addView(rRelativeImg);

                    nick[i] = new TextView(context);
                    String nickname = comment.get(i).getNickname();
                    nick[i].setText(nickname.length() <= 10 ? nickname : nickname.substring(0, 10) + "...");
                    nick[i].setTextColor(Color.BLACK);
                    nick[i].setTextSize(20);
                    rRelativeNick = new RelativeLayout(context);
                    rRelativeNick.setId(i + nickInt);
                    RelativeLayout.LayoutParams pNick = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    pNick.setMargins(250, 0, 0, 0);
                    rRelativeNick.setLayoutParams(pNick);
                    rRelativeNick.setPadding(0, 10, 10, 0);
                    rRelativeNick.addView(nick[i]);
                    rRelativeImg.addView(rRelativeNick);

                    cont[i] = new TextView(context);
                    cont[i].setText(!comment.get(i).getContent().equals("") ? comment.get(i).getContent() : comment.get(i).getType().equals("1") ? "/*   Upload music   */" : "/*   Upload cover image   */");
                    cont[i].setTextColor(Color.BLACK);
                    cont[i].setTextSize(12);
                    rRelativeCont = new RelativeLayout(context);
                    RelativeLayout.LayoutParams pCont = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    pCont.setMargins(250, 0, 0, 0);
                    pCont.addRule(RelativeLayout.BELOW, rRelativeNick.getId());
                    rRelativeCont.setId(i + contInt);
                    rRelativeCont.setPadding(0, 0, 10, 10);
                    rRelativeCont.setLayoutParams(pCont);
                    rRelativeCont.addView(cont[i]);
                    rRelativeImg.addView(rRelativeCont);

                    date[i] = new TextView(context);
                    String dateStr = comment.get(i).getDate();
                    date[i].setText(dateStr.substring(0, dateStr.indexOf("T")));
                    date[i].setTextColor(Color.BLACK);
                    date[i].setTextSize(10);
                    rRelativeDate = new RelativeLayout(context);
                    rRelativeDate.setId(i + dateInt);
                    RelativeLayout.LayoutParams pDate = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    pDate.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    pDate.setMargins(0, 0, 20, 0);
                    rRelativeDate.setLayoutParams(pDate);
                    rRelativeDate.addView(date[i]);
                    rRelativeImg.addView(rRelativeDate);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setStroke(3, Color.BLACK);
                    rRelativeImg.setBackground(drawable);
                }
                musicinfo_timeline.addView(rAbsolute);
            }
            img3Check = true;
        }
        musicinfo_timeline.setVisibility(!text3Check ? View.VISIBLE : View.GONE);
        musicinfo_text3.setText(getString(R.string.musicinfo_txt3, !text3Check ? "▲" : "▼"));
        text3Check = !text3Check;
    }

    @OnClick(R.id.musicinfo_play)
    public void musicPlay() {
        mediaPlayer = new MediaPlayer();
        task.onPreExecute();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(MainActivity.BASE + "/api/play/stream/" + repo.getSong().getKey());
        } catch (Exception e) {
        }
        mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.start();
            task.onPostExecute(null);
        });
        mediaPlayer.prepareAsync();
    }

    // 1) bundle로 받은 sid로 해당 노래의 정보를 받아옴
    // 2) 노래의 정보를 이미 받았을 경우, 같은 노래의 정보를 받아옴
    public void requestRetrofit(String what, int index) {
        Call<Repo> repos = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MainActivity.UtaiteBoxGetRepo.class)
                .listRepos(what, index);
        repos.enqueue(new Callback<Repo>() {
            @Override
            public void onResponse(Call<Repo> call, Response<Repo> response) {
                if (repo == null) {
                    repo = response.body();
                    task.onPostExecute(null);
                    String cover = repo.getSong().getCover();
                    glide.load(cover == null ? MainActivity.BASE + "/images/cover.jpg" : MainActivity.BASE + "/res/cover/" + cover)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(musicinfo_cover);
                    for (Ribbon e : repo.getSidebar().getRibbon()) {
                        if (!ribbonCheck) {
                            ribbonCheck = MainActivity._mid == Integer.parseInt(e.get_mid());
                        }
                    }
                    musicinfo_ribbonsrc.setBackground(getResources().getDrawable(ribbonCheck ? R.drawable.circle_yellow : R.drawable.circle_black));
                    musicinfo_text2src.setBackground(getResources().getDrawable(ribbonCheck ? R.drawable.circle_yellow : R.drawable.circle_black));
                    str2Check = getString(ribbonCheck ? R.string.musicinfo_txt2_1 : R.string.musicinfo_txt2_2);
                    String played = repo.getSong().getPlayed();
                    if (Integer.parseInt(played) >= 1000) {
                        played = String.valueOf(Integer.parseInt(played) / 1000) + "." + String.valueOf(Integer.parseInt(played) % 1000).substring(0, 1) + "K+";
                    }
                    musicinfo_playedright.setText(played);
                    musicinfo_status.setVisibility(View.VISIBLE);
                    musicinfo_recyclerview.setHasFixedSize(true);
                    LinearLayoutManager llm = new LinearLayoutManager(context);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    musicinfo_recyclerview.setLayoutManager(llm);
                    mainVOSet = new ArrayList<>();
                    custom.viewTexts(new TextView[]{musicinfo_text1, musicinfo_text2, musicinfo_text3, musicinfo_utaite, musicinfo_song, musicinfo_songkr, musicinfo_ribbonright, musicinfo_commentright},
                            new String[]{getString(R.string.musicinfo_txt1, "▼"), "▼" + str2Check, getString(R.string.musicinfo_txt3, "▼"), repo.getSong().getArtist_en(), repo.getSong().getSong_original(), repo.getSong().getSong_kr(), repo.getSong().getRibbon(), repo.getSong().getComment()});
                    custom.viewPaints(musicinfo_ribbonright, musicinfo_songkr, musicinfo_ribbon, musicinfo_addlist, musicinfo_utaite, musicinfo_song, musicinfo_playedright, musicinfo_commentright);
                } else {
                    Song song = response.body().getSong();
                    mainVOSet.add(new MainVO(song.getCover(), song.getArtist_cover(), song.getSong_original(), song.getArtist_en(),
                            song.get_sid(), song.get_aid()));
                    musicinfo_recyclerview.setAdapter(new MainAdapter(mainVOSet, context, glide, getFragmentManager()));
                }
            }

            @Override
            public void onFailure(Call<Repo> call, Throwable t) {
                Log.e(TAG, String.valueOf(t));
            }
        });
    }

}
package com.yuyu.utaitebox.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.utaitebox.R;
import com.yuyu.utaitebox.activity.MainActivity;
import com.yuyu.utaitebox.activity.MusicActivity;
import com.yuyu.utaitebox.adapter.MainAdapter;
import com.yuyu.utaitebox.chain.Chained;
import com.yuyu.utaitebox.rest.Comment;
import com.yuyu.utaitebox.rest.Other;
import com.yuyu.utaitebox.rest.Repo;
import com.yuyu.utaitebox.rest.RestUtils;
import com.yuyu.utaitebox.rest.Ribbon;
import com.yuyu.utaitebox.rest.Song;
import com.yuyu.utaitebox.service.MusicService;
import com.yuyu.utaitebox.utils.Constant;
import com.yuyu.utaitebox.utils.MainVO;
import com.yuyu.utaitebox.utils.PlaylistVO;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

public class MusicInfoFragment extends RxFragment {

    private static final String TAG = MusicInfoFragment.class.getSimpleName();

    private Repo repo;
    private Context context;
    private RequestManager glide;

    private ArrayList<MainVO> vo;
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
    @BindView(R.id.musicinfo_ribbon_src)
    ImageView musicinfo_ribbon_src;
    @BindView(R.id.musicinfo_text2_src)
    ImageView musicinfo_text2_src;
    @BindView(R.id.musicinfo_ribbon_right)
    TextView musicinfo_ribbon_right;
    @BindView(R.id.musicinfo_played_right)
    TextView musicinfo_played_right;
    @BindView(R.id.musicinfo_comment_right)
    TextView musicinfo_comment_right;
    @BindView(R.id.musicinfo_status)
    RelativeLayout musicinfo_status;
    @BindView(R.id.musicinfo_text1)
    TextView musicinfo_text1;
    @BindView(R.id.musicinfo_text2)
    TextView musicinfo_text2;
    @BindView(R.id.musicinfo_ribbon_img)
    LinearLayout musicinfo_ribbon_img;
    @BindView(R.id.musicinfo_timeline)
    LinearLayout musicinfo_timeline;
    @BindView(R.id.musicinfo_avatar)
    ImageView musicinfo_avatar;
    @BindView(R.id.musicinfo_text3)
    TextView musicinfo_text3;
    @BindView(R.id.musicinfo_timeline_edittext)
    EditText musicinfo_timeline_edittext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_musicinfo, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        glide = Glide.with(context);
        initialize();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(musicinfo_timeline_edittext.getWindowToken(), 0);
    }

    public void initialize() {
        repo = null;
        ribbonCheck = text1Check = img1Check = text2Check = img2Check = text3Check = img3Check = false;
        Chained.setVisibilityMany(View.GONE, musicinfo_text2_src, musicinfo_text2_src, musicinfo_ribbon_img, musicinfo_timeline, musicinfo_status);
        musicinfo_ribbon_src.setBackground(getResources().getDrawable(R.drawable.circle_black));
        requestRetrofit(getString(R.string.rest_song), getArguments().getInt(getString(R.string.rest_sid)));
    }

    @OnClick(R.id.musicinfo_text1)
    public void onTextView1Click() {
        if (!text1Check && !img1Check) {
            for (Other e : repo.getSidebar().getOther()) {
                requestRetrofit(getString(R.string.rest_song), Integer.parseInt(e.get_sid()));
            }
            img1Check = true;
        }
        musicinfo_recyclerview.setVisibility(!text1Check ? View.VISIBLE : View.GONE);
        musicinfo_text1.setText(getString(R.string.musicinfo_txt1, !text1Check ? "▲" : "▼"));
        text1Check = !text1Check;
    }

    @OnClick(R.id.musicinfo_text2)
    public void onTextView2Click() {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(musicinfo_timeline_edittext.getWindowToken(), 0);
        if (!text2Check && !img2Check) {
            ArrayList<Ribbon> ribbon = repo.getSidebar().getRibbon();
            int size = ribbon.size();
            if (size != 0) {
                ImageView iv[] = new ImageView[size];
                TextView tv[] = new TextView[size];
                LinearLayout rAbsolute, rHorizontal = null;
                RelativeLayout rRelative;

                for (int i = 0; i < size; i++) {
                    if (i % 4 == 0) {
                        rHorizontal = new LinearLayout(context);
                        rHorizontal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        rHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                        rHorizontal.setHorizontalGravity(Gravity.CENTER);
                        musicinfo_ribbon_img.addView(rHorizontal);
                    }

                    iv[i] = new ImageView(context);
                    iv[i].setScaleType(ImageView.ScaleType.FIT_XY);
                    String avatar = ribbon.get(i).getAvatar();
                    glide.load(RestUtils.BASE + getString(R.string.rest_profile_image) + (avatar == null ? getString(R.string.rest_profile) : avatar))
                            .bitmapTransform(new CropCircleTransformation(context))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(iv[i]);
                    int position = i;
                    iv[i].setOnClickListener(v -> onProfileClick(ribbon.get(position).get_mid()));

                    tv[i] = new TextView(context);
                    String nickname = ribbon.get(i).getNickname();
                    tv[i].setText(nickname.length() <= Constant.LONG_STRING ? nickname : nickname.substring(0, Constant.LONG_STRING) + "...");
                    tv[i].setTextColor(Color.BLACK);
                    tv[i].setOnClickListener(v -> onProfileClick(ribbon.get(position).get_mid()));

                    rAbsolute = new LinearLayout(context);
                    rAbsolute.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    rAbsolute.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    param1.setMargins(23, 10, 23, 10);
                    rAbsolute.addView(iv[i], param1);
                    rAbsolute.setId(i + 1);

                    rRelative = new RelativeLayout(context);
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    p.addRule(RelativeLayout.BELOW, rAbsolute.getId());
                    rRelative.setLayoutParams(p);
                    rRelative.addView(tv[i]);
                    rAbsolute.addView(rRelative);
                    rAbsolute.setGravity(Gravity.CENTER);
                    rAbsolute.setPadding(10, 0, 10, 0);
                    rHorizontal.addView(rAbsolute);
                }
            } else {
                TextView tv = new TextView(context);
                tv.setText(R.string.not_ribbon);
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(20);
                tv.setGravity(Gravity.CENTER);
                musicinfo_ribbon_img.addView(tv);
            }
            img2Check = true;
        }
        musicinfo_ribbon_img.setVisibility(!text2Check ? View.VISIBLE : View.GONE);
        musicinfo_text2_src.setVisibility(!text2Check ? View.VISIBLE : View.GONE);
        musicinfo_text2.setText((!text2Check ? "▲" : "▼") + str2Check);
        text2Check = !text2Check;
    }

    @OnClick(R.id.musicinfo_text3)
    public void onTextView3Click() {
        int nickInt = 1, contInt = 1001, dateInt = 2001;
        if (!text3Check && !img3Check) {
            glide.load(RestUtils.BASE + getString(R.string.rest_profile_image) + (MainActivity.TEMP_AVATAR == null ? getString(R.string.rest_profile) : MainActivity.TEMP_AVATAR))
                    .bitmapTransform(new CropCircleTransformation(context))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(musicinfo_avatar);
            ArrayList<Comment> comment = repo.getComment();
            int size = comment.size();
            if (size != 0) {
                LinearLayout rAbsolute;
                rAbsolute = new LinearLayout(context);
                rAbsolute.setOrientation(LinearLayout.VERTICAL);
                RelativeLayout rRelativeNick, rRelativeCont, rRelativeImg, rRelativeDate;
                ImageView iv[] = new ImageView[size];
                TextView nick[] = new TextView[size];
                TextView cont[] = new TextView[size];
                TextView date[] = new TextView[size];

                for (int i = 0; i < size; i++) {
                    iv[i] = new ImageView(context);
                    iv[i].setScaleType(ImageView.ScaleType.FIT_XY);
                    String avatar = comment.get(i).getAvatar();
                    glide.load(RestUtils.BASE + getString(R.string.rest_profile_image) + (avatar == null ? getString(R.string.rest_profile) : avatar))
                            .bitmapTransform(new CropCircleTransformation(context))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(iv[i]);
                    int position = i;
                    iv[i].setOnClickListener(v -> onProfileClick(comment.get(position).get_mid()));

                    rRelativeImg = new RelativeLayout(context);
                    RelativeLayout.LayoutParams pImg = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    rRelativeImg.setLayoutParams(pImg);
                    rRelativeImg.setPadding(10, 10, 0, 10);
                    rRelativeImg.addView(iv[i]);
                    rAbsolute.addView(rRelativeImg);

                    nick[i] = new TextView(context);
                    String nickname = comment.get(i).getNickname();
                    nick[i].setText(nickname.length() <= Constant.LONG_STRING ? nickname : nickname.substring(0, Constant.LONG_STRING) + "...");
                    nick[i].setTextColor(Color.BLACK);
                    nick[i].setTextSize(20);
                    nick[i].setOnClickListener(v -> onProfileClick(comment.get(position).get_mid()));

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
                    cont[i].setText(!TextUtils.isEmpty(comment.get(i).getContent()) ?
                            comment.get(i).getContent() : Integer.parseInt(comment.get(i).getType()) == 1 ?
                            getString(R.string.musicinfo_content_1) : getString(R.string.musicinfo_content_2));
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
    public void onPlayButtonClick() {
        if (MainActivity.MID == Constant.GUEST) {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.rest_guest_err));
        } else if (repo != null) {
            addList(false);
            if (MusicService.mediaPlayer != null) {
                MusicService.mediaPlayer.stop();
                MusicService.mediaPlayer.release();
            }
            Intent service = new Intent(context, MusicService.class);
            context.stopService(service);
            context.startService(putIntent(service));

            Intent intent = new Intent(context, MusicActivity.class);
            startActivity(putIntent(intent));
        }
    }

    public Intent putIntent(Intent intent) {
        return intent.putExtra(getString(R.string.music_sid), Integer.parseInt(repo.getSong().get_sid()))
                .putExtra(getString(R.string.music_key), repo.getSong().getKey())
                .putExtra(getString(R.string.music_cover), repo.getSong().getCover())
                .putExtra(getString(R.string.music_title), repo.getSong().getSong_original())
                .putExtra(getString(R.string.music_utaite), repo.getSong().getArtist_en());
    }

    @OnClick({R.id.musicinfo_ribbon_src, R.id.musicinfo_text2_src})
    public void onMusicRibbonClick() {
        if (MainActivity.MID == Constant.GUEST) {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.rest_guest_err));
        } else if (repo != null) {
            RestUtils.getRetrofit()
                    .create(RestUtils.MusicRibbon.class)
                    .musicRibbon(MainActivity.TOKEN, getArguments().getInt(getString(R.string.rest_sid)))
                    .compose(bindToLifecycle())
                    .distinct()
                    .subscribe(o -> {
                                ((MainActivity) context).getToast().setTextShow(getString(ribbonCheck ? R.string.musicinfo_not_ribbon : R.string.musicinfo_ribbon));
                                getFragmentManager().beginTransaction()
                                        .detach(this)
                                        .attach(this)
                                        .commit();
                            },
                            e -> Log.e(TAG, e.toString()));
        }
    }

    @OnClick(R.id.musicinfo_timeline_button)
    public void onMusicTimelineClick() {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(musicinfo_timeline_edittext.getWindowToken(), 0);
        if (MainActivity.MID == Constant.GUEST) {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.rest_guest_err));
            musicinfo_timeline_edittext.getText().clear();
        } else if (repo != null) {
            RestUtils.getRetrofit()
                    .create(RestUtils.MusicTimeline.class)
                    .musicTimeline(MainActivity.TOKEN, getArguments().getInt(getString(R.string.rest_sid)),
                            musicinfo_timeline_edittext.getText().toString().trim())
                    .compose(bindToLifecycle())
                    .distinct()
                    .subscribe(o -> {
                                musicinfo_timeline_edittext.getText().clear();
                                getFragmentManager().beginTransaction()
                                        .detach(this)
                                        .attach(this)
                                        .commit();
                            },
                            e -> Log.e(TAG, e.toString()));
        }
    }

    @OnClick(R.id.musicinfo_addlist_src)
    public void onMusicAddlistClick() {
        if (MainActivity.MID == Constant.GUEST) {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.rest_guest_err));
        } else if (repo != null) {
            addList(true);
        }
    }

    public void addList(boolean isShow) {
        PlaylistVO vo = new PlaylistVO();
        vo.set_sid(Integer.parseInt(repo.getSong().get_sid()))
                .set_aid(Integer.parseInt(repo.getSong().get_aid()))
                .setRibbon(Integer.parseInt(repo.getSong().getRibbon()))
                .setSong_original(repo.getSong().getSong_original())
                .setArtist_en(repo.getSong().getArtist_en())
                .setKey(repo.getSong().getKey())
                .setCover(repo.getSong().getCover());
        MainActivity.playlists.add(vo);

        ArrayList<Integer> arrayList = new ArrayList<>();
        for (PlaylistVO v : MainActivity.playlists) {
            arrayList.add(v.get_sid());
        }
        RestUtils.getRetrofit()
                .create(RestUtils.PlaylistUpdate.class)
                .playlistUpdate(MainActivity.TOKEN, arrayList)
                .subscribe(o -> {
                            if (isShow) {
                                ((MainActivity) context).getToast().setTextShow(getString(R.string.musicinfo_addlist));
                                getFragmentManager().beginTransaction()
                                        .detach(this)
                                        .attach(this)
                                        .commit();
                            }
                        },
                        e -> Log.e(TAG, e.toString()));
    }

    public void requestRetrofit(String what, int index) {
        RestUtils.getRetrofit()
                .create(RestUtils.DefaultApi.class)
                .defaultApi(what, index)
                .compose(bindToLifecycle())
                .distinct()
                .subscribe(response -> {
                            if (repo == null) {
                                repo = response;
                                String cover = repo.getSong().getCover();
                                glide.load(RestUtils.BASE + (cover == null ? getString(R.string.rest_images_cover) : getString(R.string.rest_cover) + cover))
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(musicinfo_cover);

                                for (Ribbon e : repo.getSidebar().getRibbon()) {
                                    if (!ribbonCheck) {
                                        ribbonCheck = MainActivity.MID == Integer.parseInt(e.get_mid());
                                    }
                                }
                                musicinfo_ribbon_src.setBackground(getResources().getDrawable(ribbonCheck ? R.drawable.circle_yellow : R.drawable.circle_black));
                                musicinfo_text2_src.setBackground(getResources().getDrawable(ribbonCheck ? R.drawable.circle_yellow : R.drawable.circle_black));
                                str2Check = getString(ribbonCheck ? R.string.musicinfo_txt2_1 : R.string.musicinfo_txt2_2);

                                vo = new ArrayList<>();
                                String played = repo.getSong().getPlayed();
                                if (Integer.parseInt(played) >= 1000) {
                                    played = String.valueOf(Integer.parseInt(played) / 1000) + "." + String.valueOf(Integer.parseInt(played) % 1000).substring(0, 1) + "K+";
                                }
                                musicinfo_played_right.setText(played);
                                musicinfo_status.setVisibility(View.VISIBLE);
                                musicinfo_recyclerview.setHasFixedSize(true);
                                LinearLayoutManager llm = new LinearLayoutManager(context);
                                llm.setOrientation(LinearLayoutManager.VERTICAL);
                                musicinfo_recyclerview.setLayoutManager(llm);

                                Chained.setTextMany(new TextView[]{musicinfo_text1, musicinfo_text2, musicinfo_text3, musicinfo_utaite, musicinfo_song, musicinfo_songkr, musicinfo_ribbon_right, musicinfo_comment_right},
                                        new String[]{getString(R.string.musicinfo_txt1, "▼"), "▼" + str2Check, getString(R.string.musicinfo_txt3, "▼"), repo.getSong().getArtist_en(),
                                                repo.getSong().getSong_original(), repo.getSong().getSong_kr(), repo.getSong().getRibbon(), repo.getSong().getComment()});
                                Chained.setPaintFlagsMany(musicinfo_ribbon_right, musicinfo_songkr, musicinfo_ribbon, musicinfo_addlist, musicinfo_utaite, musicinfo_song, musicinfo_played_right, musicinfo_comment_right);

                                musicinfo_utaite.setOnClickListener(v ->  {
                                    Fragment fragment = new UtaiteInfoFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(context.getString(R.string.rest_aid), Integer.parseInt(repo.getSong().get_aid()));
                                    fragment.setArguments(bundle);
                                    ((MainActivity) context).getFragmentManager().beginTransaction()
                                            .replace(R.id.content_main, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                });
                            } else {
                                Song song = response.getSong();
                                vo.add(new MainVO(song.getCover(), song.getArtist_cover(), song.getSong_original(), song.getArtist_en(),
                                        song.get_sid(), song.get_aid()));
                                musicinfo_recyclerview.setAdapter(new MainAdapter(context, vo));
                            }
                        },
                        e -> {
                            Log.e(TAG, e.toString());
                            ((MainActivity) context).getToast().setTextShow(getString(R.string.rest_error));
                        });
    }

    public void onProfileClick(String mid) {
        Fragment fragment = new UserInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(R.string.rest_mid), Integer.parseInt(mid));
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment)
                .addToBackStack(null)
                .commit();
    }

}
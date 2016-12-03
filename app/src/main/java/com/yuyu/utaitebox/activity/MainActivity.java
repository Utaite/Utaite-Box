package com.yuyu.utaitebox.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyu.utaitebox.fragment.MainFragment;
import com.yuyu.utaitebox.fragment.MusicListFragment;
import com.yuyu.utaitebox.R;
import com.yuyu.utaitebox.retrofit.Repo;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public interface UtaiteBoxGET {
        @GET("/api/{what}/{index}")
        Call<Repo> listRepos(@Path("what") String what,
                             @Path("index") int index);
    }

    public static final String BASE = "http://utaitebox.com", PROFILE = "1083_1477456743726.jpeg";
    public static String tempCover = null;
    public static int _mid = 1, today = -1;

    private long currentTime;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @BindView(R.id.nav_view)
    NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        nav_view.getMenu().getItem(0).setChecked(true);
        requestRetrofit("member", _mid);
        getFragmentManager().beginTransaction().replace(R.id.content_main, new MainFragment()).commit();
    }

    public void requestRetrofit(String what, int index) {
        Call<Repo> repos = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UtaiteBoxGET.class)
                .listRepos(what, index);
        repos.enqueue(new Callback<Repo>() {
            @Override
            public void onResponse(Call<Repo> call, Response<Repo> response) {
                String username = null, avatar = null, cover = null;
                Repo repo = response.body();
                if (repo.getMember().getUsername() != null) {
                    username = repo.getMember().getUsername();
                }
                if (repo.getProfile().getAvatar() != null) {
                    tempCover = repo.getProfile().getAvatar();
                    avatar = MainActivity.BASE + "/res/profile/image/" + repo.getProfile().getAvatar();
                }
                if (repo.getProfile().getCover() != null) {
                    cover = MainActivity.BASE + "/res/profile/cover/" + repo.getProfile().getCover();
                }
                memberRetrofit(username, avatar, cover);
            }

            @Override
            public void onFailure(Call<Repo> call, Throwable t) {
                Log.e("member Problem", String.valueOf(t));
            }
        });
    }

    public void memberRetrofit(String username, String avatar, String cover) {
        View view = nav_view.getHeaderView(0);
        TextView nav_id = (TextView) view.findViewById(R.id.nav_id);
        ImageView nav_bg1 = (ImageView) view.findViewById(R.id.nav_bg1);
        ImageView nav_img = (ImageView) view.findViewById(R.id.nav_img);
        if (username != null) {
            nav_id.setText(username);
        }
        if (avatar != null) {
            Glide.with(this).load(avatar)
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(nav_img);
        } else {
            Glide.with(this).load(BASE + "/res/profile/image/" + PROFILE)
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(nav_img);
        }
        if (cover != null) {
            Glide.with(this).load(cover)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(nav_bg1);
        } else {
            nav_bg1.setImageResource(android.R.color.transparent);
            nav_bg1.setBackgroundColor(Color.rgb(204, 204, 204));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            if(currentTime + 2000 < System.currentTimeMillis()) {
                currentTime = System.currentTimeMillis();
                Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_home) {
            fragment = new MainFragment();
        } else if (id == R.id.nav_music) {
            fragment = new MusicListFragment();
        } else if (id == R.id.nav_chart) {

        } else if (id == R.id.nav_upload) {

        } else if (id == R.id.nav_timeline) {

        }
        if (fragment != null) {
            getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

}
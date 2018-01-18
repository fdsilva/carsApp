package com.br.androidcandidatetest.controllers;

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.br.androidcandidatetest.R;
import com.br.androidcandidatetest.adapters.ViewPagerAdapter;
import com.br.androidcandidatetest.helpers.DataSetFetchedEvent;
import com.br.androidcandidatetest.helpers.OnCardSelectedEvent;
import com.br.androidcandidatetest.model.Placemarks;
import com.br.androidcandidatetest.model.VehicleCathalog;
import com.br.androidcandidatetest.service.MytaxiService;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        setTabIcons();
        loadData();
        EventBus.getDefault().register(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new VehicleListFragment(),
                getResources().getString(R.string.tab_vehicle_available));
        adapter.addFragment(new MapFragment(),
                getResources().getString(R.string.tab_map));
        viewPager.setAdapter(adapter);
    }

    @SuppressWarnings("ResourceType")
    private void setTabIcons() {
        TypedArray tabIcons = getResources().obtainTypedArray(R.array.tabIcons);
        tabLayout.getTabAt(0).setIcon(tabIcons.getResourceId(0, -1));
        tabLayout.getTabAt(1).setIcon(tabIcons.getResourceId(1, -1));
    }

    /**
     * This method is responsable fot fetch data form the server and update
     * the two fragments.
     */
    public void loadData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        final ArrayList placemarksList = new ArrayList<Placemarks>();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        /**
         * Creating a srvice intance and make a call to the endpoit
         */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MytaxiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        MytaxiService service = retrofit.create(MytaxiService.class);

        Call<VehicleCathalog> requestVehicleCathalog = service.listAllVehicles();

        /**
         * This property "enqueue" make the request goes assincronously
         * avoiding frozen the GUI
         */
        requestVehicleCathalog.enqueue(new Callback<VehicleCathalog>() {
            @Override
            public void onResponse(Call<VehicleCathalog> call, Response<VehicleCathalog> response) {
                if (!response.isSuccessful()) {
                    progressDialog.dismiss();
                    Log.e("Error", "Request Error" + response.code());

                } else {
                    progressDialog.dismiss();
                    VehicleCathalog cathalog = response.body();

                    for (Placemarks p : cathalog.placemarks) {
                        placemarksList.add(p);
                    }
                    /**
                     * Creating and post a event to eventBus
                     * also passing the fetched data from the server
                     */
                    DataSetFetchedEvent event = new DataSetFetchedEvent();
                    event.setPlacemarksList(placemarksList);
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onFailure(Call<VehicleCathalog> call, Throwable t) {
                progressDialog.dismiss();

                Log.e("Error", "Request error " + t.getMessage());
            }
        });
    }

    @Subscribe
    public void onVehicleSelectedEvent(OnCardSelectedEvent event) {
        /** Getting the current tab position and set viewPage to map fragment */
        int currentPosition = tabLayout.getSelectedTabPosition();
        viewPager.setCurrentItem(currentPosition + 1);
    }
}


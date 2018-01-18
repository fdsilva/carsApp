package com.br.androidcandidatetest.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.br.androidcandidatetest.R;
import com.br.androidcandidatetest.helpers.DataSetFetchedEvent;
import com.br.androidcandidatetest.helpers.OnCardSelectedEvent;
import com.br.androidcandidatetest.model.Placemarks;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private List<Placemarks> placemarksList;
    private ArrayList<Marker> markers;
    private GoogleMap googleMap;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /**
         * Using evenBus library to comunication between main activity
         * and the fragments.
         */
        EventBus.getDefault().register(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        placemarksList = new ArrayList<Placemarks>();
        return inflater.inflate(R.layout.map_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mapView = (MapView) getActivity().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        centerCamera(markers);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }

    @Subscribe
    public void onDataSetFetchedEvent(DataSetFetchedEvent event){
        placemarksList.addAll(event.getPlacemarksList());
        setMarkers();
    }

    @Subscribe
    public void onVehicleSelectedEvent(OnCardSelectedEvent event){
        centerCameraAtOneVehicle(event.getSelectedvVehicleCoordinates());
    }

    public void setMarkers () {
        markers = new ArrayList<Marker>();
        for(Placemarks p: placemarksList) {
            markers.add(googleMap.addMarker(new MarkerOptions()
                    .position(
                            new LatLng(p.getCoordinates()[1],
                                    p.getCoordinates()[0])).title(p.getName())
                    .snippet(p.getAddress())));
        }
        centerCamera(markers);
    }

    public void centerCamera(ArrayList<Marker> markers){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 0;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.animateCamera(cameraUpdate);
    }

    public void centerCameraAtOneVehicle(Double[] coordinates){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(new LatLng(coordinates[1],
                coordinates[0]));

        LatLngBounds bounds = builder.build();

        int padding = 0;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.animateCamera(cameraUpdate);
    }
}
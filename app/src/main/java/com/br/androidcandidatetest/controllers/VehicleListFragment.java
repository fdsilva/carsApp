package com.br.androidcandidatetest.controllers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.br.androidcandidatetest.R;
import com.br.androidcandidatetest.helpers.DataSetFetchedEvent;
import com.br.androidcandidatetest.helpers.OnCardSelectedEvent;
import com.br.androidcandidatetest.model.Placemarks;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment class that's display a list of all vehicles available
 */
public class VehicleListFragment extends Fragment {
    private List<Placemarks> vehicles;
    private RecyclerView recyclerView;
    private Activity activity;

    public static VehicleListFragment newInstance(){
        return new VehicleListFragment();
    }

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
        final View view = inflater.inflate(R.layout.taxi_list_fragment_layout, container, false);

        vehicles = new ArrayList<Placemarks>();

        activity = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        recyclerView.setAdapter(new VehicleAdapter(activity, vehicles));

        return view;
    }

    @Subscribe
    public void onEvent(DataSetFetchedEvent event){
        vehicles.addAll(event.getPlacemarksList());
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.MyViewHolder> {
        private Context mContext;
        private List<Placemarks> vehiclesList;

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvVehicleName;
            private TextView tvVehicleAddress;
            public TextView tvVehicleFuelState;
            private TextView tvVehicleExteriorState;
            private TextView tvVehicleInteriorState;
            private TextView tvEngineType;
            private TextView tvVin;

            public MyViewHolder(View view) {
                super(view);
                tvVehicleName = (TextView) view.findViewById(R.id.tv_vehicle_name);
                tvVehicleAddress = (TextView) view.findViewById(R.id.tv_vehicle_address);
                tvVehicleExteriorState = (TextView) view.findViewById(R.id.tv_vehicle_exterior);
                tvVehicleInteriorState = (TextView) view.findViewById(R.id.tv_vehicle_interiior);
                tvVehicleFuelState = (TextView) view.findViewById(R.id.tvFuel);
                tvEngineType = (TextView) view.findViewById(R.id.tvEngineType);
                tvVin = (TextView) view.findViewById(R.id.tv_vin);
            }
        }

        public VehicleAdapter (Context mContext, List<Placemarks> vehicles) {
            this.mContext = mContext;
            this.vehiclesList = vehicles;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.vehicle_card_layout, parent, false);
            return new VehicleAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Placemarks vehicle = vehiclesList.get(position);
            loadViews(holder, vehicle);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                OnCardSelectedEvent event = new OnCardSelectedEvent();
                event.setSelectedvVehicleCoordinates(vehicle.getCoordinates());
                EventBus.getDefault().post(event);
                }
            });
        }

        @Override
        public int getItemCount() {
            return vehiclesList.size();
        }

        public void loadViews(MyViewHolder holder, final Placemarks vehicle){
            holder.tvVehicleName.setText(vehicle.getName());
            holder.tvVehicleAddress.setText(vehicle.getAddress());
            holder.tvVehicleFuelState.setText(Integer.toString(vehicle.getFuel()));
            holder.tvVehicleExteriorState.setText(vehicle.getExterior());
            holder.tvVehicleInteriorState.setText(vehicle.getInterior());
            holder.tvEngineType.setText(vehicle.getEngineType());
            holder.tvVin.setText(vehicle.getVin());

        }
    }
}
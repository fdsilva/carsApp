package com.br.androidcandidatetest.helpers;

import com.br.androidcandidatetest.model.Placemarks;

/**
 * Event to handle cardview CLick
 */
public class OnCardSelectedEvent {

    private Double [] selectedvVehicleCoordinates;

    public Double[] getSelectedvVehicleCoordinates() {
        return selectedvVehicleCoordinates;
    }

    public void setSelectedvVehicleCoordinates(Double[] selectedvVehicleCoordinates) {
        this.selectedvVehicleCoordinates = selectedvVehicleCoordinates;
    }
}

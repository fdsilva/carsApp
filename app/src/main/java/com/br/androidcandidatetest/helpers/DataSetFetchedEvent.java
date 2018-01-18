package com.br.androidcandidatetest.helpers;

import com.br.androidcandidatetest.model.Placemarks;

import java.util.ArrayList;
/**
 * Event class to exchange placemarks data when it was fetched from the server
 */
public class DataSetFetchedEvent {
    private ArrayList<Placemarks> placemarksList;

    public ArrayList<Placemarks> getPlacemarksList() {
        return placemarksList;
    }

    public void setPlacemarksList(ArrayList<Placemarks> placemarksList) {
        this.placemarksList = placemarksList;
    }
}

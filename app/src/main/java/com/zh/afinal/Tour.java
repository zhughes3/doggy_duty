package com.zh.afinal;

import java.util.LinkedList;
import java.util.List;

public class Tour {
    private List<Vertex> tour;
    private double totalDistance;

    public Tour() {
        tour = new LinkedList<>();
        totalDistance = 0.0;
    }

    public int getSize() {
        return tour.size();
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void addStop(Vertex v, double distance) {
        tour.add(v);
        totalDistance += distance;
    }

    public List<Vertex> getTour() {
        return tour;
    }

}

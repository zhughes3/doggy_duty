package com.zh.afinal;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    private LatLng coord;
    private List<Edge> adjList;
    private boolean visited = false;
    private double distanceFromSrc = Double.MAX_VALUE;
    private Vertex prev;

    public Vertex(double lat, double lon) {
        coord = new LatLng(lat, lon);
        adjList = new ArrayList<>();
    }

    public LatLng getCoord() {
        return coord;
    }

    public void setVisited(boolean b) {visited = b;}

    public boolean getVisited() {
        return visited;
    }

    public List<Edge> getAdjList() {
        return adjList;
    }

    public void setAdjList(List<Vertex> nodes) {
        for (Vertex node : nodes) {
            LatLng currentNodeCoord = node.getCoord();
            double lat1 = this.coord.latitude;
            double lon1 = this.coord.longitude;
            double lat2 = currentNodeCoord.latitude;
            double lon2 = currentNodeCoord.longitude;
            if (lat1 != lat2 && lon1 != lon2) {
                //calculate edge distance
                double distance = distance(lat1, lat2, lon1, lon2, 0 ,0);
                Edge edge = new Edge(distance, node);
                adjList.add(edge);
            }
        }
    }

    public void setDistanceFromSrc(double d) {
        distanceFromSrc = d;
    }

    public double getDistanceFromSrc() { return distanceFromSrc; }

    public void setPrev(Vertex v) {
        prev = v;
    }

    public Vertex getPrev() { return prev;}

    //returns distance in meters using Haversine method
    private double distance(double lat1, double lat2, double lon1,
                           double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    @Override
    public String toString() {
        String output = "";
        output += "Vertex (" + coord.latitude + ", " + coord.longitude + ")";
        output += " has " + adjList.size() + " neighbors.\n";
        for (Edge e: adjList) {
            LatLng toVertex = e.getToVertex().getCoord();
            output += "\tDistance to vertex (" + toVertex.latitude + " , " + toVertex.longitude + ") is " +e.getDistance() + ".\n";
        }
        return output;
    }
}

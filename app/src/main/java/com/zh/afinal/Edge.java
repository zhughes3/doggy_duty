package com.zh.afinal;

public class Edge {
    private double distance;
    private Vertex toVertex;

    public Edge(double d, Vertex v) {
        distance = d;
        toVertex = v;
    }

    public double getDistance() {
        return distance;
    }

    public Vertex getToVertex(){
        return toVertex;
    }
}

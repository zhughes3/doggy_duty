package com.zh.afinal;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class Graph {
    private List<Marker> markers;
    private List<Polyline> polylines;
    private List<Vertex> nodes;
    private GoogleMap mMap;
    private List<Vertex> dfsOrdering = new ArrayList<>();
    private List<Vertex> bfsOrdering = new ArrayList<>();
    private List<Vertex> dijkstraOrdering = new LinkedList<>();
    //private Stack<Vertex> dijkstraOrdering = new Stack<>();

    public Graph(GoogleMap map) {
        markers = new ArrayList<>();
        polylines = new ArrayList<>();
        mMap = map;
        nodes = new ArrayList<>();
    }

    static List<LatLng> toLatLngList(List<Vertex> vertices) {
        List<LatLng> output = new ArrayList<>();
        for (Vertex v : vertices) {
            output.add(v.getCoord());
        }
        return output;
    }

    public void clearMarkers() {
        for (Marker m : markers) {
            m.remove();
        }
        markers = new ArrayList<>();
    }

    public List<Vertex> getNodes() { return nodes; }

    public List<Vertex> getDfsOrdering() {
        return dfsOrdering;
    }

    public List<Vertex> getDijkstraOrdering() {
        return dijkstraOrdering;
    }

    public void populateGraph(int numNodes) {
        //populating graph around my current location
        double minLat = 35.917651;
        double maxLat = 35.918062;
        double minLong = 79.060521;
        double maxLong = 79.060886;

        for (int i = 0; i < numNodes ; i++) {
            double lat = ThreadLocalRandom.current().nextDouble(minLat, maxLat);
            double lon = ThreadLocalRandom.current().nextDouble(minLong, maxLong);
            //multiply long by negative 1
            addVertex(lat, lon*(-1), BitmapDescriptorFactory.HUE_RED);
        }
    }

    public void addVertex(Vertex v, float color) {
        nodes.add(v);
        addMarkerToMap(v.getCoord(), color);
    }

    public void addVertex(double lat, double lon, float color) {
        Vertex v = new Vertex(lat, lon);
        nodes.add(v);
        addMarkerToMap(v.getCoord(), color);
    }

    private void addMarkerToMap(LatLng ll, float color) {
        Marker m = mMap.addMarker(new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.defaultMarker(color)));
        markers.add(m);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));

    }

    public void createAdjLists() {
        for (Vertex node : nodes) {
            node.setAdjList(nodes);
        }
        for (Vertex node: nodes) {
            System.out.print(node);
        }
    }

    // recursive depth first search
    public void dfs(Vertex v) {
        v.setVisited(true);
        dfsOrdering.add(v);
        List<Edge> adjList = v.getAdjList();
        for (Edge e : adjList) {
            if (e.getToVertex().getVisited() == false) {
                dfs(e.getToVertex());
            }
        }
    }

    //breadth first search given a source vertex
    public void bfs(Vertex src) {
        List<Vertex> queue = new LinkedList<>();

        src.setVisited(true);
        queue.add(src);

        while (queue.size() != 0) {
            Vertex current = queue.remove(0);
            bfsOrdering.add(current);

            List<Edge> adjList = current.getAdjList();

            for (Edge e : adjList) {
                if (!e.getToVertex().getVisited()) {
                    e.getToVertex().setVisited(true);
                    queue.add(e.getToVertex());
                }
            }
        }
    }

    public void aStar() {

    }

    public void dijkstra(Vertex src) {
        PriorityQueue pq = new PriorityQueue();
        src.setDistanceFromSrc(0);
        for (Vertex v : nodes) {
            pq.add(v);
        }

        while (!pq.isEmpty()) {
            Vertex minDistanceVertex = pq.removeMin(); //get min vertex
            minDistanceVertex.setVisited(true);
            dijkstraOrdering.add(minDistanceVertex);

            List<Edge> adjList = minDistanceVertex.getAdjList();
            for (Edge e : adjList) {
                if (e.getToVertex().getVisited() == false) {
                    double alt = minDistanceVertex.getDistanceFromSrc() + e.getDistance();

                    if (alt < e.getToVertex().getDistanceFromSrc()) {
                        e.getToVertex().setDistanceFromSrc(alt);
                        e.getToVertex().setPrev(minDistanceVertex);
                    }
                }
            }
        }
    }

    public Tour nearestNeigbors(Vertex src) {
        Tour T = new Tour();

        Vertex currentVertex = src;
        src.setVisited(true);
        T.addStop(src, 0.0);
        while (T.getSize() != nodes.size()) {
            //find shortest edge connecting current vertex and unvisited vertex V
            Edge shortestEdge = null;
            List<Edge> adjList = currentVertex.getAdjList();
            for (Edge e: adjList) {
                if (e.getToVertex().getVisited()) {
                    continue;
                }
                if (shortestEdge == null) {
                    shortestEdge = e;
                    continue;
                }

                if (e.getDistance() < shortestEdge.getDistance()) {
                    shortestEdge = e;
                }
            }

            currentVertex = shortestEdge.getToVertex();
            currentVertex.setVisited(true);
            T.addStop(currentVertex, shortestEdge.getDistance());
        }

        //get Distance from currentVertex to src
        Edge finalLeg = null;
        for (Edge e: currentVertex.getAdjList()) {
            Vertex toVertex = e.getToVertex();
            if ((toVertex.getCoord().latitude == src.getCoord().latitude)
                    && (toVertex.getCoord().longitude == src.getCoord().longitude)) {
                finalLeg = e;
                break;
            }
        }
        T.addStop(src, finalLeg.getDistance());
        return T;
    }

    public Tour farthestNeighbors() {
        return new Tour();
    }
}

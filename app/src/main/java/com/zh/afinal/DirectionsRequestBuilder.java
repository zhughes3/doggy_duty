package com.zh.afinal;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class DirectionsRequestBuilder {
    String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?";
    final String COMMA = "%2C";
    final String PIPE = "%7C";
    final String KEY = "XXXXXXXXX";

    public String request(LatLng origin, List<LatLng> waypoints) {
        int destIdx = waypoints.size() - 1;
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        sb.append("origin=" + encode(origin));
        sb.append("&destination=" + encode(waypoints.get(destIdx)));
        sb.append("&waypoints=" + encode(waypoints));
        sb.append("&key=" + KEY);
        return sb.toString();
    }

    private String encode(LatLng ll) {
        return ll.latitude + COMMA + ll.longitude;
    }

    private String encode(List<LatLng> waypoints) {
        StringBuilder sb = new StringBuilder();
        boolean appendPipe = false;

        for (int i = 0; i < waypoints.size(); i++) {
            if (i > 0) {
                sb.append(PIPE);
            }
            sb.append("via=" + encode(waypoints.get(i)));
        }
        return sb.toString();
    }
}

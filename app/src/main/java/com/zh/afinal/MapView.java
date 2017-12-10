package com.zh.afinal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class MapView extends FragmentActivity implements OnMapReadyCallback {
    private boolean DEBUG = true;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<Polyline> polylines;
    private RequestQueue queue;
    private static final String TAG = MapView.class.getSimpleName();
    private Graph graph;
    private ArrayList<LatLng> locations;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location lastLocation;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private DirectionsRequestBuilder GOOG_DIRECTIONS = new DirectionsRequestBuilder();
    private RadioButton radio_ts, radio_d, radio_a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locations = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        polylines = new ArrayList<>();
        radio_ts = (RadioButton) findViewById(R.id.radio_ts);
        radio_d = (RadioButton) findViewById(R.id.radio_d);
        radio_a = (RadioButton) findViewById(R.id.radio_a);
    }

    public void remove(View v) {
        for (Polyline p : polylines) {
            p.remove();
        }
        polylines = new ArrayList<>();
        graph.clearMarkers();
    }

    public void record(View v) {
        if (ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions();
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();
                            graph.addVertex(lastLocation.getLatitude(), lastLocation.getLongitude(), BitmapDescriptorFactory.HUE_BLUE);
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    public void shortestPath(View v) {
        preprocessGraph();
        List<Vertex> nodes = graph.getNodes();

        if (radio_ts.isChecked()) {
            Tour t = graph.nearestNeigbors(nodes.get(nodes.size() - 1));
            drawPath(t.getTour());
        } else if (radio_d.isChecked()) {
            graph.dijkstra(nodes.get(nodes.size() - 1));
            drawPath(graph.getDijkstraOrdering());
        } else if (radio_a.isChecked()) {
            graph.dfs(nodes.get(nodes.size() - 1));
            drawPath(graph.getDfsOrdering());
        } else {
            Toast.makeText(this, "Please pick an algorithm to use below.",
                    Toast.LENGTH_SHORT).show();
        }
//
//        List<LatLng> waypoints = Graph.toLatLngList(graph.getDfsOrdering());
//        LatLng origin = waypoints.get(0);
//        String url = GOOG_DIRECTIONS.request(origin, waypoints);

        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        //you have to
//                        System.out.println(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "err on http request");
//                    }
//            });
        // Add the request to the RequestQueue.
//        queue.add(stringRequest);


    }

    public void drawPath(List<Vertex> list) {
        PolylineOptions options = new PolylineOptions();
        options.color( Color.parseColor( "#CC0000FF" ) );
        options.width( 5 );
        options.visible( true );

        for ( Vertex v : list ) {
            options.add(v.getCoord());
            //.endCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 16));
        }

        Polyline p = mMap.addPolyline(options);
        polylines.add(p);

//        mMap.addPolyline( options ).setEndCap(
//                new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow),
//                        16));
    }

    public void preprocessGraph() {
        graph.createAdjLists();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        graph = new Graph(mMap);
//        graph.addVertex(35.917968, -79.060780, BitmapDescriptorFactory.HUE_RED);
//        graph.addVertex(35.918003, -79.060850, BitmapDescriptorFactory.HUE_RED);
        if (DEBUG) { graph.populateGraph(10); }

        mMap.moveCamera(CameraUpdateFactory.zoomTo(20));
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MapView.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

//            showSnackbar(R.string.permission_rationale, android.R.string.ok,
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            startLocationPermissionRequest();
//                        }
//                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
            }
        }
    }
}

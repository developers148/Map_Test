package com.leads.maptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.internal.$Gson$Preconditions;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.Objects;

import static android.graphics.Color.parseColor;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.maps.Style.MAPBOX_STREETS;
import static com.mapbox.mapboxsdk.style.expressions.Expression.color;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lineProgress;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineGradient;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class showPositionActivity extends AppCompatActivity {

    MapView mapView;
    MapboxMap mapbox;
    private MapboxDirections client;
    private DirectionsRoute currentRoute;

    private static final String ORIGIN_ICON_ID = "origin-icon-id";
    private static final String DESTINATION_ICON_ID = "destination-icon-id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_LINE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";


    // Adjust variables below to change the example's UI
    private static Point ORIGIN_POINT = Point.fromLngLat(90.393358, 23.749831);
    private static Point DESTINATION_POINT = Point.fromLngLat(90.376952, 23.787238);
    private static final float LINE_WIDTH = 6f;
    private static final String ORIGIN_COLOR = "#2096F3";
    private static final String DESTINATION_COLOR = "#F84D4D";



    private LatLng currentPosition = new LatLng(64.900932, -18.167040);
    private GeoJsonSource geoJsonSource;
    private ValueAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_show_position);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapbox = mapboxMap;

                Style.Builder style = new Style.Builder().fromUri(Style.MAPBOX_STREETS)
                        .withImage(ORIGIN_ICON_ID, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                                getResources().getDrawable(R.drawable.green_marker))))
                        .withImage(DESTINATION_ICON_ID, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                                getResources().getDrawable(R.drawable.red_marker))));






                geoJsonSource = new GeoJsonSource("source-id",
                        Feature.fromGeometry(ORIGIN_POINT));







                mapboxMap.setStyle(style, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        initSources(style);
                        initLayers(style);


                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(ORIGIN_POINT.latitude(), ORIGIN_POINT.longitude())) // Sets the new camera position
                                .zoom(12) // Sets the zoom
                                .bearing(180) // Rotate the camera
                                .tilt(30) // Set the camera tilt
                                .build(); // Creates a CameraPosition from the builder

                        mapboxMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(position), 7000);







                        style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                                getResources(), R.drawable.red_marker));

                        style.addSource(geoJsonSource);

                        style.addLayer(new SymbolLayer("layer-id", "source-id")
                                .withProperties(
                                        PropertyFactory.iconImage("marker_icon"),
                                        PropertyFactory.iconIgnorePlacement(true),
                                        PropertyFactory.iconAllowOverlap(true)
                                ));

                        Toast.makeText(
                                showPositionActivity.this,
                                "balsal",
                                Toast.LENGTH_LONG
                        ).show();

                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {
                                if (animator != null && animator.isStarted()) {
                                    currentPosition = (LatLng) animator.getAnimatedValue();
                                    animator.cancel();
                                }

                                animator = ObjectAnimator
                                        .ofObject(latLngEvaluator, currentPosition, point)
                                        .setDuration(2000);
                                animator.addUpdateListener(animatorUpdateListener);
                                animator.start();

                                currentPosition = point;

                                return true;
                            }

                        });





                        //getRoute(mapboxMap, ORIGIN_POINT, DESTINATION_POINT);



                    }
                });
            }
        });
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
// Add the LineLayer to the map. This layer will display the directions route.
        loadedMapStyle.addLayer(new LineLayer(ROUTE_LAYER_ID, ROUTE_LINE_SOURCE_ID).withProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(LINE_WIDTH),
                lineGradient(interpolate(
                        linear(), lineProgress(),

// This is where the gradient color effect is set. 0 -> 1 makes it a two-color gradient
// See LineGradientActivity for an example of a 2+ multiple color gradient line.
                        stop(0f, color(parseColor(ORIGIN_COLOR))),
                        stop(1f, color(parseColor(DESTINATION_COLOR)))
                ))));

// Add the SymbolLayer to the map to show the origin and destination pin markers
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(match(get("originDestination"),
                        literal("origin"),
                        stop("origin", ORIGIN_ICON_ID),
                        stop("destination", DESTINATION_ICON_ID))),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[]{0f, -4f})));
    }
    private void initSources(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_LINE_SOURCE_ID, new GeoJsonOptions().withLineMetrics(true)));
        loadedMapStyle.addSource(new GeoJsonSource(ICON_SOURCE_ID, getOriginAndDestinationFeatureCollection()));
    }


    private FeatureCollection getOriginAndDestinationFeatureCollection() {
        Feature originFeature = Feature.fromGeometry(ORIGIN_POINT);
        originFeature.addStringProperty("originDestination", "origin");
        Feature destinationFeature = Feature.fromGeometry(DESTINATION_POINT);
        destinationFeature.addStringProperty("originDestination", "destination");
        return FeatureCollection.fromFeatures(new Feature[]{originFeature, destinationFeature});
    }








    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                    geoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
                }
            };








    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };
















//    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
//        client = MapboxDirections.builder()
//                .origin(origin)
//                .destination(destination)
//                .overview(DirectionsCriteria.OVERVIEW_FULL)
//                .profile(DirectionsCriteria.PROFILE_WALKING)
//                .accessToken(getString(R.string.mapbox_access_token))
//                .build();
//
//
//
//        CameraPosition position = new CameraPosition.Builder()
//                .target(new LatLng(origin.latitude(), origin.longitude())) // Sets the new camera position
//                .zoom(12) // Sets the zoom
//                .bearing(180) // Rotate the camera
//                .tilt(30) // Set the camera tilt
//                .build(); // Creates a CameraPosition from the builder
//
//        mapboxMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(position), 7000);
//
//
//        client.enqueueCall(new Callback<DirectionsResponse>() {
//            @Override
//            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//// You can get the generic HTTP info about the response
//                Log.e("Response code: %s", String.valueOf(response.code()));
//
//                if (response.body() == null) {
//                    Log.e("error","No routes found, make sure you set the right user and access token.");
//                    return;
//                } else if (response.body().routes().size() < 1) {
//                    Log.e("Error","No routes found");
//                    return;
//                }
//
//// Get the Direction API response's route
//                currentRoute = response.body().routes().get(0);
//
//                if (currentRoute != null) {
//                    if (mapboxMap != null) {
//                        mapboxMap.getStyle(new Style.OnStyleLoaded() {
//                            @Override
//                            public void onStyleLoaded(@NonNull Style style) {
//
//// Retrieve and update the source designated for showing the directions route
//                                GeoJsonSource originDestinationPointGeoJsonSource = style.getSourceAs(ICON_SOURCE_ID);
//
//                                if (originDestinationPointGeoJsonSource != null) {
//                                    originDestinationPointGeoJsonSource.setGeoJson(getOriginAndDestinationFeatureCollection());
//                                }
//
//// Retrieve and update the source designated for showing the directions route
//                                GeoJsonSource lineLayerRouteGeoJsonSource = style.getSourceAs(ROUTE_LINE_SOURCE_ID);
//
//// Create a LineString with the directions route's geometry and
//// reset the GeoJSON source for the route LineLayer source
//                                if (lineLayerRouteGeoJsonSource != null) {
//// Create the LineString from the list of coordinates and then make a GeoJSON
//// FeatureCollection so we can add the line to our map as a layer.
//                                    LineString lineString = LineString.fromPolyline(currentRoute.geometry(), PRECISION_6);
//                                    lineLayerRouteGeoJsonSource.setGeoJson(Feature.fromGeometry(lineString));
//                                }
//                            }
//                        });
//                    }
//                } else {
//                    Log.e("error","Directions route is null");
//                    Toast.makeText(showPositionActivity.this,
//                            "getString(R.string.route_can_not_be_displayed)", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
//                Toast.makeText(showPositionActivity.this,
//                        "getString(R.string.route_call_failure)", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }







}

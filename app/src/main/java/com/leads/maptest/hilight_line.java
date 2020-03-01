package com.leads.maptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class hilight_line extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private LineLayer backgroundLineLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_hilight_line);



        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapbox) {
                mapboxMap = mapbox;

                mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull final Style style) {
                        initSource(style);
                        initLayers(style);
                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {

                                PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
                                RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
                                List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, "line-layer-id");
                                if (featureList.size() > 0) {
                                    for (Feature feature : featureList) {
                                        GeoJsonSource source = style.getSourceAs("background-geojson-source-id");
                                        if (source != null) {
                                            source.setGeoJson(feature);
                                            backgroundLineLayer.setProperties(visibility(VISIBLE));
                                        }
                                    }
                                }

                                return true;
                            }
                        });
                        Toast.makeText(hilight_line.this,"hilight line", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }








    private void initSource(@NonNull Style loadedMapStyle) {
        try {
            loadedMapStyle.addSource(new GeoJsonSource("source-id", new URI("asset://brussels_station_exits.geojson")));
        } catch (URISyntaxException exception) {
            Log.e("log",exception.getMessage());
        }
        loadedMapStyle.addSource(new GeoJsonSource("background-geojson-source-id"));
    }

    /**
     * Set up the main and background LineLayers
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
// Add the regular LineLayer
        LineLayer routeLineLayer = new LineLayer("line-layer-id", "source-id");
        routeLineLayer.setProperties(
                lineWidth(9f),
                lineColor(Color.BLUE),
                lineCap(LINE_CAP_ROUND),
                lineJoin(LINE_JOIN_ROUND)
        );
        loadedMapStyle.addLayer(routeLineLayer);

// Add the background LineLayer that will act as the highlighting effect
        backgroundLineLayer = new LineLayer("background-line-layer-id",
                "background-geojson-source-id");
        backgroundLineLayer.setProperties(
                lineWidth(routeLineLayer.getLineWidth().value + 8),
                lineColor(Color.parseColor("#ff8402")),
                lineCap(LINE_CAP_ROUND),
                lineJoin(LINE_JOIN_ROUND),
                visibility(NONE)
        );
        loadedMapStyle.addLayerBelow(backgroundLineLayer, "line-layer-id");
    }



}

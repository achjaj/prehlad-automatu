package com.achjaj.covidautomat2.ui.regions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.view.*;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.achjaj.covidautomat2.MainActivity;
import com.achjaj.covidautomat2.Utils;
import com.achjaj.covidautomat2.ui.regioninfo.RegionInfoActivity;
import com.achjaj.covidautomat2.R;
import org.achjaj.covid.CovidAutomat;
import org.achjaj.covid.Region;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RegionsFragment extends Fragment {
    private RegionsAdapter adapter;
    private Geocoder geocoder;
    private Context context;
    private ActivityResultLauncher<String> permissionsRequester;
    private ViewGroup parent;
    private MyLocationListener locationListener;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        permissionsRequester = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                if (locationListener == null)
                    locationListener = new MyLocationListener(context);

                ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE))
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.1f, locationListener);
                showFromLocation();
            }
            else {
                System.out.println("DENIED");
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        geocoder = new Geocoder(container.getContext(), Locale.getDefault());
        context = container.getContext();
        parent = container;

        View root = inflater.inflate(R.layout.fragment_regions, container, false);
        final RecyclerView recycler = root.findViewById(R.id.recycler);

        List<String> regions = getRegions();
        adapter = new RegionsAdapter(regions);
        recycler.setAdapter(adapter);
        recycler.addOnItemTouchListener(new RecyclerItemClickListener(context, recycler, (view, position) ->
            showRegionInfoByName(regions.get(position), false)));

        root.findViewById(R.id.fab).setOnClickListener(this::fabAction);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.regions_menu, menu);
        ((SearchView) menu.getItem(0).getActionView()).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    protected static List<String> getRegions() {
        return Utils.covidAutomat.getRegions().keySet().stream()
            .sorted()
            .collect(Collectors.toList());
    }

    private void fabAction(View v) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationListener == null)
                locationListener = new MyLocationListener(context);

            showFromLocation();
        } else
            permissionsRequester.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private void showFromLocation() {
        Location location = locationListener.location;

        if (location != null)
            try {
                Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                if (address.getCountryCode().equals("SK")) {
                    showRegionInfoByName(address.getSubAdminArea(), false);
                } else {
                    Toast.makeText(context, R.string.not_in_sk, Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.no_location, Toast.LENGTH_SHORT).show();
            }
        else
            Toast.makeText(context, R.string.no_location, Toast.LENGTH_SHORT).show();


    }

    private void showRegionInfoByName(String name, boolean nextWeek) {
        Utils.showRegionInfo("regionName", name, getActivity(), nextWeek);
    }

    private static class MyLocationListener implements LocationListener {
        private Location location;

        MyLocationListener(Context context) {
            location = getLocation(context);
        }

        private Location getLocation(Context context) {
            LocationManager manager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
            AtomicReference<Location> best = new AtomicReference<>(null);

            manager.getAllProviders().stream()
                .map(manager::getLastKnownLocation)
                .filter(Objects::nonNull)
                .forEach(l -> {
                    if (best.get() == null || l.getAccuracy() < best.get().getAccuracy())
                        best.set(l);
                });

            return best.get();
        }

        @Override
        public void onLocationChanged(Location location) {
            this.location = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }
}

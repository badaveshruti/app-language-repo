package qnopy.com.qnopyandroid.ui.splitLocationAndMap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import qnopy.com.qnopyandroid.R;

public class LocationFragment extends Fragment {

    public TextView emptylist_view, location_attribute_hdr_tv;
    public ListView listView;
    public RecyclerView rvLocations;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUi(view);
    }

    private void setUpUi(View view) {
        location_attribute_hdr_tv = view.findViewById(R.id.location_attribute_header);
        emptylist_view = view.findViewById(R.id.empty_location);
        listView = view.findViewById(R.id.locListView);
        rvLocations = view.findViewById(R.id.rvLocations);
    }
}
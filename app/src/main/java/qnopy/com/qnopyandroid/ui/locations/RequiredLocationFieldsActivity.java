package qnopy.com.qnopyandroid.ui.locations;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.ui.locations.adapter.MandatoryLocationAdapter;
import qnopy.com.qnopyandroid.ui.locations.model.MandatoryLocation;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class RequiredLocationFieldsActivity extends ProgressDialogActivity {

    private ArrayList<Location> listLocations;
    private int rollAppId;
    private int eventId;
    private RecyclerView rvMandatoryLoc;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_required_location_fields);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Required Locations");

        if (getIntent() != null) {
            listLocations = (ArrayList<Location>) getIntent().getSerializableExtra(GlobalStrings.REQUIRED_LOCATION);
            rollAppId = getIntent().getIntExtra(GlobalStrings.KEY_ROLL_APP_ID, 0);
            eventId = getIntent().getIntExtra(GlobalStrings.KEY_EVENT_ID, 0);
        }

        setUpRecycler();
    }

    private void setUpRecycler() {
        FieldDataSource fieldDataSource = new FieldDataSource(this);

        Collection<Location> list = Collections2.filter(listLocations, new Predicate<Location>() {
            @Override
            public boolean apply(Location loc) {
                return !fieldDataSource.getMandatoryFieldListByLocation(rollAppId + "",
                        eventId + "", loc.getSiteID() + "",
                        loc.getLocationID()).isEmpty();
            }
        });

        MandatoryLocationAdapter adapter = new MandatoryLocationAdapter(Lists.newArrayList(list),
                this, rollAppId + "", eventId + "");
        rvMandatoryLoc = findViewById(R.id.rvMandatoryLoc);
        rvMandatoryLoc.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
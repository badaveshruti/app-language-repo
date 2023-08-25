package qnopy.com.qnopyandroid.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.customView.CustomTextView;

/* this fragment is used only in split mode for tablet inside location detail activity  */
public class FormDetailsFragment extends Fragment {

    public LinearLayout setInfo_container;
    public ImageButton setNavLeft;
    public ImageButton setNavRight;
    public TextView setCount;
    public FloatingActionButton fab_new_reading, fab_delete_current_reading;
    public NestedScrollView nested_scroll_view;
    public LinearLayout locationDetail_master_container;
    public ProgressBar form_loading_bar;
    public FloatingActionsMenu menuMultipleActions;
    public RecyclerView rvForms;
    public CustomTextView tvCalculate;

    public FormDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setInfo_container = view.findViewById(R.id.setData);
        setNavLeft = view.findViewById(R.id.action_nav_left2);
        setNavRight = view.findViewById(R.id.action_nav_right2);
        setCount = view.findViewById(R.id.set_title1);
        fab_new_reading = view.findViewById(R.id.action_new_set);
        fab_delete_current_reading = view.findViewById(R.id.action_delete_current_set);

        nested_scroll_view = view.findViewById(R.id.nestedScrollerid);
        locationDetail_master_container = view.findViewById(R.id.LocDetailList);
        form_loading_bar = view.findViewById(R.id.loading_pb_vertical);
        menuMultipleActions = view.findViewById(R.id.multiple_actions);
        rvForms = view.findViewById(R.id.rvForms);
        tvCalculate = view.findViewById(R.id.tvCalculate);
    }
}
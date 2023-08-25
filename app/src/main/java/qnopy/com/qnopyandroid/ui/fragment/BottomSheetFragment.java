package qnopy.com.qnopyandroid.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import qnopy.com.qnopyandroid.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    Button btnAscending, btnDescending, btnCancel;

    public BottomSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_sort, container, false);

        btnAscending = view.findViewById(R.id.buttonAscending);
        btnDescending = view.findViewById(R.id.buttonDescending);
        btnCancel = view.findViewById(R.id.buttonCancel);

        btnAscending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.ascending), Toast.LENGTH_SHORT).show();
            }
        });

        btnDescending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.descending), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.cancel), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}

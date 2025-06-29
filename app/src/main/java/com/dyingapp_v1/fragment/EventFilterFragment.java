package com.dyingapp_v1.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyingapp_v1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EventFilterFragment extends BottomSheetDialogFragment {

    private CheckBox chkbWorkshop, chkbArtShow, chkbCompetition, chkbOpenRegistration, chkClosedRegistration;
    private Button btnApply;

    // ✅ THÊM `public` ở đây để các class khác dùng được interface này
    public interface OnFilterApplyListener {

        void onFilterApply(boolean isWorkshop, boolean isArtShow, boolean isCompetition,
                           boolean isOpen, boolean isClosed);
    }

    private  OnFilterApplyListener listener;

    public void setOnFilterApplyListener(OnFilterApplyListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_filter, container, false);

        // Ánh xạ các checkbox và button
        chkbWorkshop = view.findViewById(R.id.chkbWorkshop);
        chkbArtShow = view.findViewById(R.id.chkbArtShow);
        chkbCompetition = view.findViewById(R.id.chkbCompetition);
        chkbOpenRegistration = view.findViewById(R.id.chkbOpenRegistration);
        chkClosedRegistration = view.findViewById(R.id.chkClosedRegistration);
        btnApply = view.findViewById(R.id.btnApply);

        btnApply.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterApply(
                        chkbWorkshop.isChecked(),
                        chkbArtShow.isChecked(),
                        chkbCompetition.isChecked(),
                        chkbOpenRegistration.isChecked(),
                        chkClosedRegistration.isChecked()
                );
            }
            dismiss();
        });

        return view;
    }
}

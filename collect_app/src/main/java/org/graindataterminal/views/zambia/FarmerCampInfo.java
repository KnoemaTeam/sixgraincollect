package org.graindataterminal.views.zambia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerCampInfo extends BaseFragment {

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerCampInfo fragment = new FarmerCampInfo();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            screenIndex = getArguments().getInt(SCREEN_INDEX, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.za_farmer_camp_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateBlockName(view);
        updateVetCampName(view);
        updateAgrCampName(view);
        updateVillageName(view);

        return view;
    }

    protected void updateBlockName(View parentView) {
        String blockNameText = ((ZambiaSurvey) survey).getBlockName();

        EditText blockName = (EditText) parentView.findViewById(R.id.blockNameText);
        blockName.setEnabled(!isModeLocked);
        blockName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((ZambiaSurvey) survey).setBlockName(null);
                else
                    ((ZambiaSurvey) survey).setBlockName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(blockNameText))
            blockName.setText(blockNameText);
    }

    protected void updateVetCampName(View parentView) {
        String vetCampNameText = ((ZambiaSurvey) survey).getVetCampName();

        EditText vetCampName = (EditText) parentView.findViewById(R.id.vetCampNameText);
        vetCampName.setEnabled(!isModeLocked);
        vetCampName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((ZambiaSurvey) survey).setVetCampName(null);
                else
                    ((ZambiaSurvey) survey).setVetCampName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(vetCampNameText))
            vetCampName.setText(vetCampNameText);
    }

    protected void updateAgrCampName(View parentView) {
        String agrCampNameText = ((ZambiaSurvey) survey).getAgrCampName();

        EditText agrCampName = (EditText) parentView.findViewById(R.id.agrCampNameText);
        agrCampName.setEnabled(!isModeLocked);
        agrCampName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((ZambiaSurvey) survey).setAgrCampName(null);
                else
                    ((ZambiaSurvey) survey).setAgrCampName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(agrCampNameText))
            agrCampName.setText(agrCampNameText);
    }

    protected void updateVillageName(View parentView) {
        String villageNameText = ((ZambiaSurvey) survey).getVillageName();

        EditText villageName = (EditText) parentView.findViewById(R.id.villageNameText);
        villageName.setEnabled(!isModeLocked);
        villageName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((ZambiaSurvey) survey).setVillageName(null);
                else
                    ((ZambiaSurvey) survey).setVillageName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(villageNameText))
            villageName.setText(villageNameText);
    }
}

package org.graindataterminal.views.cameroon;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.odk.collect.android.R;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.cameroon.FarmerGeneralInfo;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerLocalLanguage extends BaseFragment {
    private static String[] localLanguageIdList = {"english", "french", "abo", "afade", "aghem", "akoose", "akum", "ambele", "atong", "awing", "babanki", "bafanji", "bafaw_balong", "bafia", "bafut", "baghante", "baka", "bakoo", "bakole", "bakundu_balue", "bakweri", "baldamu", "balo", "balundu_bima", "bamali", "bambalang", "bambili", "bamenyam", "bamiléké", "bamukumbit", "bamoun", "bamumbu", "bamunka", "bana", "bangandu", "bangolan", "bangwa", "bansop", "barombi", "bassa", "bassossi", "bata", "batanga", "bati", "bebe", "bebele", "bebil", "beezen", "befang", "bekwel", "beti", "bikya", "bishuo", "bitare", "bokyi", "bomwali", "bu", "bubia", "buduma", "bulu", "bumbung", "busam", "busuu", "buwal", "byep", "caka", "chadian_arabic", "cung", "cuvok", "daba", "dama", "dek", "denya", "dii", "dimbong", "doyayo", "duala", "dugwor", "duli", "duupa", "dzodinka", "efik", "ejagham", "elip", "eman", "esimbi", "eton", "evand", "ewondo", "fali", "fang", "fefe", "fulfulde", "fungom", "gaduwa", "gavar", "gbaya", "ghomala", "guiziga", "kotoko", "mambila", "massa", "mousgoum", "mousseye", "medumba", "moghamo", "mundani", "ndanda", "nso", "tikar", "toupouri", "wute", "yabassi", "yamba"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerLocalLanguage fragment = new FarmerLocalLanguage();
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
        View view = inflater.inflate(R.layout.cm_farmer_local_language, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateLocalLanguage(view, inflater);

        return view;
    }

    protected void updateLocalLanguage(View parentView, LayoutInflater inflater) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        final List<String> sources = farmerGeneralInfo.getLocalLanguage();

        LinearLayout localLanguageLayout1 = (LinearLayout) parentView.findViewById(R.id.localLanguageGroup1);
        localLanguageLayout1.setEnabled(!isModeLocked);

        LinearLayout localLanguageLayout2 = (LinearLayout) parentView.findViewById(R.id.localLanguageGroup2);
        localLanguageLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] languageList = resources.getStringArray(R.array.cameroon_local_language_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !sources.contains(id))
                        sources.add(id);
                }
                else {
                    sources.remove(id);
                }

                farmerGeneralInfo.setLocalLanguage(sources);
                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }
        };

        int index = 1;
        for (String language: languageList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, localLanguageLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(localLanguageIdList[index - 1]);
            checkBox.setText(language);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < languageList.length / 2)
                localLanguageLayout1.addView(checkBox);
            else
                localLanguageLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}

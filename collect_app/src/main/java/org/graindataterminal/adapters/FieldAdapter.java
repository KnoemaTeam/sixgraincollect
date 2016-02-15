package org.graindataterminal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;

import org.graindataterminal.models.base.BaseCrop;
import org.graindataterminal.models.base.BaseField;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.views.base.FieldCenterLocation;
import org.graindataterminal.views.base.FieldLocation;
import org.graindataterminal.views.senegal.CropList;
import org.graindataterminal.views.zambia.CropDetailInfo;
import org.graindataterminal.views.base.CropPhoto;
import org.graindataterminal.views.zambia.CropLastDetailInfo;
import org.graindataterminal.views.zambia.FieldInfo;
import org.graindataterminal.views.base.FieldPhoto;
import org.graindataterminal.views.zambia.FieldFertilizer;
import org.graindataterminal.views.zambia.FieldHarvested;
import org.graindataterminal.views.zambia.FieldPesticide;
import org.graindataterminal.views.zambia.FieldSeed;
import org.graindataterminal.views.zambia.FieldSources;
import org.graindataterminal.views.senegal.FieldRestoration;

import java.util.Arrays;

public class FieldAdapter extends FragmentStatePagerAdapter {
    private BaseDelegate delegate = null;
    private static final int ZERO_PAGES = 0;
    private static final int ZAMBIA_PAGES = 12;
    private static final int TUNISIA_PAGES = 11;
    private static final int SENEGAL_PAGES = 5;
    private boolean isNeedRecreateViews = false;
    private boolean isNeedSwitchPhoto = false;

    public FieldAdapter(FragmentManager fragmentManager, BaseDelegate delegate) {
        super(fragmentManager);
        this.delegate = delegate;
    }

    public void updateAdapter(boolean isNeedRecreateViews) {
        if (this.isNeedRecreateViews != isNeedRecreateViews) {
            this.isNeedRecreateViews = true;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        String surveyVersion = survey.getSurveyVersion();

        String[] answers = BaseCrop.answerIdList;
        int numberPages = ZERO_PAGES;

        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_ZAMBIA).contains(surveyVersion))
            numberPages = ZAMBIA_PAGES;
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_TUNISIA).contains(surveyVersion)) {
            numberPages = TUNISIA_PAGES;
            answers = org.graindataterminal.views.tunisia.FieldInfo.answerIdList;
        }
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(surveyVersion))
            numberPages = SENEGAL_PAGES;

        isNeedSwitchPhoto = false;

        if (!Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(surveyVersion)) {
            String isCropProduction = survey.getCropProduction();
            if (!TextUtils.isEmpty(isCropProduction) && answers[1].equals(isCropProduction)) {
                numberPages = 3;
            }

            BaseField baseField = DataHolder.getInstance().getCurrentField();
            if (baseField != null && answers[0].equals(baseField.getIsField())) {
                isNeedRecreateViews = true;
                isNeedSwitchPhoto = true;
                numberPages = 2;
            }
        }

        return numberPages;
    }

    @Override
    public int getItemPosition(Object object) {
        return isNeedRecreateViews ? POSITION_NONE : POSITION_UNCHANGED;
    }

    @Override
    public Fragment getItem(int position) {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        String surveyVersion = survey.getSurveyVersion();

        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_ZAMBIA).contains(surveyVersion))
            return getZambiaFragment(position);
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_TUNISIA).contains(surveyVersion))
            return getTunisiaFragment(position);
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(surveyVersion))
            return getSenegalFragment(position);

        return null;
    }

    private Fragment getZambiaFragment(int position) {
        switch (position) {
            case 0:
                return FieldInfo.getInstance(delegate);
            case 1:
                if (isNeedSwitchPhoto) {
                    return FieldCenterLocation.getInstance(1);
                }
                else {
                    return FieldPhoto.getInstance(1);
                }
            case 2:
                return FieldCenterLocation.getInstance(2);
            case 3:
                return CropPhoto.getInstance(3);
            case 4:
                return CropDetailInfo.getInstance(4);
            case 5:
                return CropLastDetailInfo.getInstance(5);
            case 6:
                return FieldSources.getInstance(6);
            case 7:
                return FieldSeed.getInstance(7);
            case 8:
                return FieldFertilizer.getInstance(8);
            case 9:
                return FieldPesticide.getInstance(9);
            case 10:
                return FieldHarvested.getInstance(10);
            case 11:
                return FieldLocation.getInstance(11);
        }

        return null;
    }

    private Fragment getTunisiaFragment(int position) {
        switch (position) {
            case 0:
                return org.graindataterminal.views.tunisia.FieldInfo.getInstance(delegate);
            case 1:
                if (isNeedSwitchPhoto) {
                    return FieldCenterLocation.getInstance(1);
                }
                else {
                    return FieldPhoto.getInstance(1);
                }
            case 2:
                return FieldCenterLocation.getInstance(2);
            case 3:
                return CropPhoto.getInstance(3);
            case 4:
                return org.graindataterminal.views.tunisia.CropDetailInfo.getInstance(4);
            case 5:
                return org.graindataterminal.views.tunisia.FieldSources.getInstance(5);
            case 6:
                return org.graindataterminal.views.tunisia.FieldSeed.getInstance(6);
            case 7:
                return org.graindataterminal.views.tunisia.FieldFertilizer.getInstance(7);
            case 8:
                return org.graindataterminal.views.tunisia.FieldPesticide.getInstance(8);
            case 9:
                return org.graindataterminal.views.tunisia.FieldHarvested.getInstance(9);
            case 10:
                return FieldLocation.getInstance(10);
        }

        return null;
    }

    private Fragment getSenegalFragment(int position) {
        switch (position) {
           case 0:
                return org.graindataterminal.views.senegal.FieldFertilizer.getInstance(0);
            case 1:
                return org.graindataterminal.views.senegal.FieldPesticide.getInstance(1);
            case 2:
                return FieldRestoration.getInstance(2);
            case 3:
                return CropList.getInstance(3);
            case 4:
                return FieldLocation.getInstance(4);
        }

        return null;
    }
}

package org.graindataterminal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.views.base.FieldCenterLocation;
import org.graindataterminal.views.cameroon.FarmerBasf;
import org.graindataterminal.views.cameroon.FarmerCppOrders;
import org.graindataterminal.views.cameroon.FarmerCppSustainability;
import org.graindataterminal.views.cameroon.FarmerEducationLevel;
import org.graindataterminal.views.cameroon.FarmerFarmPractice;
import org.graindataterminal.views.cameroon.FarmerFarmScreening;
import org.graindataterminal.views.cameroon.FarmerLocalLanguage;
import org.graindataterminal.views.senegal.FarmerDestinationProduction;
import org.graindataterminal.views.senegal.FarmerDietarySupplement;
import org.graindataterminal.views.senegal.FarmerGenetics;
import org.graindataterminal.views.senegal.FarmerHerdInfo;
import org.graindataterminal.views.senegal.FarmerLivestockServices;
import org.graindataterminal.views.senegal.FarmerPetsFood;
import org.graindataterminal.views.senegal.FarmerRacesHeld;
import org.graindataterminal.views.senegal.FarmerTrainedProduction;
import org.graindataterminal.views.senegal.FarmerVaccines;
import org.graindataterminal.views.zambia.FarmerActivitiesLast;
import org.graindataterminal.views.zambia.FarmerCampInfo;
import org.graindataterminal.views.zambia.FarmerActivities;
import org.graindataterminal.views.zambia.FarmerInformation;
import org.graindataterminal.views.senegal.FarmerAgrActivity;
import org.graindataterminal.views.senegal.FarmerCommonInfo;
import org.graindataterminal.views.senegal.FarmerCompanyInfo;
import org.graindataterminal.views.senegal.FarmerCreditInfo;
import org.graindataterminal.views.senegal.FarmerEducation;
import org.graindataterminal.views.senegal.FarmerMarketingInfo;
import org.graindataterminal.views.senegal.FarmerPolicyInfo;
import org.graindataterminal.views.senegal.FarmerRegion;
import org.graindataterminal.views.zambia.FarmerDetailInfo;
import org.graindataterminal.views.base.FarmerPhoto;
import org.graindataterminal.views.senegal.FarmerService;
import org.graindataterminal.views.senegal.FarmerSoil;
import org.graindataterminal.views.zambia.FarmerLivelihood;

import java.util.Arrays;

public class FarmerAdapter extends FragmentStatePagerAdapter {
    private static final int ZERO_PAGES = 0;
    private static final int ZAMBIA_PAGES = 7;
    private static final int TUNISIA_PAGES = 6;
    private static final int SENEGAL_PAGES = 23;
    private static final int CAMEROON_PAGES = 11;

    private SparseArray<Fragment> screens = new SparseArray<>();

    public FarmerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        String surveyVersion = survey.getSurveyVersion();

        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_ZAMBIA).contains(surveyVersion))
            return ZAMBIA_PAGES;
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_TUNISIA).contains(surveyVersion))
            return TUNISIA_PAGES;
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(surveyVersion))
            return SENEGAL_PAGES;
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(surveyVersion))
            return CAMEROON_PAGES;

        return ZERO_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        if (screens.get(position) != null) {
            return screens.get(position);
        }

        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        String surveyVersion = survey.getSurveyVersion();

        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_ZAMBIA).contains(surveyVersion))
            return getZambiaFragment(position);
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_TUNISIA).contains(surveyVersion))
            return getTunisiaFragment(position);
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(surveyVersion))
            return getSenegalFragment(position);
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(surveyVersion))
            return getCameroonFragment(position);

        return null;
    }

    private Fragment getZambiaFragment(int position) {
        switch (position) {
            case 0:
                screens.put(position, FarmerCampInfo.getInstance(0));
                return screens.get(position);
            case 1:
                screens.put(position, FarmerPhoto.getInstance(1));
                return screens.get(position);
            case 2:
                screens.put(position, FarmerDetailInfo.getInstance(2));
                return screens.get(position);
            case 3:
                screens.put(position, FarmerActivities.getInstance(3));
                return screens.get(position);
            case 4:
                screens.put(position, FarmerActivitiesLast.getInstance(4));
                return screens.get(position);
            case 5:
                screens.put(position, FarmerLivelihood.getInstance(5));
                return screens.get(position);
            case 6:
                screens.put(position, FarmerInformation.getInstance(6));
                return screens.get(position);
        }

        return null;
    }

    private Fragment getTunisiaFragment(int position) {
        switch (position) {
            case 0:
                screens.put(position, org.graindataterminal.views.tunisia.FarmerRegion.getInstance(0));
                return screens.get(position);
            case 1:
                screens.put(position, org.graindataterminal.views.tunisia.FarmerCampInfo.getInstance(1));
                return screens.get(position);
            case 2:
                screens.put(position, org.graindataterminal.views.tunisia.FarmerDetailInfo.getInstance(2));
                return screens.get(position);
            case 3:
                screens.put(position, org.graindataterminal.views.tunisia.FarmerActivities.getInstance(3));
                return screens.get(position);
            case 4:
                screens.put(position, org.graindataterminal.views.tunisia.FarmerLivelihood.getInstance(4));
                return screens.get(position);
            case 5:
                screens.put(position, org.graindataterminal.views.tunisia.FarmerInformation.getInstance(5));
                return screens.get(position);
        }

        return null;
    }

    private Fragment getSenegalFragment(int position) {
        switch (position) {
            case 0:
                screens.put(position, FarmerRegion.getInstance(0));
                return screens.get(position);
            case 1:
                screens.put(position, org.graindataterminal.views.senegal.FarmerCampInfo.getInstance(1));
                return screens.get(position);
            case 2:
                screens.put(position, FarmerCommonInfo.getInstance(2));
                return screens.get(position);
            case 3:
                screens.put(position, FarmerPhoto.getInstance(3));
                return screens.get(position);
            case 4:
                screens.put(position, org.graindataterminal.views.senegal.FarmerDetailInfo.getInstance(4));
                return screens.get(position);
            case 5:
                screens.put(position, FarmerEducation.getInstance(5));
                return screens.get(position);
            case 6:
                screens.put(position, FarmerCompanyInfo.getInstance(6));
                return screens.get(position);
            case 7:
                screens.put(position, FarmerAgrActivity.getInstance(7));
                return screens.get(position);
            case 8:
                screens.put(position, FarmerSoil.getInstance(8));
                return screens.get(position);
            case 9:
                screens.put(position, org.graindataterminal.views.senegal.FarmerActivities.getInstance(9));
                return screens.get(position);
            case 10:
                screens.put(position, FarmerService.getInstance(10));
                return screens.get(position);
            case 11:
                screens.put(position, FarmerCreditInfo.getInstance(11));
                return screens.get(position);
            case 12:
                screens.put(position, FarmerPolicyInfo.getInstance(12));
                return screens.get(position);
            case 13:
                screens.put(position, FarmerMarketingInfo.getInstance(13));
                return screens.get(position);
            case 14:
                screens.put(position, FarmerHerdInfo.getInstance(14));
                return screens.get(position);
            case 15:
                screens.put(position, FarmerLivestockServices.getInstance(15));
                return screens.get(position);
            case 16:
                screens.put(position, FarmerTrainedProduction.getInstance(16));
                return screens.get(position);
            case 17:
                screens.put(position, FarmerRacesHeld.getInstance(17));
                return screens.get(position);
            case 18:
                screens.put(position, FarmerPetsFood.getInstance(18));
                return screens.get(position);
            case 19:
                screens.put(position, FarmerDietarySupplement.getInstance(19));
                return screens.get(position);
            case 20:
                screens.put(position, FarmerVaccines.getInstance(20));
                return screens.get(position);
            case 21:
                screens.put(position, FarmerGenetics.getInstance(21));
                return screens.get(position);
            case 22:
                screens.put(position, FarmerDestinationProduction.getInstance(22));
                return screens.get(position);
        }

        return null;
    }

    private Fragment getCameroonFragment(int position) {
        switch (position) {
            case 0:
                screens.put(position, org.graindataterminal.views.cameroon.FarmerRegion.getInstance(0));
                return screens.get(position);
            case 1:
                screens.put(position, FarmerPhoto.getInstance(1));
                return screens.get(position);
            case 2:
                screens.put(position, FieldCenterLocation.getInstance(2));
                return screens.get(position);
            case 3:
                screens.put(position, org.graindataterminal.views.cameroon.FarmerDetailInfo.getInstance(3));
                return screens.get(position);
            case 4:
                screens.put(position, FarmerLocalLanguage.getInstance(4));
                return screens.get(position);
            case 5:
                screens.put(position, FarmerEducationLevel.getInstance(5));
                return screens.get(position);
            case 6:
                screens.put(position, FarmerFarmScreening.getInstance(6));
                return screens.get(position);
            case 7:
                screens.put(position, FarmerFarmPractice.getInstance(7));
                return screens.get(position);
            case 8:
                screens.put(position, FarmerBasf.getInstance(8));
                return screens.get(position);
            case 9:
                screens.put(position, FarmerCppOrders.getInstance(9));
                return screens.get(position);
            case 10:
                screens.put(position, FarmerCppSustainability.getInstance(10));
                return screens.get(position);
        }

        return null;
    }
}

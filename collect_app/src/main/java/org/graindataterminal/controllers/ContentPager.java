package org.graindataterminal.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.graindataterminal.adapters.ContentAdapter;
import org.graindataterminal.animations.PagerTransformer;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.CustomViewPager;
import org.graindataterminal.views.system.RoundedImageView;
import org.graindataterminal.views.tabs.FarmerEmployeeList;
import org.graindataterminal.views.tabs.FarmerEmployeeLivestockList;
import org.graindataterminal.views.tabs.FarmerEquipmentLivestockList;
import org.graindataterminal.views.tabs.FarmerEquipmentOperationList;
import org.graindataterminal.views.tabs.FarmerEquipmentProductionList;
import org.graindataterminal.views.tabs.FarmerExpensesList;
import org.graindataterminal.views.tabs.FarmerInfrastructureList;
import org.graindataterminal.views.tabs.FarmerLivestockExpenditureList;
import org.graindataterminal.views.tabs.FarmerLivestockProductionList;
import org.graindataterminal.views.tabs.FarmerMovementList;
import org.graindataterminal.views.tabs.FarmerOwnerList;
import org.graindataterminal.views.tabs.FieldsList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentPager extends BaseActivity {
    protected ContentAdapter contentAdapter = null;
    protected CustomViewPager contentPager = null;
    protected TabLayout contentTabs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        setContentPager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setFarmerInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<BaseSurvey> surveys = DataHolder.getInstance().getSurveys();
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_READ_MODE)
            return;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Helper.EDIT_FARMER_REQUEST_CODE) {
                int position = DataHolder.getInstance().findSurveyIndex(survey.getId());

                surveys.remove(position);
                surveys.add(position, survey);
            }

            String now = Helper.getDate();
            survey.setEndTime(now);
            survey.setUpdateTime(now);

            MyApp.setSurveyList(surveys);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_synchronize).setVisible(false);

        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content_pager;
    }

    protected void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_app_logo);
        }
    }

    protected void setFarmerInfo () {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        String surveyVersion = survey.getSurveyVersion();

        RoundedImageView photoView = (RoundedImageView) findViewById(R.id.farmerImageView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCurrentFarmer(ContentPager.this);
            }
        });

        if (survey.getFarmerPhoto() != null) {
            Helper.setImage(photoView, survey.getFarmerPhoto());
            photoView.setIsDefaultImage(false);
        }
        else {
            photoView.setImageResource(R.drawable.ic_perm_identity_white_48dp);
            photoView.setScaleType(ImageView.ScaleType.CENTER);
        }

        String farmerName = null;
        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_ZAMBIA).contains(surveyVersion))
            farmerName = ((ZambiaSurvey) survey).getFarmerName();
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_TUNISIA).contains(surveyVersion))
            farmerName =((TunisiaSurvey) survey).getFarmerName();
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(surveyVersion))
            farmerName = ((SenegalSurvey) survey).getHeadName();

        if (!TextUtils.isEmpty(farmerName)) {
            TextView farmerNameView = (TextView) findViewById(R.id.farmerNameView);
            farmerNameView.setText(farmerName);
        }
    }

    protected void setContentPager() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        int visibility = View.VISIBLE;

        List<Map<String, Fragment>> tabsList = new ArrayList<>();
        final String[] tabsNames = getResources().getStringArray(R.array.app_tab_name_list);

        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(survey.getSurveyVersion())) {
            SenegalSurvey senegalSurvey = (SenegalSurvey) survey;
            String answer = SenegalSurvey.answerIdList[0];
            String hasAgrActivities = senegalSurvey.getHasAgrActivity();
            String hasMadeFarming = senegalSurvey.getFarmingPracticeGroup().getIsMadeFarming();
            String hasEquipment = senegalSurvey.getHasEquipment();

            if (answer.equals(hasAgrActivities)) {
                tabsList.add(new HashMap<String, Fragment>() {{
                    put(tabsNames[0], FieldsList.getInstance());
                }});

                if (answer.equals(senegalSurvey.getHasOperatingEquipment()))
                    tabsList.add(new HashMap<String, Fragment>() {{
                        put(tabsNames[1], FarmerEquipmentOperationList.getInstance());
                    }});

                if (answer.equals(senegalSurvey.getHasInfrastructure()))
                    tabsList.add(new HashMap<String, Fragment>() {{
                        put(tabsNames[2], FarmerInfrastructureList.getInstance());
                    }});
                else
                    tabsList.add(new HashMap<String, Fragment>() {{
                        put(tabsNames[3], FarmerExpensesList.getInstance());
                    }});
            }

            if (answer.equals(hasMadeFarming)) {
                tabsList.add(new HashMap<String, Fragment>() {{
                    put(tabsNames[9], FarmerOwnerList.getInstance());
                }});

                tabsList.add(new HashMap<String, Fragment>() {{
                    put(tabsNames[10], FarmerLivestockProductionList.getInstance());
                }});

                tabsList.add(new HashMap<String, Fragment>() {{
                    put(tabsNames[11], FarmerLivestockExpenditureList.getInstance());
                }});

                tabsList.add(new HashMap<String, Fragment>() {{
                    put(tabsNames[6], FarmerEmployeeLivestockList.getInstance());
                }});

                tabsList.add(new HashMap<String, Fragment>() {{
                    put(tabsNames[12], FarmerMovementList.getInstance());
                }});
            }

            if (answer.equals(hasEquipment)) {
                tabsList.add(new HashMap<String, Fragment>() {{
                    put(tabsNames[7], FarmerEquipmentProductionList.getInstance());
                }});

                tabsList.add(new HashMap<String, Fragment>() {{
                    put(tabsNames[8], FarmerEquipmentLivestockList.getInstance());
                }});
            }

            tabsList.add(new HashMap<String, Fragment>() {{
                put(tabsNames[4], FarmerEmployeeList.getInstance());
            }});
        }
        else
            tabsList.add(new HashMap<String, Fragment>() {{
                put(tabsNames[0], FieldsList.getInstance());
            }});

        contentAdapter = new ContentAdapter(getSupportFragmentManager(), tabsList);
        contentTabs = (TabLayout) findViewById(R.id.contentTabs);
        contentTabs.setVisibility(visibility);

        for (Map<String, Fragment> tabFragment: tabsList) {
            Map.Entry<String, Fragment> entrySet = tabFragment.entrySet().iterator().next();
            contentTabs.addTab(contentTabs.newTab().setText(entrySet.getKey()));
        }

        if (tabsList.size() > 8)
            contentTabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        contentTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                contentPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (!Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(survey.getSurveyVersion()))
            contentTabs.setVisibility(View.GONE);

        contentPager = (CustomViewPager) findViewById(R.id.contentPager);
        contentPager.setPageTransformer(false, new PagerTransformer());
        contentPager.setIsPagingEnabled(true);
        contentPager.setAdapter(contentAdapter);
        contentPager.setCurrentItem(0);
        contentPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(contentTabs));
    }
}

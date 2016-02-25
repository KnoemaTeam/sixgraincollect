package org.graindataterminal.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.graindataterminal.adapters.BaseDelegate;
import org.graindataterminal.adapters.FarmerAdapter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;
import org.graindataterminal.views.base.CustomViewPager;
import org.graindataterminal.views.system.MessageBox;
import org.odk.collect.android.activities.FormChooserList;
import org.odk.collect.android.utilities.DataUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FarmersPager extends BaseActivity implements BaseFragment.FragmentNotificationListener, BaseDelegate {
    protected FarmerAdapter farmerAdapter = null;
    protected CustomViewPager farmerPager = null;

    private Button previous = null;
    private Button forward = null;
    private View separator = null;
    private boolean isPagerLocked = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_farmer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        setNavigationArrows();
        setContentPages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_survey));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_survey));

        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        if (survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED)
            menu.findItem(R.id.action_mode).setVisible(false);
        else if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            menu.findItem(R.id.action_edit_item).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_delete_item:
                showConfirmation();
                break;
            case R.id.action_edit_item:
                updateMode(getIntent(), farmerPager.getCurrentItem());
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_survey),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
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

    protected void setContentPages () {
        farmerAdapter = new FarmerAdapter(getSupportFragmentManager());

        farmerPager = (CustomViewPager) findViewById(R.id.farmerPager);
        farmerPager.setAdapter(farmerAdapter);
        farmerPager.setCurrentItem(0);
        farmerPager.setOnTouchListener(null);
        farmerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    previous.setVisibility(View.GONE);
                    separator.setVisibility(View.GONE);

                    updateForwardButtonView();

                } else if (position == farmerAdapter.getCount() - 1) {
                    previous.setVisibility(View.VISIBLE);
                    separator.setVisibility(View.VISIBLE);

                    if (DataHolder.getInstance().getCurrentSurvey().getMode() == BaseSurvey.SURVEY_READ_MODE)
                        forward.setText(getString(R.string.action_finish));
                    else
                        forward.setText(getString(R.string.action_done));

                    forward.setBackgroundResource(R.drawable.done_navigation_button);
                    forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checkRequiredFields()) {
                                saveFarmerInfo();
                            }
                        }
                    });

                } else if (!isPagerLocked) {
                    previous.setVisibility(View.VISIBLE);
                    separator.setVisibility(View.VISIBLE);

                    updateForwardButtonView();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            farmerPager.setCurrentItem(bundle.getInt(Helper.SELECTED_SURVEY_PAGE));
        }
    }

    protected void showConfirmation () {
        createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                getString(R.string.confirmation_message),
                getString(R.string.confirmation_message_delete_survey),
                Helper.DELETE_MESSAGE);
    }

    protected void updateForwardButtonView () {
        forward.setVisibility(View.VISIBLE);
        forward.setText(getString(R.string.action_next_step));
        forward.setBackgroundResource(R.drawable.navigation_button);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredFields()) {
                    BaseSurvey currentSurvey = DataHolder.getInstance().getCurrentSurvey();
                    if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(currentSurvey.getSurveyVersion())) {
                        int currentPage = farmerPager.getCurrentItem();
                        String regime = ((SenegalSurvey) currentSurvey).getLegalExpRegime();
                        String hasAgrActivity = ((SenegalSurvey) currentSurvey).getHasAgrActivity();

                        if (currentPage == 2 && !regime.equals(SenegalSurvey.regimeIdList[0])) {
                            gotoPage(farmerPager.getCurrentItem() + 3);
                            return;
                        }

                        if (currentPage == 7 && (hasAgrActivity == null || SenegalSurvey.answerIdList[1].equals(hasAgrActivity))) {
                            gotoPage(farmerPager.getCurrentItem() + 7);
                            return;
                        }
                    }

                    gotoPage(farmerPager.getCurrentItem() + 1);
                }
            }
        });
    }

    protected void setNavigationArrows () {
        previous = (Button) findViewById(R.id.previousItem);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSurvey currentSurvey = DataHolder.getInstance().getCurrentSurvey();
                isPagerLocked = false;

                if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(currentSurvey.getSurveyVersion())) {
                    int currentPage = farmerPager.getCurrentItem();
                    String regime = ((SenegalSurvey) currentSurvey).getLegalExpRegime();
                    String hasAgrActivity = ((SenegalSurvey) currentSurvey).getHasAgrActivity();

                    if (currentPage == 5 && !regime.equals(SenegalSurvey.regimeIdList[0])) {
                        gotoPage(farmerPager.getCurrentItem() - 3);
                        return;
                    }

                    if (currentPage == 14 && SenegalSurvey.answerIdList[1].equals(hasAgrActivity)) {
                        gotoPage(farmerPager.getCurrentItem() - 7);
                        return;
                    }
                }

                gotoPage(farmerPager.getCurrentItem() - 1);
            }
        });

        forward = (Button) findViewById(R.id.nextItem);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredFields()) {
                    gotoPage(farmerPager.getCurrentItem() + 1);
                }
            }
        });

        separator = findViewById(R.id.separatorLine);
    }

    @Override
    public void onControlStateChanged(boolean isLocked) {
        Log.i(getClass().getSimpleName(), String.valueOf(isLocked));
        isPagerLocked = isLocked;

        BaseSurvey currentSurvey = DataHolder.getInstance().getCurrentSurvey();
        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(currentSurvey.getSurveyVersion())) {
            if (isLocked) {
                if (DataHolder.getInstance().getCurrentSurvey().getMode() == BaseSurvey.SURVEY_READ_MODE)
                    forward.setText(getString(R.string.action_finish));
                else
                    forward.setText(getString(R.string.action_done));

                forward.setBackgroundResource(R.drawable.done_navigation_button);
                forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkRequiredFields()) {
                            saveFarmerInfo();
                        }
                    }
                });
            }
            else {
                updateForwardButtonView();
            }
        }
    }

    protected boolean checkRequiredFields () {
        Map<String, Boolean> requiredFields = null;
        int screenIndex = farmerPager.getCurrentItem();

        if (requiredScreensFields != null)
            requiredFields = requiredScreensFields.get(screenIndex);

        if (requiredFields == null)
            return true;

        for (Map.Entry<String, Boolean> entry: requiredFields.entrySet()) {
            if (entry.getValue()) {
                getNotificationListener(screenIndex).refreshFragmentView();
                return false;
            }
        }

        return true;
    }

    public boolean checkRequiredFieldByKey (Integer screenIndex, String key) {
        Map<String, Boolean> requiredFields = null;

        if (requiredScreensFields != null)
            requiredFields = requiredScreensFields.get(screenIndex);

        if (requiredFields == null)
            return false;

        if (requiredFields.get(key) == null)
            return false;

        return requiredFields.get(key);
    }

    protected void gotoPage (int numberPage) {
        hideSoftKeyboard();
        farmerPager.setCurrentItem(numberPage);
    }

    protected void saveFarmerInfo () {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            DataUtils.deleteSurvey();

            Intent intent = new Intent(this, FormChooserList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if (tag.equals(Helper.CLOSE_MESSAGE)) {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        }
    }

    @Override
    public void onRequiredFieldChanged(Integer screenIndex, String key, Boolean isEmpty) {
        Map<String, Boolean> requiredFields = null;

        if (requiredScreensFields != null)
            requiredFields = requiredScreensFields.get(screenIndex);

        if (requiredFields == null)
            requiredFields = new HashMap<>();

        requiredFields.put(key, isEmpty);
        requiredScreensFields.put(screenIndex, requiredFields);
    }
}

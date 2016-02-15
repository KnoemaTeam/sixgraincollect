package org.graindataterminal.controllers;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.graindataterminal.adapters.FieldAdapter;
import org.graindataterminal.adapters.BaseDelegate;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.odk.collect.android.R;
import org.graindataterminal.network.LocationService;
import org.graindataterminal.views.base.BaseFragment;
import org.graindataterminal.views.base.CustomViewPager;
import org.graindataterminal.views.system.MessageBox;

import java.util.HashMap;
import java.util.Map;

public class FieldsPager extends BaseActivity implements BaseDelegate, BaseFragment.FragmentNotificationListener {
    protected FieldAdapter fieldAdapter = null;
    protected CustomViewPager fieldPager = null;

    private Button back = null;
    private Button forward = null;
    private View separator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentPages();

        setNavigationArrows();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_field));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_field));

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
                updateMode(getIntent(), fieldPager.getCurrentItem());
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
                    getString(R.string.confirmation_message_close_field),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_field;
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

    protected void showConfirmation () {
        createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                getString(R.string.confirmation_message),
                getString(R.string.confirmation_message_delete_field),
                Helper.DELETE_MESSAGE);
    }

    protected void setContentPages () {
        fieldAdapter = new FieldAdapter(getSupportFragmentManager(), this);

        fieldPager = (CustomViewPager) findViewById(R.id.fieldPager);
        fieldPager.setAdapter(fieldAdapter);
        fieldPager.setCurrentItem(0);
        fieldPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    back.setVisibility(View.GONE);
                    separator.setVisibility(View.GONE);

                    updateForwardButtonView();
                } else if (position == fieldAdapter.getCount() - 1) {
                    back.setVisibility(View.VISIBLE);
                    separator.setVisibility(View.VISIBLE);

                    if (DataHolder.getInstance().getCurrentSurvey().getMode() == BaseSurvey.SURVEY_READ_MODE) {
                        forward.setText(getString(R.string.action_finish));
                    }
                    else {
                        forward.setText(getString(R.string.action_done));
                    }

                    forward.setBackgroundResource(R.color.color_image_button);
                    forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doneEvent(v);
                        }
                    });

                } else {
                    back.setVisibility(View.VISIBLE);
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
            fieldPager.setCurrentItem(bundle.getInt(Helper.SELECTED_SURVEY_PAGE));
        }
    }

    protected void updateForwardButtonView () {
        forward.setVisibility(View.VISIBLE);
        forward.setText(getString(R.string.action_next_step));
        forward.setBackgroundResource(R.color.color_image_button_navigation);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredFields()) {
                    gotoPage(fieldPager.getCurrentItem() + 1);
                }
            }
        });
    }

    protected void setNavigationArrows () {
        back = (Button) findViewById(R.id.previousItem);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage(fieldPager.getCurrentItem() - 1);
            }
        });

        forward = (Button) findViewById(R.id.nextItem);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredFields()) {
                    gotoPage(fieldPager.getCurrentItem() + 1);
                }
            }
        });

        separator = findViewById(R.id.separatorLine);
    }

    protected void gotoPage (int numberPage) {
        hideSoftKeyboard();
        fieldPager.setCurrentItem(numberPage);
    }

    protected void saveFieldInfo () {
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onControlStateChanged(boolean isLocked) {
        if (isLockedState == isLocked)
            return;

        isLockedState = isLocked;
        fieldAdapter.updateAdapter(isLockedState);

        updateForwardButtonView();
    }

    protected void doneEvent (View v) {
        if (checkRequiredFields()) {
            saveFieldInfo();
        }
    }

    protected boolean checkRequiredFields () {
        Map<String, Boolean> requiredFields = null;
        int screenIndex = fieldPager.getCurrentItem();

        if (requiredScreensFields != null)
            requiredFields = requiredScreensFields.get(screenIndex);

        if (requiredFields == null)
            return true;

        for (Map.Entry<String, Boolean> entry: requiredFields.entrySet()) {
            getNotificationListener(screenIndex).refreshFragmentView();
            if (entry.getValue()) {
                return false;
            }
        }

        return true;
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

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteField();
        }
        else if (tag.equals(Helper.CLOSE_MESSAGE)) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }
}

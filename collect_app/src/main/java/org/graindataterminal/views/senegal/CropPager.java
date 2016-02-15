package org.graindataterminal.views.senegal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.odk.collect.android.R;
import org.graindataterminal.adapters.BaseDelegate;
import org.graindataterminal.adapters.CropAdapter;
import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.MyApp;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.graindataterminal.models.senegal.SenegalField;
import org.graindataterminal.views.base.BaseFragment;
import org.graindataterminal.views.base.CustomViewPager;
import org.graindataterminal.views.system.MessageBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CropPager extends BaseActivity implements BaseDelegate,  BaseFragment.FragmentNotificationListener {
    private final static String CURRENT_PAGE_NUMBER = "CURRENT_PAGE_NUMBER";

    protected CropAdapter contentAdapter = null;
    protected CustomViewPager contentPager = null;

    private Button previous = null;
    private Button forward = null;
    private View separator = null;

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

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_crop));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_crop));

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
                updateMode(getIntent(), contentPager.getCurrentItem());
                break;
        }

        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.base_crop_pager;
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

    protected void deleteCrop() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            SenegalField field = (SenegalField) DataHolder.getInstance().getCurrentField();

            List<SenegalCrop> cropList = field.getCropList();
            int index = DataHolder.getInstance().getCropIndex();

            if (index > cropList.size() - 1)
                return;

            cropList.remove(index);
            field.setCropList(cropList);

            MyApp.setSurveyList(surveyList);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        }
    }

    protected void showConfirmation () {
        createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                getString(R.string.confirmation_message),
                getString(R.string.confirmation_message_delete_crop),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_crop),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteCrop();
        }
        else if (tag.equals(Helper.CLOSE_MESSAGE)) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    protected void setContentPages () {
        contentAdapter = new CropAdapter(getSupportFragmentManager());

        contentPager = (CustomViewPager) findViewById(R.id.contentPager);
        contentPager.setAdapter(contentAdapter);
        contentPager.setCurrentItem(0);
        contentPager.setOnTouchListener(null);
        contentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    previous.setVisibility(View.GONE);
                    separator.setVisibility(View.GONE);

                    updateForwardButtonView();

                } else if (position == contentAdapter.getCount() - 1) {
                    previous.setVisibility(View.VISIBLE);
                    separator.setVisibility(View.VISIBLE);

                    if (DataHolder.getInstance().getCurrentSurvey().getMode() == BaseSurvey.SURVEY_READ_MODE) {
                        forward.setText(getString(R.string.action_finish));
                    } else {
                        forward.setText(getString(R.string.action_done));
                    }

                    forward.setBackgroundResource(R.color.color_image_button);
                    forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if (checkRequiredFields()) {
                            saveData();
                            //}
                        }
                    });

                } else {
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
        if (bundle != null)
            contentPager.setCurrentItem(bundle.getInt(CURRENT_PAGE_NUMBER));
    }

    protected void updateForwardButtonView () {
        forward.setVisibility(View.VISIBLE);
        forward.setText(getString(R.string.action_next_step));
        forward.setBackgroundResource(R.color.color_image_button_navigation);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredFields()) {
                    gotoPage(contentPager.getCurrentItem() + 1);
                }
            }
        });
    }

    protected void setNavigationArrows () {
        previous = (Button) findViewById(R.id.previousItem);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage(contentPager.getCurrentItem() - 1);
            }
        });

        forward = (Button) findViewById(R.id.nextItem);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequiredFields()) {
                    gotoPage(contentPager.getCurrentItem() + 1);
                }
            }
        });

        separator = findViewById(R.id.separatorLine);
    }

    protected void gotoPage (int numberPage) {
        hideSoftKeyboard();
        contentPager.setCurrentItem(numberPage);
    }

    protected void saveData () {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    protected void doneEvent (View v) {
        if (checkRequiredFields()) {
            saveData();
        }
    }

    @Override
    public void onControlStateChanged(boolean isLocked) {
        isLockedState = isLocked;

        if (isLocked) {
            contentAdapter.notifyDataSetChanged();

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
        }
        else {
            contentAdapter.notifyDataSetChanged();
            updateForwardButtonView();
        }
    }

    protected boolean checkRequiredFields () {
        Map<String, Boolean> requiredFields = null;
        int screenIndex = contentPager.getCurrentItem();

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

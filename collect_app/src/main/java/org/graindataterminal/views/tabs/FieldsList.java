package org.graindataterminal.views.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.graindataterminal.adapters.ListDataAdapter;
import org.graindataterminal.adapters.ListDataDelegate;
import org.graindataterminal.controllers.FieldsPager;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.base.BaseField;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.graindataterminal.models.senegal.SenegalField;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.tunisia.TunisiaField;
import org.graindataterminal.models.zambia.ZambiaField;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;
import org.odk.collect.android.utilities.DataUtils;

import java.util.Arrays;
import java.util.List;

public class FieldsList extends BaseFragment implements ListDataDelegate {
    protected List<BaseField> dataList = null;
    protected ListDataAdapter dataAdapter = null;

    public static FieldsList getInstance() {
        return new FieldsList();
    }

    @Override
    public void onStart() {
        super.onStart();

        dataList = survey.getFields();

        if (dataAdapter != null)
            dataAdapter.updateDataList(dataList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_field_list, container, false);
        survey = DataHolder.getInstance().getCurrentSurvey();

        setContentList(view);
        setAddImageButton(view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        List fields = survey.getFields();

        if (survey.getMode() == BaseSurvey.SURVEY_READ_MODE)
            return;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Helper.ADD_FIELD_REQUEST_CODE) {
                fields.add(DataHolder.getInstance().getCurrentField());
            }
            else if (requestCode == Helper.EDIT_FIELD_REQUEST_CODE) {
                int position = DataHolder.getInstance().getCurrentFieldIndex();

                fields.remove(position);
                fields.add(position, DataHolder.getInstance().getCurrentField());
            }

            String now = Helper.getDate();
            survey.setEndTime(now);
            survey.setUpdateTime(now);

            DataUtils.setSurveyList(DataHolder.getInstance().getSurveys());
        }

        if (dataAdapter != null)
            dataAdapter.updateDataList(fields);
    }

    protected void setAddImageButton (View view) {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        int visibility = View.VISIBLE;

        if (survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED)
            visibility = View.GONE;

        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addNewItem);
        addButton.setVisibility(visibility);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewField();
            }
        });
    }

    protected void setContentList(View view) {
        dataList = survey.getFields();
        dataAdapter = new ListDataAdapter(dataList, this, ListDataAdapter.LIST_TYPE_DEFAULT);

        ListView dataListView = (ListView) view.findViewById(R.id.fieldsList);
        dataListView.setAdapter(dataAdapter);
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editField(position);
            }
        });
    }

    @Override
    public View getListDataView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        BaseField item = dataList.get(position);

        if (viewHolder.imageView != null) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.imageView.setImageResource(R.drawable.ic_camera_alt_white_36dp);
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER);

            if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(survey.getSurveyVersion()))
                viewHolder.imageView.setVisibility(View.GONE);
            else {
                String image = item.getFieldPhoto();

                if (TextUtils.isEmpty(image)) {
                    if (item.getCrop() != null)
                        image = item.getCrop().getCropPhoto();
                }

                if (!TextUtils.isEmpty(item.getFieldPhoto())) {
                    Helper.setImage(viewHolder.imageView, image);
                    viewHolder.imageView.setIsDefaultImage(false);
                }
            }
        }

        if (viewHolder.titleView != null) {
            viewHolder.titleView.setVisibility(View.VISIBLE);
            viewHolder.titleView.setText(null);

            String name = item.getTitle();

            if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(survey.getSurveyVersion())) {
                List<SenegalCrop> cropList = ((SenegalField) item).getCropList();
                name = "";

                for (int i = 0; i < cropList.size(); i++) {
                    SenegalCrop crop = cropList.get(i);

                    if (!TextUtils.isEmpty(crop.getTitle())) {
                        if (cropList.size() == 1)
                            name = crop.getTitle();
                        else if (cropList.size() - 1 == i)
                            name += crop.getTitle();
                        else
                            name += crop.getTitle() + "," + "\n";
                    }
                }
            }

            if (!TextUtils.isEmpty(name))
                viewHolder.titleView.setText(name);
            else
                viewHolder.titleView.setVisibility(View.GONE);
        }

        if (viewHolder.subtitleView != null) {
            viewHolder.subtitleView.setVisibility(View.GONE);
            viewHolder.subtitleView.setText(null);

            if (viewHolder.titleView != null)
                viewHolder.titleView.setGravity(Gravity.CENTER_VERTICAL);
        }

        if (viewHolder.numberView != null) {
            viewHolder.numberView.setVisibility(View.VISIBLE);
            viewHolder.numberView.setText(null);

            if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(survey.getSurveyVersion())) {
                List<SenegalField> fields = ((SenegalSurvey) survey).getFields();
                if (fields != null && !fields.isEmpty()) {
                    SenegalField field = fields.get(position);
                    List<SenegalCrop> cropList = field.getCropList();

                    if (cropList != null && !cropList.isEmpty()) {
                        viewHolder.numberView.setText(String.valueOf(cropList.size()));
                    }
                    else {
                        viewHolder.numberView.setVisibility(View.GONE);
                    }
                }
            }
            else {
                viewHolder.numberView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    protected void addNewField () {
        try {
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
            survey.setMode(BaseSurvey.SURVEY_EDIT_MODE);

            List fields = survey.getFields();

            DataHolder.getInstance().setCurrentFieldIndex(fields.size());
            String type = DataHolder.getInstance().getSurveysType();

            switch (type) {
                case BaseSurvey.SURVEY_TYPE_ZAMBIA:
                    DataHolder.getInstance().setCurrentField(new ZambiaField());
                    break;
                case BaseSurvey.SURVEY_TYPE_TUNISIA:
                    DataHolder.getInstance().setCurrentField(new TunisiaField());
                    break;
                case BaseSurvey.SURVEY_TYPE_SENEGAL:
                    DataHolder.getInstance().setCurrentField(new SenegalField());
                    break;
            }

            Intent intent = new Intent(getContext(), FieldsPager.class);
            startActivityForResult(intent, Helper.ADD_FIELD_REQUEST_CODE);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void editField (int position) {
        try {
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
            survey.setMode(BaseSurvey.SURVEY_READ_MODE);

            List fields = survey.getFields();
            DataHolder.getInstance().setCurrentFieldIndex(position);

            String type = DataHolder.getInstance().getSurveysType();
            switch (type) {
                case BaseSurvey.SURVEY_TYPE_ZAMBIA:
                    DataHolder.getInstance().setCurrentField(((ZambiaField) fields.get(position)).clone());
                    break;
                case BaseSurvey.SURVEY_TYPE_TUNISIA:
                    DataHolder.getInstance().setCurrentField(((TunisiaField) fields.get(position)).clone());
                    break;
                case BaseSurvey.SURVEY_TYPE_SENEGAL:
                    DataHolder.getInstance().setCurrentField(((SenegalField) fields.get(position)).clone());
                    break;
            }

            Intent intent = new Intent(getContext(), FieldsPager.class);
            startActivityForResult(intent, Helper.EDIT_FIELD_REQUEST_CODE);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
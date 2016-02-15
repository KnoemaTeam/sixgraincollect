package org.graindataterminal.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.graindataterminal.controllers.MyApp;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.base.BaseField;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.RoundedImageView;
import org.odk.collect.android.application.Collect;


import java.util.ArrayList;
import java.util.List;

public class LinearListAdapter extends BaseAdapter {
    public final static int LIST_VIEW_TYPE_FARMERS = 0;
    public final static int LIST_VIEW_TYPE_FIELDS = 1;

    protected int type = LIST_VIEW_TYPE_FARMERS;
    protected List data = new ArrayList<>();

    static class LinearViewHolder {
        RoundedImageView rowImageView;
        TextView rowTitleView;
        TextView rowDateView;
        TextView rowSurveyStateView;
        TextView rowFieldsCountView;
        TextView rowFieldsCountTitleView;
    }

    public LinearListAdapter(int type, List data) {
        this.type = type;
        this.data = data;
    }

    public void setData(List data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data == null || data.isEmpty())
            return 0;

        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = Collect.getContext();
        View rowView = convertView;

        try {
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final LinearViewHolder viewHolder = new LinearViewHolder();

                if (type == LIST_VIEW_TYPE_FARMERS) {
                    rowView = inflater.inflate(R.layout.base_farmer_list_item, parent, false);

                    viewHolder.rowImageView = (RoundedImageView) rowView.findViewById(R.id.farmerImageView);
                    viewHolder.rowTitleView = (TextView) rowView.findViewById(R.id.farmerNameView);
                    viewHolder.rowDateView = (TextView) rowView.findViewById(R.id.farmerDateView);
                    viewHolder.rowSurveyStateView = (TextView) rowView.findViewById(R.id.surveyState);
                    viewHolder.rowFieldsCountView = (TextView) rowView.findViewById(R.id.farmerFieldsCountView);
                    viewHolder.rowFieldsCountTitleView = (TextView) rowView.findViewById(R.id.farmerFieldsCountTitleView);
                }
                else {
                    rowView = inflater.inflate(R.layout.base_field_list_item, parent, false);

                    viewHolder.rowImageView = (RoundedImageView) rowView.findViewById(R.id.fieldImageView);
                    viewHolder.rowTitleView = (TextView) rowView.findViewById(R.id.fieldNameView);
                    viewHolder.rowDateView = (TextView) rowView.findViewById(R.id.fieldDateView);
                }

                rowView.setTag(viewHolder);
            }

            final LinearViewHolder viewHolder = (LinearViewHolder) rowView.getTag();

            if (type == LIST_VIEW_TYPE_FARMERS) {
                BaseSurvey survey = (BaseSurvey) data.get(position);
                int surveyState = survey.getState();

                String farmerName = null;
                String farmerPhoto = survey.getFarmerPhoto();
                String updateTime = survey.getUpdateTime();

                if (survey instanceof ZambiaSurvey
                        || survey instanceof TunisiaSurvey) {
                    farmerName = survey.getFarmerName();
                }
                else if (survey instanceof SenegalSurvey) {
                    farmerName = survey.getHeadName();
                }
                else if (survey instanceof CameroonSurvey) {
                    farmerName = ((CameroonSurvey) survey).getFarmerGeneralInfo().getFirstName();
                }

                if (viewHolder.rowTitleView != null) {
                    viewHolder.rowTitleView.setText(null);

                    if (!TextUtils.isEmpty(farmerName))
                        viewHolder.rowTitleView.setText(farmerName);
                }

                if (viewHolder.rowImageView != null) {
                    viewHolder.rowImageView.setImageResource(R.drawable.ic_perm_identity_white_48dp);
                    viewHolder.rowImageView.setScaleType(ImageView.ScaleType.CENTER);

                    if (!TextUtils.isEmpty(farmerPhoto)) {
                        Helper.setImage(viewHolder.rowImageView, farmerPhoto);
                        viewHolder.rowImageView.setIsDefaultImage(false);
                    }
                }

                if (viewHolder.rowDateView != null) {
                    viewHolder.rowDateView.setText(null);

                    if (!TextUtils.isEmpty(updateTime))
                        viewHolder.rowDateView.setText(Helper.getDate("dd.MM.yyyy HH:mm", updateTime));
                }

                if (viewHolder.rowSurveyStateView != null) {
                    viewHolder.rowSurveyStateView.setText(null);

                    String[] states = context.getResources().getStringArray(R.array.survey_state_list);
                    switch (surveyState) {
                        case ZambiaSurvey.SURVEY_STATE_NEW:
                            viewHolder.rowSurveyStateView.setText(states[1]);
                            viewHolder.rowSurveyStateView.setBackgroundResource(R.drawable.view_survey_state_new);
                            break;
                        case ZambiaSurvey.SURVEY_STATE_SUBMITTED:
                            viewHolder.rowSurveyStateView.setText(states[0]);
                            viewHolder.rowSurveyStateView.setBackgroundResource(R.drawable.view_survey_state_submitted);
                            break;
                        case ZambiaSurvey.SURVEY_STATE_SYNC_ERROR:
                            viewHolder.rowSurveyStateView.setText(states[2]);
                            viewHolder.rowSurveyStateView.setBackgroundResource(R.drawable.view_survey_state_sync_error);
                            break;
                    }
                }


                if (viewHolder.rowFieldsCountView != null && viewHolder.rowFieldsCountTitleView != null) {
                    viewHolder.rowFieldsCountView.setText("0");
                    viewHolder.rowFieldsCountTitleView.setText(context.getString(R.string.label_fields));

                    List fields = survey.getFields();

                    if (fields != null) {
                        viewHolder.rowFieldsCountView.setText(String.valueOf(fields.size()));
                        viewHolder.rowFieldsCountTitleView.setText(fields.size() == 1 ? context.getString(R.string.label_field) : context.getString(R.string.label_fields));
                    }

                    int visibility = View.VISIBLE;
                    if (survey instanceof CameroonSurvey)
                        visibility = View.GONE;

                    viewHolder.rowFieldsCountView.setVisibility(visibility);
                    viewHolder.rowFieldsCountTitleView.setVisibility(visibility);
                }
            }
            else if (type == LIST_VIEW_TYPE_FIELDS) {
                BaseField field = (BaseField) data.get(position);

                String fieldImage = field.getFieldPhoto();
                String fieldName = field.getTitle();

                if (fieldImage == null && field.getCrop() != null) {
                    fieldImage = field.getCrop().getCropPhoto();
                }

                if (viewHolder.rowTitleView != null) {
                    viewHolder.rowTitleView.setVisibility(View.VISIBLE);
                    viewHolder.rowTitleView.setText(null);

                    if (!TextUtils.isEmpty(fieldName))
                        viewHolder.rowTitleView.setText(fieldName);
                    else
                        viewHolder.rowTitleView.setVisibility(View.GONE);
                }

                if (viewHolder.rowImageView != null) {
                    viewHolder.rowImageView.setVisibility(View.VISIBLE);
                    viewHolder.rowImageView.setImageResource(R.drawable.ic_camera_alt_white_36dp);
                    viewHolder.rowImageView.setScaleType(ImageView.ScaleType.CENTER);

                    if (!TextUtils.isEmpty(fieldImage)) {
                        Helper.setImage(viewHolder.rowImageView, fieldImage);
                        viewHolder.rowImageView.setIsDefaultImage(false);
                    }
                    else
                        viewHolder.rowImageView.setVisibility(View.GONE);
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return rowView;
    }
}

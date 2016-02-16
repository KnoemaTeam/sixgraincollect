package org.graindataterminal.views.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.graindataterminal.adapters.ListDataAdapter;
import org.graindataterminal.adapters.ListDataDelegate;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.Animal;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;
import org.graindataterminal.views.senegal.FarmerAnimals;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class FarmerAnimalsList extends BaseFragment implements ListDataDelegate {
    private ListView dataListView = null;
    private List<Animal> dataList = null;
    private ListDataAdapter dataAdapter = null;
    private FloatingActionButton addButton = null;

    public static Fragment getInstance() {
        return new FarmerAnimalsList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sn_farmer_data_list, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        dataList = ((SenegalSurvey) survey).getAnimals();

        updateDataList(view);
        updateAddButton(view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Helper.ADD_ITEM_REQUEST_CODE) {
                Log.i("Animal List: ", "Add animal");

                dataList.add(DataHolder.getInstance().getAnimal());
            }
            else if (requestCode == Helper.EDIT_ITEM_REQUEST_CODE) {
                Log.i("Animal List: ", "Edit animal");

                int position = DataHolder.getInstance().getAnimalIndex();

                dataList.remove(position);
                dataList.add(position, DataHolder.getInstance().getAnimal());
            }

            String now = Helper.getDate();
            ((SenegalSurvey) survey).setEndTime(now);
            ((SenegalSurvey) survey).setUpdateTime(now);
            ((SenegalSurvey) survey).setAnimals(dataList);

            DataUtils.setSurveyList(DataHolder.getInstance().getSurveys());
        }

        if (dataAdapter != null)
            dataAdapter.updateDataList(dataList);
    }

    @Override
    public View getListDataView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Animal item = dataList.get(position);

        if (viewHolder.imageView != null) {
            viewHolder.imageView.setImageResource(R.drawable.ic_camera_alt_white_36dp);
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER);
        }

        if (viewHolder.titleView != null) {
            viewHolder.titleView.setVisibility(View.VISIBLE);
            viewHolder.titleView.setText(null);

            String name = item.getTitle();

            if (!TextUtils.isEmpty(name))
                viewHolder.titleView.setText(name);
            else
                viewHolder.titleView.setVisibility(View.GONE);
        }

        if (viewHolder.subtitleView != null) {
            viewHolder.subtitleView.setVisibility(View.GONE);
            viewHolder.subtitleView.setText(null);
        }

        if (viewHolder.numberView != null) {
            viewHolder.numberView.setVisibility(View.GONE);
            viewHolder.numberView.setText(null);
        }

        return convertView;
    }

    protected void updateDataList(View parentView) {
        if (dataAdapter == null)
            dataAdapter = new ListDataAdapter(dataList, this, ListDataAdapter.LIST_TYPE_DEFAULT);

        if (dataListView == null) {
            dataListView = (ListView) parentView.findViewById(R.id.dataList);
            dataListView.setAdapter(dataAdapter);
            dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    survey.setMode(BaseSurvey.SURVEY_READ_MODE);

                    DataHolder.getInstance().setAnimal(dataList.get(position).clone());
                    DataHolder.getInstance().setAnimalIndex(position);

                    Intent intent = new Intent(getActivity(), FarmerAnimals.class);
                    startActivityForResult(intent, Helper.EDIT_ITEM_REQUEST_CODE);
                }
            });
        }
    }

    protected void updateAddButton(View parentView) {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        int visibility = View.VISIBLE;

        if (survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED)
            visibility = View.GONE;

        addButton = (FloatingActionButton) parentView.findViewById(R.id.addNewItem);
        addButton.setEnabled(!isModeLocked);
        addButton.setVisibility(visibility);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolder.getInstance().setAnimal(new Animal());
                DataHolder.getInstance().setAnimalIndex(dataList.size());

                Intent intent = new Intent(getActivity(), FarmerAnimals.class);
                startActivityForResult(intent, Helper.ADD_ITEM_REQUEST_CODE);
            }
        });
    }
}

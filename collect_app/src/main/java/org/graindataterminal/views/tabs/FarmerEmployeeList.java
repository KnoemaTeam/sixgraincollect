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
import org.graindataterminal.models.senegal.Employee;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;
import org.graindataterminal.views.senegal.FarmerEmployee;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class FarmerEmployeeList extends BaseFragment implements ListDataDelegate {
    private List<Employee> dataList = null;
    private ListDataAdapter dataAdapter = null;

    public static Fragment getInstance() {
        return new FarmerEmployeeList();
    }

    @Override
    public void onStart() {
        super.onStart();

        dataList = ((SenegalSurvey) survey).getEmployees();

        if (dataAdapter != null)
            dataAdapter.updateDataList(dataList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sn_farmer_data_list, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        dataList = ((SenegalSurvey) survey).getEmployees();

        updateDataList(view);
        updateAddButton(view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Helper.ADD_ITEM_REQUEST_CODE) {
                Log.i("Employee List: ", "Add employee");

                dataList.add(DataHolder.getInstance().getEmployee());
            }
            else if (requestCode == Helper.EDIT_ITEM_REQUEST_CODE) {
                Log.i("Employee List: ", "Edit employee");

                int position = DataHolder.getInstance().getEmployeeIndex();

                dataList.remove(position);
                dataList.add(position, DataHolder.getInstance().getEmployee());
            }

            String now = Helper.getDate();
            ((SenegalSurvey) survey).setEndTime(now);
            ((SenegalSurvey) survey).setUpdateTime(now);
            ((SenegalSurvey) survey).setEmployees(dataList);

            DataUtils.setSurveyList(DataHolder.getInstance().getSurveys());
        }

        if (dataAdapter != null)
            dataAdapter.updateDataList(dataList);
    }

    @Override
    public View getListDataView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Employee item = dataList.get(position);

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
            viewHolder.numberView.setVisibility(View.VISIBLE);
            viewHolder.numberView.setText(null);

            String count = item.getHeadWorkforceCount();

            if (!TextUtils.isEmpty(count))
                viewHolder.numberView.setText(count);
            else
                viewHolder.numberView.setVisibility(View.GONE);
        }

        return convertView;
    }

    protected void updateDataList(View parentView) {
        dataAdapter = new ListDataAdapter(dataList, this, ListDataAdapter.LIST_TYPE_DEFAULT);

        ListView dataListView = (ListView) parentView.findViewById(R.id.dataList);
        dataListView.setAdapter(dataAdapter);
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                survey.setMode(BaseSurvey.SURVEY_READ_MODE);

                DataHolder.getInstance().setEmployee(dataList.get(position).clone());
                DataHolder.getInstance().setEmployeeIndex(position);

                Intent intent = new Intent(getActivity(), FarmerEmployee.class);
                startActivityForResult(intent, Helper.EDIT_ITEM_REQUEST_CODE);
            }
        });
    }

    protected void updateAddButton(View parentView) {
        BaseSurvey baseSurvey = DataHolder.getInstance().getCurrentSurvey();
        int visibility = View.VISIBLE;

        if (baseSurvey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED)
            visibility = View.GONE;

        FloatingActionButton addButton = (FloatingActionButton) parentView.findViewById(R.id.addNewItem);
        addButton.setVisibility(visibility);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                survey.setMode(BaseSurvey.SURVEY_EDIT_MODE);

                DataHolder.getInstance().setEmployee(new Employee());
                DataHolder.getInstance().setEmployeeIndex(dataList.size());

                Intent intent = new Intent(getActivity(), FarmerEmployee.class);
                startActivityForResult(intent, Helper.ADD_ITEM_REQUEST_CODE);
            }
        });
    }
}

package org.graindataterminal.views.senegal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.odk.collect.android.R;
import org.graindataterminal.adapters.ListDataAdapter;
import org.graindataterminal.adapters.ListDataDelegate;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.graindataterminal.models.senegal.SenegalField;
import org.graindataterminal.views.base.BaseFragment;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class CropList extends BaseFragment implements ListDataDelegate {
    private ListView dataListView = null;
    private List<SenegalCrop> dataList = null;
    private ListDataAdapter dataAdapter = null;
    private FloatingActionButton addButton = null;
    private LinearLayout cropListView = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        CropList fragment = new CropList();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            screenIndex = getArguments().getInt(SCREEN_INDEX, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sn_farmer_data_list, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        dataList = ((SenegalField) field).getCropList();

        updateDataList(view);
        updateAddButton(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (dataList == null || dataList.isEmpty())
            cropListView.setVisibility(View.VISIBLE);
        else
            cropListView.setVisibility(View.GONE);

        if (dataAdapter != null)
            dataAdapter.updateDataList(dataList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Helper.ADD_ITEM_REQUEST_CODE) {
                Log.i("Animal List: ", "Add crop");

                dataList.add((SenegalCrop) DataHolder.getInstance().getCrop());
            }
            else if (requestCode == Helper.EDIT_ITEM_REQUEST_CODE) {
                Log.i("Animal List: ", "Edit crop");

                int position = DataHolder.getInstance().getAnimalIndex();

                dataList.remove(position);
                dataList.add(position, (SenegalCrop) DataHolder.getInstance().getCrop());
            }

            ((SenegalField) field).setCropList(dataList);
            DataUtils.setSurveyList(DataHolder.getInstance().getSurveys());
        }

        if (dataAdapter != null)
            dataAdapter.updateDataList(dataList);
    }

    @Override
    public View getListDataView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        SenegalCrop item = dataList.get(position);

        if (viewHolder.imageView != null) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.imageView.setImageResource(R.drawable.ic_camera_alt_white_36dp);
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER);

            String image = item.getCropPhoto();

            if (!TextUtils.isEmpty(image)) {
                Helper.setImage(viewHolder.imageView, image);
                viewHolder.imageView.setIsDefaultImage(false);
            }
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
        Context context = Collect.getInstance().getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CoordinatorLayout frameLayout = (CoordinatorLayout) parentView.findViewById(R.id.contentList);

        cropListView = (LinearLayout) inflater.inflate(R.layout.app_crop_view, frameLayout, false);
        cropListView.setVisibility(View.GONE);

        frameLayout.addView(cropListView);

        dataAdapter = new ListDataAdapter(dataList, this, ListDataAdapter.LIST_TYPE_DEFAULT);
        dataListView = (ListView) parentView.findViewById(R.id.dataList);
        dataListView.setAdapter(dataAdapter);
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                survey.setMode(BaseSurvey.SURVEY_READ_MODE);

                DataHolder.getInstance().setCrop(dataList.get(position).clone());
                DataHolder.getInstance().setCropIndex(position);

                Intent intent = new Intent(getActivity(), CropPager.class);
                startActivityForResult(intent, Helper.EDIT_ITEM_REQUEST_CODE);
            }
        });
    }

    protected void updateAddButton(View parentView) {
        addButton = (FloatingActionButton) parentView.findViewById(R.id.addNewItem);
        addButton.setEnabled(!isModeLocked);
        addButton.setVisibility(isModeLocked ? View.GONE : View.VISIBLE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                survey.setMode(BaseSurvey.SURVEY_EDIT_MODE);

                DataHolder.getInstance().setCrop(new SenegalCrop());
                DataHolder.getInstance().setCropIndex(dataList.size());

                Intent intent = new Intent(getActivity(), CropPager.class);
                startActivityForResult(intent, Helper.ADD_ITEM_REQUEST_CODE);
            }
        });
    }
}

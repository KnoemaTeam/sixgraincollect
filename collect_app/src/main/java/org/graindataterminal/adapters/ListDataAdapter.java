package org.graindataterminal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.controllers.MyApp;

import org.graindataterminal.adapters.ListDataDelegate.ViewHolder;
import org.graindataterminal.views.system.RoundedImageView;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.List;

public class ListDataAdapter extends BaseAdapter {
    public final static int LIST_TYPE_DEFAULT = 0;
    public final static int LIST_TYPE_SIMPLE = 1;

    private List<?> data = new ArrayList<>();
    private ListDataDelegate delegate = null;
    private int type = LIST_TYPE_DEFAULT;

    public ListDataAdapter(List<?> data, ListDataDelegate delegate, int type) {
        this.data = data;
        this.delegate = delegate;
        this.type = type;
    }

    public void updateDataList(List<?> data) {
        if (data == null)
            return;

        this.data = data;
        this.notifyDataSetChanged();
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
        if (convertView == null) {
            Context context = Collect.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder viewHolder = new ViewHolder();

            if (type == LIST_TYPE_DEFAULT) {
                convertView = inflater.inflate(R.layout.sn_farmer_data_list_item, parent, false);

                viewHolder.dataContent = (LinearLayout) convertView.findViewById(R.id.dataContent);
                viewHolder.titleView = (TextView) convertView.findViewById(R.id.titleText);
                viewHolder.subtitleView = (TextView) convertView.findViewById(R.id.subtitleText);
                viewHolder.numberView = (TextView) convertView.findViewById(R.id.numberText);
                viewHolder.imageView = (RoundedImageView) convertView.findViewById(R.id.imageView);
            }
            else {
                convertView = inflater.inflate(R.layout.sn_farmer_data_linear_list_item, parent, false);
                viewHolder.titleView = (TextView) convertView.findViewById(R.id.titleText);
            }

            convertView.setTag(viewHolder);
        }

        return delegate.getListDataView(position, convertView, parent);
    }
}

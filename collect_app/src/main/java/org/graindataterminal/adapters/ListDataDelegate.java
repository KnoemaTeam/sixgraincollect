package org.graindataterminal.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.graindataterminal.views.system.RoundedImageView;

public interface ListDataDelegate {
    class ViewHolder {
        public LinearLayout dataContent;
        public RoundedImageView imageView;
        public TextView titleView;
        public TextView subtitleView;
        public TextView numberView;
    }

    View getListDataView(int position, View convertView, ViewGroup parent);
}

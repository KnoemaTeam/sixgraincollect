package org.graindataterminal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContentAdapter extends FragmentStatePagerAdapter {
    protected List<Map<String, Fragment>> tabs = new ArrayList<>();

    public ContentAdapter(FragmentManager fragmentManager, List<Map<String, Fragment>> tabs) {
        super(fragmentManager);
        this.tabs = tabs;
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Map<String, Fragment> tab = tabs.get(position);
        Map.Entry<String, Fragment> entrySet = tab.entrySet().iterator().next();

        return entrySet.getKey();
    }

    @Override
    public Fragment getItem(int position) {
        Map<String, Fragment> tab = tabs.get(position);
        Map.Entry<String, Fragment> entrySet = tab.entrySet().iterator().next();

        return entrySet.getValue();
    }
}

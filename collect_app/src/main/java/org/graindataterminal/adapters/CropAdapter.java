package org.graindataterminal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.graindataterminal.views.base.CropPhoto;
import org.graindataterminal.views.senegal.CropCommonInfo;
import org.graindataterminal.views.senegal.CropDetailInfo;
import org.graindataterminal.views.senegal.CropFrequentAttack;
import org.graindataterminal.views.senegal.CropSeedInfo;

public class CropAdapter extends FragmentStatePagerAdapter {
    private static final int SENEGAL_PAGES = 5;

    public CropAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return SENEGAL_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CropPhoto.getInstance(0);
            case 1:
                return CropCommonInfo.getInstance(1);
            case 2:
                return CropDetailInfo.getInstance(2);
            case 3:
                return CropSeedInfo.getInstance(3);
            case 4:
                return CropFrequentAttack.getInstance(4);
        }

        return null;
    }
}

package org.graindataterminal.views.senegal;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.odk.collect.android.R;
import org.graindataterminal.models.senegal.SenegalCropAttackGroup;
import org.graindataterminal.models.senegal.SenegalCropGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.views.base.BaseFragment;

public class CropFrequentAttack extends BaseFragment {
    private final static String[] vermitNameIdList = {"insectes", "millepattes", "champignon", "autre_attaque"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        CropFrequentAttack fragment = new CropFrequentAttack();
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
        View view = inflater.inflate(R.layout.sn_crop_frequent_attack, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = DataHolder.getInstance().getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateFrequentAttacks(view, inflater);
        updateAttackDate(view);

        return view;
    }

    protected void updateFrequentAttacks(View parentView, LayoutInflater inflater) {
        final SenegalCropAttackGroup attackGroup = ((SenegalCrop) crop).getAttackGroup();
        String attackTypeText = attackGroup.getVerminAttacksType();

        RadioGroup attackTypeGroup = (RadioGroup) parentView.findViewById(R.id.attackTypeGroup);
        attackTypeGroup.setEnabled(!isModeLocked);
        attackTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                attackGroup.setVerminAttacksType(id);
                ((SenegalCrop) crop).setAttackGroup(attackGroup);
            }
        });

        Resources resources = getResources();
        String[] typeList =  resources.getStringArray(R.array.senegal_crop_vermit_attack_list);

        int index = 1;
        boolean notSet = true;

        for (String type: typeList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, attackTypeGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(vermitNameIdList[index - 1]);
            rb.setId(index * 10);

            attackTypeGroup.addView(rb);

            if (!TextUtils.isEmpty(attackTypeText)) {
                if (rb.getTag().equals(attackTypeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            attackTypeGroup.check(10);
    }

    protected void updateAttackDate(View parentView) {
        final SenegalCropAttackGroup attackGroup = ((SenegalCrop) crop).getAttackGroup();
        String attackDateText = attackGroup.getVerminAttacksDate();

        final EditText attackDate = (EditText) parentView.findViewById(R.id.attackDateText);
        attackDate.setEnabled(!isModeLocked);
        attackDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        attackDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    attackGroup.setVerminAttacksDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(attackDate.getText().toString())) {
                                attackDate.setText(correctDate);
                                attackDate.setSelection(correctDate.length());
                            }

                            attackGroup.setVerminAttacksDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }

                ((SenegalCrop) crop).setAttackGroup(attackGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(attackDateText))
            attackDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", attackDateText));
    }
}

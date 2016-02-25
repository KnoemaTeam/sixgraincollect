package org.graindataterminal.views.tunisia;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.FarmersPager;
import org.graindataterminal.helpers.DictionaryManager;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerRegion extends BaseFragment {
    private static String[] governorateIdList = {"gouvernorat_de_Ariana", "gouvernorat_de_Béja", "gouvernorat_de_Ben_Arous", "gouvernorat_de_Bizerte", "gouvernorat_de_Gabès", "gouvernorat_de_Gafsa", "gouvernorat_de_Jendouba", "gouvernorat_de_Kairouan", "gouvernorat_de_Kasserine", "gouvernorat_de_Kébili", "gouvernorat_du_Kef", "gouvernorat_de_Mahdia", "gouvernorat_de_la_Manouba", "gouvernorat_de_Médenine", "gouvernorat_de_Monastir", "gouvernorat_de_Nabeul", "gouvernorat_de_Sfax", "gouvernorat_de_Sidi_Bouzid", "gouvernorat_de_Siliana", "gouvernorat_de_Sousse", "gouvernorat_de_Tataouine", "gouvernorat_de_Tozeur", "gouvernorat_de_Tunis", "gouvernorat_de_Zaghouan"};

    private final static String FARMER_REG_CARD_KEY = "reg_card_number";

    private FarmersPager parentActivity = null;
    private FragmentNotificationListener notificationListener = null;
    private ScrollView scrollView = null;

    private EditText regCardNumber = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerRegion fragment = new FarmerRegion();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tn_farmer_region, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateRegCardNumber(view);
        updateGovernorateGroup(view);

        parentActivity = (FarmersPager) getActivity();
        parentActivity.setNotificationListener(screenIndex,  new BaseActivity.NotificationListener() {
            @Override
            public void refreshFragmentView() {
                if (regCardNumber != null && parentActivity.checkRequiredFieldByKey(screenIndex, FARMER_REG_CARD_KEY)) {
                    regCardNumber.setHint(R.string.title_required_field);
                    regCardNumber.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                    scrollView.smoothScrollTo(0, regCardNumber.getTop());
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            try {
                notificationListener = (FragmentNotificationListener) context;
            }
            catch (ClassCastException exception) {
                throw new ClassCastException(context.toString() + " " + "must implement FarmerNotificationListener");
            }
        }
    }

    protected void updateRegCardNumber(View parentView) {
        String regCardText = ((TunisiaSurvey) survey).getRegCardNumber();

        regCardNumber = (EditText) parentView.findViewById(R.id.regCardText);
        regCardNumber.setEnabled(!isModeLocked);
        regCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((TunisiaSurvey) survey).setRegCardNumber(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, FARMER_REG_CARD_KEY, true);
                } else {
                    ((TunisiaSurvey) survey).setRegCardNumber(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, FARMER_REG_CARD_KEY, false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(regCardText))
            regCardNumber.setText(regCardText);
        else
            notificationListener.onRequiredFieldChanged(screenIndex, FARMER_REG_CARD_KEY, true);
    }

    protected void updateGovernorateGroup(final View parentView) {
        String governorateText = ((TunisiaSurvey) survey).getGovernorate();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.governorate_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner governorateSpinner = (Spinner) parentView.findViewById(R.id.governorateSpinner);
        governorateSpinner.setEnabled(!isModeLocked);
        governorateSpinner.setAdapter(adapter);
        governorateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ident = governorateIdList[position];

                ((TunisiaSurvey) survey).setGovernorate(ident);
                updateDelegationGroup(position, null, parentView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!TextUtils.isEmpty(governorateText)) {
            for (int i = 0; i < governorateIdList.length; i++) {
                if (governorateIdList[i].equals(governorateText)) {
                    governorateSpinner.setSelection(i);
                    updateDelegationGroup(i, ((TunisiaSurvey) survey).getDelegation(), parentView);
                    break;
                }
            }
        }
    }

    private void updateDelegationGroup(int index, String defaultValue, View parentView) {
        int delegation;

        switch (index) {
            case 0:
                delegation = R.array.governorate_ariana;
                break;
            case 1:
                delegation = R.array.governorate_beja;
                break;
            case 2:
                delegation = R.array.governorate_ben_arous;
                break;
            case 3:
                delegation = R.array.governorate_bizerte;
                break;
            case 4:
                delegation = R.array.governorate_gabes;
                break;
            case 5:
                delegation = R.array.governorate_gafsa;
                break;
            case 6:
                delegation = R.array.governorate_jendouba;
                break;
            case 7:
                delegation = R.array.governorate_kairouan;
                break;
            case 8:
                delegation = R.array.governorate_kasserine;
                break;
            case 9:
                delegation = R.array.governorate_kebili;
                break;
            case 10:
                delegation = R.array.governorate_kef;
                break;
            case 11:
                delegation = R.array.governorate_mahdia;
                break;
            case 12:
                delegation = R.array.governorate_manouba;
                break;
            case 13:
                delegation = R.array.governorate_medenine;
                break;
            case 14:
                delegation = R.array.governorate_monastir;
                break;
            case 15:
                delegation = R.array.governorate_nabeul;
                break;
            case 16:
                delegation = R.array.governorate_sfax;
                break;
            case 17:
                delegation = R.array.governorate_sidi_bouzid;
                break;
            case 18:
                delegation = R.array.governorate_siliana;
                break;
            case 19:
                delegation = R.array.governorate_sousse;
                break;
            case 20:
                delegation = R.array.governorate_tataouine;
                break;
            case 21:
                delegation = R.array.governorate_tozeur;
                break;
            case 22:
                delegation = R.array.governorate_tunis;
                break;
            case 23:
                delegation = R.array.governorate_zaghouan;
                break;
            default:
                delegation = R.array.governorate_ariana;
                break;
        }

        String delegationText = ((TunisiaSurvey) survey).getGovernorate();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), delegation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner delegationSpinner = (Spinner) parentView.findViewById(R.id.delegationSpinner);
        delegationSpinner.setEnabled(!isModeLocked);
        delegationSpinner.setAdapter(adapter);
        delegationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) parent.getItemAtPosition(position);
                String text = DictionaryManager.getInstance().findKeyInDictionary(survey.getSurveyVersion(), value, null);

                ((TunisiaSurvey) survey).setDelegation(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!TextUtils.isEmpty(delegationText)) {
            String text = DictionaryManager.getInstance().findValueInDictionaryWithName(survey.getSurveyVersion(), delegationText);
            int position = adapter.getPosition(text);

            if (position != View.NO_ID) {
                delegationSpinner.setSelection(position);
            }
        }
    }
}

package org.graindataterminal.views.senegal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.graindataterminal.helpers.DictionaryManager;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerRegion extends BaseFragment {
    private final static String[] regionIdsList = {"region_dakar", "region_diourbel", "region_fatick", "region_kaffrine", "region_kaolack", "region_kedougou", "region_kolda", "region_louga", "region_matam", "region_saint louis", "region_sedhiou", "region_tambacounda", "region_thies", "region_ziguinchor"};

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sn_farmer_region, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateRegionGroup(view);

        return view;
    }

    protected void updateRegionGroup(final View parentView) {
        String region = ((SenegalSurvey) survey).getRegion();

        ArrayAdapter<CharSequence> regionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.senegal_region_list, android.R.layout.simple_spinner_item);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner regionSpinner = (Spinner) parentView.findViewById(R.id.regionSpinner);
        regionSpinner.setEnabled(!isModeLocked);
        regionSpinner.setAdapter(regionAdapter);
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isModeLocked)
                    return;

                String regionId = regionIdsList[position];

                ((SenegalSurvey) survey).setRegion(regionId);
                updateDepartmentGroup(regionId, parentView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!TextUtils.isEmpty(region)) {
            for (int i = 0; i < regionIdsList.length; i++) {
                String regionId = regionIdsList[i];

                if (regionId.equals(region)) {
                    regionSpinner.setSelection(i);
                    updateDepartmentGroup(regionId, parentView);
                }
            }
        }
    }

    private void updateDepartmentGroup (String regionName, final View parentView) {
        String defaultValue = ((SenegalSurvey) survey).getDepartment();
        int departments;

        switch(regionName) {
            case "region_dakar":
                departments = R.array.senegal_department_dakar_list;
                break;

            case "region_thies":
                departments = R.array.senegal_department_thies_list;
                break;

            case "region_diourbel":
                departments = R.array.senegal_department_diourbel_list;
                break;

            case "region_fatick":
                departments = R.array.senegal_department_fatick_list;
                break;

            case "region_kaolack":
                departments = R.array.senegal_department_kaolack_list;
                break;

            case "region_kedougou":
                departments = R.array.senegal_department_kedougou_list;
                break;

            case "region_kolda":
                departments = R.array.senegal_department_kolda_list;
                break;

            case "region_louga":
                departments = R.array.senegal_department_louga_list;
                break;

            case "region_matam":
                departments = R.array.senegal_department_matam_list;
                break;

            case "region_saint_louis":
                departments = R.array.senegal_department_saint_louis_list;
                break;

            case "region_sedhiou":
                departments = R.array.senegal_department_sedhiou_list;
                break;

            case "region_tambacounda":
                departments = R.array.senegal_department_tambacounda_list;
                break;

            case "region_ziguinchor":
                departments = R.array.senegal_department_ziguinchor_list;
                break;

            case "region_kaffrine":
                departments = R.array.senegal_department_kaffrine_list;
                break;

            default:
                departments = R.array.senegal_department_dakar_list;
                break;
        }

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(getActivity(), departments, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner departmentSpinner = (Spinner) parentView.findViewById(R.id.departmentSpinner);
        departmentSpinner.setEnabled(!isModeLocked);
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isModeLocked)
                    return;

                String value = (String) parent.getItemAtPosition(position);
                String text = DictionaryManager.getInstance().findKeyInDictionary(survey.getSurveyVersion(), value, null);

                ((SenegalSurvey) survey).setDepartment(text);
                updateDistrictGroup(text, parentView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        departmentSpinner.setAdapter(departmentAdapter);
        departmentSpinner.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(defaultValue)) {
            String text = DictionaryManager.getInstance().findValueInDictionaryWithName(survey.getSurveyVersion(), defaultValue);
            int position = departmentAdapter.getPosition(text);

            if (position != View.NO_ID) {
                departmentSpinner.setSelection(position);
                updateDistrictGroup(defaultValue, parentView);
            }
        }
    }

    protected void updateDistrictGroup(String departmentName, final View parentView) {
        String defaultValue = ((SenegalSurvey) survey).getDistrict();
        int districts;

        switch(departmentName) {
            case "departement_dakar":
                districts = R.array.senegal_district_dakar_list;
                break;

            case "departement_guediawaye":
                districts = R.array.senegal_district_guediawaye_list;
                break;

            case "departement_pikine":
                districts = R.array.senegal_district_pikine_list;
                break;

            case "departement_rufisque":
                districts = R.array.senegal_district_rufisque_list;
                break;

            case "departement_tivaoune":
                districts = R.array.senegal_district_tivaoune_list;
                break;

            case "departement_mbour":
                districts = R.array.senegal_district_mbour_list;
                break;

            case "departement_thies":
                districts = R.array.senegal_district_thies_list;
                break;

            case "departement_bambey":
                districts = R.array.senegal_district_bambey_list;
                break;

            case "departement_diourbel":
                districts = R.array.senegal_district_diourbel_list;
                break;

            case "departement_mbacke":
                districts = R.array.senegal_district_mbacke_list;
                break;

            case "departement_fatick":
                districts = R.array.senegal_district_fatick_list;
                break;

            case "departement_foundioune":
                districts = R.array.senegal_district_foundioune_list;
                break;

            case "departement_gossas":
                districts = R.array.senegal_district_gossas_list;
                break;

            case "departement_guinguineo":
                districts = R.array.senegal_district_guinguineo_list;
                break;

            case "departement_kaolack":
                districts = R.array.senegal_district_kaolack_list;
                break;

            case "departement_nioro":
                districts = R.array.senegal_district_nioro_list;
                break;

            case "departement_kedougou":
                districts = R.array.senegal_district_kedougou_list;
                break;

            case "departement_salemata":
                districts = R.array.senegal_district_salemata_list;
                break;

            case "departement_saraya":
                districts = R.array.senegal_district_saraya_list;
                break;

            case "department_kolda":
                districts = R.array.senegal_district_kolda_list;
                break;

            case "department_medina_yoro_foulah":
                districts = R.array.senegal_district_medina_yoro_foulah_list;
                break;

            case "department_velingara":
                districts = R.array.senegal_district_velingara_list;
                break;

            case "departement_kebemer":
                districts = R.array.senegal_district_kebemer_list;
                break;

            case "departement_linguere":
                districts = R.array.senegal_district_linguere_list;
                break;

            case "departement_louga":
                districts = R.array.senegal_district_louga_list;
                break;

            case "departement_matam":
                districts = R.array.senegal_district_matam_list;
                break;

            case "departement_ranelou_ferlo":
                districts = R.array.senegal_district_ranelou_ferlo_list;
                break;

            case "departement_kanel":
                districts = R.array.senegal_district_kanel_list;
                break;

            case "department_dagana":
                districts = R.array.senegal_district_dagana_list;
                break;

            case "department_podor":
                districts = R.array.senegal_district_podor_list;
                break;

            case "department_saint_louis":
                districts = R.array.senegal_district_saint_louis_list;
                break;

            case "department_bounkiling":
                districts = R.array.senegal_district_bounkiling_list;
                break;

            case "department_goudomp":
                districts = R.array.senegal_district_goudomp_list;
                break;

            case "department_sedhiou":
                districts = R.array.senegal_district_sedhiou_list;
                break;

            case "departement_bakel":
                districts = R.array.senegal_district_bakel_list;
                break;

            case "departement_goudiry":
                districts = R.array.senegal_district_goudiry_list;
                break;

            case "departement_koumpentoum":
                districts = R.array.senegal_district_koumpentoum_list;
                break;

            case "departement_tambacounda":
                districts = R.array.senegal_district_tambacounda_list;
                break;

            case "departement_bignona":
                districts = R.array.senegal_district_bignona_list;
                break;

            case "departement_oussouye":
                districts = R.array.senegal_district_oussouye_list;
                break;

            case "departement_ziguinchor":
                districts = R.array.senegal_district_ziguinchor_list;
                break;

            case "departement_birkelane":
                districts = R.array.senegal_district_birkelane_list;
                break;

            case "departement_kaffrine":
                districts = R.array.senegal_district_kaffrine_list;
                break;

            case "departement_koungheul":
                districts = R.array.senegal_district_koungheul_list;
                break;

            case "departement_malem_hoddar":
                districts = R.array.senegal_district_malem_hoddar_list;
                break;

            default:
                districts = R.array.senegal_district_dakar_list;
                break;
        }

        ArrayAdapter<CharSequence> boroughAdapter = ArrayAdapter.createFromResource(getActivity(), districts, android.R.layout.simple_spinner_item);
        boroughAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner districtSpinner = (Spinner) parentView.findViewById(R.id.districtSpinner);
        districtSpinner.setEnabled(!isModeLocked);
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isModeLocked)
                    return;

                String value = (String) parent.getItemAtPosition(position);
                String text = DictionaryManager.getInstance().findKeyInDictionary(survey.getSurveyVersion(), value, null);

                ((SenegalSurvey) survey).setDistrict(text);
                updateTownGroup(text, parentView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtSpinner.setAdapter(boroughAdapter);
        districtSpinner.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(defaultValue)) {
            String text = DictionaryManager.getInstance().findValueInDictionaryWithName(survey.getSurveyVersion(), defaultValue);
            int position = boroughAdapter.getPosition(text);

            if (position != View.NO_ID) {
                districtSpinner.setSelection(position);
                updateTownGroup(defaultValue, parentView);
            }
        }
    }

    protected void updateTownGroup(String districtName, final View parentView) {
        String defaultValue = ((SenegalSurvey) survey).getTown();
        int towns;

        switch(districtName) {
            case "arrondissement_almadies":
                towns = R.array.senegal_town_almadies_list;
                break;

            case "arrondissement_dakar_plateau":
                towns = R.array.senegal_town_dakar_plateau_list;
                break;

            case "arrondissement_grand_dakar":
                towns = R.array.senegal_town_grand_dakar_list;
                break;

            case "arrondissement_parcelles_assainies":
                towns = R.array.senegal_town_parcelles_assainies_list;
                break;

            case "arrondissement_guediawaye":
                towns = R.array.senegal_town_guediawaye_list;
                break;

            case "arrondissement_niayes":
                towns = R.array.senegal_town_niayes_list;
                break;

            case "arrondissement_pikine_dagoudane":
                towns = R.array.senegal_town_pikine_dagoudane_list;
                break;

            case "arrondissement_thiaroye":
                towns = R.array.senegal_town_thiaroye_list;
                break;

            case "arrondissement_rufisque":
                towns = R.array.senegal_town_rufisque_list;
                break;

            case "arrondissement_sangalcam":
                towns = R.array.senegal_town_sangalcam_list;
                break;

            case "arrondissement_meouane":
                towns = R.array.senegal_town_meouane_list;
                break;

            case "arrondissement_merina_dakhar":
                towns = R.array.senegal_town_merina_dakhar_list;
                break;

            case "arrondissement_niakhene":
                towns = R.array.senegal_town_niakhene_list;
                break;

            case "arrondissement_pambal":
                towns = R.array.senegal_town_pambal_list;
                break;

            case "arrondissement_fissel":
                towns = R.array.senegal_town_fissel_list;
                break;

            case "arrondissement_sessene":
                towns = R.array.senegal_town_sessene_list;
                break;

            case "arrondissement_sindia":
                towns = R.array.senegal_town_sindia_list;
                break;

            case "arrondissement_keur_moussa":
                towns = R.array.senegal_town_keur_moussa_list;
                break;

            case "arrondissement_notto":
                towns = R.array.senegal_town_notto_list;
                break;

            case "arrondissement_thienaba":
                towns = R.array.senegal_town_thienaba_list;
                break;

            case "ville_de_thies":
                towns = R.array.senegal_town_de_thies_list;
                break;

            case "arrondissement_baba_garage":
                towns = R.array.senegal_town_baba_garage_list;
                break;

            case "arrondissement_lambaye":
                towns = R.array.senegal_town_lambaye_list;
                break;

            case "arrondissement_ngoye":
                towns = R.array.senegal_town_ngoye_list;
                break;

            case "arrondissement_ndindy":
                towns = R.array.senegal_town_ndindy_list;
                break;

            case "arrondissement_ndoulo":
                towns = R.array.senegal_town_ndoulo_list;
                break;

            case "arrondissement_kael":
                towns = R.array.senegal_town_kael_list;
                break;

            case "arrondissement_ndame":
                towns = R.array.senegal_town_ndame_list;
                break;

            case "arrondissement_taif":
                towns = R.array.senegal_town_taif_list;
                break;

            case "arrond_diakhao":
                towns = R.array.senegal_town_diakhao_list;
                break;

            case "arrond_fimela":
                towns = R.array.senegal_town_fimela_list;
                break;

            case "arrond_niakhar":
                towns = R.array.senegal_town_niakhar_list;
                break;

            case "arrond_tattaguine":
                towns = R.array.senegal_town_tattaguine_list;
                break;

            case "arrond_djilor":
                towns = R.array.senegal_town_djilor_list;
                break;

            case "arrond_niodior":
                towns = R.array.senegal_town_niodior_list;
                break;

            case "arrond_toubacouta":
                towns = R.array.senegal_town_toubacouta_list;
                break;

            case "arrond_colobane":
                towns = R.array.senegal_town_colobane_list;
                break;

            case "arrond_ouadiour":
                towns = R.array.senegal_town_ouadiour_list;
                break;

            case "ard_mbadakhoune":
                towns = R.array.senegal_town_mbadakhoune_list;
                break;

            case "ard_nguelou":
                towns = R.array.senegal_town_nguelou_list;
                break;

            case "arrondissement_koumbal":
                towns = R.array.senegal_town_koumbal_list;
                break;

            case "arrondissement_ndiédieng":
                towns = R.array.senegal_town_ndiédieng_list;
                break;

            case "arrondissement_sibassor":
                towns = R.array.senegal_town_sibassor_list;
                break;

            case "ard_medina_sabakh":
                towns = R.array.senegal_town_medina_sabakh_list;
                break;

            case "ard_paos_koto":
                towns = R.array.senegal_town_paos_koto_list;
                break;

            case "ard_wack_ngouna":
                towns = R.array.senegal_town_wack_ngouna_list;
                break;

            case "arrondissement_bandafassi":
                towns = R.array.senegal_town_bandafassi_list;
                break;

            case "arrondissement_fongolimbi":
                towns = R.array.senegal_town_fongolimbi_list;
                break;

            case "arrondissement_dakateli":
                towns = R.array.senegal_town_dakateli_list;
                break;

            case "arrondissement_dar_salam":
                towns = R.array.senegal_town_dar_salam_list;
                break;

            case "arrondissement_bembou":
                towns = R.array.senegal_town_bembou_list;
                break;

            case "arrondissement_sabodala":
                towns = R.array.senegal_town_sabodala_list;
                break;

            case "arrondissement_dioulacolon":
                towns = R.array.senegal_town_dioulacolon_list;
                break;

            case "arrondissement_mampatim":
                towns = R.array.senegal_town_mampatim_list;
                break;

            case "arrondissement_sare_bidji":
                towns = R.array.senegal_town_sare_bidji_list;
                break;

            case "arrondissement_fafacourou":
                towns = R.array.senegal_town_fafacourou_list;
                break;

            case "arrondissement_ndorna":
                towns = R.array.senegal_town_ndorna_list;
                break;

            case "arrondissement_niaming":
                towns = R.array.senegal_town_niaming_list;
                break;

            case "arrondissement_bonconto":
                towns = R.array.senegal_town_bonconto_list;
                break;

            case "arrondissement_pakour":
                towns = R.array.senegal_town_pakour_list;
                break;

            case "arrondissement__sare_coly_salle":
                towns = R.array.senegal_town_sare_coly_salle_list;
                break;

            case "arrondissement_darou_mousty":
                towns = R.array.senegal_town_darou_mousty_list;
                break;

            case "arrondissement_ndande":
                towns = R.array.senegal_town_ndande_list;
                break;

            case "arrondissement_sagatta":
                towns = R.array.senegal_town_sagatta_list;
                break;

            case "arrondissement_barkedji":
                towns = R.array.senegal_town_barkedji_list;
                break;

            case "arrondissement_dodji":
                towns = R.array.senegal_town_dodji_list;
                break;

            case "arrondissement_sagatta_djolof":
                towns = R.array.senegal_town_sagatta_djolof_list;
                break;

            case "arrondissement_mbediene":
                towns = R.array.senegal_town_mbediene_list;
                break;

            case "arrondissement_sakal":
                towns = R.array.senegal_town_sakal_list;
                break;

            case "arrondissement_coki":
                towns = R.array.senegal_town_coki_list;
                break;

            case "arrondissement_keur_momar_sarr":
                towns = R.array.senegal_town_keur_momar_sarr_list;
                break;

            case "arrondissement_yang_yang":
                towns = R.array.senegal_town_yang_yang_list;
                break;

            case "arrondissement_ogo":
                towns = R.array.senegal_town_ogo_list;
                break;

            case "arrondissement_agnam_civol":
                towns = R.array.senegal_town_agnam_civol_list;
                break;

            case "arrondissement_velingara":
                towns = R.array.senegal_town_velingara_list;
                break;

            case "arrondissement__orkadiere":
                towns = R.array.senegal_town_orkadiere_list;
                break;

            case "arrondissement_wouro_sidy":
                towns = R.array.senegal_town_wouro_sidy_list;
                break;

            case "ardt_mbane":
                towns = R.array.senegal_town_mbane_list;
                break;

            case "ardt_ndiaye":
                towns = R.array.senegal_town_ndiaye_list;
                break;

            case "ardt_cas_cas":
                towns = R.array.senegal_town_cas_cas_list;
                break;

            case "ardt_gamadji_sare":
                towns = R.array.senegal_town_gamadji_sare_list;
                break;

            case "ardt_salde":
                towns = R.array.senegal_town_salde_list;
                break;

            case "ardt_thile_boubacar":
                towns = R.array.senegal_town_thile_boubacar_list;
                break;

            case "ardt_rao":
                towns = R.array.senegal_town_rao_list;
                break;

            case "arrondissement_boghal":
                towns = R.array.senegal_town_boghal_list;
                break;

            case "arrondissement_diaroume":
                towns = R.array.senegal_town_diaroume_list;
                break;

            case "arrondissement_bona":
                towns = R.array.senegal_town_bona_list;
                break;

            case "arrondissement_djibanar":
                towns = R.array.senegal_town_djibanar_list;
                break;

            case "arrondissement_karantaba":
                towns = R.array.senegal_town_karantaba_list;
                break;

            case "arrondissement_simbandi_brassou":
                towns = R.array.senegal_town_simbandi_brassou_list;
                break;

            case "arrondissement_diende":
                towns = R.array.senegal_town_diende_list;
                break;

            case "arrondissement_djibabouya":
                towns = R.array.senegal_town_djibabouya_list;
                break;

            case "arrondissement_djiredji":
                towns = R.array.senegal_town_djiredji_list;
                break;

            case "arrondissement_bele":
                towns = R.array.senegal_town_bele_list;
                break;

            case "arrondissement_keniaba":
                towns = R.array.senegal_town_keniaba_list;
                break;

            case "arrondissement_moudery":
                towns = R.array.senegal_town_moudery_list;
                break;

            case "arrondissement_bala":
                towns = R.array.senegal_town_bala_list;
                break;

            case "arrondissement_boynguel_bamba":
                towns = R.array.senegal_town_boynguel_bamba_list;
                break;

            case "arrondissement_dianke_makha":
                towns = R.array.senegal_town_dianke_makha_list;
                break;

            case "arrondissement_koulor":
                towns = R.array.senegal_town_koulor_list;
                break;

            case "arrondissement_bamba_thialene":
                towns = R.array.senegal_town_bamba_thialene_list;
                break;

            case "arrondissement_kouthiaba_wolof":
                towns = R.array.senegal_town_kouthiaba_wolof_list;
                break;

            case "arrondissement_koussanar":
                towns = R.array.senegal_town_koussanar_list;
                break;

            case "arrondissement_makakoulibantan":
                towns = R.array.senegal_town_makakoulibantan_list;
                break;

            case "arrondissement_missirah":
                towns = R.array.senegal_town_missirah_list;
                break;

            case "arrondissement_kataba2":
                towns = R.array.senegal_town_kataba2_list;
                break;

            case "arrondissement_kataba3":
                towns = R.array.senegal_town_kataba3_list;
                break;

            case "arrondissement_kataba4":
                towns = R.array.senegal_town_kataba4_list;
                break;

            case "arrondissement_sindian":
                towns = R.array.senegal_town_sindian_list;
                break;

            case "arrondissement_tendouck":
                towns = R.array.senegal_town_tendouck_list;
                break;

            case "arrondissement_tenghory":
                towns = R.array.senegal_town_tenghory_list;
                break;

            case "arrondissement_loudia_ouolof":
                towns = R.array.senegal_town_loudia_ouolof_list;
                break;

            case "arrondissement_cabrousse":
                towns = R.array.senegal_town_cabrousse_list;
                break;

            case "arrondissement_niassia":
                towns = R.array.senegal_town_niassia_list;
                break;

            case "arrondissement_niaguis":
                towns = R.array.senegal_town_niaguis_list;
                break;

            case "arrondissement_keur_mboucki":
                towns = R.array.senegal_town_keur_mboucki_list;
                break;

            case "arrondissement_mabo":
                towns = R.array.senegal_town_mabo_list;
                break;

            case "arrondissement_gniby":
                towns = R.array.senegal_town_gniby_list;
                break;

            case "arrondissement_katakel":
                towns = R.array.senegal_town_katakel_list;
                break;

            case "arrondissement_ida_mouride":
                towns = R.array.senegal_town_ida_mouride_list;
                break;

            case "arrondissement_lour_escale":
                towns = R.array.senegal_town_lour_escale_list;
                break;

            case "arrondissement_missira_waddene":
                towns = R.array.senegal_town_missira_waddene_list;
                break;

            case "arrondissement_sagna":
                towns = R.array.senegal_town_sagna_list;
                break;

            case "arrondissement_darou_minamii":
                towns = R.array.senegal_town_darou_minamii_list;
                break;

            default:
                towns = R.array.senegal_town_almadies_list;
                break;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), towns, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner townSpinner = (Spinner) parentView.findViewById(R.id.townSpinner);
        townSpinner.setEnabled(!isModeLocked);
        townSpinner.setAdapter(adapter);
        townSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isModeLocked)
                    return;

                String value = (String) parent.getItemAtPosition(position);
                String text = DictionaryManager.getInstance().findKeyInDictionary(survey.getSurveyVersion(), value, null);

                ((SenegalSurvey) survey).setTown(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (!TextUtils.isEmpty(defaultValue)) {
            String text = DictionaryManager.getInstance().findValueInDictionaryWithName(survey.getSurveyVersion(), defaultValue);
            int position = adapter.getPosition(text);

            if (position != View.NO_ID) {
                townSpinner.setSelection(position);
            }
        }
    }
}
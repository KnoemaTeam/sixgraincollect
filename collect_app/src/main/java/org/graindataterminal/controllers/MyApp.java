package org.graindataterminal.controllers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class MyApp extends Application {
    public final static String UPDATE_FILE_CHANNELS = "channels.txt";
    public final static String UPDATE_FILE_VERSION = "version.txt";
    public final static String UPDATE_FILE_INSTALLER = "6grain.apk";
    private final static String SHARED_PREFERENCES_KEY = "SIX_GRAIN_PREFERENCES_KEY";
    private final static String UPDATE_TYPE = "UPDATE_TYPE";
    private final static String INTERVIEWER_NAME = "INTERVIEWER_NAME";
    private final static String CONTROLLER_NAME = "CONTROLLER_NAME";
    private final static String SUPERVISOR_NAME = "SUPERVISOR_NAME";
    private final static String SHARED_PREFERENCES_SURVEYS_TYPE_KEY = "SIX_GRAIN_SURVEYS_TYPE_KEY";
    private final static String EXAMPLE_SURVEY = "[{\"info_agricole\":[\"rassemblement\"],\"service_exp\":{\"credit_dest\":[\"extention_exploitation\"],\"credit_source\":[\"banque\"],\"credit_obtenu\":[\"credit_inves\"],\"credit\":\"oui\"},\"unit_mesure\":\"hectare\",\"date_chef_exp\":\"2016-01-13T00:00:00+0000\",\"lieu_nais\":\"Paris\",\"commerciale\":{\"contrainte_commerciale\":[\"aucune_contrainte\"],\"commerce_production\":[\"bord_champ\"]},\"prenom_controler\":\"Senegal 2\",\"unit_mesure_s_t\":\"hectare\",\"department\":\"departement_dakar\",\"dest_production\":{\"dest_prod_oeuf\":[\"autoconsommation\"],\"dest_prod_miel\":[\"autoconsommation\"],\"dest_prod_viande\":[\"autoconsommation\"],\"dest_prod_lait\":[\"autoconsommation\"]},\"arrondissement_communes\":\"arrondissement_almadies\",\"instruction\":\"non_ins\",\"source_eau\":[\"sde\"],\"effectif_des_especes_dans_le_ferme\":[],\"propriete\":\"prop\",\"activte_elevage\":{\"fait_elevage\":\"oui\"},\"caracteristique\":{\"fouragere_pratique\":\"oui\",\"ope\":\"ajae\"},\"field_info\":[{\"speculation\":[{\"attaque\":{\"frequence_attaque\":\"2016-01-13T00:00:00+0000\",\"spec_attaque\":\"insectes\"},\"crop\":{\"superficie_embave\":\"50\",\"crop_name\":\"papaye\",\"quantite\":\"100\",\"don_semence\":\"oui\",\"semence\":\"oui\",\"motif_production_speculation\":[\"autoconsommation\"],\"water\":\"pluviale\"},\"title\":\"Papaya\"}],\"acqu_engrais\":\"achat\",\"input\":[\"npk\",\"dap\",\"uree\"],\"use_engrais\":\"oui\",\"use_pesticide\":\"oui\",\"use_pratique_restauration_sol\":\"oui\",\"restauration\":[\"compost\"],\"corners\":[{\"lat\":58.0181941,\"lng\":56.2507612},{\"lat\":58.0181953,\"lng\":56.2507613}]}],\"sexe\":\"masculin\",\"genetique\":{\"esp_gen\":[\"bovin\"],\"prati_genetique\":\"aucune_pratique_gen\",\"esp_cont\":[\"bovin\"],\"contr_sante\":\"aucune_controle\"},\"unit_mesure_s_c\":\"hectare\",\"activite_agricole\":\"oui\",\"org_prof\":\"oui\",\"infrastructure\":\"oui\",\"ninea\":\"oui\",\"materiel_exploitation\":\"oui\",\"date_del\":\"2016-01-13T00:00:00+0000\",\"infrastructure_exploitation\":[{\"mode_acquisition_infrastructure\":\"comptant\",\"infrastructure_name\":\"station_filtraton\",\"title\":\"Filtration plants\"}],\"mode_occupation\":\"mode_propriete\",\"reg_juridique\":\"reg_indi\",\"source_subsistance\":\"in_exploitation\",\"matromonial\":\"marie\",\"nationalite\":\"sen\",\"activite_exploitation\":[\"agriculture_pluviale\"],\"materiels\":[{\"materiel_sub\":\"oui\",\"materiel_name\":\"tracteur\",\"mode_acquisition\":\"comptant\",\"etat_materiel\":\"neuf\",\"title\":\"Tractor (CV)\"}],\"nom_chef_exp\":\"Cheid\",\"alimentation\":{\"ali_pat\":[\"bovin\"],\"md_aliment\":[\"paturage_alimentation\"]},\"source_energie\":[\"aucune_energie\"],\"race_exploitation\":{\"race_locale\":[\"bovin\"],\"race_metis\":[\"bovin\"],\"race_in_exploitation\":[\"locale\"]},\"region\":\"region_dakar\",\"relief\":\"plaine\",\"securite_cheptel\":{\"cheptel_identification\":\"oui\",\"police_assurance_cheptel\":\"oui\",\"moy_iden\":[\"boucle\"]},\"service_elevage\":{\"r_credit\":\"oui\",\"info_elevage\":[\"rassemblement\"],\"s_credit\":[\"banque\"],\"use_credit\":[\"ex_expl\"]},\"type_sol\":\"dior\",\"prenom_supervisor\":\"Senegal 3\",\"complementation\":{\"compl_aliment\":\"non\"},\"rural_commune_arrondissement\":\"ca_mermoz_sacre_cœur\",\"production_cheptel\":{\"pr_an_trait\":[\"bovin\"],\"prod_elevage_naisseur\":[\"bovin\"],\"pr_lait\":[\"bovin\"],\"type_prod\":[\"naisseur\"]},\"raison_inculte\":[\"jachere\"],\"vaccin\":{\"esp_dep\":[\"bovin\"],\"deparasite\":\"aucune_vaccin\",\"es_vac\":[\"bovin\"],\"vac\":\"aucune_vaccin\"},\"zone\":\"urbain\",\"app_version\":\"0.8.80\",\"deviceid\":\"42fe81b2c87cf64\",\"end_time\":\"2016-01-13T08:08:57+0000\",\"head_name\":\"Manager\",\"id\":\"b63dc942-fb66-4dbd-b386-2508f005e182\",\"imei\":\"209448603478588\",\"interviewer\":\"Senegal 1\",\"key\":891,\"village_name\":\"locality\",\"phonenumber\":\"\",\"sim_serial_number\":\"8901260291498643161\",\"start_time\":\"2016-01-13T07:51:48+0000\",\"update_time\":\"2016-01-13T08:08:57+0000\",\"subscriberid\":\"310260498643161\",\"q_version\":\"sn20151130\",\"state\":1,\"mode\":0}]";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static int getAppVersionCode() {
        int versionCode = 0;

        try {
            PackageManager packageManager = Collect.getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(Collect.getContext().getPackageName(), 0);

            versionCode = packageInfo.versionCode;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return versionCode;
    }

    public static String getAppVersionName() {
        String versionName = null;

        try {
            PackageManager packageManager = Collect.getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(Collect.getContext().getPackageName(), 0);

            versionName = packageInfo.versionName;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return versionName;
    }

    public static void setUpdateType(int type) {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(UPDATE_TYPE, type);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static int getUpdateType() {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            return sharedPreferences.getInt(UPDATE_TYPE, -1);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return -1;
    }

    public static void setInterviewerName (String interviewer) {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(INTERVIEWER_NAME, interviewer);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getInterviewerName() {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            return sharedPreferences.getString(INTERVIEWER_NAME, null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void setControllerName (String interviewer) {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(CONTROLLER_NAME, interviewer);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getControllerName() {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            return sharedPreferences.getString(CONTROLLER_NAME, null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void setSupervisorName (String interviewer) {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(SUPERVISOR_NAME, interviewer);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getSupervisorName() {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            return sharedPreferences.getString(SUPERVISOR_NAME, null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void setSurveysType (int type) {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(SHARED_PREFERENCES_SURVEYS_TYPE_KEY, type);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static int getSurveysType () {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            return sharedPreferences.getInt(SHARED_PREFERENCES_SURVEYS_TYPE_KEY, BaseSurvey.SURVEY_TYPE_NONE);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return BaseSurvey.SURVEY_TYPE_NONE;
    }

    public static void setSurveyList(List surveyList) {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            int type = DataHolder.getInstance().getSurveysType();
            Type objectType = new TypeToken<List<BaseSurvey>>(){}.getType();

            if (type == BaseSurvey.SURVEY_TYPE_ZAMBIA)
                objectType = new TypeToken<List<ZambiaSurvey>>(){}.getType();
            else if (type == BaseSurvey.SURVEY_TYPE_TUNISIA)
                objectType = new TypeToken<List<TunisiaSurvey>>(){}.getType();
            else if (type == BaseSurvey.SURVEY_TYPE_SENEGAL)
                objectType = new TypeToken<List<SenegalSurvey>>(){}.getType();
            else if (type == BaseSurvey.SURVEY_TYPE_CAMEROON)
                objectType = new TypeToken<List<CameroonSurvey>>(){}.getType();

            String jsonString = gson.toJson(surveyList, objectType);
            editor.putString(SHARED_PREFERENCES_KEY, jsonString);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static List getSurveyList() {
        try {
            SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
            String savedPreferences = sharedPreferences.getString(SHARED_PREFERENCES_KEY, null);

            if (savedPreferences != null) {
                int type = DataHolder.getInstance().getSurveysType();
                Type objectType = new TypeToken<List<BaseSurvey>>(){}.getType();

                if (type == BaseSurvey.SURVEY_TYPE_ZAMBIA)
                    objectType = new TypeToken<List<ZambiaSurvey>>(){}.getType();
                else if (type == BaseSurvey.SURVEY_TYPE_TUNISIA)
                    objectType = new TypeToken<List<TunisiaSurvey>>(){}.getType();
                else if (type == BaseSurvey.SURVEY_TYPE_SENEGAL)
                    objectType = new TypeToken<List<SenegalSurvey>>(){}.getType();
                else if (type == BaseSurvey.SURVEY_TYPE_CAMEROON)
                    objectType = new TypeToken<List<CameroonSurvey>>(){}.getType();

                Gson gson = new GsonBuilder().create();
                return gson.fromJson(savedPreferences, objectType);
            }
            /*
            else {
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(EXAMPLE_SURVEY, new TypeToken<List<SenegalSurvey>>(){}.getType());
            }
            */
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    protected static <T> List<T> stringToArray(String jsonString, Class<T[]> className) {
        Gson gson = new GsonBuilder().create();
        T[] array = gson.fromJson(jsonString, className);

        return Arrays.asList(array);
    }

    public static void clearPreferences () {
        SharedPreferences sharedPreferences = Collect.getContext().getSharedPreferences(Collect.getContext().getString(R.string.app_preferences_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
    }
}

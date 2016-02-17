package org.odk.collect.android.constants;

import android.os.Environment;

import java.io.File;

public final class Constants {
    public static final String ODK_ROOT = Environment.getExternalStorageDirectory() + File.separator + "6G Data Terminal";
    public static final String FORMS_PATH = ODK_ROOT + File.separator + "forms";
    public static final String INSTANCES_PATH = ODK_ROOT + File.separator + "instances";
    public static final String CACHE_PATH = ODK_ROOT + File.separator + ".cache";
    public static final String METADATA_PATH = ODK_ROOT + File.separator + "metadata";
    public static final String TMPFILE_PATH = CACHE_PATH + File.separator + "tmp.jpg";
    public static final String TMPDRAWFILE_PATH = CACHE_PATH + File.separator + "tmpDraw.jpg";
    public static final String TMPXML_PATH = CACHE_PATH + File.separator + "tmp.xml";
    public static final String LOG_PATH = ODK_ROOT + File.separator + "log";

    public static final String DEFAULT_FONTSIZE = "21";

    public static final String SURVEY_SOURCE_URL = "http://192.168.1.29/api/farmSurvey";
    public static final String SURVEY_UPLOAD_URL = "http://192.168.1.29/api/farmSurvey";
    public static final String PREFERENCES_KEY = "SIX_GRAIN_PREFERENCES_KEY";
    public static final String UPDATE_TYPE_KEY = "UPDATE_TYPE";
    public static final String INTERVIEWER_NAME_KEY = "INTERVIEWER_NAME";
    public static final String CONTROLLER_NAME_KEY = "CONTROLLER_NAME";
    public static final String SUPERVISOR_NAME_KEY = "SUPERVISOR_NAME";
    public static final String SURVEYS_TYPE_KEY = "SIX_GRAIN_SURVEYS_TYPE_KEY";
    public static final String UPDATE_FILE_INSTALLER = "6grain.apk";
}

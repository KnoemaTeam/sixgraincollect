package org.graindataterminal.helpers;

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.graindataterminal.controllers.MyApp;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DictionaryItem;
import org.odk.collect.android.R;

import org.odk.collect.android.application.Collect;
import org.xmlpull.v1.XmlPullParser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DictionaryManager {
    private Set<DictionaryItem> zambiaDictionary = new HashSet<>();
    private Set<DictionaryItem> tunisiaDictionary = new HashSet<>();
    private Set<DictionaryItem> senegalDictionary = new HashSet<>();
    private Set<DictionaryItem> cameroonDictionary = new HashSet<>();

    private static DictionaryManager instance = null;
    protected DictionaryManager() {}

    public static DictionaryManager getInstance() {
        if (instance == null) {
            instance = new DictionaryManager();
        }

        return instance;
    }

    public void updateDictionaries() {
        this.readDictionary(R.xml.zambia_dictionary, zambiaDictionary);
        this.readDictionary(R.xml.tunisia_dictionary, tunisiaDictionary);
        this.readDictionary(R.xml.senegal_dictionary, senegalDictionary);
        this.readDictionary(R.xml.cameroon_dictionary, cameroonDictionary);
    }

    public String findKeyInDictionary(String version, String value, String group) {
        String findValue = value.replaceAll("\\s+","");
        String key = null;

        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_ZAMBIA).contains(version)) {
            for (DictionaryItem dictionaryItem: zambiaDictionary) {
                String sourceValue = dictionaryItem.getName().replaceAll("\\s+", "");

                if (sourceValue.compareToIgnoreCase(findValue) == 0 && (group == null || group.compareToIgnoreCase(dictionaryItem.getGroup()) == 0)) {
                    key = dictionaryItem.getId();
                    break;
                }
            }
        }
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_TUNISIA).contains(version)) {
            for (DictionaryItem dictionaryItem: tunisiaDictionary) {
                String sourceValue = dictionaryItem.getName().replaceAll("\\s+", "");

                if (sourceValue.compareToIgnoreCase(findValue) == 0 && (group == null || group.compareToIgnoreCase(dictionaryItem.getGroup()) == 0)) {
                    key = dictionaryItem.getId();
                    break;
                }
            }
        }
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(version)) {
            for (DictionaryItem dictionaryItem: senegalDictionary) {
                String sourceValue = dictionaryItem.getName().replaceAll("\\s+", "");

                if (sourceValue.compareToIgnoreCase(findValue) == 0 && (group == null || group.compareToIgnoreCase(dictionaryItem.getGroup()) == 0)) {
                    key = dictionaryItem.getId();
                    break;
                }
            }
        }
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(version)) {
            for (DictionaryItem dictionaryItem: cameroonDictionary) {
                String sourceValue = dictionaryItem.getName().replaceAll("\\s+", "");

                if (sourceValue.compareToIgnoreCase(findValue) == 0 && (group == null || group.compareToIgnoreCase(dictionaryItem.getGroup()) == 0)) {
                    key = dictionaryItem.getId();
                    break;
                }
            }
        }

        return key;
    }

    public String findValueInDictionaryWithName(String version, String key) {
        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_ZAMBIA).contains(version))
            for (DictionaryItem dictionaryItem: zambiaDictionary) {
                if (dictionaryItem.getId().compareToIgnoreCase(key) == 0) {
                    return dictionaryItem.getName();
                }
            }
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_TUNISIA).contains(version))
            for (DictionaryItem dictionaryItem: tunisiaDictionary) {
                if (dictionaryItem.getId().compareToIgnoreCase(key) == 0) {
                    return dictionaryItem.getName();
                }
            }
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(version))
            for (DictionaryItem dictionaryItem: senegalDictionary) {
                if (dictionaryItem.getId().compareToIgnoreCase(key) == 0) {
                    return dictionaryItem.getName();
                }
            }
        else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(version))
            for (DictionaryItem dictionaryItem: cameroonDictionary) {
                if (dictionaryItem.getId().compareToIgnoreCase(key) == 0) {
                    return dictionaryItem.getName();
                }
            }

        return null;
    }

    private void readDictionary(int xmlId, Set<DictionaryItem> dictionary) {
        try {
            Context context = Collect.getContext();
            XmlResourceParser parser = context.getResources().getXml(xmlId);

            int eventType = -1;
            dictionary.clear();

            if (parser != null) {
                parser.next();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = parser.getName();

                        if (tagName.equals("dictionary")) {
                            eventType = parser.next();
                            continue;
                        }

                        String tagId = parser.getAttributeValue(null, "id").trim();
                        String tagValue = parser.getAttributeValue(null, "name").trim();
                        String tagGroup = parser.getAttributeValue(null, "group").trim();

                        dictionary.add(new DictionaryItem(tagId, tagValue, tagGroup));
                    }

                    eventType = parser.next();
                }

                parser.close();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

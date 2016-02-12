package org.odk.collect.android.utilities;

import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class XMLUtils {

    private static boolean mTestMode = false;

    public static void setTestMode(boolean testMode) {
        mTestMode = testMode;
    }

    public static String[] getArrayProperties(String xFormsXml) throws Exception {
        List<String> properties = new ArrayList<>();

        XmlPullParser parser = getXmlParser();
        parser.setInput(new StringReader(xFormsXml));
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("repeat")) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        String attributeName = parser.getAttributeName(i);
                        String attributeValue = parser.getAttributeValue(i);
                        if (attributeName.equals("nodeset") && attributeValue != null && !attributeValue.isEmpty())
                            properties.add(attributeValue.substring(attributeValue.lastIndexOf('/') + 1));
                    }
                }
            }

            eventType = parser.next();
        }

        return properties.toArray(new String[0]);
    }

    public static JSONObject toJSONObject(String xml) throws Exception {
        return toJSONObject(xml, null);
    }

    public static JSONObject toJSONObject(String xml, String[] arrayProperties) throws Exception {
        List<String> arrayPropertyList = arrayProperties != null ? Arrays.asList(arrayProperties) : null;

        Stack<JSONObject> objStack = new Stack<>();
        String lastName = null;
        int currentDepth = 0;
        boolean attributeAdded = false;

        XmlPullParser parser = getXmlParser();
        parser.setInput(new StringReader(xml));
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;

                case XmlPullParser.START_TAG:
                    if (parser.getDepth() > currentDepth) {
                        currentDepth = parser.getDepth();

                        JSONObject propertyValue = null;
                        if (lastName != null) {
                            if (objStack.peek().has(lastName)) {
                                Object value = objStack.peek().get(lastName);
                                if (value instanceof JSONObject) {
                                    JSONObject obj = (JSONObject) value;
                                    if (attributeAdded)
                                        propertyValue = obj;
                                    else {
                                        JSONArray array = new JSONArray();
                                        array.put(obj);
                                        objStack.peek().put(lastName, array);
                                    }
                                }
                            }
                            else if (arrayPropertyList != null && arrayPropertyList.contains(lastName))
                                objStack.peek().put(lastName, new JSONArray());
                        }

                        if (propertyValue == null)
                            propertyValue = new JSONObject();

                        if (lastName != null) {
                            if (objStack.peek().has(lastName) && objStack.peek().get(lastName) instanceof JSONArray) {
                                JSONArray array = (JSONArray)objStack.peek().get(lastName);
                                array.put(propertyValue);
                            }
                            else
                                objStack.peek().put(lastName, propertyValue);
                        }

                        objStack.push(propertyValue);
                    }

                    lastName = parser.getName();

                    attributeAdded = false;

                    if (parser.isEmptyElementTag()) {
                        objStack.peek().put(lastName, "");
                    }
                    else if (parser.getAttributeCount() > 0) {
                        JSONObject propertyValue = new JSONObject();
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attributeName = parser.getAttributeName(i);
                            String attributeValue = parser.getAttributeValue(i);
                            propertyValue.put(attributeName, attributeValue);
                        }
                        objStack.peek().put(lastName, propertyValue);
                        attributeAdded = true;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (parser.getDepth() < currentDepth) {
                        currentDepth = parser.getDepth();
                        objStack.pop();
                    }
                    break;

                case XmlPullParser.TEXT:
                    objStack.peek().put(lastName, parser.getText());
                    break;
            }

            eventType = parser.next();
        }

        if (objStack.size() != 1)
            throw new XmlPullParserException("Parse error: stack size = " + objStack.size());

        JSONObject result = objStack.pop();

        return result;
    }

    private static XmlPullParser getXmlParser() throws Exception {
        if (mTestMode) {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newPullParser();
        }

        return Xml.newPullParser();
    }
}

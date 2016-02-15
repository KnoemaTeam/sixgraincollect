package org.graindataterminal.views.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import org.graindataterminal.models.base.BaseCrop;
import org.graindataterminal.models.base.BaseField;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.views.system.MessageBox;

public abstract class BaseFragment extends Fragment {
    protected static final String SCREEN_INDEX = "SCREEN_INDEX";
    protected int screenIndex = 0;

    protected BaseSurvey survey = null;
    protected BaseField field = null;
    protected BaseCrop crop = null;

    protected boolean isModeLocked = false;

    public interface FragmentNotificationListener {
        void onRequiredFieldChanged(Integer screenIndex, String key, Boolean isEmpty);
    }

    protected void createMessage(int type, String title, String message, String tag) {
        MessageBox box = new MessageBox();
        Bundle arguments = new Bundle();
        arguments.putInt(MessageBox.DIALOG_TYPE_KEY, type);
        arguments.putString(MessageBox.DIALOG_TITLE_KEY, title);
        arguments.putString(MessageBox.DIALOG_MESSAGE_KEY, message);
        arguments.putString(MessageBox.DIALOG_TAG_KEY, tag);

        box.setArguments(arguments);
        box.show(getActivity().getFragmentManager(), tag);
    }

    protected void clearSelectionInViewGroup(ViewGroup viewGroup) {
        if (viewGroup instanceof RadioGroup) {
            ((RadioGroup) viewGroup).clearCheck();
        }
        else if (viewGroup instanceof LinearLayout) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof CheckBox) {
                    ((CheckBox) viewGroup.getChildAt(i)).setChecked(false);
                }
            }
        }
    }

    protected void selectElementInViewGroup(ViewGroup viewGroup, int id) {
        if (viewGroup instanceof RadioGroup) {
            ((RadioGroup) viewGroup).clearCheck();
            ((RadioGroup) viewGroup).check(id);
        }
        else if (viewGroup instanceof LinearLayout) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) viewGroup.getChildAt(i);
                    if (checkBox.getId() == id) {
                        checkBox.setChecked(true);
                        break;
                    }
                }
            }
        }
    }
}

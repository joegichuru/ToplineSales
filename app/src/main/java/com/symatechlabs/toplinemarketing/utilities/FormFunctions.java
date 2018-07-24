package com.symatechlabs.toplinemarketing.utilities;

/**
 * Created by root on 4/19/17.
 */

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.Arrays;

public class FormFunctions {

    public boolean empty = true, equals = true;
    public String[] arrayElements;
    String firstValue = null;
    OnClickListener listerner;

    public FormFunctions() {
        // TODO Auto-generated constructor stub
    }


    public void setListerner(OnClickListener listerner) {

        this.listerner = listerner;
    }

    public boolean hasValue(EditText... editTexts) {

        arrayElements = new String[editTexts.length];

        for (int i = 0; i < editTexts.length; i++) {

            if (editTexts[i].getText().toString().trim().length() > 0) {
                arrayElements[i] = "true";
            } else {
                arrayElements[i] = "false";
            }

        }

        if (Arrays.asList(arrayElements).contains("false")) {
            return false;
        } else {
            return true;
        }

    }

    public boolean hasValue(Spinner... spinner) {

        arrayElements = new String[spinner.length];

        for (int i = 0; i < spinner.length; i++) {

            if (spinner[i].getSelectedItem().toString().trim().length() > 0) {
                arrayElements[i] = "true";
            } else {
                arrayElements[i] = "false";
            }

        }

        if (Arrays.asList(arrayElements).contains("false")) {
            return false;
        } else {
            return true;
        }

    }

    public boolean hasValue(String... string) {

        arrayElements = new String[string.length];

        for (int i = 0; i < string.length; i++) {

            if (string[i].trim().length() > 0) {
                arrayElements[i] = "true";
            } else {
                arrayElements[i] = "false";
            }

        }

        if (Arrays.asList(arrayElements).contains("false")) {
            return false;
        } else {
            return true;
        }

    }

    public boolean equals(EditText... editTexts) {

        arrayElements = new String[editTexts.length];

        for (int i = 0; i < editTexts.length; i++) {
            arrayElements[i] = editTexts[i].getText().toString().trim();
        }

        if (arrayElements.length == 0) {
            return true;
        } else {

            firstValue = arrayElements[0];

            for (String element : arrayElements) {

                if (!element.equalsIgnoreCase(firstValue)) {
                    return false;
                }
            }
            return true;

        }

    }

    public void setOnClickListener(LinearLayout... linearLayout) {

        for (int i = 0; i < linearLayout.length; i++) {
            linearLayout[i].setOnClickListener(this.listerner);
        }

    }

    public boolean isChecked(RadioButton... button) {

        for (int i = 0; i < button.length; i++) {
            //button[i].setOnClickListener(this.listerner);
        }


        return false;

    }

    public void editextListerner(final EditText... editText) {

        for ( int i = 0; i < editText.length; i++) {
            final int a = i;
            editText[i].addTextChangedListener(new TextWatcher() {

                Drawable originalDrawable =  editText[a].getBackground();
                int sdk = android.os.Build.VERSION.SDK_INT;
                int jellyBean = android.os.Build.VERSION_CODES.JELLY_BEAN;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() > 0) {
                            editText[a].setBackgroundColor(Color.parseColor("#79d3f9"));
                        } else {
                            if(sdk < jellyBean) {
                                editText[a].setBackgroundDrawable(originalDrawable);
                            } else {
                                editText[a].setBackground(originalDrawable);
                            }

                        }
                    } catch (Exception e) {

                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }


    }

}
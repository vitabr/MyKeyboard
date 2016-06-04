package com.vito.mykeyboard.ui.views;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Vito on 6/4/2016.
 */

public class VitoKeyboardView extends KeyboardView {
    public VitoKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e("VITO", "VitoKeyboardView: CONSTRACTOR");
    }
}

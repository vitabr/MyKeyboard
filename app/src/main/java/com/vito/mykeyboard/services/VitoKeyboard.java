/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vito.mykeyboard.services;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vito.mykeyboard.R;
import com.vito.mykeyboard.ui.views.VitoKeyboardView;
import com.vito.mykeyboard.uttil.Parser;

import java.util.ArrayList;

public class VitoKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private VitoKeyboardView mInputView;
    private Keyboard mQwertyKeyboard;
    private Keyboard mCurKeyboard;
    private ArrayList<String> mHistory = new ArrayList<>();
    private ListView mHistoryList;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override public void onCreate() {
        super.onCreate();
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override public void onInitializeInterface() {
        mQwertyKeyboard = new Keyboard(this, R.xml.qwerty);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override public View onCreateInputView() {
        mInputView = (VitoKeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setKeyboard(mQwertyKeyboard);
        return mInputView;
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override public View onCreateCandidatesView() {
        return null;
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        mCurKeyboard = mQwertyKeyboard;
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override public void onFinishInput() {
        super.onFinishInput();
        setCandidatesViewShown(false);

        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        mInputView.setKeyboard(mCurKeyboard);
        mInputView.closing();
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                            int newSelStart, int newSelEnd,
                                            int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                onKey(Keyboard.KEYCODE_DELETE, null);
                return true;

            case KeyEvent.KEYCODE_ENTER:
                onKey(Keyboard.KEYCODE_DONE, null);
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }


    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode == Keyboard.KEYCODE_DONE) {
            handleDone();
        }else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            // Override Delete button as Hystory Button.
            handleHistory();
        }else{
            handleCharacter(primaryCode, keyCodes);
        }
    }

    private void handleDone() {

        String input = popText();
        mHistory.add(input);
        String result = String.valueOf(new Parser().eval(input));
        getCurrentInputConnection().commitText(result, 1);
    }

    private void handleHistory() {
        if(mHistory.isEmpty())
            return;

        if(mHistoryList == null) {
            mHistoryList = new ListView(getBaseContext());
            mHistoryList.setBackgroundResource(R.color.white_half_transperent);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mInputView.getHeight());
            mHistoryList.setLayoutParams(params);
            mHistoryList.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, mHistory));
            mHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    animateHistoryClose();
                }
            });
        }

        ((FrameLayout)mInputView.getParent()).addView(mHistoryList);
        animateHistoryOpen();
    }

    private void animateHistoryOpen() {
        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_up);
        animation.setStartOffset(0);
        mHistoryList.startAnimation(animation);
    }

  /**
   * Animate close history list view and remove it on animation end
   */
  private void animateHistoryClose() {
        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_down);
        animation.setStartOffset(0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((FrameLayout)mInputView.getParent()).removeView(mHistoryList);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mHistoryList.startAnimation(animation);
    }

    private String popText() {
        String inputText = (String) getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(),0).text;
        CharSequence beforCursorText = getCurrentInputConnection().getTextBeforeCursor(inputText.length(), 0);
        CharSequence afterCursorText = getCurrentInputConnection().getTextAfterCursor(inputText.length(), 0);
        getCurrentInputConnection().deleteSurroundingText(beforCursorText.length(), afterCursorText.length());
        return inputText;
    }

    private void handleBackspace() {
        keyDownUp(KeyEvent.KEYCODE_DEL);
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        ic.commitText(text, 0);
        ic.endBatchEdit();
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {

        getCurrentInputConnection().commitText(
                String.valueOf((char) primaryCode), 1);

    }

    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    public void swipeDown() {

    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {
    }

    public void onRelease(int primaryCode) {
    }
}

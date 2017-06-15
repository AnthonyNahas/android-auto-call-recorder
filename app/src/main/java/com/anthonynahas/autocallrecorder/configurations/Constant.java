package com.anthonynahas.autocallrecorder.configurations;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class that deals with static variables and content in order to hold them across the app.
 * <p>
 * <p>
 * https://www.javacodegeeks.com/2014/01/android-tutorial-two-methods-of-passing-object-by-intent-serializableparcelable.html
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 11.04.2017
 */
@Singleton
public class Constant {

    @Inject
    public Constant() {
    }

    //Settings
    public final int RECYCLER_VIEW_ANIMATION_DELAY = 500;
    public final int CURSOR_LOADER_SIMULATION_DELAY = 2000;

    //Local Broadcast actions
    public final String BROADCAST_ACTION_ON_ACTION_MODE = "broadcast_action_on_action_mode";

    //Local Broadcast actions key for intent
    public final String ACTION_MODE_SATE = "action_mode_state";
    public final String ACTION_MODE_SENDER = "action_mode_sender";
    public final String ACTION_MODE_COUNTER = "action_mode_counter";


    //Intent - Bundle KEYS
    public final String REC_PARC_KEY = "rec_parc_key";
    public final String FAB_PARC_KEY = "fab_parc_key";
    public final String IS_CHECKED_KEY = "is_checked_key";

    //others
    public String DEMO_PATH = "demo_path";

}

package com.anthonynahas.autocallrecorder.classes;

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

public class Res {

    //Settings
    public static final int RECYCLER_VIEW_ANIMATION_DELAY = 500;
    public static final int CURSOR_LOADER_SIMULATION_DELAY = 2000;

    //Local Broadcast actions
    public static final String BROADCAST_ACTION_ON_ACTION_MODE = "broadcast_action_on_action_mode";

    //Local Broadcast actions key for intent
    public static final String ACTION_MODE_SATE = "action_mode_state";
    public static final String ACTION_MODE_SENDER = "action_mode_sender";
    public static final String ACTION_MODE_COUNTER = "action_mode_counter";


    //Intent - Bundle KEYS
    public static final String REC_PARC_KEY = "rec_parc_key";
    public static final String FAB_PARC_KEY = "fab_parc_key";
    public static final String IS_CHECKED_KEY = "is_checked_key";

    //others
    public static String DEMO_PATH = "demo_path";

}

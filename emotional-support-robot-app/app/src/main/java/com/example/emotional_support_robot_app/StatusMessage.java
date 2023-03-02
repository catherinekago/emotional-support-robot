package com.example.emotional_support_robot_app;

/**
 * The StatusMessage enum holds all possible stati that are sent between app and robot
 */
public enum StatusMessage {
    // from APP
    WAKEWORD,
    STOP,
    ANXIOUS_SHORT,
    ANXIOUS_MEDIUM,
    ANXIOUS_LONG,
    HAPPY,
    HAPPY_244,
    HAPPY_208,
    ANXIOUS,

    // from ROBOT
    AWAKE,
    PLAYING,
    ANXIOUS_END,


    // from BOTH
    SNAKE

}

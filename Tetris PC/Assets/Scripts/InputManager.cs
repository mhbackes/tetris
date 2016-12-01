using UnityEngine;
using System.Collections;

public static class InputManager {

    public enum ACTION
    {
        ROTATE_LEFT = 0,
        ROTATE_RIGHT = 1,
        START_DROP = 2,
        STOP_DROP = 3,
        MOVE_LEFT = 4,
        MOVE_RIGHT = 5,
        NONE = 6
    }

    private static ACTION action = ACTION.NONE;

    public static ACTION getAction()
    {
        return action;
    }

    public static bool M_Horizontal_L()
    {
        bool keyboard;
        bool bluetooth;

        keyboard = Input.GetButtonDown("M_Horizontal_L");

        bluetooth = checkBluetooth(ACTION.MOVE_LEFT);

        return keyboard || bluetooth;
    }

    public static bool M_Horizontal_R()
    {
        bool keyboard;
        bool bluetooth;

        keyboard = Input.GetButtonDown("M_Horizontal_R");

        bluetooth = checkBluetooth(ACTION.MOVE_RIGHT);

        return keyboard || bluetooth;
    }

    public static bool R_Horizontal_L()
    {
        bool keyboard;
        bool bluetooth;

        keyboard = Input.GetButtonDown("R_Horizontal_L");

        bluetooth = checkBluetooth(ACTION.ROTATE_LEFT);

        return keyboard || bluetooth;
    }

    public static bool R_Horizontal_R()
    {
        bool keyboard;
        bool bluetooth;

        keyboard = Input.GetButtonDown("R_Horizontal_R");

        bluetooth = checkBluetooth(ACTION.ROTATE_RIGHT);

        return keyboard || bluetooth;
    }

    public static bool Fall()
    {
        bool keyboard;
        bool bluetooth;

        keyboard = Input.GetButtonDown("Fall");

        bluetooth = checkBluetooth(ACTION.START_DROP);

        return keyboard || bluetooth;
    }

    public static bool StopFall()
    {
        bool keyboard;
        bool bluetooth;

        keyboard = Input.GetButtonUp("Fall");

        bluetooth = checkBluetooth(ACTION.STOP_DROP);

        return keyboard || bluetooth;
    }

    private static bool checkBluetooth(ACTION newAction)
    {
        if (action == newAction)
        {
            action = ACTION.NONE;
            return true;
        }

        return false;
    }

    public static void Reset()
    {
        action = ACTION.NONE;

        Debug.Log("Input Manager reseted");
    }

    public static void Parse(string text)
    {
        int c = text[0] - '0';

        action = (ACTION) c;

        Debug.Log("From IM: " + action);
    }

}

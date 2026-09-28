package org.firstinspires.ftc.teamcode.robot;

/** True once per press, even if held across multiple OpMode loops. */
public final class EdgeButton {
    private boolean wasPressed;
    public boolean rising(boolean pressed) {
        boolean event = pressed && !wasPressed;
        wasPressed = pressed;
        return event;
    }
}

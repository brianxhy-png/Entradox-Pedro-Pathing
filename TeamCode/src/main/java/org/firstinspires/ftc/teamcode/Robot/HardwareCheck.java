package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/** Read-only configuration checklist; this OpMode never powers hardware. */
@TeleOp(name = "Entradox | Hardware Check", group = "Entradox")
public final class HardwareCheck extends OpMode {
    private static final String[] MOTOR_NAMES = {
            RobotConfig.FRONT_LEFT, RobotConfig.FRONT_RIGHT,
            RobotConfig.BACK_LEFT, RobotConfig.BACK_RIGHT,
            RobotConfig.INTAKE, RobotConfig.FLYWHEEL_LEFT,
            RobotConfig.FLYWHEEL_RIGHT, RobotConfig.TURRET
    };

    @Override
    public void init() {
        telemetry.addLine("Read-only hardware check");
        for (String name : MOTOR_NAMES) {
            try {
                DcMotorEx motor = hardwareMap.get(DcMotorEx.class, name);
                telemetry.addData(name, "OK (%d ticks)", motor.getCurrentPosition());
            } catch (IllegalArgumentException ex) {
                telemetry.addData(name, "MISSING / WRONG TYPE");
            }
        }
        telemetry.update();
    }

    @Override
    public void loop() {
        telemetry.addLine("No motors are commanded by this OpMode.");
        telemetry.update();
    }
}

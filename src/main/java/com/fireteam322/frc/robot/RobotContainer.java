/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.fireteam322.frc.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import com.fireteam322.frc.robot.commands.AutomaticLED;
import com.fireteam322.frc.robot.commands.BasicAutonomous;
import com.fireteam322.frc.robot.commands.DashboardUpdater;
import com.fireteam322.frc.robot.commands.DoNothing;
import com.fireteam322.frc.robot.commands.DriveWithJoystick;
import com.fireteam322.frc.robot.commands.ForwardAutonomous;
import com.fireteam322.frc.robot.commands.LimelightCameraModeControl;
import com.fireteam322.frc.robot.commands.LimelightLightModeControl;
import com.fireteam322.frc.robot.commands.RunFeeder;
import com.fireteam322.frc.robot.commands.RunIntake;
import com.fireteam322.frc.robot.commands.RunRearCamera;
import com.fireteam322.frc.robot.commands.RunShooter;
import com.fireteam322.frc.robot.commands.ShooterAutonomous;
import com.fireteam322.frc.robot.commands.SimpleAutonomous;
import com.fireteam322.frc.robot.commands.StraightShooterAutonomous;
import com.fireteam322.frc.robot.subsystems.AddressableLEDs;
import com.fireteam322.frc.robot.subsystems.Chassis;
import com.fireteam322.frc.robot.subsystems.Dashboard;
import com.fireteam322.frc.robot.subsystems.Feeder;
import com.fireteam322.frc.robot.subsystems.Intake;
import com.fireteam322.frc.robot.subsystems.LED;
import com.fireteam322.frc.robot.subsystems.LimelightCamera;
import com.fireteam322.frc.robot.subsystems.RearCamera;
import com.fireteam322.frc.robot.subsystems.RobotPower;
import com.fireteam322.frc.robot.subsystems.Shooter;
import com.fireteam322.frc.robot.utilities.F310Controller;
import com.fireteam322.frc.robot.utilities.Limelight;
import com.fireteam322.frc.robot.utilities.Limelight.CameraMode;
import com.fireteam322.frc.robot.utilities.Limelight.LightMode;
//import com.fireteam322.frc.robot.utilities.RumblePad2;

import io.github.oblarg.oblog.Logger;
import io.github.oblarg.oblog.annotations.Config;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
// The robot's subsystems and commands are defined here...
	Command m_autoCommand;
	SendableChooser<Command> autonomousChooser = new SendableChooser<>();

	private final AddressableLEDs m_AddressableLEDs = new AddressableLEDs();
	private final Chassis m_chassis = new Chassis();
	private final Dashboard m_dashboard = new Dashboard();
	private final Feeder m_feeder = new Feeder();
	private final Intake m_intake = new Intake();
	private final LED m_led = new LED();
	private final LimelightCamera m_limelightCamera = new LimelightCamera();
	private final RearCamera m_rearCamera = new RearCamera();
	private final RobotPower m_robotPower = new RobotPower();
	private final Shooter m_shooter = new Shooter();

	private final F310Controller m_driveStick = new F310Controller(Constants.DRIVE_STICK);
	private final F310Controller m_manipulatorStick = new F310Controller(Constants.MANIPULATOR_STICK);
	//private final RumblePad2 m_debuggerStick = new RumblePad2(Constants.DEBUGGER_STICK);

	private final JoystickButton m_brakeButton = new JoystickButton(m_driveStick, F310Controller.Button.kA.getValue());
	//private final JoystickButton m_brakeButton = new JoystickButton(m_debuggerStick, F310Controller.Button.kA.getValue());
	private final JoystickButton m_logButton = new JoystickButton(m_driveStick, F310Controller.Button.kStart.getValue());
	//private final JoystickButton m_logButton = new JoystickButton(m_debuggerStick, F310Controller.Button.kStart.getValue());
	private final JoystickButton m_feederButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kA.getValue());
	private final JoystickButton m_feederReverseButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kB.getValue());
	private final JoystickButton m_shooterButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kX.getValue());
	private final JoystickButton m_shooterReverseButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kY.getValue());
	//private final JoystickButton m_manipulatorLogButton = new JoystickButton(m_driveStick, F310Controller.Button.kStart.getValue());
	private final JoystickButton m_visionModeButton = new JoystickButton(m_driveStick, F310Controller.Button.kBumperLeft.getValue());
	private final JoystickButton m_driverModeButton = new JoystickButton(m_driveStick, F310Controller.Button.kBumperRight.getValue());
	private final JoystickButton m_LEDOnButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kStart.getValue());
	private final JoystickButton m_LEDBlinkButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kStickLeft.getValue());
	private final JoystickButton m_LEDOffButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kBack.getValue());
	private final JoystickButton m_LEDDefaultButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kStickRight.getValue());
	private final JoystickButton m_intakeReverseButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kBumperLeft.getValue());
	private final JoystickButton m_intakeButton = new JoystickButton(m_manipulatorStick, F310Controller.Button.kBumperRight.getValue());

	/**
	 * The container for the robot.  Contains subsystems, OI devices, and commands.
	 */
	public RobotContainer() {
		// Assign default commands
		m_chassis.setDefaultCommand(new DriveWithJoystick(
						    ()->m_driveStick.getTriggerAxis(Hand.kRight) - m_driveStick.getTriggerAxis(Hand.kLeft),
						    ()->(m_driveStick.getX(Hand.kLeft)), m_chassis, m_brakeButton, m_logButton));

		// Default command for debugging purposes
		//m_chassis.setDefaultCommand(new DriveWithJoystick(
		//				    ()-> m_debuggerStick.getY(Hand.kRight), ()-> m_debuggerStick.getX(Hand.kRight),
		//					   m_chassis, m_brakeButton, m_logButton));

		m_dashboard.setDefaultCommand(new DashboardUpdater(m_dashboard));

		m_feeder.setDefaultCommand(new RunFeeder(m_feeder, ()->- m_manipulatorStick.getY(Hand.kLeft)));

		m_intake.setDefaultCommand(new RunIntake(m_intake, ()->- m_manipulatorStick.getY(Hand.kRight)));

		m_led.setDefaultCommand(new AutomaticLED(m_led, m_AddressableLEDs));

		m_limelightCamera.setDefaultCommand(new LimelightLightModeControl(m_limelightCamera, Limelight.LightMode.kforceOff));

		m_rearCamera.setDefaultCommand(new RunRearCamera(m_rearCamera));

		m_shooter.setDefaultCommand(new RunShooter(m_shooter, ()->m_manipulatorStick.getTriggerAxis(Hand.kRight)
							   - m_manipulatorStick.getTriggerAxis(Hand.kLeft)));

		// Setup the SendableChooser
		chooserSetup();

		// Create the Oblog Logger
		Logger.configureLoggingAndConfig(this, false);

		// Configure the button bindings
		configureButtonBindings();
	}

	/**
	 * Use this method to define your button->command mappings.  Buttons can be created by
	 * instantiating a {@link GenericHID} or one of its subclasses ({@link
	 * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
	 * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
	 */
	private void configureButtonBindings() {
		m_visionModeButton.whileActiveOnce(new LimelightCameraModeControl(m_limelightCamera,
										  CameraMode.kvision));
		m_driverModeButton.whileActiveOnce(new LimelightCameraModeControl(m_limelightCamera,
										  CameraMode.kdriver));

		m_LEDDefaultButton.whileActiveOnce(new LimelightLightModeControl(m_limelightCamera,
										 LightMode.kpipeLine));
		m_LEDOffButton.whileActiveOnce(new LimelightLightModeControl(m_limelightCamera,
									     LightMode.kforceOff));
		m_LEDBlinkButton.whileActiveOnce(new LimelightLightModeControl(m_limelightCamera,
									       LightMode.kforceBlink));
		m_LEDOnButton.whileActiveOnce(new LimelightLightModeControl(m_limelightCamera,
									    LightMode.kforceOn));

		m_feederButton.whileActiveOnce(new RunFeeder(m_feeder, ()->Constants.FEEDER_SPEED), true);
		m_feederReverseButton.whileActiveOnce(new RunFeeder(m_feeder, ()->Constants.FEEDER_REVERSE_SPEED), true);

		m_shooterButton.whileActiveOnce(new RunShooter(m_shooter, ()->Constants.SHOOTER_SPEED), true);
		m_shooterReverseButton.whileActiveOnce(new RunShooter(m_shooter, ()->Constants.SHOOTER_REVERSE_SPEED), true);

		m_intakeButton.whileActiveOnce(new RunIntake(m_intake, ()->Constants.INTAKE_SPEED));
		m_intakeReverseButton.whileActiveOnce(new RunIntake(m_intake, ()->Constants.INTAKE_REVERSE_SPEED));
	}

	public Dashboard getDashboard() {
		return m_dashboard;
	}

	// Use this to setup the SendableChooser.
	private void chooserSetup() {
		// Add commands to Autonomous SendableChooser
		autonomousChooser.setDefaultOption("Do Nothing", new DoNothing());
		autonomousChooser.addOption("Basic Autonomous", new BasicAutonomous(m_chassis));
		autonomousChooser.addOption("Forward Autonomous", new ForwardAutonomous(m_chassis));
		autonomousChooser.addOption("Simple Autonomous", new SimpleAutonomous(m_chassis));
		autonomousChooser.addOption("Shooter Autonomous", new ShooterAutonomous(m_chassis, m_feeder, m_shooter));
		autonomousChooser.addOption("Straight Shooter", new StraightShooterAutonomous(m_chassis, m_feeder, m_shooter));

		// Add the Autonomous SendableChooser to the Shuffleboard
		m_dashboard.getAutonomousTab().add("Autonomous Mode", getChooser());
	}

	/**
	 * Use this to pass the SendableChooser to the Logger or the main {@link Robot} class.
	 *
	 * @return the SendableChooser<Command>
	 */
	@Config(name = "Autonomous Chooser", tabName = "Autonomous", width = 2, height = 1,
		columnIndex = 0, rowIndex = 0)
	@Config(name = "Autonomous Chooser", tabName = "Debugger", width = 2, height = 1,
		columnIndex = 0, rowIndex = 0)
	public SendableChooser<Command> getChooser() {
		return autonomousChooser;
	}

	/**
	 * Use this to pass the autonomous command to the main {@link Robot} class.
	 *
	 * @return the command to run in autonomous
	 */
	public Command getAutonomousCommand() {
		m_autoCommand = autonomousChooser.getSelected();
		return m_autoCommand;
	}
}

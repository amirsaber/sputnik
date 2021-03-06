package com.sputnik;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.ColorSensor;
import lejos.nxt.comm.RConsole;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Sputnik {
	private static int wheelSize = 56;
	private static int trackWidth = 112;

	public static float lightThreshold = 40;

	private static int travelSpeed = 70;
	private static int rotateSpeed = 70;

	private static int map_x = 4;
	private static int map_y = 5;

	private static int heading = 0;
	private static int pos_x = 0;
	private static int pos_y = 0;

	public static int intersectionDistance_height = 255;
	public static int intersectionDistance_width = 255;

	public static RegulatedMotor scannerMotor = Motor.B;

	public static DifferentialPilot pilot = new DifferentialPilot(wheelSize,
			trackWidth, Motor.A, Motor.C);

	public static LightSensor lightSensor = new LightSensor(SensorPort.S1);

	public static ColorSensor colorSensor = new ColorSensor(SensorPort.S3);

	public static OdometryPoseProvider opp = new OdometryPoseProvider(
			Sputnik.pilot);

	public static Map robotMap = new Map(map_x, map_y, pos_x, pos_y, heading);

	public static void main(String[] args) {
		Button.waitForAnyPress();
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		Behavior pathSweeping = new PathSweeping();
		Behavior pathPlanning = new PathPlanning();
		Behavior ballDetecting = new BallDetecting();
		Behavior localizing = new Localizing();
		//pathPlanning.action();
		Behavior[] bArray = {pathSweeping, pathPlanning, localizing, ballDetecting };
		Arbitrator arby = new Arbitrator(bArray);
		arby.start();
	}
}

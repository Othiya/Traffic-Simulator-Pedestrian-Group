package thesisfinal;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class Parameters {

    static int simulationStep = 1;
    static int simulationEndTime;
    static double pixelPerStrip;
    static double pixelPerMeter;
    static double pixelPerFootpathStrip;
    static int simulationSpeed;
    static double encounterPerAccident;//-----------------------------------rename and redefine
    static double stripWidth;
    static double footpathStripWidth;
    static double maximumSpeed;
    static boolean acrossPedestrianMode;
    static boolean alongPedestrianMode;
    static boolean DEBUG_MODE;
    static boolean TRACE_MODE;
    static Random random;
    static int seed;
    static int SIGNAL_CHANGE_DURATION;
    static double DEFAULT_TRANSLATE_X;
    static double DEFAULT_TRANSLATE_Y;
    static boolean CENTERED_VIEW;
    static double slowVehiclePercentage;
    static double mediumVehiclePercentage;
    static double fastVehiclePercentage;
    static double TTC_THRESHOLD;
    static DLC_MODEL lane_changing_model;
    static CAR_FOLLOWING_MODEL car_following_model;
    static VEHICLE_GENERATION_RATE vehicle_generation_rate;
    static boolean ERROR_MODE;
    static boolean OBJECT_MODE;
    static int FT_METHOD;
    static int NO_OF_READINGS;
    static double M_FACTOR;
    static boolean GUI_MODE;
    static double ALPHA;
    static double BETA;
    static double ETA;
    static int ACROSS_PEDESTRIAN_LIMIT; // probability of generating pedestrian is 1/this parameter
    static int ACROSS_PEDESTRIAN_PER_HOUR;
    static int ALONG_PEDESTRIAN_PERCENTAGE;
    static int NO_OF_ROUTES_FOR_STAT;
    static boolean BRAKE_HARD;
    static int DENSITY_PERCENTAGE;
    static double PEDESTRIAN_WEIGHT;
    static int PEDESTRIAN_RANDOM_LANE_CHANGE_PERCENTAGE;
    static int PEDESTRIAN_LEFT_BIAS_PERCENTAGE;
    static boolean PENALTY_WAIT;
    static boolean CONSIDER_MINIMUM;
    final static int SIDE_STRIPS_TO_CONSIDER = 1;

    static double totalTypesOfObjects = 4;


    static JSlider showProgressSlider;
    static ArrayList<Integer> simulationStepLineNos;

    enum DLC_MODEL {    // Discretionary lane changing models
        NAIVE_MODEL,    // no model at all; just instantly changes lane if possible
        GIPPS_MODEL,
        GHR_MODEL,
        MOBIL_MODEL
    }

    enum CAR_FOLLOWING_MODEL {
        NAIVE_MODEL,    // no model at all; just moves as fast as possible and brakes as hard as possible
        GIPPS_MODEL,    // moving velocity is controlled; but no braking; so collision could not be avoided
        HYBRID_MODEL,    // velocity from GIPPS model, braking hard in emergency
        KRAUSS_MODEL,
        GFM_MODEL,
        IDM_MODEL,
        RVF_MODEL,
        VFIAC_MODEL,
        OVCM_MODEL,
        KFTM_MODEL,
        HDM_MODEL,
        SBM_MODEL,
        MY_MODEL
    }

    enum VEHICLE_GENERATION_RATE {
        POISSON,
        CONSTANT
    }
}

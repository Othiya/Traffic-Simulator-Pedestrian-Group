package thesisfinal;

import java.util.ArrayList;

public class Statistics {
    static double[] avgSpeedOfVehicle;
    static int[] noOfVehicles;
    static int[] waitingTime;
    static int[] totalTravelTime;
    static int[][] tripTime;
    static double[][] noOfVehiclesCompletingTrip;
    static double[][] totalFuelConsumption;
    static int noOfCollisions;
    static int noOfAccidents;
    static double[][] noCollisionsPerDemand;
    static double[][] noAccidentsPerDemand;
    static double[] flow;
    static int flowCount;
    static double[] noOfGeneratedVehicles;

    static ArrayList<VehicleStats> vehicleStats;

    Statistics(int demandSize) {
        avgSpeedOfVehicle = new double[Constants.TYPES_OF_CARS];
        noOfVehicles = new int[Constants.TYPES_OF_CARS];
        noOfGeneratedVehicles = new double[Constants.TYPES_OF_CARS];
        waitingTime = new int[Constants.TYPES_OF_CARS];
        totalTravelTime = new int[Constants.TYPES_OF_CARS];
        totalFuelConsumption = new double[demandSize][Constants.TYPES_OF_CARS];

        noOfVehiclesCompletingTrip = new double[demandSize][Constants.TYPES_OF_CARS];
        noCollisionsPerDemand = new double[demandSize][Constants.TYPES_OF_CARS];
        noAccidentsPerDemand = new double[demandSize][Constants.TYPES_OF_CARS];
        tripTime = new int[demandSize][Constants.TYPES_OF_CARS];
        vehicleStats = new ArrayList<>();
        noOfCollisions = 0;
        noOfAccidents = 0;

        flow = new double[Parameters.simulationEndTime / (int)(60 * Constants.TIME_STEP)];
        flowCount = 0;
    }

    static void saveVehicleStat(VehicleStats stats) {
        int[] vehicleIds = {1, 2, 3};

        for (int id : vehicleIds) {
            if (id == stats.getVehicleId()) {
                vehicleStats.add(stats);
            }
        }
    }
}

package thesisfinal;

import java.awt.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author USER
 */
public class Pedestrian {
    public double relaxationTime;
    public double desiredSpeed;
    public double radius;
    private double destPos;
    private double currX;
    private double currY;
    private SocialForceModel sfm_model;

    private int pedestrianId;
    private int startTime;
    boolean toRemove = false;
    private Segment segment;
    private double initPos;
    private double distance;
    private double speed;
    private Strip strip;
    boolean inAccident = false;
    private boolean reverse;
    public int clock = Parameters.simulationStep;
    public int index;

    public Pedestrian(int pedestrianId, Segment seg, int strip, double initpos, double sp, double dp,
            SocialForceModel sfm_model) {
        radius = 1;
        destPos = dp;
        desiredSpeed = 1;
        relaxationTime = 2;
        // this.sfm_model = sfm_model;

        this.pedestrianId = pedestrianId;
        if (strip != 0) {
            reverse = true;
            this.strip = seg.getStrip(seg.numberOfStrips() - 1);
        } else {
            reverse = false;
            this.strip = seg.getStrip(0);
        }
        segment = seg;
        initPos = initpos;
        distance = 0;
        // System.out.println("speed when created" + speed);
        speed = sp;
        this.strip.addPedestrian(this);
        startTime = Parameters.simulationStep;

    }

    void cleanUp() {
        strip.delPedestrian(this);
        toRemove = true;
    }

    boolean isStuck() {
        return Parameters.simulationStep - startTime >= ((segment.getSegmentWidth() / speed) * Constants.TIME_STEP);
    }

    @Override
    public String toString() {
        return "Pedestrian{" +
                "pedestrianId=" + pedestrianId +
                ", initPos=" + initPos +
                ", distance=" + distance +
                ", speed=" + speed +
                ", strip=" + strip.getStripIndex() +
                ", distanceInSegment= " + getDistanceInSegment() +
                '}';
    }

    void printPedestrianDetails() {
        if (Parameters.DEBUG_MODE) {
            if (pedestrianId == 5899 || pedestrianId == -711) {
                String pathname = "debug/p_debug" + pedestrianId + ".txt";
                try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(pathname), true))) {
                    writer.println("Sim step: " + Parameters.simulationStep);
                    writer.println("Pedestrian ID: " + pedestrianId);
                    writer.println("Init Pos: " + initPos);
                    writer.printf("Speed: %.2f\n", speed);
                    writer.printf("Distance in Segment: %.2f\n", getDistanceInSegment());
                    writer.println("Strip Index: " + strip.getStripIndex());
                    writer.println();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public int getPedestrianId() {
        return pedestrianId;
    }

    private double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    double getInitPos() {
        return initPos;
    }

    boolean getReverseSegment() {
        return strip.getStripIndex() > segment.middleLowStripIndex;
    }

    double getDistanceInSegment() {
        int stripIndex = strip.getStripIndex();
        boolean reverseSegment = getReverseSegment();

        if (reverseSegment) {
            return segment.getLength() - initPos;
        } else {
            return initPos;
        }
    }

    Segment getSegment() {
        return segment;
    }

    double getUpdatedSpeed() {
        double new_speed = sfm_model.getSpeedFromSFM(this, getNearbyPedestrian(), Parameters.simulationStep);
        System.out.println("here is speed from SFM " + new_speed);
        return new_speed;
    }

    // TODO ????
    boolean moveLengthWise() {
        // speed = getUpdatedSpeed();

        boolean reverseSegment = getReverseSegment();
        if (reverseSegment) {
            if (strip.hasGapForMoveAlongPositive(this)) {
                this.initPos += this.speed;
                return true;
            }
        } else {
            if (strip.hasGapForMoveAlongNegative(this)) {
                this.initPos -= this.speed;
                return true;
            }
        }
        return false;
    }

    boolean moveForward() {

        // speed = getUpdatedSpeed();
        System.out.println(speed);

        if (!reverse) {
            int x;
            if (distance + speed < Parameters.footpathStripWidth) {

                distance += speed;
                return true;
            }
            x = 1 + (int) ((distance - Parameters.footpathStripWidth + speed) / Parameters.stripWidth);
            if (x < segment.numberOfStrips()) {
                if (segment.getStrip(x).hasGapForPedestrian(this)) {
                    distance = distance + speed;
                    strip.delPedestrian(this);
                    setStrip(segment.getStrip(x));
                    strip.addPedestrian(this);
                    return true;
                }
                return false;
            } else {
                distance = distance + speed;
                strip.delPedestrian(this);
                return true;
            }
        } else {
            int x;
            double w = 1 * Parameters.footpathStripWidth + (segment.numberOfStrips() - 1) * Parameters.stripWidth;
            x = 1 + (int) ((w - distance - speed - Parameters.footpathStripWidth) / Parameters.stripWidth);
            if (x > 1) {
                if (segment.getStrip(x).hasGapForPedestrian(this)) {
                    distance = distance + speed;
                    strip.delPedestrian(this);
                    setStrip(segment.getStrip(x));
                    strip.addPedestrian(this);
                    return true;
                }
                return false;
            } else {
                distance = distance + speed;
                strip.delPedestrian(this);
                return true;
            }
        }
    }

    public Strip getStrip() {
        return strip;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    private void setStrip(Strip strip) {
        this.strip = strip;
    }

    boolean hasCrossedRoad() {
        // System.out.println(distance + " " + segment.getSegWidth() + " " +
        // strip.getStripIndex());
        return distance >= segment.getSegmentWidth();
    }

    public double getX() {

        double mpRatio = Parameters.pixelPerMeter;
        double fpStripPixelCount = Parameters.pixelPerFootpathStrip;

        if (!reverse) {
            Segment seg = getSegment();
            double segmentLength = seg.getLength();
            double length = 1;
            // Using internally section or ratio formula,it finds the coordinates along
            // which vehicles are
            double xp = (getInitPos() * seg.getEndX() + (segmentLength - getInitPos()) * seg.getStartX())
                    / segmentLength * mpRatio;
            double yp = (getInitPos() * seg.getEndY() + (segmentLength - getInitPos()) * seg.getStartY())
                    / segmentLength * mpRatio;
            double xq = ((getInitPos() + length) * seg.getEndX()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartX()) / segmentLength * mpRatio;
            double yq = ((getInitPos() + length) * seg.getEndY()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartY()) / segmentLength * mpRatio;
            int x1 = (int) Math.round(Utilities.returnX3(xp, yp, xq, yq,
                    (getDistance() / Parameters.footpathStripWidth) * fpStripPixelCount)); // obj.getDistance()*mpRatio

            //////////////// setting currentPos
            currX = x1;
        }

        else {
            Segment seg = getSegment();
            double segmentLength = seg.getLength();
            double length = 1;
            // Using internally section or ratio formula,it finds the coordinates along
            // which vehicles are
            double xp = (getInitPos() * seg.getEndX() + (segmentLength - getInitPos()) * seg.getStartX())
                    / segmentLength * mpRatio;
            double yp = (getInitPos() * seg.getEndY() + (segmentLength - getInitPos()) * seg.getStartY())
                    / segmentLength * mpRatio;
            double xq = ((getInitPos() + length) * seg.getEndX()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartX()) / segmentLength * mpRatio;
            double yq = ((getInitPos() + length) * seg.getEndY()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartY()) / segmentLength * mpRatio;
            double w = 1 * Parameters.footpathStripWidth + (segment.numberOfStrips() - 1) * Parameters.stripWidth;
            int wi = (int) (w - getDistance());
            int x1 = (int) Math.round(
                    Utilities.returnX3(xp, yp, xq, yq, (wi / Parameters.footpathStripWidth) * fpStripPixelCount)); // obj.getDistance()*mpRatio

            //////////////// setting currentPos
            currX = x1;

        }

        return currX;

    }

    public double getY() {

        double mpRatio = Parameters.pixelPerMeter;
        double fpStripPixelCount = Parameters.pixelPerFootpathStrip;

        if (!reverse) {
            Segment seg = getSegment();
            double segmentLength = seg.getLength();
            double length = 1;
            // Using internally section or ratio formula,it finds the coordinates along
            // which vehicles are
            double xp = (getInitPos() * seg.getEndX() + (segmentLength - getInitPos()) * seg.getStartX())
                    / segmentLength * mpRatio;
            double yp = (getInitPos() * seg.getEndY() + (segmentLength - getInitPos()) * seg.getStartY())
                    / segmentLength * mpRatio;
            double xq = ((getInitPos() + length) * seg.getEndX()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartX()) / segmentLength * mpRatio;
            double yq = ((getInitPos() + length) * seg.getEndY()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartY()) / segmentLength * mpRatio;
            int y1 = (int) Math.round(Utilities.returnY3(xp, yp, xq, yq,
                    (getDistance() / Parameters.footpathStripWidth) * fpStripPixelCount));

            //////////////// setting currentPos
            currY = y1;
        }

        else {
            Segment seg = getSegment();
            double segmentLength = seg.getLength();
            double length = 1;
            // Using internally section or ratio formula,it finds the coordinates along
            // which vehicles are
            double xp = (getInitPos() * seg.getEndX() + (segmentLength - getInitPos()) * seg.getStartX())
                    / segmentLength * mpRatio;
            double yp = (getInitPos() * seg.getEndY() + (segmentLength - getInitPos()) * seg.getStartY())
                    / segmentLength * mpRatio;
            double xq = ((getInitPos() + length) * seg.getEndX()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartX()) / segmentLength * mpRatio;
            double yq = ((getInitPos() + length) * seg.getEndY()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartY()) / segmentLength * mpRatio;
            double w = 1 * Parameters.footpathStripWidth + (segment.numberOfStrips() - 1) * Parameters.stripWidth;
            int wi = (int) (w - getDistance());
            int y1 = (int) Math.round(
                    Utilities.returnY3(xp, yp, xq, yq, (wi / Parameters.footpathStripWidth) * fpStripPixelCount));

            //////////////// setting currentPos
            currY = y1;

        }

        return currY;

    }

    int getDestPosX() {
        Segment seg = getSegment();
        double segmentLength = seg.getLength();
        double length = 1;
        // Using internally section or ratio formula,it finds the coordinates along
        // which vehicles are
        double xp = (destPos * seg.getEndX() + (segmentLength - destPos) * seg.getStartX()) / segmentLength
                * Parameters.pixelPerMeter;
        double yp = (destPos * seg.getEndY() + (segmentLength - destPos) * seg.getStartY()) / segmentLength
                * Parameters.pixelPerMeter;
        double xq = ((destPos + length) * seg.getEndX() + (segmentLength - (destPos + length)) * seg.getStartX())
                / segmentLength * Parameters.pixelPerMeter;
        double yq = ((destPos + length) * seg.getEndY() + (segmentLength - (destPos + length)) * seg.getStartY())
                / segmentLength * Parameters.pixelPerMeter;
        int x1 = (int) Math.round(Utilities.returnX3(xp, yp, xq, yq,
                (seg.getSegmentWidth() / Parameters.footpathStripWidth) * Parameters.pixelPerFootpathStrip)); // obj.getDistance()*mpRatio
        return x1;
    }

    int getDestPosY() {
        Segment seg = getSegment();
        double segmentLength = seg.getLength();
        double length = 1;
        // Using internally section or ratio formula,it finds the coordinates along
        // which vehicles are
        double xp = (destPos * seg.getEndX() + (segmentLength - destPos) * seg.getStartX()) / segmentLength
                * Parameters.pixelPerMeter;
        double yp = (destPos * seg.getEndY() + (segmentLength - destPos) * seg.getStartY()) / segmentLength
                * Parameters.pixelPerMeter;
        double xq = ((destPos + length) * seg.getEndX() + (segmentLength - (destPos + length)) * seg.getStartX())
                / segmentLength * Parameters.pixelPerMeter;
        double yq = ((destPos + length) * seg.getEndY() + (segmentLength - (destPos + length)) * seg.getStartY())
                / segmentLength * Parameters.pixelPerMeter;
        int y1 = (int) Math.round(Utilities.returnY3(xp, yp, xq, yq,
                (seg.getSegmentWidth() / Parameters.footpathStripWidth) * Parameters.pixelPerFootpathStrip));
        return y1;
    }

    void drawMobilePedestrian(BufferedWriter traceWriter, Graphics2D g, double stripPixelCount, double mpRatio,
            double fpStripPixelCount) {
        if (!reverse) {
            Segment seg = getSegment();
            double segmentLength = seg.getLength();
            double length = 1;
            // Using internally section or ratio formula,it finds the coordinates along
            // which vehicles are
            double xp = (getInitPos() * seg.getEndX() + (segmentLength - getInitPos()) * seg.getStartX())
                    / segmentLength * mpRatio;
            double yp = (getInitPos() * seg.getEndY() + (segmentLength - getInitPos()) * seg.getStartY())
                    / segmentLength * mpRatio;
            double xq = ((getInitPos() + length) * seg.getEndX()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartX()) / segmentLength * mpRatio;
            double yq = ((getInitPos() + length) * seg.getEndY()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartY()) / segmentLength * mpRatio;
            int x1 = (int) Math.round(Utilities.returnX3(xp, yp, xq, yq,
                    (getDistance() / Parameters.footpathStripWidth) * fpStripPixelCount)); // obj.getDistance()*mpRatio
            int y1 = (int) Math.round(Utilities.returnY3(xp, yp, xq, yq,
                    (getDistance() / Parameters.footpathStripWidth) * fpStripPixelCount));

            //////////////// setting currentPos
            currX = x1;
            currY = y1;

            if (inAccident) {
                g.setColor(Color.red);
                g.fillOval(x1, y1, (int) (1.5 * mpRatio), (int) (1.5 * mpRatio));
            } else {
                g.setColor(Constants.pedestrianColor);
                g.fillOval(x1, y1, (int) (0.4 * mpRatio), (int) (0.4 * mpRatio));
            }

            if (Parameters.DEBUG_MODE) {
                Font font = new Font("Serif", Font.PLAIN, 64);
                g.setFont(font);
                g.drawString(Integer.toString(pedestrianId), x1, y1);
            }
            try {
                traceWriter.write(x1 + " " + y1 + " " + inAccident);
                traceWriter.newLine();
            } catch (IOException ex) {
                Logger.getLogger(Pedestrian.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Segment seg = getSegment();
            double segmentLength = seg.getLength();
            double length = 1;
            // Using internally section or ratio formula,it finds the coordinates along
            // which vehicles are
            double xp = (getInitPos() * seg.getEndX() + (segmentLength - getInitPos()) * seg.getStartX())
                    / segmentLength * mpRatio;
            double yp = (getInitPos() * seg.getEndY() + (segmentLength - getInitPos()) * seg.getStartY())
                    / segmentLength * mpRatio;
            double xq = ((getInitPos() + length) * seg.getEndX()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartX()) / segmentLength * mpRatio;
            double yq = ((getInitPos() + length) * seg.getEndY()
                    + (segmentLength - (getInitPos() + length)) * seg.getStartY()) / segmentLength * mpRatio;
            double w = 1 * Parameters.footpathStripWidth + (segment.numberOfStrips() - 1) * Parameters.stripWidth;
            int wi = (int) (w - getDistance());
            int x1 = (int) Math.round(
                    Utilities.returnX3(xp, yp, xq, yq, (wi / Parameters.footpathStripWidth) * fpStripPixelCount)); // obj.getDistance()*mpRatio
            int y1 = (int) Math.round(
                    Utilities.returnY3(xp, yp, xq, yq, (wi / Parameters.footpathStripWidth) * fpStripPixelCount));

            //////////////// setting currentPos
            currX = x1;
            currY = y1;

            if (inAccident) {
                g.setColor(Color.red);
                g.fillOval(x1, y1, (int) (1.2 * mpRatio), (int) (1.2 * mpRatio));
            } else {
                g.setColor(Constants.pedestrianColor);
                g.fillOval(x1, y1, (int) (0.4 * mpRatio), (int) (0.4 * mpRatio));
            }
            if (Parameters.DEBUG_MODE) {
                Font font = new Font("Serif", Font.PLAIN, 64);
                g.setFont(font);
                g.drawString(Integer.toString(pedestrianId), x1, y1);
            }
            try {
                traceWriter.write(x1 + " " + y1 + " " + inAccident);
                traceWriter.newLine();
            } catch (IOException ex) {
                Logger.getLogger(Pedestrian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void printObject() {
        System.out.println(index + " " + initPos + " " + distance + " " + speed);
    }

    boolean isToRemove() {
        return toRemove;
    }

    boolean isInAccident() {
        return inAccident;
    }

    void setToRemove(boolean b) {
        toRemove = b;
    }

    ////////////////////////////////////////

    Pedestrian getNearbyPedestrian() {
        if (!reverse) {

            int x = 1 + (int) ((distance - Parameters.footpathStripWidth + speed) / Parameters.stripWidth);
            if (x < segment.numberOfStrips()) {
                Pedestrian q = segment.getStrip(x).getAnyPedestrianInRange(distance, distance + 100);
                return q;
            }
        } else {

            int x;
            double w = 1 * Parameters.footpathStripWidth + (segment.numberOfStrips() - 1) * Parameters.stripWidth;
            x = 1 + (int) ((w - distance - speed - Parameters.footpathStripWidth) / Parameters.stripWidth);
            if (x > 1) {
                Pedestrian q = segment.getStrip(x).getAnyPedestrianInRange(distance, distance + 100);
                return q;
            }

        }
        return null;

    }

    // private void formGroup() {
    // // Initialize groups as a list of groups of pedestrians
    // Vector<Vector<Pedestrian>> groups = new Vector<>();

    // // Iterate over each pedestrian
    // for (Pedestrian pedestrian : pedestrians) {
    // boolean addedToGroup = false;

    // // Try to add the pedestrian to an existing group
    // for (Vector<Pedestrian> group : groups) {
    // for (Pedestrian otherPedestrian : group) {
    // // Check if the pedestrian is within the specified radius of any pedestrian
    // in
    // // the group
    // if (distance(pedestrian, otherPedestrian) <= stripWidth * 30
    // && pedestrian.getDirection() == otherPedestrian.getDirection()) {
    // group.add(pedestrian); // Add to the group
    // addedToGroup = true;
    // System.out.println("Added to group,group size: " + group.size());
    // break; // No need to check further, already added
    // }
    // }

    // if (addedToGroup) {
    // break; // Exit if the pedestrian is already added to a group
    // }
    // }

    // // If the pedestrian wasn't added to any existing group, create a new group
    // if (!addedToGroup) {
    // Vector<Pedestrian> newGroup = new Vector<>();
    // newGroup.add(pedestrian);
    // groups.add(newGroup); // Add the new group to the list of groups
    // }
    // }

    // // Process the newly formed groups as needed
    // for (Vector<Pedestrian> group : groups) {
    // // You can do any group-level operations here, such as adjusting positions
    // float init = 0;
    // for (Pedestrian ped : group) {
    // init += ped.getInitPos();
    // }
    // init = init / group.size();
    // for (Pedestrian ped : group) {
    // ped.setInitPos(init + (ped.getInitPos() - init) * stripWidth * 5 /
    // group.size());
    // }
    // }
    // }

    // // Helper function to calculate the distance between two pedestrians
    // private double distance(Pedestrian p1, Pedestrian p2) {
    // double DiffX = p1.getInitPos() - p2.getInitPos();
    // double DiffY = p1.getDistanceInSegment() - p2.getDistanceInSegment();
    // double Diff = Math.sqrt(DiffY * DiffY + DiffX * DiffX);
    // return Diff;
    // }

    // private void movePedestrians() {
    // formGroup();
    // for (Pedestrian pedestrian : pedestrians) {
    // if (pedestrian.hasCrossedRoad()) {
    // pedestrian.setToRemove(true);
    // } else {
    // if (!pedestrian.moveForward()) {
    // if (!pedestrian.moveLengthWise()) {
    // if (pedestrian.isStuck()) {
    // pedestrian.cleanUp();
    // }
    // }

    // }
    // pedestrian.printPedestrianDetails();
    // }
    // }
    // }
}

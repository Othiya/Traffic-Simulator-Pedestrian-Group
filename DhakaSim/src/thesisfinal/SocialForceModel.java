package thesisfinal;

public class SocialForceModel {
    double drivingForce;
    double pedestrianInteractionForce;
    double obstacleInteractionForce;
    double fluctuationForce;

    double A = 1;// Strength coefficient for pedestrian repulsion
    double B = 0.3;// Range of interaction
    double K = 1;// Repulsive force from obstacles
    double lambda = 0.5;// ranges from 0 to 1 Anisotropy coefficient

    public double getAnistropicFactor(Pedestrian p, Vector Nab) {

        Vector e_alpha = (getcurrentSpeedVector(p)).normalize();
        double cos_phi = -(e_alpha.dot(Nab));
        double Fab = lambda + (1 - lambda) * ((1 + cos_phi) / 2);

        return Fab;
    }

    public Vector getcurrentSpeedVector(Pedestrian p) {
        Vector currentPos = new Vector(p.getX(), p.getY());
        Vector desiredPos = new Vector(p.getDestPosX(), p.getDestPosY());

        Vector diff = desiredPos.subtract(currentPos);
        Vector e = diff.normalize();
        Vector currentSpeedVec = e.multiply(p.getSpeed());
        return currentSpeedVec;

    }

    double g(double x) {
        if (x > 0) {
            return x;
        }

        return 0;
    }

    public Vector getDrivingForce(Pedestrian p) {

        Vector currentPos = new Vector(p.getX(), p.getY());
        Vector desiredPos = new Vector(p.getDestPosX(), p.getDestPosY());
        System.out.println("Position vector " + currentPos);

        Vector diff = desiredPos.subtract(currentPos);
        Vector e = diff.normalize();
        Vector desiredSpeedVec = e.multiply(p.desiredSpeed);
        System.out.println("direction vector " + e);
        System.out.println("desired " + desiredSpeedVec);
        Vector currentSpeedVec = e.multiply(p.getSpeed());

        System.out.println("current " + currentSpeedVec);

        Vector drivingForce = ((desiredSpeedVec).subtract(currentSpeedVec)).divide(p.relaxationTime);

        return drivingForce;
    }

    public Vector getPedestrianInteractionForce(Pedestrian a, Pedestrian b) {

        double Rab = a.radius + b.radius;// Rab calculation
        Point2D center_a = new Point2D(a.getX(), a.getY());
        Point2D center_b = new Point2D(b.getX(), b.getY());
        double Dab = center_a.distance(center_b);// Dab calculation

        Vector currentPos_a = new Vector(a.getX(), a.getY());
        Vector currentPos_b = new Vector(b.getX(), b.getY());
        Vector Nab = (currentPos_a.subtract(currentPos_b)).divide(Dab);// Nab Calculation

        Vector speed_a = getcurrentSpeedVector(a);
        Vector speed_b = getcurrentSpeedVector(b);
        Vector Vab = speed_a.subtract(speed_b);// Vab calculation

        Vector Tab = Nab.tangent();

        double del_Vab = Vab.dot(Tab);
        double Fab = getAnistropicFactor(a, Nab);

        Vector SOCForceForPedestrian = Nab.multiply(A * Math.exp((Rab - Dab) / B) * Fab);
        Vector PHForceForPedestrian = (Nab.multiply(K * g(Rab - Dab))).add(Tab.multiply(K * g(Rab - Dab) * del_Vab));
        Vector pedestrianInteractionForce = SOCForceForPedestrian.add(PHForceForPedestrian);

        return pedestrianInteractionForce;
    }

    /*
     * public Vector getObjectInteractionForce(Pedestrian a, Object b) {
     * 
     * double Rab = a.radius + b.getObjectLength();// Rab calculation
     * Point2D center_a = new Point2D(a.getX(), a.getY());
     * Point2D center_b = new Point2D(b.getX(), b.getY());
     * double Dab = center_a.distance(center_b);// Dab calculation
     * 
     * Vector currentPos_a = new Vector(a.getX(), a.getY());
     * Vector currentPos_b = new Vector(b.getX(), b.getY());
     * Vector Nab = (currentPos_a.subtract(currentPos_b)).divide(Dab);// Nab
     * Calculation
     * 
     * Vector speed_a = getcurrentSpeedVector(a);
     * Vector Vab = speed_a; // Object is static so v=0
     * 
     * Vector Tab = Nab.tangent();
     * 
     * double del_Vab = Vab.dot(Tab);
     * double Fab = getAnistropicFactor(a, Nab);
     * 
     * Vector SOCForceForObject = Nab.multiply(A * Math.exp((Rab - Dab) / B) * Fab);
     * Vector PHForceForObject = (Nab.multiply(K * g(Rab - Dab))).add(Tab.multiply(K
     * * g(Rab - Dab) * del_Vab));
     * Vector objectInteractionForce = SOCForceForObject.add(PHForceForObject);
     * 
     * return objectInteractionForce;
     * }
     */
    double getSpeedFromSFM(Pedestrian p, Pedestrian q, int time) {

        // no nearby pedestrian
        if (q == null) {
            Vector force = getDrivingForce(p); // there is no nearby pedestrian

            double force_value = force.magnitude();

            double speed = force_value * time + p.getSpeed();// mass of every pedestrian is assumed to be 1
            System.out.println("without pedestrian" + speed);
            return speed;
        }

        // Vector force = (getDrivingForce(p).add(getPedestrianInteractionForce(p, q)))
        // .add(getObjectInteractionForce(p, null));
        Vector force = getDrivingForce(p).add(getPedestrianInteractionForce(p, q));

        double force_value = force.magnitude();
        System.out.println("with pedestrian" + force_value);
        double speed = force_value * time + p.getSpeed();// mass of every pedestrian is assumed to be 1

        return speed;

    }

}

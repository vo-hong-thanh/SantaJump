/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vot2
 */
public class Location {
    private int id;
    private double longitude; //run north-south: -180 and +180 degrees
    private double latitude; //east-west: ranging between -90 and +90
    
    private long weight; //gram
    
    public Location(int id, double longitude, double latitude, long weight)
    {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;        
        this.weight = weight;
    }
        
    public int getId()
    {
        return id;
    }
    
    public double getLatitude()
    {
        return latitude;
    }
    
    public double getLongitude()
    {
        return longitude;
    }
    
    public long getWeight()
    {
        return weight;
    }
    
    public static double distanceBetween(Location A, Location B)
    {
        double phi1 = Math.toRadians(A.latitude);
        double phi2 = Math.toRadians(B.latitude);
        double deltaPhi = Math.toRadians(B.latitude - A.latitude);
        double deltaLambda = Math.toRadians(B.longitude - A.longitude);

        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                   Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = Constants.R * c;
        
        return d;
    }
    
    public String toString()
    {
        return id + "(" + longitude +"N, " + latitude +"E)";
    }
}


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vot2
 */
public class RunImprovedSanta {

    public static void main(String[] args) throws Exception {
        System.out.println("Heuristic Santa Jump");
//        boolean dumpIt = true;
        Location Santa = new Location(1, 68.073611, 29.315278, 0);

        ArrayList<Location> destinationList = new ArrayList<>();

        File currentDirectory = new File(".");
        File file = new File(currentDirectory, "nicelist.txt");
        System.out.println("Reading file: " + file.getName());
        BufferedReader datafileReader = new BufferedReader(new FileReader(file));
        String data;
        while ((data = datafileReader.readLine()) != null) {
//            System.out.println(" >> read: " + data);
            StringTokenizer token = new StringTokenizer(data, ";");
            int id = Integer.parseInt(token.nextToken());
            double longitude = Double.parseDouble(token.nextToken());
            double latitude = Double.parseDouble(token.nextToken());
            long weight = Long.parseLong(token.nextToken());

            destinationList.add(new Location(id, longitude, latitude, weight));
        }
        System.out.println("Total: " + destinationList.size() + " destinations");

        BufferedWriter writer = new BufferedWriter(new FileWriter("ImprovedSantaRun.csv"));

//        BufferedWriter dumpWriter = null;
//        if (dumpIt) {
//            dumpWriter = new BufferedWriter(new FileWriter("SantaRun_Dump.txt"));
//        }

        System.out.println("Build pathes");
        double totalDistance = 0;
        int run = 1;
        do {

            int V = destinationList.size() + 1;

            Location[] locations = new Location[V];
            locations[0] = Santa;
            for (int i = 0; i < destinationList.size(); i++) {
                locations[i + 1] = destinationList.get(i);
            }

            // To represent set of vertices not yet included in MST 
            int parent[] = new int[V];
            boolean mstSet[] = new boolean[destinationList.size() + 1];
            double[] distances = new double[destinationList.size() + 1];

            // Initialize all distances
            //Santa to Santa
            distances[0] = 0;
            parent[0] = -1;
            mstSet[0] = false;

            //Santa to others
//            System.out.println("Distance between " + Santa + " to "); 
            for (int i = 1; i < V; i++) {
                distances[i] = Location.distanceBetween(Santa, locations[i]);
//                System.out.println(" location " + locations[i] +" = " + distances[i]);                    
                mstSet[i] = false;
            }

            //MST tree
            int count = 0;
            ArrayList<Integer> path = new ArrayList<>();
            while (count < V) {
                // Pick thd minimum distances vertex from the set of vertices 
                // not yet included in MST 
                int u = minDistanceIndex(V, distances, mstSet);
                
                if(parent[u] == 0 && count > 1)
                {
                    int lastVisited = path.get(count - 1);
                    int candidateU = minDistanceIndex(V, lastVisited, parent, distances, mstSet);
                    
                    double uLastVisitedSanta = distances[u] + Location.distanceBetween(locations[u], locations[lastVisited]);
                    double lastVisitedCandidateUSanta = distances[candidateU] + Location.distanceBetween(locations[candidateU], Santa);
                    
                    System.out.println("distance between " + locations[lastVisited] + " to " + Santa + " to " + locations[u] + " = " + uLastVisitedSanta);
                    System.out.println("distance between " + locations[lastVisited] + " to " + locations[candidateU] + " to " + Santa  + " = " + lastVisitedCandidateUSanta);
                    
                    if(lastVisitedCandidateUSanta < uLastVisitedSanta)
                    {
                        System.out.println("update " + u + " to " + candidateU); 
                        u = candidateU;
                    }
                    else
                    {
                        System.out.println("no change " + u); 
                    }
                }
                
                // Add the picked vertex to the MST Set 
                mstSet[u] = true;
                
                //add to the path
                path.add(u);

                // Update distances value and parent index of the adjacent 
                // vertices of the picked vertex. Consider only those 
                // vertices which are not yet included in MST 
                for (int v = 0; v < V; v++) {
                    // graph[u][v] is non zero only for adjacent vertices of m 
                    // mstSet[v] is false for vertices not yet included in MST 
                    // Update the distances only if graph[u][v] is smaller than distances[v] 
                    
                    if (mstSet[v] == false) {
                        double distanceUV = Location.distanceBetween(locations[u], locations[v]);
                        if(distanceUV != 0 && distanceUV < distances[v]){
                            parent[v] = u;
                            distances[v] = distanceUV;
                        }
                    }
                }

                count++;
            }

            long weightOfCompleteCycle = 0;
            int i = 0;
            while (i < path.size()) {
                int locationIndex = path.get(i);
                weightOfCompleteCycle += locations[locationIndex].getWeight();
                if (weightOfCompleteCycle > Constants.Max_Weight) {
                    break;
                }
                i++;
            }

            System.out.println("Path @" + run);
//            if (dumpIt) {
//                dumpWriter.write("Path @" + run);
//                dumpWriter.newLine();
//                dumpWriter.flush();
//            }
            run++;
            long weightOfPath = 0;
            
            System.out.println("Starting at Location " + locations[path.get(0)]);
//            if (dumpIt) {
//                dumpWriter.write("Starting at Location " + locations[path.get(0)]);
//                dumpWriter.newLine();
//                dumpWriter.flush();
//            }
            
            StringBuffer outputPath = new StringBuffer();
            for (int r = 1; r < i; r++) {
                int locationIndex = path.get(r);
                weightOfPath += locations[locationIndex].getWeight();
                totalDistance += distances[locationIndex];

                System.out.println("-> Location " + locations[locationIndex] + " distance: " + distances[locationIndex] + " carrying " + locations[locationIndex].getWeight() + " parent location " + locations[parent[locationIndex]]);
                
//                if (dumpIt) {
//                    dumpWriter.write("-> Location " + locations[locationIndex] + " distance: " + distances[locationIndex] + " carrying " + locations[locationIndex].getWeight() + " parent location " + locations[parent[locationIndex]]);
//                    dumpWriter.newLine();
//                    dumpWriter.flush();
//                }
                
                if(r == i-1)
                {
                    System.out.println("-> return " + locations[0]);
//                    if (dumpIt) {
//                        dumpWriter.write("-> return " + locations[0]);
//                        dumpWriter.newLine();
//                        dumpWriter.flush();
//                    }
                    totalDistance += Location.distanceBetween(Santa, locations[locationIndex]);
                }
                
                destinationList.remove(locations[path.get(r)]);

                outputPath.append(locations[locationIndex].getId());
                outputPath.append("; ");
            }
            outputPath.delete(outputPath.length() - 2, outputPath.length());
            writer.write(outputPath.toString());
            writer.newLine();
            writer.flush();

            System.out.println("Weight on path " + weightOfPath + " (gram), distance travelled so far " + totalDistance + " (km)");
//            if (dumpIt) {
//                dumpWriter.write("Weight on path " + weightOfPath + " (gram), distance travelled so far " + totalDistance + " (km)");
//                dumpWriter.newLine();
//                dumpWriter.flush();
//            }
        } while (!destinationList.isEmpty());

        System.out.println("Total distance travelled " + totalDistance + " (km)");

//        if (dumpIt) {
//            dumpWriter.write("Total distance travelled " + totalDistance + " (km)");
//            dumpWriter.newLine();
//            dumpWriter.flush();
//            
//            dumpWriter.close();
//        }
        //done
        writer.flush();
        writer.close();
        System.out.println("Done!");
    }

    private static int minDistanceIndex(int Vertices, double distances[], boolean mstSet[]) {
        // Initialize min value 
        double min = Double.MAX_VALUE;
        int min_index = -1;

        for (int v = 0; v < Vertices; v++) {
            if (!mstSet[v] && distances[v] < min) {
                min = distances[v];
                min_index = v;
            }
        }

        return min_index;
    }

    private static int minDistanceIndex(int Vertices, int lastVisitedVertex, int[] parent, double distances[], boolean mstSet[]) {
        // Initialize min value 
        double min = Double.MAX_VALUE;
        int min_index = -1;

        for (int v = 0; v < Vertices; v++) {
            if (!mstSet[v] && parent[v] == lastVisitedVertex && distances[v] < min) {
                min = distances[v];
                min_index = v;
            }
        }

        return min_index;
    }    
}

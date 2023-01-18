package giskard.assignment.oddscalculator.backend.utils;

import com.google.gson.Gson;
import giskard.assignment.oddscalculator.backend.model.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.sql.*;
import java.util.*;

import static giskard.assignment.oddscalculator.MillenniumFalconApplication.paths;

/**
 * Class with utility functions
 * @author Samuele Di Tullio
 */
public class ConfigurationParser {

    // create Gson instance
    private static Gson gson = new Gson();
    private static BufferedReader bufferedReader = null;

    /**
     * Given a path to a json file, return the configurations
     * @return the InputConfiguration object
     */
    public static InputConfiguration readConfiguration(String path) {
        InputConfiguration configurations = null;

        try {
            // create a file reader
            bufferedReader = new BufferedReader(new FileReader(path));
            // convert JSON file to InputConfiguration class
            configurations = gson.fromJson(bufferedReader, InputConfiguration.class);

            // print input configuration fields
            System.out.println("Input configuration json file:\n" + configurations);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Json configuration file not found at given path");
            System.exit(1);
        } finally {
            try {
                // close the reader
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }

            return configurations;
        }
    }

    /**
     * Connect to the db database
     * @return the Connection object
     */
    private static Connection connect(String url) {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection("jdbc:sqlite://" + url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    /**
     * Given a path to a DB, select all rows in the Routes table
     * @return the Graph with all possible routes
     */
    public static Graph readGraphFromDb(String path){
        String sql = "SELECT origin, destination, travel_time FROM routes";
        Graph graph = new Graph();

        try (Connection conn = connect(path);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            Node nodeA, nodeB;
            while (rs.next()) {
                System.out.println(rs.getString("origin") +  "\t" +
                        rs.getString("destination") + "\t" +
                        rs.getInt("travel_time"));

                nodeA = new Node(rs.getString("origin"));
                nodeA.addDestination(rs.getString("destination"), rs.getInt("travel_time"));

                //routes can be traversed both ways
                nodeB = new Node(rs.getString("destination"));
                nodeB.addDestination(rs.getString("origin"), rs.getInt("travel_time"));

                graph.addNode(nodeA);
                graph.addNode(nodeB);
                System.out.println("Node added!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return graph;
    }

    /**
     * List all paths from source to destination
     */
    public static void getAllPaths(InputConfiguration inputConfiguration, Graph graph) {
        if(inputConfiguration.getDeparture() == null || inputConfiguration.getArrival() == null) {
            return;
        }
        List<Node> nodeList = new ArrayList<>();

        // add source to path
        Node sourceNode = graph.getExistingNode(inputConfiguration.getDeparture());
        if(sourceNode != null) {
            nodeList.add(sourceNode);
        } else {
            System.err.println("Source node not existing in graph");
            return;
        }

        // Inizialize the map of visited nodes with all false
        Map<String, Boolean> isVisited = initializeIsVisitedMap(graph);
        // Call recursive utility
        getAllPathsUtil(inputConfiguration, graph, inputConfiguration.getDeparture(), inputConfiguration.getArrival(), isVisited, nodeList);
    }

    /**
     * Inizialize the map of visited nodes
     * @return the Map (nodeName : isVisited)
     */
    private static Map<String, Boolean> initializeIsVisitedMap(Graph graph) {
        Map<String, Boolean> isVisited = new HashMap<>();

        for(Node node : graph.getNodes()) {
            isVisited.put(node.getName(), false);
        }

        return isVisited;
    }


    /**
     * A recursive function to find all paths from source to destination Node
     */
    private static void getAllPathsUtil(InputConfiguration inputConfiguration, Graph graph, String source, String destination, Map<String, Boolean> isVisited, List<Node> localNodeList) {
        if (source.equals(destination)) {
            // if match found then no need to traverse more till depth
            Path pathToAdd = calculateActualPath(inputConfiguration, localNodeList);
            paths.add(pathToAdd);
            return;
        }

        // Mark the current node
        isVisited.put(source, true);

        // find all the nodes
        // adjacent to the current one
        for (Node i : graph.getNodes()) {
            if (i.getDestinationNameList().contains(source) && !isVisited.get(i.getName())) {
                // store current node
                // in path
                localNodeList.add(i);
                getAllPathsUtil(inputConfiguration, graph, i.getName(), destination, isVisited, localNodeList);

                // remove current node
                // in path
                localNodeList.remove(i);
            }
        }

        isVisited.put(source, false);
    }

    /**
     * Calculate the scheduled trip, adding eventual refuel steps, by counting fuel autonomy and total travel cost
     * @return an actual travel schedule path
     */
    private static Path calculateActualPath(InputConfiguration inputConfiguration, List<Node> localNodeList) {

        List<Node> newPath = new ArrayList<>();
        newPath.addAll(localNodeList);

        int cost = 0;
        int fuel = inputConfiguration.getAutonomy();
        for (int i = 0; i < localNodeList.size() - 1; i++) {
            for (Destination destination : localNodeList.get(i).getDestinations()) {
                if (destination.getDestinationName().equals(localNodeList.get(i + 1).getName())) {
                    cost += destination.getDistance();
                    fuel = fuel - destination.getDistance();

                    // If there is not enough fuel to move to the next step, refuel in the actual planet
                    if (fuel < 0) {
                        List<Node> head = localNodeList.subList(0, i);
                        List<Node> tail = localNodeList.subList(i, localNodeList.size());
                        newPath.clear();
                        newPath.addAll(head);
                        newPath.add(localNodeList.get(i));
                        newPath.addAll(tail);

                        cost += 1;
                        fuel = inputConfiguration.getAutonomy();
                    }
                }
            }
        }
        return new Path(newPath, cost);
    }

    /**
     * Calculate the odds to success for each path whose travel time is less than empire countdown
     * @return an actual travel schedule path
     */
    public static Map<Path, Double> calculateOdds(EmpireInterceptions interceptions) {
        System.out.println("Calculating odds...");
        Map<Path, Double> odds = new HashMap<>();

        // Update the paths list: Bounty hunters are avoided by stopping to one adjacent planet
        List<Path> newPaths= avoidBountyHuntersIterations(interceptions, paths);

        // Calculate the odds
        odds = decreaseOddsIteration(interceptions, odds, newPaths);

        return odds;
    }
    private static List<Path> avoidBountyHuntersIterations(EmpireInterceptions interceptions, List<Path> actualPaths) {
        List<Path> newPaths = new ArrayList<>();
        newPaths.addAll(actualPaths);
        int pathIndex = 0;
        for(Path path : actualPaths) {
            // number of times the Bounty Hunter tried to capture the Millennium Falcon
            int k = 0;
            Path newPath = path;
            // if the Millennium Falcon cannot reach Endor before the Death Star annihilates Endor, the probability of success is 0
            if (path.getCost() <= interceptions.getCountdown()) {
                int day = 0;
                for (int i = 0; i < path.getNodes().size() - 1; i++) {
                    Node node = path.getNodes().get(i);
                    Node nextNode = path.getNodes().get(i + 1);
                    for (BountyHunter bountyHunter : interceptions.getBounty_hunters()) {
                        // If Millennium Falcon pass by (or refuel) in a planet the same day in which the Bounty Hunter is expected, decrease the odds of success
                        if (node.getName().equals(bountyHunter.getPlanet()) && day == bountyHunter.getDay()) {
                            //If Millennium Falcon has enough days to reach the destination, avoid the planet with bounty hunters by landing one more day in the previous planet
                            if (interceptions.getCountdown() - newPath.getCost() > 0) {
                                newPath = avoidBountyHunters(newPath, path.getNodes().get(i - 1).getName(), newPath.getCost());
                                newPaths.remove(pathIndex);
                                newPaths.add(pathIndex, newPath);
                            }
                        }
                    }
                    day += node.getCost(nextNode.getName());
                }
            }
            pathIndex++;
        }
        return newPaths;
    }

    private static Map<Path, Double> decreaseOddsIteration(EmpireInterceptions interceptions, Map<Path, Double> odds, List<Path> actualPaths) {
        for(Path path : actualPaths) {
            // number of times the Bounty Hunter tried to capture the Millennium Falcon
            int k = 0;
            Double odd = Double.valueOf(100);
            Path newPath = path;
            // if the Millennium Falcon cannot reach Endor before the Death Star annihilates Endor, the probability of success is 0
            if (path.getCost() <= interceptions.getCountdown()) {
                int day = 0;
                for (int i = 0; i < path.getNodes().size() - 1; i++) {
                    Node node = path.getNodes().get(i);
                    Node nextNode = path.getNodes().get(i + 1);
                    for (BountyHunter bountyHunter : interceptions.getBounty_hunters()) {
                        // If Millennium Falcon pass by (or refuel) in a planet the same day in which the Bounty Hunter is expected, decrease the odds of success
                        if (node.getName().equals(bountyHunter.getPlanet()) && day == bountyHunter.getDay()) {
                            odd = decreaseOdds(odd, k);
                            k += 1;
                        }
                    }
                    day += node.getCost(nextNode.getName());
                }
                System.out.println("Odd for path " + newPath.toString() + ": = " + odd);
                odds.put(newPath, odd);
            }
        }
        return odds;
    }

    private static Path avoidBountyHunters(Path path, String planetToStay, int cost) {
        int step = 0;
        int i = 0;
        for(Node node : path.getNodes()) {
            if(node.getName().equals(planetToStay)) {
                step = i;
                break;
            }
            i++;
        }

        List<Node> newNodes = new ArrayList<>();
        newNodes.addAll(path.getNodes().subList(0, step));
        //Avoid bounty hunters by landing one more day in a nearby planet
        newNodes.add(path.getNodes().get(step));
        newNodes.addAll(path.getNodes().subList(step, path.getNodes().size()));
        return new Path(newNodes, cost+1);
    }

    private static double decreaseOdds(double odds, int k) {
        if(odds == 100.0) {
            return 90;
        } else {
            return (odds - (Math.pow(9, k)/Math.pow(10, k+1))*100);
        }
    }
}

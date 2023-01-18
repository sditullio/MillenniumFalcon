package giskard.assignment.oddscalculator.backend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that represents a planet node in the graph
 * @author Samuele Di Tullio
 */
@Slf4j
public class Node {

    @Getter
    @Setter
    private String name;


    // The adjacentNodes attribute is used to associate immediate neighbors with edge length
    @Getter
    @Setter
    private List<Destination> destinations;


    public void addDestination(String destination, int distance) {
        destinations.add(new Destination(destination, distance));
    }

    public Node(String name) {
        this.name = name;
        destinations = new ArrayList<>();
    }

    public List<String> getDestinationNameList() {
        List<String> result = new ArrayList<>();
        for(Destination destination : destinations) {
            result.add(destination.getDestinationName());
        }

        return result;
    }

    public int getCost(String node) {
        if(this.getName().equals(node)) {
            return 1;
        }

        int result = 0;
        for(Destination destination : destinations) {
            if(node.equals(destination.getDestinationName())) {
                result = destination.getDistance();
            }
        }
        return result;
    }
}
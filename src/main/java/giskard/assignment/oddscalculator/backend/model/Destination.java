package giskard.assignment.oddscalculator.backend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents an adjacent node in the graph
 * @author Samuele Di Tullio
 */
@Slf4j
public class Destination {

    @Getter
    @Setter
    private int distance;

    @Getter
    @Setter
    private String destinationName;

    public Destination(String destinationName, int distance) {
        this.destinationName = destinationName;
        this.distance = distance;
    }
}
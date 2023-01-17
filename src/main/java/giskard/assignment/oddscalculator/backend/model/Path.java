package giskard.assignment.oddscalculator.backend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Class that represents a solution path to the destination
 * @author Samuele Di Tullio
 */
@Slf4j
public class Path {

    @Getter
    @Setter
    private List<Node> nodes;

    @Getter
    @Setter
    private int cost;
    public Path(List<Node> nodes, int cost) {
        this.nodes = nodes;
        this.cost = cost;
    }

    @Override
    public String toString() {
        String showNodes = "[ ";
        for(Node node : nodes) {

            showNodes += node.getName() + " ";
        }
        showNodes += "]";
        return showNodes +
                ", cost=" + cost;
    }

}
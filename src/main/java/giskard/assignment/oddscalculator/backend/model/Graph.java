package giskard.assignment.oddscalculator.backend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class Graph {

    @Getter
    @Setter
    private List<Node> nodes = new ArrayList<>();

    public void addNode(Node newNode) {
        int i = 0;
        boolean isNew = true;
        for(Node node : getNodes()) {
            if(node.getName().equals(newNode.getName())) {
                node.getDestinations().addAll(newNode.getDestinations());
                nodes.set(i, node);
                isNew = false;
            }
            i++;
        }

        if(isNew) {
            nodes.add(newNode);
        }
    }

    public Node getExistingNode (String name) {
        Node result = null;
        for(Node node : nodes) {
            if(node.getName().equals(name)) {
                result = node;
            }
        }

        return result;
    }

}
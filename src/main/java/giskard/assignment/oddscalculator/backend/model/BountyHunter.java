package giskard.assignment.oddscalculator.backend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class that represents an interception of one day in the plan of the Empire
 * @author Samuele Di Tullio
 */
@Slf4j
public class BountyHunter {

    @Getter
    @Setter
    private String planet;

    @Getter
    @Setter
    private int day;

}

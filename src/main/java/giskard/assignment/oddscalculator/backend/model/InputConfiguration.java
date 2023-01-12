package giskard.assignment.oddscalculator.backend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Class that represents an instance of the input JSON configuration file
 * @author Samuele Di Tullio
 */
@Slf4j
public class InputConfiguration {

    @Getter
    @Setter
    private int autonomy;

    @Getter
    @Setter
    private String departure;

    @Getter
    @Setter
    private String arrival;

    @Getter
    @Setter
    private String routes_db;

    @Override
    public String toString() {
        return "InputConfiguration{" +
                "autonomy=" + autonomy +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", routes_db='" + routes_db + '\'' +
                '}';
    }
}

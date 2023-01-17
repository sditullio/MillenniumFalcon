package giskard.assignment.oddscalculator.backend.controller;

import giskard.assignment.oddscalculator.backend.model.EmpireInterceptions;
import giskard.assignment.oddscalculator.backend.model.Path;
import giskard.assignment.oddscalculator.backend.utils.ConfigurationParser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller that receive the JSON file containing the plans of the Empire
 * @author Samuele Di Tullio
 */
@RestController
@RequestMapping("/c3po")
public class C3POController {

    @PostMapping("/intercept")
    @ResponseBody
    public @ResponseStatus(HttpStatus.OK) String handleInterceptions(@RequestBody EmpireInterceptions interceptions) {
        System.out.println("intercepting empire.json");

        Map<Path, Double> odds = ConfigurationParser.calculateOdds(interceptions);

        int minIndex = calculateMinOdd(odds);

        Path path = odds.keySet().stream().toList().get(minIndex);

        String showResult = "Path: " + path.toString() + " \n\nSuccess probability: " + odds.get(path);

        System.out.println(showResult);

        return showResult;
    }

    private int calculateMinOdd(Map<Path, Double> odds) {
        Double min = Double.valueOf(100);
        int i = 0;
        int minIndex = 0;
        for(Double odd : odds.values()) {
            if(odd < min) {
                minIndex = i;
                min = odd;
            }
            i++;
        }

        return minIndex;

    }
}

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

        int minIndex = calculateMaxOdd(odds);

        String showResult = "";
        if(!odds.isEmpty()) {
            Path path = odds.keySet().stream().toList().get(minIndex);
            showResult = "Path: " + path.toString() + " \n\nSuccess probability: " + odds.get(path);
        } else {
            showResult = "Success probability: 0";
        }
        System.out.println(showResult);

        return showResult;
    }

    private int calculateMaxOdd(Map<Path, Double> odds) {
        Double max = Double.valueOf(0);
        int i = 0;
        int maxIndex = 0;
        for(Double odd : odds.values()) {
            if(odd > max) {
                maxIndex = i;
                max = odd;
            }
            i++;
        }

        return maxIndex;

    }
}

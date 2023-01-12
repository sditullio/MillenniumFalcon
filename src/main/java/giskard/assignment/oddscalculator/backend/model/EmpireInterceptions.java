package giskard.assignment.oddscalculator.backend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class EmpireInterceptions {

    @Getter
    @Setter
    private int countdown;

    @Getter
    @Setter
    private List<BountyHunter> bounty_hunters;

    public EmpireInterceptions(int countdown, List<BountyHunter> bountyHunters) {
        this.countdown = countdown;
        this.bounty_hunters = bountyHunters;
    }
}

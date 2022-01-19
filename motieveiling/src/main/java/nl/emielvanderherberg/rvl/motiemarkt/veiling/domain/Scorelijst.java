package nl.emielvanderherberg.rvl.motiemarkt.veiling.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Scorelijst {
    private String fractie;
    private int lootjes;
    private int maxMoties;
    private List<String> inschrijvingen;
    private List<String> jokers;

    private List<Motie> gewensteMoties;
    private List<Motie> jokerMoties;

    public Scorelijst(final Map<String, Object> properties, final Map<String, Motie> alleMoties) {
        this.fractie = String.class.cast(properties.get("naam"));
        this.lootjes = Integer.class.cast(properties.get("lootjes"));
        this.maxMoties = Integer.class.cast(properties.get("max"));
        this.inschrijvingen = List.class.cast(properties.get("inschrijvingen"));
        this.jokers = List.class.cast(properties.get("jokers"));

        if (this.jokers == null) {
            this.jokers = new ArrayList<>();
        }

        initialiseerIngeschrevenMoties(alleMoties);
    }

    private void initialiseerIngeschrevenMoties(final Map<String, Motie> alleMoties) {
        this.gewensteMoties = this.inschrijvingen.stream().map(motieNr -> alleMoties.get(motieNr)).collect(Collectors.toList());
        this.jokerMoties = this.jokers.stream().map(motieNr -> alleMoties.get(motieNr)).collect(Collectors.toList());
    }
}

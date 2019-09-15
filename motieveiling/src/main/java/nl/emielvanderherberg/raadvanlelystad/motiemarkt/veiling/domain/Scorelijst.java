package nl.emielvanderherberg.raadvanlelystad.motiemarkt.veiling.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Data
public class Scorelijst {
    private String fractie;
    private int lootjes;
    private List<Integer> inschrijvingen;
    private List<Integer> jokers;

    private List<Motie> gewensteMoties;
    private List<Motie> jokerMoties;

    public Scorelijst(final Map<String, Object> properties, final Map<Integer, Motie> alleMoties) {
        this.fractie = String.class.cast(properties.get("naam"));
        this.lootjes = Integer.class.cast(properties.get("lootjes"));
        this.inschrijvingen = List.class.cast(properties.get("inschrijvingen"));
        this.jokers = List.class.cast(properties.get("jokers"));

        initialiseerIngeschrevenMoties(alleMoties);
    }

    private void initialiseerIngeschrevenMoties(final Map<Integer, Motie> alleMoties) {
        this.gewensteMoties = this.inschrijvingen.stream().map(motieNr -> alleMoties.get(motieNr)).collect(Collectors.toList());
        this.jokerMoties = this.jokers.stream().map(motieNr -> alleMoties.get(motieNr)).collect(Collectors.toList());
    }
}
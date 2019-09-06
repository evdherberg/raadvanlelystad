package nl.emielvanderherberg.raadvanlelystad.motiemarkt.veiling.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
@Data
public class Fractie {
    // Initiële gegevens
    private String naam;
    private int aantalLeden;
    private Collection<Motie> deelnames;
    private Collection<Motie> jokers;

    // Loting gegevens
    private int resterendeLoten;

    // Uitslag gegevens
    private Collection<Motie> gewonnenMoties = new ArrayList<>();

    public void winMotie(Motie motie) {
        gewonnenMoties.add(motie);
        resterendeLoten = resterendeLoten - 1;
        motie.setWinnaar(this);
    }
}

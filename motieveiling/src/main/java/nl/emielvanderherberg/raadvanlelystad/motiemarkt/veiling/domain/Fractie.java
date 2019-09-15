package nl.emielvanderherberg.raadvanlelystad.motiemarkt.veiling.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Fractie {
    // Initiële gegevens
    private String naam;
    private Scorelijst scorelijst;

    // Dynamische gegevens
    private int resterendeLoten;
    private List<Motie> gewonnenMoties = new ArrayList<>();

    public Fractie(Scorelijst scorelijst) {
        this.naam = scorelijst.getFractie();
        this.scorelijst = scorelijst;
        this.resterendeLoten = scorelijst.getLootjes();
    }

    public void winMotie(Motie motie) {
        gewonnenMoties.add(motie);
        if (resterendeLoten > 0) {
            resterendeLoten--;
        }
        motie.setWinnaar(this);
    }
}

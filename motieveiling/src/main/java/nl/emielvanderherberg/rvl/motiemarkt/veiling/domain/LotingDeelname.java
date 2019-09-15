package nl.emielvanderherberg.rvl.motiemarkt.veiling.domain;

import lombok.Data;

@Data
public class LotingDeelname implements Comparable<LotingDeelname> {
    // Dynamische gegevens
    private Fractie fractie;
    private LotingRonde lotingRonde;
    private int aantalJokerLoten = 0;
    private int aantalFractieLoten = 0;

    public Integer getAantalLoten() {
        // Geen deelname met jokerloten als de reguliere loten al op zijn
        return aantalFractieLoten + (aantalFractieLoten > 0 ? aantalJokerLoten : 0);
    }

    @Override
    public int compareTo(LotingDeelname o) {
        return -1 * getAantalLoten().compareTo(o.getAantalLoten());
    }
}

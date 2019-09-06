package nl.emielvanderherberg.raadvanlelystad.motiemarkt.veiling.domain;

import lombok.Data;

/**
 *
 */
@Data
public class LotingDeelname {
    // Loting gegevens
    private Fractie fractie;
    private LotingRonde lotingRonde;
    private int aantalJokerLoten = 0;

    // Uitslag gegevens
    private int aantalFractieLoten = 0;

    public int getAantalLoten() {
        return aantalFractieLoten + aantalJokerLoten;
    }
}

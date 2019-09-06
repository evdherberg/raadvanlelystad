package nl.emielvanderherberg.raadvanlelystad.motiemarkt.veiling.domain;

import lombok.Data;

/**
 *
 */
@Data
public class Motie {
    // Initiële gegevens
    private int nummer;
    private String titel;
    private String indiener;

    // Loting gegevens
    private LotingRonde lotingRonde;

    // Uitslag gegevens
    private Fractie winnaar;
}

package nl.emielvanderherberg.raadvanlelystad.motiemarkt.veiling.domain;

import lombok.Data;

import java.util.Map;

@Data
public class Motie {
    // Initiële gegevens
    private Integer nummer;
    private String titel;
    private String indiener;

    // Dynamische gegevens
    private LotingRonde lotingRonde;
    private Fractie winnaar;

    public Motie(Map<String, Object> properties) {
        this.nummer = Integer.class.cast(properties.get("nummer"));
        this.titel = String.class.cast(properties.get("titel"));
        this.indiener = String.class.cast(properties.get("indiener"));
    }

    @Override
    public String toString() {
        return "\"" + titel + "\" (nummer " + nummer + ")";
    }
}

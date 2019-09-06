package nl.emielvanderherberg.raadvanlelystad.motiemarkt.veiling.domain;

import lombok.Data;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 *
 */
@Data
public class LotingRonde {
    // Initiële gegevens
    private int rondeNr;
    private Motie motie;

    // Loting gegevens
    private List<LotingDeelname> deelnames;
    private List<Fractie> lootjes;

    // Uitslag gegevens
    private int getrokkenLootjeNr;
    private Fractie winnaar;

    public void doeLoting(PrintWriter out) {
        printStartInfo(out);
        vulPot(out);
        trekLootje(out);
    }

    private void printStartInfo(PrintWriter out) {
        out.println("===================================================");
        out.println("Loting ronde nummer " + rondeNr);
        out.println("Deze loting betreft de motie " + motie.toString());
        out.println("Deelnemers:");
        deelnames.forEach(deelname -> {
            out.println("- " + deelname.getFractie().getNaam() + " met " + deelname.getAantalFractieLoten() + " reguliere loten en " + deelname.getAantalJokerLoten() + " joker loten");
        });
        out.println("Het totaal aantal loten is " + getTotaalLoten());
    }

    private void vulPot(PrintWriter out) {
        deelnames.forEach(deelname -> {
            for (int i = 0; i < deelname.getAantalLoten() + deelname.getAantalJokerLoten(); i++) {
                lootjes.add(deelname.getFractie());
            }
        });
        out.println("---------------------------------------------------");
        out.println("De volgende lootjes zitten in de pot:");
        IntStream.range(0, lootjes.size() - 1).forEach(i -> {
            out.println(String.format("- %02d ", i + 1) + lootjes.get(i).getNaam());
        });
    }

    private void trekLootje(PrintWriter out) {
        out.println("---------------------------------------------------");
        out.println("De trekking vindt nu plaats");
        int getrokkenLootjeIndex = ThreadLocalRandom.current().nextInt(0, lootjes.size());
        this.getrokkenLootjeNr = getrokkenLootjeIndex + 1;
        out.println("Het getrokken lootje is nummer " + getrokkenLootjeNr);
        winnaar = lootjes.get(getrokkenLootjeIndex);
        out.println("Dus motie " + motie.getNummer() + " wordt toegewezen aan " + winnaar.getNaam());
        winnaar.winMotie(motie);
        out.println("Fractie " + winnaar.getNaam() + " heeft nu nog " + winnaar.getResterendeLoten() + " loten over");
    }

    private int getTotaalLoten() {
        return deelnames.stream().mapToInt(LotingDeelname::getAantalLoten).sum();
    }
}

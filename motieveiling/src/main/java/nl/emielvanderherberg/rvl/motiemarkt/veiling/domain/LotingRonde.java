package nl.emielvanderherberg.rvl.motiemarkt.veiling.domain;

import lombok.Data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class LotingRonde {
    // Initiële gegevens
    private int rondeNr;
    private Motie motie;
    private boolean waitForKey = false;

    // Dynamische gegevens
    private List<LotingDeelname> deelnames;
    private List<LotingDeelname> tijdelijkBuitenMededinging = new ArrayList<>();
    private List<Fractie> lootjes = new ArrayList<>(); // Voor ieder lootje een "fractie" element
    private int getrokkenLootjeNr;
    private Fractie winnaar;

    private String fractiesInDeelnames(List<LotingDeelname> d) {
        return d.stream().map(deelname -> deelname.getFractie().getNaam()).collect(Collectors.joining(" en "));
    }

    public void doeLoting(PrintStream out) {
        bepaalBuitenMededinging();
        Collections.sort(deelnames); // Sorteren op volgorde van aantal lootjes
        printStartInfo(out);
        if (getTotaalLoten() == 0) {
            if (!deelnames.isEmpty()) {
                out.println("Geen van de geïnteresseerde fracties heeft nog lootjes");
                out.println("Maar omdat er geen andere fracties deelnemen, komen " + fractiesInDeelnames(deelnames) + " alsnog in aanmerking");

                // Geef nu voorrang aan fracties die op deze motie een joker hebben ingezet
                List<LotingDeelname> deelnamesMetJokers = deelnames.stream().filter(deelname -> deelname.getAantalJokerLoten() > 0).collect(Collectors.toList());
                if (!deelnamesMetJokers.isEmpty()) {
                    winnaar = deelnamesMetJokers.get(ThreadLocalRandom.current().nextInt(0, deelnamesMetJokers.size())).getFractie();
                } else {
                    winnaar = deelnames.get(ThreadLocalRandom.current().nextInt(0, deelnames.size())).getFractie();
                }

                out.println("Uitslag random trekking ---> " + winnaar.getNaam());
                verwerkResultaat(out);
            } else {
                out.println("Helaas heeft geen enkele fractie interesse getoond voor deze motie");
            }
        } else {
            vulPot(out);
            trekLootje(out);
            verwerkResultaat(out);
        }
        out.println();
        out.println();
        out.println();
    }

    /**
     * "Om ervoor te zorgen dat iedere fractie een idee krijgt toegewezen, kent de eerste fase van de loting een extra
     * regel. Namelijk: een fractie die een loting wint wordt tijdelijk uitgesloten van de daaropvolgende lotingen. Deze
     * fase eindigt zodra alle fracties één idee toegewezen hebben gekregen. Op deze wijze krijgen alle fracties één
     * idee, voordat er tweede en volgende ideeën worden toegekend."
     */
    private void bepaalBuitenMededinging() {
        boolean deelnemersNogZonderOverwinningen = deelnames.stream().map(LotingDeelname::getFractie).anyMatch(fractie -> fractie.getGewonnenMoties().isEmpty());
        if (deelnemersNogZonderOverwinningen) {
            deelnames.stream().filter(deelname -> !deelname.getFractie().getGewonnenMoties().isEmpty()).forEach(deelname -> tijdelijkBuitenMededinging.add(deelname));
            deelnames.removeIf(deelname -> !deelname.getFractie().getGewonnenMoties().isEmpty());
        }
    }

    private void printStartInfo(PrintStream out) {
        String rondeNummerString = (rondeNr < 10 ? "0" : "") + String.valueOf(rondeNr);
        out.println("=========================================== Loting ronde " + rondeNummerString + " ==========================================");
        out.println("Deze ronde betreft " + motie.toString());
        if (!tijdelijkBuitenMededinging.isEmpty()) {
            out.println(fractiesInDeelnames(tijdelijkBuitenMededinging) + (tijdelijkBuitenMededinging.size() == 1 ? " neemt" : " nemen") + " niet aan deze loting deel omdat andere deelnemende fracties nog geen enkele motie hebben gekregen");
        }
        out.println("Deelnemers:");
        deelnames.forEach(deelname -> {
            out.println("- " + deelname.getFractie().getNaam() + " met " + deelname.getAantalLoten() + (deelname.getAantalLoten() == 1 ? " lot" : " loten") + (deelname.getAantalJokerLoten() > 0 ? " (inclusief jokers)" : ""));
        });

        Integer totaalLoten = getTotaalLoten();
        out.println("Het totaal aantal loten is " + totaalLoten);
    }

    private void vulPot(PrintStream out) {
        deelnames.forEach(deelname -> {
            for (int i = 0; i < deelname.getAantalLoten(); i++) {
                lootjes.add(deelname.getFractie());
            }
        });
        out.println("------------------------------------------------------------------------------------------------------");
        out.println("De volgende lootjes zitten in de pot:");
        IntStream.range(0, lootjes.size()).forEach(i -> {
            out.print(String.format(" [%02d] ", i + 1) + lootjes.get(i).getNaam());
        });
        out.println();
    }

    private void trekLootje(PrintStream out) {
        out.println("------------------------------------------------------------------------------------------------------");
        if (waitForKey) {
            out.print("De trekking vindt nu plaats");
            waitForEnter("... (druk ENTER om het lootje te trekken)");
        } else {
            out.println("De trekking vindt nu plaats");
        }
        int getrokkenLootjeIndex = ThreadLocalRandom.current().nextInt(0, lootjes.size());
        this.getrokkenLootjeNr = getrokkenLootjeIndex + 1;
        winnaar = lootjes.get(getrokkenLootjeIndex);
        out.println("Het getrokken lootje is nummer " + getrokkenLootjeNr + " ---> " + winnaar.getNaam());
    }

    private void verwerkResultaat(PrintStream out) {
        winnaar.winMotie(motie);
        out.println("(" + winnaar.getNaam() + " heeft nu nog " + winnaar.getResterendeLoten() + (winnaar.getResterendeLoten() == 1 ? " lot" : " loten") + " over)");
        if (waitForKey) {
            waitForEnter(null);
        }
    }

    private int getTotaalLoten() {
        return deelnames.stream().mapToInt(LotingDeelname::getAantalLoten).sum();
    }

    private void waitForEnter(String message) {
        if (message != null) {
            System.out.print(message);
        }
        new Scanner(System.in).nextLine();
    }
}

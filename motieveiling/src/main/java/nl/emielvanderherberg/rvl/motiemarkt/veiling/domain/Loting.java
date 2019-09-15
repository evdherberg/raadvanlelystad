package nl.emielvanderherberg.rvl.motiemarkt.veiling.domain;

import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class Loting {
    private Map<Integer, Motie> alleMoties;
    private Map<String, Scorelijst> alleScoreLijsten;
    private Map<String, Fractie> alleFracties;
    private List<Motie> motiesInVolgordeVanPopulariteit;

    private void leesAlleMotiesIn() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("moties.yaml");
        List<Map<String, Object>> motieRecords = yaml.load(inputStream);

        this.alleMoties = motieRecords.stream().map(propertyMap -> new Motie(propertyMap)).collect(Collectors.toMap(Motie::getNummer, Function.identity()));
    }

    private void leesAlleScoreLijstenIn() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("scorelijsten.yaml");
        List<Map<String, Object>> scorelijstRecords = yaml.load(inputStream);

        List<Scorelijst> scorelijsten = scorelijstRecords.stream().map(propertyMap -> new Scorelijst(propertyMap, this.alleMoties)).collect(Collectors.toList());

        this.alleScoreLijsten = scorelijsten.stream().collect(Collectors.toMap(Scorelijst::getFractie, Function.identity()));
        this.alleFracties = scorelijsten.stream().map(scorelijst -> new Fractie(scorelijst)).collect(Collectors.toMap(Fractie::getNaam, Function.identity()));
    }

    /**
     * "De loting vindt plaats per idee, waarbij het idee met de meeste ja’s en jokers als eerste wordt geloot."
     */
    private void bepaalLotingVolgorde() {
        motiesInVolgordeVanPopulariteit = new ArrayList<>();

        Map<Motie, Integer> belangstellingPerMotie = new HashMap<>();
        for (Motie motie : alleMoties.values()) {
            int belangstellingVoorDezeMotie = 0;
            for (Scorelijst scorelijst : alleScoreLijsten.values()) {
                if (scorelijst.getGewensteMoties().contains(motie)) {
                    belangstellingVoorDezeMotie++;
                    if (scorelijst.getJokerMoties().contains(motie)) {
                        belangstellingVoorDezeMotie++;
                    }
                }
            }
            belangstellingPerMotie.put(motie, belangstellingVoorDezeMotie);
        }

        motiesInVolgordeVanPopulariteit = belangstellingPerMotie
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(e -> e.getKey())
                .collect(Collectors.toList());

        System.out.println("De loting zal in de volgende volgorde plaatsvinden:");
        int rondeNr = 1;
        for (Motie m : motiesInVolgordeVanPopulariteit) {
            System.out.println("- Ronde " + rondeNr++ + ": " + m + " (" + belangstellingPerMotie.get(m) + " keer aangevinkt)");
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    private void voerLotingUit(boolean waitForKey) {
        Integer huidigeRonde = 1;

        for (Motie huidigeMotie : motiesInVolgordeVanPopulariteit) {
            LotingRonde lotingRonde = new LotingRonde();
            lotingRonde.setRondeNr(huidigeRonde);
            lotingRonde.setMotie(huidigeMotie);
            lotingRonde.setWaitForKey(waitForKey);

            List<LotingDeelname> alleDeelnamesAanDezeRonde = new ArrayList<>();
            for (Fractie fractie : alleFracties.values()) {
                if (fractie.getScorelijst().getGewensteMoties().contains(huidigeMotie)) {
                    // Deze fractie heeft deze motie aangevinkt
                    LotingDeelname deelname = new LotingDeelname();
                    deelname.setFractie(fractie);
                    deelname.setAantalFractieLoten(fractie.getResterendeLoten());
                    if (fractie.getScorelijst().getJokerMoties().contains(huidigeMotie)) {
                        deelname.setAantalJokerLoten(5);
                    }
                    deelname.setLotingRonde(lotingRonde);
                    alleDeelnamesAanDezeRonde.add(deelname);
                }
            }
            lotingRonde.setDeelnames(alleDeelnamesAanDezeRonde);

            lotingRonde.doeLoting(System.out);
            huidigeRonde++;
        }
    }

    private void schrijfEindUitslag() {
        System.out.println();
        System.out.println("======================================================================================================");
        System.out.println("============================================ EIND UITSLAG ============================================");
        System.out.println("======================================================================================================");

        List<Fractie> fractiesInVolgordeVanOmvang = alleFracties
                .values()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(fractie -> fractie.getScorelijst().getLootjes())))
                .collect(Collectors.toList());

        for (Fractie fractie : fractiesInVolgordeVanOmvang) {
            System.out.println();
            System.out.println(fractie.getNaam() + " gaat de volgende " + (fractie.getGewonnenMoties().size() == 1 ? "motie" : "moties") + " begeleiden en uitwerken:");
            for (Motie motie : fractie.getGewonnenMoties()) {
                System.out.println("- " + motie.toString());
            }
        }

        List<Motie> overblijvers = alleMoties.values().stream().filter(motie -> motie.getWinnaar() == null).collect(Collectors.toList());
        if (!overblijvers.isEmpty()) {
            System.out.println();
            System.out.println();
            System.out.println("Voor de volgende moties heeft zich helaas geen enkele fractie gemeld:");
            for (Motie motie : overblijvers) {
                System.out.println("- " + motie.toString());
            }
            System.out.println("Deze moties worden door de voorzitter van het presidium verder afgehandeld");
        }

    }

    public static void main(String[] args) {
        Loting loting = new Loting();
        loting.leesAlleMotiesIn();
        loting.leesAlleScoreLijstenIn();
        loting.bepaalLotingVolgorde();
        loting.voerLotingUit(false);

        loting.schrijfEindUitslag();
    }

}

package keyboardcrusader.locations.generators;

import keyboardcrusader.locations.api.NameGenerator;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class UndergroundVillageNameGenerator extends NameGenerator {
    private List<String> name1 = Arrays.asList("B", "D", "Dh", "Bh", "G", "H", "K", "Kh", "M", "N", "Th", "V");
    private List<String> name2 = Arrays.asList("ag", "agh", "al", "am", "an", "ar", "arn", "eg", "egh", "el", "em", "en", "er", "ern", "ig", "igh", "il", "im", "in", "ir", "irn", "og", "ogh", "ol", "om", "on", "or", "orn", "ug", "ugh", "ul", "um", "un", "ur", "urn");
    private List<String> name3 = Arrays.asList(" Badihr", " Badir", " Baduhr", " Badur", " Boldahr", " Boldar", " Boldihr", " Boldir", " Boldohr", " Boldor", " Boram", " Boramm", " Borim", " Borimm", " Buldahr", " Buldar", " Buldihr", " Buldohr", " Buldor", " Burim", " Burimm", " Darahl", " Daral", " Darihm", " Darim", " Darohm", " Darom", " Daruhl", " Daruhm", " Darul", " Darum", " Dorahl", " Doral", " Doruhl", " Dorul", " Durahl", " Dural", " Faldihr", " Faldir", " Falduhr", " Faldur", " Faruhm", " Farum", " Furuhm", " Furum", " Garohm", " Garom", " Garuhm", " Garum", " Gurihm", " Guruhm", " Gurum", " Kahldur", " Kalduhr", " Kohldur", " Kolduhr", " Kuldihr", " Kuldir", " Kuldohr", " Kuldor", " Laduhr", " Ladur", " Lodahr", " Lodar", " Lodihr", " Lodir", " Loduhr", " Lodur", " Maldir", " Malduhr", " Maldur", " Moldir", " Molduhr", " Moldur", " Olihm", " Oluhm", " Tarihr", " Taruhm", " Taruhr", " Tarum", " Tharim", " Tharum", " Thoram", " Thorim", " Thorum", " Thurim", " Thurum", " Todihr", " Todir", " Toduhr", " Todur", " Toruhm", " Torum", " Turuhm", " Turum", " Ulihm", " Uluhm", " Ulum", " Wahrum", " Wohrum", " Wuhrum", "ahm", "alduhr", "aldur", "am", "aruhm", "arum", "badihr", "badir", "baduhr", "badur", "bihr", "bohr", "boldahr", "boldar", "boldihr", "boldir", "boldohr", "boldor", "bor", "boram", "boramm", "borim", "borimm", "buhr", "buldahr", "buldar", "buldihr", "buldohr", "buldor", "bur", "burim", "burimm", "dahn", "dan", "darahl", "daral", "darihm", "darim", "darohm", "darom", "darth", "daruhl", "daruhm", "darul", "darum", "dihm", "dihr", "dim", "dirth", "dohr", "dor", "dorahl", "doral", "dorth", "doruhl", "dorul", "duahr", "duar", "duhm", "duhn", "duhr", "dum", "dun", "dur", "durahl", "dural", "eduhr", "edur", "elduhr", "eldur", "eruhm", "erum", "faldihr", "faldir", "falduhr", "faldur", "faruhm", "farum", "fuhn", "furuhm", "furum", "galir", "galor", "gan", "gari", "garohm", "garom", "garuhm", "garum", "golar", "golir", "gon", "gran", "grim", "grin", "grom", "gron", "grum", "grun", "gulir", "gulor", "gurihm", "guruhm", "gurum", "heim", "kahldur", "kahm", "kalduhr", "kihm", "kohldur", "kohm", "kolduhr", "kuhm", "kuldihr", "kuldir", "kuldohr", "kuldor", "laduhr", "ladur", "lodahr", "lodar", "lodihr", "lodir", "loduhr", "lodur", "olduhr", "oldur", "olihm", "oluhm", "oluhr", "olur", "ragh", "rahm", "ram", "rhia", "ria", "righ", "rihm", "rim", "rogh", "rugh", "ruhm", "rum", "tarihr", "taruhm", "taruhr", "tarum", "thiad", "thiod", "tihrm", "tirm", "todihr", "todir", "toduhr", "todur", "torhm", "torm", "toruhm", "torum", "tuhrm", "turm", "turuhm", "turum", "uhm", "ulihm", "ulihr", "ulir", "uluhm", "uluhr", "ulum", "ulur", "um", "wahr", "wahrum", "wihr", "wohr", "wohrum", "wuhr", "wuhrum", "yahr", "yar", "yaruhm", "yuhr", "yur");

    public UndergroundVillageNameGenerator(String name) {
        super(name);
    }

    @Override
    public String generateName(ResourceLocation registryName, float temperature) {
        String rnd1 = name1.get((int) (Math.random() * name1.size()));
        String rnd2 = name2.get((int) (Math.random() * name2.size()));
        String rnd3 = name3.get((int) (Math.random() * name3.size()));

        return rnd1 + rnd2 + rnd3;
    }
}

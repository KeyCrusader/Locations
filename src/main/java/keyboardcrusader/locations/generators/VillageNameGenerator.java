package keyboardcrusader.locations.generators;

import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class VillageNameGenerator extends BaseNameGenerator {
    private List<String> hot1 = Arrays.asList("Am", "An", "Andu", "Bai", "Ba", "Balo", "Ben", "Bi", "Biba", "Bim", "Biu", "Bun", "Ca", "Caba", "Cabam", "Cabin", "Cabo", "Cacon", "Cacu", "Cafun", "Caha", "Caien", "Caim", "Calan", "Calen", "Calon", "Calu", "Cama", "Cam", "Cambon", "Cambun", "Camis", "Can", "Cangam", "Cangan", "Cangum", "Cape", "Capen", "Cas", "Cassan", "Casson", "Ca", "Cata", "Cat", "Catum", "Caun", "Caxi", "Caza", "Cazom", "Ce", "Chian", "Chi", "Chiban", "Chibem", "Chica", "Chin", "Chipin", "Chis", "Chitem", "Coem", "Co", "Con", "Cou", "Couta", "Cuan", "Cua", "Cu", "Cui", "Cuim", "Cuve", "Da", "Dam", "Didi", "Di", "Don", "Dun", "Ekun", "Fol", "Folga", "Fun", "Ga", "Gabe", "Gan", "Golun", "Gu", "Huam", "Hum", "Jam", "Kui", "Lo", "Lobi", "Lom", "Lon", "Longon", "Lua", "Luaca", "Luan", "Luban", "Luca", "Lucus", "Lue", "Luia", "Luim", "Lum", "Lu", "Lumba", "Lume", "Lure", "Luxi", "Lan", "Malen", "Malem", "Ma", "Malu", "Marim", "Ma", "Mas", "Mata", "Mavin", "Mban", "Menon", "Mu", "Mhcin", "Mucon", "Mucum", "Mucu", "Mugin", "Mulon", "Mun", "Mus", "Mussen", "Na", "Nama", "Nami", "Nega", "Nha", "No", "Nza", "On", "Ondji", "Pin", "Que", "Qui", "Quiba", "Quilen", "Quima", "Quiban", "Quiri", "Quima", "Quim", "Quir", "Sa", "Saco", "Samu", "Sauri", "Sava", "Savun", "Son", "So", "Sum", "Tchin", "Tchi", "Tchipe", "Techa", "Tenta", "Tom", "Tum", "Uku", "Via", "Wa", "Xangon", "Xan");
    private List<String> hot2 = Arrays.asList("ba", "bal", "bala", "bale", "bamba", "bambe", "bambo", "banda", "bango", "batela", "baxe", "be", "bela", "bele", "bemba", "bia", "binda", "bito", "bo", "bola", "boledo", "bongue", "briz", "bua", "bundi", "cala", "canha", "cano", "capa", "chi", "chinda", "chiungo", "cinga", "colo", "comar", "combo", "conda", "culama", "cumbo", "cunda", "cupa", "cuso", "cusse", "cusso", "da", "dala", "dimbo", "do", "dulo", "funfo", "ga", "gage", "gamba", "gandala", "gar", "gares", "gi", "ginga", "go", "gongo", "gonjo", "gue", "guela", "guengo", "gufo", "guide", "gula", "gumbe", "hango", "heiro", "je", "jenje", "jimbe", "jiva", "kuma", "la", "lai", "lama", "lanje", "ledo", "lembo", "lengues", "lo", "lombo", "londo", "longo", "ludi", "lui", "lulo", "lundo", "lungo", "ma", "mavongo", "meje", "mibe", "munona", "mutete", "na", "nongue", "pata", "penda", "pindo", "pulo", "pungo", "quembe", "qui", "rea", "remo", "ri", "rima", "rimba", "rimo", "riz", "sa", "samba", "sango", "sende", "serra", "sombo", "songue", "suco", "sueje", "ta", "tada", "tado", "tala", "tativa", "teba", "tela", "tembo", "tiva", "to", "vate", "vinga", "vongo", "vungo", "xilo", "xita", "xito", "zaje", "zombo");
    private List<String> temp1 = Arrays.asList("Amber", "Angel", "Spirit", "Basin", "Lagoon", "Basin", "Arrow", "Autumn", "Bare", "Bay", "Beach", "Bear", "Bell", "Black", "Bleak", "Blind", "Bone", "Boulder", "Bridge", "Brine", "Brittle", "Bronze", "Castle", "Cave", "Chill", "Clay", "Clear", "Cliff", "Cloud", "Cold", "Crag", "Crow", "Crystal", "Curse", "Dark", "Dawn", "Dead", "Deep", "Deer", "Demon", "Dew", "Dim", "Dire", "Dirt", "Dog", "Dragon", "Dry", "Dusk", "Dust", "Eagle", "Earth", "East", "Ebon", "Edge", "Elder", "Ember", "Ever", "Fair", "Fall", "False", "Far", "Fay", "Fear", "Flame", "Flat", "Frey", "Frost", "Ghost", "Glimmer", "Gloom", "Gold", "Grass", "Gray", "Green", "Grim", "Grime", "Hazel", "Heart", "High", "Hollow", "Honey", "Hound", "Ice", "Iron", "Kil", "Knight", "Lake", "Last", "Light", "Lime", "Little", "Lost", "Mad", "Mage", "Maple", "Mid", "Might", "Mill", "Mist", "Moon", "Moss", "Mud", "Mute", "Myth", "Never", "New", "Night", "North", "Oaken", "Ocean", "Old", "Ox", "Pearl", "Pine", "Pond", "Pure", "Quick", "Rage", "Raven", "Red", "Rime", "River", "Rock", "Rogue", "Rose", "Rust", "Salt", "Sand", "Scorch", "Shade", "Shadow", "Shimmer", "Shroud", "Silent", "Silk", "Silver", "Sleek", "Sleet", "Sly", "Small", "Smooth", "Snake", "Snow", "South", "Spring", "Stag", "Star", "Steam", "Steel", "Steep", "Still", "Stone", "Storm", "Summer", "Sun", "Swamp", "Swan", "Swift", "Thorn", "Timber", "Trade", "West", "Whale", "Whit", "White", "Wild", "Wilde", "Wind", "Winter", "Wolf");
    private List<String> temp2 = Arrays.asList("acre", "band", "barrow", "bay", "bell", "born", "borough", "bourne", "breach", "break", "brook", "burgh", "burn", "bury", "cairn", "call", "chill", "cliff", "coast", "crest", "cross", "dale", "denn", "drift", "fair", "fall", "falls", "fell", "field", "ford", "forest", "fort", "front", "frost", "garde", "gate", "glen", "grasp", "grave", "grove", "guard", "gulch", "gulf", "hall", "hallow", "ham", "hand", "harbor", "haven", "helm", "hill", "hold", "holde", "hollow", "horn", "host", "keep", "land", "light", "maw", "meadow", "mere", "mire", "mond", "moor", "more", "mount", "mouth", "pass", "peak", "point", "pond", "port", "post", "reach", "rest", "rock", "run", "scar", "shade", "shear", "shell", "shield", "shore", "shire", "side", "spell", "spire", "stall", "wich", "minster", "star", "storm", "strand", "summit", "tide", "town", "vale", "valley", "vault", "vein", "view", "ville", "wall", "wallow", "ward", "watch", "water", "well", "wharf", "wick", "wind", "wood", "yard");
    private List<String> cold1 = Arrays.asList("Aval", "Bare", "Bleak", "Bliz", "Chill", "Clear", "Cloud", "Cold", "Cristal", "Crystal", "Dark", "Drift", "Frost", "Gliss", "Ice", "Moon", "North", "Shiver", "Sleet", "Snow", "Storm", "That", "Thaw", "Therm", "Whit", "White", "Wild", "Wind", "Winter", "Wit", "Wolf", "Yce");
    private List<String> cold2 = Arrays.asList("band", "barrow", "bell", "born", "borough", "bourne", "breach", "break", "chill", "cliff", "crest", "dale", "denn", "drift", "fall", "fell", "field", "ford", "fort", "frost", "gard", "garde", "glen", "grasp", "grave", "guard", "hallow", "ham", "hand", "helm", "hill", "hold", "holde", "hollow", "horn", "host", "keep", "maw", "mire", "mond", "moor", "more", "pass", "peak", "point", "port", "reach", "rest", "scar", "shield", "spell", "spire", "storm", "strand", "tide", "vale", "vault", "vein", "ville", "wall", "wallow", "ward", "watch", "wich");

    public VillageNameGenerator(ResourceLocation id) {
        super(id);
    }

    @Override
    public String generateName(ResourceLocation registryName, float temperature) {
        List<? extends String> name1;
        List<? extends String> name2;
        if (temperature <= 0.5F) {
            name1 = cold1;
            name2 = cold2;
        }
        else if (temperature > 1.0F) {
            name1 = hot1;
            name2 = hot2;
        }
        else {
            name1 = temp1;
            name2 = temp2;
        }
        String rnd1 = name1.get((int) (Math.random() * name1.size()));
        String rnd2 = name2.get((int) (Math.random() * name2.size()));

        return rnd1 + rnd2;
    }
}

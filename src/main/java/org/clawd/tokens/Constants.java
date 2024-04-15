package org.clawd.tokens;

public final class Constants {
    public static final String LOGGER_NAME = "DISCLAWD BOT";
    public static final String SQL_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String PROPERTIES_FILE_NAME = "bot.properties";

    /*
     * JSON String constants
     */

    public static final String ITEMS_JSON_FILEPATH = "src/main/resources/jsonfiles/items.json";
    public static final String ITEMS_JSON_ITEMS = "items";
    public static final String MOBS_JSON_FILEPATH = "src/main/resources/jsonfiles/mobs.json";
    public static final String MOBS_JSON_MOBS = "mobs";

    /*
     * THRESHOLDS for item params
     */

    public static final double ITEM_DROP_CHANCE_LOWER_B = 0;
    public static final double ITEM_DROP_CHANCE_UPPER_B = 1;
    public static final double XP_MULTIPLIER_LOWER_B = 1;
    public static final double GOLD_MULTIPLIER_LOWER_B = 0;
    public static final double DMG_MULTIPLIER_LOWER_B = 0;

    /*
     * THRESHOLDS for mob params
     */

    public static final double MOB_SPAWN_CHANCE_LOWER_B = 0;
    public static final double MOB_SPAWN_CHANCE_UPPER_B = 1;
    public static final double XP_DROP_AMOUNT_LOWER_B = 1;
    public static final double GOLD_DROP_AMOUNT_LOWER_B = 1;

    /*
     * Biom HP
     */

    public static final double BIOM_COAL_HP = 50.0;
    public static final double BIOM_IRON_HP = 100.0;
    public static final double BIOM_DIAMOND_HP = 150.0;

    /*
     * Ore image path's
     */

    public static final String BIOME_COAL_IMG_PATH = "src/main/resources/images/biome/coal.png";
    public static final String BIOME_IRON_IMG_PATH = "src/main/resources/images/biome/iron.png";
    public static final String BIOME_DIAMOND_IMG_PATH = "src/main/resources/images/biome/diamond.png";

    /*
     * Command Id's
     */

    public static final String BIOME_COMMAND_ID = "biome";

    /*
     * Button Id's
     */

    public static final String MINE_BUTTON_ID = "mine";
}

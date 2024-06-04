package org.clawd.parser;

import org.clawd.data.biomes.Biome;
import org.clawd.data.enums.BiomeType;
import org.clawd.data.mobs.enums.MobSubType;
import org.clawd.main.Main;
import org.clawd.parser.exceptions.FailedDataParseException;
import org.clawd.tokens.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiomeParser {

    /**
     * Wrapper function to parse the Biomes out of a JSON file
     *
     * @return a list containing all items
     * @throws FailedDataParseException when parsing fails, either to invalid items or
     *                                  empty item list
     */
    public List<Biome> parseBiomes() throws FailedDataParseException {
        List<Biome> biomes = getBiomesFromJSON();

        boolean isBiomeListValid = validateBiomes(biomes);
        // Storing if biome list is empty is for logging purposes
        boolean isBiomeListEmpty = biomes.isEmpty();

        if (!isBiomeListValid || isBiomeListEmpty)
            throw new FailedDataParseException(
                    "Could not parse biomes correctly:\n" +
                            "- valid biomes = " + isBiomeListValid + "\n" +
                            "- is biome list empty = " + isBiomeListEmpty);

        Main.LOG.info("Biome parsing finished, biome list size: " + biomes.size());
        return biomes;
    }

    /**
     * The actual JSON parser itself for the biome JSON
     *
     * @return the list of parsed biomes
     */
    private List<Biome> getBiomesFromJSON() {
        List<Biome> biomeList = new ArrayList<>();

        try (FileReader fileReader = new FileReader(Constants.BIOMES_JSON_FILEPATH)) {

            JSONObject obj = new JSONObject(new JSONTokener(fileReader));
            JSONArray arr = obj.getJSONArray(Constants.BIOMES_JSON_BIOMES);

            for (Object o : arr) {

                JSONObject jsonItem = (JSONObject) o;

                BiomeType biomeType = BiomeType.valueOf(jsonItem.getString("type"));
                double biomeHP = jsonItem.getDouble("hp");
                String imgPath = jsonItem.getString("imgPath");
                boolean xpEnabled = jsonItem.getBoolean("xpEnabled");
                JSONArray jsonArray = jsonItem.getJSONArray("mobs");
                Set<MobSubType> spawnableMobs = new HashSet<>();

                for (Object object : jsonArray) {
                    spawnableMobs.add(MobSubType.valueOf((String) object));
                }

                biomeList.add(new Biome(biomeType, biomeHP, imgPath, xpEnabled, spawnableMobs));
            }
        } catch (JSONException | IOException ex) {
            Main.LOG.severe("Failed to parse JSON biomes file: " + ex.getMessage());
        }

        return biomeList;
    }

    /**
     * Validates all biomes for some restrictions make help of the
     * isValidBiome() method
     *
     * @param biomes The list of Biomes that needs to be validated
     * @return True or false, depending on validation
     */
    private boolean validateBiomes(List<Biome> biomes) {
        for (Biome biome : biomes) {
            if (!isValidBiome(biome))
                return false;
        }
        Main.LOG.info("Biome validation finished");
        return true;
    }

    /**
     * Checks the validity of exactly one biome
     *
     * @param biome The biome to be checked
     * @return True of false, depending on the biomes validity
     */
    private boolean isValidBiome(Biome biome) {
        return !biome.spawnableMobs().isEmpty() && !(biome.biomeHP() <= 0);
    }
}

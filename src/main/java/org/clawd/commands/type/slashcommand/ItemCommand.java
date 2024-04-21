package org.clawd.commands.type.slashcommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.clawd.data.items.Item;
import org.clawd.data.items.UtilItem;
import org.clawd.data.items.WeaponItem;
import org.clawd.data.items.enums.ItemType;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ItemCommand implements SlashCommand{
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {

        String searchTerm = Objects.requireNonNull(event.getOption(Constants.ITEM_COMMAND_OPTION_ID)).getAsString();

        List<Item> items = Main.mineworld.getItemList()
                .stream()
                .filter(i -> i.getDropChance() == 0)
                .toList();

        Item foundItem = null;

        for (Item item : items) {
            if (item.getName().replace(" ", "").equalsIgnoreCase(searchTerm.replace(" ", "")))
                foundItem = item;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(":mag: Item Info");
        embedBuilder.setColor(Color.ORANGE);

        if (foundItem == null) {
            embedBuilder.setDescription("Item **" + searchTerm + "** was not found, maybe you mistyped something :/");
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        } else {
            embedBuilder.setColor(Color.ORANGE);
            String itemDesc = foundItem.getDescription();
            int itemPrice = foundItem.getPrice();
            double itemXPMult = foundItem.getXpMultiplier();
            String alternativeTxt;
            double alternativePerk;

            if (foundItem.getItemType() == ItemType.UTILITY) {
                UtilItem utilItem = (UtilItem) foundItem;
                alternativeTxt = ":black_small_square: Gold boost: ";
                alternativePerk = utilItem.getGoldMultiplier();
            } else {
                WeaponItem weaponItem = (WeaponItem) foundItem;
                alternativeTxt = ":black_small_square: Damage boost: ";
                alternativePerk = weaponItem.getDmgMultiplier();
            }
            embedBuilder.addField(
                    itemDesc,
                    ":black_small_square: XP boost: " + itemXPMult + "\n"
                            + alternativeTxt + alternativePerk + "\n"
                            + ":black_small_square: Price: " + itemPrice + " Coins" + "\n"
                            + ":black_small_square: Required lvl. " + foundItem.getReqLvl()
                    ,
                    false);
            event.replyEmbeds(embedBuilder.build())
                    .addActionRow(
                            Button.success(Constants.BUY_BUTTON_ID, "Buy").asDisabled() //TODO change to enabled
                    )
                    .setEphemeral(true)
                    .queue();
        }
    }
}
package org.clawd.commands.type.slashcommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.clawd.data.inventory.UserStats;
import org.clawd.data.items.Item;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.awt.*;
import java.io.File;
import java.util.Objects;

public class ItemCommand implements SlashCommand {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        String searchTerm = getSearchTerm(event);

        Item foundItem = Main.mineworld.getItemByName(searchTerm);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE);

        if (foundItem == null) {
            embedBuilder.setDescription("Item **" + searchTerm + "** was not found, maybe you mistyped something :/");
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        } else {
            String userID = event.getUser().getId();
            UserStats userStats = Main.sqlHandler.sqlStatsHandler.getUserStats(userID);

            int userLvl = Main.generator.computeLevel(userStats.getXpCount());
            int userGold = userStats.getGoldCount();

            Button buyButton = createBuyButton(foundItem, userLvl, userGold, userID);
            Button equipButton = createEquipButton(foundItem, userID);

            File imgFile = new File(foundItem.getImgPath());
            event.replyEmbeds(foundItem.createInspectEmbed(userStats, buyButton, equipButton).build())
                    .addFiles(FileUpload.fromData(imgFile, "item.png"))
                    .addActionRow(buyButton, equipButton)
                    .setEphemeral(true)
                    .queue();

            Main.LOG.info("Executed '" + Constants.ITEM_COMMAND_ID + "' command");
        }
    }

    private String getSearchTerm(SlashCommandInteractionEvent event) {
        return Objects.requireNonNull(event.getOption(Constants.ITEM_COMMAND_OPTION_ID)).getAsString();
    }

    private Button createBuyButton(Item foundItem, int userLvl, int userGold, String userID) {
        Button buyButton = Button.success(Constants.BUY_BUTTON_ID + foundItem.getID(), "Buy");
        boolean isItemInUserInv = Main.sqlHandler.sqlInventoryHandler.isItemInUserInventory(userID, foundItem.getID());
        if (isItemInUserInv || userLvl < foundItem.getReqLvl() || userGold < foundItem.getPrice()) {
            buyButton = buyButton.asDisabled();
        }
        return buyButton;
    }

    private Button createEquipButton(Item foundItem, String userID) {
        Button equipButton = Button.success(Constants.EQUIP_BUTTON_ID + foundItem.getID(), "Equip");
        boolean isItemInUserInv = Main.sqlHandler.sqlInventoryHandler.isItemInUserInventory(userID, foundItem.getID());
        if (!isItemInUserInv) {
            equipButton = equipButton.asDisabled();
        }
        return equipButton;
    }
}

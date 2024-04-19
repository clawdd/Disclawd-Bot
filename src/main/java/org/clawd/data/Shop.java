package org.clawd.data;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.clawd.data.items.Item;
import org.clawd.data.items.UtilItem;
import org.clawd.data.items.WeaponItem;
import org.clawd.data.items.enums.ItemType;
import org.clawd.tokens.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

//TODO comment the code
public class Shop {
    private final List<Item> itemList;
    private final List<WeaponItem> weaponItemList;
    private final List<UtilItem> utilItemList;
    private final List<EmbedBuilder> pages;
    private final int shopPagesCount;

    public Shop(List<Item> itemList) {
        this.itemList = itemList;
        this.weaponItemList = populateWeaponList();
        this.utilItemList = populateUtilList();
        this.shopPagesCount = (int) Math.ceil((double) itemList.size() / 6);
        this.pages = createPages();
    }

    private List<EmbedBuilder> createPages() {
        List<EmbedBuilder> embedBuilders = new ArrayList<>();

        for (int i = 0; i < this.shopPagesCount; i++) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Shop");
            embedBuilder.setColor(Color.ORANGE);
            embedBuilder.setFooter("Page: " + (i + 1) + "/" + shopPagesCount);

            int startIndex = i * 6;
            int endIndex = Math.min(startIndex + 6, this.itemList.size());

            for (int j = startIndex; j < endIndex; j++) {
                Item item = itemList.get(j);

                String itemName = item.getName();
                String itemDesc = item.getDescription();
                double itemXPMult = item.getXpMultiplier();

                String icon;
                String alternativeTxt;
                double alternativePerk;

                if (item.getItemType() == ItemType.UTILITY) {
                    UtilItem utilItem = (UtilItem) item;
                    icon = " :crystal_ball:";
                    alternativeTxt = "Gold boost: ";
                    alternativePerk = utilItem.getGoldMultiplier();
                } else {
                    WeaponItem weaponItem = (WeaponItem) item;
                    icon = " :dagger:";
                    alternativeTxt = "Damage boost: ";
                    alternativePerk = weaponItem.getDmgMultiplier();
                }

                embedBuilder.addField(
                        itemName + icon + item.getUniqueID(),
                        itemDesc + "\n" +
                                "XP boost: " + itemXPMult + "\n" + alternativeTxt + alternativePerk,
                        true);
            }
            embedBuilders.add(embedBuilder);
        }
        return embedBuilders;
    }

    /**
     * Creates the core page for the shop
     *
     * @param event The event
     */
    public void replyWithShopCoreEmbedded(SlashCommandInteractionEvent event) {

        EmbedBuilder embedBuilder = pages.getFirst();

        event.replyEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.secondary(Constants.BACK_BUTTON_ID, Emoji.fromUnicode("U+25C0")).asDisabled(),
                        Button.secondary(Constants.CLOSE_BUTTON_ID, Emoji.fromUnicode("U+274C")).asDisabled(),
                        Button.secondary(Constants.NEXT_BUTTON_ID, Emoji.fromUnicode("U+25B6"))
                )
                .setEphemeral(true)
                .queue();

    }

    public void replyToNextShopPage(ButtonInteractionEvent event, boolean back) {
        String footer = Objects.requireNonNull(event.getMessage().getEmbeds().getFirst().getFooter()).getText();
        String[] parts = footer.split("/");
        int currentPage = Integer.parseInt(parts[0].substring(6).strip());

        if (back) {
            currentPage--;
        } else {
            currentPage++;
        }

        event.getMessage().getContentStripped();
        currentPage = Math.max(1, Math.min(currentPage, this.shopPagesCount)) - 1;

        EmbedBuilder embedBuilder = pages.get(currentPage);

        Button nextButton = Button.secondary(Constants.NEXT_BUTTON_ID, Emoji.fromUnicode("U+25B6"));
        Button closeButton = Button.secondary(Constants.CLOSE_BUTTON_ID, Emoji.fromUnicode("U+274C"));
        Button backButton = Button.secondary(Constants.BACK_BUTTON_ID, Emoji.fromUnicode("U+25C0"));

        if (currentPage > 0 && currentPage < this.shopPagesCount - 1) {

            nextButton = nextButton.asEnabled();
            closeButton = closeButton.asDisabled();
            backButton = backButton.asEnabled();

            InteractionHook hook = event.editMessageEmbeds(embedBuilder.build()).complete();
            hook.editOriginalComponents(ActionRow.of(backButton,closeButton, nextButton)).queue();
        } else if (currentPage == 0) {

            nextButton = nextButton.asEnabled();
            closeButton = closeButton.asDisabled();
            backButton = backButton.asDisabled();

            InteractionHook hook = event.editMessageEmbeds(embedBuilder.build()).complete();
            hook.editOriginalComponents(ActionRow.of(backButton,closeButton, nextButton)).queue();
        } else if (currentPage == this.shopPagesCount - 1) {

            nextButton = nextButton.asDisabled();
            closeButton = closeButton.asDisabled();
            backButton = backButton.asEnabled();

            InteractionHook hook = event.editMessageEmbeds(embedBuilder.build()).complete();
            hook.editOriginalComponents(ActionRow.of(backButton,closeButton, nextButton)).queue();
        }
    }

    // might be unnecessary
    private List<WeaponItem> populateWeaponList() {
        List<WeaponItem> weaponItems = new ArrayList<>();
        for (Item item : this.itemList) {
            if (item.getItemType() == ItemType.WEAPON)
                weaponItems.add((WeaponItem) item);
        }
        return weaponItems;
    }

    private List<UtilItem> populateUtilList() {
        List<UtilItem> utilItems = new ArrayList<>();
        for (Item item : this.itemList) {
            if (item.getItemType() == ItemType.UTILITY)
                utilItems.add((UtilItem) item);
        }
        return utilItems;
    }
}
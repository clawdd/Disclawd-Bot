package org.clawd.commands.type.slashcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.clawd.main.Main;

public class BiomeCommand implements SlashCommand {
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        Main.mineworld.replyWithBiomeEmbedded(event);
        Main.logger.info("Executed '/biome' command");
    }
}
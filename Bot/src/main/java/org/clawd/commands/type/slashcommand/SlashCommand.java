package org.clawd.commands.type.slashcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommand {

    /**
     * Function get overridden by concrete slash command
     *
     * @param event The event to be handled
     */
    public void executeCommand(SlashCommandInteractionEvent event);
}
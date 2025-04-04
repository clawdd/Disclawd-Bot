package org.clawd.commands.type.slashcommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.awt.*;
import java.io.File;

public class HelpCommand implements SlashCommand{
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Helpcenter");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setThumbnail("attachment://logo.png");

        embedBuilder.setDescription(Constants.HELP_COMMAND_DESC);
        embedBuilder.addBlankField(false);
        embedBuilder.addField("Disclawd", Constants.HELP_WHAT_IS, false);
        embedBuilder.addField("", Constants.HELP_END_TEXT, false);

        embedBuilder.setFooter("PS: The title is a link :D");
        embedBuilder.setUrl("https://krgermax.com/");

        File imgFile = new File(Constants.PATH_LOGO_IMG);

        event.replyEmbeds(embedBuilder.build())
                .addFiles(FileUpload.fromData(imgFile, "logo.png"))
                .setEphemeral(true)
                .complete();

        Main.LOG.info("Executed '"+ Constants.HELP_COMMAND_ID +"' command");
    }
}

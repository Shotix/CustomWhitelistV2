package org.sh0tix.customwhitelistv2.handlers;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;

public class SendMessageToPlayer {
    
    private static class MessagePart {
        String messagePart;
        String pluginName = "CustomWhitelistV2";
        Color color;
        
        MessagePart(String messagePart, Color color) {
            this.messagePart = messagePart;
            this.color = color;
        }
    }
    
    private List<MessagePart> messageParts;
    
    /**
     * Create a new instance of SendMessageToPlayer
     */
    public SendMessageToPlayer() {
        this.messageParts = new ArrayList<>();
    }
    
    /**
     * Add a part to the message
     * @param messagePart The part of the message
     * @param color The color of the message part
     */
    public void addPartToMessage(String messagePart, Color color) {
        this.messageParts.add(new MessagePart(messagePart, color));
    }
    
    /**
     * Get the message
     * @return The message
     */
    public Component getMessage() {
        Component message = Component.empty();
        
        message = message.append(Component.text("[" + messageParts.get(0).pluginName + "] ", NamedTextColor.GREEN));

        for (MessagePart messagePart : messageParts) {
            NamedTextColor color = getColorCode(messagePart.color);
            Component part = Component.text(messagePart.messagePart, color);
            message = message.append(part);
        }

        return message;
    }

    /**
     * Get the color code for the message
     * @param color The color
     * @return The color code
     */
    private NamedTextColor getColorCode(Color color) {
        if (color.equals(Color.BLACK)) {
            return NamedTextColor.BLACK;
        } else if (color.equals(Color.BLUE)) {
            return NamedTextColor.BLUE;
        } else if (color.equals(Color.GRAY)) {
            return NamedTextColor.GRAY;
        } else if (color.equals(Color.GREEN)) {
            return NamedTextColor.GREEN;
        } else if (color.equals(Color.RED)) {
            return NamedTextColor.RED;
        } else if (color.equals(Color.WHITE)) {
            return NamedTextColor.WHITE;
        } else if (color.equals(Color.YELLOW)) {
            return NamedTextColor.YELLOW;
        }
        return NamedTextColor.WHITE; // Default to white
    }
    
}

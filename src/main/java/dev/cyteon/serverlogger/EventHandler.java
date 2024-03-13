package dev.cyteon.serverlogger;

import com.mojang.logging.LogUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.ServerChatEvent;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class EventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<UUID> connectedPlayers = new HashSet<>();
    private static final Set<UUID> killedPlayers = new HashSet<>();

    private static final Map<UUID, Long> playerCooldowns = new HashMap<>();
    private static final long COOLDOWN_DURATION_MS = 10000;
    private static final List<String> VALUABLE_ITEMS = Arrays.asList("netherite ingot");

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) throws IOException {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting! Sending STARTING to discord webhook");

        DiscordWebhook webhook = new DiscordWebhook(Config.WEBHOOKURL.get());
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Server Started")
                .setColor(Color.GREEN));
        webhook.execute();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) throws IOException {
        LOGGER.info("BYE from server starting! Sending STOPPED to discord webhook");

        DiscordWebhook webhook = new DiscordWebhook(Config.WEBHOOKURL.get());
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Server Stopped")
                .setColor(Color.RED));
        webhook.execute();
    }

    @SubscribeEvent
    public static void onPlayerJoined(EntityJoinWorldEvent event) throws IOException {
        if (event.getEntity() != null && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID playerId = player.getUUID();

            if (!connectedPlayers.contains(playerId)) {
                connectedPlayers.add(playerId);
                DiscordWebhook webhook = new DiscordWebhook(Config.WEBHOOKURL.get());
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("Player Joined")
                        .setDescription(player.getName().getString() + " has joined the server")
                        .setColor(Color.CYAN));
                webhook.execute();
            } else {
                killedPlayers.remove(playerId);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeft(EntityLeaveWorldEvent event) throws IOException {
        if (event.getEntity() != null && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID playerId = player.getUUID();

            LOGGER.info("left");

            if (!killedPlayers.contains(playerId)) {
                if (player.level.getPlayerByUUID(playerId) == null) {
                    connectedPlayers.remove(playerId);
                    DiscordWebhook webhook = new DiscordWebhook(Config.WEBHOOKURL.get());
                    webhook.addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Player Left")
                            .setDescription(player.getName().getString() + " has left the server")
                            .setColor(Color.CYAN));
                    webhook.execute();
                }

            }
        }
    }

    @SubscribeEvent
    public static void onPlayerKilled(LivingDeathEvent event) throws IOException {
        if(event.getEntity() != null && event.getEntity() instanceof Player) {
            LOGGER.info("killed");

            killedPlayers.add(event.getEntity().getUUID());

            String killerName;

            if (((Player) event.getEntity()).getKillCredit() != null) {
                killerName = ((Player) event.getEntity()).getKillCredit().getName().getString();
            } else {
                killerName = String.valueOf(((Player) event.getEntity()).getKillCredit());
                if (killerName == "null") {
                    killerName = "Unknown";
                }

            }

            DiscordWebhook webhook = new DiscordWebhook(Config.WEBHOOKURL.get());
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(event.getEntity().getName().getString() + " was killed by " + killerName)
                    .setColor(Color.ORANGE));
            webhook.execute();
        }
    }

    @SubscribeEvent
    public static void onPlayerPickedUpValuable(EntityItemPickupEvent event) throws IOException {
        if (event.getEntity() != null && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID playerId = player.getUUID();
            String itemName = event.getItem().getName().getString().toLowerCase(); // Get the item name as a lowercase string

            long currentTime = System.currentTimeMillis();
            Long lastItemPickupTime = playerCooldowns.get(playerId);

            if (VALUABLE_ITEMS.contains(itemName) && (lastItemPickupTime == null || currentTime - lastItemPickupTime >= COOLDOWN_DURATION_MS)) {
                playerCooldowns.put(playerId, currentTime);

                DiscordWebhook webhook = new DiscordWebhook(Config.WEBHOOKURL.get());
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle(player.getName().getString() + " got " + event.getItem().getName().getString())
                        .setColor(Color.CYAN));
                webhook.execute();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAchievementUnlocked(AdvancementEvent event) throws IOException {
        if (event.getEntity() != null && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Advancement advancement = event.getAdvancement();
            String advancementName;

            if (advancement.getDisplay() != null) {
                advancementName = advancement.getChatComponent().getString();


                DiscordWebhook webhook = new DiscordWebhook(Config.WEBHOOKURL.get());
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle(player.getName().getString() + " achieved " + advancementName)
                        .setDescription(advancement.getDisplay().getDescription().getString())
                        .setColor(Color.CYAN));
                webhook.execute();
            }
        }
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) throws IOException {
        String message = event.getMessage();
        String playerName = event.getPlayer().getDisplayName().getString();

        DiscordWebhook webhook = new DiscordWebhook(Config.CHATWEBHOOKURL.get());
            webhook.setContent("**" + playerName + " >> **" + message);
        webhook.execute();
    }
}

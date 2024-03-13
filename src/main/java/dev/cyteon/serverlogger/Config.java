package dev.cyteon.serverlogger;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> WEBHOOKURL;
    public static final ForgeConfigSpec.ConfigValue<String> CHATWEBHOOKURL;

    static {
        BUILDER.push("Discord");

        WEBHOOKURL = BUILDER.comment("Discord webhook URL")
                .define("Logs Webhook URL", "");
        CHATWEBHOOKURL = BUILDER.comment("Discord webhook URL")
                .define("Chat Webhook URL", "");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
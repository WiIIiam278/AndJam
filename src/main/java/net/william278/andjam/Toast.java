package net.william278.andjam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.roxeez.advancement.Advancement;
import net.roxeez.advancement.AdvancementManager;
import net.roxeez.advancement.display.FrameType;
import net.roxeez.advancement.trigger.Impossible;
import net.roxeez.advancement.trigger.TriggerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an advanced toast message that can be sent to a player and appear in the top right corner of their
 * screen, utilizing Roxeez's Advancement API
 */
@SuppressWarnings("unused")
public class Toast {

    // Prefix key to use for toast advancements
    private static final String ADVANCEMENT_KEY = "andjam_toast";

    // Roxeez's AdvancementAPI manager instance for registering advancements
    private static AdvancementManager advancementManager;

    // Cache of created toast advancements
    private static final Map<String, Advancement> cachedToastAdvancements = new HashMap<>();

    @NotNull
    private final JavaPlugin plugin;
    @NotNull
    private final Material icon;
    @NotNull
    private final Component title;
    @NotNull
    private final Component description;
    @NotNull
    private final FrameType frameType;

    /**
     * Create a new toast message
     *
     * @param plugin      The plugin that is sending the toast
     * @param name        The name of the toast
     * @param description The description of the toast
     * @param icon        The icon of the toast
     * @param frameType   The frame type of the toast
     * @see Toaster
     */
    protected Toast(@NotNull JavaPlugin plugin,
                    @NotNull Component name, @NotNull Component description,
                    @NotNull Material icon, @NotNull FrameType frameType) {
        this.plugin = plugin;
        this.title = name;
        this.description = description;
        this.icon = icon;
        this.frameType = frameType;

        // Prepare the advancement manager
        advancementManager = advancementManager == null ? new AdvancementManager(plugin) : advancementManager;
    }

    /**
     * Acceptor for the dummy advancement criteria
     *
     * @param trigger the trigger
     */
    private static void accept(Impossible trigger) {
    }

    /**
     * Get the title, formatted as a legacy string
     *
     * @return the title
     */
    @NotNull
    private String getLegacyTitleText() {
        return LegacyComponentSerializer.legacySection().serialize(title);
    }

    /**
     * Get the description, formatted as a legacy string
     *
     * @return the description
     */
    @NotNull
    private String getLegacyDescriptionText() {
        return LegacyComponentSerializer.legacySection().serialize(description);
    }

    /**
     * Get the ID value of the toast. This comprises the {@link #ADVANCEMENT_KEY}/A {@link UUID} seeded based on the value of
     * the {@link #getLegacyTitleText() legacy-text title} and {@link #getLegacyDescriptionText() description}.
     *
     * @return the ID value of the toast advancement
     */
    @NotNull
    private String getId() {
        return ADVANCEMENT_KEY + "/" + UUID.nameUUIDFromBytes((getLegacyTitleText()
                + getLegacyDescriptionText()).getBytes());
    }

    /**
     * Get the {@link Advancement} used to send this toast
     *
     * @return the advancement
     */
    @NotNull
    private Advancement getAdvancement() {
        final String advancementId = getId();
        if (cachedToastAdvancements.containsKey(advancementId)) {
            return cachedToastAdvancements.get(advancementId);
        }

        // Register the advancement
        final Advancement advancement = new Advancement(plugin, advancementId);
        advancement.addCriteria("display_toast", TriggerType.IMPOSSIBLE, Toast::accept);
        advancement.setDisplay(toast -> {
            toast.setTitle(getLegacyTitleText());
            toast.setDescription(getLegacyDescriptionText());
            toast.setIcon(icon);
            toast.setAnnounce(false);
            toast.setToast(true);
            toast.setHidden(true);
            toast.setFrame(frameType);
        });
        advancementManager.register(advancement);
        cachedToastAdvancements.put(advancementId, advancement);
        advancementManager.createAll(false);

        return advancement;
    }

    /**
     * Create a new toast message
     *
     * @param plugin the plugin
     * @return the toast message
     */
    @NotNull
    public static Toaster builder(@NotNull JavaPlugin plugin) {
        return new Toaster(plugin);
    }

    /**
     * Send the toast to a player, by granting then revoking the dummy advancement
     *
     * @param player the player to send the toast to
     */
    public void show(@NotNull Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            final Advancement advancementData = getAdvancement();
            final org.bukkit.advancement.Advancement advancement = Bukkit.getAdvancement(advancementData.getKey());
            if (advancement == null) {
                throw new IllegalStateException("Advancement not found");
            }
            advancement.getCriteria().forEach(criterion ->
                    player.getAdvancementProgress(advancement).awardCriteria(criterion));
            advancement.getCriteria().forEach(criterion ->
                    player.getAdvancementProgress(advancement).revokeCriteria(criterion));
        });
    }
}

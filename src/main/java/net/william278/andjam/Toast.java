package net.william278.andjam;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.roxeez.advancement.Advancement;
import net.roxeez.advancement.AdvancementManager;
import net.roxeez.advancement.display.FrameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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
     * Get the title, formatted as a legacy string
     *
     * @return the title
     */
    @NotNull
    final String getLegacyTitleText() {
        return LegacyComponentSerializer.legacySection().serialize(title);
    }

    /**
     * Get the description, formatted as a legacy string
     *
     * @return the description
     */
    @NotNull
    final String getLegacyDescriptionText() {
        return LegacyComponentSerializer.legacySection().serialize(description);
    }

    /**
     * Get the icon material
     *
     * @return the icon {@link Material} type
     */
    @NotNull
    final Material getIcon() {
        return icon;
    }

    /**
     * Get the toast frame type
     *
     * @return the frame type
     */
    @NotNull
    final FrameType getFrameType() {
        return frameType;
    }

    /**
     * Get the {@link NamespacedKey} of the toast advancement
     *
     * @return the advancement key, the plugin mapped to the id
     */
    @NotNull
    private NamespacedKey getKey() {
        return Objects.requireNonNull(NamespacedKey.fromString(getId(), plugin));
    }

    /**
     * Get the ID value of the toast. This comprises the {@link #ADVANCEMENT_KEY}/A {@link UUID} seeded based on the value of
     * the {@link #getLegacyTitleText() legacy-text title} and {@link #getLegacyDescriptionText() description}.
     *
     * @return the ID value of the toast advancement
     */
    @NotNull
    String getId() {
        return ADVANCEMENT_KEY + "/" + UUID.nameUUIDFromBytes((getLegacyTitleText() + getLegacyDescriptionText()).getBytes());
    }

    /**
     * Get the {@link Advancement} used to send this toast
     */
    private void prepareAdvancement() {
        if (Bukkit.getAdvancement(getKey()) != null) {
            return;
        }

        final ToastAdvancement advancement = new ToastAdvancement(plugin, this);
        advancementManager.register(advancement);
        advancementManager.createAll(false);
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
     * @throws IllegalStateException if the toast advancement could not be created
     */
    public void show(@NotNull Player player) throws IllegalStateException {
        Bukkit.getScheduler().runTask(plugin, () -> {
            prepareAdvancement();

            final org.bukkit.advancement.Advancement advancement = Bukkit.getAdvancement(getKey());
            if (advancement == null) {
                throw new IllegalStateException("Advancement not found");
            }

            // Grant the advancement to the player
            player.getAdvancementProgress(advancement).awardCriteria(ToastAdvancement.CRITERIA_NAME);

            // Revoke the advancement from the player
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.getAdvancementProgress(advancement)
                    .revokeCriteria(ToastAdvancement.CRITERIA_NAME), 1);
        });
    }
}

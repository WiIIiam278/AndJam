/*
 * This file is part of AndJam, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.andjam;

import net.kyori.adventure.text.Component;
import net.roxeez.advancement.display.FrameType;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * A toaster, a.k.a. a builder for {@link Toast} messages
 */
@SuppressWarnings("unused")
public class Toaster {

    @NotNull
    private final JavaPlugin plugin;
    @NotNull
    private Component title = Component.empty();
    @NotNull
    private Component description = Component.empty();
    @NotNull
    private Material icon = Material.STONE;
    @NotNull
    private FrameType frameType = FrameType.TASK;

    /**
     * Create a new toast builder
     *
     * @param plugin The plugin that is sending the toast
     */
    Toaster(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Set the title of the toast
     *
     * @param title The title of the toast
     * @return The toast builder
     */
    @NotNull
    public Toaster setTitle(@NotNull Component title) {
        this.title = title;
        return this;
    }

    /**
     * Set the description of the toast
     *
     * @param description The description that should appear below the title
     * @return The builder
     */
    @NotNull
    public Toaster setDescription(@NotNull Component description) {
        this.description = description;
        return this;
    }

    /**
     * Set the icon {@link Material} of the toast
     *
     * @param icon The icon of the toast
     * @return The toast builder
     */
    @NotNull
    public Toaster setIcon(@NotNull Material icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Set the frame type of the toast; the background of the toast message box
     *
     * @param frameType The {@link FrameType} of the toast
     * @return The toast builder
     */
    @NotNull
    public Toaster setFrameType(@NotNull FrameType frameType) {
        this.frameType = frameType;
        return this;
    }

    /**
     * Build the toast message
     *
     * @return The {@link Toast} message that can be shown to players
     */
    @NotNull
    public Toast build() {
        return new Toast(plugin, title, description, icon, frameType);
    }

}

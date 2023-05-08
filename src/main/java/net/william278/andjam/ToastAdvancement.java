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

import net.roxeez.advancement.Advancement;
import net.roxeez.advancement.AdvancementCreator;
import net.roxeez.advancement.Context;
import net.roxeez.advancement.trigger.Impossible;
import net.roxeez.advancement.trigger.TriggerType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the advancement used to display a {@link Toast} message
 */
public class ToastAdvancement implements AdvancementCreator {

    private final JavaPlugin plugin;
    private final Toast toast;
    static final String CRITERIA_NAME = "display_toast";

    protected ToastAdvancement(@NotNull JavaPlugin plugin, @NotNull Toast toast) {
        this.plugin = plugin;
        this.toast = toast;
    }

    @Override
    @NotNull
    public Advancement create(@NotNull Context context) {
        // Register the advancement
        final Advancement advancement = new Advancement(plugin, toast.getId());

        // Set the toast's display properties and criteria
        advancement.addCriteria(CRITERIA_NAME, TriggerType.IMPOSSIBLE, ToastAdvancement::accept);
        advancement.setDisplay(display -> {
            display.setTitle(toast.getLegacyTitleText());
            display.setDescription(toast.getLegacyDescriptionText());
            display.setIcon(toast.getIcon());
            display.setFrame(toast.getFrameType());
            display.setAnnounce(false);
            display.setToast(true);
            display.setHidden(true);
        });

        return advancement;
    }

    /**
     * Acceptor for the impossible advancement criteria
     *
     * @param trigger the trigger
     */
    private static void accept(Impossible trigger) {
    }

}

/*
 * This file is part of Politics.
 *
 * Copyright (c) 2019 Oliver Stanley
 * Politics is licensed under the Affero General Public License Version 3.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pw.ollie.politics.util.message;

import gnu.trove.map.hash.THashMap;

import pw.ollie.politics.Politics;
import pw.ollie.politics.util.MutableString;
import pw.ollie.politics.util.reflect.ReflectionUtil;
import pw.ollie.politics.util.stream.CollectorUtil;

import com.google.mu.util.stream.BiStream;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

public final class Messenger {

    private final Map<String, String> messages;
    private final Function<String, String> transformer;

    private Configuration source;

    public Messenger(Configuration source) {
        this.messages = new THashMap<>();

        this.transformer = value -> {
            value = Politics.getColourScheme().transform('&', value);
            value = ChatColor.translateAlternateColorCodes('&', value);
            return value;
        };

        this.source = source;

        for (String missingKey : loadValues(MessageKeys.class)) {
            Politics.getLogger().log(Level.WARNING, "No configured message found for message key '" + missingKey + "'. Default value will be used.");
        }
    }

    public void sendConfiguredMessage(CommandSender recipient, String key) {
        sendConfiguredMessage(recipient, key, BiStream.empty());
    }

    public void sendConfiguredMessage(CommandSender recipient, String key, BiStream<String, String> vars) {
        send(recipient, new MutableString(transformer.apply(get(key))).replace(vars.mapKeys(k -> "%" + k + "%")));
    }

    public void sendConfiguredMessage(CommandSender recipient, String key, Map<String, String> vars) {
        sendConfiguredMessage(recipient, key, BiStream.from(vars));
    }

    public void setMessage(String key, String value) {
        messages.put(key, value);
    }

    public void setConfigurationSource(Configuration source) {
        this.source = source;
    }

    public Set<String> loadValues(Class<?>... keyHolders) {
        return Arrays.stream(keyHolders).map(Class::getDeclaredFields).flatMap(Arrays::stream)
                .filter(isKeyHolder).map(this::loadValue).filter(Objects::nonNull).collect(CollectorUtil.toTHashSet());
    }

    public static final Predicate<Field> isKeyHolder = field -> ReflectionUtil.isConstant(field) && String.class.isAssignableFrom(field.getType());

    public static void setDefault(String key, String def) {
        defaultMessages.put(key, def);
    }

    private String get(String key) {
        return messages.computeIfAbsent(key, k -> defaultMessages.getOrDefault(k, k));
    }

    private void send(CommandSender recipient, MutableString message) {
        recipient.sendMessage(message.get());
    }

    private String loadValue(Field field) {
        String key = getStringFieldValue(field);
        Optional<String> val = getSourceValue(key);
        val.ifPresent(v -> messages.put(key, v));
        return val.isPresent() ? null : key;
    }

    private Optional<String> getSourceValue(String key) {
        return Optional.ofNullable(key == null ? null : source.getString(key, null));
    }

    private static final Map<String, String> defaultMessages = new THashMap<>();

    static {
        setDefault(MessageKeys.ACTIVITY_SELECTION_FIRST_POINT_SET, "&normalFirst point set. Please left click a block to select second point.");

        setDefault(MessageKeys.COMMAND_SPECIFY_PLAYER, "&errorYou must specify a player for that.");
        setDefault(MessageKeys.COMMAND_NO_PERMISSION, "&errorYou don't have permission to do that.");
        setDefault(MessageKeys.COMMAND_PLAYER_OFFLINE, "&errorThat player is not online.");

        setDefault(MessageKeys.COMMAND_GROUP_ADD_PLAYER_HAS_GROUP, "&errorThat player is already part of a %level%.");
        setDefault(MessageKeys.COMMAND_GROUP_ADD_NO_IMMEDIATE_MEMBERS, "&errorYou cannot add to a %level% other than through a sub-organisation.");
        setDefault(MessageKeys.COMMAND_GROUP_ADD_DISALLOWED, "&errorYou may not add that player to that %level%.");
        setDefault(MessageKeys.COMMAND_GROUP_ADD_SUCCESS, "&normalAdded the player to the %level% with role &highlight%role% &normal.");
        // todo more defaults
    }

    private static String getStringFieldValue(Field field) {
        return (String) ReflectionUtil.getAccessibleFieldValue(field, null);
    }
}

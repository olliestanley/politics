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

public final class Messages {
    // todo docs
    public static final class PoliticsKeys {
        public static final String ACTIVITY_SELECTION_FIRST_POINT_SET = "Activity.Selection.First-Point-Set";
        // todo add keys

        private PoliticsKeys() {
            throw new UnsupportedOperationException();
        }
    }

    private final Map<String, String> messages;
    private final Function<String, String> transformer;

    private Configuration source;

    public Messages(Configuration source) {
        this.messages = new THashMap<>();

        this.transformer = value -> {
            value = ChatColor.translateAlternateColorCodes('&', value);
            value = Politics.getColourScheme().transform('&', value);
            return value;
        };

        this.source = source;

        for (String missingKey : this.loadValues(PoliticsKeys.class)) {
            Politics.getLogger().log(Level.WARNING, "No configured message found for message key '" + missingKey + "'. Default value will be used.");
        }
    }

    public void sendConfiguredMessage(CommandSender recipient, String key) {
        sendConfiguredMessage(recipient, key, BiStream.empty());
    }

    public void sendConfiguredMessage(CommandSender recipient, String key, BiStream<String, String> vars) {
        get(key).ifPresent(msg -> send(recipient, new MutableString(msg).transform(transformer).replace(vars)));
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

    public static void setDefault(String key, String def) {
        defaultMessages.put(key, def);
    }

    private Optional<String> get(String key) {
        return Optional.ofNullable(messages.getOrDefault(key, defaultMessages.get(key)));
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
        String raw = key == null ? null : source.getString(key, null);
        return raw == null ? Optional.empty() : Optional.of(raw);
    }

    private static final Map<String, String> defaultMessages = new THashMap<>();

    static {
        setDefault(PoliticsKeys.ACTIVITY_SELECTION_FIRST_POINT_SET, "First point set. Please left click a block to select second point.");
        // todo more defaults
    }

    private static Predicate<Field> isKeyHolder = field -> ReflectionUtil.isConstant(field) && String.class.isAssignableFrom(field.getType());

    private static String getStringFieldValue(Field field) {
        return (String) ReflectionUtil.getAccessibleFieldValue(field, null);
    }
}

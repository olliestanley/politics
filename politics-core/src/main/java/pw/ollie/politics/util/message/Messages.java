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
import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.Politics;
import pw.ollie.politics.util.stream.StreamUtil;

import org.bukkit.configuration.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class Messages {
    // todo docs
    public static final class PoliticsKeys {
        // todo add keys

        private PoliticsKeys() {
            throw new UnsupportedOperationException();
        }
    }

    private final Map<String, String> messages;

    private Configuration source;

    public Messages(Configuration source) {
        this.messages = new THashMap<>();
        this.source = source;

        for (String missingKey : this.loadValues(PoliticsKeys.class)) {
            Politics.getLogger().log(Level.WARNING, "No configured message found for message key '" + missingKey + "'. Default value will be used.");
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, defaultMessages.get(key));
    }

    public void setMessage(String key, String value) {
        messages.put(key, value);
    }

    public void setConfigurationSource(Configuration source) {
        this.source = source;
    }

    public Set<String> loadValues(Class<?>... keyHolders) {
        return Arrays.stream(keyHolders).map(this::doLoadValues).flatMap(Set::stream).collect(Collectors.toSet());
    }

    private Set<String> doLoadValues(Class<?> keyHolder) {
        Set<String> missing = new THashSet<>();
        StreamUtil.zipStreams(
                () -> Arrays.stream(keyHolder.getDeclaredFields()).filter(isKeyHolder).map(Messages::getStringFieldValue).filter(Objects::nonNull),
                this::getSourceValue)
                .forEach((key, val) -> val.ifPresentOrElse(v -> setMessage(key, v), () -> missing.add(key)));
        return missing;
    }

    private Optional<String> getSourceValue(String key) {
        String raw = source.getString(key, null);
        if (raw == null) {
            return Optional.empty();
        }
        return Optional.of(raw);
    }

    private static final Map<String, String> defaultMessages = new THashMap<>();

    public static void setDefault(String key, String def) {
        defaultMessages.put(key, def);
    }

    static {
        // todo add default messages
    }

    private static Predicate<Integer> isConst = mod -> Modifier.isFinal(mod) && Modifier.isStatic(mod) && Modifier.isPublic(mod);
    private static Predicate<Field> isKeyHolder = field -> isConst.test(field.getModifiers()) && String.class.isAssignableFrom(field.getType());

    private static String getStringFieldValue(Field field) {
        try {
            return (String) field.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
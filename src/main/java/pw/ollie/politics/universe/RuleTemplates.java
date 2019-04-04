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
package pw.ollie.politics.universe;

import pw.ollie.politics.Politics;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class RuleTemplates {
    public static boolean copyTemplate(String name, String as) {
        InputStream templateStream = RuleTemplates.class.getResourceAsStream("templates/" + name.toLowerCase() + ".yml");
        if (templateStream == null) {
            return false;
        }
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(templateStream, writer, Charset.defaultCharset());
        } catch (IOException ex) {
            Politics.getLogger().log(Level.SEVERE, "Could not read template " + name + "!", ex);
            return false;
        } finally {
            IOUtils.closeQuietly(templateStream);
        }

        File dest = new File(Politics.getFileSystem().getRulesDir(), as.toLowerCase() + ".yml");
        try {
            FileUtils.writeStringToFile(dest, writer.toString());
        } catch (IOException ex) {
            Politics.getLogger().log(Level.SEVERE, "Could not write template as " + dest.getPath() + "!", ex);
            return false;
        } finally {
            IOUtils.closeQuietly(writer);
        }

        return true;
    }

    public static Set<String> listTemplateNames() {
        CodeSource src = RuleTemplates.class.getProtectionDomain().getCodeSource();
        Set<String> set = new HashSet<>();

        try {
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip;
                zip = new ZipInputStream(jar.openStream());
                ZipEntry ze;

                while ((ze = zip.getNextEntry()) != null) {
                    final String entryName = ze.getName();
                    if (entryName.startsWith("templates") && entryName.endsWith(".yml")) {
                        set.add(entryName.substring("templates".length(), entryName.length() - 4));
                    }
                }
            }
        } catch (IOException ex) {
            Politics.getLogger().log(Level.SEVERE, "Could not list template names!", ex);
        }

        return set;
    }

    private RuleTemplates() {
        throw new UnsupportedOperationException();
    }
}

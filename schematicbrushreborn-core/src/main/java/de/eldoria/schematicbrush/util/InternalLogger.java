/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.Deflater;

public class InternalLogger {
    public static void init(Plugin plugin) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();

        Path logfile = plugin.getDataFolder().toPath().resolve("logs").resolve("latest.log");
        try {
            Files.createDirectories(logfile.getParent());
            plugin.getLogger().info("Created directory");
        } catch (IOException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Could not create directory for logs", e);
        }
        // Create a new RollingFileAppender
        RollingFileAppender appender = RollingFileAppender.newBuilder()
                .withFileName(logfile.toString())
                .withFilePattern("logs/log-%d{yyyy-MM-dd}.log.gz")
                .withPolicy(TimeBasedTriggeringPolicy.newBuilder().withInterval(1).withModulate(true).build())
                .withStrategy(DefaultRolloverStrategy.newBuilder().withMin("1").withCompressionLevelStr(String.valueOf(Deflater.DEFAULT_COMPRESSION)).build())
                .setLayout(PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss]}[%p{length=4}][%logger] %msg%ex%n").build())
                .setName(plugin.getName())
                .build();
        appender.start();

        config.addAppender(appender);
        config.getRootLogger().addAppender(appender, Level.ALL, new NameFilter(Set.of("SchematicBrushReborn")));

        context.updateLoggers();
    }

    static class NameFilter extends AbstractFilter {
        private final Set<String> whitelist;

        public NameFilter(Set<String> whitelist) {
            this.whitelist = whitelist;
        }

        @Override
        public Result filter(LogEvent event) {
            if (whitelist.contains(event.getLoggerName())) return Result.ACCEPT;
            for (String s : whitelist) {
                if (event.getLoggerName().startsWith(s)) return Result.ACCEPT;
            }
            return Result.DENY;
        }
    }
}

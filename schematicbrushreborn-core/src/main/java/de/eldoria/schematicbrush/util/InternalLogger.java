package de.eldoria.schematicbrush.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.Deflater;

public class InternalLogger {
    public static void init(Plugin plugin) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();

        Path logfile = plugin.getDataFolder().toPath().resolve("logs").resolve("app.log");
        try {
            Files.createDirectories(logfile.getParent());
            plugin.getLogger().info("Created directory");
        } catch (IOException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Could not create directory for logs", e);
        }
        // Create a new RollingFileAppender
        RollingFileAppender appender = RollingFileAppender.newBuilder()
                .withFileName(logfile.toString())
                .withFilePattern("logs/app-%d{MM-dd-yyyy}.log.gz")
                .withPolicy(TimeBasedTriggeringPolicy.newBuilder().withInterval(1).withModulate(true).build())
                .withStrategy(DefaultRolloverStrategy.newBuilder().withMin("1").withCompressionLevelStr(String.valueOf(Deflater.DEFAULT_COMPRESSION)).build())
                .setLayout(PatternLayout.createDefaultLayout())
                .setName(plugin.getName())
                .build();
        appender.start();

        config.addAppender(appender);

        // Add appender to loggers
        AppenderRef ref = AppenderRef.createAppenderRef(plugin.getName(), null, null);
        AppenderRef[] refs = new AppenderRef[]{ref};

        var eldoriaLogger = LoggerConfig.newBuilder().withConfig(config).withLoggerName("de.eldoria.schematicbrush").withLevel(Level.ALL).withRefs(refs).build();
        var chojoLogger = LoggerConfig.newBuilder().withConfig(config).withLoggerName("de.chojo").withLevel(Level.ALL).withRefs(refs).build();
        eldoriaLogger.addAppender(appender, Level.ALL, null);
        chojoLogger.addAppender(appender, Level.ALL, null);

        config.addLogger("de.eldoria", eldoriaLogger);
        config.addLogger("de.chojo", chojoLogger);

        context.updateLoggers();
    }
}

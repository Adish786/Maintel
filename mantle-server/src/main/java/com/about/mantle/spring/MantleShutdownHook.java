package com.about.mantle.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Handles writing thread dump to output file on shutdown. JVM can write thread dump on SIGQUIT but does
 * not appear to do so under SIGTERM, so this is done manually with the shutdown hook.
 * Shutdown hook also adds delay before jvm exits. This allows container scripts time to sync thread dump to S3
 * before being terminated.
 */
public class MantleShutdownHook {
	private static final Logger logger = LoggerFactory.getLogger(MantleShutdownHook.class);

	private final String threadDumpFilePath;
	private final Thread shutdownListener;

	public MantleShutdownHook(String threadDumpFilePath) {
		this.threadDumpFilePath = threadDumpFilePath;

		this.shutdownListener = new Thread(){
			public void run(){
				final StringBuilder dump = new StringBuilder();
				final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
				final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
				for (ThreadInfo threadInfo : threadInfos) {
					// Null values may be returned
					if (threadInfo == null) continue;
					dump.append('"');
					dump.append(threadInfo.getThreadName());
					dump.append("\" ");
					final State state = threadInfo.getThreadState();
					dump.append("\n   java.lang.Thread.State: ");
					dump.append(state);
					final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
					for (final StackTraceElement stackTraceElement : stackTraceElements) {
						dump.append("\n        at ");
						dump.append(stackTraceElement);
					}
					dump.append("\n\n");
				}

				try (BufferedWriter writer = new BufferedWriter(new FileWriter(threadDumpFilePath))) {
					writer.write(dump.toString());
					writer.flush();
				} catch (IOException e) {
					logger.error("Unable to write thread dump file on shutdown", e);
				}

				try {
					Thread.sleep(10*1000); // Sleep 10 seconds to allow scripts to sync S3 content
				} catch (InterruptedException e) {
					logger.error("Unable to wait in shutdown handler", e);
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(this.shutdownListener);
	}

	public Thread getShutdownListener() {
		return shutdownListener;
	}
}
package hu.juranyi.zsolt.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO JAVADOC
public class JSoupDownloader {

	private static final Logger LOG = LoggerFactory
			.getLogger(JSoupDownloader.class);
	private int beforeSleepMs = 1000;
	private int retryCount = 10;
	private int retrySleepMs = 3000;
	private int retrySleepMul = 2;

	public JSoupDownloader() {
	}

	public JSoupDownloader(int beforeSleepMs, int retryCount, int retrySleepMs,
			int retrySleepMul) {
		this.beforeSleepMs = beforeSleepMs;
		this.retryCount = retryCount;
		this.retrySleepMs = retrySleepMs;
		this.retrySleepMul = retrySleepMul;
	}

	public Document downloadDocument(String url) {
		LOG.info("Download parameters: {}", this);
		try {
			Thread.sleep(beforeSleepMs);
		} catch (InterruptedException ex) {
		}

		int currentSleepMs = retrySleepMs;
		int triesRemaining = retryCount;
		while (triesRemaining > 0) {
			try {
				LOG.info("Downloading URL: " + url);
				Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
				LOG.info("Download complete.");
				return doc;
			} catch (Exception e) {
				triesRemaining--;
				LOG.info("Download failed, retrying after {} ms...",
						currentSleepMs);
				try {
					Thread.sleep(currentSleepMs);
				} catch (InterruptedException e1) {
				}
				currentSleepMs *= retrySleepMul;
			}
		}
		LOG.error("Download failed.");
		return null;
	}

	@Override
	public String toString() {
		return "[beforeSleepMs=" + beforeSleepMs + ", retryCount=" + retryCount
				+ ", retrySleepMs=" + retrySleepMs + ", retrySleepMul="
				+ retrySleepMul + "]";
	}

}

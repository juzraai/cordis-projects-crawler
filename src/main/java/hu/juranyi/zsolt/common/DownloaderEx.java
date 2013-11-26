package hu.juranyi.zsolt.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO JAVADOC
// TODO settings!
public class DownloaderEx extends Downloader {

	private static final Logger LOG = LoggerFactory
			.getLogger(DownloaderEx.class);
	private int beforeSleepMs = 1000;
	private int retryCount = 10;
	private int retrySleepMs = 3000;
	private int retrySleepMul = 2;
	private String serverErrorMessageRegex;

	public DownloaderEx() {
	}

	public DownloaderEx(String url) {
		super(url);
	}

	public DownloaderEx(String url, String proxy) {
		super(url, proxy);
	}

	@Override
	public boolean download() {
		LOG.info("Download parameters: {}", this);
		try {
			Thread.sleep(beforeSleepMs);
		} catch (InterruptedException ex) {
		}

		int currentSleepMs = retrySleepMs;
		int triesRemaining = retryCount;
		while (triesRemaining > 0) {
			LOG.info("Downloading URL: " + url);
			boolean success = super.download();
			if (success && !serverErrorFound(getHtml())) {
				LOG.info("Download complete.");
				return true;
			} else {
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
		return false;
	}

	public String getServerErrorMessageRegex() {
		return serverErrorMessageRegex;
	}

	private boolean serverErrorFound(String html) {
		if (null != serverErrorMessageRegex) {
			LOG.info("Checking server errors...");
			if (null != StringTools.findFirstMatch(html,
					serverErrorMessageRegex, 0)) {
				LOG.error("Server error message found!");
				return true;
			}
		}
		return false;
	}

	public void setServerErrorMessageRegex(String serverErrorMessageRegex) {
		this.serverErrorMessageRegex = serverErrorMessageRegex;
	}

	@Override
	public String toString() {
		return "[beforeSleepMs=" + beforeSleepMs + ", retryCount=" + retryCount
				+ ", retrySleepMs=" + retrySleepMs + ", retrySleepMul="
				+ retrySleepMul + "]";
	}
}

package hu.juranyi.zsolt.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Downloads a HTML page with HTTP GET request. Sends and receives cookies,
 * which are stored in 'cookies' field. You can set user-agent (has a default:
 * Firefox 9 on Win 7), and proxy. It sums all downloaded HTML page lengths in a
 * static 'bytes' field.
 * 
 * @author Zsolt Jurányi
 */
public class Downloader {

	/**
	 * Sent and received cookies.
	 */
	protected Map<String, String> cookies = new HashMap<String, String>();
	/**
	 * URL to download.
	 */
	protected String url;
	/**
	 * Proxy in IP:PORT format.
	 */
	protected String proxy;
	/**
	 * HTTP User-Agent parameter. Default value is a Firefox 9 on Windows 7.
	 */
	protected String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:9.0.1) Gecko/20100101 Firefox/9.0.1";
	/**
	 * The HTML response downloaded or null on fail.
	 */
	protected String html;
	/**
	 * Bytes (HTML lengths) downloaded so far.
	 */
	protected static int bytes;

	/**
	 * Creates a Downloader. You have to set URL and call download() before you
	 * query the HTML.
	 */
	public Downloader() {
	}

	/**
	 * Creates a Downloader and sets 'url' field. You have to call download()
	 * before you query the HTML.
	 * 
	 * @param url
	 *            URL to the page to download.
	 */
	public Downloader(String url) {
		this.url = url;
	}

	/**
	 * Creates a Downloader and sets 'url' and 'proxy' field.
	 * 
	 * @param url
	 *            URL to the page to download.
	 * @param proxy
	 *            Proxy in IP:PORT format.
	 */
	public Downloader(String url, String proxy) {
		this.url = url;
		this.proxy = proxy;
	}

	/**
	 * Calls download() and retry if download failed.
	 * 
	 * @param retries
	 *            Retry count.
	 * @return True on success, false on fail.
	 */
	public boolean download(int retries) {
		boolean success = false;
		for (int i = 0; i < retries && !success; i++) {
			success = download();
			if (!success && i < retries) {
				System.out.println("Retrying after 5 sec...");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
				}
			}
		}
		return success;
	}

	/**
	 * Executes the request, send and receive cookies, downloads HTML, adds
	 * length to bytes.
	 * 
	 * @return True on success, false on fail.
	 */
	public boolean download() {
		if (null == url) {
			return false;
		}
		html = null;
		try {
			URL u = new URL(url);

			Proxy p = Proxy.NO_PROXY;
			if (null != proxy) {
				String proxyIP = proxy.split(":")[0];
				int proxyPort = Integer.parseInt(proxy.split(":")[1]);
				p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIP,
						proxyPort));
			}
			HttpURLConnection huc = (HttpURLConnection) u.openConnection(p);

			huc.setConnectTimeout(15 * 1000);
			huc.setReadTimeout(15 * 1000);

			// set user agent
			huc.addRequestProperty("Accept-Language",
					"hu-HU,hu;q=0.8,en-US;q=0.6,en;q=0.4");
			huc.addRequestProperty("User-agent", userAgent);

			// set cookies
			StringBuffer c = new StringBuffer();
			for (String key : cookies.keySet()) {
				c.append(key).append("=").append(cookies.get(key)).append(";");
			}
			huc.addRequestProperty("Cookie", c.toString());

			huc.connect();

			// get cookies
			String hf;
			int i = 0;
			while ((hf = huc.getHeaderField(i)) != null) {
				String key = huc.getHeaderFieldKey(i);
				if (null != key) {
					// System.out.println(hf);
				}
				if (null != key && key.equalsIgnoreCase("Set-Cookie")) {
					String def = hf.substring(0, hf.indexOf(';'));
					String[] dp = def.split("=", 2);
					if (dp.length == 2) {
						cookies.put(dp[0], dp[1]);
					}
				}
				i++;
			}

			// read
			if (huc.getResponseCode() / 100 == 2) {
				// html = "";
				StringBuffer buf = new StringBuffer();
				BufferedReader r = new BufferedReader(new InputStreamReader(
						huc.getInputStream()));
				String line;
				while ((line = r.readLine()) != null) {
					line = new String(line.getBytes(), "UTF-8");
					// html += (line + "\n");
					buf.append(line).append("\n");
				}
				r.close();
				html = buf.toString();
				bytes += html.length();
			} else {
				System.out.println("Response: " + huc.getResponseCode() + " "
						+ huc.getResponseMessage());
				System.out.println("( " + url + " )\n");
			}

			huc.disconnect();

			return (html != null);
		} catch (Exception ex) {
			// System.out.println(ex.getMessage() + " " + url + "(proxy: " +
			// proxy + ")");
			// System.out.println(url + " EXCEPTION:");
			// ex.printStackTrace();
			return false;
		}
	}

	public static int getBytes() {
		return bytes;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	public String getHtml() {
		return html;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}

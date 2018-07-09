/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 * 
 * The GDSC Analytics package contains code to use the Google Analytics Measurement
 * protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert, Daniel Murphy
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package gdsc.analytics;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Populates the client with information from the system
 * 
 * @author Alex Herbert
 */
public class ClientParametersManager
{
	/**
	 * Populates the client parameters with information from the system.
	 *
	 * @param data
	 *            The data
	 */
	public static final void populate(ClientParameters data)
	{
		String region = System.getProperty("user.region");
		if (region == null)
		{
			region = System.getProperty("user.country");
		}
		data.setUserLanguage((System.getProperty("user.language") + "-" + region).toLowerCase());

		// Do not collect the hostname be default
		data.setHostName("localhost");

		final String os_name = System.getProperty("os.name");

		final Dimension d = getScreenSize(os_name);
		data.setScreenResolution(d.width + "x" + d.height);

		// The browser and operating system are taken from the User-Agent property.

		//data.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"); // To simulate Chrome

		// The Java URLConnection User-Agent property will default to 'Java/1.6.0.19' where the 
		// last part is the JRE version. Add the operating system to this, e.g. Java/1.6.0.19 (Windows NT 6.1)

		StringBuilder sb = new StringBuilder();
		sb.append("Java/").append(System.getProperty("java.version"));
		sb.append(" (").append(getPlatform(os_name)).append(")");
		data.setUserAgent(sb.toString());

		// Note: Adding the OS does not currently work within Google Analytics.
		//
		// https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ua
		// "The User Agent of the browser. Note that Google has libraries to identify real user agents. 
		// Hand crafting your own agent could break at any time."

		// A better option is to pass in custom dimension so this data can be used in reports.
	}

	/**
	 * Populates the client parameters with the system hostname.
	 * <p>
	 * The call will timeout after 2 seconds (e.g. if the DNS is not working) and the hostname will be set to localhost.
	 *
	 * @param data
	 *            The data
	 */
	public static final void populateHostname(ClientParameters data)
	{
		String hostName = "localhost";

		// This can wait for a long time (e.g. if the DNS is not working). 
		// Write so that it can timeout without causing a delay to the calling program.
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(new Callable<String>()
		{
			@Override
			public String call() throws Exception
			{
				String hostName = "localhost";
				try
				{
					final InetAddress iAddress = InetAddress.getLocalHost();
					// This performs a lookup of the name service as well
					// e.g. host.domain.com
					hostName = iAddress.getCanonicalHostName();

					// This only retrieves the bare hostname
					// e.g. host
					// hostName = iAddress.getHostName();

					// This retrieves the IP address as a string
					// e.g. 192.168.0.1 
					//hostName = iAddress.getHostAddress();
				}
				catch (UnknownHostException e)
				{
					//ignore this
				}
				return hostName;
			}
		});
		try
		{
			hostName = future.get(2, TimeUnit.SECONDS); //timeout is in 2 seconds
		}
		catch (TimeoutException e)
		{
			System.err.println("GDSC Analytics: Timeout when resolving hostname");
		}
		catch (InterruptedException e)
		{
			//ignore this
		}
		catch (ExecutionException e)
		{
			//ignore this
		}
		executor.shutdownNow();

		data.setHostName(hostName);
	}

	/**
	 * Get the platform for the User-Agent string.
	 *
	 * @param os_name
	 *            the os name
	 * @return The platform
	 */
	private static String getPlatform(String os_name)
	{
		// Note that on Windows the os.version property does not directly translate into the user agent platform token:
		// https://msdn.microsoft.com/en-gb/library/ms537503(v=vs.85).aspx
		// https://en.wikipedia.org/wiki/Windows_NT#Releases
		final String lc_os_name = os_name.toLowerCase();
		if (lc_os_name.contains("windows"))
		{
			//@formatter:off
            if (lc_os_name.contains("10"))              return "Windows NT 10.0";
            if (lc_os_name.contains("server 2016"))     return "Windows NT 10.0";
            if (lc_os_name.contains("8.1"))             return "Windows NT 6.3";
            if (lc_os_name.contains("server 2012 r2"))  return "Windows NT 6.3";
            if (lc_os_name.contains("8"))               return "Windows NT 6.2";
            if (lc_os_name.contains("server 2012"))     return "Windows NT 6.2";
            if (lc_os_name.contains("7"))               return "Windows NT 6.1";
            if (lc_os_name.contains("server 2011"))     return "Windows NT 6.1";
            if (lc_os_name.contains("server 2008 r2"))  return "Windows NT 6.1";
            if (lc_os_name.contains("vista"))           return "Windows NT 6.0";
            if (lc_os_name.contains("server 2008"))     return "Windows NT 6.0";
            if (lc_os_name.contains("server 2003"))     return "Windows NT 5.2";
            if (lc_os_name.contains("xp x64"))          return "Windows NT 5.2";
            if (lc_os_name.contains("xp"))              return "Windows NT 5.1";
            if (lc_os_name.contains("2000, service"))   return "Windows NT 5.01";
            if (lc_os_name.contains("2000"))            return "Windows NT 5.0";
            if (lc_os_name.contains("nt 4"))            return "Windows NT 4.0";
            if (lc_os_name.contains("mw"))              return "Windows 98; Win 9x 4.90";
            if (lc_os_name.contains("98"))              return "Windows 98";
            if (lc_os_name.contains("95"))              return "Windows 95";
            if (lc_os_name.contains("ce"))              return "Windows CE";
			return "Windows NT 6.1"; // Default to Windows 7
			//@formatter:on
		}

		// Mac - Note sure what to put here.
		// E.g. Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A
		if (lc_os_name.startsWith("mac"))
			// Just pick a recent valid platform from a valid Mac User-Agent string
			return "Macintosh; Intel Mac OS X 10_9_3";

		// Linux variants will just return 'Linux'. 
		// This is apparently detected by Google Analytics so we leave this as is.

		// Other - Just leave it

		final String os_version = System.getProperty("os.version");
		return os_name + " " + os_version;
	}

	/**
	 * Get the screen size
	 * <p>
	 * Adapted from ij.IJ.getScreenSize() in the ImageJ code.
	 * 
	 * @see "http://imagej.nih.gov/ij/"
	 * 
	 * @param os_name
	 *            The os.name system property
	 * @return The dimension of the primary screen
	 */
	public static Dimension getScreenSize(String os_name)
	{
		if (isWindows(os_name)) // GraphicsEnvironment.getConfigurations is *very* slow on Windows
			return Toolkit.getDefaultToolkit().getScreenSize();
		if (GraphicsEnvironment.isHeadless())
			return new Dimension(0, 0);
		// Can't use Toolkit.getScreenSize() on Linux because it returns 
		// size of all displays rather than just the primary display.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		GraphicsConfiguration[] gc = gd[0].getConfigurations();
		Rectangle bounds = gc[0].getBounds();
		if ((bounds.x == 0 && bounds.y == 0) || (isLinux(os_name) && gc.length > 1))
			return new Dimension(bounds.width, bounds.height);
		else
			return Toolkit.getDefaultToolkit().getScreenSize();
	}

	private static boolean isWindows(String os_name)
	{
		return os_name.startsWith("Windows");
	}

	private static boolean isLinux(String os_name)
	{
		return os_name.startsWith("Linux");
	}
}

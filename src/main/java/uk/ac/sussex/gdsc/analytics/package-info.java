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
/**
 * Provide a framework to send raw user interaction data directly to Google
 * Analytics servers via the <a href=
 * "https://developers.google.com/analytics/devguides/collection/protocol/v1/">Google
 * Analytics Measurement Protocol</a>.
 * <p>
 * This package is based on ideas from JGoogleAnalyticsTracker by Daniel Murphy.
 * A similar package is JGoogleAnalytics by Siddique Hameed.
 * <p>
 * The JGoogleAnalyticsTracker code dummied the GET request sent to Google
 * Analytics by a browser. This code uses the new Analytics Measurement Protocol
 * which is designed to allow any web connected device to measure user
 * interaction via a HTTP POST request.
 * <p>
 * As a result all of the classes related to building the analytics URL were
 * re-written. The URIEncoder and JGoogleAnalyticsTracker classes have been
 * modified but remain recognisable and so have the same name.
 * <p>
 * Since the code will only be used within a Java application the referral,
 * search referral and campaign functionality has been removed to simplify the
 * analytics and allow caching most of the constructed analytics URL. The code
 * is redistributed under the original MIT licence and maintains the copyright
 * of Daniel Murphey through the applicable original code with the addition of
 * Alex Herbert.
 *
 * @see <a href=
 *      "https://developers.google.com/analytics/devguides/collection/protocol/v1/">Google
 *      Analytics Measurement Protocol</a>
 * @see <a href=
 *      "https://code.google.com/archive/p/jgoogleanalyticstracker/">JGoogleAnalyticsTracker</a>
 * @see <a href=
 *      "https://github.com/siddii/jgoogleanalytics">JGoogleAnalytics</a>
 * @since 1.0.0
 */
package uk.ac.sussex.gdsc.analytics;

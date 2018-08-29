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
package uk.ac.sussex.gdsc.analytics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class SessionTest
{
	@Test
	public void testReset()
	{
		Session session = new Session();
		Assertions.assertTrue(session.isNew());
		Assertions.assertFalse(session.isNew());
		session.reset();
		Assertions.assertTrue(session.isNew());
	}

	@Test
	public void testTimeout()
	{
		Session session = new Session();
		// 1 second timeout
		session.setTimeout(1);
		Assertions.assertEquals(1, session.getTimeout());
		Assertions.assertTrue(session.isNew());
		Assertions.assertFalse(session.isNew());
		try
		{
			// Sleep for 1 second to ensure timeout
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{ // Ignore
		}
		Assertions.assertTrue(session.isNew());
	}
}
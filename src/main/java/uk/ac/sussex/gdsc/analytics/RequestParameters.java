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

/**
 * Google Analytics request data
 *
 * @see "https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters"
 * @author Alex Herbert
 */
public class RequestParameters extends Parameters
{
	private final HitType hitType;

	private String documentPath = null;
	private String documentTitle = null;
	private String category;
	private String action;
	private String label;
	private Integer value;

	/**
	 * Instantiates a new request parameters.
	 *
	 * @param hitType
	 *            the hit type
	 */
	public RequestParameters(HitType hitType)
	{
		this.hitType = hitType;
	}

	/**
	 * @return The hit type
	 */
	public String getHitType()
	{
		return hitType.toString();
	}

	/**
	 * @return The hit type
	 */
	public HitType getHitTypeEnum()
	{
		return hitType;
	}

	/**
	 * @return the document path
	 */
	public String getDocumentPath()
	{
		return documentPath;
	}

	/**
	 * @param documentPath
	 *            the document path to set
	 */
	public void setDocumentPath(String documentPath)
	{
		this.documentPath = documentPath;
	}

	/**
	 * @return the document title
	 */
	public String getDocumentTitle()
	{
		return documentTitle;
	}

	/**
	 * @param documentTitle
	 *            the document title to set
	 */
	public void setDocumentTitle(String documentTitle)
	{
		this.documentTitle = documentTitle;
	}

	/**
	 * @return the event category
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * @param category
	 *            the event category to set
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}

	/**
	 * @return the event action
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * @param action
	 *            the event action to set
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

	/**
	 * @return the event label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param label
	 *            the event label to set
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @return the event value
	 */
	public Integer getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *            the event value to set
	 */
	public void setValue(Integer value)
	{
		this.value = value;
	}

	/**
	 * Add a hit level custom dimension
	 *
	 * @see uk.ac.sussex.gdsc.analytics.Parameters#addCustomDimension(int, java.lang.String)
	 */
	@Override
	public void addCustomDimension(int index, String value)
	{
		super.addCustomDimension(index, value);
	}

	/**
	 * Add a hit level custom metric
	 *
	 * @see uk.ac.sussex.gdsc.analytics.Parameters#addCustomMetric(int, int)
	 */
	@Override
	public void addCustomMetric(int index, int value)
	{
		super.addCustomMetric(index, value);
	}
}
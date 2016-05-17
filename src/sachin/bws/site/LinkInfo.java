package sachin.bws.site;

import java.io.Serializable;

public class LinkInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String url;
	private String screenshot;

	public LinkInfo(String url) {
		this.url = url;
	}

	/**
	 * @return the screenshot location
	 */
	public String getScreenshot() {
		return screenshot;
	}

	/**
	 * @param screenshot
	 *            the screenshot location to set for this link
	 */
	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}

	/**
	 * @return the url of the link
	 */
	public String getUrl() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LinkInfo other = (LinkInfo) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}

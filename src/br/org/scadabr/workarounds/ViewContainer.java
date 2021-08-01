package br.org.scadabr.workarounds;

import com.serotonin.mango.view.View;

/**
 * This class stores graphic views in memory, and provides methods to access
 * these views from ViewManager class
 * 
 * @author celso
 */

public class ViewContainer {
	private View view;
	private Integer viewId;
	private long lastAccess;

	public ViewContainer(View view) {
		setView(view);
	}

	public void setView(View view) {
		this.view = view;
		this.viewId = view.getId();
		this.lastAccess = System.currentTimeMillis();
	}

	public View getView() {
		this.lastAccess = System.currentTimeMillis();
		return this.view;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public int getViewId() {
		return viewId.intValue();
	}

	public void setViewId(Integer viewId) {
		this.viewId = viewId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((viewId == null) ? 0 : viewId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		ViewContainer other = (ViewContainer) obj;
		if (viewId == null) {
			if (other.viewId != null)
				return false;
		} else if (!viewId.equals(other.getViewId()))
			return false;
		return true;
	}

}

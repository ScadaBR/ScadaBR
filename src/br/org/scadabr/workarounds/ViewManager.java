package br.org.scadabr.workarounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.serotonin.mango.view.View;

/**
 * This class allows to use multiple graphic views in ScadaBR. It is not the
 * best solution, but is an efficient workaround
 * 
 * @author celso
 */

public abstract class ViewManager {
	private static List<ViewContainer> views = new ArrayList<>();
	// Time (in milisseconds) to dischard old views
	private static final long PURGE_TIME = 240_000;
	private static final long TIME_BETWEEN_PURGES = 60_000;
	private static long lastPurge = System.currentTimeMillis();

	// Adds a new View Container, if not exists
	public static void addView(View view) {
		if (view != null) {
			ViewContainer container = new ViewContainer(view);
			if (!views.contains(container)) {
				views.add(container);
			} else {
				int index = views.indexOf(container);
				views.get(index).updateLastAccess();
			}
		}
	}

	// Updates View in a View Container, or creates a new View Container
	public static void updateView(View view) {
		if (view != null) {
			ViewContainer container = new ViewContainer(view);
			if (views.contains(container)) {
				int index = views.indexOf(container);
				views.get(index).setView(view);
			} else {
				views.add(container);
			}
		}
	}

	public static View getView(int viewId) {
		for (ViewContainer view : views) {
			if (view.getViewId() == viewId) {
				return view.getView();
			}
		}
		return null;
	}

	// Get number of views stored in this class. For debug purposes
	public int getViewsCount() {
		return views.size();
	}

	public static synchronized void purgeOldViews() {
		// This method will not run every time to enhance performance
		if (lastPurge > (System.currentTimeMillis() - TIME_BETWEEN_PURGES)) {
			// System.out.println("Purge skipped!");
			return;
		} else {
			lastPurge = System.currentTimeMillis();
			// System.out.println("Purging...");
		}

		long referenceTime = System.currentTimeMillis() - PURGE_TIME;
		Iterator<ViewContainer> viewIterator = views.iterator();

		while (viewIterator.hasNext()) {
			ViewContainer view = viewIterator.next();
			if (view.getLastAccess() < referenceTime) {
				// System.out.println("Removed view " + view.getViewId());
				viewIterator.remove();
			}
		}
	}
}

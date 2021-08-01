package br.org.scadabr.workarounds;

/**
 * This class cleans old views in ViewManager class asynchronously
 * 
 * @author celso
 */

public class ViewGarbageCollector extends Thread {

	public ViewGarbageCollector() {

	}

	@Override
	public void run() {
		// System.out.println("Collecting garbage...");
		ViewManager.purgeOldViews();
	}
}

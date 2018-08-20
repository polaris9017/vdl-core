package kr.projectn.vdl.core.event;

import com.google.common.eventbus.Subscribe;

public abstract class SubmoduleEventListener {
    private String subcode;

    /**
     * Method for receiving events from Eventbus
     * @param e Event instance
     */
    @Subscribe
    public void receive(SubmoduleEvent e) {
        subcode = e.getSubmodule();

        switch (e.getCurrentMethod()) {
            case "init":
                onInitPageLoaded();
                break;
            case "parse":
                onPageParsed();
                break;
            case "fetch":
                onFetchedVideoList();
                break;
            case "retrieve":
                onRetrievedMediaSpec();
                break;
            case "store":
                onStoredMediaSpec();
                break;
            case "error":
                onError(e);
        }
    }

    /*
     * methods handling events received from submodules
     */

    /**
     * handle requestInitPage() method
     */
    public abstract void onInitPageLoaded();

    /**
     * handle parsePage() method
     */
    public abstract void onPageParsed();

    /**
     * handle FetchVideoList() method
     */
    public abstract void onFetchedVideoList();

    /**
     * handle retrieveMediaSpec() method
     */
    public abstract void onRetrievedMediaSpec();

    /**
     * handle getFinalMediaSpec() method
     */
    public abstract void onStoredMediaSpec();

    /**
     * handle errors
     */
    public abstract void onError(SubmoduleEvent e);
}

package kr.projectn.vdl.core.event;

import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;

public interface SubmoduleEventListener {
    /**
     * Method for receiving events from Eventbus
     *
     * @param status status code<br>
     *               Structure: (subcode)-(mcode) [subcode: submodule code / mcode: current method]<br>
     *               ex) naver-parse: 'naver' submodule, running method: parsePage()
     */
    @Subscribe
    default void receive(String status) {
        switch (Splitter.on('-')
                .trimResults()
                .omitEmptyStrings()
                .splitToList(status).get(1)) {
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
        }
    }

    /*
     * methods handling events received from submodules
     */

    /**
     * handle requestInitPage() method
     */
    void onInitPageLoaded();

    /**
     * handle parsePage() method
     */
    void onPageParsed();

    /**
     * handle FetchVideoList() method
     */
    void onFetchedVideoList();

    /**
     * handle retrieveMediaSpec() method
     */
    void onRetrievedMediaSpec();

    /**
     * handle getFinalMediaSpec() method
     */
    void onStoredMediaSpec();

}

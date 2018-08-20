package kr.projectn.vdl.core.event;

import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;

public class SubmoduleEventListener {
    private String subcode;

    /**
     * Method for receiving events from Eventbus
     *
     * @param status status code<br>
     *               Structure: (subcode)-(mcode) [subcode: submodule code / mcode: current method]<br>
     *               ex) naver-parse: 'naver' submodule, running method: parsePage()
     */
    @Subscribe
    public void receive(String status) {
        subcode = Splitter.on('-')
                .trimResults()
                .omitEmptyStrings()
                .splitToList(status).get(0);

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
    public void onInitPageLoaded() {

    }

    /**
     * handle parsePage() method
     */
    public void onPageParsed() {

    }

    /**
     * handle FetchVideoList() method
     */
    public void onFetchedVideoList() {

    }

    /**
     * handle retrieveMediaSpec() method
     */
    public void onRetrievedMediaSpec() {

    }

    /**
     * handle getFinalMediaSpec() method
     */
    public void onStoredMediaSpec() {

    }
}

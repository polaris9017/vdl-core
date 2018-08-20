package kr.projectn.vdl.core.event;

import com.google.common.eventbus.Subscribe;
import kr.projectn.vdl.core.SubmoduleLoader;

public interface SubmoduleEventListener {
    SubmoduleLoader loader = null;

    /**
     * Method for receiving events from Eventbus
     *
     * @param status status code<br>
     *               Structure: (subcode)-(mcode) [subcode: submodule code / mcode: current method]<br>
     *               ex) 4-parse: 'naver' submodule, current method: parsePage()
     */
    @Subscribe
    void receive(String status);

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

    /***
     * handle getFinalMediaSpec() method
     */
    void onStoredMediaSpec();
}

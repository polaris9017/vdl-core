package kr.projectn.vdl.core.event;

public class DefaultSubmoduleEventListener extends SubmoduleEventListener {


    @Override
    public void onInitPageLoaded() {

    }

    @Override
    public void onPageParsed() {

    }

    @Override
    public void onFetchedVideoList() {

    }

    @Override
    public void onRetrievedMediaSpec() {

    }

    @Override
    public void onStoredMediaSpec() {

    }

    @Override
    public void onError(SubmoduleEvent e) {
        System.err.println(e.getErrorMessage());
    }
}

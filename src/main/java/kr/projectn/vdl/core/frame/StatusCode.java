package kr.projectn.vdl.core.frame;

public enum StatusCode {
    STAT_EVENT(0x100000),
    STAT_ERR(0x200000),
    E_INIT(0x100001),
    E_PARSE(0x100010),
    E_LOAD(0x101000),
    E_STORE(0x110000),
    E_FETCH(0x100100);

    private int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

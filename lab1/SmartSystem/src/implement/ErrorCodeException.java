package implement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hzq
 */
public class ErrorCodeException extends RuntimeException
{
    public static final  int IO_EXCEPTION=1;
    public static final  int CHECKSUM_CHECK_FAILED=2;
    public static final int FM_UNAVAILABLE=3;
    public static final int FILENAME_DUPLICATION=4;
    public static final int ALLOCATE_NEW_BLOCK_FAILED=5;
    public static final int DUPLICATION_UNAVAILABLE=6;
    public static final int CURSOR_OUT_OF_BOUND=7;
    public static final int WRONG_WHERE=8;

    private static final Map<Integer,String> ErrorCodeMap= new HashMap<>();
    static {
        ErrorCodeMap.put(IO_EXCEPTION,"IO_EXCEPTION");
        ErrorCodeMap.put(CHECKSUM_CHECK_FAILED,"检验和失败");
        ErrorCodeMap.put(FM_UNAVAILABLE,"FM 不可用");
        ErrorCodeMap.put(FILENAME_DUPLICATION,"文件名重复");
        ErrorCodeMap.put(ALLOCATE_NEW_BLOCK_FAILED,"分配新块失败");
        ErrorCodeMap.put(DUPLICATION_UNAVAILABLE,"block和备份都不可用");
        ErrorCodeMap.put(CURSOR_OUT_OF_BOUND,"光标越界");
        ErrorCodeMap.put(WRONG_WHERE,"输入where错误");
    }
    public static String getErrorText(int errorCode){
        return ErrorCodeMap.getOrDefault(errorCode,"invalid");
    }
    private int errorCode;
    public ErrorCodeException(int errorCode){
        super(String.format("error code '%d' \"%s\" ",errorCode,getErrorText(errorCode)));
        this.errorCode=errorCode;
    }
    public int getErrorCode(){
        return errorCode;
    }
}

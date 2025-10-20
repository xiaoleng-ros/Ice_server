package liuyuyang.net.common.execption;

import com.qiniu.common.QiniuException;
import liuyuyang.net.common.utils.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    // 处理自定义的异常
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public Result<Object> customException(CustomException e) {
        e.printStackTrace();
        return Result.error(e.getCode(), e.getMessage());
    }

    // 处理七牛云Oss异常
    @ResponseBody
    @ExceptionHandler(QiniuException.class)
    public Result<Object> qiniuException(QiniuException e) {
        e.printStackTrace();
        return Result.error(e.code(), e.error());
    }

    // 处理所有异�?    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result<Object> exception(Exception e) {
        e.printStackTrace();
        return Result.error(e.getMessage());
    }
}

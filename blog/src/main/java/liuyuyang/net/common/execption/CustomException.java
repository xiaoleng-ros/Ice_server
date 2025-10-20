package liuyuyang.net.common.execption;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException {
    private Integer code;

    private String message;

    /**
     * 通过状态码和错误消息创建异常
     *
     * @param code    状态码
     * @param message 错误消息
     */
    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CustomException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustomException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}

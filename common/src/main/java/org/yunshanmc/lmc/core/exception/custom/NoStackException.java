package org.yunshanmc.lmc.core.exception.custom;

/**
 * 无异常栈的异常
 * <p>
 * 通过覆写fillInStackTrace来屏蔽异常栈，使得抛出异常的速度大大加快.<br>
 * <strong>仅用于部分'滥用'异常进行流程控制的地方，使用前思考是否有更好的处理方案.</strong>
 *
 * @author Yun-Shan
 */
@Deprecated
public class NoStackException extends RuntimeException {

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

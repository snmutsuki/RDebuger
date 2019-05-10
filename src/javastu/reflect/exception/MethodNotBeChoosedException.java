package javastu.reflect.exception;

public class MethodNotBeChoosedException extends Exception {
	@Override
	public String getMessage() {
		return "error！请先选择方法";
	}
}

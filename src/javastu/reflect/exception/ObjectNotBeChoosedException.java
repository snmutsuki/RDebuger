package javastu.reflect.exception;

public class ObjectNotBeChoosedException extends Exception {
	@Override
	public String getMessage() {
		return "error！请先选择对象";
	}
}

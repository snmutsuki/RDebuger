package javastu.reflect.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javastu.reflect.preconfig.MyCMD;
import javastu.reflect.preconfig.lib.ObjectStorage;

public class Test {

	public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
		PersonTest p1=new PersonTest("张三",22);
		PersonTest p2=new PersonTest("王五",22);
		
		MyCMD.getInstance().start();
		
	}

}

package javastu.reflect.preconfig;

import java.util.Scanner;

import javastu.reflect.preconfig.lib.ObjectStorage;
/**
 * 
 * @author AS
 *
 */
public class CMDService {
	/**
	 * 从storage中获取已经存在的对象
	 * @param cla 对象所属的类
	 * @param scan 一个输入流
	 * @return 所选择的对象
	 */
	public static Object getObjectFromStorage(Class<?> cla,Scanner scan){
		if(ObjectStorage.listAllObjectOfAClass(cla)){
			System.out.println("请输入您要选择的对象的序号");
			System.out.print("CMDService>");
			return ObjectStorage.getObjectByIndex(scan.nextInt(), cla);
		}else{
			System.out.println("不存在该类！");
			return null;
		}
	}
	/**
	 * 获得由指定字符串指示的指定基本类型的装箱型对象
	 * @param str 值的指定字符串
	 * @param cla 基本类型的Class
	 * @return
	 */
	public static Object getObjectOfBasicType(String str,Class<?> cla){
		if(cla.equals(Integer.TYPE)){
			return new Integer(str);
		}else if(cla.equals(Void.TYPE)){
			return null;
		}else if(cla.equals(Float.TYPE)){
			return new Float(str);
		}else if(cla.equals(Boolean.TYPE)){
			return new Boolean(str);
		}else if(cla.equals(Double.TYPE)){
			return new Double(str);
		}else if(cla.equals(Long.TYPE)){
			return new Long(str);
		}else if(cla.equals(Character.TYPE)){
			return str.toCharArray()[0];
		}else if(cla.equals(Byte.TYPE)){
			return new Byte(str);
		}else if(cla.equals(Short.TYPE)){
			return new Short(str);
		}
		return null;
	}
}

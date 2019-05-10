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
	 * ��storage�л�ȡ�Ѿ����ڵĶ���
	 * @param cla ������������
	 * @param scan һ��������
	 * @return ��ѡ��Ķ���
	 */
	public static Object getObjectFromStorage(Class<?> cla,Scanner scan){
		if(ObjectStorage.listAllObjectOfAClass(cla)){
			System.out.println("��������Ҫѡ��Ķ�������");
			System.out.print("CMDService>");
			return ObjectStorage.getObjectByIndex(scan.nextInt(), cla);
		}else{
			System.out.println("�����ڸ��࣡");
			return null;
		}
	}
	/**
	 * �����ָ���ַ���ָʾ��ָ���������͵�װ���Ͷ���
	 * @param str ֵ��ָ���ַ���
	 * @param cla �������͵�Class
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

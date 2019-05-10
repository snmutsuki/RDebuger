package javastu.reflect.preconfig.lib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ObjectStorage {
	private static List<List<Object>> classArr=new LinkedList<List<Object>>();//���ֶ����ڴ洢����
	private static List<Class<?>> classNameArr=new ArrayList<Class<?>>();//���ֶ����ڴ洢��������classArr�ֶε�����ͬ��
	private static int index=-1;
	//--------------------------------------------------------------------------------------------------------------------------------------
	
	public static boolean removeObjectByIndex(int index,Class<?> cla){
		if(classNameArr.contains(cla)){
			int claindex=-1;
			claindex=classNameArr.indexOf(cla);
			classArr.get(claindex).remove(index);
			return true;
		}
		return false;
	}
	
	
	/**
	 * �鿴ָ����Ķ���Ŀ�Ĵ�С
	 * @param cla
	 * @return
	 */
	public static int sizeOfObjectArr(Class<?> cla){
		if(classNameArr.contains(cla)){
			int claindex=-1;
			claindex=classNameArr.indexOf(cla);
			return classArr.get(claindex).size();
		}else{
			return -1;
		}
	}
	/**
	 * ��˽�з��������ڲ����ã����ڻ��ָ����洢����������������ڸ��࣬����ӣ�������������index
	 * @param cla obj.getClass
	 * @param index ���ص�����
	 * @return �Ƿ�Ϊ����
	 */
	private static boolean getClassIndex(Class<?> cla/*,Integer index*/){//�˴�ʹ��Integer��Ϊ�������ݻᷢ������Integer��ֵ��final���εģ�ÿ�θı䶼�ᴴ���µ�integer���з���
		if(classNameArr.contains(cla)){
			//System.out.println("����");
			index=classNameArr.indexOf(cla);
			//System.out.println("index="+index);
			return false;
		}else{
			//System.out.println("����");
			classNameArr.add(cla);
			index=classNameArr.size()-1;
			//System.out.println("index="+index);
			return true;
		}
	}
	/**
	 * ��������µĶ���
	 * @param obj
	 */
	public static boolean addNewObject(Object obj){
		List<Object> list=null;
		if(getClassIndex(obj.getClass())){
			//System.out.println(index);
			classArr.add(new ArrayList<Object>());
		}
		list=classArr.get(index);
		if(list.contains(obj)){index=-1; return false;}//equal�Ķ��󲻱��洢,��Ҫ������дһ�����õ�equal����
		list.add(obj);
		index=-1;
		return true;
		//System.out.println("�Ѵ洢����"+list.get(list.size()-1));
	}
	/**
	 * ��ӡ���е�ָ������Ѿ������Ķ���
	 * @param cla ָ��������
	 * @return �����Ƿ������classNameArr��
	 */
	public static boolean listAllObjectOfAClass(Class<?> cla){
		System.out.println("***************************");
		if(classNameArr.contains(cla)){
			int claindex=-1;
			claindex=classNameArr.indexOf(cla);
			int i=0;
			for(Object obj : classArr.get(claindex)){
				System.out.println(i+++":"+obj.toString());
			}
			System.out.println("***************************");
			return true;
		}
		System.out.println("***************************");
		return false;
	}
	/**
	 * ͨ��ָ���ַ���ƥ�����Ӧ�Ķ��󲢷��أ���������equal������toString����ʹ����ͬ����Դ���Ա���õ�ƥ��
	 * @param strָ�����ַ���
	 * @param claָ��������
	 * @return ƥ��Ķ���,δ�ҵ���û�и��෵��null
	 */
	public static Object getObjectByString(String str,Class<?> cla){
		if(classNameArr.contains(cla)){
			int claindex=-1;
			claindex=classNameArr.indexOf(cla);
			Object obj=null;
			for(Object objt : classArr.get(claindex)){
				if(objt.toString().equals(str)){
					obj=objt;
					return obj;
				}
			}	
		}
		return null;
	}
	/**
	 * ����ͨ��listAllObjectOfAClass����ʾ��index���ж�Ӧ�Ķ������ȡ
	 * @param index ��Ӧ�����index
	 * @param cla ��Ӧ���������
	 * @return ��Ӧ�Ķ����಻���ڷ���null
	 */
	public static Object getObjectByIndex(int index,Class<?> cla){
		if(classNameArr.contains(cla)){
			int claindex=-1;
			claindex=classNameArr.indexOf(cla);
			return classArr.get(claindex).get(index);
				
		}
		return null;
	}
}


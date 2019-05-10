package javastu.reflect.preconfig.lib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ObjectStorage {
	private static List<List<Object>> classArr=new LinkedList<List<Object>>();//本字段用于存储对象
	private static List<Class<?>> classNameArr=new ArrayList<Class<?>>();//本字段用于存储类名，与classArr字段的索引同步
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
	 * 查看指定类的对象的库的大小
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
	 * 本私有方法用于内部调用，用于获得指定类存储的索引，如果不存在该类，则添加，并将索引存入index
	 * @param cla obj.getClass
	 * @param index 返回的索引
	 * @return 是否为新类
	 */
	private static boolean getClassIndex(Class<?> cla/*,Integer index*/){//此处使用Integer作为参数传递会发生错误，Integer中值是final修饰的，每次改变都会创建新的integer进行返回
		if(classNameArr.contains(cla)){
			//System.out.println("旧类");
			index=classNameArr.indexOf(cla);
			//System.out.println("index="+index);
			return false;
		}else{
			//System.out.println("新类");
			classNameArr.add(cla);
			index=classNameArr.size()-1;
			//System.out.println("index="+index);
			return true;
		}
	}
	/**
	 * 用于添加新的对象
	 * @param obj
	 */
	public static boolean addNewObject(Object obj){
		List<Object> list=null;
		if(getClassIndex(obj.getClass())){
			//System.out.println(index);
			classArr.add(new ArrayList<Object>());
		}
		list=classArr.get(index);
		if(list.contains(obj)){index=-1; return false;}//equal的对象不被存储,需要对象重写一个可用的equal方法
		list.add(obj);
		index=-1;
		return true;
		//System.out.println("已存储对象"+list.get(list.size()-1));
	}
	/**
	 * 打印所有的指定类的已经创建的对象
	 * @param cla 指定的类名
	 * @return 该类是否存在于classNameArr中
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
	 * 通过指定字符串匹配相对应的对象并返回，建议对象的equal方法和toString方法使用相同的资源，以便更好的匹配
	 * @param str指定的字符串
	 * @param cla指定的类名
	 * @return 匹配的对象,未找到或没有该类返回null
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
	 * 根据通过listAllObjectOfAClass所显示的index进行对应的对象的提取
	 * @param index 对应对象的index
	 * @param cla 对应对象的类名
	 * @return 对应的对象，类不存在返回null
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


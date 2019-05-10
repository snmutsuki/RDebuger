package javastu.reflect.preconfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javastu.reflect.exception.ClassNotBeChoosedException;
import javastu.reflect.exception.FieldNotBeChoosedException;
import javastu.reflect.exception.MethodNotBeChoosedException;
import javastu.reflect.exception.ObjectNotBeChoosedException;
import javastu.reflect.preconfig.lib.ObjectStorage;
/**
 * 本类是MyCMD的核心类
 * @author AS
 * 
 *log：
 *2019/4/12 ver19.4.12
 *新：可以调用参数为基本类型的方法了，可以更改为基本类型的字段了，在chooseclass里添加了初始化方法，更改类将会进行初始化，添加了help命令可以获得所有的指令，添加了退出命令结束本线程
 *	~添加了打印状态码命令
 *问题：还不可以调用参数有非基本类型的方法，字段也不可以，目前使用将会出错。
 *预解决办法：可以添加创建新对象并存入storage的新命令
 *
 *2019/4/13 ver19.4.13
 *新：添加对String的匹配，可以使用字符串更改字段，可以使用字符串作为参数了，对错误的命令进行了提示，优化了体验
 *问题：是否可以添加Storage的功能，比如删除指定对象，查看容量等等
 *
 *2019/4/15 ver19.4.15
 *新：添加了从storage中获取对象的功能，在choose相关的命令中添加了list功能，优化了体验
 *
 *2019/4/17 ver19.4.17
 *新：添加了查看指定类的库种对象数量的指令
 *
 *2019/4/19 ver19.4.19
 *新：修改了ObjectStorage的addNewObject的返回值，false代表已有该对象,添加了两个新指令
 *new object by reflect:创建一个由无参构造函数创建的新的对象，并覆盖已经选择的对象
 *put this object to storage：将已选择的对象存入库中
 *
 *2019/4/23 ver19.4.23
 *添加了快捷命令模式优化了体验
 *
 *2019/4/25 ver19.4.25
 *修改了库中classArr为LinkedList的实现
 *在ObjectStorage中添加了删除指定索引对象的方法，并在MyCMD中添加了删除指定索引的指令
 *修改了可能会序号越界的bug，会出现明确提示了
 *
 *2019/4/29
 *修复了qc后输入错误指令，提示不正确的情况
 *修改MyCMD为枚举单例，强化了单例形态
 *
 *2019/5/6 ver19.5.6
 *修改了几处getMethod的问题
 *问题：是否可以将cgLib技术引入，是否可以将一部分内容改为枚举
 */
public enum MyCMD implements Runnable{
	INSTANCE;//本类的唯一不可变实例，使用getInstance可以获得
	//0xxx保留     xxxx(是否选择类，是否选择对象，是否选择方法，是否选择字段)      xxxxxxxx（各种命令）
	private static short flag=0x0000;//状态码
	private static Class<?> cla=null;//Class 类的变量用于保存选择的类
	private static Object obj=null;//用于保存选择的对象
	private Thread thread=null;//本类的核心线程
	private static boolean runFlag=true;//用于控制线程是否结束。为0线程停止
	private static Method myMethod=null;//用于保存选择的方法
	private static int numOfParaOfChoosedMethod=0;//用于保存选择方法的参数个数
	private static List<Class<?>> claArr=new ArrayList<Class<?>>();//用于保存需要选择的函数的参数所属的类的数组
	private static List<Object> paraArr=new ArrayList<Object>();//用于保存需要调用函数的参数数组
	private static Field field=null;//用于保存选择字段
	
	private MyCMD(){
		thread=new Thread(this,"MYCMD");
	}
	/*************************************************************command
	
	private static final short CHOOSECLASS=0x0800;选择类
	private static final short CHOOSEOBJECT=0x0400;选择对象
	private static final short CHOOSEMETHOD=0x0200;选择方法
	private static final short CHOOSEFIELD=0x0100;选择属性
	private static final short CHOOSEOBJECTBYINDEX=0x0402;通过index选择对象
	private static final short CHOOSEOBJECTBYSTRING=0x0403;通过toString返回值选择对象
	private static final short LISTALLOBJECT=0x0001;列出所有的对象
	private static final short LISTALLMETHOD=0x0004;列出所有的方法
	private static final short LISTALLFIELD=0x0007;列出所有的属性
	private static final short CHOOSEBYINDEX=0x0002;
	private static final short CHOOSEBYSTRING=0x0003;
	private static final short PRINTFLAG=0x0005;打印flag
	private static final short INVOKECHOOSEDMETHOD=0x0006;调用方法
	private static final short MODIFYCHOOSEDFILED=0x0008;修改属性值
	private static final short GETCHOOSEDFIELD=0x0009;得到属性值
	private static final short GETSIZEOFOBJECTSTORAGE=0x000A;得到库容量
	private static final short NEWOBJECTBYREFLECT=0x000B;实例化一个新的对象
	private static final short PUTTHISOBJECTTOSTORAGE=0x000C;将这个对象放入库中
	private static final short REMOVEOBJECTBYINDEX=0x000D;通过指定索引删除对象 
	private static final short QM=0x00fd;开启快捷命令模式
	private static final short HELP=0x00fe;帮助
	private static final short EXIT=0x00ff;退出
	****************************************************************/
	private static final String[] comm_str_arr=new String[]{//命令的字符串数组，序号与下个数组对应
			"choose_class",//0
			"choose_object",//1
			"choose_method",//2
			"choose_field",//3
			"choose_object_by_index",//4
			"choose_object_by_string",//5
			"list_all_object",//6
			"list_all_method",//7
			"list_all_field",//8
			"choose_by_index",//9
			"choose_by_string",//10
			"print_flag",//11
			"invoke_choosed_method",//12
			"modify_choosed_field",//13
			"get_choosed_field",//14
			"get_size_of_object_storage",//15
			"new_object_by_reflect",//16
			"put_this_object_to_storage",//17
			"remove_object_by_index",//18
			"qc",//19
			"help",//20
			"exit"//21
			};
	private static final String[] quickComm_str_arr=new String[]{//快捷命令数组
			"choc",//0
			"choo",//1
			"chom",//2
			"chof",//3
			"chooi",//4
			"choos",//5
			"liso",//6
			"lism",//7
			"lisf",//8
			"-i",//9
			"-s",//10
			"prif",//11
			"invm",//12
			"modf",//13
			"getf",//14
			"getsos",//15
			"newo",//16
			"putos",//17
			"remoi",//18
			"unqc",//19
			"-?",//20
			"exit"//21
	};
	private static final short[] comm_short_arr=new short[]{//命令的二进制码数组
			0x0800,//0
			0x0400,//1
			0x0200,//2
			0x0100,//3
			0x0402,//4
			0x0403,//5
			0x0001,//6
			0x0004,//7	
			0x0007,//8
			0x0002,//9
			0x0003,//10
			0x0005,//11
			0x0006,//12
			0x0008,//13
			0x0009,//14
			0x000a,//15
			0x000b,//16
			0x000c,//17
			0x000d,//18
			0x00fd,//19
			0x00fe,//20
			0x00ff//21
			};
	private static Scanner scan=new Scanner(System.in);//本类的全局Scanner
	private static boolean isQuickCommand=false;//是否是快捷命令模式
	
	public static MyCMD getInstance(){//用于获得本类的唯一实例
		return INSTANCE;
	}
	public static void init(){//初始化函数
		System.out.println("正在进行初始化......");
		cla=null;
		obj=null;
		myMethod=null;
		numOfParaOfChoosedMethod=0;
		claArr=new ArrayList<Class<?>>();
		paraArr=new ArrayList<Object>();
		field=null;
		flag&=0x0000;
		System.out.println("初始化完成.........");
	}
	public void start(){//开启线程，需要从外部调用，以开启MyCMD
		thread.start();
	}

	@Override
	public void run() {//被override的线程run方法
		while(runFlag){
			getCommand();
		}
	}
	/**
	 * 获得命令
	 */
	private static void getCommand(){
		String cmd=null;
		System.out.print("MyCMD>");
		cmd=scan.next();
		commandAnalysize(cmd);
	}
	/**
	 * 命令的分析类
	 * @param comm命令的字符串
	 */
	private static void commandAnalysize(String comm){
		if(isQuickCommand){
			for(int i=0;i<quickComm_str_arr.length;++i){
				if(quickComm_str_arr[i].equals(comm)){				
					doCommandByShort(comm_short_arr[i]);
					return;
				}
			}
			System.out.println("命令无效！您可以使用-?指令查看所有已存在的命令");
		}else{
			for(int i=0;i<comm_str_arr.length;++i){
				if(comm_str_arr[i].equals(comm)){				
					doCommandByShort(comm_short_arr[i]);
					return;
				}
			}
			System.out.println("命令无效！您可以使用help指令查看所有已存在的命令");
		}		
		
	}
	/**
	 * 根据命令码分析并调用对应的方法
	 * @param comm 命令码
	 */
	private static void doCommandByShort(short comm){
		try{
			byte firstComm=(byte) (comm>>8);//命令的前8个bit
			byte lastComm=(byte)comm;//命令的后8个bit
			if(firstComm!=0){
				if(firstComm==8){
					chooseClass();
				}
				if(firstComm==4){
					if(lastComm==0){//此处判断用于choose_object_by_index,choose_object_by_string的快捷命令
						chooseObject(false);
					}else{
						chooseObject(true);
					}
				}
				if(firstComm==2){
					chooseMethod();
				}
				if(firstComm==1){
					chooseField();
				}
			}
			
			//**************************
			if((lastComm&0xff)==0x01){
				listAllObject();
				return;
			}
			if((lastComm&0xff)==0x02){
				chooseObjectByIndex();
				return;
			}
			if((lastComm&0xff)==0x03){
				chooseObjectByString();
				return;
			}
			if((lastComm&0xff)==0x04){
				listAllMethod();
				return;
			}
			if((lastComm&0xff)==0x05){
				printFlag();
				return;
			}
			if((lastComm&0xff)==0x06){
				invokeChoosedMethod();
				return;
			}
			if((lastComm&0xff)==0x07){
				listAllField();
				return;
			}
			if((lastComm&0xff)==0x08){
				modifyChoosedField();
				return;
			}
			if((lastComm&0xff)==0x09){
				getChoosedField();
				return;
			}
			if((lastComm&0xff)==0x0a){
				getSizeOfObjectStorage();
				return;
			}
			if((lastComm&0xff)==0x0b){
				newObjectByReflect();
				return;
			}
			if((lastComm&0xff)==0x0c){
				putThisObjectToStorage();
				return;
			}
			if((lastComm&0xff)==0x0d){
				removeObjectByIndex();
				return;
			}
			if((lastComm&0xff)==0xfd){
				turnIsQuickComm();
				return;
			}
			if((lastComm&0xff)==0xfe){
				help();
				return;
			}
			if((lastComm&0xff)==0xff){
				exit();
				return;
			}
			
			
		}catch(NullPointerException e){
			System.out.println("error!空指针异常！");
		}catch(ClassNotBeChoosedException e){
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("error!该类未被找到");
		} catch (NoSuchMethodException e) {
			System.out.println("error!该方法未被找到,请检查方法名，参数，或则该方法为非public的");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ObjectNotBeChoosedException e) {
			System.out.println(e.getMessage());
		} catch (MethodNotBeChoosedException e) {
			System.out.println(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("error!参数错误");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			System.out.println("error!没有该属性");
		} catch (FieldNotBeChoosedException e) {
			System.out.println(e.getMessage());
		} catch (InstantiationException e) {
			System.out.println("error!实例化失败！");
		} catch (IndexOutOfBoundsException e){
			System.out.println("error!序号错误！");
		}
		
		
		
	}
	
	
	/**
	 * 选择类
	 * 如果在选择了类的基础上更改类，将会将某些设置初始化
	 */
	private static void chooseClass(){
		if((flag&0x0800)==0x0800){//如果已经选择了类
			System.out.println("检测到您已经选择了类，是否重新选择？将会将部分设置初始化，1为是，0为否");
			System.out.print("MyCMD>Choose Class>");
			if(scan.nextInt()==1){
				init();
			}else{
				return;
			}
		}
		System.out.println("请输入需要选择的类的全名，如：java.lang.String");
		System.out.print("MyCMD>Choose Class>");
		String cla_name=scan.next();
		try {
			cla=Class.forName(cla_name);
		} catch (ClassNotFoundException e) {
			System.out.println("error!一个无效的类名");
			return;
		}
		System.out.println("Choose completed");
		flag |= 0x0800;
	}
	/**
	 * 选择对象
	 * @param skip_flag 是否跳过某些语句
	 * @throws ClassNotBeChoosedException
	 */
	private static void chooseObject(boolean skip_flag) throws ClassNotBeChoosedException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		if(skip_flag) return;//如果是复合命令，下列代码不会执行
		System.out.println("choose_by_index  or  choose _by_string?");
		System.out.print("MyCMD>Choose Object>");
		commandAnalysize(scan.next());
	}
	/**
	 * 列出所有的对象
	 * @throws ClassNotBeChoosedException
	 */
	private static void listAllObject() throws ClassNotBeChoosedException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		if(!ObjectStorage.listAllObjectOfAClass(cla)){
			System.out.println("该类暂无对象被存储");
		}
	}
	/**
	 * 快捷命令：通过index选择对应对象
	 * @throws ClassNotBeChoosedException 
	 */
	private static void chooseObjectByIndex() throws ClassNotBeChoosedException{
		listAllObject();
		System.out.println("输入所选择对象的对应索引");
		System.out.print("MyCMD>Choose Object>Choose By Index>");
		obj=ObjectStorage.getObjectByIndex(scan.nextInt(), cla);
		if(obj==null){
			System.out.println("error!未知的错误！选择失败！");
		}else{
			System.out.println("choose completed");
			flag |=0x0400;
		}
	}
	/**
	 * 快捷命令：通过toString的表达形式选择对应对象
	 * @throws ClassNotBeChoosedException 
	 */
	private static void chooseObjectByString() throws ClassNotBeChoosedException{
		listAllObject();
		System.out.println("输入所选择对象的对应toString表达式");
		System.out.print("MyCMD>Choose Object>Choose By String>");
		obj=ObjectStorage.getObjectByString(scan.next(), cla);
		if(obj==null){
			System.out.println("error!未知的错误！选择失败！");
		}else{
			System.out.println("choose completed");
			flag |=0x0400;
		}
	}
	/**
	 * 列出所选择的类的所有的方法
	 * @throws ClassNotBeChoosedException
	 */
	private static void listAllMethod() throws ClassNotBeChoosedException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		Method[] method=cla.getDeclaredMethods();
		System.out.println("******************");
        for(Method me:method){
        	System.out.println(me.toString());
        }
        System.out.println("******************");
	}
	/**
	 * 选择方法,只限公共的方法
	 * @throws ClassNotBeChoosedException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ObjectNotBeChoosedException
	 */
	private static void chooseMethod() throws ClassNotBeChoosedException, ClassNotFoundException, 
	NoSuchMethodException, SecurityException, ObjectNotBeChoosedException{
		listAllMethod();
		System.out.println("是静态方法吗？1为是，0为否");
		System.out.print("MyCMD>Choose Method>");
		if(scan.nextInt()==0){
			if((flag&0x0c00)!=0x0c00){
				throw new ObjectNotBeChoosedException();
			}
		}
		System.out.println("请输入方法名");
		System.out.print("MyCMD>Choose Method>");
		String temp_str1=scan.next();
		System.out.println("请输入参数个数");
		System.out.print("MyCMD>Choose Method>");
		numOfParaOfChoosedMethod=scan.nextInt();
		if(numOfParaOfChoosedMethod!=0){
			System.out.println("请输入参数所属的类名，以空格隔开，基本类型用j.xxx表示，如j.int");
			System.out.print("MyCMD>Choose Method>");
			for(int i=0;i<numOfParaOfChoosedMethod;++i){
				String temp_str2=scan.next();
				if(temp_str2.substring(0,2).equals("j.")){//判断为基本类型	
					temp_str2=temp_str2.substring(2);
					if(temp_str2.equals("int")){
						claArr.add(Integer.TYPE);
					}else if(temp_str2.equals("float")){
						claArr.add(Float.TYPE);
					}else if(temp_str2.equals("boolean")){
						claArr.add(Boolean.TYPE);
					}else if(temp_str2.equals("double")){
						claArr.add(Double.TYPE);
					}else if(temp_str2.equals("long")){
						claArr.add(Long.TYPE);
					}else if(temp_str2.equals("char")){
						claArr.add(Character.TYPE);
					}else if(temp_str2.equals("byte")){
						claArr.add(Byte.TYPE);
					}else if(temp_str2.equals("short")){
						claArr.add(Short.TYPE);
					}					
				}else{
					claArr.add(Class.forName(temp_str2));
				}
			}
			myMethod=cla.getDeclaredMethod(temp_str1, claArr.toArray(new Class<?>[0]));
		}else{
			myMethod=cla.getDeclaredMethod(temp_str1, new Class<?>[0]);
		}
			
		flag |=0x0200;
		System.out.println("choose completed");
	}
	/**
	 * 打印状态码，2进制
	 */
	private static void printFlag(){
		char[] arr=new char[16];
		arr[0]='0';		
		for(int i=1,res=flag;i<arr.length;++i){
			if((res-Math.pow(2,arr.length-i-1))>=0){
				arr[i]='1';
				res-=Math.pow(2,arr.length-i-1);
			}else{
				arr[i]='0';
			}
		}
		System.out.print("flag=");
		for(char ch : arr){
			System.out.print(ch);
		}
		System.out.println();
	}
	/**
	 * 调用选择的函数
	 * @throws MethodNotBeChoosedException
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private static void invokeChoosedMethod() throws MethodNotBeChoosedException, InterruptedException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if((flag&0x0200) != 0x0200){
			throw new MethodNotBeChoosedException();
		}
		if(numOfParaOfChoosedMethod!=0){
			System.out.println("请输入参数，共"+numOfParaOfChoosedMethod+"个");
			for(int i=0;i<numOfParaOfChoosedMethod;++i){
				System.out.println("第"+(i+1)+"个参数，类型为："+claArr.get(i));				
				if(claArr.get(i).isPrimitive()){	
					System.out.print("MyCMD>Invoke Choosed Method>");
					paraArr.add(CMDService.getObjectOfBasicType(scan.next(),claArr.get(i)));
				}else if(claArr.get(i).equals(String.class)){//如果为String，也可以直接输入
					System.out.print("MyCMD>Invoke Choosed Method>");
					paraArr.add(scan.next());
				}else{
					System.out.println("该参数为非基本类型，是否从Storage中获得已保存的对象？1为是，0为退出此命令");
					System.out.print("MyCMD>Invoke Choosed Method>");
					if(scan.nextInt()==1){
						Object obj_temp1=CMDService.getObjectFromStorage(claArr.get(i),scan);
						if(obj_temp1==null){
							paraArr=new ArrayList<Object>();
							return ;
						}
						paraArr.add(paraArr);					
					}else{
						return;
					}
				}
				System.out.println("Add Successful");					
			}
		}	
		System.out.println("调用开始****************************");
		if((flag&0x0400)==0x0400){
			myMethod.invoke(obj,paraArr.toArray(new Object[0]));
		}else{
			myMethod.invoke(null, paraArr.toArray(new Object[0]));
		}
		System.out.println("调用结束****************************");
		System.out.println("Invoke Completed");
	}
	/**
	 * 选择字段
	 * @throws ClassNotBeChoosedException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	private static void chooseField() throws ClassNotBeChoosedException, NoSuchFieldException, SecurityException{
		listAllField();
		System.out.println("请输入要选择的字段名");
		System.out.print("MyCMD>Choose Field>");
		field=cla.getDeclaredField(scan.next());
		field.setAccessible(true);//安全检查设为否，可以获得私有属性
		System.out.println("Choose Complete");
		flag |= 0x0100;
	}
	/**
	 * 列出该类的所有属性
	 * @throws ClassNotBeChoosedException 
	 */
	private static void listAllField() throws ClassNotBeChoosedException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		System.out.println("****************************");
		Field[] fieldArr=cla.getDeclaredFields();
		for(Field f :fieldArr){
			System.out.println(f);
		}
		System.out.println("****************************");
	}
	/**
	 * 更改选择的属性
	 * @throws FieldNotBeChoosedException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private static void modifyChoosedField() throws FieldNotBeChoosedException, IllegalArgumentException, IllegalAccessException{
		if((flag&0x0100)!=0x0100){
			throw new FieldNotBeChoosedException();
		}
		if(!field.getType().isPrimitive()){//判断为非基本类型
			if(field.getType().equals(String.class)){//如果为String，也可以直接输入
				System.out.println("请输入要进行更改的新值");
				System.out.print("MyCMD>Modify Choosed Field>");
				if((flag&0x0400)==0x0400){
					field.set(obj,scan.next());
				}else{
					field.set(null,scan.next());
				}
				System.out.println("Modify Completed");
				return;
			}
			System.out.println("该字段为非基本类型,是否从Storage中获得已保存的对象？1为是，0为退出此命令");	
			System.out.print("MyCMD>Modify Choosed Field>");
			if(scan.nextInt()==1){
				Object obj_temp1=CMDService.getObjectFromStorage(field.getType(),scan);
				if(obj_temp1==null){
					return;
				}
				if((flag&0x0400)==0x0400){
					field.set(obj,obj_temp1);
				}else{
					field.set(null,obj_temp1);
				}	
				System.out.println("Modify Completed");
			}else{
				return;
			}
		}else{
			System.out.println("请输入要进行更改的新值");
			System.out.print("MyCMD>Modify Choosed Field>");
			if((flag&0x0400)==0x0400){
				field.set(obj,CMDService.getObjectOfBasicType(scan.next(), field.getType()));
			}else{
				field.set(null,CMDService.getObjectOfBasicType(scan.next(), field.getType()));
			}
			System.out.println("Modify Completed");
		}	
		
	}
	/**
	 * 获得选中的字段值.toString
	 * @throws FieldNotBeChoosedException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private static void getChoosedField() throws FieldNotBeChoosedException, IllegalArgumentException, IllegalAccessException{
		if((flag&0x0100)!=0x0100){
			throw new FieldNotBeChoosedException();
		}
		if((flag&0x0400)==0x0400){
			System.out.println(field.getName()+"-->"+field.get(obj));
		}else{
			System.out.println(field.getName()+"-->"+field.get(null));
		}
		
	}
	/**
	 * 获得存储指定类的对象arr的大小
	 * @throws ClassNotBeChoosedException 
	 */
	private static void getSizeOfObjectStorage() throws ClassNotBeChoosedException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		int size=ObjectStorage.sizeOfObjectArr(cla);
		if(size==-1){
			System.out.println("该类不存在于库");
		}else{
			System.out.println("Size:"+size);
		}
	}
	
	private static void putThisObjectToStorage() throws ObjectNotBeChoosedException{
		if((flag&0x0400)!=0x0400){
			throw new ObjectNotBeChoosedException();
		}
		if(ObjectStorage.addNewObject(obj)){
			System.out.println("添加完成");
		}else{
			System.out.println("此对象已存在");
		}
	}
	
	private static void newObjectByReflect() throws ClassNotBeChoosedException, InstantiationException, IllegalAccessException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		System.out.println("警告！\n1、使用该方法创建的对象属性为初始化属性，不经修改后使用会造成不确定的后果！\n2、请确保该类有公有的无参构造函数！\n3、已选择的对象将会被覆盖，以便您进一步修改\n如果继续创建，请输入1，否则输入0");
		System.out.print("MyCMD>New Object By Reflect>");
		if(scan.nextInt()==1){
			obj=cla.newInstance();
			flag |=0x0400;
			System.out.println("create complete");
		}
		return ;
	}
	//反转qc状态
	private static void turnIsQuickComm(){
		isQuickCommand=!isQuickCommand;
		if(isQuickCommand){
			System.out.println("已经开启快捷命令模式，通过-？可以查看命令对应项");
		}else{
			System.out.println("已经回到正常命令模式");
		}
	}
	
	private static void removeObjectByIndex() throws ClassNotBeChoosedException{
		listAllObject();//包含了处理未选择类的异常
		System.out.println("输入所需要删除的对象的对应索引");
		System.out.print("MyCMD>Remove Object By Index>");
		if(ObjectStorage.removeObjectByIndex(scan.nextInt(), cla)){
			listAllObject();
			System.out.println("delete complete！");
		}else{
			System.out.println("delete false");
		}
	}
	
	/**
	 * 打印所有命令
	 */
	private static void help(){
		System.out.println("*****************************");
		int i=0;
		for(String str : comm_str_arr){
			System.out.println(str);
			if(isQuickCommand){
				System.out.println("-------------------------------"+quickComm_str_arr[i]);
			}
			++i;
		}
		System.out.println("*****************************");
	}
	/**
	 * 退出关闭MyCMD
	 */
	private static void exit(){
		runFlag=false;
		scan.close();
	}
	
	public static void infoOfThisClass(){
		System.out.println("This is MyCMD@Author:mutsuki");
	}
	@Override
	public String toString() {
		return "This is MyCMD";
	}
}

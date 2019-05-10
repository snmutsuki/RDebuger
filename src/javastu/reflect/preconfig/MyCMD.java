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
 * ������MyCMD�ĺ�����
 * @author AS
 * 
 *log��
 *2019/4/12 ver19.4.12
 *�£����Ե��ò���Ϊ�������͵ķ����ˣ����Ը���Ϊ�������͵��ֶ��ˣ���chooseclass������˳�ʼ�������������ཫ����г�ʼ���������help������Ի�����е�ָ�������˳�����������߳�
 *	~����˴�ӡ״̬������
 *���⣺�������Ե��ò����зǻ������͵ķ������ֶ�Ҳ�����ԣ�Ŀǰʹ�ý������
 *Ԥ����취��������Ӵ����¶��󲢴���storage��������
 *
 *2019/4/13 ver19.4.13
 *�£���Ӷ�String��ƥ�䣬����ʹ���ַ��������ֶΣ�����ʹ���ַ�����Ϊ�����ˣ��Դ���������������ʾ���Ż�������
 *���⣺�Ƿ�������Storage�Ĺ��ܣ�����ɾ��ָ�����󣬲鿴�����ȵ�
 *
 *2019/4/15 ver19.4.15
 *�£�����˴�storage�л�ȡ����Ĺ��ܣ���choose��ص������������list���ܣ��Ż�������
 *
 *2019/4/17 ver19.4.17
 *�£�����˲鿴ָ����Ŀ��ֶ���������ָ��
 *
 *2019/4/19 ver19.4.19
 *�£��޸���ObjectStorage��addNewObject�ķ���ֵ��false�������иö���,�����������ָ��
 *new object by reflect:����һ�����޲ι��캯���������µĶ��󣬲������Ѿ�ѡ��Ķ���
 *put this object to storage������ѡ��Ķ���������
 *
 *2019/4/23 ver19.4.23
 *����˿������ģʽ�Ż�������
 *
 *2019/4/25 ver19.4.25
 *�޸��˿���classArrΪLinkedList��ʵ��
 *��ObjectStorage�������ɾ��ָ����������ķ���������MyCMD�������ɾ��ָ��������ָ��
 *�޸��˿��ܻ����Խ���bug���������ȷ��ʾ��
 *
 *2019/4/29
 *�޸���qc���������ָ���ʾ����ȷ�����
 *�޸�MyCMDΪö�ٵ�����ǿ���˵�����̬
 *
 *2019/5/6 ver19.5.6
 *�޸��˼���getMethod������
 *���⣺�Ƿ���Խ�cgLib�������룬�Ƿ���Խ�һ�������ݸ�Ϊö��
 */
public enum MyCMD implements Runnable{
	INSTANCE;//�����Ψһ���ɱ�ʵ����ʹ��getInstance���Ի��
	//0xxx����     xxxx(�Ƿ�ѡ���࣬�Ƿ�ѡ������Ƿ�ѡ�񷽷����Ƿ�ѡ���ֶ�)      xxxxxxxx���������
	private static short flag=0x0000;//״̬��
	private static Class<?> cla=null;//Class ��ı������ڱ���ѡ�����
	private static Object obj=null;//���ڱ���ѡ��Ķ���
	private Thread thread=null;//����ĺ����߳�
	private static boolean runFlag=true;//���ڿ����߳��Ƿ������Ϊ0�߳�ֹͣ
	private static Method myMethod=null;//���ڱ���ѡ��ķ���
	private static int numOfParaOfChoosedMethod=0;//���ڱ���ѡ�񷽷��Ĳ�������
	private static List<Class<?>> claArr=new ArrayList<Class<?>>();//���ڱ�����Ҫѡ��ĺ����Ĳ����������������
	private static List<Object> paraArr=new ArrayList<Object>();//���ڱ�����Ҫ���ú����Ĳ�������
	private static Field field=null;//���ڱ���ѡ���ֶ�
	
	private MyCMD(){
		thread=new Thread(this,"MYCMD");
	}
	/*************************************************************command
	
	private static final short CHOOSECLASS=0x0800;ѡ����
	private static final short CHOOSEOBJECT=0x0400;ѡ�����
	private static final short CHOOSEMETHOD=0x0200;ѡ�񷽷�
	private static final short CHOOSEFIELD=0x0100;ѡ������
	private static final short CHOOSEOBJECTBYINDEX=0x0402;ͨ��indexѡ�����
	private static final short CHOOSEOBJECTBYSTRING=0x0403;ͨ��toString����ֵѡ�����
	private static final short LISTALLOBJECT=0x0001;�г����еĶ���
	private static final short LISTALLMETHOD=0x0004;�г����еķ���
	private static final short LISTALLFIELD=0x0007;�г����е�����
	private static final short CHOOSEBYINDEX=0x0002;
	private static final short CHOOSEBYSTRING=0x0003;
	private static final short PRINTFLAG=0x0005;��ӡflag
	private static final short INVOKECHOOSEDMETHOD=0x0006;���÷���
	private static final short MODIFYCHOOSEDFILED=0x0008;�޸�����ֵ
	private static final short GETCHOOSEDFIELD=0x0009;�õ�����ֵ
	private static final short GETSIZEOFOBJECTSTORAGE=0x000A;�õ�������
	private static final short NEWOBJECTBYREFLECT=0x000B;ʵ����һ���µĶ���
	private static final short PUTTHISOBJECTTOSTORAGE=0x000C;���������������
	private static final short REMOVEOBJECTBYINDEX=0x000D;ͨ��ָ������ɾ������ 
	private static final short QM=0x00fd;�����������ģʽ
	private static final short HELP=0x00fe;����
	private static final short EXIT=0x00ff;�˳�
	****************************************************************/
	private static final String[] comm_str_arr=new String[]{//������ַ������飬������¸������Ӧ
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
	private static final String[] quickComm_str_arr=new String[]{//�����������
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
	private static final short[] comm_short_arr=new short[]{//����Ķ�����������
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
	private static Scanner scan=new Scanner(System.in);//�����ȫ��Scanner
	private static boolean isQuickCommand=false;//�Ƿ��ǿ������ģʽ
	
	public static MyCMD getInstance(){//���ڻ�ñ����Ψһʵ��
		return INSTANCE;
	}
	public static void init(){//��ʼ������
		System.out.println("���ڽ��г�ʼ��......");
		cla=null;
		obj=null;
		myMethod=null;
		numOfParaOfChoosedMethod=0;
		claArr=new ArrayList<Class<?>>();
		paraArr=new ArrayList<Object>();
		field=null;
		flag&=0x0000;
		System.out.println("��ʼ�����.........");
	}
	public void start(){//�����̣߳���Ҫ���ⲿ���ã��Կ���MyCMD
		thread.start();
	}

	@Override
	public void run() {//��override���߳�run����
		while(runFlag){
			getCommand();
		}
	}
	/**
	 * �������
	 */
	private static void getCommand(){
		String cmd=null;
		System.out.print("MyCMD>");
		cmd=scan.next();
		commandAnalysize(cmd);
	}
	/**
	 * ����ķ�����
	 * @param comm������ַ���
	 */
	private static void commandAnalysize(String comm){
		if(isQuickCommand){
			for(int i=0;i<quickComm_str_arr.length;++i){
				if(quickComm_str_arr[i].equals(comm)){				
					doCommandByShort(comm_short_arr[i]);
					return;
				}
			}
			System.out.println("������Ч��������ʹ��-?ָ��鿴�����Ѵ��ڵ�����");
		}else{
			for(int i=0;i<comm_str_arr.length;++i){
				if(comm_str_arr[i].equals(comm)){				
					doCommandByShort(comm_short_arr[i]);
					return;
				}
			}
			System.out.println("������Ч��������ʹ��helpָ��鿴�����Ѵ��ڵ�����");
		}		
		
	}
	/**
	 * ������������������ö�Ӧ�ķ���
	 * @param comm ������
	 */
	private static void doCommandByShort(short comm){
		try{
			byte firstComm=(byte) (comm>>8);//�����ǰ8��bit
			byte lastComm=(byte)comm;//����ĺ�8��bit
			if(firstComm!=0){
				if(firstComm==8){
					chooseClass();
				}
				if(firstComm==4){
					if(lastComm==0){//�˴��ж�����choose_object_by_index,choose_object_by_string�Ŀ������
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
			System.out.println("error!��ָ���쳣��");
		}catch(ClassNotBeChoosedException e){
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("error!����δ���ҵ�");
		} catch (NoSuchMethodException e) {
			System.out.println("error!�÷���δ���ҵ�,���鷽����������������÷���Ϊ��public��");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ObjectNotBeChoosedException e) {
			System.out.println(e.getMessage());
		} catch (MethodNotBeChoosedException e) {
			System.out.println(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("error!��������");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			System.out.println("error!û�и�����");
		} catch (FieldNotBeChoosedException e) {
			System.out.println(e.getMessage());
		} catch (InstantiationException e) {
			System.out.println("error!ʵ����ʧ�ܣ�");
		} catch (IndexOutOfBoundsException e){
			System.out.println("error!��Ŵ���");
		}
		
		
		
	}
	
	
	/**
	 * ѡ����
	 * �����ѡ������Ļ����ϸ����࣬���ὫĳЩ���ó�ʼ��
	 */
	private static void chooseClass(){
		if((flag&0x0800)==0x0800){//����Ѿ�ѡ������
			System.out.println("��⵽���Ѿ�ѡ�����࣬�Ƿ�����ѡ�񣿽��Ὣ�������ó�ʼ����1Ϊ�ǣ�0Ϊ��");
			System.out.print("MyCMD>Choose Class>");
			if(scan.nextInt()==1){
				init();
			}else{
				return;
			}
		}
		System.out.println("��������Ҫѡ������ȫ�����磺java.lang.String");
		System.out.print("MyCMD>Choose Class>");
		String cla_name=scan.next();
		try {
			cla=Class.forName(cla_name);
		} catch (ClassNotFoundException e) {
			System.out.println("error!һ����Ч������");
			return;
		}
		System.out.println("Choose completed");
		flag |= 0x0800;
	}
	/**
	 * ѡ�����
	 * @param skip_flag �Ƿ�����ĳЩ���
	 * @throws ClassNotBeChoosedException
	 */
	private static void chooseObject(boolean skip_flag) throws ClassNotBeChoosedException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		if(skip_flag) return;//����Ǹ���������д��벻��ִ��
		System.out.println("choose_by_index  or  choose _by_string?");
		System.out.print("MyCMD>Choose Object>");
		commandAnalysize(scan.next());
	}
	/**
	 * �г����еĶ���
	 * @throws ClassNotBeChoosedException
	 */
	private static void listAllObject() throws ClassNotBeChoosedException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		if(!ObjectStorage.listAllObjectOfAClass(cla)){
			System.out.println("�������޶��󱻴洢");
		}
	}
	/**
	 * ������ͨ��indexѡ���Ӧ����
	 * @throws ClassNotBeChoosedException 
	 */
	private static void chooseObjectByIndex() throws ClassNotBeChoosedException{
		listAllObject();
		System.out.println("������ѡ�����Ķ�Ӧ����");
		System.out.print("MyCMD>Choose Object>Choose By Index>");
		obj=ObjectStorage.getObjectByIndex(scan.nextInt(), cla);
		if(obj==null){
			System.out.println("error!δ֪�Ĵ���ѡ��ʧ�ܣ�");
		}else{
			System.out.println("choose completed");
			flag |=0x0400;
		}
	}
	/**
	 * ������ͨ��toString�ı����ʽѡ���Ӧ����
	 * @throws ClassNotBeChoosedException 
	 */
	private static void chooseObjectByString() throws ClassNotBeChoosedException{
		listAllObject();
		System.out.println("������ѡ�����Ķ�ӦtoString���ʽ");
		System.out.print("MyCMD>Choose Object>Choose By String>");
		obj=ObjectStorage.getObjectByString(scan.next(), cla);
		if(obj==null){
			System.out.println("error!δ֪�Ĵ���ѡ��ʧ�ܣ�");
		}else{
			System.out.println("choose completed");
			flag |=0x0400;
		}
	}
	/**
	 * �г���ѡ���������еķ���
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
	 * ѡ�񷽷�,ֻ�޹����ķ���
	 * @throws ClassNotBeChoosedException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ObjectNotBeChoosedException
	 */
	private static void chooseMethod() throws ClassNotBeChoosedException, ClassNotFoundException, 
	NoSuchMethodException, SecurityException, ObjectNotBeChoosedException{
		listAllMethod();
		System.out.println("�Ǿ�̬������1Ϊ�ǣ�0Ϊ��");
		System.out.print("MyCMD>Choose Method>");
		if(scan.nextInt()==0){
			if((flag&0x0c00)!=0x0c00){
				throw new ObjectNotBeChoosedException();
			}
		}
		System.out.println("�����뷽����");
		System.out.print("MyCMD>Choose Method>");
		String temp_str1=scan.next();
		System.out.println("�������������");
		System.out.print("MyCMD>Choose Method>");
		numOfParaOfChoosedMethod=scan.nextInt();
		if(numOfParaOfChoosedMethod!=0){
			System.out.println("����������������������Կո����������������j.xxx��ʾ����j.int");
			System.out.print("MyCMD>Choose Method>");
			for(int i=0;i<numOfParaOfChoosedMethod;++i){
				String temp_str2=scan.next();
				if(temp_str2.substring(0,2).equals("j.")){//�ж�Ϊ��������	
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
	 * ��ӡ״̬�룬2����
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
	 * ����ѡ��ĺ���
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
			System.out.println("�������������"+numOfParaOfChoosedMethod+"��");
			for(int i=0;i<numOfParaOfChoosedMethod;++i){
				System.out.println("��"+(i+1)+"������������Ϊ��"+claArr.get(i));				
				if(claArr.get(i).isPrimitive()){	
					System.out.print("MyCMD>Invoke Choosed Method>");
					paraArr.add(CMDService.getObjectOfBasicType(scan.next(),claArr.get(i)));
				}else if(claArr.get(i).equals(String.class)){//���ΪString��Ҳ����ֱ������
					System.out.print("MyCMD>Invoke Choosed Method>");
					paraArr.add(scan.next());
				}else{
					System.out.println("�ò���Ϊ�ǻ������ͣ��Ƿ��Storage�л���ѱ���Ķ���1Ϊ�ǣ�0Ϊ�˳�������");
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
		System.out.println("���ÿ�ʼ****************************");
		if((flag&0x0400)==0x0400){
			myMethod.invoke(obj,paraArr.toArray(new Object[0]));
		}else{
			myMethod.invoke(null, paraArr.toArray(new Object[0]));
		}
		System.out.println("���ý���****************************");
		System.out.println("Invoke Completed");
	}
	/**
	 * ѡ���ֶ�
	 * @throws ClassNotBeChoosedException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	private static void chooseField() throws ClassNotBeChoosedException, NoSuchFieldException, SecurityException{
		listAllField();
		System.out.println("������Ҫѡ����ֶ���");
		System.out.print("MyCMD>Choose Field>");
		field=cla.getDeclaredField(scan.next());
		field.setAccessible(true);//��ȫ�����Ϊ�񣬿��Ի��˽������
		System.out.println("Choose Complete");
		flag |= 0x0100;
	}
	/**
	 * �г��������������
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
	 * ����ѡ�������
	 * @throws FieldNotBeChoosedException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private static void modifyChoosedField() throws FieldNotBeChoosedException, IllegalArgumentException, IllegalAccessException{
		if((flag&0x0100)!=0x0100){
			throw new FieldNotBeChoosedException();
		}
		if(!field.getType().isPrimitive()){//�ж�Ϊ�ǻ�������
			if(field.getType().equals(String.class)){//���ΪString��Ҳ����ֱ������
				System.out.println("������Ҫ���и��ĵ���ֵ");
				System.out.print("MyCMD>Modify Choosed Field>");
				if((flag&0x0400)==0x0400){
					field.set(obj,scan.next());
				}else{
					field.set(null,scan.next());
				}
				System.out.println("Modify Completed");
				return;
			}
			System.out.println("���ֶ�Ϊ�ǻ�������,�Ƿ��Storage�л���ѱ���Ķ���1Ϊ�ǣ�0Ϊ�˳�������");	
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
			System.out.println("������Ҫ���и��ĵ���ֵ");
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
	 * ���ѡ�е��ֶ�ֵ.toString
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
	 * ��ô洢ָ����Ķ���arr�Ĵ�С
	 * @throws ClassNotBeChoosedException 
	 */
	private static void getSizeOfObjectStorage() throws ClassNotBeChoosedException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		int size=ObjectStorage.sizeOfObjectArr(cla);
		if(size==-1){
			System.out.println("���಻�����ڿ�");
		}else{
			System.out.println("Size:"+size);
		}
	}
	
	private static void putThisObjectToStorage() throws ObjectNotBeChoosedException{
		if((flag&0x0400)!=0x0400){
			throw new ObjectNotBeChoosedException();
		}
		if(ObjectStorage.addNewObject(obj)){
			System.out.println("������");
		}else{
			System.out.println("�˶����Ѵ���");
		}
	}
	
	private static void newObjectByReflect() throws ClassNotBeChoosedException, InstantiationException, IllegalAccessException{
		if((flag&0x0800) != 0x0800){
			throw new ClassNotBeChoosedException();
		}
		System.out.println("���棡\n1��ʹ�ø÷��������Ķ�������Ϊ��ʼ�����ԣ������޸ĺ�ʹ�û���ɲ�ȷ���ĺ����\n2����ȷ�������й��е��޲ι��캯����\n3����ѡ��Ķ��󽫻ᱻ���ǣ��Ա�����һ���޸�\n�������������������1����������0");
		System.out.print("MyCMD>New Object By Reflect>");
		if(scan.nextInt()==1){
			obj=cla.newInstance();
			flag |=0x0400;
			System.out.println("create complete");
		}
		return ;
	}
	//��תqc״̬
	private static void turnIsQuickComm(){
		isQuickCommand=!isQuickCommand;
		if(isQuickCommand){
			System.out.println("�Ѿ������������ģʽ��ͨ��-�����Բ鿴�����Ӧ��");
		}else{
			System.out.println("�Ѿ��ص���������ģʽ");
		}
	}
	
	private static void removeObjectByIndex() throws ClassNotBeChoosedException{
		listAllObject();//�����˴���δѡ������쳣
		System.out.println("��������Ҫɾ���Ķ���Ķ�Ӧ����");
		System.out.print("MyCMD>Remove Object By Index>");
		if(ObjectStorage.removeObjectByIndex(scan.nextInt(), cla)){
			listAllObject();
			System.out.println("delete complete��");
		}else{
			System.out.println("delete false");
		}
	}
	
	/**
	 * ��ӡ��������
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
	 * �˳��ر�MyCMD
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

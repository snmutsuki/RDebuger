package javastu.reflect.test;

import javastu.reflect.preconfig.interf.ReflectControlInterf;
import javastu.reflect.preconfig.lib.ObjectStorage;

public class PersonTest implements ReflectControlInterf {
	private String name;
	private int age;
	private PersonTest son;
	public PersonTest(){}
	public PersonTest(String name,int age){
		this.name=name;
		this.age=age;
		this.son=null;
		putThisObjectTotheArr();
	}
	
	public String getName(){
		
		return name;
	}
	public int getAge(){
		return age;
	}
	public void printName(){
		System.out.println(name);
	}
	public int getAgeAfterYears(int years){
		System.out.println(age+years);
		return age+years;
	}
	
	public void printSomething(String str,int a){
		System.out.println(str+"--->"+a);
	}
	public void printInfoOfSon(){
		System.out.println(son);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name+"&"+String.valueOf(age);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PersonTest){
			if(((PersonTest)obj).getAge()==age &&((PersonTest)obj).getName()==name){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void putThisObjectTotheArr() {
		ObjectStorage.addNewObject(this);
	}
	

}

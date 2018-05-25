package com.vechace.JVM;

/**
 * Description：查看线程栈大小
 * @author vechace
 *
 */
public class JVMTest4 {
	
	//计算递归调用次数
	static int count = 0;
	/**
	 * 递归查看栈深度
	 */
	static void foo(){
		count++;
		foo();
	}

	public static void main(String[] args) {
		try{
			foo();
		}catch(Throwable t){
			System.out.println(count);
			t.printStackTrace();//栈溢出，递归调用过深
		}

	}

}

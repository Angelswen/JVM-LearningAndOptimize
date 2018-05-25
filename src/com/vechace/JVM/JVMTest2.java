package com.vechace.JVM;
/**
 * Description:使用Runtime类计算内存情况，常用于测试剩余内存
 * @author vechace
 *	
 */
public class JVMTest2 {
	
	static void printMemoryInfo(){
		System.out.println("total: " + Runtime.getRuntime().totalMemory());
		System.out.println("free: " +Runtime.getRuntime().freeMemory());
	}

	public static void main(String[] args) {
		
		printMemoryInfo();
		
		byte[] b = new byte[1024*1024];
		System.out.println("------------------");
		
		printMemoryInfo();

	}

}

package com.vechace.JVM;
/**
 * Description:ʹ��Runtime������ڴ�����������ڲ���ʣ���ڴ�
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

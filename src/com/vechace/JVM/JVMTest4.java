package com.vechace.JVM;

/**
 * Description���鿴�߳�ջ��С
 * @author vechace
 *
 */
public class JVMTest4 {
	
	//����ݹ���ô���
	static int count = 0;
	/**
	 * �ݹ�鿴ջ���
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
			t.printStackTrace();//ջ������ݹ���ù���
		}

	}

}

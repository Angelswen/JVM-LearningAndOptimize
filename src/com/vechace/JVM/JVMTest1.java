package com.vechace.JVM;

/**
 * 
 * @author vechace
 *	-XX:-DoEscapeAnalysis  �ر����ݷ���
 *	-XX:-EliminateAllocations �رձ����滻
 *	-XX:-UseTLAB �ر��̱߳����ڴ�
 *	-XX:-PrintGC ��ӡGC��Ϣ
 */
public class JVMTest1 {
	
	class User{
		int id;
		String name;
		
		User(int id,String name){
			this.id = id;
			this.name = name;
			
		}
	}
	
	/*void alloc(int i){
		new User(i,"name"+i);
	}*/
	
	User u;
	void alloc(int i){
		//�����ⲿ����u,�����˷�������
		u = new User(i,"name"+i);
	}
	

	public static void main(String[] args) {
		
			JVMTest1 t = new JVMTest1();
			long s1 = System.currentTimeMillis();
			for(int i = 0;i<10000000;i++){
				t.alloc(i);
			}
			long s2 = System.currentTimeMillis();
			System.out.println(s2-s1);

	}

}

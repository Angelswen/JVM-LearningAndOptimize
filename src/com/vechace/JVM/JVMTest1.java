package com.vechace.JVM;

/**
 * 
 * @author vechace
 *	-XX:-DoEscapeAnalysis  关闭逃逸分析
 *	-XX:-EliminateAllocations 关闭标量替换
 *	-XX:-UseTLAB 关闭线程本地内存
 *	-XX:-PrintGC 打印GC信息
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
		//存在外部引用u,逃逸了方法控制
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

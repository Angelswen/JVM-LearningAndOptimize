package com.vechace.JVM;

import java.util.ArrayList;
import java.util.List;

/**
 * ÄÚ´æÒç³ö
 * -XX:+HeapDumpOutOfMemoryError 
 * -XX:HeapDumpPath=d:\tmp\jvm.dump
 * -XX:+PrintGCDetails
 * -Xms10M
 * -Xmx10M
 * @author asus-pc
 *
 */
public class JVMTest3 {

	public static void main(String[] args) {
		List<Object> lists = new ArrayList<>();
		
		for(int i=0;i<100000000;i++){
			lists.add(new byte[1024*1024]);
		}

	}

}

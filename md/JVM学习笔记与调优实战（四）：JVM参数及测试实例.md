# JVM学习笔记与调优实战（四）：JVM参数及测试实例

标签： JVM

---
**1、商业虚拟机：**

 - HotSpot：oracle商业虚拟机，在jdk1.8下，默认模式是Server

 - Openjdk：开源虚拟机
 


**2、JVM参数格式**
  
   -：标准参数，所有JVM都应该支持，可在命令行下输入Java查看
 -X  : 非标准参数，每个JVM实现都不同
-XX : 不稳定参数，下一个版本可能会取消

**3、常用JVM参数**

 - 堆设置：
    - -Xms 初始堆大小
    - -Xmx 最大堆大小
    - -Xss 线程栈大小
    - -XX:NewSize=n  设置新生代大小
    - -XX:NewRatio=n设置新生代和老年代的比值，如-XX:NewRatio=3，表示新生代：老年代= 1:3，新生代占整个新老年代和的1/4
    - -XX:SurvivorRatio=n新生代中eden区与两个survivor区的比值，如-XX:SurvivorRatio=3，表示eden:survior =3:2，一个survivor区占整个新生代的1/5
    - -XX:MaxPermSize=n 设置永久代大小
<br></br>
 - 收集器设置：
    - -XX:+UseSerialGC  设置使用串行收集器
    - -XX:+UseParallelGC  设置并行收集器
    - -XX:+UseConcMarkSweepGC  设置并发收集器
<br></br>
 - GC统计信息：
    - -XX:+PrintGC 打印GC信息
    - -XX:+PrintGCDetails  打印详细GC信息
    - -Xloggc:filename 打印GC信息到日志文件中
<br></br>
 - 其他：
    - -XX:-DoEscapeAnalysis  关闭逃逸分析
    - -XX:-EliminateAllocations 关闭标量替换
    - -XX:-UseTLAB 关闭线程本地内存

**4、测试实例**

-XX:PrintGCDetails，输出GC信息如下：
```
Heap
PSYoungGen  total 394240K, used 94393K [0x0000000780800000, 0x0000000798a00000, 0x00000007c0000000)
eden space 393216K, 23% used [0x0000000780800000,0x000000078641e400,0x0000000798800000)
from space 1024K, 6% used [0x0000000798900000,0x0000000798910000,0x0000000798a00000)
to space 1024K, 0% used [0x0000000798800000,0x0000000798800000,0x0000000798900000)
ParOldGen  total 131072K, used 596K [0x0000000701800000, 0x0000000709800000, 0x0000000780800000)
object space 131072K, 0% used [0x0000000701800000,0x0000000701895200,0x0000000709800000)
Metaspace  used 2692K, capacity 4490K, committed 4864K, reserved 1056768K
class space used 296K, capacity 386K, committed 512K, reserved 1048576K
```


使用Runtime类大致计算内存情况，用于追踪程序内存使用情况
[参考代码](https://github.com/Angelswen/JVM-LearningAndOptimize/blob/master/src/com/vechace/JVM/JVMTest2.java)

```Java
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
```

//控制台输出：
```
total: 192937984
free: 190924680
------------------
total: 192937984
free: 189876088
```




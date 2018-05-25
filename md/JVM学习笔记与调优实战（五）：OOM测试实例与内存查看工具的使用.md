# JVM学习笔记与调优实战（五）：OOM测试实例与内存查看工具的使用

标签： JVM

---
**1、OOM测试实例：**

```
package com.vechace.JVM;

import java.util.ArrayList;
import java.util.List;

/**
* 内存溢出
* -XX:+HeapDumpOutOfMemoryError
* -XX:HeapDumpPath=d:\tmp\jvm.dump
* -XX:+PrintGCDetails
* -Xms10M
* -Xmx10M
* @author vechace
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
```


JVM配置说明：
```
-XX:+HeapDumpOnOutOfMemoryError  ：当发生内存溢出时，导出dump文件
-XX:HeapDumpPath=d:\tmp\jvm.dump2：设置dump文件的路径为d:\tmp\jvm.dump2
-XX:+PrintGCDetails：控制台打印详细GC信息
-Xms10M：设置虚拟机初始堆内存为10M
-Xmx10M：设置虚拟机最大堆内存为10M
```

 - 分析：
    - 一般情况下，虚拟机的初始堆内存会比最大堆内存要小，而调优时往往会把初始值-Xms调至最大值-Xmx或者接近最大值,目的是减少中间的GC内存计算过程。
    - 例如，设置-Xmx1G，-Xms256M，当程序运行时，虚拟机会不断地进行GC、申请新内存用于存新对象，且进行一次GC的效率较低，耗时。而直接设置-Xms1G时，初始内存开始就分配1G，与最大内存相等，程序运行时就省去了中间的内存计算及GC过程，进而提高了效率，这是调优的小技巧。
<br></br>
执行程序后，打开内存查看工具visualVM查看程序导出的dump文件，结果如下

![dump][1]
<br></br>
 - 结果分析：从图中可以看出，程序中出现了一个字节数组byte[]，占用了92.7%的内存，对应程序中的for循环，在实际开发过程中，可以通过visualVM查看内存分配情况，再回过来检查代码，找出问题所在。


**2、StackOverflow栈溢出：**

```
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
```

结果1：
```
38084
java.lang.StackOverflowError
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    ......

```
<br></br>
调整JVM参数-Xss512M，设置栈起始内存为512M，再运行程序。

结果2
```
33526899
java.lang.StackOverflowError
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
    at com.vechace.JVM.JVMTest4.foo(JVMTest4.java:17)
```
<br></br>

 - 分析：

    - 可见，递归调用次数明显增多了，在JVM调优时，-Xss也是一个非常重要的调优参数，当-Xss调的值较小时，线程的并发数就多（总内存不变，每个线程分的内存少，线程数自然变多）

    - 而当-Xss调的比较大，则线程递归深度就深（内存分得多，调用栈深度越深，同时线程数变少），该值属于经验值，需要结合业务来进行分析。


  [1]: https://raw.githubusercontent.com/Angelswen/JVM-LearningAndOptimize/master/image/dump.png
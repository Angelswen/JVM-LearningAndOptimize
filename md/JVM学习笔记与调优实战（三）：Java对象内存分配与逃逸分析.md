# JVM学习笔记与调优实战（三）：Java对象内存分配与逃逸分析

标签： JVM

---
1、Java对象的分配：

 - 栈上分配
    - 线程私有小对象
    - 无逃逸
    - 支持标量替换
    - 无需调整（虚拟机自动优化，无需调优）
<br></br>
 - 线程本地分配TLAB（Thread Local Allocation Buffer）
    - 占用eden，默认1%，仍在堆上申请，用作线程专用
    - 多线程的时候不用竞争（加锁）eden就可以申请空间（同步消除），提高效率
    - 小对象
    - 无需调整
<br></br>
 - 老年代
    - 大对象（大数组、长字符串）
<br></br>
 - eden
    - new普通对象
<br></br>

**分配策略：**
如果JVM启动了逃逸分析，那么new一个对象时，首先会尝试在栈上分配，如果分配不了，则会尝试在线程本地分配，如果栈上分配与线程本地分配均分配失败的话，则会先判断该对象是否为大对象，如果是大对象，则在老年代分配内存，否则到新生代的eden区分配。
<br></br>
**2、逃逸分析：**
逃逸分析是一种为其他优化手段提供依据的分析技术，其基本行为是分析对象动态作用域：当一个对象在方法中被定义后，它可能被外部方法所引用，例如作为调用参数传递到其他方法中，称为方法逃逸；也有可能被其外部线程访问到，如复制给类变量或者可以在其他线程中访问的实例变量，称为线程逃逸。
如果一个对象不会逃逸到方法或者线程之外，则可以对这个对象进行一些高效的优化：

 - 栈上分配Stack Allocation：如果一个对象不会逃逸到方法之外，那么可以让这个对象在栈上分配内存，以提高执行效率，对象所占内存会随着栈帧出栈而销毁。在一般应用中，无逃逸的局部变量对象所占的比例较大，如果能使用栈上分配，那么大量的对象就会随着方法的结束而自动销毁，GC压力减小很多。

 - 同步消除SynchronizationElimination：线程同步是一个相对耗时的过程，如果逃逸分析能够确定一个变量不会逃逸出线程，无法被其他线程访问，那么该变量的读写不存在竞争关系，即可以消除掉对这个变量的同步措施
 
 - 标量替换：
    - 标量：指的是一个数据已经无法再分解成更小的数据来表示了，Java虚拟机的原始数据类型（int,float等数值类型以及reference类型）都不能再进行进一步的分解
    - 聚合量：相对于标量，如果一个数据可继续分解，则可以称作聚合量，Java对象是典型的聚合量。
    - 如果把一个Java对象拆散，根据程序访问的情况，将其使用到的成员变量恢复原始类型来访问，这过程成为标量替换
    - 如果逃逸分析可以确定一个对象不会被外部访问，且这个对象可以被拆散，那程序真正执行的时候，可以不创建这个对象，而是直接创建它的成员变量来替换这个对象。将对象拆分后，可以在栈上分配内存


**3、测试实例：**
[参考代码](https://github.com/Angelswen/JVM-LearningAndOptimize/blob/master/src/com/vechace/JVM/JVMTest1.java)
```Java
package com.vechace.JVM;

/**
* Description：新建10000000个对象，计算执行时间，再配置不同JVM参数
* 比较执行结果
* @author vechace
*    -XX:-DoEscapeAnalysis  关闭逃逸分析
*    -XX:-EliminateAllocations 关闭标量替换
*    -XX:-UseTLAB 关闭线程本地内存
*    -XX:-PrintGC 打印GC信息
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
    
    void alloc(int i){
        new User(i,"name"+i);
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
```
IDE：Eclipse

run As --> run configuration --> Argument --> VM argument ：填入如下配置：
```
-XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:-UseTLAB -XX:+PrintGC
```

**结果分析：**

a.无逃逸分析、无栈上分配、不使用线程本地内存：
```
-XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:-UseTLAB -XX:+PrintGC
```
控制台输出：
```
[GC (Allocation Failure)  49152K->688K(188416K), 0.0010012 secs]
[GC (Allocation Failure)  49840K->728K(188416K), 0.0009848 secs]
[GC (Allocation Failure)  49880K->640K(188416K), 0.0007432 secs]
[GC (Allocation Failure)  49792K->672K(237568K), 0.0008412 secs]
[GC (Allocation Failure)  98976K->640K(237568K), 0.0012708 secs]
[GC (Allocation Failure)  98944K->656K(328704K), 0.0008696 secs]
[GC (Allocation Failure)  197264K->624K(328704K), 0.0017397 secs]
[GC (Allocation Failure)  197232K->624K(320512K), 0.0003312 secs]
791
```

b.使用线程本地内存，无需在eden区分配内存时加锁，效率变高
```
-XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+UseTLAB -XX:+PrintGC
```
控制台输出：
```
[GC (Allocation Failure)  49760K->640K(188416K), 0.0007129 secs]
[GC (Allocation Failure)  49792K->624K(237568K), 0.0008062 secs]
[GC (Allocation Failure)  98928K->608K(237568K), 0.0014966 secs]
[GC (Allocation Failure)  98912K->728K(328704K), 0.0008608 secs]
[GC (Allocation Failure)  197336K->588K(328704K), 0.0016310 secs]
[GC (Allocation Failure)  197196K->620K(525312K), 0.0003275 secs]
528
```

c.开启逃逸分析、使用标量替换、使用线程本地内存、效率变高
```
-XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:+UseTLAB -XX:+PrintGC
```
控制台输出：
```
[GC (Allocation Failure)  49152K->688K(188416K), 0.0010576 secs]
[GC (Allocation Failure)  49840K->640K(188416K), 0.0009443 secs]
[GC (Allocation Failure)  49792K->640K(188416K), 0.0007502 secs]
[GC (Allocation Failure)  49792K->696K(237568K), 0.0008981 secs]
[GC (Allocation Failure)  99000K->656K(237568K), 0.0011229 secs]
[GC (Allocation Failure)  98960K->608K(328704K), 0.0010558 secs]
[GC (Allocation Failure)  197216K->644K(328704K), 0.0015396 secs]
486
```

问题分析：开启逃逸分析存在开销，有时效率不如未开逃逸分析时的效率高


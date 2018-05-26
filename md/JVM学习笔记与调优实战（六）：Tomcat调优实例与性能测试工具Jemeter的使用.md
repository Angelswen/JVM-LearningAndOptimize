# JVM学习笔记与调优实战（六）：Tomcat调优实例与性能测试工具Jemeter的使用

标签： JVM

---
**1、典型tomcat优化配置：**
```
set JAVA_OPTS = 
-Xms4g 
-Xmx4g 
-Xss512k 
-XX:+AggressiveOpts 尽可能地用上JVM自带的优化策略
-XX:+UseBiasedLocking  启用偏置锁优化
-XX:PermSize=64M //jdk1.8取消了该参数
-XX:MaxPermSize=300M 
-XX:+DisableExplicitGC  关闭显式调用GC，如System.gc()，以免打乱调优结构
-XX:+UseConcMarkSweepGC 使用CMS缩短响应时间，并发收集，低停顿
-XX:+UseParNewGC 并发收集新生代的垃圾
-XX:+CMSParallelRemarkEnabled 在使用UseParNewGC的情况下，尽量减少Mark标记时间
-XX:+UseCMSCompactAtFullCollection 使用并发收集器CMS时，开启对老年代的压缩，使得内存碎片减少
-XX:LargePageSizeInBytes=128M 内存分页大小对性能的提升（操作系统）
-XX:+UseFastAccessorMethods get/set方法转成本地代码
-Djava.awt.headless=true 修复Linux下tomcat处理图表时可能产生的一个bug
```

解析：

 - -Xms的内存值如何选择：
     - 根据实际业务来定，先查看服务器上部署了多少个Java应用，再来选择
     - 例如：服务器内存64g，只部署了一个tomcat应用，那么可以设置-Xms的值接近64g，以达到内存最大利用，但是要注意设置前提：仅部署一个tomcat，如果部署了多个应用，则要根据实际业务来权衡
     - 例如，一些业务中的实现需要频繁new对象的，则可以分配较大的eden区内存（调整-XX:NewRatio=n ，新老年代内存比例），以满足业务需求
     - 而另一些业务服务需要不断的运行，老年代上对象占用较多，则可以分配较大的old区内存
<br></br>
 - -XX:PermSize -XX:MaxPermSize值如何选择：当程序中类信息比较多的时候（类信息存在永久代），可适当调大永久代的内存空间，如：Eclipse启动速度慢，可以调大永久代内存大小，使得启动速度变快
<br></br>
**2、tomcat调优实例：**
在tomcat根目录下的bin目录下，编辑catalina.bat文件，添加调优参数

![tomcat][1]

启动tomcat
<br></br>
**3、性能测试：**
通过Jmeter模拟高并发情景，观察系统响应时间是否拖长，系统响应时间拖长了说明系统撑住了高并发
其他测试技巧：持久加压测峰值，稳定运行测平均吞吐量
<br></br>
在Jemter中创建线程组，模拟2000个线程，同时发起http的get请求，请求tomcat实例http://localhost:8080/examples/jsp/jsp2/el/basic-arithmetic.jsp

配置如下：

![jmeter1][2]


新建Aggregate Graph图表查看测试结果，如下：

优化前：457.5/sec

![jmeter2][3]

优化后：8.7/sec，系统性能提升了

![jmeter3][4]

<br></br>
**4、误差分析：**
由于测试采用的是本机测试，所有机子本身的测试也会影响到系统性能，因此本机的性能测试仅供参考，实际开发中一般采用不同的机子来测试，一台机子专门跑性能测试工具，用来测试服务器的性能


  [1]: https://raw.githubusercontent.com/Angelswen/JVM-LearningAndOptimize/master/image/tomcat.png
  [2]: https://raw.githubusercontent.com/Angelswen/JVM-LearningAndOptimize/master/image/jmeter1.png
  [3]: https://raw.githubusercontent.com/Angelswen/JVM-LearningAndOptimize/master/image/jmeter2.png
  [4]: https://raw.githubusercontent.com/Angelswen/JVM-LearningAndOptimize/master/image/jmeter3.png
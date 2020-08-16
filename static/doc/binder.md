#### 课程准备资料

> Android系统源码采用8.0讲解， Binder驱动层源码，希望同学们能提前下载，源码文件比较大
>
> 链接：https://pan.baidu.com/s/1qND9JdMXAsDxzrM4mEIjXA 
> 提取码：2moj

![image-20200506111622725](img.png)

从上之下, 整个Binder架构所涉及的总共有以下5个目录:

```
/framework/base/core/java/               (Java)
/framework/base/core/jni/                (JNI)
/framework/native/libs/binder            (Native)
/framework/native/cmds/servicemanager/   (Native)
/kernel/drivers/staging/android          (Driver)
```

##### 1.1 Java framework

```
/framework/base/core/java/android/os/  
    - IInterface.java
    - IBinder.java
    - Parcel.java
    - IServiceManager.java
    - ServiceManager.java
    - ServiceManagerNative.java
    - Binder.java  


/framework/base/core/jni/    
    - android_os_Parcel.cpp
    - AndroidRuntime.cpp
    - android_util_Binder.cpp (核心类)
```

##### 1.2 Native framework

```
/framework/native/libs/binder         
    - IServiceManager.cpp
    - BpBinder.cpp
    - Binder.cpp
    - IPCThreadState.cpp (核心类)
    - ProcessState.cpp  (核心类)

/framework/native/include/binder/
    - IServiceManager.h
    - IInterface.h

/framework/native/cmds/servicemanager/
    - service_manager.c
    - binder.c
```

##### 1.3 Kernel

```
/kernel/drivers/staging/android/
    - binder.c
    - uapi/binder.h
```
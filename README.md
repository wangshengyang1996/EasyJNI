# EasyJNI
根据java的Class、Method、Field生成相关JNI代码

### 以为File.toURL()函数为例，生成的代码如下：
```
jmethodID toURLID = env->GetMethodId(fileCls,"toURL","()Ljava/net/URL;");
jobject toURLValue = env->CallObjectMethod(fileObj,toURLID);
// this exception may occur:
// java.net.MalformedURLException
jthrowable error = env->ExceptionOccurred();
if (error != NULL) {
    // WARNING: YOU CAN NOT USE SOME JNI FUNCTIONS AFTER EXCEPTION OCCURRED AND 'env->ExceptionClear()' IS NOT CALLED
    // see 
    // https://developer.android.google.cn/training/articles/perf-jni#exceptions_1
    // or
    // https://developer.android.com/training/articles/perf-jni#exceptions_1
    // for more information
    // 
    // print exception:
    // env->ExceptionDescribe();
    // 
    // clear exception:
    // env->ExceptionClear();
    // 
    // TODO: write your code here to solve this exception
    // like 
    // jclass MalformedURLExceptionClazz = env->FindClass("java/net/MalformedURLException");
    // if(env->IsInstanceOf(error,MalformedURLExceptionClazz)){
    //     //do when this exception occurred
    // }
}
```

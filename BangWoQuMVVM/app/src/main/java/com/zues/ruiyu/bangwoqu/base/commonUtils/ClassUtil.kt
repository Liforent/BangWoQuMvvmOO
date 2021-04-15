package com.zues.ruiyu.bangwoqu.base.commonUtils

import com.zues.ruiyu.bangwoqu.module.home.view.BaseScannerActivity
//import com.zues.ruiyu.bangwoqu.module.home.view.ScannerActivity
//import com.zues.ruiyu.bangwoqu.module.home.view.ScannerOutActivity
import java.lang.reflect.ParameterizedType

/**
 *@Author liforent
 *@create 2020/8/25 14:12
 */
object ClassUtil {
    fun <T> getClass(t: Any): Class<T> {
        // 通过反射 获取父类泛型 (T) 对应 Class类

        return if(t is BaseScannerActivity){
            (BaseScannerActivity::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[0]
                    as Class<T>
        } else{
            (t.javaClass.genericSuperclass as ParameterizedType)
                .actualTypeArguments[0]
                    as Class<T>
        }
    }
}
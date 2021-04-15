package com.zues.ruiyu.bangwoqu.test

import com.zues.ruiyu.bangwoqu.base.ZLog

/**
 * @Author liforent
 * @create 2020/11/16 16:05
 * lambda语法规则：
 *      var/val 变量名：（参数类型，参数类型，...)->返回值类型 = {参数类型，参数类型，...->操作参数的代码}
 *      等价于
 *      var/val 变量名 = {参数1：类型，参数2：类型，...->操作参数的代码}
 */
class TestCorou {


    //1.无参数
    //kotlin:
    private fun fun0() {
        print("111")
    }

    //lambda:
    var fun1 = { print("111") }


    //2.有参数
    //kotlin:
    private fun fun2(a: Int, b: Int): Int {
        return a + b
    }

    //lambda:
    var fun3: (Int, Int) -> Int = { a, b -> a + b }
    var fun4 = { a: Int, b: Int ->
        a + b
    }


    //3.lambda表达式作为函数中的参数
    //kotlin:
    fun fun4(a: Int, b: Int): Int {
        return a + b
    }

    fun sumK(x: Int, y: Int): Int {
        return x - y
    }
    //调用：sumK(2,fun4(3,1))

    //lambda:
    fun sumL(a: Int, b: (x: Int, y: Int) -> Int): Int {
        return a + b.invoke(3, 1)
    }
    //调用：sumL(1, { x: Int, y: Int -> x + y }) -> (大括号移到外边)
    //      sumL(1) { x: Int, y: Int -> x + y }


    //4.匿名函数。匿名函数的特点是可以明确指定其返回值类型。
    //常规函数：
    fun fun5(x: Int, y: Int): Int = x + y

    //匿名函数：
    val fun6 = fun(x: Int, y: Int): Int = x + y


    //5.带接收者的函数字面值
    var func: Int.(a: Int) -> Int = { a -> a + this }

    //匿名函数作为接收者类型
    var fund = fun Int.(a: Int): Int = this + a

    class People{
        fun smile(){}
    }

    //lambda表达式作为接收者类型
    private fun goon(dinit: People.() -> Unit): People {
        val zss = People()
        zss.dinit()
        return zss
    }

    //6.闭包（函数包喊函数）
    //~~

    //7.


    fun main(args: Array<String>) {
        println("Hello Kotlin")
        fun0()
        fun1()
        fun2(1, 2)
        fun3(1, 3)
        fun4(1, 2)
        sumK(1, fun4(2, 1))
        sumL(1) { x: Int, y: Int -> x + y }
        goon { smile() }
        ZLog.e(3.func(2).toString())
        ZLog.e(3.fund(2).toString())

    }


}
package com.zues.ruiyu.bangwoqu.test

class BugReport {
}

/**
 * 版本信息：
 *  1.扫描入库页面增加退出确认弹窗
 *  2.增加单号过滤
 *  3.首页增加查询订单功能
 *
 *
 * BUG
 * 1.接口重复扫描导致的顶部提示问题
 *  btn_mark_address目前存在位置不对
 *
 *
 *
 * RECORD
 * 1.处理软键盘相关新思路
 *  用keyboardListener监听键盘弹出和收起，用动画位移来处理后续相关
 * 2.Button可以直接通过设置background实现selector状态切换，但是TextView不可以
 * 3.EventBus使用姿势
 *      接收的地方才注册
 * 4.
 *
 * TODO
 * 1.scan_phone_Mode下，etStrPhone弹出的软键盘会影响布局，是软键盘收起弹出监听里的代码导致的，原因未知，只是做了一层保护处理。
 * 2.无网络情况下，  mUserInfo!!.id.toInt(),进入扫码页面会闪退
 * 3.键盘还有轻微遮挡，检测键盘弹出的方法在华为8.0测试机上有问题，华为测试机在键盘没有弹出的情况下检测到了键盘弹出，并直接影响到了后续逻辑。
 *  现在只是加了一层保护不止于闪退，但是还是不能扫描，并且后续扫描逻辑还有问题。
 * 4.这个扫描界面逻辑太多，需要优化，现在扫描效率低，很吃手机性能。
 * 5.扫描页面没有设置自动退出逻辑
 * 6.价格+ — icon状态
 * 7.首页RecyclerView item 颜色改变问题
 * 8.首页中去除获取快递信息的接口，处理多个接口请求顺序导致得ERRORCALLBACK偶尔不出现问题
 *
 * WARN
 * 1.emptyResponse不能直接toString来log
 * 3.编辑信息页面，收起键盘，再点击et弹出键盘，会导致布局有个闪烁上顶
 * 5.tab切换无法触发onResume,onResume的时候如果需要更新界面，需要手动在mainActivity里处理。
 *
 *
 *
 * 1.外部拦截:
 *      父类onInterceptTouchEvent中拦截
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
fun bugRecord() {}


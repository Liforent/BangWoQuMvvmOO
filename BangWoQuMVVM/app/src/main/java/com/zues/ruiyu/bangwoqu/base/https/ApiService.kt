import com.zues.ruiyu.bangwoqu.base.https.BasePagingResponse
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.module.account.data.*
import com.zues.ruiyu.bangwoqu.module.home.data.*
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.FinancialRecordsResponse
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse
import com.zues.ruiyu.bangwoqu.module.setting.data.SettingPswModel
import okhttp3.RequestBody
import retrofit2.http.*

/**
 *@Author liforent
 *@create 2020/8/24 15:14
 */
interface ApiService {
    //1001 获取快递列表
    @GET("/api/packages/")
    suspend fun loadPackages(
        @Header("Authorization") token: String,
        @Query("page_num") page_num: Int?,
        @Query("receiver_phone") receiver_phone: String?,
        @Query("tracking_number") tracking_number: String?,//	快递单号
        @Query("shipper") shipper: String?,//快递点id
        @Query("courier") courier: String?,//快递员id, 查询快递员的接单
        @Query("status") status: String?
    ): BasePagingResponse<List<PackagesListResponse>>


    //1002
    @POST("/api/users/sms_auth/")
    suspend fun onLogin(
        @Body loginModel: LoginModel
    ): BaseResponse<AccountResponse>

    //1003
    @POST("/api/users/sms_auth/")
    suspend fun onRegister(
        @Body registerModel: RegisterModel
    ): BaseResponse<AccountResponse>

    //1004
    @POST("/api/users/get_sms_code/")
    suspend fun getVerificationCode(
        @Body verificationCodeModel: VerificationCodeModel
    ): BaseResponse<EmptyResponse>

    //1005
    @POST("/api/users/set_password/")
    suspend fun setPsw(
        @Header("Authorization") token: String,
        @Body settingPswModel: SettingPswModel
    ): BaseResponse<EmptyResponse>

    //1006
    @GET("/api/users/{user_id}/")
    suspend fun getUserInfo(
        @Header("Authorization") token: String,
        @Path("user_id") id: Long
    ): BaseResponse<UserInfoResponse>

    //1007
    @POST("/api/users/withdraw/")
    suspend fun withdraw(
        @Header("Authorization") token: String,
        @Body model: WithdrawModel
    ): BaseResponse<EmptyResponse>

    //1008
    @PUT("/api/users/{user_id}/")
    suspend fun modifyUserInfo(
        @Header("Authorization") token: String,
        @Path("user_id") user_id: Long,
        @Body modifyUserInfoModel: ModifyUserInfoModel
    ): BaseResponse<UserInfoResponse>


    //1009 获取收支记录
    @GET("/api/financial_records/")
    suspend fun getFinancialRecords(
        @Header("Authorization") token: String,
        @Query("page_num") page_num: Int?,
        @Query("user") user: Long,
        @Query("type") type: Int?//收入:1, 支出: 0
    ): BasePagingResponse<List<FinancialRecordsResponse>>

    //1010 快递信息查询
    @GET("/api/packages/query/")
    suspend fun queryPackage(
        @Header("Authorization") token: String,
        @Query("tracking_number") tracking_number: String,
        @Query("com") com: String?,//	快递公司编码, 如果不填, 则自动识别, 并和返回结果一起返回
        @Query("phone") phone: String?    //快递单号收件人手机号, 如果为顺丰快递, 则必填此字段, 详情见快递列表
    ): BaseResponse<PackageInfoResponse>

    //1011 快递公司列表接口
    @GET("/api/courier_com/")
    suspend fun getCompanyInfo(
        @Header("Authorization") token: String
    ): BasePagingResponse<List<CompanyInfoResponse>>


    //1012 扫码出库接口
    @PATCH("/api/packages/outbound/")
    suspend fun scanOut(
        @Header("Authorization") token: String,
        @Body model: ScanOutModel
    ): BaseResponse<EmptyResponse>


    //deprecated
    //1013 出库前快递状态查询
    @POST("api/packages/check_package/")
    suspend fun queryPackageStatusBeforeScanOut(
        @Header("Authorization") token: String,
        @Body model: ScanOutModel
    ): BaseResponse<PackagesListResponse>

    //1014 快递入库
    @POST("/api/packages/")
    suspend fun warehousingPackage(
        @Header("Authorization") token: String,
        @Body warehousingPackageModel: WarehousingPackageModel
    ): BaseResponse<WarehousingPackageResponse>

    //1015 快递删除
    @DELETE("/api/packages/{id}/")
    suspend fun deletePackage(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BaseResponse<EmptyResponse>


    //1016 快递修改手机号码
    @PATCH("/api/packages/{package_id}/")
    suspend fun modifyPhone(
        @Header("Authorization") token: String,
        @Path("package_id") id: Int,
        @Body modifyPhoneModel: ModifyPhoneModel
    ): BaseResponse<EmptyResponse>

    //1017 入库前快递状态查询
    @GET("api/packages/inbound_check/")
    suspend fun queryPackageStatusBeforeScanIn(
        @Header("Authorization") token: String,
        @Query("tracking_number") tracking_number: String,
        @Query("phone") phone: String
    ): BaseResponse<PackageInfoResponse>

    //1018 批量入库
    //@HTTP(method = "GET",path="api/packages/bulk_storage/",hasBody = true)
    @POST("api/packages/bulk_storage/")
    suspend fun bulkStorage(
        @Header("Authorization") token: String,
        @Body packages: RequestBody
    ): BaseResponse<EmptyResponse>


    //1019 地区5级联动接口接口
    @GET("api/areas/")
    suspend fun queryVillageList(
        @Header("Authorization") token: String,
        @Query("parent") parent: Long,
        @Query("page_size") page_size: Int,
        @Query("page_num") page_num: Int
    ): BasePagingResponse<List<AreaInfoResponse>>


    //1020 快递点补全(创建)收件人(保存收件人地址)
    @POST("api/sms_user_auth/shipper_create_receiver/")
    suspend fun completeReceiverInfo(
        @Header("Authorization") token: String,
        @Body() model: CompleteReceiverInfoModel
    ): BaseResponse<EmptyResponse>


}
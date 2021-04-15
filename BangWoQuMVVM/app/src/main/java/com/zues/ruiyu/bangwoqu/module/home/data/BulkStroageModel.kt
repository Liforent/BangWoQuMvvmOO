package com.zues.ruiyu.bangwoqu.module.home.data

import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 *@Author liforent
 *@create 2020/12/3 18:29
 */
data class BulkStorageModel(
    var receiver_phone: String,
    var tracking_number: String,
    var com_name: String?,
    var com: String,
    var info: List<PackageInfoResponse.PackageInfo>?,
    var shipper: Int,
    var freight: Float,
    var priceInfo: PackageInfoResponse.PriceInfo?,
    var logo: String?,
    var town_code: Long?
) : LitePalSupport()

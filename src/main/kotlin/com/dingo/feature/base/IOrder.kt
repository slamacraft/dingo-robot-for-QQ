package com.dingo.feature.base

interface IOrder {

    /**
     * 排序值，越小越靠前
     */
    fun order(): Int

}
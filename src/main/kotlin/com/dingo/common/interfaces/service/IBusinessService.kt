package com.dingo.common.interfaces.service

import com.dingo.common.enums.BusinessTypeEnum

interface IBusinessService {
    fun businessType(): BusinessTypeEnum
}
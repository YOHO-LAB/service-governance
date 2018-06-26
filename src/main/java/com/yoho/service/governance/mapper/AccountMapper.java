package com.yoho.service.governance.mapper;

import com.yoho.service.governance.model.Account;
import org.apache.ibatis.annotations.Param;


public interface AccountMapper {

    Account queryAccountByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    Account queryAccountByUsername(@Param("username") String username);

}

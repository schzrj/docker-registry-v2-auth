package test.com.zhangj.registry.auth.service;

import com.zhangj.registry.auth.model.Account;
import com.zhangj.registry.auth.service.AccountService;
import org.springframework.stereotype.Service;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/15
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Override
    public Account login(String userName, String password) {
        Account account=new Account();
        account.setId(1L);
        return account;
    }

    @Override
    public Account findByAccount(String userName) {
        Account account=new Account();
        account.setId(1L);
        return account;
    }

    @Override
    public Boolean isAdmin(Long userId) {
        return true;
    }
}

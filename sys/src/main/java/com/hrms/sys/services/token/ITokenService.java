package com.hrms.sys.services.token;

import com.hrms.sys.models.Token;
import com.hrms.sys.models.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
}

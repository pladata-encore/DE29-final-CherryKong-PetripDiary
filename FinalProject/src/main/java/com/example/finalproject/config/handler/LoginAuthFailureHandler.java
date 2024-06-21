package com.example.finalproject.config.handler;

import java.net.URLEncoder;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.finalproject.config.contant.AuthenticationTypes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler{

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {

            log.error("[LoginAuthFailureHandler][onAuthenticationFailure] Start");
            exception.printStackTrace();
            writePrintErrorResponse(response, exception);

    super.onAuthenticationFailure(request, response, exception);
  }
  private void writePrintErrorResponse(HttpServletResponse response,
      AuthenticationException exception) throws IOException {

          AuthenticationTypes authenticationTypes = AuthenticationTypes.valueOf(exception.getClass().getSimpleName());
          String errorMessage = authenticationTypes.getMsg();
          int code = authenticationTypes.getCode();
          log.error("message: "+errorMessage+" / code: "+code);

          errorMessage = URLEncoder.encode(errorMessage, "UTF-8"); /* 한글 인코딩 깨진 문제 방지 */
          setDefaultFailureUrl("/loginPage?errorMessage="+errorMessage);
    }
}

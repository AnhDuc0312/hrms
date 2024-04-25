package com.hrms.sys.filter;

import com.hrms.sys.models.User;
import com.hrms.sys.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("api/v1")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            //KIểm tra token bỏ qua
            if (isBypassToken(request)) {
                filterChain.doFilter(request,response);
                return;
            }

            //Kiểm tra header và token
            final  String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            //Trích xuất thông tin từ token và xác thực
            final String token = authHeader.substring(7);
            final String username = jwtTokenUtil.extractUsername(token);

            if (username != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }
            //Tiếp tục chuỗi filter
            filterChain.doFilter(request,response);

        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {

        //Không cần qua xác thực
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/news", apiPrefix),"GET" ),
                Pair.of(String.format("%s/roles", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/login", apiPrefix),"POST"),
                Pair.of(String.format("%s/users/register", apiPrefix),"POST")
//                Pair.of(String.format("%s/news/{id}", apiPrefix),"GET")



        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        if (requestPath.startsWith(String.format("/%s/departments", apiPrefix))
        && requestMethod.equals("GET")) {
            if (requestPath.matches(String.format("/%s/departments/\\d+", apiPrefix))){
                return true;
            }

            if (requestPath.equals(String.format("/%s/departments",apiPrefix))){
                return true;
            }
        }

        for (Pair<String, String> bypassToken : bypassTokens) {
            if (request.getServletPath().contains(bypassToken.getFirst())&&
            request.getMethod().equals(bypassToken.getSecond())){
                return true;
            }
        }
        return false;
    }
}

package com.ly.filter;

import com.alibaba.fastjson.JSON;
import com.ly.Global;
import com.ly.helper.ErrorCode;
import com.ly.helper.Result;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class AccessFilter extends ZuulFilter{

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${jwt.key}")
    private String jwt_key;

    @Value("${xendit.token:4459e7d313f232f38ec15ba097b654d905caeb221fc2e71e73e163729599fcbc}")
    private String xendit_token;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        LOGGER.debug(" ============== request.getServletPath {}" , request.getServletPath());
        LOGGER.debug(" ============== request.getHeaders {} " , request.getHeaderNames());

        if (request.getServletPath().contains("login")
                || request.getServletPath().contains("find-all-banner")
                || request.getServletPath().contains("all-data")
                || request.getServletPath().contains("versionnew")
                ) {
            return null;
        }

        if (request.getServletPath().contains("loanCallback")
                || request.getServletPath().contains("repayment-callback")) {

            String token = request.getHeader("token");
            LOGGER.debug(" ==============  loanCallback token {} " , token);

            if (StringUtils.hasText(token)){
                if (xendit_token.equals(token)){
                    return null;
                }
            }
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(200);
            Result result =  new Result(ErrorCode.SESSION_EXPIRE.getCode(), ErrorCode.SESSION_EXPIRE.getMessage());
            ctx.setResponseBody(JSON.toJSONString(result));
            return null;
        }

        String token = request.getHeader("token");

        //TODO 查询Redis
        if (StringUtils.hasText(token)) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(jwt_key)
                        .parseClaimsJws(token)
                        .getBody();
                String userId = claims.get(Global.USER_ID).toString();
                StringBuilder sb = new StringBuilder();
                String key = sb.append("userId_").append(userId).toString();
                ValueOperations<String, String> operations = redisTemplate.opsForValue();
                // 缓存存在
                boolean hasKey = redisTemplate.hasKey(key);
                if (hasKey) {
                    String redisToken = operations.get(key);
                    if (!token.equals(redisToken)){
                        LOGGER.info("toekn ==== {} ",token);
                        LOGGER.info("redisToken ==== {} ",redisToken);
                        ctx.setSendZuulResponse(false);
                        ctx.setResponseStatusCode(200);
                        Result result =  new Result(ErrorCode.SESSION_ERROR.getCode(), ErrorCode.SESSION_ERROR.getMessage());
                        ctx.setResponseBody(JSON.toJSONString(result));
                        return null;
                    }
                }else{
                    LOGGER.info("nokey ==== {} ",key);
                    ctx.setSendZuulResponse(false);
                    ctx.setResponseStatusCode(200);
                    Result result =  new Result(ErrorCode.SESSION_ERROR.getCode(), ErrorCode.SESSION_ERROR.getMessage());
                    ctx.setResponseBody(JSON.toJSONString(result));
                    return null;
                }

                ctx.addZuulRequestHeader(Global.USER_ID, claims.get(Global.USER_ID).toString());
                ctx.addZuulRequestHeader(Global.PHONE, claims.get(Global.PHONE).toString());
            }catch (Exception e){
                LOGGER.info("key error ==== {} ",token);
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(200);
                Result result =  new Result(ErrorCode.SESSION_ERROR.getCode(), ErrorCode.SESSION_ERROR.getMessage());
                ctx.setResponseBody(JSON.toJSONString(result));
                return null;
            }

        }else{
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(200);
            Result result =  new Result(ErrorCode.SESSION_EXPIRE.getCode(), ErrorCode.SESSION_EXPIRE.getMessage());
            ctx.setResponseBody(JSON.toJSONString(result));
            return null;
        }
        return null;
    }
}

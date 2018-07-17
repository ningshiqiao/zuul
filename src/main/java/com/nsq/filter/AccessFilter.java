package com.nsq.filter;

import com.alibaba.fastjson.JSON;
import com.nsq.Global;
import com.nsq.helper.ErrorCode;
import com.nsq.helper.Result;
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

        LOGGER.info(" ============== request.getServletPath {}" , request.getServletPath().toString());
        String productName = request.getHeader("PRODUCT_NAME");
        if (!StringUtils.hasText(productName)){
            productName = "";
        }
        String version = request.getHeader("VERSION");
        if (!StringUtils.hasText(version)){
            version = "";
        }
        if (request.getServletPath().contains("login")
                ) {
            ctx.addZuulRequestHeader(Global.VERSION, version);
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

package com.ly;

import com.ly.filter.AccessFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
@EnableEurekaClient
@EnableFeignClients
@SpringCloudApplication
public class WebapiApplication  {

	public static void main(String[] args) {
        SpringApplication.run(WebapiApplication.class, args);
	}

	@Bean
    public AccessFilter accessFilter() {
	    return new AccessFilter();
    }

}

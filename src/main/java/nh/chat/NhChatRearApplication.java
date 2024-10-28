package nh.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@MapperScan("nh.chat.mapper")
public class NhChatRearApplication {

	public static void main(String[] args) {
		SpringApplication.run(NhChatRearApplication.class, args);
	}
	@Configuration
	public static class CorsConfig implements WebMvcConfigurer {
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**")
					.allowedOrigins("**")
					.allowedMethods("GET","POST","DELETE","PUT")
					.allowedOriginPatterns("**")
					.maxAge(3600)
					.allowedHeaders("Content-Type", "Authorization");
		}
	}
}

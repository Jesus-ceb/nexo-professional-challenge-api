package com.jc.professional_challenge_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS is configured in SecurityConfig (corsConfigurationSource).
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); //sugerencia usar la propiedad 'app.upload.dir', si se cambia la carpeta no se desincroniza.

    }


}

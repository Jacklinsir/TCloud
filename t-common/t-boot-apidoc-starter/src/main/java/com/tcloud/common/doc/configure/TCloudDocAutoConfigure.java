package com.tcloud.common.doc.configure;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.Lists;
import com.tcloud.common.doc.properties.TCloudDocProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;


@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
@EnableConfigurationProperties(TCloudDocProperties.class)
@ConditionalOnProperty(value = "tcloud.doc.enable", havingValue = "true", matchIfMissing = true)
public class TCloudDocAutoConfigure
{
    private final TCloudDocProperties tCloudDocProperties;

    public TCloudDocAutoConfigure(TCloudDocProperties tCloudDocProperties)
    {
        this.tCloudDocProperties = tCloudDocProperties;
    }

    @Bean
    @Order(-1)
    public Docket groupRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(tCloudDocProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build().securityContexts(Lists.newArrayList(securityContext())).securitySchemes(Lists.<SecurityScheme>newArrayList(apiKey()));
    }

    private ApiKey apiKey()
    {
        return new ApiKey("BearerToken", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    private ApiInfo groupApiInfo() {
        String description = String.format("<div style='font-size:%spx;color:%s;'>%s</div>",
                tCloudDocProperties.getDescriptionFontSize(), tCloudDocProperties.getDescriptionColor(), tCloudDocProperties.getDescription());

        Contact contact = new Contact(tCloudDocProperties.getName(), tCloudDocProperties.getUrl(), tCloudDocProperties.getEmail());

        return new ApiInfoBuilder()
                .title(tCloudDocProperties.getTitle())
                .description(description)
                .termsOfServiceUrl(tCloudDocProperties.getTermsOfServiceUrl())
                .contact(contact)
                .license(tCloudDocProperties.getLicense())
                .licenseUrl(tCloudDocProperties.getLicenseUrl())
                .version(tCloudDocProperties.getVersion())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
    }
}

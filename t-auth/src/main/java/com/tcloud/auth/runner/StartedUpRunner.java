package com.tcloud.auth.runner;

import com.tcloud.common.core.toolkit.T;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Description:
 * <br/>
 * StartedUpRunner
 * Created by laiql on 2020/10/13 17:51.
 */
@Component
@RequiredArgsConstructor
public class StartedUpRunner implements ApplicationRunner {

    private final ConfigurableApplicationContext context;
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        if (context.isActive()) {
            T.util().bootUtil().printSystemUpBanner(environment);
        }
    }
}

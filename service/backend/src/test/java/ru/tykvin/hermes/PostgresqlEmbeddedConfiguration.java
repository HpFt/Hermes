package ru.tykvin.hermes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.SocketUtils;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_6;

@Slf4j
@Configuration
public class PostgresqlEmbeddedConfiguration implements BeanFactoryPostProcessor {
    private static final String TEST_DB_NAME = "test-db";
    final EmbeddedPostgres postgres = new EmbeddedPostgres(V9_6);
    private volatile int databasePort = 0;

    @Bean(destroyMethod = "stop")
    public EmbeddedPostgres embeddedPostgres() {
        return postgres;
    }

    @Bean
    public DataSource dataSource() {
        return new SimpleDriverDataSource(new org.postgresql.Driver(),
                "jdbc:postgresql://127.0.0.1:" + databasePort + "/" + TEST_DB_NAME, TEST_DB_NAME, TEST_DB_NAME);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        databasePort = SocketUtils.findAvailableTcpPort();
        log.info("Start postgresql: databasePort={}", databasePort);
        try {
//          postgres.start("127.0.0.1", databasePort, componentName, componentName, componentName);
            postgres.start(EmbeddedPostgres.cachedRuntimeConfig(Paths.get(System.getProperty("java.io.tmpdir"))),
                    "127.0.0.1", databasePort, TEST_DB_NAME, TEST_DB_NAME, TEST_DB_NAME, Arrays.asList(
                            "-E", "SQL_ASCII",
                            "--locale=C",
                            "--lc-collate=C",
                            "--lc-ctype=C"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

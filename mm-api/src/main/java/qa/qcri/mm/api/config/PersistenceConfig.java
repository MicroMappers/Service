package qa.qcri.mm.api.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Reponsible for jpa configurations
 */
@Profile("default")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "qa.qcri.mm.api.repository")
public class PersistenceConfig {

	@Value("${jdbc.driver}")
	private String driver;

	@Value("${jdbc.url}")
	private String url;

	@Value("${jdbc.user}")
	private String username;

	@Value("${jdbc.password}")
	private String password;

	@Value("${hibernate.dialect}")
	private String dialect;

	@Value("${aidr_predict.hibernate.dialect}")
	private String aidrPredictDialect;

	@Value("${aidr_predict.jdbc.driver}")
	private String aidrPredictDriver;

	@Value("${aidr_predict.dataSource.persistentUnitName}")
	private String aidrPredictPersistentUnitName;

	@Value("${aidr_predict.jdbc.url}")
	private String aidrPredictUrl;

	@Value("${aidr_predict.jdbc.user}")
	private String aidrPredictUsername;

	@Value("${aidr_predict.jdbc.password}")
	private String aidrPredictPassword;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(url);
		dataSource.setDriverClassName(driver);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory
				.setPackagesToScan(new String[] { "qa.qcri.mm.api.entity" });
		sessionFactory.setHibernateProperties(hibernateProperties());

		return sessionFactory;
	}

	@Bean(destroyMethod = "close")
	public DataSource aidrPredictDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(aidrPredictUrl);
		dataSource.setDriverClassName(aidrPredictDriver);
		dataSource.setUsername(aidrPredictUsername);
		dataSource.setPassword(aidrPredictPassword);
		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean aidrPredictSessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(aidrPredictDataSource());
		sessionFactory
				.setPackagesToScan(new String[] { "qa.qcri.mm.api.aidr_predict_entity" });
		sessionFactory.setHibernateProperties(aidrPredictHibernateProperties());

		return sessionFactory;
	}

	Properties hibernateProperties() {
		return new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto", "create");
				setProperty("hibernate.dialect", dialect);
				setProperty("hibernate.show_sql", "true");
			}
		};
	}

	Properties aidrPredictHibernateProperties() {
		return new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto", "update");
				setProperty("hibernate.dialect", aidrPredictDialect);
				setProperty("hibernate.show_sql", "true");
			}
		};
	}

	/* configuring jpa vendor adapter */
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.MYSQL);
		vendorAdapter.setDatabasePlatform(dialect);
		vendorAdapter.setShowSql(true);
		vendorAdapter.setGenerateDdl(true);
		return vendorAdapter;
	}

	/*
	 * protected Properties getJpaProperties() { Properties properties = new
	 * Properties(); properties.setProperty(NAMING_STRATEGY,
	 * RespectfulImprovedNamingStrategy.class.getName()); return properties; }
	 */

	/* Configuring Entity manager factory */
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(aidrPredictDataSource());
		entityManagerFactory
				.setPackagesToScan("qa.qcri.mm.api.aidr_predict_entity");
		entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
		// entityManagerFactory.setJpaProperties(getJpaProperties());
		entityManagerFactory
				.setPersistenceUnitName(aidrPredictPersistentUnitName);
		entityManagerFactory.afterPropertiesSet();
		return entityManagerFactory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory());
	}

	@Bean
	public PersistenceExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
}

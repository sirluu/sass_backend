package jp.co.japantaxi.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(value = "jp.co.stockholm.mapper.stockholm",
    sqlSessionFactoryRef = "stockholmSqlSessionFactory")
@EnableTransactionManagement
public class StockholmConfig {

  @Bean(name = "stockholmDataSource")
  @Primary
  @ConfigurationProperties(prefix = "stockholm.datasource")
  public DataSource stockholmDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "stockholmSqlSessionFactory")
  @Primary
  public SqlSessionFactory stockholmSqlSessionFactory(
      @Qualifier("stockholmDataSource") DataSource stockholmDataSource,
      ApplicationContext applicationContext) throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(stockholmDataSource);
    sqlSessionFactoryBean.setMapperLocations(
        applicationContext.getResources("classpath:jp/co/japantaxi/mapper/stockholm/*.xml"));
    return sqlSessionFactoryBean.getObject();
  }

  @Bean(name = "stockholmSqlSessionTemplate")
  @Primary
  public SqlSessionTemplate stockholmSqlSessionTemplate(
      SqlSessionFactory stockholmSqlSessionFactory) {
    return new SqlSessionTemplate(stockholmSqlSessionFactory);
  }
}

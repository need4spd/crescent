package com.tistory.devyongsik.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tistory.devyongsik.config.SpringApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml",
 "classpath:spring/action-config.xml",
 "classpath:spring/spring-config.xml"})
public class InitialLizeDataBaseUtil {

	@Test
	public void initDatabase() throws SQLException, IOException {
		DataSource dataSource = SpringApplicationContext.getBean("dataSource", DataSource.class);
		Connection con = dataSource.getConnection();
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("ddl_dml.sql");
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader br = new BufferedReader(isr);
		
		String line = "";
		
		while((line = br.readLine()) != null) {
			System.out.println(line);
		}
		
	}
}

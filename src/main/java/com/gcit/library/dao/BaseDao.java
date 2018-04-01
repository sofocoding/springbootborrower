/**
 * 
 */
package com.gcit.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author gcit
 *
 */
@Component
public class BaseDao <T>{
	
	@Autowired
	public JdbcTemplate mysqlTemplate;
}

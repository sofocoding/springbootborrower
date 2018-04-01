package com.gcit.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.gcit.library.model.Branch;
import com.gcit.library.model.Book;
import com.gcit.library.model.Branch;

@Component
public class BranchDao extends BaseDao<Branch> implements ResultSetExtractor<List<Branch>>{


	public void updateBranch(Branch branch) {
		mysqlTemplate.update("update tbl_library_branch set branchName = ?, branchAddress = ? where branchId = ?;",
				new Object[] {branch.getName(),branch.getAddress(),branch.getId()});
		mysqlTemplate.update("delete from tbl_book_copies where branchId = ?;", new Object[] {branch.getId()});
		for(Book book : branch.getBooks()) {
			mysqlTemplate.update("insert into tbl_book_copies values(?,?,?);", new Object[] {book.getId(),branch.getId(),book.getCopies()});
		}
	}
	
	public List<Branch> getBranches(String sql, Object[]values) {
		return mysqlTemplate.query(sql,values,this);
	}
	
	public Integer addBranchGetPK(Branch branch)  {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		mysqlTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException  {
		        PreparedStatement statement = con.prepareStatement("INSERT INTO tbl_library_branch (branchName,branchAddress) VALUES (?,?) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, branch.getName());
		        statement.setString(2, branch.getAddress());
		        return statement;
		    }
		}, holder);

		return holder.getKey().intValue();
	}
	
	public List<Branch> extractData(ResultSet rs) throws SQLException  {
		List<Branch> branchs = new LinkedList<Branch>();
		Branch branch = null;
		int size = rs.getMetaData().getColumnCount();
		while(rs.next()) {
			branch = new Branch();
			branch.setId(rs.getInt(1));
			branch.setName(rs.getString(2));
			branch.setAddress(rs.getString(3));
			if(size == 4) {
				branch.setCopies(rs.getInt(4));
			}
			branchs.add(branch);
		}
		return branchs;
	}

	public void deleteBranchByPK(Integer branchId) {
		mysqlTemplate.update("delete from tbl_library_branch where branchId = ?;", new Object[] {branchId});
	}

	public void insertBranch(Branch branch) {
		for(Book book : branch.getBooks()) {
			mysqlTemplate.update("insert into tbl_book_copies values(?,?,?);", new Object[] {book.getId(),branch.getId(),book.getCopies()});
		}	
	}

}

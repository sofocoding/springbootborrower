package com.gcit.library.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.library.dao.BookDao;
import com.gcit.library.dao.BranchDao;
import com.gcit.library.model.Branch;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class BranchService {
	
	@Autowired
	BranchDao brdao;
	
	@Autowired
	BookDao bdao;
	
	@Transactional
	@RequestMapping(value="/branches",method=RequestMethod.GET)
	public ResponseEntity<Object> getAllBranch(@RequestParam(value="pageNo",required=false) Integer pageNo,
			@RequestParam(value="search",required=false) String search){
		if(search == null || search.length() == 0) search = null;
		StringBuffer str = new StringBuffer("select * from tbl_library_branch");
		List<Branch> branches = null;
		try {
			if(pageNo != null && search != null) {
				String query = "%"+search+"%";
				str.append(" where branchName = ? limit ?,?");
				branches = brdao.getBranches(str.toString(),new Object[] {query,(pageNo-1)*10,10});
			} else {
				if(search != null) {
					String query = "%"+search+"%";
					str.append(" where branchName = ?");
					branches = brdao.getBranches(str.toString(),new Object[] {query});
				} else if(pageNo != null){
					str.append(" limit ?,?");
					branches = brdao.getBranches(str.toString(), new Object[] {(pageNo-1)*10,10});
				} else {
					branches = brdao.getBranches(str.toString(), null);
				}
			}
			for(Branch branch: branches) {
				branch.setBooks(bdao.getBooks("select book.bookId,book.title,copy.noOfCopies from tbl_book book\n" + 
						"join tbl_book_copies copy on copy.bookId = book.bookId\n" + 
						"where copy.branchId = ?;", new Object[] {branch.getId()}));
			}
			return new ResponseEntity<Object>(branches,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/branches/{branchId}",method=RequestMethod.GET)
	public ResponseEntity<Object> getBranchByPK(@PathVariable(value="branchId") Integer branchId){
		StringBuffer str = new StringBuffer("select * from tbl_library_branch where branchId = ?");
		try {
			List<Branch> branches = brdao.getBranches(str.toString(),new Object[] {branchId});
			if(branches.size() != 0) {
				Branch branch = branches.get(0);
				branch.setBooks(bdao.getBooks("select book.bookId,book.title,copy.noOfCopies from tbl_book book\n" + 
						"join tbl_book_copies copy on copy.bookId = book.bookId\n" + 
						"where copy.branchId = ?;", new Object[] {branch.getId()}));
				return new ResponseEntity<Object>(branch,HttpStatus.OK);
			}
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@RequestMapping(value="/branches/{branchId}",method=RequestMethod.PUT)
	public ResponseEntity<Object> updateBranch(@RequestBody Branch branch, @PathVariable(value="branchId") Integer branchId){
		try {
			brdao.updateBranch(branch);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create("/branches/"+branchId));
			return new ResponseEntity<Object>(headers,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@Transactional
//	@RequestMapping(value="/branches/{branchId}",method=RequestMethod.DELETE,produces="application/json")
//	public ResponseEntity<Object> deleteBranch(@PathVariable(value="branchId") Integer branchId){
//		try {
//			brdao.deleteBranchByPK(branchId);
//			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
//	@Transactional
//	@RequestMapping(value="/branches",method=RequestMethod.POST, consumes= {"application/json"},produces= {"application/json"})
//	public ResponseEntity<Object> addBranch(@RequestBody Branch branch){
//		try {
//			Integer branchId = brdao.addBranchGetPK(branch);
//			branch.setId(branchId);
//			brdao.insertBranch(branch);
//			HttpHeaders headers = new HttpHeaders();
//			headers.setLocation(URI.create("/branchs/"+branchId));
//			return new ResponseEntity<Object>(HttpStatus.CREATED);
//		} catch(Exception e) {
//			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
}

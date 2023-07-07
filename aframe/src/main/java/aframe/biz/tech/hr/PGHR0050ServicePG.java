package aframe.biz.tech.hr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.antlr.grammar.v3.ANTLRParser.finallyClause_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import aframe.biz.tech.mapif.hr.PGHR0050Mapper;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

/*
 * 프로그램 관리 처리 클래스
 */
@Service("PGHR0050ServicePG")
public class PGHR0050ServicePG extends EgovAbstractServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(PGHR0050ServicePG.class);
	
	@Resource(name = "PGHR0050Mapper")
	PGHR0050Mapper PGHR0050DAO;

	/**
	 * 직원 등록
	 * @param param 조회범위 및 조회조건
	 * @return
	 * @throws Exception
	 */
	public void insertMember(Map<?, ?> param) throws RuntimeException, Exception {
		PGHR0050DAO.insertMember(param);
	}
	
  /**
	 * 직원 조회
	 * @param param 조회범위 및 조회조건
	 * @return
	 * @throws Exception
	 */
	
	 public Map<?, ?> findUsermember(Map<?, ?> param) throws RuntimeException, Exception { 
		 return PGHR0050DAO.findUsermember(param); }
	
	/**
	 * 직원 삭제
	 * @param param 조회범위 및 조회조건
	 * @return
	 * @throws Exception
	 */
	public void deletemember(Map<?, ?> param) throws RuntimeException, Exception {
		PGHR0050DAO.deletemember(param);
	}

	/**
	 * 직원 수정
	 * @param param 조회범위 및 조회조건
	 * @return
	 * @throws Exception
	 */
	public void updatemember(Map<?, ?> param) throws RuntimeException, Exception {
		PGHR0050DAO.updatemember(param);
	}
	
	/**
	 * 직원 갯수 조회(중복체크)
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public int selectmemberCnt(Map<?, ?> param) throws RuntimeException, Exception {
		return PGHR0050DAO.selectmemberCnt(param);
	}
    
	/**
	 * 직원 조회
	 */
	public List<Map> findmember(Map<?, ?> param) throws RuntimeException, Exception {
		return PGHR0050DAO.findmember(param);
	}
	
	public int findUsermemberlist(Map<?, ?> param) throws RuntimeException, Exception {
		return PGHR0050DAO.findUsermemberlist(param);
	}

}

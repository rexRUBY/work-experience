package aframe.biz.tech.mapif.hr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;



/*
 * 프로그램 Mapper
 */

@Mapper("PGHR0050Mapper")
public interface PGHR0050Mapper {
	
	public int selectProgrmCnt (Map<?, ?> param) throws RuntimeException, Exception;
	public void insertMember(Map<?, ?> param) throws RuntimeException, Exception;
	public Map<?, ?> findUsermember (Map<?,?> param) throws RuntimeException, Exception;
	
	/**
	 * 회원 삭제
	 *
	 * @param param
	 * @return
	 * @throws RuntimeException, Exception
	 */
	public int deletemember(Map<?, ?> param) throws RuntimeException, Exception;
	
	/**
	 * 회원 수정
	 *
	 * @param param
	 * @return
	 * @throws RuntimeException, Exception
	 */
	public int updatemember(Map<?, ?> param) throws RuntimeException, Exception;
	public int selectmemberCnt (Map<?, ?> param) throws RuntimeException, Exception;
	
	/**
	 * 회원 조회
	 */
	public List<Map> findmember(Map<?, ?> param) throws RuntimeException, Exception;
	public int findUsermemberlist(Map<?, ?> param) throws RuntimeException, Exception;
}

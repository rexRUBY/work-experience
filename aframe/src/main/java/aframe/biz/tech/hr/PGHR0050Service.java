package aframe.biz.tech.hr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.python.antlr.PythonParser.return_stmt_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.cj.x.protobuf.MysqlxCrud.Update;

import aframe.biz.tech.mapif.hr.PGHR0050Mapper;
import aframe.com.comm.page.Pager;
import aframe.com.infra.util.Validate;
import jnr.ffi.Struct.int16_t;

@Service("PGHR0050")
public class PGHR0050Service extends EgovAbstractServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(PGHR0050Service.class);
	private Locale locale = Locale.getDefault();

	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	@Autowired
	PGHR0050ServicePG PGHR0050ServicePG;

	/**
	 * 회원관리 회원 리스트 검색
	 * 
	 * @param rqstMap
	 * @return
	 * @throws Exception
	 */
	public ModelAndView index(Map<?, ?> rqstMap) throws Exception {
		ModelAndView mv = new ModelAndView();
		Map param = new HashMap<>();
		param = (Map<?, ?>) rqstMap;

		int pageNo = MapUtils.getIntValue(rqstMap, "df_curr_page");
		int rowSize = MapUtils.getIntValue(rqstMap, "df_row_per_page");
		String initSearchYn = MapUtils.getString(rqstMap, "init_search_yn");

		int totalRowCnt = 0;
		if (Validate.isNotEmpty(initSearchYn)) {
			totalRowCnt = PGHR0050ServicePG.findUsermemberlist(param);
		}

		// 페이저 빌드
		Pager pager = new Pager.Builder().pageNo(pageNo).totalRowCount(totalRowCnt).rowSize(rowSize).build();
		pager.makePaging();
		param.put("limitFrom", pager.getLimitFrom());
		param.put("limitTo", pager.getLimitTo());

		int start = (pageNo - 1) * rowSize;
		int end = start + rowSize - 1;
		List<Map> userList = null;
		List<Map> userListLimit = new ArrayList<>();

		if (Validate.isNotEmpty(initSearchYn)) {
		    userList = PGHR0050ServicePG.findmember(param);
		    for (int i = start; i <= end && i < userList.size(); i++) {
		    	try {
		        userListLimit.add(userList.get(i));
		    	} catch (Exception e) {
		    		//Nothing
				}
		    }
		}

		mv.addObject("pager", pager);
		mv.addObject("pagerRS", pager.getRowSize()-1);  //선택된 페이지 행 갯수
		mv.addObject("pageNum", pager.getPageNo());    //현재 페이지 번호
		mv.addObject("userList", userListLimit);
		mv.setViewName("/admin/hr/BD_UIMAS0050");

		return mv;
	}

	// 등록 창 띄우기
	public ModelAndView programRegist(Map<?, ?> rqstMap) throws Exception {
		HashMap param = new HashMap();
		ModelAndView mView = new ModelAndView();
		mView.addObject("param", param);
		mView.setViewName("/admin/hr/BD_UIMAS0051");
		return mView;
	}

	// 등록 및 수정
	public ModelAndView programModify(Map<?, ?> rqstMap) throws Exception {

		HashMap param = new HashMap();

		String adPK = MapUtils.getString(rqstMap, "ad_PK");
		param.put("adPK", adPK);
		
		
		Map<?, ?> userInfo = PGHR0050ServicePG.findUsermember(param);

		ModelAndView mv = new ModelAndView();
		mv.addObject("userInfo", userInfo);
		mv.setViewName("/admin/hr/BD_UIMAS0052");

		return mv;
	}

	// 등록 및 수정
	public ModelAndView processProgrm(Map<?, ?> rqstMap) throws Exception {
		HashMap progrmParam = new HashMap();
		HashMap autoParam = new HashMap();
		
		// 등록 정보
		String adPK = MapUtils.getString(rqstMap, "ad_PK"); // 사원번호
		String adname = MapUtils.getString(rqstMap, "ad_name"); // 이름
		String adrank = MapUtils.getString(rqstMap, "ad_rank"); // 직급
		String adss = MapUtils.getString(rqstMap, "ad_ss"); // 주민번호
		String adbank = MapUtils.getString(rqstMap, "ad_bank"); // 결제은행
		String adbanknum = MapUtils.getString(rqstMap, "ad_banknum"); // 결제계좌
		String startdt = MapUtils.getString(rqstMap, "start_dt"); // 입사일
		String adtel = MapUtils.getString(rqstMap, "ad_tel"); // 전화번호
		String adpmail = MapUtils.getString(rqstMap, "ad_pmail"); // 개인 이메일
		String adcmail = MapUtils.getString(rqstMap, "ad_cmail"); // 회사 이메일
		String adadd = MapUtils.getString(rqstMap, "ad_add"); // 주소

		progrmParam.put("adPK", adPK);
		progrmParam.put("adname", adname);
		progrmParam.put("adrank", adrank);
		progrmParam.put("adss", adss);
		progrmParam.put("adbank", adbank);
		progrmParam.put("adbanknum", adbanknum);
		progrmParam.put("startdt", startdt);
		progrmParam.put("adtel", adtel);
		progrmParam.put("adpmail", adpmail);
		progrmParam.put("adcmail", adcmail);
		progrmParam.put("adadd", adadd);

		// 등록, 수정 구분
		String insert_update = MapUtils.getString(rqstMap, "insert_update");
		
		
		try {
		// 등록
			if ("INSERT".equals(insert_update.toUpperCase())) {
				PGHR0050ServicePG.insertMember(progrmParam);
			}
			// 수정
			else {
				PGHR0050ServicePG.updatemember(progrmParam);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		HashMap indexMap = new HashMap();

		int pageNo = MapUtils.getIntValue(rqstMap, "df_curr_page");
		int rowSize = MapUtils.getIntValue(rqstMap, "df_row_per_page");

		indexMap.put("df_curr_page", pageNo);
		indexMap.put("df_row_per_page", rowSize);

		ModelAndView mView = index(indexMap);

		if ("INSERT".equals(insert_update.toUpperCase())) {
			mView.addObject("resultMsg",
					messageSource.getMessage("success.common.insert", new String[] { "프로그램" }, Locale.getDefault()));
		} else {
			mView.addObject("resultMsg",
					messageSource.getMessage("success.common.update", new String[] { "프로그램" }, Locale.getDefault()));
		}
		return mView;
	}

	// 정보 삭제
	public ModelAndView deleteProgrm(Map<?, ?> rqstMap) throws Exception {
		HashMap param = new HashMap();
		String adPK = MapUtils.getString(rqstMap, "ad_PK");

		// 정보 삭제
		PGHR0050ServicePG.deletemember(param);
		HashMap indexMap = new HashMap();

		int pageNo = MapUtils.getIntValue(rqstMap, "df_curr_page");
		int rowSize = MapUtils.getIntValue(rqstMap, "df_row_per_page");

		indexMap.put("df_curr_page", pageNo);
		indexMap.put("df_row_per_page", rowSize);

		ModelAndView mv = index(indexMap);
		mv.addObject("resultMsg",
				messageSource.getMessage("success.common.delete", new String[] { "프로그램" }, Locale.getDefault()));
		return mv;
	}
}

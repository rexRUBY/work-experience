package aframe.biz.tech.hr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import aframe.com.comm.page.Pager;
import aframe.com.comm.response.ExcelVO;
import aframe.com.comm.response.IExcelVO;
import aframe.com.comm.user.UserVO;
import aframe.com.infra.file.FileDAO;
import aframe.com.infra.file.FileVO;
import aframe.com.infra.file.UploadHelper;
import aframe.com.infra.system.GlobalConst;
import aframe.com.infra.util.DateFormatUtil;
import aframe.com.infra.util.ResponseUtil;
import aframe.com.infra.util.SessionUtil;
import aframe.com.infra.util.Validate;
import aframe.com.infra.web.DhtmlxGridCodi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.fdl.property.impl.EgovPropertyServiceImpl;

@Service("PGHR0120")
public class PGHR0120Service extends EgovAbstractServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(PGHR0120Service.class);

	private Locale locale = Locale.getDefault();

	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	@Resource(name = "messageSource")
	private MessageSource messageSource;
	
	@Autowired
	PGHR0050ServicePG PGHR0120ServicePG;
	
	@Resource(name = "filesDAO")
	private FileDAO fileDao;
	
	
	/**
	 * 회원관리 회원 리스트 검색
	 * 
	 * @param rqstMap
	 * @return
	 * @throws Exception
	 */
	public ModelAndView index(Map<?, ?> rqstMap) throws Exception
	{		
		ModelAndView mv = new ModelAndView();
		Map param = new HashMap<>();
		param = (Map<?, ?>) rqstMap;

		int pageNo = MapUtils.getIntValue(rqstMap, "df_curr_page");
		int rowSize = MapUtils.getIntValue(rqstMap, "df_row_per_page");
		String initSearchYn = MapUtils.getString(rqstMap, "init_search_yn");

		int totalRowCnt = 0;
		 if (Validate.isNotEmpty(initSearchYn)) { 
			 totalRowCnt = PGHR0120ServicePG.findUsermemberlist(param); 
		 }

		// 페이저 빌드
		Pager pager = new Pager.Builder().pageNo(pageNo).totalRowCount(totalRowCnt).rowSize(rowSize).build();
		pager.makePaging();		
		param.put("limitFrom", pager.getLimitFrom());
		param.put("limitTo", pager.getLimitTo());
		
		List<Map> userList = null;
		if (Validate.isNotEmpty(initSearchYn)) {
			userList = PGHR0120ServicePG.findmember(param);
		}

		mv.addObject("pager", pager);
		mv.addObject("userList", userList);
		mv.setViewName("/admin/ms/BD_UIMSA0120");

		return mv;
	}
	
	// 등록 창 띄우기
		public ModelAndView programRegist(Map<?,?> rqstMap) throws Exception {
			
			HashMap param = new HashMap();
			
			ModelAndView mv = new ModelAndView();
			mv.addObject("param", param);
			
			mv.setViewName("/admin/ms/BD_UIMSA0121");
			return mv;
			
		}

		
		// 수정/삭제 View 띄우기
		public ModelAndView programModify(Map<?, ?> rqstMap) throws Exception {
			
			HashMap param = new HashMap();
			HashMap indexValue = new HashMap();
			
			int df_curr_page = MapUtils.getIntValue(rqstMap, "df_curr_page");
			int df_row_per_page = MapUtils.getIntValue(rqstMap, "df_row_per_page");
			int limitFrom = MapUtils.getIntValue(rqstMap, "limitFrom");
			int limitTo = MapUtils.getIntValue(rqstMap, "limitTo");
			
			String adPK = MapUtils.getString(rqstMap, "PK");
			
			param.put("adPK", adPK);
			indexValue.put("df_curr_page", df_curr_page);
			indexValue.put("df_row_per_page", df_row_per_page);
			
			Map<?, ?> userInfo = PGHR0120ServicePG.findUsermember(param);
					
			ModelAndView mv = new ModelAndView();
			
			mv.addObject("indexValue", indexValue);
			mv.addObject("userInfo", userInfo);
			
			mv.setViewName("/admin/ms/BD_UIMSA0122");
			
			return mv;
		}
		
		
		// 등록 및 수정
		public ModelAndView processProgrm (Map<?,?> rqstMap) throws Exception {
			HashMap progrmParam = new HashMap();
			HashMap authorParam = new HashMap();
			
			// 등록자 정보
			UserVO user = SessionUtil.getUserInfo();
			progrmParam.put("register", user.getUserNo());
			progrmParam.put("updusr", user.getUserNo());
			authorParam.put("register", user.getUserNo());
			authorParam.put("updusr", user.getUserNo());
			
			// 프로그램 등록 정보
			String adPK = MapUtils.getString(rqstMap, "PK");							// 사원번호
			String adname = MapUtils.getString(rqstMap, "name");						// 이름
			String adrank = MapUtils.getString(rqstMap, "rank");						// 직급
			String adss = MapUtils.getString(rqstMap, "ssnum");							// 주민번호
			String adbank = MapUtils.getString(rqstMap, "bank");						// 결제은행
			String adbanknum = MapUtils.getString(rqstMap, "banknum");				// 결제계좌
			String startdt = MapUtils.getString(rqstMap, "indate");					// 입사일
			String adtel = MapUtils.getString(rqstMap, "tel");						// 전화번호
			String adpmail = MapUtils.getString(rqstMap, "pmail");					// 개인 이메일
			String adcmail = MapUtils.getString(rqstMap, "cmail");					// 회사 이메일
			String adadd = MapUtils.getString(rqstMap, "address");						// 주소
			
			
			
			progrmParam.put("adPK", adPK);                         //사원번호
			progrmParam.put("adname", adname);                 //이름
			progrmParam.put("adrank", adrank);                    //직급
			progrmParam.put("adss", adss);                            //주민번호
			progrmParam.put("adbank", adbank);                   //결제은행
			progrmParam.put("adbanknum", adbanknum);     //결제계좌
			progrmParam.put("startdt", startdt);                    //입사일
			progrmParam.put("adtel", adtel);                           //전화번호
			progrmParam.put("adpmail", adpmail);                  //개인 이메일
			progrmParam.put("adcmail", adcmail);                   //회사 이메일
			progrmParam.put("adadd", adadd);                        //주소
			
			
			// 등록,수정 구분
			String insert_update = MapUtils.getString(rqstMap, "insert_update");

			// 프로그램 등록
			if("INSERT".equals(insert_update.toUpperCase())) {
				PGHR0120ServicePG.insertMember(progrmParam);
			}
			// 프로그램 수정
			else {
				// 프로그램 수정
				PGHR0120ServicePG.updatemember(progrmParam);
			} 
			
			HashMap indexMap = new HashMap(); 
			
			int pageNo = MapUtils.getIntValue(rqstMap, "df_curr_page");
			int rowSize = MapUtils.getIntValue(rqstMap, "df_row_per_page");
			String searchJobSe = MapUtils.getString(rqstMap, "searchJobSe");
			String searchProgramNm = MapUtils.getString(rqstMap, "searchProgramNm");
			
			indexMap.put("df_curr_page", pageNo);
			indexMap.put("df_row_per_page", rowSize);
			indexMap.put("searchJobSe", searchJobSe);
			indexMap.put("searchProgramNm", searchProgramNm);
			
			ModelAndView mv = index(indexMap);
			
			if("INSERT".equals(insert_update.toUpperCase())) {
				mv.addObject("resultMsg", messageSource.getMessage("success.common.insert", new String[] {"프로그램"}, Locale.getDefault()));
			} else {
				mv.addObject("resultMsg", messageSource.getMessage("success.common.update", new String[] {"프로그램"}, Locale.getDefault()));
			}
			
			return mv;
		}
		
		//프로그램, 권한 정보 삭제
		public ModelAndView deleteProgrm (Map <?, ?> rqstMap) throws Exception {
			HashMap param = new HashMap();
			
			String adPK = MapUtils.getString(rqstMap, "PK");
			
			param.put("adPK", adPK);
			
			// 프로그램 삭제
			PGHR0120ServicePG.deletemember(param);
			
			HashMap indexMap = new HashMap();
			
			int pageNo = MapUtils.getIntValue(rqstMap, "df_curr_page");
			int rowSize = MapUtils.getIntValue(rqstMap, "df_row_per_page");
			String searchJobSe = MapUtils.getString(rqstMap, "searchJobSe");
			String searchProgramNm = MapUtils.getString(rqstMap, "searchProgramNm");
			
			indexMap.put("df_curr_page", pageNo);
			indexMap.put("df_row_per_page", rowSize);
			indexMap.put("searchJobSe", searchJobSe);
			indexMap.put("searchProgramNm", searchProgramNm);
			
			ModelAndView mv = index(indexMap);
			mv.addObject("resultMsg", messageSource.getMessage("success.common.delete", new String[] {"프로그램"}, Locale.getDefault()));
			return mv;
		} 
		
		//그리드로 DB 불러오기
		public ModelAndView countSess(Map<?, ?> rqstMap) throws Exception

		{

			ModelAndView mv = new ModelAndView();
			Map param = new HashMap<>();
			param = (Map<?, ?>) rqstMap;
			
			List<Map> userList = PGHR0120ServicePG.findmember(param);
			String count = DhtmlxGridCodi.MaptoJson(userList); 

			return ResponseUtil.responseText(mv, count);

		}
		
		
		// 프로그램 ID 입력 체크
		public ModelAndView checkProgrmId (Map <?, ?> rqstMap) throws Exception {
			
			HashMap param = new HashMap();
			
			String adPK = MapUtils.getString(rqstMap, "PK");
			
			param.put("adPK", adPK);
			
			ModelAndView mv = new ModelAndView();

			if (PGHR0120ServicePG.selectmemberCnt(param) > 0) {
				return ResponseUtil.responseJson(mv, false);
			}

			return ResponseUtil.responseJson(mv, true);
	    } 
		
		
		/**
		 * 엑셀 다운로드
		 * @param rqstMap
		 * @return
		 * @throws Exception 
		 */
		public ModelAndView excelStatics(Map rqstMap) throws Exception {
			ModelAndView mv = new ModelAndView();

			// 타이틀
			ArrayList<String> headers = new ArrayList<String>();

			// 기업현황/매출형태 구분
			headers.add("사원번호");
			headers.add("이름");
			headers.add("주민번호");
			headers.add("거래은행");
			headers.add("계좌번호");
			headers.add("핸드폰번호");
			headers.add("회사메일");
			headers.add("주소");
			
			ArrayList<String> items = new ArrayList<String>();
			
			// jsp에서 받아온 명칭
			items.add("PK");                  //사원번호
			items.add("name");              //이름
			items.add("ssnum");             //주민번호
			items.add("bank");                //거래은행
			items.add("banknum");         //계좌번호
			items.add("tel");                    //핸드폰번호
			items.add("cmail");                //회사메일
			items.add("address");             //주소

			String[] arryHeaders = new String[] {};
			String[] arryItems = new String[] {};
			arryHeaders = headers.toArray(arryHeaders);
			arryItems = items.toArray(arryItems);
			
			List<Map> userList = PGHR0120ServicePG.findmember(rqstMap);
			
			mv.addObject("_headers", arryHeaders);
			mv.addObject("_items", arryItems);
			mv.addObject("_list", userList);
			
			IExcelVO excel = null;
			
				excel = new ExcelVO("직원정보_"+DateFormatUtil.getTodayFull());
		
			return ResponseUtil.responseExcel(mv, excel);
		}
		
		// 파일 업로드
		public ModelAndView insertFile(Map<?, ?> rqstMap) throws Exception {		
			ModelAndView mv = new ModelAndView();
			
			MultipartHttpServletRequest mptRequest = (MultipartHttpServletRequest)rqstMap.get(GlobalConst.RQST_MULTIPART);	
			List<MultipartFile> multipartFiles = mptRequest.getFiles("file");	
			
			logger.debug("-------------------------multipartFiles"+multipartFiles);
			
			List<FileVO> fileList = new ArrayList<FileVO>();
			fileList = UploadHelper.upload(multipartFiles, "test");
			
			logger.debug("^^^^^^^^^^^^^^^^^fileList:"+fileList);
			
			int fileSeq = fileDao.saveFile(fileList, 0);        
			logger.debug("fileSeq: "+fileSeq);
			
			mv.addObject("fileSeq",fileSeq);
			mv.setViewName("/admin/ms/BD_UIMSA0120");		
			return mv;
		}
		
		/**
		 * 파일업로드 수정
		 * @param rqstMap request parameters
		 * @return ModelAndView
		 * @throws Exception
		 */
		public ModelAndView updateFile(Map<?, ?> rqstMap) throws Exception {		
			ModelAndView mv = new ModelAndView();
			
			MultipartHttpServletRequest mptRequest = (MultipartHttpServletRequest)rqstMap.get(GlobalConst.RQST_MULTIPART);	
			List<MultipartFile> multipartFiles = mptRequest.getFiles("file");		
			List<FileVO> fileList = new ArrayList<FileVO>();
			fileList = UploadHelper.upload(multipartFiles, "test");
			int fileSeq = fileDao.saveFile(fileList, 1000);        
			logger.debug("fileSeq: "+fileSeq);
			
			mv.setViewName("/admin/ms/BD_UIMSA0120");		
			return mv;
		}

		
		// 파일 다운로드 테스트 시도중
		public ModelAndView updateFileDownBySeq(Map<?, ?> rqstMap) {
	    	ModelAndView mv = new ModelAndView();
	    	logger.debug("--------------- 001");
	    	
	        String fileVo = "aframe/src/webapp/download/기업직간접소유현황_20140301.xlsx";
	        
	        logger.debug("--------------- 002 : "+fileVo);
	        
	        mv.addObject(GlobalConst.FILE_DATA_KEY, fileVo);
	        logger.debug("------ 003");
	    	mv.setViewName(GlobalConst.DOWNLOAD_VIEW_NAME);
	    	logger.debug("------ 004");

	        return mv;        
	    }
	 
		
}


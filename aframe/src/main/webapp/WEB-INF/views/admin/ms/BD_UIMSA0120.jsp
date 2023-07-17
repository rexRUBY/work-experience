<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.tech.kr/jsp/jstl" prefix="ap"%>
<ap:globalConst />
<c:set var="svcUrl" value="${requestScope[SVC_URL_NAME]}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<title>정보</title>
<ap:jsTag type="web" items="jquery,toos,ui,form,validate,notice,msgBoxAdmin,colorboxAdmin" />
<ap:jsTag type="tech" items="acm,msg,util" />
<script type="text/javascript">
function countSess(){
 	$("#df_method_nm").val("countSess");
	var postData = {"df_method_nm":$("#df_method_nm").val(), "searchNum":$("#searchNum").val(), "searchName":$("#searchName").val()};
	
	$.ajax({
		type	 : "POST",
		url		 : "PGHR0120.do",
		data : postData,
		success	 : function(response){
        	try {
        		var iii = response;
        		console.log(iii);
        		var ddd = JSON.parse(iii);
        		console.log(ddd);
        		mygrid.clearAll();
        		mygrid.parse(ddd, "js");
            } catch (e) {
                if(response != null) {
                	jsSysErrorBox(response);
                } else {
                    jsSysErrorBox(e);
                }
                return;
			}
		}
	})
}

function fn_ajaxtest() {		
	var URLdata = "PGHR0120.do";
	var method_nm = "Ajax_gridData";
	const pdata = {
			searchNum : $("#searchNum").val(),
			searchName : $("#searchName").val(),
			testdata1 : 3,
			testdata2 : $("#searchName").val()
	};
		
	var resData = ajaxpost(URLdata, method_nm, pdata); // ajaxpost 호출
	//console.log(resData);
	mygrid.clearAll();
	mygrid.parse(resData.gridData1, "js");
}

//ajaxpost
var ajaxpost = function(URLdata, method_nm, pdata) {

	var postData = {
			df_method_nm : method_nm,
	};
	
	Object.assign(postData, pdata);
	console.log(postData);

	$.ajax({
		type : "POST",
		dataType : "text",
		url : URLdata,
		data : postData,
		async : false,
		success : function(response) {
			var res = response;
			console.log(res);
			var jsonres = JSON.parse(res);
			resData = jsonres;
			//console.log(resData);
			//alert(resData); // 확인
		},
		error : function(e) {
			alert("값을 가져오지 못했습니다.");
		}
	});

	return resData;
}

//ajaxpost
var ajaxSubmit = function(dataForm, URLdata, method_nm, pdata) {
	
	var resultData;
	console.log("df_method_nm : " +method_nm);
	$("#df_method_nm").val(method_nm);

	$(dataForm).ajaxSubmit({
        url      : URLdata,
        type     : "POST",
        dataType : "text",
        async    : false,
        data	 : pdata,
        beforeSubmit : function(){
        },
        success : function(response) {
			var res = response;
			var jsonres = JSON.parse(res);
			console.log(jsonres);
			resultData = jsonres;
		},
		error : function(e) {
			alert("값을 가져오지 못했습니다.");
		}		
	});

	return resultData;
}

<c:if test="${resultMsg != null}">
$(function(){
	jsMsgBox(null, "info", "${resultMsg}");
});
</c:if>
	/*
	 * 검색단어 입력 후 엔터 시 자동 submit
	 */
	function press(event) {
		if (event.keyCode == 13) {
			event.preventDefault();
			btn_program_get_list();
		}
	}

	/* 
	 * 목록 검색
	 */
	function btn_program_get_list() {
		var form = document.dataForm;
		
		form.df_curr_page.value = 1;
		form.df_method_nm.value = "";
		form.submit();
	}
	
	
	
	/* 
	 * 등록 화면 이동
	 */
	function btn_program_regist_view() {
		var form = document.dataForm;
		form.df_method_nm.value = "programRegist";
		form.submit();
	}
	/* 
	 * 수정,취소 화면 이동
	 */
	function btn_program_modify_view(PK) {
		var form = document.dataForm;
		form.df_method_nm.value = "programModify";
		document.getElementById("PK").value = PK;
		form.submit();
	}
	
	// 엑셀다운로드
	function downloadExcel() {

		$("#df_method_nm").val("excelStatics");
		$("#dataForm").submit();
	}
	
	//첨부파일 다운로드
	function downAttFile(){
		jsFiledownload("/PGHR0120.do",$("#df_method_nm").val("updateFileDownBySeq"));
		$("#dataForm").submit();
	}
	
	// 첨부파일 업로드
	function uploadfile() {
		$("#df_method_nm").val("insertFile");
		$("#upForm").submit(); 		
	}
	
</script>
<script type="text/javascript" src="${contextPath}/script/jquery/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${contextPath}/script/dhtmlxSuite/codebase/dhtmlx.js"></script>
<link rel="stylesheet" type="text/css" href="${contextPath}/script/dhtmlxSuite/codebase/dhtmlx.css"/>
<link rel="stylesheet" type="text/css" href="${contextPath}/nimbleRes/js/nimblegw.css"/>
<script type="text/javascript" src="${contextPath}/nimbleRes/js/dhtmlpost.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/nimbleRes/js/logKey.js"></script>
</head>

<body>
	<form name="upForm" id="upForm" action="PGHR0120.do" enctype="multipart/form-data" method="post">
		<input type="hidden" name="df_method_nm" value="insertFile" />
		<br><br><input type="file" name="file" value="찾아보기" />
		<div id="multiFilesListDiv" class="regist-file"></div>
		<a href="#" class="btn_page_admin" onclick="uploadfile()">업로드</a>
	</form>
	<form name="dataForm" id="dataForm" method="post">
		<ap:include page="param/defaultParam.jsp" />
		<ap:include page="param/dispParam.jsp" />
		<ap:include page="param/pagingParam.jsp" />

		<input type="hidden" name="PK" id="PK" />
		<input type="hidden" id="init_search_yn" name="init_search_yn" value="N" />

		<!--//content-->
		<div id="wrap">
			<div class="wrap_inn">
				<div class="contents">
					<!--// 타이틀 영역 -->
					<h2 class="menu_title">직원조회</h2>
					<!-- 타이틀 영역 //-->
					<!--// 검색 조건 -->
					<div class="search_top">
						<table cellpadding="0" cellspacing="0" class="table_search" summary="프로그램관리 검색">
							<caption>검색</caption>
							<colgroup>
								<col width="15%" />
								<col width="*" />
								<col width="*" />
							</colgroup>
							<tr>
								 <th scope="row">회원검색</th>
                                <td>     
                                	<input name="ad_search_word" id="ad_search_word" title="이름을 입력하세요." placeholder="이름을 입력하세요." type="text" style="width:200px" value="${param.ad_search_word}" />                        
                                </td>
                                <td>
									<p class="btn_src_zone">
										<a href="#" class="btn_search" onclick="btn_program_get_list()">검색</a>
									</p></td>
							</tr>
						</table>
					</div>
					<div class="block">
						<ap:pagerParam />
						<!--// 리스트 -->
						<br>
						<div class="list_zone">
							<table cellspacing="0" border="0" summary="프로그램관리 리스트">
								<caption>프로그램관리</caption>
								<colgroup>
									<col width="5%" />
									<col width="8%" />
									<col width="10%" />
									<col width="10%" />
									<col width="15%" />
									<col width="10%" />
									<col width="15%" />
									<col width="*" />
								</colgroup>
								<thead>
									<tr>
										<th scope="col">사원번호</th>
										<th scope="col">이름</th>
										<th scope="col">주민번호</th>
										<th scope="col">결제은행</th>
										<th scope="col">결제계좌</th>
										<th scope="col">연락처</th>
										<th scope="col">회사이메일</th>
										<th scope="col">주소</th>
									</tr>
								</thead>
								<tbody>
									<c:if test="${fn:length(userList) == 0}">
										<tr>
											<td colspan="11"><c:if test="${param.searchJobSe == '' || param.searchProgramNm == ''}">
													<spring:message code="info.nodata.msg" />
												</c:if> <c:if test="${param.searchJobSe != '' || param.searchProgramNm != ''}">
													<spring:message code="info.noresult.msg" />
												</c:if></td>
										</tr>
									</c:if>
									<c:forEach items="${userList}" var="userList" varStatus="status">
										<tr>
											<td >${userList.PK}</td> 
											<td><a href="#" class="btn_in_gray" onclick="btn_program_modify_view('<c:out value="${userList.PK}"/>');"><span>${userList.name}</span></a>
											</td> 
											<td>${userList.ssnum}</td>
											<td>${userList.bank}</td>
											<td>${userList.banknum}</td>
											<td>${userList.tel}</td>
											<td>${userList.cmail}</td>
											<td>${userList.address}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
						<!-- 리스트// -->
						<div class="btn_page_last">
							<a class="btn_page_admin" href="#" onclick="btn_program_regist_view()"><span>등록</span></a>
							<a class="btn_page_admin" href="#" onclick="downloadExcel();"><span>엑셀다운로드</span></a>
						</div>
						<!--// paginate -->
						<ap:pager pager="${pager}" />
						<!-- paginate //-->
					</div>
					
                  <br>
				</div>
				<!--content//-->
			</div>
		</div>

	</form>
</body>
</html>

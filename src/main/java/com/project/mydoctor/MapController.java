package com.project.mydoctor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.project.mydoctor.model.All_Hs;
import com.project.mydoctor.model.All_test;
import com.project.mydoctor.model.HdetailVO;
import com.project.mydoctor.model.Hospital;
import com.project.mydoctor.model.Work_hs;
import com.project.mydoctor.service.BookmarkService;
import com.project.mydoctor.service.HospitalService;

/**
 * @author 김건수
 * @맵관련
 *
 */
@Controller
public class MapController {
	private static final Logger logger = LoggerFactory.getLogger(MapController.class);
	private static final String radius = "3000";

	@Autowired
	private HospitalService hospitalService;

	@Autowired
	private BookmarkService bookmarkService;
	
	
	
	/**
	 * @author 김건수 
	 * @param Allquery
	 * @return
	 * @throws Exception
	 * @모든병원검색
	 */
	@RequestMapping(value = "Allquery.net",method = RequestMethod.POST)
	public String Allquery(String Allquery,@RequestParam(required = false,defaultValue = "1")int p,
			@RequestParam(required = false,defaultValue = "no")String kim,@RequestParam(required = false, defaultValue = "no")String kim2,@RequestParam(required = false, defaultValue = "no")String kim3, Model model)throws Exception {		
		Map<String, Object> all = new HashMap<String, Object>();
		ArrayList<All_Hs> all_one = new ArrayList<All_Hs>();
		Gson gson = new Gson();		
		
		try {
			all = Alldetail(URLEncoder.encode(Allquery,"UTF-8"),p,kim,kim2);			
			if((Long)all.get("total")==1) {						
				all_one.add((All_Hs) all.get("result_all"));
				model.addAttribute("all", all_one);				
				model.addAttribute("total", all.get("total"));	
				model.addAttribute("hidden", Allquery);
				if(p>=2 || kim3.equals("kim3"))					
					return "details/all_click";
			}else {			
				JSONObject a = (JSONObject) all.get("result_all");
				String real = a.toJSONString();
				All_test testing = gson.fromJson(real, All_test.class);
				model.addAttribute("all", testing.getItem());
				model.addAttribute("total", all.get("total"));
				model.addAttribute("hidden", Allquery);	
				if(p>=2 || kim3.equals("kim3")) 
					return "details/all_click";
				
			}
				
			
		}catch (NullPointerException e) {		
			model.addAttribute("msg", "검색된 결과가없습니다");
			model.addAttribute("hidden", Allquery);
			model.addAttribute("total", "0");
			if(kim3.equals("kim3")) 
				return "details/all_click";
			
		}
	
	
		return "details/allquery";
	}
	
	
	/**
	 * @author 김건수
	 * @param Allquery
	 * @return hdtailVO
	 * 
	 */
	public Map<String, Object>Alldetail(String all,int p,String kim,String kim2) throws Exception{
		Map<String, Object> result_hs = new HashMap<String, Object>();		
		All_Hs all_one = new All_Hs();		
		JSONParser jsonparser = new JSONParser();
		String url = "http://apis.data.go.kr/B551182/hospInfoService/getHospBasisList?numOfRows=10&_type=json&ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D"
				+ "&yadmNm="+all+"&pageNo="+p;
		if(!kim.equals("no")) 
		url +="&sidoCd="+kim;
		if(!kim2.equals("no")) 
		url +="&dgsbjtCd="+kim2;

		JSONObject jsonobject = (JSONObject) jsonparser.parse(readUrl(url));
		
		JSONObject json = (JSONObject) jsonobject.get("response");
		JSONObject body = (JSONObject) json.get("body");
		
		Long total = (Long) body.get("totalCount");
		if(total==1) {	
			try {
			JSONObject items = (JSONObject) body.get("items");				
			JSONObject item = (JSONObject) items.get("item");
			//ㅅㅄ ㅄㅄ ㅂㄷㅅ ㅂ ㅅㅄ ㅄㅄ ㅄ ㅄ ㅄ ㅄ ㄷㅄ ㄷ
			all_one.setClCd(items.get("clCd"));
			all_one.setAddr(item.get("addr"));
			all_one.setClCdNm(item.get("clCdNm"));
			all_one.setDrTotCnt(item.get("drTotCnt"));
			all_one.setEstbDd(item.get("estbDd"));
			all_one.setGdrCnt(item.get("gdrCnt"));
			all_one.setIntnCnt(item.get("intnCnt"));
			all_one.setPostNo(item.get("postNo"));
			all_one.setHospUrl(item.get("hospUrl"));
			all_one.setResdntCnt(item.get("resdntCnt"));
			all_one.setSdrCnt(item.get("sdrCnt"));
			all_one.setSgguCd(item.get("sgguCd"));
			all_one.setSgguCdNm(item.get("sgguCdNm"));
			all_one.setSidoCd(item.get("sidoCd"));
			all_one.setSidoCdNm(item.get("sidoCdNm"));
			all_one.setTelno(item.get("telno"));
			all_one.setXPos(item.get("XPos"));
			all_one.setYPos(item.get("YPos"));
			all_one.setYadmNm(item.get("yadmNm"));
			all_one.setYkiho(item.get("ykiho"));
			result_hs.put("result_all", all_one);
			result_hs.put("total",total);
			return result_hs;
			}catch (Exception e) {
				System.out.println("캐치 ㅡㅡ");
				return result_hs;
			}
			
		}else if(total==0) {			
			return null;
		}else {		
		try {
		JSONObject items = (JSONObject) body.get("items");		
		result_hs.put("result_all", items);		
		result_hs.put("total", total);	
		return result_hs;
		}catch (ClassCastException e) {
				System.out.println("개빡치네 캐치몇번째야");
				return result_hs;
			}
		}
		
	
	}
	
	
	@ResponseBody
	@RequestMapping(value = "favorites_add.net",method = RequestMethod.POST)
	public int favorites_add(HttpSession session,String yki){
			String member =(String)session.getAttribute("loginid");						
			Hospital result = hospitalService.getFavorites(yki);
			//System.out.println(result.getId());
			//System.out.println(result.getYadmNm());
			Map<String, String> fa= new HashMap<String, String>();
			fa.put("id", member);
			fa.put("hosid", result.getId());
			fa.put("hosname", result.getYadmNm());
			
			int insert = hospitalService.Fa_insert(fa);
			System.out.println(insert);
			
		return insert;
	}
	
	@ResponseBody
	@RequestMapping(value = "favorites_del.net",method = RequestMethod.POST)
	public int favorites_del(HttpSession session,String yki){
			String member =(String)session.getAttribute("loginid");						
			Hospital result = hospitalService.getFavorites(yki);
			System.out.println(result.getId());
			System.out.println(result.getYadmNm());
			Map<String, String> fa= new HashMap<String, String>();
			fa.put("id", member);
			fa.put("hosid", result.getId());
			fa.put("hosname", result.getYadmNm());
			
			int insert = hospitalService.Fa_delete(fa);
			System.out.println(insert);
			
		return insert;
		
		
	}
	
	/**
	 * @param req 좌표
	 * @param res
	 * @throws Exception
	 * @author 김건수
	 * @현재위치 좌표
	 */
	@RequestMapping(value = "intro_hs")
	public void intro_hs(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String xPos = req.getParameter("xPos");
		String yPos = req.getParameter("yPos");
		String addr = "http://apis.data.go.kr/B551182/hospInfoService/getHospBasisList?numOfRows=100&ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D";

		PrintWriter out = res.getWriter();

		String parameter = "";

		parameter = parameter + addr;
		parameter = parameter + "&xPos=" + xPos;
		parameter = parameter + "&yPos=" + yPos;
		parameter = parameter + "&radius=" + radius;
		parameter = parameter + "&_type=json";

		URL url = new URL(parameter);

		InputStream in = url.openStream();

		ByteArrayOutputStream bos1 = new ByteArrayOutputStream();

		IOUtils.copy(in, bos1); // 인풋 데이터 바이트배열복수
		in.close();
		bos1.close();
		String mbos = bos1.toString("UTF-8");
		byte[] b = mbos.getBytes("UTF-8");
		String s = new String(b, "UTF-8");

		out.println(s);

	}

	/**
	 * @param req 좌표
	 * @param res
	 * @throws Exception
	 * @author 김건수
	 * @검색결과
	 */
	@RequestMapping(value = "hs_query", method = RequestMethod.POST)
	public void hs_query(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String cate = req.getParameter("cate");
		String query = req.getParameter("query");
		String xPos = req.getParameter("xPos");
		String yPos = req.getParameter("yPos");

		String addr = "http://apis.data.go.kr/B551182/hospInfoService/getHospBasisList?numOfRows=30&ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D";

		PrintWriter out = res.getWriter();

		String parameter = "";

		parameter = parameter + addr;
		parameter = parameter + "&xPos=" + xPos;
		parameter = parameter + "&yPos=" + yPos;
		parameter = parameter + "&radius=" + 100000;
		parameter = parameter + "&_type=json";
		if (query != "")
			parameter = parameter + "&yadmNm=" + URLEncoder.encode(query, "UTF-8");
		if (cate != "")
			parameter = parameter + "&dgsbjtCd=" + cate;

		URL url = new URL(parameter);

		InputStream in = url.openStream();

		ByteArrayOutputStream bos1 = new ByteArrayOutputStream();

		IOUtils.copy(in, bos1); // 인풋 데이터 바이트배열복수
		in.close();
		bos1.close();
		String mbos = bos1.toString("UTF-8");
		byte[] b = mbos.getBytes("UTF-8");
		String s = new String(b, "UTF-8");

//		데이터 1개일 때 검증용
//		System.out.println(s);
//		System.out.println(s.indexOf("items"));
//		System.out.println(s.charAt(s.indexOf("items")+15)); // { 면은 [추가
//		System.out.println(s.indexOf("numOf"));
//		System.out.println(s.charAt(s.indexOf("numOf")-4));
		if (s.charAt(s.indexOf("items") + 15) == '{') {
			s = s.substring(0, s.indexOf("items") + 15) + "["
					+ s.substring(s.indexOf("items") + 15, s.indexOf("numOf") - 3) + "]"
					+ s.substring(s.indexOf("numOf") - 3);

		}
//		System.out.println(s);
		out.println(s);
	}

	/**
	 * @author 김건수
	 * @상세페이지
	 * @param ykiho 암호 키
	 * @param model HdetailVO
	 * @return locationView 
	 * @API밀집부분
	 */
	@RequestMapping(value = "detail.net", method = RequestMethod.GET)
                 	public String locationView(HdetailVO vo, Model model,HttpSession session) throws Exception {
		/*
		 		1. 요양기호로 병원 검색됨 -> 기존 자료 사용
		 		2. 병원 없을 경우
		 			1) 월~일이 있을경우 + 점심
		 				1- 토일 중에 기록이 있으면 다 표시
		 				2- 기록이 없으면 그부분만제거(ex. 월~금)
		 			2) 월~일 없을 경우 -> 자료없음
		 */
		Integer check =(Integer)session.getAttribute("chk");
		
		if(check!=null) {			
			String member =(String)session.getAttribute("loginid");
			String fa_hos = vo.getYkiho();
			String favorite = hospitalService.getFavorite_sel(fa_hos);
			
			
			if(favorite!=null) {
				Map<String, String> fa = new HashMap<String, String>();
				fa.put("id", member);
				fa.put("hosid",favorite);
				String count = hospitalService.getFavorite_re(fa);
				
				
				if(count.equals("1")) {				
					model.addAttribute("count", count);
				}
			}
		}
		
		Hospital result = hospitalService.getDetail(vo.getYkiho());//요양키넣어서 관리자승인받은거로 고쳐야함
		
		Work_hs work_hs= work(vo.getYkiho());		
		
		String hs_empty="";
		if(result!=null) {
			hs_empty ="1";
			model.addAttribute("work", result);
			model.addAttribute("hs_empty", hs_empty);			
		}else if(work_hs!=null){
			hs_empty ="2";			
			model.addAttribute("work", work_hs);
			model.addAttribute("hs_empty", hs_empty);
			System.out.println("API확인용");
			
		}
		
		vo = detail(vo);			
		model.addAttribute("vo", vo);	
		return "details/hospitaldetail";	
	}

	/**
	 * @param ykiho
	 * @return 결과값 상세
	 */
	public HdetailVO detail(HdetailVO result) {

		JSONParser jsonparser = new JSONParser();
		JSONObject jsonobject;
		try {

			// api 안나오는거 있음 요양기호 -> url에 보내줌
//			HdetailVO result = new HdetailVO();
//			jsonobject = (JSONObject) jsonparser.parse(readUrl(
//					"http://apis.data.go.kr/B551182/medicInsttDetailInfoService/getFacilityInfo?ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D&_type=json&ykiho="
//							+ ykiho));
//			
//			JSONObject json = (JSONObject) jsonobject.get("response");
//			JSONObject body = (JSONObject) json.get("body");
//			JSONObject items = (JSONObject) body.get("items");
//			JSONObject item = (JSONObject) items.get("item");					
//			//시설정보
//			result.setYadmNm((String) item.get("yadmNm"));
//			result.setAddr((String) item.get("addr"));
//			result.setTelno((String) item.get("telno"));
//			result.setHospUrl((String) item.get("hospUrl"));
//			result.setClCdNm((String)item.get("clCdNm"));			
//			result.setEstbDd((Long)item.get("estbDd"));
//			
			// 근무시간 따로 클래스빼야함
//			jsonobject = (JSONObject) jsonparser.parse(readUrl(
//					"http://apis.data.go.kr/B551182/medicInsttDetailInfoService/getDetailInfo?ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D&_type=json&ykiho="
//							+ ykiho));
//			

			// 진료과목
			jsonobject = (JSONObject) jsonparser.parse(readUrl(
					"http://apis.data.go.kr/B551182/medicInsttDetailInfoService/getMdlrtSbjectInfoList?ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D&_type=json&ykiho="
							+ result.getYkiho()));

			JSONObject json = (JSONObject) jsonobject.get("response");
			JSONObject body = (JSONObject) json.get("body");
			Long total = (Long) body.get("totalCount");

			if (total == 0) {
				return result;
			}

			JSONObject items = (JSONObject) body.get("items");

			try {
				JSONArray itemArray = (JSONArray) items.get("item");
				String s = "";
				JSONObject a;
				for (int i = 0; i < itemArray.size(); i++) {
					a = (JSONObject) itemArray.get(i);
					s += (String) a.get("dgsbjtCdNm");
					if (i < itemArray.size() - 1)
						s += ",";
				}

				result.setDgsbjtCdNm(s);
				return result;
			} catch (Exception e) {
				JSONObject itemz = (JSONObject) items.get("item");
				result.setDgsbjtCdNm((String) itemz.get("dgsbjtCdNm"));
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @author 김건수
	 * @param result
	 * @return
	 * @근무시간
	 */
	public Work_hs work(String ykiho) {
		JSONParser jsonparser = new JSONParser();
		JSONObject jsonobject;
		Work_hs work_hs = new Work_hs();
		try {
			// 근무시간
			jsonobject = (JSONObject) jsonparser.parse(readUrl(
					"http://apis.data.go.kr/B551182/medicInsttDetailInfoService/getDetailInfo?ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D&_type=json&ykiho="
							+ ykiho));

			JSONObject json = (JSONObject) jsonobject.get("response");
			JSONObject body = (JSONObject) json.get("body");
//			 System.out.println(body.size());
			int total = body.size();
			int a = 4;
			if (total >= a) {
//				 System.out.println("실행됨");
				 JSONObject items = (JSONObject) body.get("items");
				 JSONObject item = (JSONObject) items.get("item");	
				 
				 work_hs.setNoTrmtSun((String)item.get("noTrmtSun"));
				 work_hs.setNoTrmtHoli((String)item.get("noTrmtHoli"));
				 work_hs.setLunchWeek((String)item.get("lunchWeek"));
				 work_hs.setRcvSat((String)item.get("rcvSat"));
				 work_hs.setRcvWeek((String)item.get("rcvWeek"));
				 work_hs.setTrmtMonStart(item.get("trmtMonStart"));
				 work_hs.setTrmtMonEnd(item.get("trmtMonEnd"));
				 work_hs.setTrmtTueStart(item.get("trmtTueStart"));
				 work_hs.setTrmtTueEnd(item.get("trmtTueEnd"));
				 work_hs.setTrmtWedStart(item.get("trmtWedStart"));
				 work_hs.setTrmtWedEnd(item.get("trmtWedEnd"));
				 work_hs.setTrmtThuStart(item.get("trmtThuStart"));
				 work_hs.setTrmtThuEnd(item.get("trmtThuEnd"));
				 work_hs.setTrmtFriStart(item.get("trmtFriStart"));
				 work_hs.setTrmtFriEnd(item.get("trmtFriEnd"));
				 work_hs.setTrmtSatStart(item.get("trmtSatStart"));
				 work_hs.setTrmtSatEnd(item.get("trmtSatEnd"));
				 work_hs.setTrmtSunStart(item.get("trmtSunStart"));
				 work_hs.setTrmtSunEnd(item.get("trmtSunEnd"));

        return work_hs;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @author 김건수
	 * @param urlString
	 * @return
	 * @throws Exception
	 * 
	 */
	private static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}
}

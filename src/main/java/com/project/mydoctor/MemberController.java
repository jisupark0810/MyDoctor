package com.project.mydoctor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.project.mydoctor.model.Hospital;
import com.project.mydoctor.model.Member;
import com.project.mydoctor.service.BoardService;
import com.project.mydoctor.service.HospitalService;
import com.project.mydoctor.service.MemberService;
import com.project.mydoctor.service.MypageService;
import com.project.mydoctor.service.QnaService;
import com.project.mydoctor.service.ReserveService;

@Controller
public class MemberController {
	@Autowired
	private MemberService memberService;

	@Autowired
	private HospitalService hospitalService;

	@Autowired
	private MypageService mypageService;
	
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ReserveService reserveService;
	
	@Autowired
	private QnaService qnaService;
	
	//jisu_0204_아이디 찾습니다
	@RequestMapping(value="/id.find")
	public  void findId(@RequestParam("name") String name, @RequestParam("email") String email,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String foundId = memberService.findId(name, email);
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		if (foundId == null) {
			out.println("<script>");
			out.println("alert('해당 계정이 존재하지 않습니다. 회원가입 후 이용해 주세요');");
			out.println("history.back();");
			out.println("</script>");
			out.close();
		}
		out.println("<script>");
		out.println("alert('아이디는 "+foundId+" 입니다');");
		out.println("location.href='findAccount'");
		out.println("</script>");
		out.close();
		

	}
	//jisu_0204_아이디/비밀번호 찾기 페이지로 이동
	@GetMapping(value="/findAccount")
	public String findAccount() {

		return "header/findAccount";

	}
	
	@GetMapping(value="/joinForm")
	public ModelAndView joinForm(ModelAndView mv) {
		
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(date);
		
		mv.setViewName("member/joinForm");
		mv.addObject("today", today);
		
		return mv;
	}

	@PostMapping(value = "/join")
	public ModelAndView joinProcess(Member member, ModelAndView mv, HttpServletResponse response, HttpSession session)
			throws Exception {
		int result = memberService.insertMember(member);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();

		if (result == 1) {
			out.print("<script>alert('가입을 축하드립니다');</script>");
			session.setAttribute("loginid", member.getId());
			session.setAttribute("yesaccept",0);
			session.setAttribute("chk", 1);
			mv.setViewName("redirect:/main");
		} else {
			out.print("<script>alert('가입에 실패했습니다.\n입력한 사항을 다시 확인하세요.'); history.go(-1)</script>");
			out.close();
			return null;
		}

		return mv;
	}

	@PostMapping(value = "/loginMember")
	public ModelAndView loginMember(Member member, String user, ModelAndView mv, HttpSession session,
			HttpServletResponse response) throws Exception {
		int result = 0;
		int chk = 0;
		response.setContentType("text/html;charset=utf-8");

		if (user.equals("pub")) {
			result = memberService.isId(member);
			chk = 1;
		} else {
			result = memberService.isHosId(member);
			chk = 2;
		}

		if (result == 1) {
			session.setAttribute("loginid", member.getId());
			session.setAttribute("chk", chk);

			if (member.getId().equals("admin")) {
				mv.setViewName("redirect:/hospitalcontrol");
				session.setAttribute("accepctReq", hospitalService.getSignRequestCount());
				session.setAttribute("adminReq", boardService.getAdminRequestNoCheckListCount());
			}else {
				mv.setViewName("redirect:/main");
				if(chk==1) {
					session.setAttribute("yesaccept", mypageService.reserveCount(member.getId()));
				}else {
					List<Integer> mypageList = reserveService.getReserves(member.getId());
					session.setAttribute("accepted", mypageList.get(0));
					session.setAttribute("wait", mypageList.get(1));
					session.setAttribute("canceled", mypageList.get(2));
					session.setAttribute("qnawait", qnaService.getNoReplyQnaCount(member.getId()));
				}
			}
		} else {
			PrintWriter out = response.getWriter();
			out.print("<script>alert('아이디 또는 비밀번호를 확인해주세요'); history.go(-1);</script>");
			out.close();
			return null;
		}
		return mv;
	}

	@GetMapping(value = "/logout")
	public ModelAndView logout(HttpSession session, ModelAndView mv) {
		session.invalidate();
		mv.setViewName("redirect:/main");
		return mv;
	}

	/**
	 * @return 병원회원가입
	 * @author 김건수
	 * 
	 */
	@RequestMapping(value = "hs_signup.do")
	public String hs_signup() {
		return "member/hs_signup";		
	}

	/**
	 * @param h_name 병원명
	 * @throws Exception
	 * @author 김건수
	 * @병원검색
	 */
	@RequestMapping(value = "/hs_signup_name.do")
	public void sign(HttpServletRequest req, HttpServletResponse res, String h_name) throws Exception {
		String searchText = URLEncoder.encode(h_name, "UTF-8");
		req.setCharacterEncoding("utf-8");
		res.setContentType("text/html;charset=utf-8");
		String ServiceKey = "http://apis.data.go.kr/B551182/hospInfoService/getHospBasisList?pageNo=1&numOfRows=100&_type=json&ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D&yadmNm="
				+ searchText;		
		PrintWriter out = res.getWriter();
		URL url = new URL(ServiceKey);
		InputStream in = url.openStream();
		ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
		IOUtils.copy(in, bos1);
		in.close();
		bos1.close();
		String mbos = bos1.toString("UTF-8");
		byte[] b = mbos.getBytes("UTF-8");
		String s = new String(b, "UTF-8");
		out.println(s);
	}

	/**
	 * 
	 * @param h_hideen
	 * @throws Exception
	 * @author 김건수
	 * @근무시간
	 */
	@RequestMapping(value = "/hs_work.do")
	public void work(HttpServletRequest req, HttpServletResponse res, String h_hideen) throws Exception {
		String searchText = URLEncoder.encode(h_hideen, "UTF-8");
		req.setCharacterEncoding("utf-8");
		res.setContentType("text/html;charset=utf-8");
		String ServiceKey = "http://apis.data.go.kr/B551182/medicInsttDetailInfoService/getDetailInfo?ServiceKey=G9rzPM3G3d1FVN%2F8ZyPSkwQ7B0IICxPX3Sks%2FrUY2wLu94BsUzYPUHzcNhSwJj%2FIjuLsoBMYMJ7JcX4thVA7Lg%3D%3D&_type=json&ykiho="
				+ searchText;
		PrintWriter out = res.getWriter();
		URL url = new URL(ServiceKey);
		InputStream in = url.openStream();
		ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
		IOUtils.copy(in, bos1);
		in.close();
		bos1.close();
		String mbos = bos1.toString("UTF-8");
		byte[] b = mbos.getBytes("UTF-8");
		String s = new String(b, "UTF-8");
		out.println(s);
	}

	/**
	 * @param req
	 * @param 병원가입시 vo
	 * @author 김건수
	 * @throws Exception
	 */
	@RequestMapping(value = "hs_signup.do", method = RequestMethod.POST)
	public void signup(HttpServletRequest req, HttpServletResponse res, Hospital vo) throws Exception {
		
		int result = hospitalService.hs_insert(vo);
		res.setContentType("text/html;charset=utf-8");
		PrintWriter out = res.getWriter();
		out.println("<script>");
		if (result == 1) {
			out.println("alert('회원가입을 축하합니다');");
			out.println("location.href='main';");
		} else {
			out.println("alert('실패했습니다');");
			out.println("history.back()");
		}
		out.println("</script>");
		out.close();

	}

	@ResponseBody
	@PostMapping(value = "/idcheck")
	public String idcheck(String id, String pub) {
		return memberService.idcheck(id, pub);
	}
	
	@RequestMapping(value = "/ididCheck.do")
	public void ididCheck(@RequestParam(value = "id") String id, HttpServletResponse response) throws IOException {
		int result = memberService.ididCheck(id);
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(result);
	}
}

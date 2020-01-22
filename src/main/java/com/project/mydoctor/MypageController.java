package com.project.mydoctor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.project.mydoctor.model.Member;
import com.project.mydoctor.service.MemberService;

@Controller
public class MypageController {
	@Autowired
	private MemberService memberService;
	
	
	//새로 입력받은 정보로 개인정보 update합니다.
			@RequestMapping(value="/updatePass")	
			public void updatePass(Member member, HttpServletResponse response) throws IOException{
				int result=memberService.update(member);
				response.setContentType("text/html; charset=utf-8");
				PrintWriter out=response.getWriter();
				if(result==1) {
					out.println("<script>");
					out.println("alert('정보 수정을 완료했습니다.')");
					out.println("location.href='mypage.net");
					out.println("</script>");
				}else {
					out.println("<script>");
					out.println("alert('정보 수정에 실패했습니다. 같은 문제가 반복될 경우 관리자에게 문의해 주세요.')");
					out.println("history.back()");
					out.println("</script>");
					out.close();
				}
			}
	//회원탈퇴 이전 비밀번호 맞는지 확인
	@RequestMapping(value="/removeaccount.do")
	public void removeaccount(Member member, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		String id=(String)session.getAttribute("loginid");
		String password=member.getPassword();
		System.out.println("id="+id+"password="+password);
		boolean usercheck=memberService.passck(id, password);
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out=response.getWriter();
		if(usercheck==false) {
			out.println("<script>");
			out.println("alert('비밀번호가 다릅니다');");
			out.println("history.back();");
			out.println("</script>");
			out.close();
		}
		out.println("<script>");
		out.println("alert('탈퇴 되었습니다.');");
		out.println("location.href='main'");
		out.println("</script>");
		out.close();
		}
	
	
	//mypage 메뉴에서 나의정보 수정 누르면 비밀번호 확인하는 페이지로 이동
	@RequestMapping(value="/modifyck.do")
	public String gomodify() {

		return "mypage/mypage_modify";
		
	}
	//정보수정하기이전 비밀번호 맞는지 확인
	@RequestMapping(value="/modifyckpass.do")
	public void gomodifyckpass(Member member, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		String id=(String)session.getAttribute("loginid");
		String password=member.getPassword();
		System.out.println("id="+id+"password="+password);
		boolean usercheck=memberService.passck(id, password);
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out=response.getWriter();
		if(usercheck==false) {
			out.println("<script>");
			out.println("alert('비밀번호가 다릅니다');");
			out.println("history.back();");
			out.println("</script>");
			out.close();
		}
		out.println("<script>");
		out.println("location.href='modify.do'");
		out.println("</script>");
		out.close();
		}
	
	//개인정보 수정화면에 이전 정보 불러옵니다.
		@RequestMapping(value="/modify.do")
		public ModelAndView modifydo(ModelAndView mv, HttpSession session) {
	String id=(String)session.getAttribute("loginid");
	System.out.println("id="+id);
	Member m=memberService.my_info(id);
	mv.addObject("myinfo",m);
	mv.setViewName("mypage/mypage_modify2");
		return mv;	 
			
		}
		
		//새로 입력받은 정보로 개인정보 update합니다.
		@RequestMapping(value="/updateProcess.net")	
		public void updateProcess(Member member, HttpServletResponse response) throws IOException{
			int result=memberService.update(member);
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out=response.getWriter();
			if(result==1) {
				out.println("<script>");
				out.println("alert('정보 수정을 완료했습니다.')");
				out.println("location.href='mypage.net");
				out.println("</script>");
			}else {
				out.println("<script>");
				out.println("alert('정보 수정에 실패했습니다. 같은 문제가 반복될 경우 관리자에게 문의해 주세요.')");
				out.println("history.back()");
				out.println("</script>");
				out.close();
			}
		}
		
	@RequestMapping(value="/myreview.net")
	public String gomyreview() {

		return "mypage/mypage_review";
	}
	@RequestMapping(value="/quit.do")
	public String goquit_ck() {

		return "mypage/mypage_signout";
	}
	@RequestMapping(value="/myqna.net")
	public String gomyqna() {

		return "mypage/mypage_req";
	}
	@RequestMapping(value="/myreserve.net")
	public String gomyreserve() {

		return "mypage/reservation";
	}
	@RequestMapping(value="/mypage.net")
	public String gomypage() {

		return "mypage/mypage";
	}
	@RequestMapping(value="/review.net")
	public String writeReview() {

		return "writeform/review";
	}
}
 
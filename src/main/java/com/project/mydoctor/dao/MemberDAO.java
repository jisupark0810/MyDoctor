package com.project.mydoctor.dao;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.mydoctor.model.Member;

@Repository
public class MemberDAO {
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	public int insertMember(Member member) {
		return sqlSession.insert("Members.insertMember", member);
	}

	public int isID(Member member) {
		return sqlSession.selectOne("Members.isId", member);
	}

	public int isHosId(Member member) {
		return sqlSession.selectOne("Hospitals.isId", member);
	}

	public Member passck(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("Members.passck",map);
	}
}

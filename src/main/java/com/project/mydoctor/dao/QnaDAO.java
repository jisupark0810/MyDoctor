package com.project.mydoctor.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.mydoctor.model.Qna;
import com.project.mydoctor.model.Review;
@Repository
public class QnaDAO {
	@Autowired
	private SqlSessionTemplate sqlSession;
	public int insertQna(Qna qna) {
		// TODO Auto-generated method stub
		return sqlSession.insert("Qna.insertqna", qna);
	}
	public int getListCount(String id) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("Qna.qnalistcount", id);
	}
	public List<Qna> getMyQnaList(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return sqlSession.selectList("Qna.qnalist", map);
	}
	

}

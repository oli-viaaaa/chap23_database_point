package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * [static 전역변수]
 * 	- JDBC 프로그래밍을 위한 요소들을 모두 멤버변수 즉, 필드 위치로 뽑아 올림.
 * 본 클래스 어디서라도 사용가능한 전역변수가 됨.
 * 
 * [모듈화]
 * 	- 데이터베이스 커넥션 + PreparedStatemt + 쿼리실행 작업 모듈
 * 	- 실제로 쿼리를 실행하고 결과를 받아오는 부분 모듈
 * 
 * [미션]
 * 	- 전체 상품의 정보를 조회하세요(카테고리명이 나오도록)
 */

public class PointMain {

	// [멤버변수]
	// 1. oracle 드라이버 이름 문자열 상수
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";

	// 2. oracle 데이터베이스 접속 경로(url) 문자열 상수
	public static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";

	// 3. 데이터베이스 접속 객체
	public static Connection con = null;

	// 4. query 실행 객체
	public static PreparedStatement pstmt = null;

	// 5. select 결과 저장 객체
	public static ResultSet rs = null;

	// 6. oracle 계정(id/pwd)
	public static String oracleId = "tempdb";

	// 7. oracle password
	public static String oraclePwd = "1234";

	// main 메소드가 간결해짐
	public static void main(String[] args) {

		// 1. DB 접속 메소드 호출
		connectDB();

		// 2. 회원들과 보유 포인터 정보 조회
		getMemberAndPoint();

		// 3. 이소미 회원에게 포인트 15점 추가 지금
		updatePointSomi();

		// 4. 관리자에게 포인트 30점 추가 지급
		updatePointManager();

		// 5. 전체회원 평균 포인트보다 작은 회원 목록 조회
		getMemberLessThanAvg();

		// 6. Connection 자원반환
		closeResource();

	}// end main

	// 1. DB 접속 메소드 호출
	// 드라이버 로딩과 커넥션 객체 생성 메소드
	private static void connectDB() {
		try {
			Class.forName(DRIVER_NAME);
			System.out.println("1. 드라이버 로드 성공!");

			con = DriverManager.getConnection(DB_URL, oracleId, oraclePwd);
			System.out.println("2. 커넥션 객체 생성 성공!");

		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 ERR! : " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}
	} // end 1.

	/////////////////////////////////////////////////////////////////////////////////////////////////

	// 2. 회원들과 보유 포인터 정보 조회
	private static void getMemberAndPoint() {
		String sql = "";
		try {
			// SQL 쿼리문 만들기
			sql = " select m.user_id, m.name, m.pwd, m.email,m.phone, decode(m.admin,0, '일반사용자', 1, '관리자') admin,";
			sql += " p.point_id, p.points,to_char(p.reg_date, 'YYYY-MM-DD') REG_DATE";
			sql += " from member m left outer join point p on m.user_id = p.user_id";
			
			// PreparedStatement 객체 얻기
			pstmt = con.prepareStatement(sql);
			System.out.println("pstmt 객체 생성 성공!");
			
			// pstmt 객체의 executeQuery() 메소드를 통해서 쿼리 실행
			// 데이터 베이스에서 조회된 결과가 RestultSet 객체에 담겨옴
			rs = pstmt.executeQuery();
			
			System.out.println("2. 회원정보와 회원들의 포인터 정보 조회");
			// 게시물 목록 제목
			System.out.println("===============================================================");
			System.out.println("user_id" + " "
                     + "name" + " "                
                     + "   pwd" + "\t"
                     + "email" + "\t"
                     + "              phone" + "\t"
                     + "admin" + "\t"
                     + "point_id" + "\t"
                     + "points" + "\t"
                     + "reg_date");
			System.out.println("---------------------------------------------------------------");
			
			while (rs.next()) {
				System.out.println(rs.getString("user_id") + "\t" 
		                  + rs.getString("name") + "\t"
		                  + rs.getString("pwd") + "\t"
		                  + rs.getString("email") + "\t"
		                  + rs.getString("phone") + "\t"
		                  + rs.getString("admin") + "\t" 
		                  + rs.getInt("point_id") + "\t"
		                  + rs.getInt("points") + "\t"
		                  + rs.getString("reg_date") + "\t"
		            );
			}
			System.out.println("=============================================");
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			// 자원 해제 메소드 호출
			closeResource();
		}
		System.out.println();
	} // end 2.

	/////////////////////////////////////////////////////////////////////////////////////////////////

	//  3. 이소미 회원에게 포인트 15점 추가 지금
	private static void updatePointSomi() {
		System.out.println("3. 이소미 회원에게 포인트 15점 추가 지금");
		System.out.println("============================================");
		
		try {
			String sql = "";
			int intPoint = 15;
			String strName = "이소미";
			
			// 저장 SQL문 생성
			sql = " update point set points = points + ?";
			sql += " where user_id = (select user_id from member where name = ?)";
			
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, intPoint);
			pstmt.setString(2, strName);
			
			// 쿼리 실행
			// 처리된 결과 반환됨
			int resultRows = pstmt.executeUpdate();
			// executeUpdate 쿼리 업데이트 한다
			if (resultRows > 0) {
				System.out.println("수정 성공");
			} else {
				System.out.println("수정 실패");
			}
			System.out.println("=============================================");
					
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			// 자원 해제 메소드 호출
			closeResource();
		}
		System.out.println();
	}// end 3.

	/////////////////////////////////////////////////////////////////////////////////////////////////

	// 4. 관리자에게 포인트 30점 추가 지급
	private static void updatePointManager() {
		System.out.println("4. 관리자에게 포인트 30점 추가 지금");
		System.out.println("============================================");
		try {
			String sql = "";
			int intPoint = 3;
			int intAdmin = 1;
			
			sql = "update point";
			sql += " set points = points + ?";
			sql += " where user_id in( select user_id from member where admin = ?)";
			
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, intPoint);
			pstmt.setInt(2, intAdmin);
		
			
			int resultRows = pstmt.executeUpdate();
			// executeUpdate 쿼리 업데이트 한다
			if (resultRows > 0) {
				System.out.println("수정 성공");
			} else {
				System.out.println("수정 실패");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			// 자원 해제 메소드 호출
			closeResource();
		}
		System.out.println();
	}// end 4.

	/////////////////////////////////////////////////////////////////////////////////////////////////

	// 5. 전체회원 평균 포인트보다 작은 회원 목록 조회
	private static void getMemberLessThanAvg() {
		System.out.println("5. 전체회원 평균 포인트보다 작은 회원 목록 조회");
		System.out.println("============================================");
		
		try {
			String sql = " select m.user_id, m.name, m.pwd, m.email,m.phone,";
			   sql += " decode(m.admin,0, '일반사용자', 1, '관리자') admin,";
			   sql += " p.point_id,p.points,to_char(p.reg_date, 'YYYY-MM-DD')REG_DATE";
			   sql += " from member m left outer join point p on m.user_id = p.user_id";
			   sql += " where p.points > (select avg(points) from point)";
			   
		  pstmt = con.prepareStatement(sql);
		  rs = pstmt.executeQuery();
		  
		  while (rs.next()) {
			System.out.println(rs.getString("user_id")+"\t"
							  + rs.getString("name")+"\t"
							  + rs.getString("phone")+"\t"
							  + rs.getString("admin")+"\t"
							  + rs.getInt("point_id")+"\t"
							  + rs.getDate("reg_date"));
		}
			   
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			// 자원 해제 메소드 호출
			closeResource();
		}
		System.out.println();
	}// end 5.

	/////////////////////////////////////////////////////////////////////////////////////////////////

	// 6. Connection 자원반환
	private static void closeResource() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			System.out.println("자원해제 ERR! : " + e.getMessage());
		}
	} // end 8.
}

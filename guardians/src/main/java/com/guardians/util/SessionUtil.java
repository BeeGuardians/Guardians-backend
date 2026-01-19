package com.guardians.util;

import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;

/**
 * 세션 관련 유틸리티 클래스
 * - 세션에서 사용자 정보 추출 로직을 중앙화하여 코드 중복 제거
 * - null 체크 및 권한 검증 로직 통합
 */
public class SessionUtil {

    private SessionUtil() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    /**
     * 세션에서 userId를 추출합니다.
     * userId가 null인 경우 NOT_LOGGED_IN 예외를 발생시킵니다.
     *
     * @param session HttpSession
     * @return userId
     * @throws CustomException NOT_LOGGED_IN
     */
    public static Long getRequiredUserId(HttpSession session) {
        Long userId = getUserIdOrNull(session);
        if (userId == null) {
            throw new CustomException(ErrorCode.NOT_LOGGED_IN);
        }
        return userId;
    }

    /**
     * 세션에서 userId를 추출합니다.
     * userId가 null인 경우 null을 반환합니다 (비로그인 허용 API용).
     *
     * @param session HttpSession
     * @return userId 또는 null
     */
    public static Long getUserIdOrNull(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute("userId");
    }

    /**
     * 세션에서 role을 추출합니다.
     *
     * @param session HttpSession
     * @return role 또는 null
     */
    public static String getRoleOrNull(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute("role");
    }

    /**
     * 관리자 권한을 확인합니다.
     * userId가 null이면 NOT_LOGGED_IN 예외,
     * role이 ADMIN이 아니면 PERMISSION_DENIED 예외를 발생시킵니다.
     *
     * @param session HttpSession
     * @return userId
     * @throws CustomException NOT_LOGGED_IN, PERMISSION_DENIED
     */
    public static Long requireAdmin(HttpSession session) {
        Long userId = getRequiredUserId(session);
        String role = getRoleOrNull(session);

        if (!"ADMIN".equals(role)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
        return userId;
    }

    /**
     * 요청한 userId와 세션의 userId가 일치하는지 확인합니다.
     * 일치하지 않으면 UNAUTHORIZED_ACCESS 예외를 발생시킵니다.
     *
     * @param session HttpSession
     * @param targetUserId 확인할 userId
     * @return userId
     * @throws CustomException NOT_LOGGED_IN, UNAUTHORIZED_ACCESS
     */
    public static Long requireSameUser(HttpSession session, Long targetUserId) {
        Long sessionUserId = getRequiredUserId(session);

        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        return sessionUserId;
    }

    /**
     * 요청한 userId와 세션의 userId가 일치하거나 관리자인지 확인합니다.
     *
     * @param session HttpSession
     * @param targetUserId 확인할 userId
     * @return userId
     * @throws CustomException NOT_LOGGED_IN, UNAUTHORIZED_ACCESS
     */
    public static Long requireSameUserOrAdmin(HttpSession session, Long targetUserId) {
        Long sessionUserId = getRequiredUserId(session);
        String role = getRoleOrNull(session);

        if (!sessionUserId.equals(targetUserId) && !"ADMIN".equals(role)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        return sessionUserId;
    }
}

package kopo.poly.service.impl;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.mapper.INoticeMapper;
import kopo.poly.service.INoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 공지사항 도메인의 Service 레이어.
 * <p>
 * 캐싱 전략 요약
 * - 리스트만 캐시하고, 상세는 캐시하지 않는다.
 * 이유: 리스트는 요청량이 많고 변경 빈도가 상대적으로 낮아 캐싱 효율이 높다.
 * 상세는 조회수 증가와 같은 부작용이 있어 캐시하면 일관성 문제가 발생할 수 있다.
 * <p>
 * 주의사항
 * - 캐시 이름: "notice:list" (CacheConfig에서 생성, TTL 예시는 60초 등)
 * - 키 생성: KeyGenerator(bean name: "noticeList")를 사용하여 항상 동일 키("v1:notice_list_metric")를 사용
 * - 동시성: @Cacheable(sync = true)로 동일 키의 동시 요청을 1회로 합쳐 DB 부하를 줄인다.
 * - 쓰기 작업(등록, 수정, 삭제) 이후에는 목록 캐시를 반드시 무효화한다(@CacheEvict).
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService implements INoticeService {

    /**
     * 생성자 주입으로 Mapper 빈을 받는다.
     * 과거 @Autowired 필드 주입 대신 생성자 주입을 권장한다.
     */
    private final INoticeMapper noticeMapper;

    /**
     * 공지사항 목록 조회
     * <p>
     * 캐시 적용 포인트
     * - cacheNames = "notice:list" : CacheConfig에서 만든 캐시 이름과 일치해야 한다.
     * - keyGenerator = "noticeList" : 파라미터가 없으므로 단일 고정 키를 사용한다.
     * - sync = true : 동일 키에 대한 동시 요청을 1회로 수렴시켜 캐시 스탬피드를 방지한다.
     * <p>
     * TTL 및 저장 방식
     * - JCache/Ehcache 설정에서 TTL과 storeByValue 설정을 관리한다.
     */
    @Cacheable(
            cacheNames = "notice:list",
            keyGenerator = "noticeList",
            sync = true
    )
    @Override
    public List<NoticeDTO> getNoticeList() throws Exception {

        log.info("{}.getNoticeList start!", this.getClass().getName());

        // 캐시 미스일 때만 실행되어 DB를 조회한다.
        // 캐시 히트이면 이 메서드는 실행되지 않고 캐시에 저장된 값이 반환된다.
        return noticeMapper.getNoticeList();
        // 참고: CacheConfig에서 setStoreByValue(false)를 사용하면 반환 리스트가 외부에서 수정될 때
        // 캐시 내부 값까지 변경될 수 있다. 필요하면 아래처럼 불변 리스트로 반환한다.
        // return List.copyOf(noticeMapper.getNoticeList());
    }

    /**
     * 공지사항 상세 조회
     * <p>
     * 캐시를 적용하지 않는 이유
     * - type == true 인 경우 조회수 증가라는 부작용이 발생한다.
     * - 캐시를 적용하면 조회수 증가가 반영되지 않은 값이 계속 반환되어 최신성과 일관성에 문제가 생길 수 있다.
     * - 따라서 상세는 항상 DB에서 조회한다.
     * <p>
     * 트랜잭션
     * - 조회수 증가(update)와 상세 조회(select)를 하나의 트랜잭션으로 처리한다.
     */
    @Transactional
    @Override
    public NoticeDTO getNoticeInfo(NoticeDTO pDTO, boolean type) throws Exception {

        log.info("{}.getNoticeInfo start!", this.getClass().getName());

        // 상세보기 시 조회수 증가. 수정보기는 제외한다.
        if (type) {
            log.info("Update ReadCNT");
            noticeMapper.updateNoticeReadCnt(pDTO);
        }

        // 항상 DB에서 최신 상세 정보를 조회하여 반환한다.
        return noticeMapper.getNoticeInfo(pDTO);
    }

    /**
     * 공지 등록
     * <p>
     * 캐시 무효화
     * - 등록 후 목록이 변경되므로 "notice:list" 캐시의 모든 엔트리를 제거한다.
     */
    @CacheEvict(cacheNames = "notice:list", allEntries = true)
    @Transactional
    @Override
    public void insertNoticeInfo(NoticeDTO pDTO) throws Exception {

        log.info("{}.insertNoticeInfo start!", this.getClass().getName());

        noticeMapper.insertNoticeInfo(pDTO);
    }

    /**
     * 공지 수정
     * <p>
     * 캐시 무효화
     * - 수정 후 정렬, 상위 노출, 요약 등 목록 표시가 달라질 수 있으므로 목록 캐시를 비운다.
     */
    @CacheEvict(cacheNames = "notice:list", allEntries = true)
    @Transactional
    @Override
    public void updateNoticeInfo(NoticeDTO pDTO) throws Exception {

        log.info("{}.updateNoticeInfo start!", this.getClass().getName());

        noticeMapper.updateNoticeInfo(pDTO);
    }

    /**
     * 공지 삭제
     * <p>
     * 캐시 무효화
     * - 삭제 후 목록이 변경되므로 목록 캐시를 비운다.
     */
    @CacheEvict(cacheNames = "notice:list", allEntries = true)
    @Transactional
    @Override
    public void deleteNoticeInfo(NoticeDTO pDTO) throws Exception {

        log.info("{}.deleteNoticeInfo start!", this.getClass().getName());

        noticeMapper.deleteNoticeInfo(pDTO);
    }
}

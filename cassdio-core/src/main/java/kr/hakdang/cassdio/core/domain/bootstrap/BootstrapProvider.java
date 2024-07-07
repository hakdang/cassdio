package kr.hakdang.cassdio.core.domain.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * BootstrapProvider
 *
 * @author akageun
 * @since 2024-07-02
 */
@Slf4j
@Service
public class BootstrapProvider {

    private static boolean minClusterCountCheck = false;

    public boolean systemAvailable() {
        //최소 한개의 cluster 가 등록 되어 있는지
        //System 이 올라올 때 static 하게 가지고 있는다.
        //최소 한개의 값이 등록되면 그 때 true 로 업데이트 한다.
        return minClusterCountCheck;

        //
    }

    public void updateMinClusterCountCheck(boolean check) {
        minClusterCountCheck = check;
    }
}

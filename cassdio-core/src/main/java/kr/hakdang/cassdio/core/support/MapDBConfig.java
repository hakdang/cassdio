package kr.hakdang.cassdio.core.support;

import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

/**
 * MapDBConfig
 *
 * @author akageun
 * @since 2024-07-19
 */
@Slf4j
@Configuration
public class MapDBConfig {

    @Bean(name = "mapDb", destroyMethod = "close")
    public DB mapDb() {
        File file = new File(System.getProperty("user.home") + "/.cassdio");
        if (!file.exists()) {
            file.mkdir();
        }

        DBMaker.Maker maker = DBMaker
            //TODO : 추후 프로퍼티 받아서 주입할 수 있도록 변경 예정
            .fileDB(System.getProperty("user.home") + "/.cassdio/cassdio_v1.db")
            .fileMmapEnable()            // Always enable mmap
            .fileMmapEnableIfSupported() // Only enable mmap on supported platforms
            .fileMmapPreclearDisable()   // Make mmap file faster
            .transactionEnable()
            .fileLockDisable()
            // Unmap (release resources) file when its closed.
            // That can cause JVM crash if file is accessed after it was unmapped
            // (there is possible race condition).
            .cleanerHackEnable();

        return maker.make();
    }

}

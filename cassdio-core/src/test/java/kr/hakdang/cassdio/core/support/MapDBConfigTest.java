package kr.hakdang.cassdio.core.support;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@TestConfiguration
class MapDBConfigTest {


    @Bean(name = "mapDb")
    public DB mapDb() {
        DBMaker.Maker maker = DBMaker
            .memoryDB();


        return maker.make();
    }

}

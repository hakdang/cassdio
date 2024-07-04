package kr.hakdang.cadio.core.domain.cluster.info;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * ClusterUtils
 *
 * @author akageun
 * @since 2024-07-05
 */
public abstract class ClusterUtils {

    public static String generateClusterId() {
        String uuid = UUID.randomUUID().toString();
        byte[] temp = uuid.getBytes(StandardCharsets.UTF_8);
        byte[] test;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            test = digest.digest(temp);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 4; j++) {
            sb.append(String.format("%02x", test[j]));
        }

        return sb.toString();
    }
}

package kr.hakdang.cadio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * ClusterCommander
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
public abstract class ClusterCommander<
    ARGS extends ClusterCommandArgs,
    RESULT extends ClusterCommandResult
    > {

    public final RESULT execute(ClusterArgs cluster, ARGS args) {

        if (log.isDebugEnabled()) {

        }

        RESULT result = null;

        try {
            accessControl(cluster);
            argValid(cluster, args); //arg validation

            var preResult = preExecute(cluster, args);

            result = doExecute(cluster, args, preResult);

            var postResult = postExecute(cluster, args);

        } catch (Exception e) {
            throw e;
        } finally {
            completeExecute();
        }

        return result;
    }

    private void accessControl(ClusterArgs cluster) {

    }

    private void argValid(ClusterArgs cluster, ARGS args) {

        doArgValid(cluster, args);
    }

    protected abstract void doArgValid(ClusterArgs cluster, ARGS args);

    protected ClusterCommandPreExecuteResult preExecute(ClusterArgs cluster, ARGS args) {

        return new EmptyPreExecuteResult(); //TODO : static
    }

    protected abstract RESULT doExecute(ClusterArgs cluster, ARGS args, ClusterCommandPreExecuteResult preResult);

    protected ClusterCommandPostExecuteResult postExecute(ClusterArgs cluster, ARGS args) {
        return new EmptyPostExecuteResult(); //TODO : static
    }

    protected void completeExecute() {

    }


    protected CqlSession makeSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("127.0.0.1", 29042))
            .withLocalDatacenter("dc1")
            .build();
    }
}

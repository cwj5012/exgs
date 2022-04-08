package wang.ioai.exgs.core.system;

import wang.ioai.exgs.core.define.EStatus;
import wang.ioai.exgs.exec.master.MasterBoot;

import java.time.Instant;
import java.util.UUID;

public class NodeInfo {
    public String uuid;               // 唯一 id
    public EStatus status;          // 运行状态
    public long start_time;         // 进程启动时间
    public long run_time;           // 进程运行时长
    public int node_id;             // 节点 id
    public int node_type;           // 节点类型

    public NodeInfo() {
        uuid = UUID.randomUUID().toString();
    }

    public void updateStartTime() {
        start_time = Instant.now().toEpochMilli();
    }

    public long getRunTime() {
        return Instant.now().toEpochMilli() - start_time;
    }

    @Override
    public String toString() {
        return String.format("""
                        \n===================================================
                        uuid: %s
                        status: %s
                        start_time: %d run_time: %d s
                        node_id: %d node_type: %d
                        ===================================================
                        """,
                MasterBoot.nodeInfo.uuid,
                MasterBoot.nodeInfo.status,
                MasterBoot.nodeInfo.start_time / 1000,
                MasterBoot.nodeInfo.getRunTime() / 1000,
                MasterBoot.nodeInfo.node_id,
                MasterBoot.nodeInfo.node_type
        );
    }
}

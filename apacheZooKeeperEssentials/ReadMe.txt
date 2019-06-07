Apache ZooKeeper Essengtial

A.ZK速成课
A.1下载ZK
wget http://xxx/zookeeper-3.4.6.tar.gz
tar -C /usr/share -zxf zookeeper-3.4.6.tar.gz
export ZK_HOME=/usr/share/zookeeper-3.4.6/
export PATH=$PATH:$ZK_HOME/bin

A.2安装ZK
A.2.1Standalone 模式
配置：zoo.conf
tickTime=2000 //毫秒数，客户连接ZK整体服务的心跳时间，2秒
dataDir=/var/lib/zookeeper //数据库快照，内存事务日志存放处
clientPort=2181 //客户端连接端口

服务端
服务启动：
zkServer.sh start [zoo.conf]
查看PID:
ps -ef | grep zookeeper | grep -v grep | awk '{print $2}'
jps //QuorumPeerMain 进程
查看服务状态（Mode:standalone)
zkServer.sh status
服务停止
zkServer.sh stop

客户端：
基于Java的客户端shell
连接：
zkCli.sh -server localhost:2181
基于C客户端
编译：
cd /ZK_HOME/src/c
./configure && make && make install
C client libraries under /usr/local/lib
cli_st and cli_mt int current directory(singleThread/multiThread scripts)
连接：
cli_mt localhost:2181

A.2.2 Quorum多节点模式（3节点）
配置：zoo.conf
tickTime=2000
dataDir=/var/lib/zookeeper
clientPort=2181
initLimit=5 // 5*tickTime 10秒，follower初始化连接到leader的超时时间
syncLimit=2 //2*tickTime 2秒，follower同步leader的超时时间
server.1=zoo1:2888:3888
server.2=zoo2:2888:3888
server.3=zoo3:2888:3888

说明：
server.X=host:follower2LeaderPort:leaderElectionPort
echo 1 > /var/lib/zookeeper/myid
echo 2 > /var/lib/zookeeper/myid
echo 3 > /var/lib/zookeeper/myid

注意：
单机伪分布式时，注意三处
clientPort
server.X=host:Port1:Port2
dataDir=三个不同目录//放不同的三个myid文本文件

服务启动（三个机器都执行）：
zkServer.sh start
zkServer.sh status(Mode:follower,Mode:leader)
客户端连接
zkCli.sh -server zoo1:2181,zoo2:2181,zoo3:2181


B.理解ZK内部工作机制
B1.ZK数据模型
像文件系统一样，由一组层级的命名空间组成的数据登记点，称之为ZNODE
B2.ZNODE类型
持久节点（配置信息）
临时节点（集群节点成员管理/组管理）
有序节点（分布式队列，锁，屏障）
create [-s|-e] znodeName zNodeData
说明：
节点名称，除了zookeeper,.外，其他都字符都可以用来命名
临时节点不能有子节点，创建的客户端断开就被服务器删除
持久节点存在子节点，不能删父节点

B.3ZK的操作
create/delete/exist/setData/getData/setChildren/getChildren/setACL/getACL/sync
读操作：直接访问与客户端直接连接的服务端节点
写操作：转发到leader,达成多数共识的持久化写后，方响应客户端

B3.监听与通知
只有读操作才能设置监听
exist  NodeCreated/NodeDeleted (znode created or deleted,data changed)
getChildren NodeChildrenChanged(znode deleted,children deleted or updated)
getData NodeDataChanaged(znode deleted,data changed)

B4.znode节点的stat结构
cZxid:节点创建的事务ID
mZxid:最近一次修改此znode的事务ID
pZxid:从属于子节点的创建或删除的事务ID
ctime:创建时间
mtime:修改时间
dataVersion:节点修改次数
cversion:子节点修改次数
aclVersion:ACL修改次数
ephemeralOwner:临时节点时，记录SessionId,否则0
dataLentgh:数据长度
numChildren:子节点数

B5.多节点集群的问题
ZK导致集群数据不一到的因素。
节点故障或网络分区（脑裂）
集群节点最好奇数，以降低发生的概率

B6.ZK客户端的会话
客户端成功连接到服务集群，服务端分配64位的数字作为会话ID给客户端
在连接客户端时，可以设置会话超时时间（2-20倍的tickTime)
根据（ZK集群整体的规模、实际业务逻辑的复杂度、网络堵塞情况）来设置超时时间
客户端库通过TCP向服务器发送ping心跳，心跳间隔时间很短以便容易发现并重连
连接服务器的句柄状态：Connecting-> Connected->Closed(连接关闭，授权失败，会话超时)

B7.ZK事务的实现
每个服务器维护自己的核心数据库，体现整个ZK的命名空间状态。
确保更新被持久化，以便在服务器崩溃时恢复，更新被记录到磁盘中，
并且写操作先序列化到磁盘再应用到内存数据库
复制数据库+领导选举/原子广播 = ZK服务的核心
客户端写请求（setData,create,update）,开启事务64bits的Zxid(epoch,counter各32bit)
事务处理包含 领导选举/原子广播（ZAB）两阶段提交协议

阶段1：选主算法（恢复模式）
每个参与选择算法的服务器都处于LOOKING状态。
如果领导已经存在，并且有新节点加入，则follower节点通知new节点，新节点同步leader节点。
如果没有领导节点，刚开始选择算法（每个节点都是LOOKING状态），每选成主的节点变成LEADDING ，其他节点为FOLLOWING
1.每个节点将自己的服务器ID(SID)+最近一次事务执行ID（ZXID）发送给其他节点
2.接收到其他节点的SID+Zxid，判断 Zxid是自己的大还是接收到的大，并记录最大Zxid
3.判断 是否主节点已选出，选出则结果 ，否则自己再创建新Zxid+SID发出询问
选主出来后，主节点提出NEW_LEADER提议，每大多数节点公认后，主节点才被激活。
只有NEW_LEADER提议被 提交后，新领导节点才接受新的提议


阶段2：原子广播（同步模式）
所有写请求转发给主节点，主节点广播更新给从节点。
只有大多数据从节点认为自己持久化了数据后，主节点才提交更新
2PC:主Propose从，从ACK主，主Commit
观察者不参与投票，作用是：扩展服务端读请求，跨数据中心的更新传递


C.ZK编程
source ZK_HOME/bin/zkEnv.sh //setting CLASS_PATH env variable

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
public class HelloZooKeeper {
	public static void main(String[] args) throws IOException {
		String hostPort = "localhost:2181";
		String zpath = "/";
		List <String> zooChildren = new ArrayList<String>();
		ZooKeeper zk = new ZooKeeper(hostPort, 2000, null);
		if (zk != null) {
			try {
				zooChildren = zk.getChildren(zpath, false);
				System.out.println("Znodes of '/': ");
				for (String child: zooChildren) {
					//print the children
					System.out.println(child);
				}
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

Zookeeper zk = new Zookeeper("localhost:2181",2000,null)//connectString,sessionTimeout,watcher
//sessionId,sessionPassword,canBeReadOnly
List<String> zkRootChildren = zk.getChildren("/")
for(String znode:zkRootChildren){
	System.out.println(znode);
}

javac -cp $CLASS_PATH HelloZookeeper.java
java -cp $CLASS_PATH HelloZookeeper

实现自定义Watcher
public interface Watcher {
	void process(WatchedEvent event);
}

DataWatcher.java
import java.io.IOException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
public class DataWatcher implements Watcher, Runnable {
	private static String hostPort = "localhost:2181";
	private static String zooDataPath = "/MyConfig";
	byte zoo_data[] = null;
	ZooKeeper zk;
	public DataWatcher() {
		try {
			zk = new ZooKeeper(hostPort, 2000, this);
			if (zk != null) {
				try {
					if (zk.exists(zooDataPath, this) == null) {
						zk.create(zooDataPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
					}
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void printData() throws InterruptedException, KeeperException {
		zoo_data = zk.getData(zooDataPath, this, null);
		String zString = new String(zoo_data);
		System.out.printf("\nCurrent Data @ ZK Path %s: %s",zooDataPath, zString);
	}
	@Override
	public void process(WatchedEvent event) {
		System.out.printf("\nEvent Received: %s", event.toString());
		//We will process only events of type NodeDataChanged
		if (event.getType() == Event.EventType.NodeDataChanged) {
			try {
				printData();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (KeeperException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)throws InterruptedException, KeeperException {
		DataWatcher dataWatcher = new DataWatcher();
		dataWatcher.printData();
		dataWatcher.run();
	}
	public void run() {
		try {
			synchronized (this) {
				while (true) {
					wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
}

DataUpdater.java
import java.io.IOException;
import java.util.UUID;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
public class DataUpdater implements Watcher {
	private static String hostPort = "localhost:2181";
	private static String zooDataPath = "/MyConfig";
	ZooKeeper zk;
	public DataUpdater() throws IOException {
		try {
			zk = new ZooKeeper(hostPort, 2000, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() throws InterruptedException, KeeperException {
		while (true) {
			String uuid = UUID.randomUUID().toString();
			byte zoo_data[] = uuid.getBytes();
			zk.setData(zooDataPath, zoo_data, -1);
			try {
				Thread.sleep(5000); // Sleep for 5 secs
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		DataUpdater dataUpdater = new DataUpdater();
		dataUpdater.run();
	}
	@Override
	public void process(WatchedEvent event) {
		System.out.printf("\nEvent Received: %s", event.toString());
	}
}

D.ZK通用场景
栅栏/屏障
队列
锁
选主
组成员管理
两阶段提交
服务发现

E.管理ZK
最小配置
clientPort
dataDir
tickTime
存储配置
dataLogDir 事务日志存储
preAllocSize 预分配置事务日志文件块大小（64M）
snapCount 连续两次快照之间的事务数，快照文件中事务数，超过新建快照（10000）
traceFile 记录请求的日志追踪文件，for调试，但影响性能
fsync.warningthresholdms ms单位，定义flush sync"写操作"到事务日志的最长时间，超时发出调试日志
autopurge.snapRetainCount 快照和事务日志保留数量（3）
autopurge.purgeInterval 自动清理的时间间隔（0） 0：disable,1+ 单位小时
syncEnabled 允许observer同步快照与事务日志（true）,重启时减少observer的恢复时间

网络配置
globalOutstandingLimit 请求频度超过zk处理能力时，起减缓ZK压力作用(1000)
maxClientCnxns 定义一个客户最大socket并发连接数
clientPortAddress 定义接口与端口给客户
minSessionTimeout 定义最小会话超时时间,实际根据客户端连接参数定（2*tickTime)
maxSeesionTimeout 定义最大会话超时时间,实际根据客户端连接参数定（20*tickTime)

服务器整体Essemble配置
electionAlg 选举算法 0基本UDP 1未授权UDP 2授权UPD 3 TCP fast选主
initLimit follower连接到leader的超时时间
syncLimt follower同步leader的超时时间
leaderServes 配置leader是否接受客户端连接
cnxTimeout 选举过程的连接超时时间（5s）
server.x=[host]:[follower2LeaderPort]:[electionPort] 选举端口只有electionAlg = 1,2,3

配置法定人数（Quarums)
group.groupId=sid1[:sid2]
weight.groupId=weightValue //weightValue 1 default

Znode的限定与授权
create /quota_example ""
setquota -n 2 /quota_example
listquota /quota_example
create /quota_example/child1 ""
create /quota_example/child2 ""
create /quota_example/child3 "" //warning
ls /quota_example
delquota /quota_example
listquota /quota_example
授权
zookeeper.DigestAuthenticationProvider.superDigest
 org.
apache.zookeeper.server.auth.DigestAuthenticationProvider（super:<password>）

管理ZK实例
四字母单词+JMX(JAVA Management Extension jconsole)
telnet or nc 向服务器发出四字母命令，echo conf | nc host port
conf 服务器配置项
cons 所有连接服务器的连接/会话详情
crst 重置所有连接/会话的统计信息
dump 在leader节点导出会话与临时节点信息
envi 环境参数信息
ruok 向服务端发出，返回imok
stat 服务器（包括已连接上的客户端）当前状态信息
srvr 服务器当前状态信息，不显示已连接客户信息
wchs/wchc/wchp 简单/带会话连接/带znode路径 的 监听器信息
mntr 显示用于监控集群健康的变量列表


F.使用Curator装饰ZK
CuratorClient
public void myCuratorClient() throws Exception{
    CuratorZookeeperClient client = new CuratorZookeeperClient(server.getConnectString(),10000, 10000, null,new RetryOneTime(1));
    client.start();
    try{
        client.blockUntilConnectedOrTimedOut();
        String path = client.getZooKeeper().create("/test_znode","".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
    }finally{
        client.close();
    }
}
CuratorFramework
public void myCuratorFrameworkClient()throws Exception{
    CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(),new RetryOneTime(1));
    client.start();
    try{
        String path = client.create().withMode(CreateMode.PERSISTENT).forPath("/test_znode", "".getBytes());
    }finally{
        client.close();
    }
}

// namespace from builder ways to buid client
CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
CuratorFramework client = builder.connectString(server.getConnectString())
        .namespace("MyApp")
        .retryPolicy(newRetryOneTime(1))
        .build();
client.create().forPath("/test_znode", data);
Curator recipes
选主
锁（共享锁，共享可重入锁，共享可重入读/写锁，共享信号量，多条件共享锁）
屏障（单/双）
原子计数器（原子整型，长整型，原子值）
缓存
队列（简单分布式队列，分布式队列，分布式ID队列 ，分布式优先队列 ，分布式延迟队列）
节点 支持临时节点的持久化

Curator Utils
Test server/Test cluster 提供本地或集群测试环境
ZKPaths 提供静态方法操作Znode Path
EnsurePath 确保znode path在使用之间存在
BlockingQuereConsumer 类似BlokingQueue,一个消费队列
Reaper 清理没有子节点与数据的节点

G.ZK实战
使用场景
Apache Bookeeper/Hadoop/HBase/Helix/Nova
支撑机构
Yahoo/Facebook/eBay/Twitter/Netflix/Zynga/Nutanix/Vmware vSphere Storage Appliance

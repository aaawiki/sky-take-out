# Redis

> 0/20 mastered · 0% complete

## 核心基础

- ⚪ **Redis 是什么与安装部署** (unexplored)
  - Redis 特性与设计哲学
  - 单线程模型与高性能原因
  - Docker 与源码编译安装
- ⚪ **五大基本数据类型** (unexplored)
  - String
  - Hash
  - List
  - Set
  - Sorted Set
- ⚪ **通用命令与键管理** (unexplored)
  - KEYS/SCAN
  - EXPIRE/TTL
  - DEL/EXISTS/TYPE
  - 键命名规范
- ⚪ **发布订阅模式** (unexplored)
  - PUBLISH/SUBSCRIBE
  - PSUBSCRIBE 模式匹配
  - Pub/Sub 的局限性
- ⚪ **Redis 事务** (unexplored)
  - MULTI/EXEC/DISCARD
  - WATCH 乐观锁
  - 事务与 Pipeline 的区别

## 数据结构深入

- ⚪ **高级数据类型** (unexplored)
  - Bitmap 位图
  - HyperLogLog
  - GEO 地理位置
  - Stream 流
- ⚪ **底层数据结构实现** (unexplored)
  - SDS 简单动态字符串
  - Dict 字典
  - ZipList 压缩列表
  - SkipList 跳跃表
  - QuickList
  - ListPack
- ⚪ **对象系统与编码** (unexplored)
  - redisObject 结构
  - 编码转换规则
  - OBJECT ENCODING 命令
- ⚪ **Stream 消息队列** (unexplored)
  - XADD/XREAD/XREADGROUP
  - 消费者组
  - 消息确认与重试
  - Stream 与 Kafka 对比

## 持久化与高可用

- ⚪ **RDB 快照持久化** (unexplored)
  - SAVE vs BGSAVE
  - RDB 触发条件
  - fork 子进程与 COW
  - 优缺点分析
- ⚪ **AOF 日志持久化** (unexplored)
  - AOF 写入策略
  - AOF 重写机制
  - AOF 修复
  - RDB + AOF 混合持久化
- ⚪ **主从复制** (unexplored)
  - 全量复制与增量复制
  - replication buffer
  - 复制积压缓冲区
  - 主从复制延迟与一致性
- ⚪ **Sentinel 哨兵机制** (unexplored)
  - Sentinel 架构
  - 主观下线与客观下线
  - Leader 选举与故障转移
  - Sentinel 配置实践

## 集群架构

- ⚪ **Redis Cluster 集群** (unexplored)
  - 哈希槽 (Hash Slot)
  - MOVED 与 ASK 重定向
  - 集群通信协议 Gossip
  - Cluster 扩容缩容
- ⚪ **数据分片策略** (unexplored)
  - 客户端分片
  - 代理分片 (Twemproxy/Codis)
  - 一致性哈希
  - Cluster 与代理方案对比

## 性能优化与实战

- ⚪ **内存优化策略** (unexplored)
  - 内存碎片与整理
  - 共享对象池
  - 内存淘汰策略 (LRU/LFU/TTL)
  - INFO memory 分析
- ⚪ **缓存经典问题** (unexplored)
  - 缓存穿透
  - 缓存击穿
  - 缓存雪崩
  - 双写一致性问题
- ⚪ **Pipeline 与批量操作** (unexplored)
  - Pipeline 原理
  - RTT 优化
  - 批量命令最佳实践
  - Pipeline vs 事务
- ⚪ **分布式锁** (unexplored)
  - SETNX 实现
  - Redlock 算法
  - Redisson 实战
  - 分布式锁的可靠性边界
- ⚪ **Lua 脚本** (unexplored)
  - EVAL/EVALSHA
  - 脚本原子性
  - Lua 脚本调试
  - 脚本缓存与性能

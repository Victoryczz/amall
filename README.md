### 商城后端项目
[秒杀项目]:<https://github.com/Victoryczz/SecKill>
[Restlet Client]:<https://restlet.com/modules/client/>
[支付宝文档]:<https://docs.open.alipay.com/194>
##### (该项目用来熟练SSM框架的整合以及完整项目开发流程；其中v1.0完成商城后台的基本功能，包含7个子模块；v2.0对项目进行横向扩展，学习分布式项目的基本原理与应用)
##### 如果想学习高并发秒杀解决方案，可参见另外一个项目[秒杀项目]
#### 技术栈：
* 框架：Spring 4.0.3RELEASE + Spring MVC + MyBatis
* 数据库：MySQL + Redis/Redis集群 + DBCP数据库连接池
* 运行环境：Tomcat/Tomcat集群 + VSftp图片服务器 + Nginx
* 开发工具：Intellij IDEA 2017.2 + Maven3.5
#### 现在开始：
* 本地MySQL数据库新建数据库amall，为该库创建用户：amall，密码：amall，并执行amall.sql文件创建表
* IDEA打开工程，打包部署到本机Tomcat测试；由于采用前后端分离，所以可以使用Chrome插件[Restlet Client]插件进行接口测试
* 按照本地主机配置膝修改配置文件amall.properties
* 接口测试文档待更新！
#### 工程说明：
* v1.0基本模块和功能：
    * 用户模块：完成用户登录、验证、注册、修改密码、修改个人信息、忘记密码、密保、登出、session使用
    * 商品模块：
        * 前台：查询商品列表、商品详情、排序、搜索
        * 后台：商品管理、列表、搜索、图片
    * 分类管理模块：分类节点管理(增删改查)、树状结构分裂管理
    * 购物车模块：添加、删除、全选、反选、单选
    * 收获地址模块：增删改查、列表分页
    * 支付模块：集成支付宝扫码支付，参见官方文档[支付宝文档]，支付宝回调需要具有公网IP，可使用内网穿透软件实现
    * 订单管理模块：
        * 前台：创建订单、商品信息、订单详情、取消订单、支付、订单列表
        * 后台：订单列表、订单详情、订单搜索、发货管理
* v2.0重构(单机应用如何向多机应用扩展)：
    * Maven实现环境隔离(dev、beta、prod)
    * 应用横向扩展：Tomcat集群(单主机多应用)
    * Nginx实现负载均衡
    * Redis + cookie实现单点登录 -->>改进-->> spring session实现统一session管理
    * Redis集群实现分布式session(一致性算法)
    * Spring MVC拦截器实现统一权限管理 + Spring MVC全局异常处理
    * 部分接口实现RESTFul风格改造
    * Spring Schedule实现定时关单 -->>改进-->> Redis实现分布式锁定时关单 -->>改进-->> Redisson实现定时关单
* v3.0服务化
    * 还没做
    
![樱木](https://raw.githubusercontent.com/Victoryczz/SecKill/v2/src/main/resources/static/img/%E6%A8%B1%E6%9C%A8%E8%8A%B1%E9%81%93.jpg)
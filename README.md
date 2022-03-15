# QuicTest
Android app demo for comparing QUIC and TCP performance

## 测试平台
Android 12（API-31）
## Server
使用http3check作为server，进行测试
```
    "https://http3check.net/", 
    "https://http3check.net/?host=www.google.com", 
```
## Client
使用Google提供的原生Cronet进行网络请求。

包版本：18.0.1
## 可视化
[MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
v3.1.0

## 网络请求测试
以H2、H3分别向Server请求16次
### 0RTT性能测试
比较16次请求开始到收到Head的时间来说明**QUIC建立连接的性能提升**
![0RTT image](./img/result_0RTT)
### 连续请求测试
记录连续16次HTTP请求的总耗时

以下x轴为HTTP请求数，y轴为到此次HTTP请求的总耗时

![DOWNLOAD image](./img/result_DOWNLOAD)

## Client性能测试
使用Android studio的Profiler查看CPU、MEMORY、ENERGY的使用情况

以下前者为H2、后者为H3

![Performance image](./img/result_CLIENT_PERFORMANCE)


## 初步结果
1. QUIC的建立连接使用**0RTT带来了显著的性能提升**
2. **QUIC在连续HTTP请求时和TCP性能差距不大**，在连续请求测试中，从开始到最后，实际时间差距红利基本上为初始的建立连接所带来
3. QUIC在Clinet性能方面需求更大，但总体和H2相差不大



# GatewayApiSignature
接口网关统一签名

引入依赖后,在项目配置文件加上以下内容
```
security:
    signature:
    #是否开启验签
    enable: true
    #是否开启日志显示
    logEnable: false
    #过期时间
    expireTime: 60
    #签名算法模型
    algorithm: "SHA256withRSA"
    #验签公钥
    public-key: ""
    #需要验签的路劲
    include-paths:
        - "/superior/**"
        - "/new_applet/**"
    #需要排除的路径
    exclude-paths:
        - "/actuator/**"
```


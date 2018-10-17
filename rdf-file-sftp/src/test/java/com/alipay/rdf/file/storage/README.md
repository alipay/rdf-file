#FileSftpStorageTest运行须知

##linux版本

1.groupadd sftpusers

2.useradd -s /bin/false -G sftpusers sftpuser

3.编辑/etc/ssh/sshd_config,并restart sshd服务

	a.找到Subsystem这个配置项，将其修改为Subsystem sftp internal-sftp
	b.文件最尾处增加配置
	# 匹配用户组，如果要匹配多个组，多个组之间用逗号分割
	Match Group sftpusers
	# 指定登陆用户到自己的用户目录
	ChrootDirectory %h
	# 指定 sftp 命令
	ForceCommand internal-sftp
	# 这两行，如果不希望该用户能使用端口转发的话就加上，否则删掉
	X11Forwarding no
	AllowTcpForwarding no
4.chown root ~sftpuser

5.设置sftpuser用户密码

    passwd sftpuser


6.sftpuser目录下创建文件夹 mkdir files,权限如下(chown sftpuser:sftpusers files)

    drwxr-xr-x   2 sftpuser  sftpusers    68B Oct 15 18:03 files

https://segmentfault.com/a/1190000008578734

##Mac版本
1.通过设置->用户与群组->新增用户:sftpuser并设置密码,群组:sftpusers

2.设置->共享->打开远程登录并允许sftpusers组的用户访问

3.vi /etc/ssh/sshd_config

    a.找到Subsystem这个配置项，将其修改为Subsystem sftp internal-sftp
	b.文件最尾处增加配置
	# 匹配用户组，如果要匹配多个组，多个组之间用逗号分割
	Match Group sftpusers
	# 指定登陆用户到自己的用户目录
	ChrootDirectory %h
	# 指定 sftp 命令
	ForceCommand internal-sftp
	# 这两行，如果不希望该用户能使用端口转发的话就加上，否则删掉
	X11Forwarding no
	AllowTcpForwarding no

重启sshd服务

sudo launchctl unload -w /System/Library/LaunchDaemons/ssh.plist
sudo launchctl load -w /System/Library/LaunchDaemons/ssh.plist

4.chown root ~sftpuser

5.设置sftpuser用户密码

    passwd sftpuser


6.sftpuser目录下创建文件夹 mkdir files,权限如下(chown sftpuser:sftpusers files)

    drwxr-xr-x   2 sftpuser  sftpusers    68B Oct 15 18:03 files

##注意
1.根据OpenSSH(ssh -V)版本的不同考虑是否在/etc/ssh/sshd_config中添加以下配置(mac版本大多为OpenSSH_7.4p1需要添加)

    KexAlgorithms diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1
    
2.修改com.alipay.rdf.file.sftp.SftpTestUtil中的账户密码配置
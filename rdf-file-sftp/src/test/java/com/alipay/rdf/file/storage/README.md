1.groupadd sftpusers

2.useradd -s /bin/false -G sftpusers sftpuser

3.编辑/etc/ssh/sshd_config

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


6.对于测试用例FileSftpStorageTest需要自行创建files文件,权限如下

    drwxr-xr-x   2 sftpuser  sftpusers    68B Oct 15 18:03 files

https://segmentfault.com/a/1190000008578734
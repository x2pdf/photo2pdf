#readme

7z 压缩需要选择LZMA2压缩算法，而不是BCJ2。
因为 rg.apache.commons 的 commons-compress 解压缩7z并不支持 BCJ2 压缩方法。

安装7z命令工具，然后执行一下命令：

压缩nodejs（mac）

7zz a nodejs-mac-x64.7z ./nodejs-mac-x64 -m0=lzma2 -mf=off -mx=9

检测算法是否是lzma2：
7zz l -slt nodejs-mac-x64.7z


压缩nodejs（windows）

7zz a nodejs.7z ./nodejs -m0=lzma2 -mf=off -mx=9

检测算法是否是lzma2：
7zz l -slt nodejs.7z

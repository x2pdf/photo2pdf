#readme

7z 压缩需要选择LZMA2压缩算法，而不是BCJ2。
因为 rg.apache.commons 的 commons-compress 解压缩7z并不支持 BCJ2 压缩方法。

安装7z命令工具，然后执行一下命令：

7zz a jxl-x64-windows-static.7z ./jxl-x64-windows-static -m0=lzma2 -mf=off -mx=9

检测算法是否是lzma2：
7zz l -slt jxl-x64-windows-static.7z
#readme

7z 压缩需要选择LZMA2压缩算法，而不是BCJ2。
因为 rg.apache.commons 的 commons-compress 解压缩7z并不支持 BCJ2 压缩方法。

安装7z命令工具，然后执行一下命令：

7zz a jxl-x64-windows-static.7z ./jxl-x64-windows-static -m0=lzma2 -mf=off -mx=9

检测算法是否是lzma2：
7zz l -slt jxl-x64-windows-static.7z


# macOS 下的jxl静态包的打包过程

编译环境：
https://github.com/libjxl/libjxl/releases 的版本：v0.13.0 （实际是v.12 tag 之后的v.13.0 开发中的版本，v.13还未真实打tag。）

xcode-select --install
brew install cmake ninja llvm coreutils
brew install giflib jpeg-turbo libpng libwebp brotli  # 可选格式支持


确保使用 Homebrew 的 Clang（推荐）
export PATH="/opt/homebrew/opt/llvm/bin:$PATH"
export CC=clang
export CXX=clang++


获取源码
git clone https://github.com/libjxl/libjxl.git --recursive --shallow-submodules
cd libjxl

编译
mkdir -p build && cd build

cmake .. \
  -DCMAKE_BUILD_TYPE=Release \
  -DBUILD_SHARED_LIBS=OFF \
  -DBUILD_TESTING=OFF \
  -DCMAKE_OSX_ARCHITECTURES=arm64 \
  -DCMAKE_OSX_DEPLOYMENT_TARGET=11.0 \
  -DJPEGXL_ENABLE_SKCMS=OFF \
  -DJPEGXL_ENABLE_SJPEG=OFF \
  -DJPEGXL_ENABLE_PLUGINS=OFF \
  -G Ninja

重要选项解释：-DBUILD_SHARED_LIBS=OFF：强制构建静态库（.a 而非 .dylib），工具会尽量静态链接。
-DCMAKE_OSX_ARCHITECTURES=arm64：锁定 Apple Silicon。
-DCMAKE_OSX_DEPLOYMENT_TARGET：设置最低兼容 macOS 版本（越高越好，但要平衡兼容性）。
其他 JPEGXL_ENABLE_*=OFF 可以进一步减少外部依赖。


cmake --build . --config Release -j$(sysctl -n hw.ncpu)

编译完成后，tools/cjxl 和 tools/djxl 等可执行文件就在 build/tools/ 目录下。
然后再将build/tools里面的文件全部复制到新建文件夹：jxl-arm64-mac-static 里面，再使用7z并且使用lzma2算法压缩即可得到jxl-arm64-mac-static.7z 部署包。


# cjxl 命令
cjxl input.png output.jxl [options]

-d 1
| distance | 效果      |
| -------: | ------- |
|        0 | 无损      |
|      0.3 | 极高质量    |
|      0.5 | 很高质量    |
|        1 | 官方推荐高质量 |
|        2 | 默认质量附近  |
|        3 | 中等      |
|       4+ | 压缩率更高   |

--lossless
等价于：-d 0

压缩速度
-e <1~10>
| effort | 用途   |
| ------ | ---- |
| 1      | 最快   |
| 3      | 开发测试 |
| 5      | 日常   |
| 7      | 默认   |
| 9      | 发布   |
| 10     | 极限压缩 |

生成渐进式 JPEG XL
--progressive

# djxl 命令
基本没有自定义参数的设定配置。
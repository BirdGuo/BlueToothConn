package com.gxw.bluetoothhelper.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by GXW on 2016/2/10 0010.
 */
public class FileUtil {

    /**
     * The Log tag.
     */
    static final String LOG_TAG = "FileUtils";

    /**
     * 操作成功返回值
     */
    public static final int SUCCESS = 0;

    /**
     * 操作失败返回值
     */
    public static final int FAILED = -1;

    private static final int BUF_SIZE = 32 * 1024; // 32KB

    /**
     * The constant S_IRWXU.
     */
    public static final int S_IRWXU = 00700;
    /**
     * The constant S_IRUSR.
     */
    public static final int S_IRUSR = 00400;
    /**
     * The constant S_IWUSR.
     */
    public static final int S_IWUSR = 00200;
    /**
     * The constant S_IXUSR.
     */
    public static final int S_IXUSR = 00100;

    /**
     * The constant S_IRWXG.
     */
    public static final int S_IRWXG = 00070;
    /**
     * The constant S_IRGRP.
     */
    public static final int S_IRGRP = 00040;
    /**
     * The constant S_IWGRP.
     */
    public static final int S_IWGRP = 00020;
    /**
     * The constant S_IXGRP.
     */
    public static final int S_IXGRP = 00010;

    /**
     * The constant S_IRWXO.
     */
    public static final int S_IRWXO = 00007;
    /**
     * The constant S_IROTH.
     */
    public static final int S_IROTH = 00004;
    /**
     * The constant S_IWOTH.
     */
    public static final int S_IWOTH = 00002;
    /**
     * The constant S_IXOTH.
     */
    public static final int S_IXOTH = 00001;

    private static WeakReference<Exception> exReference;

    /**
     * 文件类型枚举
     */
    public static enum FileState {
        /**
         * The F state dir.
         */
        FState_Dir("I am director!"), // 目录
        /**
         * The F state file.
         */
        FState_File("I am file!"), // 文件
        /**
         * The F state none.
         */
        FState_None("I am a ghost!"), // 不存在
        /**
         * The F state other.
         */
        FState_Other("I am not human!"); // 其他类型

        private String tag;

        private FileState(String tag) {
            this.tag = tag;
        }

        /**
         * Gets tag.
         *
         * @return the tag
         */
        public String getTag() {
            return tag;
        }

        @Override
        public String toString() {
            return tag;
        }
    }

    private FileUtil() {
    }

    /**
     * 获取文件状态
     *
     * @param path
     *         the path
     *
     * @return file state
     */
    public static FileState fileState(String path) {
        return fileState(new File(path));
    }

    /**
     * File state file state.
     *
     * @param file
     *         the file
     *
     * @return the file state
     */
    public static FileState fileState(File file) {
        if (!file.exists())
            return FileState.FState_None;

        if (file.isFile())
            return FileState.FState_File;

        if (file.isDirectory())
            return FileState.FState_Dir;

        return FileState.FState_Other;
    }

    /**
     * 创建文件夹
     *
     * @param path
     *         the path
     *
     * @return int
     */
    public static int createDir(String path) {
        // int l = path.length();
        // if(path.charAt(l-1) == File.separatorChar){ //如果末尾是 /
        // 会导致创建目录失败，测试发现不会
        // path = path.substring(0, l-1);
        // }

        return createDir(new File(path));
    }

    /**
     * Create dir int.
     *
     * @param file
     *         the file
     *
     * @return the int
     */
    public static int createDir(File file) {
        if (file.exists()) {
            if (file.isDirectory())
                return SUCCESS;
            file.delete(); // 避免他是一个文件存在
        }

        if (file.mkdirs())
            return SUCCESS;

        return FAILED;
    }

    /**
     * Remove dir int.
     *
     * @param path
     *         the path
     *
     * @return the int
     */
    public static int removeDir(String path) {
        return removeDir(new File(path));
    }

    /**
     * 删除一个文件夹
     * <p/>
     * <p/>
     * by:yichou 2013-5-7 15:24:41
     * <p/>
     *
     * @param dir
     *         the dir
     *
     * @return int
     */
    public static int removeDir(File dir) {
        if (!dir.exists())
            return SUCCESS;

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory())
                        removeDir(f);
                    else
                        f.delete();
                }
            }
        }

        return dir.delete() ? SUCCESS : FAILED;
    }

    /**
     * Check parent path.
     *
     * @param path
     *         the path
     *
     * @see {@link #checkParentPath(File)}
     */
    public static void checkParentPath(String path) {
        checkParentPath(new File(path));
    }

    /**
     * 在打开一个文件写数据之前，先检测该文件路径的父目录是否已创建，保证能创建文件
     *
     * @param file
     *         the file
     */
    public static void checkParentPath(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory())
            createDir(parent);
    }

    /**
     * 将一缓冲流写入文件
     *
     * @param path
     *         目标文件路径
     * @param is
     *         输入流
     * @param isAppend
     *         是否追加
     *
     * @return 成功 {@link #SUCCESS}； 失败 {@link #FAILED}
     */
    public static int streamToFile(String path, InputStream is, boolean isAppend) {
        return streamToFile(new File(path), is, isAppend);
    }

    /**
     * Stream to file int.
     *
     * @param file
     *         the file
     * @param is
     *         the is
     * @param isAppend
     *         the is append
     *
     * @return the int
     */
    public static int streamToFile(File file, InputStream is, boolean isAppend) {
        checkParentPath(file);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, isAppend);
            byte[] buf = new byte[BUF_SIZE];
            int readSize = 0;

            while ((readSize = is.read(buf)) != -1)
                fos.write(buf, 0, readSize);
            fos.flush();

            return SUCCESS;
        } catch (Exception e) {
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }

        return FAILED;
    }

    /**
     * 写字节数组到文件
     *
     * @param file
     *         目标文件
     * @param data
     *         字节数组
     * @param offset
     *         偏移 {@code >=0&&<=data.length}
     * @param length
     *         长度 ==0 表示 data.length
     * @param isAppend
     *         是否追加
     *
     * @return 成功 {@link #SUCCESS}； 失败 {@link #FAILED}
     */
    public static int bytesToFile(File file, byte[] data, int offset,
                                  int length, boolean isAppend) {
        checkParentPath(file);

        if (data == null)
            return FAILED;

        if (length <= 0)
            length = data.length;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, isAppend);
            fos.write(data, offset, length);
            fos.flush();

            return SUCCESS;
        } catch (Exception e) {
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }

        return FAILED;
    }

    /**
     * Bytes to file int.
     *
     * @param file
     *         the file
     * @param data
     *         the data
     * @param isAppend
     *         the is append
     *
     * @return the int
     */
    public static int bytesToFile(File file, byte[] data, boolean isAppend) {
        return bytesToFile(file, data, 0, data.length, isAppend);
    }

    /**
     * Bytes to file int.
     *
     * @param file
     *         the file
     * @param data
     *         the data
     *
     * @return the int
     */
    public static int bytesToFile(File file, byte[] data) {
        return bytesToFile(file, data, 0, data.length, false);
    }

    /**
     * String to file int.
     *
     * @param file
     *         the file
     * @param string
     *         the string
     *
     * @return the int
     */
    public static int stringToFile(File file, String string) {
        return bytesToFile(file, string.getBytes());
    }

    /**
     * Bytes to file int.
     *
     * @param path
     *         the path
     * @param data
     *         the data
     * @param offset
     *         the offset
     * @param length
     *         the length
     * @param isAppend
     *         the is append
     *
     * @return the int
     *
     * @see {@link #bytesToFile(File file, byte[] data, int offset, int length, boolean isAppend)}
     */
    public static int bytesToFile(String path, byte[] data, int offset,
                                  int length, boolean isAppend) {
        return bytesToFile(new File(path), data, offset, length, isAppend);
    }

    /**
     * 读取文件内容到二进制缓冲区
     *
     * @param path
     *         文件路径
     * @param offset
     *         起始位置
     * @param length
     *         读取长度 ，0为全部
     *
     * @return 失败 或 length <=0 返回null，成功返回 字节数组
     */
    public static byte[] fileToBytes(String path, int offset, int length) {
        return fileToBytes(new File(path), offset, length);
    }

    /**
     * File to bytes byte [ ].
     *
     * @param file
     *         the file
     *
     * @return the byte [ ]
     */
    public static byte[] fileToBytes(File file) {
        return fileToBytes(file, 0, 0);
    }

    /**
     * File to string string.
     *
     * @param file
     *         the file
     *
     * @return the string
     */
    public static String fileToString(File file) {
        byte[] data = fileToBytes(file);
        return data != null ? new String(data) : null;
    }

    /**
     * 读取文件内容到二进制缓冲区
     *
     * @param file
     *         文件路径
     * @param offset
     *         起始位置
     * @param length
     *         读取长度，==0 为全部
     *
     * @return 失败 或 length < 0 返回null，成功返回 字节数组
     */
    public static byte[] fileToBytes(File file, int offset, int length) {
        if (length < 0 || !file.exists())
            return null;

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            if (length == 0)
                length = is.available();
            byte[] outBuf = new byte[length];
            is.read(outBuf, offset, length);

            return outBuf;
        } catch (Exception e) {
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }

        return null;
    }

    /**
     * 复制文件, 对于大的文件, 推荐开启一个线程来复制. 防止长时间阻塞
     *
     * @param dstPath
     *         the dst path
     * @param srcPath
     *         the src path
     *
     * @return 成功 {@link #SUCCESS}； 失败 {@link #FAILED}
     */
    public static int copyTo(String dstPath, String srcPath) {
        return copyTo(new File(dstPath), new File(srcPath));
    }

    /**
     * Copy to int.
     *
     * @param dstFile
     *         the dst file
     * @param srcFile
     *         the src file
     *
     * @return the int
     */
    public static int copyTo(File dstFile, File srcFile) {
        if (fileState(srcFile) != FileState.FState_File) // 源非文件
            return FAILED;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(srcFile);

            return streamToFile(dstFile, fis, false);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }

        return FAILED;
    }

    /**
     * Asset to file int.
     *
     * @param context
     *         the context
     * @param assetName
     *         the asset name
     * @param path
     *         the path
     *
     * @return the int
     *
     * @see {@link #assetToFile(Context context, String assetName, File file)}
     */
    public static int assetToFile(Context context, String assetName, String path) {
        return assetToFile(context, assetName, new File(path));
    }

    /**
     * assets 目录下的文件保存到本地文件
     *
     * @param context
     *         the context
     * @param assetName
     *         assets下名字，非根目录需包含路径 a/b.xxx
     * @param file
     *         目标文件
     *
     * @return 成功 {@link #SUCCESS}； 失败 {@link #FAILED}
     */
    public static int assetToFile(Context context, String assetName, File file) {
        InputStream is = null;

        try {
            is = context.getAssets().open(assetName);
            return streamToFile(file, is, false);
        } catch (Exception e) {
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }

        return FAILED;
    }

    /**
     * Asset to file if not exist int.
     *
     * @param context
     *         the context
     * @param assetName
     *         the asset name
     * @param file
     *         the file
     *
     * @return the int
     */
    public static int assetToFileIfNotExist(Context context, String assetName, File file) {
        InputStream is = null;
        try {
            is = context.getAssets().open(assetName);
            if (!checkExistBySize(file, is.available())) {
                return streamToFile(file, is, false);
            } else {
                return SUCCESS;
            }
        } catch (Exception e) {
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }

        return FAILED;
    }

    /**
     * 读取 assets 下 name 文件返回字节数组
     *
     * @param context
     *         the context
     * @param name
     *         the name
     *
     * @return 失败返回 Null
     */
    public static byte[] assetToBytes(Context context, String name) {
        InputStream is = null;

        try {
            is = context.getAssets().open(name);
            byte[] buf = new byte[is.available()];
            is.read(buf);

            return buf;
        } catch (Exception e) {
            setLastException(e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }

        return null;
    }

    /**
     * 从 Assets 文件读取文件全部，并转为字符串
     *
     * @param context
     *         the context
     * @param name
     *         文件名
     *
     * @return 读取到的字符串 string
     *
     * @author Yichou  <p/> date 2013-4-2 11:30:05
     */
    public static String assetToString(Context context, String name) {
        byte[] data = assetToBytes(context, name);

        return data != null ? new String(data) : null;
    }

    /**
     * 检查 assets 下是否存在某文件
     *
     * @param am
     *         the am
     * @param name
     *         the name
     *
     * @return boolean
     */
    public static boolean assetExist(AssetManager am, String name) {
        InputStream is = null;
        try {
            is = am.open(name);
            return true;
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }

        return false;
    }

    /**
     * Is sd mounted boolean.
     *
     * @return SD卡是否已挂载 boolean
     */
    public static boolean isSDMounted() {
        String sdState = Environment.getExternalStorageState(); // 判断sd卡是否存在
        return sdState.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Is sd available boolean.
     *
     * @param minRemainMB
     *         unit mb
     *
     * @return the boolean
     */
    public static boolean isSDAvailable(int minRemainMB) {
        if (!isSDAvailable())
            return false;

        return (getSDLeftSpace() >= minRemainMB * 1024L * 1024L);
    }

    /**
     * 2013-9-27 check if the sdcard mounted and can read and wirte
     *
     * @return the boolean
     */
    public static boolean isSDAvailable() {
        if (!isSDMounted())
            return false;

        File file = Environment.getExternalStorageDirectory();
        if (!file.canRead() || !file.canWrite())
            return false;

        return true;
    }

    /**
     * Gets sd left space.
     *
     * @return SD卡剩余容量 sd left space
     */
    public static long getSDLeftSpace() {
        if (isSDMounted() == false) {
            return 0;
        } else {
            StatFs statfs = new StatFs(
                    Environment.getExternalStorageDirectory() + File.separator);
            return (long) statfs.getAvailableBlocks()
                    * (long) statfs.getBlockSize();
        }
    }

    /**
     * Cover size string.
     *
     * @param size
     *         the size
     *
     * @return the string
     */
    public static String coverSize(long size) {
        String s = "";
        if (size < 1024)
            s += size + "b";
        else if (size < 1024 * 1024) {
            s = String.format(Locale.getDefault(), "%.1fK", size / 1024f);
        } else if (size < 1024 * 1024 * 1024) {
            s = String.format(Locale.getDefault(), "%.1fM", size / 1024 / 1024f);
        } else {
            s = String.format(Locale.getDefault(), "%.1fG", size / 1024 / 1024 / 1024f);
        }

        return s;
    }

    /**
     * Gets rom left.
     *
     * @return the rom left
     */
    public static long getROMLeft() {
        File data = Environment.getDataDirectory();

        StatFs sf = new StatFs(data.getAbsolutePath());
        long blockSize = sf.getBlockSize();
        long blockCount = sf.getBlockCount();
        long availCount = sf.getAvailableBlocks();

        Log.i("", "ROM Total:" + coverSize(blockSize * blockCount) + ", Left:"
                + coverSize(availCount * blockSize));

        return availCount * blockSize;
    }

    /**
     * 获取私有目录下的文件夹绝对路径，末尾带 "/"，不创建
     *
     * @param context
     *         the context
     * @param name
     *         文件夹名
     *
     * @return dir path in private
     */
    public static String getDirPathInPrivate(Context context, String name) {
        return context.getDir(name, Context.MODE_PRIVATE).getAbsolutePath()
                + File.separator;
    }

    /**
     * 或者本应用 so 存放路径
     *
     * @param context
     *         the context
     *
     * @return so path
     */
    public static String getSoPath(Context context) {
        return context.getApplicationInfo().dataDir + "/lib/";
    }

    /**
     * Try file lock file lock.
     *
     * @param path
     *         the path
     *
     * @return the file lock
     */
    public static FileLock tryFileLock(String path) {
        return tryFileLock(new File(path));
    }

    /**
     * 占用某个文件锁
     *
     * @param file
     *         the file
     *
     * @return file lock
     */
    public static FileLock tryFileLock(File file) {
        try {
            checkParentPath(file); // 父目录不存在会导致创建文件锁失败

            FileOutputStream fos = new FileOutputStream(file);
            FileLock fl = fos.getChannel().tryLock();
            if (fl.isValid()) {
                Log.i(LOG_TAG, "tryFileLock " + file + " SUC!");
                return fl;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "tryFileLock " + file + " FAIL! " + e.getMessage());
        }

        return null;
    }

    /**
     * Free file lock.
     *
     * @param fl
     *         the fl
     * @param file
     *         the file
     */
    public static void freeFileLock(FileLock fl, File file) {
        if (file != null)
            file.delete();

        if (fl == null || !fl.isValid())
            return;

        try {
            fl.release();
            Log.i(LOG_TAG, "freeFileLock " + file + " SUC!");
        } catch (IOException e) {
        }
    }

    /**
     * 截取路径名
     *
     * @param absolutePath
     *         the absolute path
     *
     * @return path name
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 重命名
     *
     * @param oldName
     *         the old name
     * @param newName
     *         the new name
     *
     * @return boolean
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root
     *         the root
     *
     * @return 绝对路径 list
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory()) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * 删除空目录
     * <p/>
     * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     *
     * @param path
     *         the path
     *
     * @return int
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    /**
     * 获取SD卡的根目录，末尾带\
     *
     * @return sd root
     */
    public static String getSDRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 2013-10-8 by yichou
     * <p/>
     * 检查一个文件本地是否存在，通过名称，长度，如果2者都符合，返回 true，否则返回 false
     *
     * @param file
     *         the file
     * @param size
     *         the size
     *
     * @return the boolean
     */
    public static boolean checkExistBySize(File file, long size) {
        if (!file.exists() || !file.isFile() || (file.length() != size))
            return false;

        return true;
    }

    /**
     * Sets permissions.
     *
     * @param file
     *         the file
     * @param mode
     *         the mode
     *
     * @return the permissions
     */
//public static native int setPermissions(String file, int mode, int uid, int gid);
    public static int setPermissions(String file, int mode) {
        return setPermissions(file, mode, -1, -1);
    }

    private static final Class<?>[] SIG_SET_PERMISSION =
            new Class<?>[]{String.class, int.class, int.class, int.class};

    /**
     * Sets permissions.
     *
     * @param file
     *         the file
     * @param mode
     *         the mode
     * @param uid
     *         the uid
     * @param gid
     *         the gid
     *
     * @return the permissions
     */
    public static int setPermissions(String file, int mode, int uid, int gid) {
        try {
            Class<?> clazz = Class.forName("android.os.FileUtils");
            Method method = clazz.getDeclaredMethod("setPermissions", SIG_SET_PERMISSION);
            method.setAccessible(true);
            return (Integer) method.invoke(null, file, mode, uid, gid);
        } catch (Exception e) {
        }

        return -1;
    }

    /**
     * 把 sd卡上 src 目录 链接到 私有目录 dst
     * <p/>
     * <p>例：createLink("/mnt/sdcard/freesky", "/data/data/com.test/app_links/free")
     * 之后 /data/data/com.test/app_links/free -> /mnt/sdcard/freesky
     *
     * @param src
     *         源目录，在SD卡上
     * @param dst
     *         目标路径完整
     *
     * @return boolean
     */
    public static boolean createLink(String src, String dst) {
        try {
            String command = String.format("ln -s %s %s", src, dst);
            Runtime runtime = Runtime.getRuntime();
            Process ps = runtime.exec(command);
            InputStream in = ps.getInputStream();

            int c;
            while ((c = in.read()) != -1) {
                System.out.print(c);// 如果你不需要看输出，这行可以注销掉
            }

            in.close();
            ps.waitFor();

            return true;
        } catch (Exception e) {
        }

        return false;
    }

//    /**
//     * 读取输入流全部内容到字节数组
//     *
//     * @param is 输入流，传入者关闭
//     * @return 成功 数组，失败 null
//     * <p/>
//     * 2014-9-26
//     */
//    public static ByteArrayBuffer streamToByteArray(InputStream is) {
//        try {
//            byte[] buf = new byte[256];
//            int read = 0;
//            ByteArrayBuffer array = new ByteArrayBuffer(1024);
//
//            while ((read = is.read(buf)) != -1) {
//                array.append(buf, 0, read);
//            }
//
//            return array;
//        } catch (Exception e) {
//            setLastException(e);
//        }
//
//        return null;
//    }
//
//    /**
//     * 读取输入流全部，转为字符串
//     *
//     * @param is 输入流，传入者关闭
//     * @return 成功 字串，失败 null
//     * <p/>
//     * 2014-9-26
//     */
//    public static String streamToString(InputStream is) {
//        ByteArrayBuffer buffer = streamToByteArray(is);
//        if (buffer != null)
//            return new String(buffer.buffer(), 0, buffer.length());
//
//        return null;
//    }

    /**
     * Print last exception.
     */
    public static void printLastException() {
        Exception e = getLastException();
        if (e != null)
            e.printStackTrace();
    }

    private static void setLastException(Exception e) {
        exReference = new WeakReference<Exception>(e);
    }

    /**
     * Gets last exception.
     *
     * @return the last exception
     */
    public static Exception getLastException() {
        return exReference != null ? exReference.get() : null;
    }
}

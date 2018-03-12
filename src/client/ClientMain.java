package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

public class ClientMain {

    public static void main(String[] args) throws UnknownHostException, IOException {
        String currPath = currPath();
        // String localFileName = wrapPath(currPath, "icessm");
        String localFileName = "C:/Users/Administrator/git/icessm/target/icessm.war";
        String remoteFileName = "/root/apache-tomcat-7.0.82,icessm.war";
        String ip = "47.104.123.123";
        int port = 9003;

        client(localFileName, remoteFileName, ip, port);
    }

    private static String wrapPath(String currPath, String projectName) {
        return currPath + "/" + projectName + "/target/" + projectName + ".war";
    }

    private static void client(String localFileName, String remoteFileName, String ip, int port)
            throws UnknownHostException, IOException, UnsupportedEncodingException {
        Socket socket = new Socket(ip, port);
        InputStream inputStream = socket.getInputStream();
        byte[] bys = new byte[1024 * 1024];
        int read = inputStream.read(bys, 0, 1024);
        if (read != -1) {
            System.out.println(new String(bys, "UTF-8"));
        }
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(clientData(localFileName, remoteFileName));
        outputStream.flush();
        // socket.close();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static byte[] clientData(String localFileName, String remoteFileName) throws UnsupportedEncodingException {
        String content = content(remoteFileName);
        byte[] file = file(localFileName);
        byte[] bys = sum(content, file);
        return bys;
    }

    private static byte[] sum(String content, byte[] file) {
        byte[] bys = null;
        if (content == null || "".equals(content)) {
            bys = new byte[0];
        } else {
            try {
                bys = content.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        byte[] separate = new byte[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        byte[] sum = new byte[bys.length + separate.length + file.length];
        int index = 0;
        for (int i = 0; i < bys.length; i++) {
            sum[index++] = bys[i];
        }
        for (int i = 0; i < separate.length; i++) {
            sum[index++] = separate[i];
        }
        for (int i = 0; i < file.length; i++) {
            sum[index++] = file[i];
        }
        return sum;
    }

    private static byte[] file(String fileName) {
        return FileUtil.readBytes(fileName);
    }

    private static String content(String fileName) {
        return fileName;
    }

    private static String currPath() {
        String classPath = ClassUtil.getClassPath();
        if (classPath.endsWith("/")) {
            classPath = classPath.substring(0, classPath.length() - 1);
        }
        if (classPath.endsWith("/bin")) {
            classPath = classPath.substring(0, classPath.length() - 4);
        }
        return StrUtil.subBefore(classPath, "/", true);
    }
}

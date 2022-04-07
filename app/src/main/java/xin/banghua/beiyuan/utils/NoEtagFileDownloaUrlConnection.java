package xin.banghua.beiyuan.utils;

import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;

import java.io.IOException;
import java.net.URL;

public class NoEtagFileDownloaUrlConnection extends FileDownloadUrlConnection {

    public NoEtagFileDownloaUrlConnection(String originUrl, Configuration configuration) throws IOException {
        super(originUrl, configuration);
    }

    public NoEtagFileDownloaUrlConnection(URL url, Configuration configuration) throws IOException {
        super(url, configuration);
    }

    public NoEtagFileDownloaUrlConnection(String originUrl) throws IOException {
        super(originUrl);
    }

    @Override
    public void addHeader(String name, String value) {
        if ("If-Match".equals(name)) {
            return;
        }
        super.addHeader(name, value);
    }
}

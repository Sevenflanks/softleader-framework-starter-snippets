package tw.com.softleader.starter_snippets.demo.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

/**
 * @see https://github.com/softleader/softleader-framework-docs/wiki/Entity-Guarantee
 */
@Slf4j
@Service
@Validated
public class FtpService {

	private final static String FTP_PATH = "/testFtp";
	private final static String SFTP_PATH = "/testSftp";

	public void uploadFileToFtp(final InputStream fileInputStream, final String fileName, final String hostname, final String username, final String password) {
		final StopWatch sw = new StopWatch();
		sw.start();
		final FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(hostname);
			ftpClient.login(username, password);
			// 設置上傳目錄
			ftpClient.setBufferSize(2048);
			ftpClient.setControlEncoding("utf-8");
			// 設置檔案類型（二進位）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.changeWorkingDirectory(FTP_PATH);
			ftpClient.storeFile(fileName, fileInputStream);

		} catch (IOException e) {
			log.error("{}: {}", fileName, e.getMessage(), e);
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		sw.stop();
		log.info("{} uploadFtp cost: {}", fileName, sw);
	}
	
	public InputStream getFileFromFtp(final String fileName, final String hostname, final String username, final String password){
		final StopWatch sw = new StopWatch();
		sw.start();
		final FTPClient ftpClient = new FTPClient();
		InputStream is = null;
		try {
			ftpClient.connect(hostname);
			ftpClient.login(username, password);
			// 設置檔案位置
			ftpClient.changeWorkingDirectory(FTP_PATH);
			// 取得檔案
			is = ftpClient.retrieveFileStream(fileName);

		} catch (IOException e) {
			log.error("{}: {}", fileName, e.getMessage(), e);
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		sw.stop();
		log.info("{} getFileFromFtp cost: {}", fileName, sw);
		return is;
	}
	
	

	public void uploadFileToSftp(final InputStream fileInputStream, final String fileName, final String host, final String username, final String password) {
		final StopWatch sw = new StopWatch();
		sw.start();
		final int port = 22;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, host, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(SFTP_PATH);
			channelSftp.put(fileInputStream, fileName);
			channelSftp.exit();
		} catch (Exception e) {
			log.error("{}: {}", fileName, e.getMessage(), e);
		} finally {
			if (channelSftp != null && channelSftp.isConnected()) {
				channelSftp.disconnect();
			}
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		sw.stop();
		log.info("{} uploadSftp cost: {}", fileName, sw);
	}

}
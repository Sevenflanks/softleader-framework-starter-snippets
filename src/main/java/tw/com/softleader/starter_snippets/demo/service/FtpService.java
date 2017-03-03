package tw.com.softleader.starter_snippets.demo.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
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

	/**
	 * 將檔案放置到 FTP server
	 * 
	 * @param fileInputStream
	 * @param fileName
	 * @param ftpPath
	 * @param hostname
	 * @param username
	 * @param password
	 */
	public void uploadFileToFtp(final InputStream fileInputStream, final String fileName, final String ftpPath, final String hostname, final String username, final String password) {
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
			ftpClient.changeWorkingDirectory(ftpPath);
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
	
	/**
	 * 從FTP sever 取得檔案
	 * 
	 * @param fileName
	 * @param ftpPath
	 * @param hostname
	 * @param username
	 * @param password
	 * @return
	 */
	public InputStream getFileFromFtp(final String fileName, final String ftpPath, final String hostname, final String username, final String password){
		final StopWatch sw = new StopWatch();
		sw.start();
		final FTPClient ftpClient = new FTPClient();
		InputStream is = null;
		try {
			ftpClient.connect(hostname);
			ftpClient.login(username, password);
			// 設置檔案位置
			ftpClient.changeWorkingDirectory(ftpPath);
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
	
	/**
	 * 放置檔案到SFTP server
	 * 
	 * @param fileInputStream
	 * @param fileName
	 * @param sftpPath
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 */
	public void uploadFileToSftp(final InputStream fileInputStream, final String fileName, final String sftpPath, final String hostname, final int port, final String username, final String password) {
		final StopWatch sw = new StopWatch();
		sw.start();
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, hostname, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(sftpPath);
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
	
	/**
	 * 從SFTP server 取得 檔案
	 * 
	 * @param fileName
	 * @param sftpPath
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 */
	public InputStream getFileFromSftp(final String fileName, final String sftpPath, final String hostname, final int port, final String username, final String password){
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		InputStream is = null;
		try {
			final JSch jsch = new JSch();
			session = jsch.getSession(username, hostname, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(sftpPath);
			InputStream sftpIs = null;
			try {
				sftpIs = channelSftp.get(fileName);
				is = IOUtils.toBufferedInputStream(sftpIs);
			}
			catch (Exception e) {
				log.error("file is not exists. {}", fileName, e);
			}finally{
				IOUtils.closeQuietly(sftpIs);
			}
			channelSftp.exit();
		}
		catch (Exception ex) {
			log.error("{}: {}", fileName, ex.getMessage(), ex);
		}
		finally{
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
		return is;
	}

}
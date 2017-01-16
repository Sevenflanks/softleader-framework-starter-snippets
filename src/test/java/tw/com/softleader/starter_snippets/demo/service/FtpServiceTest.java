package tw.com.softleader.starter_snippets.demo.service;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tw.com.softleader.domain.config.DefaultDomainConfiguration;
import tw.com.softleader.starter_snippets.config.DataSourceConfig;
import tw.com.softleader.starter_snippets.config.ServiceConfig;

@Slf4j
@WithMockUser("demo")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceConfig.class, DataSourceConfig.class, DefaultDomainConfiguration.class })
@Transactional
public class FtpServiceTest {

	@Autowired
	private FtpService ftpService;
	
	private final static String UTF_8 = "UTF-8";
	private final static String FTP_PATH = "/testFtp";
//	private final static String SFTP_PATH = "/testSftp";

	@Test
	public void testUploadFtpAndGetFile() {
		// host: softleader.com.tw
		final String hostname = "118.163.91.247";
		final String username = "test";
		final String password = "test";
		final String fileName = "testFtp.txt";
		final String exampleString = "THIS IS A SAMPLE, 這是範例";

		InputStream fileInputStream = null;
		try {
			fileInputStream = IOUtils.toInputStream(exampleString, UTF_8);
			ftpService.uploadFileToFtp(fileInputStream, fileName, FTP_PATH, hostname, username, password);
		} catch (Exception e1) {
			log.error(e1.getMessage(), e1);
			Assert.assertNull(e1);
		}finally{
			IOUtils.closeQuietly(fileInputStream);
		}
		
		InputStream fileFromFtp = null;
		try {
			fileFromFtp = ftpService.getFileFromFtp(fileName, FTP_PATH, hostname, username, password);
			final String theString = IOUtils.toString(fileFromFtp, UTF_8); 
			Assert.assertTrue(exampleString.equals(theString));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Assert.assertNull(e);
		}finally{
			IOUtils.closeQuietly(fileFromFtp);
		}
	}
	
	@Test
	public void testUploadSftpAndGetFile() {
		
	}
}
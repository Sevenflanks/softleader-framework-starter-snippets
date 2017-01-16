package tw.com.softleader.starter_snippets.demo.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

	@Test
	public void testUploadFtp() {
		// host: softleader.com.tw
		final String hostname = "118.163.91.247";
		final String username = "test";
		final String password = "test";
		final String fileName = "test.txt";

		try {
			log.info("start load file: {}", fileName);
			final InputStream fileInputStream = new FileInputStream(new File("D:\\PLA\\test.txt"));
			try {
				ftpService.uploadFileToFtp(fileInputStream, fileName, hostname, username, password);
			} catch (Exception e1) {
				log.error(e1.getMessage(), e1);
			}finally{
				IOUtils.closeQuietly(fileInputStream);
			}
			log.info("finish uploadFtp: {}", fileName);
			
			log.info("start getFileFromFtp: {}", fileName);
			final InputStream fileFromFtp = ftpService.getFileFromFtp(fileName, hostname, username, password);
			Assert.assertNotNull(fileFromFtp);
			final FileOutputStream fos = new FileOutputStream("D:\\PLA\\getTest.txt");
			try {
				IOUtils.write(IOUtils.toByteArray(fileFromFtp), fos);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}finally{
				IOUtils.closeQuietly(fileFromFtp);
				IOUtils.closeQuietly(fos);
			}
			log.info("finish getFileFromFtp: {}", fileName);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}

}
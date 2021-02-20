package com.receiver.app.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.receiver.app.exception.ReceiverException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReceiverServiceImpl implements ReceiverService {

	@Value("${buffer_size}")
	private Integer buffer_size;

	@Value("${port}")
	private Integer port;

	@Value("#{environment['FILE_PATH']}")
	private String FILE_PATH;

	public void process() {
		log.info("Reciever Service Started");
		try {
			SocketChannel socketChannel = createServerSocketChannel();
			readFileFromSocketChannel(socketChannel);
		} catch (ReceiverException se) {
			log.error("", se);
		}
		log.info("Reciever Service Finished");
	}

	private void readFileFromSocketChannel(SocketChannel socketChannel) {
		try {
			log.debug("Psth to save file {}", FILE_PATH);

			if (StringUtils.isEmpty(FILE_PATH) == Boolean.FALSE) {
				Path path = Paths.get(FILE_PATH);// "c:\\Nitesh\\sample.txt"
				FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
				ByteBuffer buffer = ByteBuffer.allocate(buffer_size);
				while (socketChannel.read(buffer) > 0) {
					buffer.flip();
					fileChannel.write(buffer);
					buffer.clear();

				}
				System.out.println("Receving file successfully!");
			} else {
				throw new ReceiverException("ENV variable FILE_PATH Not provided");
			}			
		} catch (IOException ioe) {
			throw new ReceiverException("IOException while Recieving file ", ioe);
		} catch (ReceiverException re) {
			throw re;
		} catch (Exception e) {
			throw new ReceiverException("Unexpected Exception while Recieving file ", e);
		} finally {
			try {
				socketChannel.close();
			} catch (IOException ioe) {
				throw new ReceiverException("IOException while Closing socketChannel ", ioe);
			}
		}

	}

	private SocketChannel createServerSocketChannel() {
		SocketChannel client = null;
		try {
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			log.debug("port {} ",port);
			serverSocket.socket().bind(new InetSocketAddress(port));
			client = serverSocket.accept();
			log.info("connection established .. {}", client.getRemoteAddress());

		} catch (IOException ioe) {
			throw new ReceiverException("IOException while Creating Server Socket channel ", ioe);
		} catch (Exception e) {
			throw new ReceiverException("Unexpected Exception while Creating Server Socket channel ", e);
		}

		return client;
	}
}

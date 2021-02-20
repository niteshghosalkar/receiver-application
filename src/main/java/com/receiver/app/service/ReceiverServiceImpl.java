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

	@Value("#{environment['path_to_save']}")
	private String path_to_save;

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
		// Try to create a new file
		try {
			log.debug("Save file to {}", path_to_save);

			if (StringUtils.isEmpty(path_to_save) == Boolean.FALSE) {
				Path path = Paths.get(path_to_save);// "c:\\Nitesh\\sample.txt"
				FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
				// Allocate a ByteBuffer
				ByteBuffer buffer = ByteBuffer.allocate(buffer_size);
				while (socketChannel.read(buffer) > 0) {
					buffer.flip();
					fileChannel.write(buffer);
					buffer.clear();

				}
			} else {
				throw new ReceiverException("ENV variable path_to_save Not provided");
			}
			System.out.println("Receving file successfully!");
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
				// TODO Auto-generated catch block
				throw new ReceiverException("IOException while Closing socketChannel ", ioe);
			}
		}

	}

	private SocketChannel createServerSocketChannel() {
		SocketChannel client = null;
		try {
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
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

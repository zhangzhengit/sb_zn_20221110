package com.vo.http;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.io.ByteProcessor;
import com.vo.core.ZLog2;
import com.vo.enums.MethodEnum;

import ch.qos.logback.core.pattern.color.RedCompositeConverter;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpStatus;

/**
 * 先写一个http请求工具
 *
 *
 * @author zhangzhen
 * @date 2022年11月11日
 *
 */
@Component
public class ZH {

	private static final ZLog2 LOG = ZLog2.getInstance();

	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private static final String CONTENT_TYPE = "Content-Type";

	/**
	 * post
	 *
	 * @param url
	 * @param request
	 * @param response
	 *
	 */
	public void post(final String url, final HttpServletRequest request, final HttpServletResponse response) {
		this.doRequest(url, MethodEnum.POST, request, response);
	}

	/**
	 * get 请求，queryString直接放在?后面
	 *
	 * @param url
	 * @param request
	 * @param response
	 *
	 */
	public void get(final String url, final HttpServletRequest request, final HttpServletResponse response) {
		this.doRequest(url, MethodEnum.GET, request, response);
	}

	private void doRequest(final String url, final MethodEnum methodEnum, final HttpServletRequest request,
			final HttpServletResponse response) {
		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod(methodEnum.name());

			// 复制参数request的header到connection
			copyHeaderFromRequest(request, connection);

			if (methodEnum == MethodEnum.POST) {

				connection.setDoOutput(true);

				final String contentType2 = request.getContentType();

				// MULTIPART_FORM_DATA
				if (contentType2.contains(MULTIPART_FORM_DATA)) {

					final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

					final Collection<Part> ps = request.getParts();
					for (final Part p : ps) {
						final List<MultipartFile> files = multipartRequest.getFiles(p.getName());
						for (final MultipartFile multipartFile : files) {
							System.out.println("mfn = "  + multipartFile.getOriginalFilename());
							upload0(connection, p.getInputStream(), multipartFile.getOriginalFilename());
						}
					}

					final int responseCode = connection.getResponseCode();
					System.out.println("responseCode = " + responseCode);
				} else {
					IOUtils.copy(request.getInputStream(), connection.getOutputStream());
					connection.getOutputStream().flush();
				}
			}

			// queryString
			final String queryString = request.getQueryString();

			// 连接
			connection.connect();

			// 复制connection的response的header到参数response
			copyResponseHeaders(connection.getHeaderFields(), response);

			// 复制connection的response到参数response
			this.copyResponseToReponse(response, connection);

			connection.disconnect();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static String getRquestContentType(final HttpServletRequest request) {
		final String value = request.getHeader(CONTENT_TYPE);
		return value;
	}

	private static void copyHeaderFromRequest(final HttpServletRequest request, final HttpURLConnection connection) {
		final Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			final String key = headerNames.nextElement();
			final String value = request.getHeader(key);
			connection.setRequestProperty(key, value);
		}
	}

	private void copyResponseToReponse(final HttpServletResponse response, final HttpURLConnection connection) {
		try {
			int responseCode;
			responseCode = connection.getResponseCode();
			switch (responseCode) {
			case HttpStatus.HTTP_OK:
				final InputStream inputStream = connection.getInputStream();
				IOUtils.copy(inputStream, response.getOutputStream());
				response.getOutputStream().flush();
				response.getOutputStream().close();
				break;

			default:
				// 非OK的情况下，同一认为是error了
				response.setStatus(responseCode);
				final InputStream errorStream = connection.getErrorStream();
				if (Objects.nonNull(errorStream)) {
					IOUtils.copy(errorStream, response.getOutputStream());
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
				break;
			}
		} catch (final IOException e) {
			LOG.error("异常,e.message={},e={}", e.getMessage(), e);
		}
	}

	private static void copyResponseHeaders(final Map<String, List<String>> headerMap, final HttpServletResponse response) {
		final Set<Entry<String, List<String>>> entrySet = headerMap.entrySet();
		for (final Entry<String, List<String>> entry : entrySet) {
			final String name = entry.getKey();
			final List<String> vl = entry.getValue();
			for (final String v : vl) {
				response.setHeader(name, v);
			}
		}
	}

	/**
	 * HttpURLConnection上传文件
	 *
	 * @param conn
	 * @param inputStream
	 * @param filename
	 *
	 */
	private static void upload0(final HttpURLConnection conn, final InputStream inputStream, final String filename) {

		// 文件边界
		final String Boundary = UUID.randomUUID().toString();

		try {

			conn.setDoOutput(true);
			conn.setRequestMethod(MethodEnum.POST.name());
			conn.setRequestProperty("Charset", "utf-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + Boundary);

			conn.setChunkedStreamingMode(1 * 1024 * 1024 * 10);


			final DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
			dataOutputStream.writeUTF("--" + Boundary + "\r\n"
					+ "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n"
					+ "Content-Type: application/octet-stream; charset=utf-8" + "\r\n\r\n");
			final InputStream in = inputStream;
			final byte[] b = new byte[1024];
			int l = 0;
			while ((l = in.read(b)) != -1) {
				dataOutputStream.write(b, 0, l); // 写入文件
			}
			dataOutputStream.writeUTF("\r\n--" + Boundary + "--\r\n");
			dataOutputStream.close();
			in.close();

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}

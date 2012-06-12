// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.http;

import jodd.io.http.Http;
import jodd.io.http.HttpParams;
import jodd.io.http.HttpTransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpGet {

	public static void main(String[] args) throws IOException {
		HttpTransfer request = Http.createRequest("GET", "http://jodd.org?id=1");
		request.addHeader("User-Agent", "jodd");

		HttpParams httpParams = request.getQueryParameters();
		httpParams.setParameter("id", "173");
		request.setQueryParameters(httpParams);

		System.out.println(request);

		Socket socket = new Socket(request.getHost(), request.getPort());
		OutputStream out = socket.getOutputStream();
		request.send(out);

		InputStream in = socket.getInputStream();
		HttpTransfer response = Http.readResponse(in);
		System.out.println(response);
		socket.close();
	}
}

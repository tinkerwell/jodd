// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.email;

import jodd.io.FileUtil;
import jodd.mail.SmtpServer;
import jodd.mail.Email;
import jodd.mail.SendMailSession;
import jodd.mail.att.ByteArrayAttachment;

import javax.activation.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static jodd.mail.Email.PRIORITY_HIGHEST;

public class SendMail {

	public static void main(String[] args) throws IOException {
		SmtpServer smtpServer = new SmtpServer("mail.beotel.rs", "weird", "...");
		SendMailSession session = smtpServer.createSession();

		session.open();

		Email email;

		email = Email.create()
				.from("weird@beotel.rs")
				.to("info@jodd.org")
				.subject("test1")
				.addText("a plain text message čtf");
		session.sendMail(email);
		System.out.println("email #1 sent");

		email = Email.create()
				.from("weird@beotel.rs")
				.to("info@jodd.org")
				.subject("test2")
				.addHtml("a <b>test 2</b> message");
		session.sendMail(email);
		System.out.println("email #2 sent");

		email = Email.create()
				.from("weird@beotel.rs")
				.to("info@jodd.org")
				.addText("and text3 message!")
				.subject("test3")
				.addHtml("a <b>test 3</b> message");
		session.sendMail(email);
		System.out.println("email #3 sent");

		email = Email.create()
				.from("weird@beotel.rs")
				.to("info@jodd.org")
				.subject("test4")
				.addText("text 4")
				.attachFile("d:\\huh2.jpg")
				.priority(PRIORITY_HIGHEST);
		session.sendMail(email);
		System.out.println("email #4 sent");


		byte[] bytes = FileUtil.readBytes("d:\\summer.jpg");

		FileInputStream fis = new FileInputStream("d:\\summer.jpg");
		ByteArrayAttachment isa = new ByteArrayAttachment(fis, "image/jpeg", "summer2.jpg", null);

		email = Email.create()
				.from("weird@beotel.rs")
				.to("info@jodd.org")
				.subject("test5")
				.addText("Здраво!")
				.addHtml("<html><META http-equiv=Content-Type content=\"text/html; charset=utf-8\"><body><h1>Здраво!</h1><img src='cid:huh2.jpg'></body></html>")
				.embedFile("d:\\huh2.jpg")
				.attachFile("d:\\cover.jpg")
				.attachBytes(bytes, "image/jpeg", "sum.jpg")
				.attach(isa)
		;
		session.sendMail(email);
		System.out.println("email #5 sent");

		session.close();

		System.out.println("done.");
	}
}

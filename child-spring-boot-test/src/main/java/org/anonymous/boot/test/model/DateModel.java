package org.anonymous.boot.test.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/04
 */
public class DateModel implements Serializable {
	private static final long serialVersionUID = -7092189338225457473L;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "DateModel{" +
				"date=" + date +
				'}';
	}
}

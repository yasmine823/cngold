package com.yasmine.spider.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OuterLink {
	public static final String path = "C:\\Users\\admin\\Desktop\\front\\subUrl1.txt";

	public static String previewPath = "";

	public static void main(String[] args) {
		OuterLink link = new OuterLink();
		link.isOuterLink("javascript:bookmarksite('金投网--实时黄金报价，行情分析，黄金投资资讯', 'http://www.cngold.org')");
	}

	public void checkUrl(String filePath) {
		File input = new File(filePath);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "https://www.cngold.org/");
		} catch (Exception e) {
			// TODO: handle exception
		}
		Elements links = doc.getElementsByTag("a");
		for (Element link : links) {

			String linkHref = link.attr("href").trim();
			if (!"".equals(linkHref)) {
				if (isOuterLink(linkHref)) {

					String rel = link.attr("rel");
					if ("".equals(rel) || !"external nofollow".equals(rel)) {
						// 写入文件
						contentToTxt(filePath, link);
					}
				} else {
					// a、onClick要考虑Click首字母为小写吗？

					String onClickStr = link.attr("onClick");
					// 1、先看下是否有onClick事件,如果有，再看一下是否有window.open(方法，如果没有，再看网址是否为外链
					// b、onclick="window.open( 'http://www.beian.gov.cn/portal
					if (!StringUtil.isBlank(onClickStr) && !onClickStr.contains("window.open('")) {
						if (isOuterLink(onClickStr)) {

							contentToTxt(filePath, link);
						}
					}
				}

			}

		}
	}

	public boolean isOuterLink(String linkHref) {
		// 1、先判断是否有https://或者http://
		// https://passport2.cngold.org/?service=http://www.cngold.org/

		int s = linkHref.indexOf("?");
		if (s > -1) {
			linkHref = linkHref.substring(0, s);
		}

		int http = linkHref.indexOf("http://");
		int https = linkHref.indexOf("https://");

		int pos = http > -1 ? http + "http://".length() : https + "https://".length();

		if (http > -1 || https > -1) {

			// 2、先取出http://之后的字符串

			linkHref = linkHref.substring(pos);
			// ?jijinhao.cn,
			if (!(linkHref.indexOf("cngold.org") > -1 || linkHref.indexOf("cngoldres.org") > -1
					|| linkHref.indexOf("jin99.cn") > -1 || linkHref.indexOf("cngoldres.com") > -1
					|| linkHref.indexOf("cnbaiyin.com") > -1 || linkHref.indexOf("cnbaiyin.com") > -1
					|| linkHref.indexOf("18qh.com") > -1)) {
				return true;
			}
		}

		return false;
	}

	public static void contentToTxt(String filePath, Element link) {
		String content = "\n" + link + "\n";
		if ("".equals(previewPath) || !previewPath.equals(filePath)) {
			previewPath = filePath;
			content = "\n" + previewPath + "\n" + link + "\n";
		}
		String str = new String(); // 原有txt内容
		String s1 = new String();// 内容更新
		try {
			File f = new File(path);
			if (!f.exists()) {
				f.createNewFile();// 不存在则创建
			}
			BufferedReader input = new BufferedReader(new FileReader(f));

			while ((str = input.readLine()) != null) {
				s1 += str + "\n";
			}
			input.close();
			s1 += content;

			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(s1);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}

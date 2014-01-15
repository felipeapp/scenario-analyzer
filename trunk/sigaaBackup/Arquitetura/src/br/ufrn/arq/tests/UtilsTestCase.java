/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/06/2010
 */
package br.ufrn.arq.tests;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Classe utilitária para testes com Selenium. 
 * 
 * @author Mayron Cachina
 * @author Gleydson Lima
 *
 */
public class UtilsTestCase {
	
	protected static Selenium selenium;
	
	public void clickJsCookMenu(PosicaoMenuJsf posicao, String jsCookId, String item, String jsCookIdNext, String next) {
		String path = null;
		
		if (posicao == PosicaoMenuJsf.MENU) {
			//mousedown
			path = "//div[@id='"+jsCookId+"']//td[@class='ThemeOfficeMainItem']/span[text()='"+item+"']";
			selenium.mouseOver(path);
			path = "//div[@id='"+jsCookIdNext+"']//td[@class='ThemeOfficeMenuFolderText' and text()='"+next+"']";
			selenium.waitForCondition("selenium.isVisible(\""+path+"\");", "1000");
		} else if (posicao == PosicaoMenuJsf.SUBMENU){
			//mouseover
			path = "xpath=//div[@id='"+jsCookId+"']//td[@class='ThemeOfficeMenuFolderText' and text()='"+item+"']";
			selenium.mouseOver(path);
			path = "//div[@id='"+jsCookIdNext+"']//td[@class='ThemeOfficeMenuItemText' and text()='"+next+"']";
			selenium.waitForCondition("selenium.isVisible(\""+path+"\");", "1000");
		} else {
			//mouseover
			path = "xpath=//div[@id='"+jsCookId+"']//td[@class='ThemeOfficeMenuItemText' and text()='"+item+"']";
			selenium.mouseOver(path);
			//mousedown
			path = "xpath=//div[@id='"+jsCookId+"']//tr[@class='ThemeOfficeMenuItemHover']/td[@class='ThemeOfficeMenuItemText' and text()='"+item+"']";
			selenium.mouseDown(path);
			path = "xpath=//div[@id='"+jsCookId+"']//tr[@class='ThemeOfficeMenuItemActive']/td[@class='ThemeOfficeMenuItemText' and text()='"+item+"']";
			selenium.mouseUp(path);
		}
		
	}
	
	 enum PosicaoMenuJsf {

			MENU,
			
			SUBMENU,
			
			ITEM
			
	 }
	
	public static void setUp(String server, int port, String browser, String url, String sistema, String login) throws Exception {
		selenium = new DefaultSelenium(server, port, browser, url);
		selenium.start();
		
		if(selenium.isElementPresent("user.login")){
			selenium.open("/" + sistema + "/verTelaLogin.do");
			selenium.type("user.login", login);
			selenium.type("user.senha", login);
			selenium.click("//input[@value='Entrar']");
		}else{	
			selenium.open(url + sistema );
			selenium.type("login", login);
			selenium.type("senha", login);
			selenium.click("logar");
		}
		
		selenium.waitForPageToLoad("200000");
	}


	public static void setUpStruts(String server, int port, String browser, String url, String sistema, String login) throws Exception {
		selenium = new DefaultSelenium(server, port, browser, url);
		selenium.start();
		
		selenium.open("/" + sistema + "/verTelaLogin.do");
		selenium.type("user.login", login);
		selenium.type("user.senha", login);
		selenium.click("//input[@value='Entrar']");
		
		selenium.waitForPageToLoad("200000");
	}

	public static void down() {
		selenium.stop();
		
	}
	
	
	
}

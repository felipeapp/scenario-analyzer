<?xml version="1.0"?>
<%@ page contentType="text/vnd.wap.wml; charset=UTF-8" %>

<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<wml>

	<card title="WAP UFRN">	
			<p align="center">
				<small>
					${mensagem}
				</small>
			</p>
			
			<p>
	        	<small>Usuário</small><br/>
				<input name="login" size="10" type="text" maxlength="25" />
			</p>
			
			<p>
	        	<small>Senha</small><br/>
				<input title="Senha" name="senha" size="10" type="password" maxlength="6" format="NNNNNN"/>
			</p>
			<p>
				<anchor>Entrar
					 <go href="/sigaa/mobile/logonMobile.do?dispatch=logon" method="post">
						  <postfield name="login" value="$login"/>
						  <postfield name="senha" value="$senha"/>
					 </go>
				</anchor>
			</p>
	</card>

</wml>
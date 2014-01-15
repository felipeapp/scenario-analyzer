<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<html>

	<body>	
		
		<form action="/sigaa/mobile/logonMobile.do?dispatch=logon" method="post">
		
			<p align="center">
				<small>
					Atenção: Página de testes para acessar as funcionalidades módulo mobile em um navegador comum. 
					<br/>
					<br/>
					Para acesar a página real de login acesse: ${ configSistema['linkSigaa'] }/sigaa/mobile/login.jsp. 
					<br/>
					<br/>
					Você vai precisar de uma navegado WAP
				</small>
			</p>
			
			<div style="text-align: center; width: 100%">
			<p>
	        	<small>Usuario</small><br/>
				<input name="login" size="10" type="text" maxlength="25" />
			</p>
			
			<p>
	        	<small>Senha</small><br/>
				<input title="Senha" name="senha" size="6" type="password" maxlength="6" format="NNNNNN"/>
			</p>

			<input  type="submit"/>
			</div>
	</form>
	</body>
	

</html>
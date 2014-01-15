<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	
	
	
	<table class="tabelaRelatorioBorda" style="width: 90%; margin-left: auto; margin-right: auto;">
		<caption> Assuntos de Maior Interesse dos Usu�rios </caption>
		
		<thead>
			<tr>
				<th style="width: 70%;"> Assunto </th>
				<th style="width: 30%;"> Quantidade Usu�rios Interessados </th>
			</tr>
		</thead>
		
		<c:forEach var="assuntos" items="${_abstractRelatorioBiblioteca.assuntosDeMaiorInteresseUsuario}">
			<tr>
				<th> ${assuntos[0]} </th>
				<td style="text-align: right;"> ${assuntos[1]} </td>
			</tr>
		</c:forEach>
	</table>
	
	
	
	<table class="tabelaRelatorioBorda" style="width: 90%; margin-top: 50px; margin-left: auto; margin-right: auto;">
		<caption> Autores de Maior Interesse dos Usu�rios </caption>
		
		<thead>
			<tr>
				<th style="width: 70%;"> Autor </th>
				<th style="width: 30%;"> Quantidade Usu�rios Interessados </th>
			</tr>
		</thead>
		
		<c:forEach var="autores" items="${_abstractRelatorioBiblioteca.autoresDeMaiorInteresseUsuario}">
			<tr>
				<th> ${autores[0]} </th>
				<td style="text-align: right;"> ${autores[1]} </td>
			</tr>
		</c:forEach>
	</table>
	
	
	<table class="tabelaRelatorioBorda" style="width: 90%;  margin-top: 50px; margin-left: auto; margin-right: auto;">
		
		<thead>
			<tr>
				<th> Usu�rios com Interesse em Receber o Informativo de Novas Aquisi��es </th>
			</tr>
		</thead>
		
		<tr>
			<td style="text-align: right; font-weight: bold;"> ${_abstractRelatorioBiblioteca.qtdUsuariosRecebendoInformativoNovasAquisicoes} </td>
		</tr>
	
	</table>
	
	
	<table class="tabelaRelatorioBorda" style="width: 90%; margin-top: 50px; margin-left: auto; margin-right: auto;">
		<caption> �reas de Maior Interesse para o Informativo de Novas Aquisi��es </caption>
		
		<thead>
			<tr>
				<th style="width: 70%;"> �rea de Interesse</th>
				<th style="width: 30%;"> Quantidade Usu�rios Interessados </th>
			</tr>
		</thead>
		
		<c:forEach var="grandeAreaInteresse" items="${_abstractRelatorioBiblioteca.grandesAreasDeMaiorInteresseUsuario}">
			<tr>
				<th> ${grandeAreaInteresse[0]} </th>
				<td style="text-align: right;"> ${grandeAreaInteresse[1]} </td>
			</tr>
		</c:forEach>
	</table>
	
	
	
	<div style="margin-top:20px;">
		<hr  />
		<h4>Observa��o</h4>
		<p> S�o mostrados os ${_abstractRelatorioBiblioteca.limiteBuscaTemasMaiorInteresse} Autores e Assuntos de maior interesse. </p> 
	</div>
	
</f:view>	


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
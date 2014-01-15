<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	
	<table class="tabelaRelatorioBorda" style="width: 100%; margin: auto;">
	
		<tr>
			<th>Quantidade de T�tulos</th> <td style="text-align: right;"> ${_abstractRelatorioBiblioteca.qtdTitulos} </td>
		</tr>

		<tr>
			<th>Quantidade de Artigos</th> <td style="text-align: right;">  ${_abstractRelatorioBiblioteca.qtdArtigos }</td>
		</tr>
	
		<tr>
			<th>Quantidade de Autoridades</th> <td style="text-align: right;">  ${_abstractRelatorioBiblioteca.qtdAutoridades} </td>
		</tr>
	
		<tr>
			<th>Quantidade de Exemplares</th> <td style="text-align: right;"> ${_abstractRelatorioBiblioteca.qtdExemplares}  </td>
		</tr>
		
		<tr>
			<th>Quantidade de Fasc�culos</th> <td style="text-align: right;"> ${_abstractRelatorioBiblioteca.qtdFasciculos} </td>
		</tr>
		
		<tr>
			<th>Quantidade de Assinaturas</th> <td style="text-align: right;"> ${_abstractRelatorioBiblioteca.qtdAssinaturas} </td>
		</tr>
		
		<tr>
			<th>Quantidade de Empr�stimos</th> <td style="text-align: right;"> ${_abstractRelatorioBiblioteca.qtdEmprestimos} </td>
		</tr>
	
		<tr>
			<th>Quantidade de Usu�rios</th> <td style="text-align: right;"> ${_abstractRelatorioBiblioteca.qtdUsuarios} </td>
		</tr>
	
	
	</table>
	
</f:view>	


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
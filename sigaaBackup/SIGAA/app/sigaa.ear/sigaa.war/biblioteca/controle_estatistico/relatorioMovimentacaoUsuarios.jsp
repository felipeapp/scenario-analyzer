<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" width="80%" align="center">
		
		<tbody>
			
			<tr>
				<th style="background-color: #DEDFE3;">Novos Usuários no Período</th>
				<th style="background-color: #DEDFE3;text-align: right;">Quantidade</th>
			</tr>
			<c:forEach items="${_abstractRelatorioBiblioteca.novosUsuarios}" var="info">
				<tr>
					<td>${info[0].descricao}</td>
					<td style="text-align: right;">${info[1]}</td>
				</tr>
			</c:forEach> 
			<tr>
				<td style="font-weight: bold;">Total de Novos Usuários</td>
				<td style="font-weight: bold;text-align: right;">${_abstractRelatorioBiblioteca.qtdTotalNovosUsuarios}</td>
			</tr>
			
			
			
			
			<tr style="height: 20px;">
			</tr>
			<tr> 
				<th style="background-color: #DEDFE3;">Usuários Quitados no Período</th>
				<th style="background-color: #DEDFE3;text-align: right;">Quantidade</th>
			</tr>
			<c:forEach items="${_abstractRelatorioBiblioteca.usuariosQuitados}" var="info">
				<tr>
					<td>${info[0].descricao}</td>
					<td style="text-align: right;">${info[1]}</td>
				</tr>
			</c:forEach> 
			<tr>
				<td style="font-weight: bold;">Total de Usuários Quitados</td>
				<td style="font-weight: bold;text-align: right;">${_abstractRelatorioBiblioteca.qtdTotalUsuariosQuitados}</td>
			</tr>
			
			
			
			
			
			<tr style="height: 20px;">
			</tr>
			<tr>
				<th style="background-color: #DEDFE3;">*Usuários Ativos</th>
				<th style="background-color: #DEDFE3;text-align: right;">Quantidade</th>
			</tr>
			<c:forEach items="${_abstractRelatorioBiblioteca.usuariosAtivos}" var="info">
				<tr>
					<td>${info[0].descricao}</td>
					<td style="text-align: right;">${info[1]}</td>
				</tr>
			</c:forEach> 
			<tr>
				<td style="font-weight: bold;">Total de Usuários Ativos</td>
				<td style="font-weight: bold;text-align: right;">${_abstractRelatorioBiblioteca.qtdTotalUsuariosAtivos}</td>
			</tr>
			
			
			
			
			
			
			<tr style="height: 20px;">
			</tr>
			<tr>
				<th style="background-color: #DEDFE3;">*Total Geral de Usuários (Total de ativos + Total de quitados ) </th>
				<th style="background-color: #DEDFE3;text-align: right;">Quantidade</th>
			</tr>
			<c:forEach items="${_abstractRelatorioBiblioteca.usuariosTotais}" var="info">
				<tr>
					<td>${info[0].descricao}</td>
					<td style="text-align: right;">${info[1]}</td>
				</tr>
			</c:forEach> 
			<tr>
				<td style="font-weight: bold;">Total</td>
				<td style="font-weight: bold;text-align: right;">${_abstractRelatorioBiblioteca.qtdTotalGeralUsuarios}</td>
			</tr>
			
		</tbody>
		
	</table>


<div style="margin-top:20px;">
		<hr  />
		<h4>ATENÇÃO</h4>
		<p> &nbsp;&nbsp;&nbsp;&nbsp; <span style="font-size: 14px; font-weight: bold;">*</span> Essa quantidade reflete a quantidade atual do sistema, ou seja, <strong>não</strong> considera o período informado.</p>
		<p> &nbsp;&nbsp;&nbsp;&nbsp;A quantidade de "usuário ativos" inclui a quantidade de novos usuários cadastrados mas <strong>não</strong> inclui a quantidade  de usuários quitados.</p>
		<br/>
		<p> &nbsp;&nbsp;&nbsp;&nbsp;Não é possível separar os usuários por biblioteca, pois o cadastro dos usuários é feito para o sistema como um todo, não existe um cadastro em separado para cada biblioteca.</p>
		<br/>
</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
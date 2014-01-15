<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" width="80%" align="center">
		
		<tbody>
			
			<tr>
				<th style="background-color: #DEDFE3;">Novos Usu�rios no Per�odo</th>
				<th style="background-color: #DEDFE3;text-align: right;">Quantidade</th>
			</tr>
			<c:forEach items="${_abstractRelatorioBiblioteca.novosUsuarios}" var="info">
				<tr>
					<td>${info[0].descricao}</td>
					<td style="text-align: right;">${info[1]}</td>
				</tr>
			</c:forEach> 
			<tr>
				<td style="font-weight: bold;">Total de Novos Usu�rios</td>
				<td style="font-weight: bold;text-align: right;">${_abstractRelatorioBiblioteca.qtdTotalNovosUsuarios}</td>
			</tr>
			
			
			
			
			<tr style="height: 20px;">
			</tr>
			<tr> 
				<th style="background-color: #DEDFE3;">Usu�rios Quitados no Per�odo</th>
				<th style="background-color: #DEDFE3;text-align: right;">Quantidade</th>
			</tr>
			<c:forEach items="${_abstractRelatorioBiblioteca.usuariosQuitados}" var="info">
				<tr>
					<td>${info[0].descricao}</td>
					<td style="text-align: right;">${info[1]}</td>
				</tr>
			</c:forEach> 
			<tr>
				<td style="font-weight: bold;">Total de Usu�rios Quitados</td>
				<td style="font-weight: bold;text-align: right;">${_abstractRelatorioBiblioteca.qtdTotalUsuariosQuitados}</td>
			</tr>
			
			
			
			
			
			<tr style="height: 20px;">
			</tr>
			<tr>
				<th style="background-color: #DEDFE3;">*Usu�rios Ativos</th>
				<th style="background-color: #DEDFE3;text-align: right;">Quantidade</th>
			</tr>
			<c:forEach items="${_abstractRelatorioBiblioteca.usuariosAtivos}" var="info">
				<tr>
					<td>${info[0].descricao}</td>
					<td style="text-align: right;">${info[1]}</td>
				</tr>
			</c:forEach> 
			<tr>
				<td style="font-weight: bold;">Total de Usu�rios Ativos</td>
				<td style="font-weight: bold;text-align: right;">${_abstractRelatorioBiblioteca.qtdTotalUsuariosAtivos}</td>
			</tr>
			
			
			
			
			
			
			<tr style="height: 20px;">
			</tr>
			<tr>
				<th style="background-color: #DEDFE3;">*Total Geral de Usu�rios (Total de ativos + Total de quitados ) </th>
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
		<h4>ATEN��O</h4>
		<p> &nbsp;&nbsp;&nbsp;&nbsp; <span style="font-size: 14px; font-weight: bold;">*</span> Essa quantidade reflete a quantidade atual do sistema, ou seja, <strong>n�o</strong> considera o per�odo informado.</p>
		<p> &nbsp;&nbsp;&nbsp;&nbsp;A quantidade de "usu�rio ativos" inclui a quantidade de novos usu�rios cadastrados mas <strong>n�o</strong> inclui a quantidade  de usu�rios quitados.</p>
		<br/>
		<p> &nbsp;&nbsp;&nbsp;&nbsp;N�o � poss�vel separar os usu�rios por biblioteca, pois o cadastro dos usu�rios � feito para o sistema como um todo, n�o existe um cadastro em separado para cada biblioteca.</p>
		<br/>
</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
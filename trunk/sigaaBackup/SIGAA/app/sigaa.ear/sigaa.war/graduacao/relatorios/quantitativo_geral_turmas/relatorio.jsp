<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
.linhaPar {
	background-color: #F9FBFD;
}
.linhaImpar {
	background-color: #EDF1F8;
}
</style>

<f:view>

	<table class="visualizacao">
		<caption>Legenda</caption>
		
		<tr>
		<td width="50%">
			<table>
				<tr>
					<th>TS</th>
					<td>Total de Solicita��es</td>
				</tr>
				<tr>
					<th>SN</th>
					<td>Solicita��es Negadas</td>
				</tr>
				<tr>
					<th>SNA</th>
					<td>Solicita��es N�o Atendidas</td>
				</tr>
				<tr>
					<th>SAP</th>
					<td>Solicita��es Atendidas Parcialmente</td>
				</tr>
				<tr>
					<th>SA</th>
					<td>Solicita��es Atendidas</td>
				</tr>
			</table>
			</td>
			
			<td width="50%">
			<table>
				<tr>
					<th>M</th>
					<td>Matriculados</td>
				</tr>
				<tr>
					<th>I</th>
					<td>Indeferidos</td>
				</tr>
				<tr>
					<th>VS</th>
					<td>Vagas solicitadas</td>
				</tr>
				<tr>
					<th>VA</th>
					<td>Vagas Atendidas</td>
				</tr>
			</table>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" style="font-size: x-small">* Neste relat�rio n�o s�o exibidos os componentes que tiveram todas as solicita��es de turmas atendidas.</td>
		</tr>
	</table>
	<br/>
	
	<table class="formulario" width="100%">
	<caption>Relat�rio de quantitativo de solicita��es, turmas, matr�culas e vagas por componente curricular em ${relatorioQuantitativoTurmasSolicitacoesBean.ano}.${relatorioQuantitativoTurmasSolicitacoesBean.periodo}</caption>
	<thead>
		<tr>
			<th width="5%" style="text-align: center">C�d</th>
			<th width="55%">Nome</th>
			<th width="5%" style="text-align: center">TS</th>
			<th width="5%" style="text-align: center">SN</th>
			<th width="5%" style="text-align: center">SNA </th>
			<th width="5%" style="text-align: center">SAP</th>
			<th width="5%" style="text-align: center">SA</th>
			<th width="5%" style="text-align: center">M</th>
			<th width="5%" style="text-align: center">I</th>
			<th width="5%" style="text-align: center">VS</th>
			<th width="5%" style="text-align: center">VA</th>
		<tr>
	</thead>
	
	<c:forEach items="${relatorioQuantitativoTurmasSolicitacoesBean.relatorio}" var="linha" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${linha.codigoDisciplina} </td>
			<td> ${linha.nomeDisciplina} </td>
			<td style="text-align: center"> ${linha.totalSolicitacoes} </td>
			<td style="text-align: center"> ${linha.turmasNegadas} </td>
			<td style="text-align: center"> ${linha.turmasNaoAtendidas} </td>
			<td style="text-align: center"> ${linha.turmasAtendidasParcialmente} </td>
			<td style="text-align: center"> ${linha.turmasAtendidas} </td>
			<td style="text-align: center"> ${linha.matriculados} </td>
			<td style="text-align: center"> ${linha.indeferidos} </td>
			<td style="text-align: center"> ${linha.vagasSolicitadas} </td>
			<td style="text-align: center"> ${linha.vagasAtendidas} </td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

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
					<td>Total de Solicitações</td>
				</tr>
				<tr>
					<th>SN</th>
					<td>Solicitações Negadas</td>
				</tr>
				<tr>
					<th>SNA</th>
					<td>Solicitações Não Atendidas</td>
				</tr>
				<tr>
					<th>SAP</th>
					<td>Solicitações Atendidas Parcialmente</td>
				</tr>
				<tr>
					<th>SA</th>
					<td>Solicitações Atendidas</td>
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
			<td colspan="2" style="font-size: x-small">* Neste relatório não são exibidos os componentes que tiveram todas as solicitações de turmas atendidas.</td>
		</tr>
	</table>
	<br/>
	
	<table class="formulario" width="100%">
	<caption>Relatório de quantitativo de solicitações, turmas, matrículas e vagas por componente curricular em ${relatorioQuantitativoTurmasSolicitacoesBean.ano}.${relatorioQuantitativoTurmasSolicitacoesBean.periodo}</caption>
	<thead>
		<tr>
			<th width="5%" style="text-align: center">Cód</th>
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

<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Processo Seletivo:</th>
			<td>${relatoriosVestibular.obj.nome}</td>
		</tr>
	</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda" width="100%">
		<caption>Inscrições com GRU quitadas: (${fn:length(relatoriosVestibular.inscricoes) })</caption>
			<thead>
				<tr>
					<th style="text-align: right;" width="10%">Inscrição</th>
					<th style="text-align: left;">Nome</th>
					<th style="text-align: center;" width="20%">CPF</th>
				</tr>
			</thead>
			<c:forEach var="inscritos" items="#{ relatoriosVestibular.inscricoes }" varStatus="status">
				<tr>
					<td style="text-align: right;"> ${ inscritos.numeroInscricao } </td>
					<td style="text-align: left;"> ${ inscritos.pessoa.nome } </td>
					<td style="text-align: center;"> <ufrn:format type="cpf_cnpj" valor="${ inscritos.pessoa.cpf_cnpj }"/> </td>
				</tr>
			</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	
		
		<c:set var="convocacao" value="#{cadastramentoDiscenteTecnico.convocacoes[0]}" />
		
		<h2>Consulta de Indeferimento</h2>
		
		<table style="width:80%;margin:auto;">
			<tbody>
				<tr><th>Nome:</th><td><strong>${ convocacao.discente.pessoa.nome }</strong></td></tr>
				<tr><th>E-mail:</th><td><strong>${ convocacao.discente.pessoa.email }</strong></td></tr>
				<tr><th>Processo Seletivo:</th><td><strong>${ convocacao.inscricaoProcessoSeletivo.processoSeletivo.nome }</strong></td></tr>
				<tr><th>Polo / Grupo:</th><td><strong>${ convocacao.inscricaoProcessoSeletivo.opcao.descricao }</strong></td></tr>
				<tr><th>Classificação:</th><td><strong>${ convocacao.resultado.classificacaoAprovado }</strong></td></tr>
				<tr><th>Motivo:</th><td><strong>${ convocacao.cancelamento.motivo.descricao }</strong></td></tr>
				<tr><th style="vertical-align:top;">Observações:</th><td><strong>${ convocacao.cancelamento.observacoes }</strong></td></tr>
			</tbody>
		</table>
			
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
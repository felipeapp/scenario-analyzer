<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.head td { padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.linhaAzul th {
		padding: 3px ;
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
		font-variant:small-caps;
		letter-spacing:0;
	}	
</style>

<f:view>

	<h2>Espectro das Resposta do Questionário do Cadastro Único</h2>

	<c:set var="imprimirCampos" value="true" />

	<table class="tabelaRelatorioBorda" width="100%">
		<tr class="head">
			<td>Alternativa</td>
			<td align="right">%</td>
			<td align="right" nowrap="nowrap">Nº Votos</td>
		</tr>
	
		<c:forEach items="${ relQuestionarioCadUnico.espectro }" var="rel">
			<tr class="linhaAzul">
				<th colspan="3">${ rel.pergunta.descricao }</th>
			</tr>
			<c:forEach items="${rel.linhaResposta}" var="linhaResposta">
				<tr>
					<td>&nbsp;&nbsp;&nbsp;${ linhaResposta.alternativa.alternativa }</td>
					<td align="right" width="5%"><fmt:formatNumber maxFractionDigits="2" value="${linhaResposta.porcentegem}" />%</td>
					<td align="right" width="5%"><fmt:formatNumber maxFractionDigits="2" value="${linhaResposta.total}" /></td>
				</tr>
			</c:forEach>			
		</c:forEach>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
<ufrn:subSistema></ufrn:subSistema> &gt;
Situações de Proposta
</h2>

<c:if test="${param.historico == null }">
<c:if test="${ not empty listaproposta }">
	<br>
	<div class="infoAltRem">
		<html:img page="/img/alterar.gif"/> : Alterar Situação da Proposta
	</div>
</c:if>
<ufrn:table collection="${listaproposta}"
	properties="nome, propostaCurso.coordenador.nome, propostaCurso.situacaoProposta.descricao"
	headers="Curso, Coordenador, Situação Atual"
	title="Situação das Propostas de Curso" crud="false" links="src='${ctx}/img/alterar.gif',/sigaa/ensino/latosensu/cadastroHistoricoSituacao.do?dispatch=cadastroStatus&id={id}" />
</c:if>

<c:if test="${param.historico != null }">
<c:if test="${ not empty listaproposta }">
	<br>
	<div class="infoAltRem">
		<html:img page="/img/avancar.gif"/> : Visualizar Histórico de Situações da Proposta
	</div>
</c:if>
<ufrn:table collection="${listaproposta}"
	properties="nome, propostaCurso.situacaoProposta.descricao"
	headers="Curso, Situação Atual"
	title="Situação das Propostas de Curso" crud="false" links="src='${ctx}/img/avancar.gif',/sigaa/ensino/latosensu/cadastroHistoricoSituacao.do?dispatch=verHistorico&id={id}" />
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

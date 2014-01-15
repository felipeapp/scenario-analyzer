<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<jwr:style src="/css/agenda.css" media="all" />
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<f:view>
<p:resources/>
	<c:set value="turmas_selecionadas" var="pagina"></c:set>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<%@ include file="/stricto/matricula/outros_programas/botoes_operacao.jsp"%>
	<%@ include file="/graduacao/matricula/_info_discente.jsp"%>
	<%@include file="/graduacao/matricula/include/turmas_escolhidas.jsp" %>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
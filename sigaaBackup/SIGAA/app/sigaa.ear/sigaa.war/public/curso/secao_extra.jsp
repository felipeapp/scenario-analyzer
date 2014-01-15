<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoCurso.lc}">
<a4j:keepAlive beanName="portalPublicoCurso"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<%-- topo --%>
<%@ include file="include/curso.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
							
<%-- conteudo --%>
<div id="conteudo">
	<c:set var="secaoCurso" value="${portalPublicoCurso.secaoExtraSiteDetalhes}" />
	<div class="titulo">${secaoCurso.titulo}</div>

			<div class="texto">
				${secaoCurso.descricao}
			</div>
		<%--  FIM CONTEÚDO  --%>	
	</div>

</f:view>
<%@ include file="./include/rodape.jsp" %>	
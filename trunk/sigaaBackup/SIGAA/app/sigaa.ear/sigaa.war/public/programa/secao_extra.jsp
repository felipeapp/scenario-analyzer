<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
							
<%-- conteudo --%>
<div id="conteudo">
	<c:set var="secaoPrograma" value="${portalPublicoPrograma.secaoExtraSiteDetalhes}" />
	<div class="titulo">${secaoPrograma.titulo}</div>

			<div class="texto">
				${secaoPrograma.descricao}
			</div>
		<%--  FIM CONTEÚDO  --%>	
	</div>

</f:view>
<%@ include file="./include/rodape.jsp" %>	
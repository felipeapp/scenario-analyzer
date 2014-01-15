
	<%@ include file="./include/cabecalho.jsp" %>
	<f:view locale="#{portalPublicoCentro.lc}">
		<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
		<c:set var="secaoCentro" value="${portalPublicoCentro.secaoExtraSiteDetalhes}" />
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/centro.jsp" %>
		<div id="colDirCorpo">
		<!--  IN�CIO CONTE�DO -->
			<h1>${secaoCentro.titulo}</h1>
			<div class="desc-noticia">
				${secaoCentro.descricao}
			</div>
		<!--  FIM CONTE�DO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	
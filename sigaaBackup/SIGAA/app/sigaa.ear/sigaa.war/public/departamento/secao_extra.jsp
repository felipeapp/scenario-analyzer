	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<c:set var="secaoPrograma" value="${portalPublicoDepartamento.secaoExtraSiteDetalhes}" />
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!--  IN�CIO CONTE�DO -->
			<h1>${secaoPrograma.titulo}</h1>
			<div class="desc-noticia">
				${secaoPrograma.descricao}
			</div>
		<!--  FIM CONTE�DO  -->		
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	
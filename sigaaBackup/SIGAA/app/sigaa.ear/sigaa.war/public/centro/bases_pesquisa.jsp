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
			<h1>Bases de Pesquisa do Centro</h1>
			<br>
			<c:set var="bases" value="${portalPublicoCentro.basesPesquisa}" />
			<c:if test="${not empty bases}">

			<table class="listagem" style="width: 98%;">
					<h:form id="formBasesPesquisaCentro">
						<thead>
							<tr>
							<th>C�digo</th>
							<th>Nome</th>
							<th>Coordenador</th>
							</tr>
						</thead>
						<tbody>	
						<c:forEach var="base" items="${bases}" varStatus="status">
							<c:set var="stripes">${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }</c:set>
				
							<tr class="${stripes} topo">
								<td> ${base.codigo} </td>
								<td> ${base.nome} </td>
								<td valign="top">
								<a title="Clique aqui para acessar a p�gina p�blica do docente." 
								href="${ctx}/public/docente/portal.jsf?siape=${base.coordenador.siape}">
								${base.coordenador.pessoa.nome }
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</h:form>		

				<tfoot >
					<td colspan="3" align="center">
					<b>${fn:length(bases)}</b> Bases de Pesquisa(s) encontrada(s)
					</td>
				</tfoot>
			</table>		
			</c:if>
				
		<!--  FIM CONTE�DO  -->	
		</div>
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>	
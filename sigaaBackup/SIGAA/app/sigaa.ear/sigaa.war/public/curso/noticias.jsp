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
	
	<div class="titulo"><h:outputText value="#{idioma.noticias}"/></div>

		<div class="listagem_tabela">
		  <div id="formulario">
			<table id="table_lt" width="100%">
				<tbody>
				<tr class="campos">
					<td width="10%"><h:outputText value="#{idioma.data}"/></td>
					<td><h:outputText value="#{idioma.titulo}"/></td>
				</tr>		
			
				<c:forEach items="${portalPublicoCurso.allNoticiaSite}" var="noticia" varStatus="status">
					<tr>
						<td><ufrn:format type="dataHora" valor="${noticia.dataCadastro}"></ufrn:format></td>	
						<td>
							<a href="noticias_desc.jsf?noticia=${noticia.id}&lc=${portalPublicoCurso.lc}&id=${portalPublicoCurso.curso.id}">
								${noticia.titulo}
							</a>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		  </div>
		</div>
		
</div>
</f:view>
<%@ include file="./include/rodape.jsp" %>
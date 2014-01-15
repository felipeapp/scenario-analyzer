<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:messages/>
	<h2><ufrn:subSistema /> > Projetos de Ensino</h2>
	<br>

	<h:outputText value="#{solicitarRenovacao.create}"/>


	<div class="infoAltRem">
	    <h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Solicitar Renovação			    
	</div>


	<h:form>
	<table class="listagem">
	<caption>Lista de Projetos de Ensino </caption>

	<c:forEach items="#{solicitarRenovacao.projetos}" var="projeto">
		<tr>
			<td> ${projeto.titulo} </td>
			<td> ${projeto.descricao} </td>
			<h:form>
				<td  width="25">
					<h:commandLink title="Solicitar" action="#{solicitarRenovacao.confirmar}" style="border: 0;">
					   	<f:param name="id" value="#{projeto.id}"/>				    	
						<h:graphicImage url="/img/seta.gif"  />
					</h:commandLink>
					
				</td>
			</h:form>
	</c:forEach>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
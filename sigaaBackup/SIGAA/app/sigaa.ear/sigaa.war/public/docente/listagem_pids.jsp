<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

<%@include file="/public/docente/cabecalho.jsp" %>

<div id="id-docente">
	<h3>${fn:toLowerCase(docente.nome)}</h3>
	<p class="departamento">${docente.unidade.siglaAcademica} - ${docente.unidade.nome}</p>
</div>

<h:form id="formListagemPIDSDocente">

<div class="infoAltRem" style="width:80%">
	<h:graphicImage value="/img/view.gif"style="overflow: visible;"/> : Visualizar Plano Individual do Docente
</div>

<div id="pids-portal-docente">
	<h4>Plano Individual do Docente</h4>

	<c:set var="listaPIDs" value="#{portal.listagemPIDDocente}" />

	<c:if test="${not empty listaPIDs}">
		<table class="listagem">
			<thead>
				<tr>
					<th>Ano/Período</th>
					<th>Situação</th>
					<th></th>
				</tr>
			</thead>

			<tbody>
				<c:forEach var="pid" items="#{listaPIDs}" varStatus="loop">
					<tr>
						<td> ${pid.ano}.${pid.periodo} </td>
						<td> ${pid.descricaoStatus} </td>
						<td style="text-align: center;"> 
							<h:commandLink title="Visualizar PID" 
								action="#{consultaPidMBean.visualizarPortalPublico}" styleClass="noborder">
								<h:graphicImage value="/img/view.gif"></h:graphicImage>
								<f:param name="id" value="#{pid.id}"/>
							</h:commandLink>		
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

	<c:if test="${empty listaPIDs}">
		<p class="vazio">
			Nenhum Plano Individual Docente foi localizado.
		</p>
	</c:if>
</div>

</h:form>

</f:view>
<%@include file="/public/include/rodape.jsp" %>

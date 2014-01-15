<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

<%@include file="/public/docente/cabecalho.jsp" %>

<div id="id-docente">
	<h3>${fn:toLowerCase(docente.nome)}</h3>
	<p class="departamento">${docente.unidade.siglaAcademica} - ${docente.unidade.nome}</p>
</div>

<div id="monitoria-docente">
	<h4>Projetos de Monitoria</h4>

	<c:set var="projetos" value="${portal.projetosMonitoria}" />

	<c:if test="${not empty projetos}">
		<table class="listagem">
			<thead>
				<tr>
					<th>Título</th>
					<th>Centro</th>
					<th></th>
				</tr>
			</thead>

			<tbody>
				<c:set var="ano" value=""/>
				<c:forEach var="projeto" items="${projetos}" varStatus="loop">

				<c:if test="${ano != projeto.ano}">
					<c:set var="ano" value="${projeto.ano}"/>

					<c:if test="${not loop.first}">
						<tr> <td class="spacer" colspan="5"> </td> </tr>
					</c:if>

					<tr><td class="ano" colspan="5"> ${ano}</td></tr>
				</c:if>
				<tr>
					<td>
						${projeto.titulo} <br />
						<i>Coordenador(a): ${projeto.coordenacao.nome}</i>
					</td>
					<td class="area"> ${projeto.unidade.gestora.sigla} </td>
					<td>
						<h:form>
							<input type="hidden" value="${projeto.id}" name="id"/>
							<h:commandButton image="/img/view.gif" alt="Visualizar Projeto de Monitoria" title="Visualizar Projeto de Monitoria" value="Visualizar Projeto" action="#{ projetoMonitoria.view }" immediate="true"/>

							<c:if test="${projeto.idArquivo != null}">
								<h:commandButton image="/img/monitoria/document_attachment.png" title="Visualizar Arquivo Anexo" action="#{projetoMonitoria.viewArquivo}" alt="Ver Arquivo"/>
							</c:if>
						</h:form>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

	<c:if test="${empty projetos}">
		<p class="vazio">
			Nenhum projeto de monitoria cadastrado
		</p>
	</c:if>
</div>


</f:view>
<%@include file="/public/include/rodape.jsp" %>

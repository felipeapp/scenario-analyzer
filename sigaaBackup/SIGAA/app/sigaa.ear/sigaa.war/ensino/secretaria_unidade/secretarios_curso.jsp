<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remo��o desta secretaria?')) return false" scope="request"/>

<f:view>
	<h2> <ufrn:subSistema /> > Secret�rios de Coordena��o de Curso</h2>

	<center>
			<h:messages/>
			<div class="infoAltRem">
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Secretaria<br/>
			</div>
	
	</center>
	
	<h:outputText value="#{secretariaUnidade.create}" />
	<table class="listagem">
		<caption>Secret�rios de Coordena��es de Curso</caption>
		<thead>
			<tr>
				<th> Curso </th>
				<th> Secret�rio(a)</th>
				<th> Ramal </th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="secretario" items="${ secretariaUnidade.secretariosCurso }" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
				<td> ${secretario.curso.descricao} </td>
				<td> ${secretario.usuario.pessoa.nome} <small>(${secretario.usuario.login})</small> </td>
				<td> ${secretario.usuario.ramal} </td>
				<td>
					<h:form>
						<input type="hidden" value="${secretario.id}" name="id"/>
						<h:commandButton image="/img/delete.gif" alt="Remover" action="#{secretariaUnidade.cancelarSecretario}" style="border: 0;" onclick="#{confirmDelete}"/>
					</h:form>
				</td>				
			</tr>
			</c:forEach>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
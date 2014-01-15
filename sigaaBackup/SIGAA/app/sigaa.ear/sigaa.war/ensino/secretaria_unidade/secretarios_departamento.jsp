<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remoção desta secretaria?')) return false" scope="request"/>

<f:view>
	<h2> Secretários de Departamento</h2>

	<center>
			<h:messages/>
			<div class="infoAltRem">
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Secretaria<br/>
			</div>
	
	</center>

	<h:outputText value="#{secretariaUnidade.create}" />
	<table class="listagem">
		<caption>Secretários de Departamento</caption>
		<thead>
			<tr>
				<th> Departamento </th>
				<th> Secretário(a)</th>
				<th> Ramal </th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="secretario" items="${ secretariaUnidade.secretariosDepartamento }" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
				<td> ${secretario.unidade.nome} </td>
				<td> ${secretario.usuario.pessoa.nome} <small>(${secretario.usuario.login})</small></td>
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

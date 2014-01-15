<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remoção desta secretaria?')) return false" scope="request"/>
<style>
	table.listagem tr.programa td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Secretários de Programa de Pós</h2>

	<center>
			<div class="infoAltRem">
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Secretaria<br/>
			</div>
	
	</center>

	<h:outputText value="#{secretariaUnidade.create}" />
	<table class="listagem">
		<caption>Secretários de Programa de Pós</caption>
		<thead>
			<tr>
				<!-- <th> Programa </th> -->
				<th> Secretário(a)</th>
				<th> E-mail </th>
				<th> Ramal </th>
				<th> Tel. Fixo </th>
				<th> Celular</th>
				<th> Inicio</th>
				<th> Fim</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="idFiltro" value="-1" />
			
			<c:forEach var="secretario" items="${ secretariaUnidade.secretariosPrograma}" varStatus="loop">
			
			<c:if test="${secretario.unidade.id != idFiltro}">
				<c:set var="idFiltro" value="${secretario.unidade.id}" />
				<tr class="programa">
					<td colspan="9"> ${secretario.unidade.nome} </td>
				</tr>
			</c:if>
			
			
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
				<%-- <td> ${secretario.unidade.nome} </td> --%>
				<td> ${secretario.usuario.pessoa.nome} <small>(${secretario.usuario.login})</small></td>
				<td> ${secretario.usuario.email}</td>
				<td> ${secretario.usuario.ramal} </td>
				<td> ${secretario.usuario.pessoa.telefone} </td>
				<td> ${secretario.usuario.pessoa.celular} </td>
				<td> <ufrn:format type="data" valor="${secretario.inicio}"/> </td>
				<td> <ufrn:format type="data" valor="${secretario.fim}" /> </td>
				<td>
					<h:form>
						<input type="hidden" value="${secretario.id}" name="id"/>
						<h:commandButton image="/img/delete.gif" alt="Remover" action="#{secretariaUnidade.cancelarSecretario}" style="border: 0;" onclick="#{confirmDelete}" id="btnRemover"/>
					</h:form>
				</td>				
			</tr>
			</c:forEach>
			
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

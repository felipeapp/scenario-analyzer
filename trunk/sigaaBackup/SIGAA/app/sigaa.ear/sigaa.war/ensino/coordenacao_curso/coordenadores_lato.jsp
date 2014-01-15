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
<h:form>
	<c:if test="${ coordenacaoCurso.opcaoLogarComo }">
		<center>
			<div class="infoAltRem">
			        <h:graphicImage value="/img/user.png" style="overflow: visible; width: 18px;"/>: Logar Como 
			</div>
		</center>
	</c:if>
	
	<h2> <ufrn:subSistema /> &gt; Coordenadores de Curso de Especialização</h2>
	<h:outputText value="#{coordenacaoCurso.create}" />
	<table class="listagem">
		<caption>Lista de Coordenadores de Curso de Especialização</caption>
		<thead>
			<tr>
				<th> Servidor(a) </th>
				<th> Função</th>
				<th> Login</th>
				<th> E-mail</th>
				<th> Telefone Fixo</th>
				<th> Telefone Celular</th>				
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="idFiltro" value="-1" />
			<c:forEach var="coord" items="#{ coordenacaoCurso.coordenadoresLato}" varStatus="loop">
			
			<c:set var="idLoop" value="${coord.curso.id}" />
			<c:if test="${coord.curso.id != idFiltro}">
				<c:set var="idFiltro" value="${coord.curso.id}" />
				<tr class="programa">
					<td colspan="7"> ${coord.curso.descricao} </td>
				</tr>
			</c:if>
						
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
				<td> ${coord.servidor.nome}</td>
				<td> ${coord.cargoAcademico.descricao} </td>
				<td> ${coord.servidor.primeiroUsuario.login}</td>
				<td> ${coord.emailContato}</td>
				<td> ${coord.servidor.pessoa.telefone}</td>
				<td> ${coord.servidor.pessoa.celular}</td>
				<c:if test="${ coordenacaoCurso.opcaoLogarComo }">
					<td width="20">
						<h:commandLink action="#{ coordenacaoCurso.logarComo }" >
							<h:graphicImage value="/img/user.png" style="overflow: visible;" title="Logar Como"/>
							<f:param name="login" value="#{ coord.servidor.primeiroUsuario.login }"/>
						</h:commandLink>
					</td>
				</c:if>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

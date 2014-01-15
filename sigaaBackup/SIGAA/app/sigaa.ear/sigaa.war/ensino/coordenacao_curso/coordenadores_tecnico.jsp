<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remoção desta secretaria?')) return false" scope="request"/>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Coordenadores de Cursos Técnicos</h2>
	<h:outputText value="#{coordenacaoCurso.create}" />
	<table class="listagem">
		<caption>Coordenadores de Cursos Técnicos</caption>
		<thead>
			<tr>
				<th> Curso </th>
				<th> Servidor(a) </th>
				<th> Função</th>
				<th> Login</th>
				<th> E-mail</th>
				<th><center> Telefone Fixo</center></th>
				<th><center> Telefone Celular</center></th>				
			</tr>
		</thead>
		<tbody>
			<c:forEach var="coord" items="${ coordenacaoCurso.coordenadoresTecnico}" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
				<td> ${coord.curso.descricao}</td>
				<td> ${coord.servidor.nome}</td>
				<td> ${coord.cargoAcademico.descricao} </td>
				<td> ${coord.servidor.primeiroUsuario.login}</td>
				<td> ${coord.emailContato}</td>
				<td align="center"> ${coord.servidor.pessoa.telefone}</td>
				<td align="center"> ${coord.servidor.pessoa.celular}</td>
				
			</tr>
			</c:forEach>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remoção desta coordenação?')) return false" scope="request"/>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Coordenadores de Curso</h2>
	<h:outputText value="#{coordenacaoCurso.create}" />
	<div class="infoAltRem">
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:Cancelar Coordenação<br />
	</div>
	<table class="listagem">
		<caption>Lista de Coordenadores de Cursos (${ fn:length(coordenacaoCurso.coordenadoresCursos) })</caption>
		<thead>
			<tr>
				<th> Curso </th>
				<th> Nome </th>
				<th> Função(a)</th>
				<th> Telefone(s) </th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="coordenador" items="${coordenacaoCurso.coordenadoresCursos}" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
					<td> ${coordenador.curso.descricao} </td>
					<td> ${coordenador.servidor.nome}</td>
					<td> ${coordenador.cargoAcademico.descricao}</td>
					<td> ${coordenador.telefoneContato1} ${coordenador.ramalTelefone1}<br>${coordenador.telefoneContato2} ${coordenador.ramalTelefone2} 
					</td>
					<td>
						<h:form>
							<input type="hidden" value="${coordenador.id}" name="id"/>
							<h:commandButton  title="Cancelar Coordenação" image="/img/delete.gif" alt="Remover" action="#{coordenacaoCurso.cancelarCoordenador}" style="border: 0;" onclick="#{confirmDelete}"/>
						</h:form>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

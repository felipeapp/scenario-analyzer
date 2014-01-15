<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2>Lista de Turmas Oferecidas ao Curso</h2>
	
	<a4j:keepAlive beanName="buscaTurmaBean"></a4j:keepAlive>
	
	<c:if test="${empty resultadosBusca}">
		<c:set value="${calendarioAcademico.ano}" var="buscaTurmaBean.obj.ano"></c:set>
		<c:set value="${calendarioAcademico.periodo}" var="buscaTurmaBean.obj.periodo"></c:set>
	</c:if>
	<h:form id="busca">
		<table class="formulario" width="50%">
			<caption>Dados da Busca</caption>
			<tbody>
				<tr>
					<td><label>Curso</label></td>
					<td>
						<h:selectOneMenu id="cursos" style="width: 400px"
							value="#{buscaTurmaBean.curso.id}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><label>Ano-Período</label></td>
					<td>
						<h:inputText value="#{buscaTurmaBean.obj.ano}" size="5" id="ano" />
						- <h:inputText value="#{buscaTurmaBean.obj.periodo}" size="2" id="periodo" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{buscaTurmaBean.buscarTurmasOferecidas}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{buscaTurmaBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty resultadoBusca}">
		<br>

		<table class=listagem>
			<caption class="listagem">Lista de Turmas Encontradas</caption>
			<thead>
				<tr>
					<td>Cód. Disciplina</td>
					<td>Nome Disciplina</td>
					<td>Cód. Turma</td>
					<td>Status</td>
				</tr>
			</thead>
			<c:forEach items="${resultadoBusca}" var="item">
				<tr>
					<td>${item.disciplina.codigo}</td>
					<td>${item.disciplina.detalhes.nome}</td>
					<td>${item.codigo}</td>
					<td>${item.situacaoTurma.descricao}</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

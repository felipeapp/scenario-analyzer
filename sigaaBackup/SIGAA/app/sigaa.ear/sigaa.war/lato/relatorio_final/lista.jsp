<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema /> &gt; Consulta de Relatórios Finais</h2>
	<h:outputText value="#{relatorioFinalLato.create}" />
	<h:form id="busca">
		<table class="formulario" width="50%">
			<caption>Busca por Relatórios</caption>
			<tbody>
				<tr>
					<td align="left">
						<input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder">
					 	<label for="checkNome">Curso:</label></td>
					<td>
						<h:inputText value="#{relatorioFinalLato.obj.curso.nome}" size="60" id="param1"
						onfocus="marcaCheckBox('checkNome')" onkeyup="CAPS(this)" />
					</td>
				</tr>
				<tr>
					<td align="left">
						<input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder">
						<label for="checkTodos">Todos</label>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="buscar" value="Buscar" action="#{relatorioFinalLato.buscar}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty relatorioFinalLato.resultadosBusca}">
		<br>
		<center>
		<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Detalhar Relatório
		</div>
		</center>
		<table class=listagem>
			<caption class="listagem">Lista de Relatórios Encontrados</caption>
			<thead>
				<tr>
					<td>Curso</td>
					<td>Coordenador</td>
					<td>Situação</td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="${relatorioFinalLato.resultadosBusca}" var="item">
				<tr>
					<td>${item.curso.descricao} ${item.curso.periodoCurso}</td>
					<td>${item.curso.coordenacao.servidor.pessoa.nome}</td>
					<td>${item.statusString}</td>
					<h:form>
					<td>
						<input type="hidden" value="${item.id}" name="id"/>
						<h:commandButton id="detallhes" image="/img/view.gif" value="Detalhes" action="#{relatorioFinalLato.detalhar}" style="border: 0;"/>
					</td>
					</h:form>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

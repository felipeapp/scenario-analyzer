<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="relatorioPorCurso"/>
	<c:set var="relatorio_" value="${relatorioPorCurso.relatorio}" />
	<h:outputText value="#{relatorioPorCurso.create}"/>
 	<h2><ufrn:subSistema /> &gt; ${relatorio_.titulo}</h2>

	<h:messages showDetail="true" showSummary="true"/>

	<c:if test="${not empty relatorio_.descricao }">
		<div class="descricaoOperacao" style="width: 75%;">
			<p> ${relatorio_.descricao} </p>
		</div>
	</c:if>

	<h:form id="form">
		<table align="center" class="formulario">
			<caption>Dados do Relatório</caption>

			<c:if test="${relatorio_.exibeAnoPeriodo}">
			<tr>
				<th width="30%"> ${relatorio_.legendaAnoPeriodo}: </th>
				<td>
					<h:inputText value="#{relatorioPorCurso.relatorio.ano}" id="ano" size="4" maxlength="4" converter="#{ intConverter }" /> .
					<h:inputText value="#{relatorioPorCurso.relatorio.periodo}" id="periodo" size="1" maxlength="1" converter="#{ intConverter }" />
				</td>
			</tr>
			</c:if>
			
			<c:if test="${relatorio_.quantitativo}">
			<tr>
				<th width="30%"> Abrangência da Consulta: </th>
				<td>
					<h:selectOneMenu value="#{relatorioPorCurso.relatorio.filtroCurso}" id="filtroCurso" style="width: 50%;" onchange="verificarTipoCurso();">
						<f:selectItems value="#{relatorioPorCurso.allFiltrosCursoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>				
			</c:if>
			
			<tr id="linhaCurso">
				<th class="${!relatorioPorCurso.relatorio.permitirTodosOsCursos?'required':''}">${relatorio_.legendaCurso}:</th>
				<td>
					<h:selectOneMenu value="#{relatorioPorCurso.relatorio.curso.id}" id="curso" style="width: 600px">
						<c:if test="${relatorio_.permitirTodosOsCursos}">
							<f:selectItem itemValue="0" itemLabel="TODOS" />
						</c:if>
						<c:if test="${not relatorio_.permitirTodosOsCursos}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CURSO --" />
						</c:if>
						<f:selectItems value="#{relatorioPorCurso.cursosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Gerar Relatório" id="gerarRelatorio" action="#{relatorioPorCurso.gerarRelatorio}" />
					<h:commandButton value="Cancelar" id="cancelar"	action="#{relatorioPorCurso.cancelar}" onclick="#{confirm}" />
				</td>
			</tr>
		</table>
	</h:form>
</f:view>

<c:if test="${relatorio_.quantitativo}">
<script>
	var verificarTipoCurso = function() {
		var filtro = $('form:filtroCurso');
		
		if (filtro) {
			valor = filtro.value;
			if (valor == 1) {
				$('linhaCurso').show();
			} else {
				$('linhaCurso').hide();
			}
		}
	}
	verificarTipoCurso();
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

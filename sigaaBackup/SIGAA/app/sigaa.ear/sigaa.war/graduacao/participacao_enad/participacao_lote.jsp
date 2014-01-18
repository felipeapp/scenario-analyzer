<%@page import="br.ufrn.academico.dominio.StatusDiscente"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
function marcarTodos(chk) {
	var re= new RegExp('selecionaDiscente', 'g')
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].id.match(re)) {
			elements[i].checked = chk.checked;
		}
	}
}
</script>
<f:view>
	<h:form id="form">
	<a4j:keepAlive beanName="participacaoDiscenteEnade"></a4j:keepAlive>
	<h2> <ufrn:subSistema /> > Cadastro de Participação no ENADE em Lote</h2>

	<table class="formulario" width="90%">
		<caption> Participação no ENADE </caption>
		<tr>
			<th class="rotulo" width="30%">Tipo do ENADE:</th>
			<td>
				<h:outputText value="#{participacaoDiscenteEnade.tipoEnade}" id="tipoEnade"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo"> Curso: </th>
			<td>
				<h:outputText value="#{participacaoDiscenteEnade.curso.descricao}" />
			</td>
		</tr>
		<c:if test="${ participacaoDiscenteEnade.tipoEnadeIngressante }">
			<tr>
				<th class="rotulo">Ano de Ingresso:</th>
				<td>
					<h:outputText value="#{participacaoDiscenteEnade.ano}" />
				</td>
			</tr>
			<tr>
				<th> Participação no ENADE Ingressante: </th>
				<td>
					<h:selectOneMenu value="#{participacaoDiscenteEnade.participacaoEnadeIngressante.id}" id="participacaoIngressante">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{participacaoDiscenteEnade.tiposParticipacaoIngressante}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th> Data da prova no ENADE Ingressante: </th>
				<td>
					<t:inputCalendar id="dataProvaIngressante" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{participacaoDiscenteEnade.readOnly}"
						disabled="#{participacaoDiscenteEnade.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{participacaoDiscenteEnade.obj.dataProvaEnadeIngressante}" >
						 <f:converter converterId="convertData"/>
					 </t:inputCalendar>
				</td>
			</tr>
		</c:if>
		<c:if test="${ not participacaoDiscenteEnade.tipoEnadeIngressante }">
			<tr>
				<th class="rotulo">Status do Discente:</th>
				<td>
					<h:outputText value="#{participacaoDiscenteEnade.obj.statusString}" rendered="#{participacaoDiscenteEnade.obj.status > 0}" />
					<h:outputText value="ATIVO, FORMANDO ou GRADUANDO" rendered="#{participacaoDiscenteEnade.obj.status == 0}" />
				</td>
			</tr>
			<c:if test="${!participacaoDiscenteEnade.obj.graduando}">
				<tr>
					<th class="rotulo">Percentual concluído:</th>
					<td>
						<h:outputText value="#{participacaoDiscenteEnade.percentualConcluido}" />
					</td>
				</tr>
			</c:if>
			<tr>
				<th> Participação no ENADE Concluinte: </th>
				<td>
					<h:selectOneMenu value="#{participacaoDiscenteEnade.participacaoEnadeConcluinte.id}" id="participacaoConcluinte">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{participacaoDiscenteEnade.tiposParticipacaoConcluinte}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th> Data da prova no ENADE Concluinte: </th>
				<td>
					<t:inputCalendar id="dataProvaConcluinte" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{participacaoDiscenteEnade.readOnly}"
						disabled="#{participacaoDiscenteEnade.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{participacaoDiscenteEnade.obj.dataProvaEnadeConcluinte}" >
						 <f:converter converterId="convertData"/>
					 </t:inputCalendar>
				</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario">Discentes</td>
		</tr>
		<tr>
			<td colspan="2">
				<table class="listagem">
					<thead>
						<tr>
							<th><input type="checkbox" id="selectAllDiscentes" title="Marcar/Desmarcar Todos" checked="checked"
										onclick="marcarTodos(this)"/></th>
							<th style="text-align: right;">Matrícula</th>
							<th>Nome</th>
							<th>Participação no ENADE</th>
							<th>Data da Prova</th>
						</tr>
					</thead>
					<c:forEach items="#{participacaoDiscenteEnade.resultadosBusca}" var="item" varStatus="status">
											<c:set var="cursoAtual" value="${item.curso.descricao}-${item.matrizCurricular.grauAcademico.descricao}-${item.curso.municipio}-${item.matrizCurricular.turno.sigla}" />
						<c:if test="${cursoAnterior != cursoAtual}">
							<c:set var="cursoAnterior" value="${item.curso.descricao}-${item.matrizCurricular.grauAcademico.descricao}-${item.curso.municipio}-${item.matrizCurricular.turno.sigla}" />
							<c:set var="cursoCount" value="${ cursoCount + 1 }" />
							<tr>
								<td colspan="5" class="subFormulario">
									<h:outputText value="#{ item.curso.descricao }"/> - 
									<h:outputText value="#{ item.matrizCurricular.grauAcademico.descricao }"/> 
									/ <h:outputText value="#{ item.matrizCurricular.turno.sigla} "/>
								</td>
							</tr>
						</c:if>
					
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td><h:selectBooleanCheckbox value="#{item.discente.matricular}" id="selecionaDiscente"/></td>
							<td style="text-align: right;">${item.discente.matricula }</td>
							<td>${item.discente.pessoa.nome }</td>
							<c:if test="${ participacaoDiscenteEnade.tipoEnadeIngressante }">
								<td>${item.participacaoEnadeIngressante.descricao }</td>
								<td><h:outputText value="#{item.dataProvaEnadeIngressante }"/></td>
							</c:if>
							<c:if test="${ not participacaoDiscenteEnade.tipoEnadeIngressante }">
								<td>${item.participacaoEnadeConcluinte.descricao }</td>
								<td><h:outputText value="#{item.dataProvaEnadeConcluinte }"/></td>
							</c:if>
						</tr>						
					</c:forEach>
				</table>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" action="#{participacaoDiscenteEnade.cadastrarLote}" id="btnCadastrarIngressante"
						onclick="if ($('form:participacaoIngressante').value == 0) 
							return confirm('Você não escolheu uma participação ENADE.\nOs discentes que possuem participação ENADE cadastrada terão esta participação excluída!\nDeseja Continuar?');"
						rendered="#{ participacaoDiscenteEnade.tipoEnadeIngressante }"
						/>
					<h:commandButton value="Confirmar" action="#{participacaoDiscenteEnade.cadastrarLote}" id="btnCadastrarConcluinte"
						onclick="if ($('form:participacaoConcluinte').value == 0) 
							return confirm('Você não escolheu uma participação ENADE.\nOs discentes que possuem participação ENADE cadastrada terão esta participação excluída!\nDeseja Continuar?');"
						rendered="#{ !participacaoDiscenteEnade.tipoEnadeIngressante }"
						/>
					<h:commandButton value="<< Voltar" action="#{participacaoDiscenteEnade.formBusca}" id="btnVoltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{participacaoDiscenteEnade.cancelar}" id="btnCancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
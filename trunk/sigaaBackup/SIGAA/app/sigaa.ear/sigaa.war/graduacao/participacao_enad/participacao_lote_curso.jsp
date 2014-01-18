<%@page import="br.ufrn.academico.dominio.StatusDiscente"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
function marcarTodos(chkCurso, idCurso) {
	var re= new RegExp(idCurso, 'g')
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].id.match(re)) {
			elements[i].checked = chkCurso.checked;
		}
	}
}
function habilitarDetalhes(id) {
	var linha = 'linha_'+ id + '_td';
	var icone = 'icone_'+ id + '_img';
	if ( $(linha).style.display != 'table-cell' ) {
		$(linha).style.display = 'table-cell';
		$(icone).src= '/sigaa/img/cima.gif';
		$(icone).title= 'Ocultar Discentes';
	} else {
		$(linha).style.display = 'none';
		$(icone).src= '/sigaa/img/baixo.gif';
		$(icone).title= 'Visualizar Discentes';
	}
}
</script>
<f:view>
	<h:form id="form">
	<a4j:keepAlive beanName="participacaoDiscenteEnade"></a4j:keepAlive>
	<h2> <ufrn:subSistema /> > Cadastro de Participação no ENADE em Lote de Curso</h2>

	<table class="formulario" width="90%">
		<caption> Participação no ENADE </caption>
		<tr>
			<th class="rotulo" width="30%">Tipo do ENADE:</th>
			<td>
				<h:outputText value="#{participacaoDiscenteEnade.tipoEnade}" id="tipoEnade"/>
			</td>
		</tr>
		<c:if test="${participacaoDiscenteEnade.tipoEnadeIngressante}">
			<tr>
				<th class="rotulo">Ano de Ingresso:</th>
				<td>
					<h:outputText value="#{participacaoDiscenteEnade.ano}" />
				</td>
			</tr>
			<c:if test="${participacaoDiscenteEnade.periodo > 0}">
			<tr>
				<th class="rotulo">Período de Ingresso:</th>
				<td>
					<h:outputText value="#{participacaoDiscenteEnade.periodo}" />
				</td>
			</tr>
			</c:if>			
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
				<th class="${participacaoDiscenteEnade.calendarioEnade.dataProva != null ? 'rotulo' : ''}"> Data da prova no ENADE Ingressante: </th>
				<td>
					<t:inputCalendar id="dataProvaIngressante" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{participacaoDiscenteEnade.readOnly}"
						disabled="#{participacaoDiscenteEnade.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{participacaoDiscenteEnade.obj.dataProvaEnadeIngressante}" 
						rendered="#{empty participacaoDiscenteEnade.calendarioEnade.dataProva}">
						 <f:converter converterId="convertData"/>
					 </t:inputCalendar>
					<h:outputText
						value="#{participacaoDiscenteEnade.calendarioEnade.dataProva}" 
						rendered="#{not empty participacaoDiscenteEnade.calendarioEnade.dataProva}"/>
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
			<c:if test="${participacaoDiscenteEnade.obj.graduando}">
				<tr>
					<th class="rotulo">Percentual Mínimo Concluído:</th>
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
			<td colspan="2" class="subFormulario">
				<input type="checkbox" id="selectAllCurso"
						onclick="marcarTodos(this, 'selectAllDiscentes_'); marcarTodos(this, 'selecaoCurso_');"/>
				CURSOS
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div class="infoAltRem">
					<h:graphicImage value="/img/baixo.gif" style="overflow: visible;" />: Visualizar Discentes
					<h:graphicImage value="/img/cima.gif" style="overflow: visible;" />: Ocultar Discentes
				</div>
				<table class="formulario" width="100%">
					<c:set var="cursoAnterior" value="" />
					<c:set var="cursoCount" value="0" />
					<c:forEach items="#{participacaoDiscenteEnade.resultadosBusca}" var="item" varStatus="status">
						<c:set var="cursoAtual" value="${item.curso.descricao}-${item.matrizCurricular.grauAcademico.descricao}-${item.curso.municipio}-${item.matrizCurricular.turno.sigla}" />
						<c:if test="${cursoAnterior != cursoAtual}">
							<c:set var="cursoAnterior" value="${item.curso.descricao}-${item.matrizCurricular.grauAcademico.descricao}-${item.curso.municipio}-${item.matrizCurricular.turno.sigla}" />
							<c:set var="cursoCount" value="${ cursoCount + 1 }" />
							<c:if test="${cursoCount > 1}">
										</table>
									</td>
								</tr>
							</c:if>
							<tr class="${cursoCount % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td width="10">
									<input type="checkbox" id="selectAllDiscentes_${cursoCount}_cb"
										onclick="marcarTodos(this, 'selecaoCurso_${cursoCount}_cb')"/>
								</td>
								<td>
									<h:outputText value="#{ item.curso.descricao }"/> - 
									<h:outputText value="#{ item.matrizCurricular.grauAcademico.descricao }"/> 
									/ <h:outputText value="#{ item.matrizCurricular.turno.sigla} "/>
								</td>
								<td width="10">
									<a href="javascript: void(0);" onclick="habilitarDetalhes(${cursoCount});" title="Visualizar Discentes">
										<img src="${ctx}/img/baixo.gif" id="icone_${cursoCount}_img"/>
									</a>	
								</td>
							</tr>
							<tr>
								<td colspan="3" style="display: none;" id="linha_${cursoCount}_td">
									<table class="formulario" width="100%">
										<thead>
											<tr>
												<th></th>
												<th style="text-align: right;">Matrícula</th>
												<th>Nome</th>
												<th>Participação no ENADE</th>
												<th>Data da Prova</th>
											</tr>
										</thead>
						</c:if>
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>
								<h:selectBooleanCheckbox value="#{item.discente.matricular}" 
									id="selecaoCurso_${cursoCount}_cb" 
									label="selecaoCurso_#{cursoCount}_cb"/>
							</td>
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
					<h:commandButton value="<< Voltar" action="#{participacaoDiscenteEnade.iniciarLoteCurso}" id="btnVoltarLote"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{participacaoDiscenteEnade.cancelar}" id="btnCancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
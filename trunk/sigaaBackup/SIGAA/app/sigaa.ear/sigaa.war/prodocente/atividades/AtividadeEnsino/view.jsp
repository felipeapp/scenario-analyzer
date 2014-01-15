<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Atividade de Ensino - Strictus Sensu</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Atividade de Ensino</caption>
			<h:inputHidden value="#{atividadeEnsino.confirmButton}" />
			<h:inputHidden value="#{atividadeEnsino.obj.id}" />

			<tr>
				<th>Docente:</th>
				<td><h:selectOneMenu value="#{atividadeEnsino.obj.servidor.id}"
					disabled="#{atividadeEnsino.readOnly}"
					disabledClass="#{atividadeEnsino.disableClass}" id="servidor">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{atividadeEnsino.docentesUnidade}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<td colspan="2">
				<table>
					<tr>
						<th class="required">Ano:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.ano}" size="5"
							maxlength="255" readonly="#{atividadeEnsino.readOnly}" id="ano" /></td>
						<th class="required">Semestre:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.semestre}"
							size="2" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="semestre" /></td>
					</tr>
					<tr>
						<th class="required">Tipo:</th>
						<td colspan="3"><h:inputText
							value="#{atividadeEnsino.obj.tipo}" size="60" maxlength="255"
							readonly="#{atividadeEnsino.readOnly}" id="tipo" /></td>
					</tr>
					<tr>
						<th class="required">Remunerada:</th>
						<td colspan="3"><h:selectBooleanCheckbox
							value="#{atividadeEnsino.obj.remunerada}" id="remunerada"
							disabled="#{atividadeEnsino.readOnly}" /></td>
					</tr>

					<tr>
					<tr>
						<th class="required">Período Início:</th>
						<td><t:inputCalendar
							value="#{atividadeEnsino.obj.periodoInicio}" size="10"
							maxlength="10" readonly="#{atividadeEnsino.readOnly}" id="periodoInicio"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
						<th>Período Fim:</th>
						<td><t:inputCalendar
							value="#{atividadeEnsino.obj.periodoFim}" size="10"
							maxlength="10" readonly="#{atividadeEnsino.readOnly}"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption class="subFormulario">Informações da Turma</caption>
					<tr>
						<th class="required">Código da Turma:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.codigoTurma}"
							size="8" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="codigoTurma" /></td>
						<th class="required">Código da Disciplina:</th>
						<td><h:inputText
							value="#{atividadeEnsino.obj.codigoDisciplina}" size="8"
							maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="codigoDisciplina" /></td>
					</tr>
					<tr>
						<th class="required">Nome da Disciplina:</th>
						<td colspan="3"><h:inputText
							value="#{atividadeEnsino.obj.disciplina}" size="60"
							maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="disciplina" /></td>
					</tr>
					<tr>
						<th class="required">Departamento:</th>
						<td colspan="3"><h:selectOneMenu style="width: 400px"
							value="#{atividadeEnsino.obj.departamento.id}"
							disabled="#{atividadeEnsino.readOnly}"
							disabledClass="#{atividadeEnsino.disableClass}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{unidade.allDepartamentoCombo}" />
						</h:selectOneMenu></td>
					</tr>
					<tr>
						<th>Informações:</th>
						<td colspan="3"><h:inputText value="#{atividadeEnsino.obj.informacoes}"
							size="60" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="informacoes" /></td>
					</tr>
				</table>
				</td>
			</tr>


			<%-- Distribuição da Carga horária --%>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption class="subFormulario">Distribuição de Carga Horária</caption>
					<tr>
						<th class="required">Carga Horária da Disciplina:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.cargaHoraria}"
							size="5" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="cargaHoraria" /></td>
						<th class="required">Carga Horária do Docente:</th>
						<td><h:inputText
							value="#{atividadeEnsino.obj.cargaHorariaDocente}" size="4"
							maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="cargaHorariaDocente" /></td>
					</tr>
					<tr>
						<th class="required">Carga Horária Teórica:</th>
						<td><h:inputText
							value="#{atividadeEnsino.obj.cargaHorariaTeorica}" size="4"
							maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="cargaHorariaTeorica" /></td>
						<th class="required">Carga Horária Prática:</th>
						<td><h:inputText
							value="#{atividadeEnsino.obj.cargaHorariaPratica}" size="4"
							maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="cargaHorariaPratica" /></td>
					</tr>
					<tr>
						<th class="required">Número de Alunos:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.numAlunos}"
							size="4" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="numAlunos" /></td>
						<th class="required">Número de Docentes:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.numDocentes}"
							size="4" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="numDocentes" /></td>
					</tr>
					<tr>
						<th>Número de Aprovados:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.aprovados}"
							size="4" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="aprovados" /></td>
						<th>Número de Reprovados por Média:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.repMedia}"
							size="4" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="repMedia" /></td>
					</tr>
					<tr>
						<th>Número de Reprovados por Frequência:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.repFreq}"
							size="4" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="repFreq" /></td>
						<th>Créditos:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.creditos}"
							size="4" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="creditos" /></td>
					</tr>

					<tr>
						<th class="required">Programa:</th>
						<td colspan="3"><h:selectOneMenu style="width: 300px"
							value="#{atividadeEnsino.obj.programa.id}"
							disabled="#{banca.readOnly}" id="programa"
							disabledClass="#{atividadeEnsino.disableClass}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{unidade.allDepartamentoCombo}" />
						</h:selectOneMenu></td>
					</tr>

					<tr>
						<th class="required">Pago:</th>
						<td><h:selectBooleanCheckbox
							value="#{atividadeEnsino.obj.pago}"
							disabled="#{atividadeEnsino.readOnly}"
							id="pago" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{atividadeEnsino.confirmButton}"
						action="#{atividadeEnsino.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{atividadeEnsino.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

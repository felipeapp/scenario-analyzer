<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

	<h2>Atividade de Ensino -
		<c:if test="${atividadeEnsino.lato || atividadeEnsino.residencia }">
			Lato Sensu
		</c:if>
		<c:if test="${!atividadeEnsino.lato && !atividadeEnsino.residencia }">
			Strictu Sensu
		</c:if>

		</h2>


	<c:if test="${atividadeEnsino.lato}">
		<h:form>

			<div class="infoAltRem" style="width: 100%">
			  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
			<h:commandLink action="#{atividadeEnsino.listarLato}"
				value="Listar Atividade de Ensino - Lato Sensu Cadastradas"/>
			</div>
		</h:form>
	</c:if>

	<c:if test="${atividadeEnsino.residencia}">
	<h:form>
		<div class="infoAltRem" style="width: 100%">
			  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		<h:commandLink action="#{atividadeEnsino.listarResidenciaMedica}"
			value="Listar Atividade de Ensino - Lato Sensu Cadastradas"/>
		</div>
	</h:form>
	</c:if>

	<c:if test="${!atividadeEnsino.lato}">
		<c:if test="${!atividadeEnsino.residencia}">
			<h:form>
				<div class="infoAltRem" style="width: 100%">
			  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
				<h:commandLink action="#{atividadeEnsino.listar}"
					value="Listar Atividade de Ensino - Strictu Sensu Cadastradas"/>
				</div>
			</h:form>
		</c:if>
	</c:if>

	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Atividade de Ensino</caption>
			<h:inputHidden value="#{atividadeEnsino.confirmButton}" />
			<h:inputHidden value="#{atividadeEnsino.obj.id}" />


			<tr>
				<td colspan="2">
				<table>
					<tr>
						<th class="required">Docente:</th>

						<td colspan="3"><h:inputHidden id="id" value="#{atividadeEnsino.obj.servidor.id}"></h:inputHidden>
						<h:inputText id="nomeServidor"
							value="#{atividadeEnsino.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
							source="form:nomeServidor" target="form:id"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
							parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
					</tr>
					<tr>
						<th class="required">Ano:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.ano}" size="5"
							maxlength="255" readonly="#{atividadeEnsino.readOnly}" id="ano" /></td>
						<th class="required">Semestre:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.semestre}"
							size="2" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="semestre" /></td>
					</tr>
					<c:if test="${atividadeEnsino.residencia || atividadeEnsino.lato}">
							<tr>
								<th >Remunerada:</th>
								<td colspan="3"><h:selectBooleanCheckbox
									value="#{atividadeEnsino.obj.remunerada}" id="remunerada"
									disabled="#{atividadeEnsino.readOnly}" /></td>
							</tr>

					</c:if>
					<tr>
					<tr>
					<th class="required">Tipo de Atividade de Ensino:</th>

						<td><h:selectOneMenu value="#{atividadeEnsino.obj.tipoAtividadeEnsino.id}"
							style="width: 300px" disabled="#{atividadeEnsino.readOnly}"
							disabledClass="#{atividadeEnsino.disableClass}" id="tipoAtividade">
							<f:selectItems value="#{atividadeEnsino.tipoAtividade}" />
						</h:selectOneMenu></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption class="subFormulario">Informações da Turma</caption>
					<tr>
						<th class="required">Código da Disciplina:</th>
						<td><h:inputText
							value="#{atividadeEnsino.obj.codigoDisciplina}" size="8"
							maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="codigoDisciplina" /></td>
						<th class="required">Turma:</th>
						<td><h:inputText value="#{atividadeEnsino.obj.codigoTurma}"
							size="8" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="codigoTurma" /></td>
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
						<td colspan="3"> <h:inputText value="#{atividadeEnsino.obj.repFreq}"
							size="4" maxlength="255" readonly="#{atividadeEnsino.readOnly}"
							id="repFreq" /></td>


					</tr>
					<c:if test="${atividadeEnsino.lato }">
						<tr>
							<th class="required">Nome do Curso: </th>
							<td colspan="3"><h:inputText value="#{atividadeEnsino.obj.nomeCurso}"
							size="40" maxlength="255" readonly="#{atividadesEnsino.readOnly}" id="nomeCurso"/>
							</td>
						</tr>
					</c:if>
					<c:if test="${atividadeEnsino.residencia }">
						<tr>
						<th class="required">Nome do Curso:</th>
						<td colspan="3"><h:selectOneMenu style="width: 400px"
							value="#{atividadeEnsino.obj.residencia.id}"  id="residenciaCurso"
							disabled="#{atividadeEnsino.readOnly}"
							disabledClass="#{atividadeEnsino.disableClass}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{programaResidencia.allCombo}" />
						</h:selectOneMenu></td>
					</tr>
					</c:if>
					<c:if test="${!atividadeEnsino.lato }">
						<c:if test="${!atividadeEnsino.residencia }">
						<tr>
							<th class="required">Programa:</th>
							<td colspan="3"><h:selectOneMenu style="width: 400px"
								value="#{atividadeEnsino.obj.programa.id}"
								disabled="#{atividadeEnsino.readOnly}"
								disabledClass="#{atividadeEnsino.disableClass}">
								<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
								<f:selectItems value="#{unidade.allProgramaPosCombo}" />
							</h:selectOneMenu></td>
						</tr>
						</c:if>
					</c:if>
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

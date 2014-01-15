<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema/> &gt; Matrizes Curriculares de Graduação &gt; Dados da Matriz</h2>

<h:form id="formulario">
	<table class="formulario" width="98%">
			<h:outputText value="#{matrizCurricular.create}" />
			<caption class="listagem">Cadastro de Matriz Curricular</caption>
			<tr>
				<th width="23%" class="required">Curso:</th>
				<td colspan="3">
					<h:selectOneMenu id="curso"
						value="#{matrizCurricular.obj.curso.id}" style="width: 85%" valueChangeListener="#{matrizCurricular.carregarEnfasesByCurso}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
						<a4j:support event="onchange"  reRender="formulario" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="23%" class="required">Campus:</th>
				<td colspan="3">
					<h:selectOneMenu id="campus"
						value="#{matrizCurricular.obj.campus.id}" style="width: 78%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{campusIes.allCampusCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>			
			<tr>
				<th class="required">Turno:</th>
				<td colspan="3">
					<h:selectOneMenu id="turno"
						value="#{matrizCurricular.obj.turno.id}" style="width: 50%"
						readonly="#{matrizCurricular.readOnly}"
						disabledClass="#{matrizCurricular.disableClass}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{turno.allAtivosCombo}" />
					</h:selectOneMenu> 
					<ufrn:help img="/img/ajuda.gif">A informação de turno é obrigatória apenas para cursos presenciais</ufrn:help>
				</td>
			</tr>
			<tr>
				<th class="required">Modalidade:</th>
				<td colspan="3">
					<h:selectOneMenu id="modalidade"
						value="#{matrizCurricular.obj.grauAcademico.id}" style="width: 50%"
						readonly="#{matrizCurricular.readOnly}"
						disabledClass="#{matrizCurricular.disableClass}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{grauAcademico.allCombo}" />
					</h:selectOneMenu> 
				</td>
			</tr>
			<tr>
				<th class="required">Possui Habilitação?</th>
				<td colspan="3">
					<h:selectOneRadio value="#{matrizCurricular.obj.possuiHabilitacao}" id="possuiHabilitacao"
						readonly="#{habilitacaoGrad.readOnly}" styleClass="noborder" >
						<f:selectItem itemLabel="Sim" itemValue="true" />
						<f:selectItem itemLabel="Não" itemValue="false" />
						<a4j:support event="onclick" reRender="formulario" />
					</h:selectOneRadio>
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/indicator.gif" />
						</f:facet>
					</a4j:status>
				</td>
			</tr>
			<c:if test="${not matrizCurricular.obj.possuiHabilitacao}">
				<tr>
					<th class="required">Possui Ênfase?</th>
					<td colspan="3">
						<h:selectOneRadio value="#{matrizCurricular.obj.possuiEnfase}" id="possuiEnfase"
						readonly="#{habilitacaoGrad.readOnly}" styleClass="noborder" valueChangeListener="#{matrizCurricular.carregarEnfasesByCurso}">
							<f:selectItem itemLabel="Sim" itemValue="true" />
							<f:selectItem itemLabel="Não" itemValue="false" />
							<a4j:support event="onclick"  reRender="formulario" />
						</h:selectOneRadio>
					</td>
				</tr>
				<c:if test="${matrizCurricular.obj.possuiEnfase}">
					<tr>
						<th class="required">Ênfase:</th>
						<td colspan="3">
							<h:selectOneMenu value="#{matrizCurricular.obj.enfase.id}" id="enfase" >
								<f:selectItems value="#{matrizCurricular.enfases}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				
			</c:if>
			<tr>
				<td colspan="4"></td>
			</tr>
			<tr>
				<th class="required">Regime Letivo:</th>
				<td colspan="3">
					<h:selectOneMenu id="regimeLetivo"
						value="#{matrizCurricular.obj.regimeLetivo.id}" style="width: 50%"
						readonly="#{matrizCurricular.readOnly}"
						disabledClass="#{matrizCurricular.disableClass}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoRegimeLetivo.allCombo}" />
					</h:selectOneMenu> 
				</td>
			</tr>
			<tr>
				<th class="required">Sistema Curricular:</th>
				<td colspan="3">
					<h:selectOneMenu id="sistemaCurricular"
						value="#{matrizCurricular.obj.tipoSistemaCurricular.id}"
						style="width: 50%" readonly="#{matrizCurricular.readOnly}"
						disabledClass="#{matrizCurricular.disableClass}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoSistemaCurricular.allCombo}" />
					</h:selectOneMenu> 
				</td>
			</tr>
			<tr>
				<th class="required">Situação:</th>
				<td colspan="3">
					<h:selectOneMenu id="situacao"
						value="#{matrizCurricular.obj.situacao.id}" style="width: 50%"
						readonly="#{matrizCurricular.readOnly}"
						disabledClass="#{matrizCurricular.disableClass}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{situacaoCursoHabil.allCombo}" />
					</h:selectOneMenu> 
				</td>
			</tr>
			<tr>
				<th>Situação do Diploma:</th>
				<td colspan="3"><h:selectOneMenu id="situacaoDiploma"
					value="#{matrizCurricular.obj.situacaoDiploma.id}" style="width: 50%"
					readonly="#{matrizCurricular.readOnly}"
					disabledClass="#{matrizCurricular.disableClass}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{situacaoDiploma.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>Código INEP:</th>
				<td>
					<h:inputText id="codigoINEP" value="#{matrizCurricular.obj.codigoINEP}" 
					size="10" disabled="#{cursoGrad.readOnly}" maxlength="10" onkeyup="CAPS(this)"/>
				</td>
			</tr>
			<tr>
				<th>Início Funcionamento:</th>
				
				<td><t:inputCalendar value="#{matrizCurricular.obj.dataInicioFuncionamento}"
					size="10" maxlength="10" readonly="#{matrizCurricular.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="inicioFuncionamento">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
				
				<c:if test="${habilitacaoGrad.obj.id != 0}">
					<th>Data Extinção:</th>
					<td><t:inputCalendar id="dataExtincao"
						value="#{matrizCurricular.obj.dataExtincao}" size="10"
						maxlength="10" onkeypress="formataData(this,event)"
						readonly="#{matrizCurricular.readOnly}" renderAsPopup="true"
						renderPopupButtonAsImage="true" /></td>
				</c:if>
			</tr>
			<tr>
				<th>Encontra-se ativa?</th>
				<td colspan="3">
					<h:selectOneRadio value="#{matrizCurricular.obj.ativo}" id="encontraseAtiva"
					readonly="#{habilitacaoGrad.readOnly}" styleClass="noborder" >
						<f:selectItem itemLabel="Sim" itemValue="true" />
						<f:selectItem itemLabel="Não" itemValue="false" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th>Permite colação de grau?</th>
				<td colspan="3">
					<h:selectOneRadio value="#{matrizCurricular.obj.permiteColacaoGrau}" id="permiteColacaoGrau"
					readonly="#{habilitacaoGrad.readOnly}" styleClass="noborder" >
						<f:selectItem itemLabel="Sim" itemValue="true" />
						<f:selectItem itemLabel="Não" itemValue="false" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<td colspan="7">
				<table width="100%" class="subFormulario">
					<caption>Autorização de Funcionamento</caption>
					<tr>
						<th width="15%" class="required">Ato Normativo:</th>
						<td>
							<h:inputText id="ato_normativo"
							value="#{matrizCurricular.obj.autorizacaoAtoNormativo}" size="40" maxlength="80"
							disabled="#{matrizCurricular.readOnly}"
							readonly="#{matrizCurricular.readOnly}" />
						</td>
					</tr>
					<tr>
						<th class="required" style="width: 23%">Data do Ato Normativo:</th>

						<td><t:inputCalendar value="#{matrizCurricular.obj.autorizacaoAtoData}"
							size="10" maxlength="10" readonly="#{matrizCurricular.readOnly}" popupDateFormat="dd/MM/yyyy"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataAtoNormativo">
							<f:converter converterId="convertData"/> </t:inputCalendar>
						</td>

					</tr>
					<tr>
						<th class="required">Data da Publicação:</th>

						<td><t:inputCalendar value="#{matrizCurricular.obj.autorizacaoPublicacao}"
							size="10" maxlength="10" readonly="#{matrizCurricular.readOnly}" popupDateFormat="dd/MM/yyyy"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataPublicacao">
							<f:converter converterId="convertData"/> </t:inputCalendar>
						</td>

					</tr>
				</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton id="btCancelar" value="Cancelar" onclick="#{confirm}" 
						action="#{matrizCurricular.cancelar}" immediate="true" /> 
						<h:commandButton id="btProximoPasso" value="Próximo Passo >>" action="#{matrizCurricular.submeterDadosMatriz}" />
					</td>
				</tr>
			</tfoot>
	</table>
	<br>
	<center><html:img page="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

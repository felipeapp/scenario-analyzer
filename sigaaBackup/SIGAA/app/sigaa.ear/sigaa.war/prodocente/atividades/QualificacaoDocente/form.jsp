<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.listagem .colCheck{ text-align: left; }
	.listagem .colDisciplina{ text-align: left; }
	.listagem .colConceito{ text-align: center; }
</style>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>Docentes em Qualificação</h2>

	<h:form id="form">
	<table class="formulario" width="70%">
	<caption class="listagem">Cadastro de Qualificação</caption> 
	 <h:inputHidden value="#{qualificacaoDocente.confirmButton}" />
	 <h:inputHidden value="#{qualificacaoDocente.obj.id}" />
	 
	 <tr>
	  <td>
	   <table id="subFormulario" width="100%">
	   	<caption> Dados Gerais da Qualificação </caption>
			<tr>
				<th width="25%;" class="required">Instituição:</th>
				<td><h:inputText value="#{qualificacaoDocente.obj.instituicao}"
					size="50" maxlength="255" readonly="#{qualificacaoDocente.readOnly}"
					id="instituicao" />
				</td>
			</tr>

			<tr>
				<th class="required">Orientador:</th>
				<td><h:inputText value="#{qualificacaoDocente.obj.orientador}"
					size="50" maxlength="255" readonly="#{qualificacaoDocente.readOnly}"
					id="orientador" />
				</td>
			</tr>

			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{qualificacaoDocente.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{qualificacaoDocente.obj.servidor.pessoa.nome}" size="50" /> 
					<ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>

			<tr>
				<th class="required">Qualificação:</th>
				<td>
				<h:selectOneMenu style="width: 220px;"
					value="#{qualificacaoDocente.obj.qualificacao}"
					disabled="#{qualificacaoDocente.readOnly}"
					disabledClass="#{qualificacaoDocente.disableClass}" id="qualificacao">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{qualificacaoDocente.qualificacoes}" />
				</h:selectOneMenu>

				</td>
			</tr>
			<tr>
				<th class="required">País:</th>
				<td><h:selectOneMenu style="width: 220px;" value="#{qualificacaoDocente.obj.pais.id}"
					disabled="#{qualificacaoDocente.readOnly}"
					disabledClass="#{qualificacaoDocente.disableClass}" id="pais">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{pais.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Parecer:</th>
				<td><h:selectOneMenu style="width: 220px;" value="#{qualificacaoDocente.obj.tipoParecer.id}"
					disabled="#{qualificacaoDocente.readOnly}"
					disabledClass="#{qualificacaoDocente.disableClass}" id="tipoParecer">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{tipoParecer.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required"> Data de Início:</th>
				<td>
	    			 <t:inputCalendar value="#{qualificacaoDocente.obj.dataInicial}" id="dataInicio" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" >
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
			</tr>
			<tr>
				<th>Data Final:</th>
				<td>
	    			 <t:inputCalendar value="#{qualificacaoDocente.obj.dataFinal}" id="dataFim" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" >
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
			</tr>
			<tr>
				<th>Afastado:</th>
				<td>
					<h:selectBooleanCheckbox value="#{qualificacaoDocente.obj.afastado}" id="afastado"
					disabled="#{qualificacaoDocente.readOnly}" />
				</td>
			</tr>
			<tr>
				<th>Afastamento Formal:</th>
				<td><h:selectBooleanCheckbox
					value="#{qualificacaoDocente.obj.afastamentoFormal}"
					id="afastamentoFormal" disabled="#{qualificacaoDocente.readOnly}" /></td>
			</tr>

          </table>
	 
	 <tr>
	  <td>
     	 <table width="100%" class="subFormulario">
		   	<caption> Disciplina/Conceito </caption>
	   			<tr>
					<th width="25%;" class="obrigatorio">Disciplina:</th>
					<td>
					    <h:inputText value="#{qualificacaoDocente.disciplina}" size="50"
						maxlength="255" readonly="#{qualificacaoDocente.readOnly}"
						id="disciplinaAdd" />
					</td>
				</tr>
					
				<tr>
					<th>Conceito:</th>
					<td><h:selectOneMenu value="#{qualificacaoDocente.conceito}"
						style="width: 35px" disabled="#{qualificacaoDocente.readOnly}"
						disabledClass="#{qualificacaoDocente.disableClass}" id="conceitoAdd">
						<f:selectItems value="#{qualificacaoDocente.conceitos}" />
					</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<td colspan="2" style="text-align: center;" class="subFormulario">
						<h:commandButton value="Adicionar" actionListener="#{qualificacaoDocente.adicionarDisciplina}" />
					</td>
				</tr>
				
				<tr>
					<td colspan="5" align="center" class="subFormulario">
					<h:dataTable id="dtTblQualDoc" value="#{qualificacaoDocente.listaDisciplinaQualificacao}" var="dis" width="100%"
						columnClasses="colCheck,colDisciplina,colConceito" styleClass="listagem">
						<h:column headerClass="colCheck">
							<h:selectBooleanCheckbox value="#{dis.selecionado}" />
							</h:column>
						<h:column headerClass="colDisciplina">
							<f:facet name="header">
								<h:outputText value="Disciplina"/>
							</f:facet>
							<h:outputText value="#{dis.disciplina}" />
						</h:column>
						<h:column headerClass="colConceito">
							<f:facet name="header">
								<h:outputText value="Conceito"/>
							</f:facet>
							<h:outputText value="#{dis.conceito}" />
						</h:column>
					</h:dataTable><br>
					<center><h:commandButton value="Excluir Selecionadas"
						actionListener="#{qualificacaoDocente.removerDisciplina}" /></center></td>
				</tr>
		   		   
		   </table>
	 <tfoot>
	 	<tr>
	   		<td colspan=2>
	   			<h:commandButton value="#{qualificacaoDocente.confirmButton}" action="#{qualificacaoDocente.cadastrar}" /> 
	   			<h:commandButton value="Cancelar" action="#{qualificacaoDocente.cancelar}" onclick="#{confirm}" />
	   		</td>
	 	</tr>
	 </tfoot>

	 </table>
	 </h:form>
	 
	 <br />
	 	<center>
	  		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 	</center>
	 <br />

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
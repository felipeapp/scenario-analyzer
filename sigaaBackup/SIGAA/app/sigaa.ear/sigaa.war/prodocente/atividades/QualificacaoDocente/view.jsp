<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>Docentes em Qualificação</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/> 
	  <a href="${ctx}/prodocente/atividades/QualificacaoDocente/lista.jsf">Listar Qualificações Cadastradas</a>
	 </div>
    </h:form>
	<h:messages showDetail="true"></h:messages>	

<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<table class="formulario" width="100%">
	<h:form>
	<caption class="listagem">Cadastro de Qualificação</caption> 
	 <h:inputHidden value="#{qualificacaoDocente.confirmButton}" />
	 <h:inputHidden value="#{qualificacaoDocente.obj.id}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{qualificacaoDocente.obj.instituicao}"
					size="50" maxlength="255" readonly="#{qualificacaoDocente.readOnly}"
					id="instituicao" /></td>
			</tr>

			<tr>
				<th class="required">Orientador:</th>
				<td><h:inputText value="#{qualificacaoDocente.obj.orientador}"
					size="50" maxlength="255" readonly="#{qualificacaoDocente.readOnly}"
					id="orientador" /></td>
			</tr>


			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{qualificacaoDocente.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{qualificacaoDocente.obj.servidor.pessoa.nome}" size="50" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>

			<tr>
				<th class="required">Qualificação:</th>
				<td><h:selectOneMenu style="width: 220px;"
					value="#{qualificacaoDocente.obj.tipoQualificacao.id}"
					disabled="#{qualificacaoDocente.readOnly}"
					disabledClass="#{qualificacaoDocente.disableClass}" id="tipoQualificacao">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{tipoQualificacao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Pais:</th>
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
				<td><t:inputCalendar value="#{qualificacaoDocente.obj.dataInicial}"
					size="10" maxlength="10" readonly="#{qualificacaoDocente.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="dataInicial" /></td>
			</tr>

			<tr>
				<th>Data Final:</th>
				<td><t:inputCalendar value="#{qualificacaoDocente.obj.dataFinal}"
					size="10" maxlength="10" readonly="#{qualificacaoDocente.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFinal" /></td>
			</tr>
			<tr>
				<th>Afastado:</th>
				<td><h:selectBooleanCheckbox
					value="#{qualificacaoDocente.obj.afastado}" id="afastado"
					disabled="#{qualificacaoDocente.readOnly}" />

					</td>
			</tr>
			<tr>
				<th>Afastamento Formal</th>
				<td><h:selectBooleanCheckbox
					value="#{qualificacaoDocente.obj.afastamentoFormal}"
					id="afastamentoFormal" disabled="#{qualificacaoDocente.readOnly}" /></td>
			</tr>



          </table>
	  </td>
	 <!-- Fim Coluna 1 --> 
	  
	 <!-- Coluna 2 -->  
	  <td width="30%">
	   <table id="coluna2">

	


		<!-- Disciplina/Conteito -->
		<tr>
		 <td colspan="2">
		  <fieldset class=""><legend>Disciplina/Conceito</legend>
		   <table>
		   			<tr>
					<th class="required">Disciplina:</th>
					<td>
					    <h:inputText value="#{qualificacaoDocente.disciplina}" size="20"
						maxlength="255" readonly="#{qualificacaoDocente.readOnly}"
						id="disciplinaAdd" />
					</td>
					</tr>
					
					<tr>
					<th class="required">Conceito:</th>
					<td><h:selectOneMenu value="#{qualificacaoDocente.conceito}"
						style="width: 50px" disabled="#{qualificacaoDocente.readOnly}"
						disabledClass="#{qualificacaoDocente.disableClass}" id="conceitoAdd">
						<f:selectItems value="#{qualificacaoDocente.conceitos}" />
					</h:selectOneMenu>
					<h:commandButton value="Adcionar"
						actionListener="#{qualificacaoDocente.adicionarDisciplina}" />
					</td>
					</tr>
				
				<tr>
					<td colspan="5" align="center">
					<h:dataTable
						value="#{qualificacaoDocente.listaDisciplinaQualificacao}" var="dis" width="100%"
						styleClass="subFormulario">
						<h:column>
							<h:selectBooleanCheckbox value="#{dis.selecionado}" />
							</h:column>
						<h:column>
							<f:facet name="header">
								<h:outputText value="Disciplina"/>
							</f:facet>
							<h:outputText value="#{dis.disciplina}" />
						</h:column>
						<h:column>
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
		  </fieldset>
		 </td>
		</tr>
	    <!-- Fim Disciplina/Conceito  -->

           </table>
	  </td>
	 <!-- Fim Coluna 2  -->
	 </tr>


         <!-- Botoes -->
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="#{qualificacaoDocente.confirmButton}"
		action="#{qualificacaoDocente.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{qualificacaoDocente.cancelar}" /></td>
	  </tr>
	 </tfoot>
	 <!-- Fim botoes -->


	 </h:form>
	 </table>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->	 
	 <br />
	 <center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	 <br />


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Orientação - ProEx</h2>
	
	<h:messages showDetail="true"/>
	
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Orientação</caption>
			<h:inputHidden value="#{orientacaoProex.confirmButton}" />
			<h:inputHidden value="#{orientacaoProex.obj.id}" />
			<tr>
				<th  class="required">Docente:</th>


				<td><h:inputHidden id="id" value="#{orientacaoProex.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{orientacaoProex.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{orientacaoProex.obj.titulo}"
					size="60" maxlength="255" readonly="#{orientacaoProex.readOnly}"
					id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Quantidade de Alunos:</th>
				<td><h:inputText value="#{orientacaoProex.obj.alunos}" size="60" maxlength="5" 
					onkeyup="return formatarInteiro(this);" readonly="#{orientacaoProex.readOnly}"
					id="alunos"/></td>
			</tr>
			<tr>
				<th class="required">Data de Início:</th>
				<td><t:inputCalendar
							value="#{orientacaoProex.obj.dataInicio}" size="10" maxlength="10" 
							popupDateFormat="dd/MM/yyyy" readonly="#{orientacao.readOnly}" id="dataInicio"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							renderAsPopup="true" renderPopupButtonAsImage="true"> 
							<f:converter converterId="convertData"/> </t:inputCalendar></td>
			</tr>
			<tr>
				<th class="required">Data do Fim:</th>
				<td><t:inputCalendar
							value="#{orientacaoProex.obj.dataFinal}" size="10" maxlength="10" 
							popupDateFormat="dd/MM/yyyy" readonly="#{orientacao.readOnly}" id="dataFinal"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							renderAsPopup="true" renderPopupButtonAsImage="true"> 
							<f:converter converterId="convertData"/> </t:inputCalendar></td>
			<tr>
				<th class="required">Nome do Aluno:</th>
				<td><h:inputText value="#{orientacaoProex.obj.nomeAluno}"
					size="60" maxlength="255" readonly="#{orientacaoProex.readOnly}"
					id="nomeAluno" /></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{orientacaoProex.obj.instituicao}"
					size="60" maxlength="255" readonly="#{orientacaoProex.readOnly}"
					id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu style="width: 400px"
							value="#{orientacaoProex.obj.departamento.id}"
							disabled="#{orientacaoProex.readOnly}"
							disabledClass="#{orientacaoProex.disableClass}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{unidade.allDepartamentoCombo}" />
						</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Financiamento:</th>
				<td><h:inputText value="#{orientacaoProex.obj.financiamento}"
					size="60" maxlength="255" readonly="#{orientacaoProex.readOnly}"
					id="financiamento" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{orientacaoProex.confirmButton}"
						action="#{orientacaoProex.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{orientacaoProex.cancelar}" onclick="#{confirm}" immediate="true" /></td>
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

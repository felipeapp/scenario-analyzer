<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<br/>
	<h:form>
		<div style="text-align:right">
		<h:commandLink action="#{avaliacaoOrganizacao.listar}"
			value="Listar Avaliação Organização Cadastradas"/>
		</div>
	</h:form>
<h:messages showDetail="true"></h:messages>
<hr/>	
<h:form id="form">
<table class="formulario" width="95%">
	<caption class="listagem">Cadastro de Avaliação Organização</caption>	
	<h:inputHidden value="#{avaliacaoOrganizacao.confirmButton}"/>
 	<h:inputHidden value="#{avaliacaoOrganizacao.obj.id}"/>
			<tr>
				<th class="required">Area:</th>

				<td><h:selectOneMenu value="#{avaliacaoOrganizacao.obj.area.id}"
					disabled="#{avaliacaoOrganizacao.readOnly}" id="area"
					disabledClass="#{avaliacaoOrganizacao.disableClass}" onchange="submit()" valueChangeListener="#{producao.changeArea}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Area:</th>

				<td><h:selectOneMenu value="#{avaliacaoOrganizacao.obj.subArea.id}"
					disabled="#{avaliacaoOrganizacao.readOnly}" id="subArea"
					disabledClass="#{avaliacaoOrganizacao.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{producao.subArea}" />
				</h:selectOneMenu></td>
			</tr>
	<tr>
		<th > Veiculo:</th>
		<td> <h:inputText value="#{avaliacaoOrganizacao.obj.veiculo}" size="60" maxlength="255" readonly="#{avaliacaoOrganizacao.readOnly}" id="veiculo"/></td>
	</tr>
	<tr>
		<th> Local:</th>
		<td> <h:inputText value="#{avaliacaoOrganizacao.obj.local}" size="60" maxlength="255" readonly="#{avaliacaoOrganizacao.readOnly}" id="local"/></td>
	</tr>
	<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{avaliacaoOrganizacao.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{avaliacaoOrganizacao.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoInicio" /></td>
			</tr>
			<tr>
				<th class="required">Período Fim:</th>
				<td><t:inputCalendar value="#{avaliacaoOrganizacao.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{avaliacaoOrganizacao.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoFim" /></td>
			</tr>
	<tr>
	<th class="required"> Servidor:</th>
	
	<td>
		<h:inputHidden id="id" value="#{avaliacaoOrganizacao.obj.servidor.id}"></h:inputHidden>
			<h:inputText id="nomeServidor"
				value="#{avaliacaoOrganizacao.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
				source="form:nomeServidor" target="form:id"
				baseUrl="/sigaa/ajaxDocente" className="autocomplete"
				indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
				parser="new ResponseXmlToHtmlListParser()" /> 
			<span id="indicator" style="display:none; "> 
			<img src="/sigaa/img/indicator.gif" /> </span>
	</td>
			
	</tr>
	
	<tr>
	<th class="required"> Tipo de Avaliacao Organizacao:</th>
	
	<td>
	
	<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.tipoAvaliacaoOrganizacao.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="tipoAvaliacaoOrganizacao">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
			<f:selectItems value="#{tipoAvaliacaoOrganizacaoEvento.allCombo}"/>
		</h:selectOneMenu>
	</td>
	</tr>
	<tr>
	<th class="required"> Tipo de Participacao:</th>
	
	<td>
	
	<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.tipoParticipacao.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="tipoParticipacao">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
			<f:selectItems value="#{tipoParticipacao.allCombo}"/>
		</h:selectOneMenu>
	</td>
	</tr>
	<tr>
	<th class="required">  TipoRegiao:</th>
	
	<td>
	
	<h:selectOneMenu value="#{avaliacaoOrganizacao.obj.tipoRegiao.id}" disabled="#{avaliacaoOrganizacao.readOnly}" disabledClass="#{avaliacaoOrganizacao.disableClass}" id="tipoRegiao">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
			<f:selectItems value="#{tipoRegiao.allCombo}"/>
		</h:selectOneMenu>
	</td>
	</tr>
	<tfoot><tr><td colspan=2>
	<h:commandButton value="#{avaliacaoOrganizacao.confirmButton}" action="#{avaliacaoOrganizacao.cadastrar}" /> <h:commandButton value="Cancelar" action="#{avaliacaoOrganizacao.cancelar}" /></td>
	</tr></tfoot>
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

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>
<f:view>
	<h2><ufrn:subSistema /> > Monitoria</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
	  <h:commandLink action="#{monitoria.listar}" value="Listar Monitoria Cadastradas"/>
	 </div>
    </h:form>
    
	<h:messages showDetail="true" />
	
	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Monitoria</caption>
			<h:inputHidden value="#{monitoria.confirmButton}" />
			<h:inputHidden value="#{monitoria.obj.id}" />
			<h:inputHidden value="#{monitoria.obj.ativo}" />
			
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{monitoria.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{monitoria.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Nome da Disciplina:</th>
				<td><h:inputText value="#{monitoria.obj.nomeDisciplina}"
					size="60" maxlength="255" readonly="#{monitoria.readOnly}"
					id="nomeDisciplina" /></td>
			</tr>

			<tr>
				<th class="required">Nome do Monitor:</th>
					<td>
					<h:inputHidden id="idDiscente" value="#{ monitoria.obj.monitor.id }"/>
			 		<h:inputText id="nomeDiscente"
					value="#{monitoria.obj.monitor.pessoa.nome}" size="60" />

					<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=D,G,T,L,S,M" 
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDiscente" style="display:none; "> 
							<img src="/sigaa/img/indicator.gif" /> </span>
					</td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:selectOneMenu value="#{monitoria.obj.ies.id}"
					disabled="#{monitoria.readOnly}" disabledClass="#{monitoria.disableClass}"
					id="ies">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{instituicoesEnsino.allCombo}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>
				<td colspan="3"><h:selectOneMenu style="width: 400px"
					value="#{monitoria.obj.departamento.id}"
					disabled="#{monitoria.readOnly}" id="departamento"
					disabledClass="#{monitoria.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Agência Financiadora:</th>

				<td><h:selectOneMenu
					value="#{monitoria.obj.entidadeFinanciadora.id}"
					disabled="#{monitoria.readOnly}"
					disabledClass="#{monitoria.disableClass}" id="entidadeFinanciadora">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{monitoria.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{monitoria.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required">Período Fim:</th>
				<td><t:inputCalendar value="#{monitoria.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{monitoria.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFim">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>

			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{monitoria.obj.informacao}" size="60"
					maxlength="255" readonly="#{monitoria.readOnly}" id="informacao" /></td>
			</tr>


			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{monitoria.confirmButton}" action="#{monitoria.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{monitoria.cancelar}" onclick="#{confirm}" immediate="true" /></td>
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
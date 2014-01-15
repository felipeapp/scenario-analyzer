<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Estágio</h2>
	
	
	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/> 
	 <a href="${ctx}/prodocente/atividades/Estagio/lista.jsf" >Listar Estágios Cadastrados</a>
	 </div>
    </h:form>
	<h:messages showDetail="true"></h:messages>	
	
	
	<h:form id="form">	
	<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Estágio</caption>
			<h:inputHidden value="#{estagio.confirmButton}" />
			<h:inputHidden value="#{estagio.obj.id}" />
			<tr>
				<th class="required">Nome do Projeto:</th>
				<td><h:inputText value="#{estagio.obj.nomeProjeto}" size="60"
					maxlength="255" readonly="#{estagio.readOnly}" id="nomeProjeto" /></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{estagio.obj.instituicao}" size="60"
					maxlength="255" readonly="#{estagio.readOnly}" id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">Orientando:</th>
				<td><h:inputText value="#{estagio.obj.orientando}" size="60"
					maxlength="255" readonly="#{estagio.readOnly}" id="orientando" /></td>
			</tr>
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{estagio.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{estagio.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th>Informação:</th>
				<td><h:inputText value="#{estagio.obj.informacao}" size="60"
					maxlength="255" readonly="#{estagio.readOnly}" id="informacao" /></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{estagio.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{estagio.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoInicio"/></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar value="#{estagio.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{estagio.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoFim"/></td>
			</tr>
			<tr>
				<th class="required">Área:</th>
				<td><h:selectOneMenu value="#{estagio.obj.area.id}"
					disabled="#{estagio.readOnly}" disabledClass="#{estagio.disableClass}"
					id="area" valueChangeListener="#{producao.changeArea}" onchange="submit()">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{estagio.obj.subArea.id}"
					disabled="#{estagio.readOnly}" disabledClass="#{estagio.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="SELECIONE A ÁREA" />
					<f:selectItems value="#{producao.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu value="#{estagio.obj.departamento.id}" style="width: 400px"
					disabled="#{estagio.readOnly}"
					disabledClass="#{estagio.disableClass}" id="departamento">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Agência Financiadora:</th>

				<td><h:selectOneMenu
					value="#{estagio.obj.entidadeFinanciadora.id}"
					disabled="#{estagio.readOnly}"
					disabledClass="#{estagio.disableClass}" id="entidadeFinanciadora">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton id="btConfirmarAcao"
						value="#{estagio.confirmButton}" action="#{estagio.cadastrar}" />
					<h:commandButton value="Cancelar" id="btCancelar" action="#{estagio.cancelar}" /></td>
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

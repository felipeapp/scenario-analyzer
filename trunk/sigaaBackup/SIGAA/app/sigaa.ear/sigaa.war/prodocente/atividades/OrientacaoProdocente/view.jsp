<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>Orientação</h2>

	<h:form>
 	    <div class="infoAltRem" style="width: 100%">
 	    <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
 	    <a href="${ctx}/prodocente/atividades/OrientacaoProdocente/lista.jsf">Listar Orientações Cadastradas</a>
	<%--
		<c:if test="${orientacao.tipoOrientacao }">
		<h:commandLink action="#{orientacao.listar}"
			value="Listar Orientações Cadastradas"/>
		</c:if>
		<c:if test="${!orientacao.tipoOrientacao }">
		<h:commandLink action="#{orientacao.listarResidencia}"
			value="Listar Orientações Cadastradas"/>
		</c:if>

	--%>
		</div>
	</h:form>





	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Orientação</caption>
			<h:inputHidden value="#{orientacao.confirmButton}" />
			<h:inputHidden value="#{orientacao.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id"
					value="#{orientacao.obj.servidor.id}"></h:inputHidden> <h:inputText
					id="nomeServidor" value="#{orientacao.obj.servidor.pessoa.nome}"
					size="60" /> <ajax:autocomplete source="form:nomeServidor"
					target="form:id" baseUrl="/sigaa/ajaxDocente"
					className="autocomplete" indicator="indicator"
					minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" />
				</span></td>
			</tr>
			<tr>
				<th class="required">Aluno:</th>
					<td>
					<h:inputHidden id="idDiscente" value="#{ orientacao.obj.aluno.id }"/>
			 		<h:inputText id="nomeDiscente"
					value="#{orientacao.obj.aluno.pessoa.nome}" size="60" />

					<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters=""
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDiscente"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
					</td>
			</tr>

			<tr>
				<th class="required">Data de Início:</th>
				<td><t:inputCalendar
							value="#{orientacao.obj.dataInicio}" size="10"
							maxlength="10" readonly="#{orientacao.readOnly}" id="dataInicio"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Data do Fim:</th>
				<td><t:inputCalendar
							value="#{orientacao.obj.dataFim}" size="10"
							maxlength="10" readonly="#{orientacao.readOnly}" id="dataFim"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Data de Alteração:</th>
				<td><t:inputCalendar
							value="#{orientacao.obj.dataAlteracao}" size="10"
							maxlength="10" readonly="#{orientacao.readOnly}" id="dataAlteracao"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>

			<tr>
				<th class="required">Tipo de Orientação Docente:</th>
				<td><h:selectOneMenu
					value="#{orientacao.obj.orientacao.id}"
					disabled="#{orientacao.readOnly}"
					disabledClass="#{orientacao.disableClass}" id="tipoOrientacaoDocente">
					<f:selectItem itemValue="-1" itemLabel="SELECIONE" />
					<f:selectItems value="#{tipoOrientacaoDocente.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<c:if test="${orientacao.tipoOrientacao }">
			<tr>
				<th class="required">Tipo de Orientação :</th>
				<td><h:selectOneMenu
					value="#{orientacao.obj.tipoOrientacao.id}"
					disabled="#{orientacao.readOnly}"
					disabledClass="#{orientacao.disableClass}" id="tipoOrientacao">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{tipoOrientacao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{orientacao.confirmButton}"
						action="#{orientacao.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{orientacao.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

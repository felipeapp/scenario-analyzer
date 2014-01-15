<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2> <ufrn:subSistema /> > Atividade de Extensão</h2>
	
	<h:messages showDetail="true" />
	<h:form id="form">
		<table class=formulario width="90%">
			
			<caption class="listagem">Cadastro de Atividade de Extensão</caption>
			<h:inputHidden value="#{atividadeExtensaoProdocente.confirmButton}" />
			<h:inputHidden value="#{atividadeExtensaoProdocente.obj.id}" />
			
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{atividadeExtensaoProdocente.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{atividadeExtensaoProdocente.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{atividadeExtensaoProdocente.obj.titulo}"
					size="60" maxlength="255" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Área:</th>

				<td><h:selectOneMenu value="#{atividadeExtensaoProdocente.obj.area.id}"
					disabled="#{atividadeExtensaoProdocente.readOnly}"
					disabledClass="#{atividadeExtensaoProdocente.disableClass}" id="area">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Departamento:</th>

				<td><h:selectOneMenu
					value="#{atividadeExtensaoProdocente.obj.unidade.id}"
					disabled="#{atividadeExtensaoProdocente.readOnly}" style=" width: 400px"
					disabledClass="#{atividadeExtensaoProdocente.disableClass}" id="unidade">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Data de Inclusão:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.dataInclusao}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="dataInclusao" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th class="required">Objetivo:</th>
				<td><h:inputText value="#{atividadeExtensaoProdocente.obj.objetivo}"
					size="60" maxlength="255" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="objetivo" /></td>
			</tr>
			<tr>
				<th class="required">Metodologia:</th>
				<td><h:inputText value="#{atividadeExtensaoProdocente.obj.metodologia}"
					size="60" maxlength="255" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="metodologia" /></td>
			</tr>
			<tr>
				<th class="required">Início Previsto:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.inicioPrevisto}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="inicioPrevisto" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th class="required">Término Previsto:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.terminoPrevisto}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="terminoPrevisto" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Início Real:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.inicioReal}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="inicioReal" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Término Real:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.terminoReal}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="terminoReal" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th class="required">Ch Total:</th>
				<td><h:inputText value="#{atividadeExtensaoProdocente.obj.chTotal}"
					size="6" maxlength="255" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="chTotal" /></td>
			</tr>
			<tr>
				<th class="required">Data de Avaliação:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.dataAvaliacao}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="dataAvaliacao" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th class="required">Data de Avaliação do Depto:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.dataAvaliacaoDepto}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="dataAvaliacaoDepto" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th class="required">Data de Avaliação do Consepe:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.dataAvaliacaoConsepe}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="dataAvaliacaoConsepe" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th class="required">Data de Avaliação do Centro:</th>
				<td><t:inputCalendar
					value="#{atividadeExtensaoProdocente.obj.dataAvaliacaoCentro}" size="10"
					maxlength="10" readonly="#{atividadeExtensaoProdocente.readOnly}"
					id="dataAvaliacaoCentro" renderAsPopup="true"
					renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Área Temática 1:</th>

				<td><h:selectOneMenu
					value="#{atividadeExtensaoProdocente.obj.areaTematica1.id}"
					disabled="#{atividadeExtensaoProdocente.readOnly}"
					disabledClass="#{atividadeExtensaoProdocente.disableClass}"
					id="areaTematica1">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{areaTematica.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>Área Tematica 2:</th>

				<td><h:selectOneMenu
					value="#{atividadeExtensaoProdocente.obj.areaTematica2.id}"
					disabled="#{atividadeExtensaoProdocente.readOnly}"
					disabledClass="#{atividadeExtensaoProdocente.disableClass}"
					id="areaTematica2">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{areaTematica.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

		<%-- 	
				Não estão sendo usadas! Por isso que as mesmas foram comentadas.
			<tr>
				<th class="required">Linha Programática de Atividade de Extensão:</th>
				<td><h:selectOneMenu
					value="#{atividadeExtensaoProdocente.obj.linhaProgramaticaAtividadeExtensao.id}"
					disabled="#{atividadeExtensaoProdocente.readOnly}"
					disabledClass="#{atividadeExtensaoProdocente.disableClass}"
					id="linhaProgramaticaAtividadeExtensao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems
						value="#{linhaProgramaticaAtividadeExtensao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			
			<tr>
				<th class="required">Situação de Atividade de Extensao:</th>

				<td><h:selectOneMenu
					value="#{atividadeExtensaoProdocente.obj.situacaoAtividadeExtensao.id}"
					disabled="#{atividadeExtensaoProdocente.readOnly}"
					disabledClass="#{atividadeExtensaoProdocente.disableClass}"
					id="situacaoAtividadeExtensao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{situacaoAtividadeExtensao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
		--%>
		
			<tr>
				<th class="required">Tipo de Atividade de Extensão:</th>

				<td><h:selectOneMenu
					value="#{atividadeExtensaoProdocente.obj.tipoAtividadeExtensao.id}"
					disabled="#{atividadeExtensaoProdocente.readOnly}"
					disabledClass="#{atividadeExtensaoProdocente.disableClass}"
					id="tipoAtividadeExtensao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			
			<tr>
				<th class="required">Tipo de Região:</th>

				<td><h:selectOneMenu
					value="#{atividadeExtensaoProdocente.obj.tipoRegiao.id}"
					disabled="#{atividadeExtensaoProdocente.readOnly}"
					disabledClass="#{atividadeExtensaoProdocente.disableClass}" id="tipoRegiao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2>
					<h:commandButton value="#{atividadeExtensaoProdocente.confirmButton}"
						action="#{cargoAdministrativo.cadastrar}" /> 
					<h:commandButton value="Cancelar" action="#{atividadeExtensaoProdocente.cancelar}" 
						onclick="#{confirm}" immediate="true"/></td>
				</tr>
			</tfoot>
						
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>
	<br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
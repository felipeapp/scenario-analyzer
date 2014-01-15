<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 	<h:graphicImage title="Voltar para Tela de Novas Produ��es" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Participa��o em Organiza��o de Eventos, Consultorias, Edi��o e Revis�o de Per�odicos
	</h2>

	 <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{participacaoComissaoOrgEventos.listar }" value="Participa��o em Organiza��o de Eventos, Consultorias, Edi��o e Revis�o de Per�odicos" />
	 </div>
    </h:form>


		<h:form>
	<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Participa��o em Organiza��o de Eventos, Consultorias, Edi��o e Revis�o de Per�odicos</caption>
			<h:inputHidden
				value="#{participacaoComissaoOrgEventos.confirmButton}" />
			<h:inputHidden value="#{participacaoComissaoOrgEventos.obj.id}" />
			<h:inputHidden value="#{participacaoComissaoOrgEventos.obj.validado}" />
			<tr>
				<th class="required">�mbito:</th>

				<td><h:selectOneMenu
					value="#{participacaoComissaoOrgEventos.obj.ambito.id}"
					disabled="#{participacaoComissaoOrgEventos.readOnly}"
					disabledClass="#{participacaoComissaoOrgEventos.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">�rea:</th>

				<td><h:selectOneMenu value="#{participacaoComissaoOrgEventos.obj.area.id}"
					disabled="#{participacaoComissaoOrgEventos.readOnly}"
					disabledClass="#{participacaoComissaoOrgEventos.disableClass}" id="area" valueChangeListener="#{participacaoComissaoOrgEventos.changeArea}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{area.allCombo}" />
					<a4j:support event="onchange" reRender="subarea" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-�rea:</th>
				<td><h:selectOneMenu value="#{participacaoComissaoOrgEventos.obj.subArea.id}"
					disabled="#{participacaoComissaoOrgEventos.readOnly}"
					disabledClass="#{participacaoComissaoOrgEventos.disableClass}" id="subarea">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{participacaoComissaoOrgEventos.subArea}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Tipo de Participa��o:</th>

				<td><h:selectOneMenu
					value="#{participacaoComissaoOrgEventos.obj.tipoParticipacaoOrganizacao.id}"
					disabled="#{participacaoComissaoOrgEventos.readOnly}"
					disabledClass="#{participacaoComissaoOrgEventos.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems
						value="#{tipoParticipacaoOrganizacaoEventos.allAtivoCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required"> Ve�culo/Evento/Comiss�o:</th>
				<td><h:inputText
					value="#{participacaoComissaoOrgEventos.obj.veiculo}" size="60"
					maxlength="255"
					readonly="#{participacaoComissaoOrgEventos.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Local:</th>
				<td><h:inputText value="#{participacaoComissaoOrgEventos.obj.local}"
					size="60" maxlength="255"
					readonly="#{participacaoComissaoOrgEventos.readOnly}" /></td>
			</tr>
			
			<tr>
				<th class="required" nowrap="nowrap">Data da publica��o:</th>
				<td>
					<t:inputCalendar value="#{participacaoComissaoOrgEventos.obj.dataProducao}"
						size="10" maxlength="10" readonly="#{participacaoComissaoOrgEventosa.readOnly}"
						renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />					
				</td>
			</tr>
			<tr>
				<th class="required">Ano de Refer�ncia:</th>
				<td>
				 <h:selectOneMenu id="anoReferencia" value="#{participacaoComissaoOrgEventos.obj.anoReferencia}">
					<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   				</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th class="required">Per�odo In�cio: <span style="font-size:9px; color:#a7a7a7; ">(M�s/Ano)</span></th>
				<td><t:inputText
					value="#{participacaoComissaoOrgEventos.obj.periodoInicio}"
					size="7" maxlength="7"
					readonly="#{participacaoComissaoOrgEventos.readOnly}"
					onkeypress="return(formatarMascara(this,event,'##/####'))" >
						 <f:convertDateTime pattern="MM/yyyy" />
				    </t:inputText>
				</td>
			</tr>
			<tr>
				<th>Per�odo Fim: <span style="font-size:9px; color:#a7a7a7; ">(M�s/Ano)</span></th>
				<td><t:inputText
					value="#{participacaoComissaoOrgEventos.obj.periodoFim}" size="7"
					maxlength="7"
					readonly="#{participacaoComissaoOrgEventos.readOnly}"
					onkeypress="return(formatarMascara(this,event,'##/####'))" >
						 <f:convertDateTime pattern="MM/yyyy" />
					</t:inputText>
					</td>
			</tr>

			<tr>
				<th>Informa��es Complementares:</th>
				<td>
					<h:inputTextarea value="#{participacaoComissaoOrgEventos.obj.informacao}" cols="49" rows="3" />
				</td>
			</tr>


			<c:if test="${participacaoComissaoOrgEventos.validar}">
			<tr>
				<th>Validar:</th>
				<td><h:selectOneRadio rendered="#{bean.validar}"
					value="#{participacaoComissaoOrgEventos.obj.validado}">
					<f:selectItem itemValue="false" itemLabel="Inv�lido" />
					<f:selectItem itemValue="true" itemLabel="V�lido" />
				</h:selectOneRadio></td>
			</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan=2>
					<h:commandButton value="Validar" action="#{participacaoComissaoOrgEventos.validar}" immediate="true" rendered="#{participacaoComissaoOrgEventos.validar}"/>
					<h:commandButton
						value="#{participacaoComissaoOrgEventos.confirmButton}"
						action="#{participacaoComissaoOrgEventos.cadastrar}" /> <h:commandButton
						value="Cancelar"
						action="#{participacaoComissaoOrgEventos.cancelar}" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
	</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
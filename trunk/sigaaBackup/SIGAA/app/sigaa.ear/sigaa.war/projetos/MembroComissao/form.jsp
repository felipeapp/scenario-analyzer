<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	
	<h2><ufrn:subSistema /> > Membros da Comissão</h2>
	<h:form id="membro">
	
	<div class="descricaoOperacao">
		Para cadastrar participação em colegiado é necessário que o Servidor selecionado seja um Docente.
	</div>
	
	<h:inputHidden value="#{membroComissao.confirmButton}"/>
	<h:inputHidden id="id" value="#{membroComissao.obj.id}"/>

	<table class="formulario" width="70%">
	<caption class="listagem"> Atribuir Membro da Comissão </caption>



	<tr>
		<th width="20%" class="required"> Servidor(a): </th>
		<td> 
			 <c:if test="${membroComissao.confirmButton == 'Cadastrar'}">
					<h:inputText value="#{membroComissao.obj.servidor.pessoa.nome}" id="nome" size="60" readonly="#{membroComissao.readOnly}"/>
						 <h:inputHidden value="#{membroComissao.obj.servidor.id}" id="idServidor"/>
			
						 <ajax:autocomplete source="membro:nome" target="membro:idServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos,inativos=false"
							parser="new ResponseXmlToHtmlListParser()" />
			
						<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> </span>
			</c:if>
			 <c:if test="${membroComissao.confirmButton == 'Alterar'}">
			 	<b>${membroComissao.obj.servidor.pessoa.nome}</b>
			 </c:if>
		</td>
	</tr>	
	

	<tr>
		<th class="required" width="20%"> Comissão: </th>
		<td>
			<h:selectOneMenu id="comissao" value="#{membroComissao.obj.papel}">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="" />
				<f:selectItems value="#{membroComissao.tiposMembro}"/>
 			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<th class="required" width="20%"> Início do Mandato: </th>
		<td> 
			<t:inputCalendar value="#{membroComissao.obj.dataInicioMandato}" popupDateFormat="dd/MM/yyyy" readonly="#{membroComissao.readOnly}" 
				renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" title="Início do Mandato"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupTodayString="Hoje é">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</t:inputCalendar>
		</td>
	</tr>
	
	<tr>
		<th class="required" width="20%"> Final do Mandato: </th>
		<td> 
			<t:inputCalendar value="#{membroComissao.obj.dataFimMandato}" popupDateFormat="dd/MM/yyyy" readonly="#{membroComissao.readOnly}" 
				renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" title="Final do Mandato"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupTodayString="Hoje é">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</t:inputCalendar>
		</td>
	</tr>

	<tr>
		<th>  </th>
		<td> 
			<h:outputText value="Também desejo cadastrar participação em colegiado:" rendered="#{membroComissao.confirmButton != 'Alterar'}"/>
			<h:selectBooleanCheckbox value="#{membroComissao.exibirProximaTela}" rendered="#{membroComissao.confirmButton != 'Alterar'}" />
		</td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{membroComissao.confirmButton}" action="#{membroComissao.cadastrarMembro}" />
				<h:commandButton value="Cancelar" action="#{membroComissao.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>

	</table>

	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
			
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
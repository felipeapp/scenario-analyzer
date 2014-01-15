<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2>Bolsista</h2>

	<%-- 
	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{bolsista.listar}"/>
			</div>
			</h:form>
	</center>
	--%>


	<table class="formulario" width="100%">
		<h:form id="formCadBolsista">
			<h:outputText value="#{bolsista.create}"/>
			<caption class="listagem">Cadastro de Bolsista</caption>
			<h:inputHidden value="#{bolsista.confirmButton}" />
			<h:inputHidden value="#{bolsista.obj.id}" />
			<h:inputHidden value="#{bolsista.blockTipoBolsa}" />
			<h:inputHidden value="#{bolsista.blockEntidade}" />
			
			
			<tr>
				<th>Discente:</th>
				<td>

					 <h:inputHidden id="idDiscente" value="#{ bolsista.obj.discente.id }"/>
					 <h:inputText id="nomeDiscente" value="#{ bolsista.obj.discente.pessoa.nome }" readonly="#{bolsista.readOnly}" size="60"/>
			
					<ajax:autocomplete source="formCadBolsista:nomeDiscente" target="formCadBolsista:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
						parser="new ResponseXmlToHtmlListParser()" />
			
					<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					
					<span class="required"></span>

				</td>
			</tr>
			
			
			<tr>
				<th>Tipo de Bolsa:</th>
				<td>
				
					<c:if test="${bolsista.blockTipoBolsa}">
						<h:inputHidden value="#{bolsista.obj.tipoBolsa.id}"/>
						<b>PEC-G</b>
					</c:if>

					<c:if test="${not bolsista.blockTipoBolsa}">
						<h:selectOneMenu value="#{bolsista.obj.tipoBolsa.id}"  readonly="#{bolsista.blockTipoBolsa}" id="selectTipoBolsa">
							<f:selectItem itemLabel=">> Opções " itemValue="0"/>
							<f:selectItems value="#{tipoBolsaUfrn.allCombo}"/>
						</h:selectOneMenu>
						<span class="required"></span>
					</c:if>
					
				</td>
			</tr>
			
			<c:if test="${not bolsista.blockEntidade}">
				<tr>
					<th>Entidade Financiadora:</th>
					<td>
						<h:selectOneMenu value="#{bolsista.obj.entidadeFinanciadora.id}" readonly="#{bolsista.blockEntidade}" id="selectEntidadeFinanciadora">
							<f:selectItem itemLabel=">> Opções " itemValue="0"/>
							<f:selectItems value="#{entidadeFinanciadora.allCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>			
			</c:if>
			
			<tr>
				<th>Data de Inicio:</th>
				<td>
					<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" value="#{bolsista.obj.inicio}" 
					 readonly="#{bolsista.readOnly}" onkeypress="formataData(this,event)"  maxlength="10"/>
					 <span class="required"></span>
				</td>
			</tr>

			<tr>
				<th>Data de Fim:</th>
				<td>
					<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" value="#{bolsista.obj.fim}" 
					 readonly="#{bolsista.readOnly}" onkeypress="formataData(this,event)"  maxlength="10"/>
				</td>
			</tr>

			
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{bolsista.confirmButton}" action="#{bolsista.cadastrar}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{bolsista.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
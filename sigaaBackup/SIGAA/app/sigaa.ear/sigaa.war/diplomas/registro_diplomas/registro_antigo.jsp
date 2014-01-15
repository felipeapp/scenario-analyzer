<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
<h2> <ufrn:subSistema /> > Registro de Diplomas Individual </h2>

<h:form id="form">

<div class="descricaoOperacao">
<p><b>Caro Usuário,</b></p>
<p>Preencha o formulário abaixo com muita atenção, informando os valores atualmentenete registrados no Livro de Registro de Diplomas.</p>
<p>Valores incorretos implicarão na busca por registros errados, bem como na impressão de diplomas com valores incorretos.</p>
</div>

<table class="formulario" >
	<caption>Dados do Registro</caption>
	<tbody>
		<tr>
			<th>Discente:</th>
			<td>
				<h:outputText value="#{registroDiplomaIndividual.obj.discente.matriculaNome}"/>
			</td>
		</tr>
		<tr>
			<th class="required"> Livro: </th>
			<td>
				<h:outputText value="#{registroDiplomaIndividual.livro.titulo} - #{registroDiplomaIndividual.obj.discente.curso}"/>
			</td>
		</tr>
		<tr>
		<th class="required"> Folha: </th>
			<td>
				<a4j:region>
					<h:selectOneMenu id="folhaCombo" value="#{registroDiplomaIndividual.folha.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{registroDiplomaIndividual.folhasCombo}"/>
						<a4j:support event="onchange" reRender="registroCombo" />
					</h:selectOneMenu>
					<a4j:status>
		                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
			</td>
		</tr>
		<tr>
			<th class="required"> Ordem do Registro na Folha: </th>
			<td>
				<h:selectOneMenu value="#{registroDiplomaIndividual.obj.id}" id="registroCombo">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems value="#{registroDiplomaIndividual.registroCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th> Nº do Registro: </th>
			<td><h:inputText value="#{registroDiplomaIndividual.obj.numeroRegistro}" size="6" maxlength="6"  onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" id="numeroRegistro"/> </td>
		</tr>
		<tr>
			<th> Número do Protocolo: </th>
			<td ><h:inputText value="#{registroDiplomaIndividual.obj.processo}" onkeyup="formatarProtocolo(this, event);" size="22" maxlength="20" id="processo2" /><br>
				(ex.: 23077.001234/2003-98) Caso n&atilde;o saiba os digitos verificadores, informe 99
			</td>
		</tr>
		<tr>
			<th class="required">
				<c:choose>
				<c:when test="${registroDiplomaIndividual.obj.discente.graduacao}">Data da Colação:</c:when>
				<c:otherwise>Data da Homologação:</c:otherwise>
				</c:choose>
			</th>
			<td>
				<ufrn:format type="dia_mes_ano" valor="${registroDiplomaIndividual.obj.dataColacao}" />
			</td>
		</tr>
		<tr>
			<th class="required"> Data do Registro: </th>
			<td ><t:inputCalendar value="#{registroDiplomaIndividual.obj.dataRegistro}" size="10" maxlength="10"
				 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataRegistro" 
				 renderAsPopup="true" renderPopupButtonAsImage="true">
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required"> Data de Expedição: </th>
			<td ><t:inputCalendar value="#{registroDiplomaIndividual.obj.dataExpedicao}" size="10" maxlength="10"
				 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataExpedicao" 
				 renderAsPopup="true" renderPopupButtonAsImage="true">
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th valign="top">Observação: </th>
			<td>
				<h:inputTextarea value="#{registroDiplomaIndividual.observacao.observacao}" id="observacao" rows="4" cols="60" 
				  onkeyup="if (this.value.length > 600) this.value = this.value.substring(0, 600); $('form:observacao_count').value = 600 - this.value.length;" 
				onkeydown="if (this.value.length > 600) this.value = this.value.substring(0, 600); $('form:observacao_count').value = 600 - this.value.length;">
				</h:inputTextarea>
				<br/>
				Você pode digitar <h:inputText id="observacao_count" size="3" value="#{600 - fn:length(registroDiplomaIndividual.observacao.observacao)}" disabled="true" /> caracteres.
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{registroDiplomaIndividual.cadastrar}" value="#{registroDiplomaIndividual.confirmButton}" id="cadastrar"/>
				<h:commandButton action="#{registroDiplomaIndividual.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
<br>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena">Campos de preenchimento obrigatório. </span> 
</center>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
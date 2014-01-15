<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2> <ufrn:subSistema /> > Registro de ${registroDiplomaIndividual.obj.livroRegistroDiploma.tipoRegistroDescricao } Individual </h2>

<h:form id="form">

<table class="formulario" width="80%">
	<caption>Dados do Registro</caption>
	<tr>
		<th class="rotulo">Discente:</th>
		<td>
			<h:outputText value="#{registroDiplomaIndividual.obj.discente.matriculaNome}"/>
		</td>
	</tr>
	<tr>
		<th class="rotulo">Curso:</th>
		<td>
			<h:outputText value="#{registroDiplomaIndividual.obj.discente.curso}" />
		</td>
	</tr>
	<tr>
		<th class="rotulo">
			<c:choose>
				<c:when test="${acesso.graduacao}">Data de Colação</c:when>
				<c:when test="${acesso.stricto}">Data da Homologação:</c:when>
				<c:when test="${acesso.lato}">Data da Conclusão:</c:when>
			</c:choose>
		</th>
		<td>
			<ufrn:format type="dia_mes_ano" valor="${registroDiplomaIndividual.obj.dataColacao}" />
		</td>
	</tr>
	<tr>
		<th class="rotulo"> Livro: </th>
		<td>
			<h:outputText value="#{registroDiplomaIndividual.livro.titulo}"/>
		</td>
	</tr>
	<tr>
		<th class="${ impressaoDiploma.protocoloAtivo ? 'required' : ''}"> Número do Processo: </th>
		<td ><h:inputText value="#{registroDiplomaIndividual.obj.processo}" onkeyup="formatarProtocolo(this, event);" size="20" maxlength="20" id="processo"/><br>
			(ex.: 23077.001234/2003-98) Caso não saiba os dígitos verificadores, informe 99
		</td>
	</tr>
	<tr>
		<th class="required"> Data do Registro: </th>
		<td ><t:inputCalendar value="#{registroDiplomaIndividual.obj.dataRegistro}" size="10" maxlength="10"
			 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataRegistro" 
			 renderAsPopup="true" renderPopupButtonAsImage="true" >
			 	<f:converter converterId="convertData"/>
			</t:inputCalendar>
		</td>
	</tr>
	<tr>
		<th class="required"> Data de Expedição: </th>
		<td ><t:inputCalendar value="#{registroDiplomaIndividual.obj.dataExpedicao}" size="10" maxlength="10"
			 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataExpedicao" 
			 renderAsPopup="true" renderPopupButtonAsImage="true" >
			 	<f:converter converterId="convertData"/>
			</t:inputCalendar>
		</td>
	</tr>
	<c:if test="${registroDiplomaIndividual.obj.livroRegistroDiploma.livroAntigo}">
		<tr>
			<th> Nº do Registro: </th>
			<td><h:inputText id = "numeroRegistro" value="#{registroDiplomaIndividual.obj.numeroRegistro}" size="6" maxlength="6"/> </td>
		</tr>
	</c:if>
	<tr>
		<th valign="top">Observação: </th>
		<td>
			<h:inputTextarea value="#{registroDiplomaIndividual.observacao.observacao}" id="observacao" rows="4" cols="60" 
			  onkeyup="if (this.value.length > 200) this.value = this.value.substring(0, 200); $('form:observacao_count').value = 200 - this.value.length;" 
			onkeydown="if (this.value.length > 200) this.value = this.value.substring(0, 200); $('form:observacao_count').value = 200 - this.value.length;">
			</h:inputTextarea>
			<br/>
			Você pode digitar <h:inputText id="observacao_count" size="3" value="#{200 - fn:length(registroDiplomaIndividual.observacao.observacao)}" disabled="true" /> caracteres.
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{registroDiplomaIndividual.cadastrar}" value="#{registroDiplomaIndividual.confirmButton}" id="cadastrar"/>
				<h:commandButton action="#{registroDiplomaIndividual.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar" />
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
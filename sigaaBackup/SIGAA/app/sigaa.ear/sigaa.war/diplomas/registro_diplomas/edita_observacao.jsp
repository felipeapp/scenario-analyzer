<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

<h2> <ufrn:subSistema /> > Registro de ${buscaRegistroDiploma.obj.livroRegistroDiploma.tipoRegistroDescricao } Individual </h2>

<h:form id="form">

<table class="formulario" width="80%">
	<caption>Dados do Registro</caption>
	</tr>
		<th><b>Discente:</b></th>
		<td><h:outputText value="#{buscaRegistroDiploma.obj.discente.matriculaNome}"/></td>
	<tr>
	<tr>
		<th><b>Livro:</b></th>
		<td><h:outputText value="#{buscaRegistroDiploma.obj.livroRegistroDiploma.titulo}" /></td>
	</tr>
	<tr>
		<th><b>Número do Processo:</b></th>
		<td><h:outputText value="#{buscaRegistroDiploma.obj.processo}"/></td>
	</tr>
	<tr>
		<th><b>Data da Colação:</b></th>
		<td><ufrn:format type="dia_mes_ano" valor="${buscaRegistroDiploma.obj.dataColacao}" /></td>
	</tr>
	<tr>
		<th><b>Data do Registro:</b></th>
		<td><ufrn:format type="dia_mes_ano" valor="${buscaRegistroDiploma.obj.dataRegistro}" /></td>
	</tr>
	<tr>
		<th><b>Data de Expedição:</b></th>
		<td><ufrn:format type="dia_mes_ano" valor="${buscaRegistroDiploma.obj.dataExpedicao}"/></td>
	</tr>
	<tr>
		<th><b>Nº do Registro:</b></th>
		<td><h:outputText value="#{buscaRegistroDiploma.obj.numeroRegistro}" /> </td>
	</tr>
	<tr>
		<th class="required" valign="top">Incluir a Observação: </th>
		<td>
			<h:inputTextarea value="#{buscaRegistroDiploma.observacao.observacao}" id="observacao" rows="4" cols="60" 
			  onkeyup="if (this.value.length > 200) this.value = this.value.substring(0, 200); $('form:observacao_count').value = 200 - this.value.length;" 
			onkeydown="if (this.value.length > 200) this.value = this.value.substring(0, 200); $('form:observacao_count').value = 200 - this.value.length;">
			</h:inputTextarea>
			<br/>
			Você pode digitar <h:inputText id="observacao_count" size="3" value="#{200 - fn:length(buscaRegistroDiploma.observacao.observacao)}" disabled="true" /> caracteres.
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{buscaRegistroDiploma.atualizaObservacao}" value="#{buscaRegistroDiploma.confirmButton}" id="btnConfirmar"/>
				<h:commandButton action="#{buscaRegistroDiploma.buscar}" value="<< Escolher Outro Registro" id="btnBuscar"/>
				<h:commandButton action="#{buscaRegistroDiploma.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
<br>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena">Campos de preenchimento obrigatório. </span> 
</center>

<br/>
<c:if test="${not empty buscaRegistroDiploma.obj.observacoesAtivas}"></c:if>
<div class="infoAltRem">
	<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover a Observação
</div>
<table class="listagem">
	<caption>Lista de Observações deste Registro</caption>
	<thead>
		<tr><th style="text-align: center;">Data</th><th>Observação</th><th></th></tr>
	</thead>
	<tbody>
		<c:forEach items="#{buscaRegistroDiploma.obj.observacoesAtivas}" var="observacao" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td style="text-align: center;"><h:outputText value="#{observacao.adicionadoEm}"/></td>
			<td><h:outputText value="#{observacao.observacao}"/></td>
			<td>	
				<h:commandLink action="#{buscaRegistroDiploma.removerObservacao}" onclick="#{confirmDelete}" id="remover" title="Remover a Observação">
					<f:param name="id" value="#{observacao.id}"/>
					<h:graphicImage url="/img/delete.gif" />
				</h:commandLink>
			</td>
		</tr>
			
		</c:forEach>
	</tbody>
</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
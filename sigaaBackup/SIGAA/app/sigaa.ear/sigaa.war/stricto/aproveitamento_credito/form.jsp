<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{aproveitamentoCredito.create}" />
	<h2 class="title"><ufrn:subSistema /> > Aproveitamento Crédito</h2>

	<c:set var="discente" value="#{aproveitamentoCredito.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class="formulario" width="70%">
			<caption class="listagem">Dados do Aproveitamento de Crédito</caption>
			<tr>
				<th class="obrigatorio">Créditos:</th>
				<td><h:inputText value="#{aproveitamentoCredito.obj.creditos}" size="2" maxlength="2" onkeyup="formatarInteiro(this)"/></td>
			</tr>
			<tr>
				<th>Observação:</th>
				<td><h:inputTextarea value="#{aproveitamentoCredito.obj.observacao}" rows="2" cols="70"/></td>
			</tr>
		
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request"/>
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Escolher Outro Discente" id="escolherOutro"	action="#{aproveitamentoCredito.buscarDiscente}" />
						<h:commandButton value="Lançar Aproveitamento de Crédito" id="confirm" action="#{aproveitamentoCredito.cadastrar}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{aproveitamentoCredito.cancelar}" />
					</td>
				</tr>
			</tfoot>
	</table>
		
	</h:form>

	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br><br>
	</center>
	
	<c:if test="${not empty aproveitamentoCredito.historicoAproveitamento}">
	<table class="subFormulario" style="width: 100%">
		<caption>Histórico de Aproveitamento de Créditos do Discente</caption>
		<thead>
			<td>Total de Créditos</td>
			<td>Observação</td>
			<td>Usuário</td>
			<td width="15%">Data</td>
		</thead>
		<tbody>
		<c:forEach items="${aproveitamentoCredito.historicoAproveitamento}" var="aprov" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td>${aprov.creditos}</td>
				<td><i>${aprov.observacao}</i></td>
				<td>${aprov.registroEntrada.usuario.pessoa.nome}</td>
				<td><ufrn:format type="dataHora" valor="${aprov.dataCadastro}" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</c:if>
	<br><br>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

</f:view>
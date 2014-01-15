<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>
	<h:messages/>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Validação de Propostas de Ações Acadêmicas</h2>

	<h:outputText value="#{autorizacaoDepartamento.create}"/>
	<h:outputText value="#{atividadeExtensao.create}"/>	


	<div class="descricaoOperacao">
		<b>Atenção:</b> selecione uma Unidade Proponente e clique em buscar para que sejam listadas 
		todas as propostas de ações de acadêmicas pendentes de validação na unidade selecionada. 
	</div>


<h:form id="form">
	<table class="formulario" width="90%">
			<caption>Busca por Autorizações</caption>
			<tbody>
				<tr>
			    	<th width="20%" class="required"> Unidade Proponente: </th>
			    	<td colspan="2">
						<h:selectOneMenu id="buscaUnidade" value="#{autorizacaoDepartamento.unidade.id}" style="width:90%" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}"/>
						</h:selectOneMenu>
			    	 </td>
			    </tr>
			</tbody>
			<tfoot>			    
			    <tr>
			    	<td align="center" colspan="3">
			    		<h:commandButton value="Buscar" action="#{autorizacaoDepartamento.listarAutorizacoesAcoes}" id="bt_buscar_autorizacoes"/>
			    		<h:commandButton value="Cancelar" action="#{autorizacaoDepartamento.cancelar}" id="bt_cancelar" onclick="#{confirm}" /> 
			    	</td>
			    </tr>
			</tfoot>
	</table>
	<br/>

    <c:if test="${not empty autorizacaoDepartamento.autorizacoes && autorizacaoDepartamento.unidade.id != 0}">
		<div class="infoAltRem">
	   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="leg_veiw"/>: Visualizar Proposta
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" id="leg_seta"/>: Analisar Proposta
		</div>

		<table class="listagem tablesorter" id="listagem">
			<caption>Lista de todas as autorizações para a unidade selecionada</caption>
		    <thead>
				<tr>
					<th>Ano </th>
					<th width="60%">Título </th>
					<th>Analisado Em </th>
					<th>Autorizado </th>
					<th></th>			
					<th></th>			
					<th></th>
				</tr>
			</thead>
			
				<c:forEach items="#{autorizacaoDepartamento.autorizacoes}" var="auto" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
						<td> ${auto.atividade.ano} </td>
						<td> ${auto.atividade.titulo} </td>
						<td> <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${auto.dataAutorizacao}" /> <c:if test="${empty auto.dataAutorizacao}"> <font color="red">NÃO ANALISADA</font></c:if> </td>
						<td> <c:if test="${not empty auto.dataAutorizacao}"> ${auto.autorizado ? 'SIM':'NÃO'} </c:if></td>	
						<td>				
							<h:commandLink title="Visualizar Proposta" action="#{atividadeExtensao.view}" style="border: 0;" id="view_acao_">
								        <f:param name="id" value="#{auto.atividade.id}"/>
				               			<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						</td>	
						<td>	
							<h:commandLink title="Analisar Proposta" action="#{autorizacaoDepartamento.escolherAutorizacao}" 
								style="border: 0;" rendered="#{empty auto.dataAutorizacao}" id="selecionar_autorizacao_">
							        <f:param name="id_autorizacao" value="#{auto.id}"/>
			                		<h:graphicImage url="/img/seta.gif"/>
							</h:commandLink>
						</td>				
					</tr>
				</c:forEach>
		</table>
	</c:if>			
	<c:if test="${empty autorizacaoDepartamento.autorizacoes && autorizacaoDepartamento.unidade.id != 0}">            
			<center><i>Unidade selecionada não possui Autorizações<br/></i></center>
	</c:if>
	<rich:jQuery selector="#listagem" query="tablesorter( {headers: {4: { sorter: false },5: { sorter: false },6: { sorter: false } } });" timing="onload" />
</h:form>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
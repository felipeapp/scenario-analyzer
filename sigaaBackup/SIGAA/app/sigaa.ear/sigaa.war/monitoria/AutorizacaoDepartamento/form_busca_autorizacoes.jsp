<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

	<f:view>
	<h:messages/>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Autorização de Propostas de Projetos de Ensino</h2>

	<h:outputText value="#{autorizacaoProjetoMonitoria.create}"/>
	<h:outputText value="#{atividadeExtensao.create}"/>	

	<h:form id="form">

	<table class="formulario" width="90%">
			<caption>Busca por Autorizações</caption>
			<tbody>
		
				<tr>
			    	<th width="10%"> Unidade: </th>
			    	<td colspan="2">
						<h:selectOneMenu id="buscaUnidade" value="#{autorizacaoProjetoMonitoria.unidade.id}" style="width:95%" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM UNIDADE --"/>
							<f:selectItems value="#{unidade.allDetentorasComponentesCombo}"/>
						</h:selectOneMenu>
			    	 </td>
			    </tr>
			    <tfoot>
			    	<tr>
			    		<td align="center" colspan="3"><h:commandButton value="Buscar" action="#{autorizacaoProjetoMonitoria.listarAutorizacoes}"/> </td>
			    	</tr>
			    </tfoot>	
			</tbody>
	</table>
		
	<br/>
	<br/>

	<div class="infoAltRem">
   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="leg_veiw"/>: Visualizar Proposta
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" id="leg_seta"/>: Analisar Proposta
	</div>

	<table class="listagem">
	<caption>Lista de todas as autorizações para a unidade selecionada</caption>
    <thead>
		<tr>
			<th>Ano </th>
			<th>Título </th>
			<th>Analisado Em </th>
			<th>Autorizado </th>
			<th></th>			
			<th></th>			
			<th></th>
		</tr>
	</thead>
	
	<c:if test="${not empty autorizacaoProjetoMonitoria.autorizacoes}">
		<c:forEach items="#{autorizacaoProjetoMonitoria.autorizacoes}" var="auto" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			
				<td> ${auto.projetoEnsino.ano} </td>
				<td width="60%"> ${auto.projetoEnsino.titulo} </td>
				<td> <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${auto.dataAutorizacao}" /> <c:if test="${empty auto.dataAutorizacao}"> <font color="red">NÃO ANALISADA</font></c:if> </td>
				<td> <c:if test="${not empty auto.dataAutorizacao}"> ${auto.autorizado ? 'SIM':'NÃO'} </c:if></td>	
				<td>				
					<h:commandLink title="Visualizar Proposta" action="#{projetoMonitoria.view}" style="border: 0;" id="view_acao_">
						        <f:param name="id" value="#{auto.projetoEnsino.id}"/>
		               			<h:graphicImage url="/img/view.gif"/>
					</h:commandLink>
				</td>	
				<td>	
					<h:commandLink title="Analisar Proposta" action="#{autorizacaoProjetoMonitoria.escolherAutorizacao}" 
						style="border: 0;" rendered="#{empty auto.dataAutorizacao}" id="selecionar_autorizacao_">
					        <f:param name="idAutorizacao" value="#{auto.id}"/>
					        <f:param name="paginaParaVoltar" value="busca"/>
	                		<h:graphicImage url="/img/seta.gif"/>
					</h:commandLink>
				</td>				
			</tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${empty autorizacaoProjetoMonitoria.autorizacoes}">            
			<tr>
				<td colspan="4"><center><font color="red">Unidade selecionada não possui Autorizações<br/></font></center></td>
			</tr>
	</c:if>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
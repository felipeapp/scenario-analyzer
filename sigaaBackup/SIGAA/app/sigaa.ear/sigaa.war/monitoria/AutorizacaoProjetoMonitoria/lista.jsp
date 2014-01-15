<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	
	<h2><ufrn:subSistema /> > Autoriza��o de Propostas de Projeto de Ensino</h2>


	<h:outputText value="#{autorizacaoProjetoMonitoria.create}"/>
	<h:outputText value="#{projetoMonitoria.create}"/>	

	<div class="infoAltRem">
   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Analisar Projeto
	    <h:graphicImage value="/img/extensao/printer.png" style="overflow: visible;" id="leg_recibo"/>: Re-imprimir Recibo
	</div>


	<h:form>
	<table class="listagem">
	<caption>Lista de Todos os Projetos de Ensino Aguardando sua Autoriza��o</caption>
    <thead>
		<tr>
			<th>Ano </th>
			<th>T�tulo </th>
			<th style="text-align: center;"> Analisado Em </th>
			<th> Autorizado </th>
			<th></th>
			<th></th>
			<th></th>			
		</tr>
	</thead>
	
	<c:set value="${autorizacaoProjetoMonitoria.autorizacoesNaoAnalisadas}" var="autorizacoes"/>
	
	<c:if test="${not empty autorizacoes}">
		<c:forEach items="#{autorizacaoProjetoMonitoria.autorizacoes}" var="autorizacao" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			
				<td> ${autorizacao.projetoEnsino.ano} </td>
				<td width="60%"> ${autorizacao.projetoEnsino.titulo} </td>
				<td style="text-align: center;"> <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${autorizacao.dataAutorizacao}" /> <c:if test="${empty autorizacao.dataAutorizacao}"> <font color="red">N�O ANALISADO</font></c:if> </td>
				<td> 
					<c:if test="${not empty autorizacao.dataAutorizacao}"> ${autorizacao.autorizado ? 'SIM':'N�O'} </c:if>
				</td>
				<td width="2%">
					<h:commandLink  action="#{projetoMonitoria.view}" title="Visualizar Projeto" style="border: 0;">
						       <f:param name="id" value="#{autorizacao.projetoEnsino.id}"/>
						       <h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</td>
				<td width="2%">
						<h:commandLink action="#{autorizacaoProjetoMonitoria.escolherAutorizacao}" 
							style="border: 0;" rendered="#{(empty autorizacao.dataAutorizacao)}" title="Analisar Projeto" id="selecionar_autorizacao_">
						       <f:param name="idAutorizacao" value="#{autorizacao.id}"/>
						       <f:param name="paginaParaVoltar" value="chefe"/>						       
						       <h:graphicImage url="/img/seta.gif" />
						</h:commandLink>						
				</td>
				
				<td>								               
					<h:commandLink action="#{autorizacaoProjetoMonitoria.reciboProjetoMonitoria}" title="Re-imprimir Recibo " style="border: 0;" id="reciboProjetoMonitoria" rendered="#{(not empty autorizacao.dataAutorizacao)}">
					   <f:param name="idAutorizacao" value="#{autorizacao.id}"/>					   
				       <h:graphicImage url="/img/extensao/printer.png" id="image_recibo_"/>
					</h:commandLink>
				</td>
						
			</tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${empty autorizacoes}">            
			<tr>
				<td colspan="4"><center><font color="red">O usu�rio atual n�o possui Projetos pendentes de Autoriza��o<br/></font> </center></td>
			</tr>
	</c:if>
			
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
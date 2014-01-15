<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Autorização para Solicitações de Reconsiderações para Requisitos Formais da Proposta de Projeto</h2>

	<div class="infoAltRem">
   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Analisar Projeto
	</div>


	<h:form id="form">	
	<table class="listagem">
	<caption>Lista de todos os Projetos de Ensino aguardando sua Autorização!</caption>
    <thead>
		<tr>
			<th> Título do Projeto</th>
			<th> Analisado Em </th>
			<th> Autorizado </th>
			<th>  </th>			
		</tr>
	</thead>
	
	<c:if test="${not empty autorizacaoReconsideracao.autorizacoes}">
		<c:forEach items="#{autorizacaoReconsideracao.autorizacoes}" var="autorizacao" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			
				<td width="60%"> ${autorizacao.projetoEnsino.anoTitulo} </td>
				<td> <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${autorizacao.dataAutorizacao}" /> <c:if test="${empty autorizacao.dataAutorizacao}"> <font color="red">NÃO ANALISADO</font></c:if> </td>
				<td> <c:if test="${not empty autorizacao.dataAutorizacao}"> ${autorizacao.autorizado ? 'Sim':'Não'} </c:if></td>

				<td width="2%">					
					<h:commandLink action="#{projetoMonitoria.view}" style="border: 0;">
						       <f:param name="id" value="#{autorizacao.projetoEnsino.id}"/>
						       <h:graphicImage url="/img/view.gif" />
					</h:commandLink>
					
				</td>
	
				<td width="2%">
					<c:if test="${empty autorizacao.dataAutorizacao}">
						<h:commandLink  action="#{autorizacaoReconsideracao.escolherAutorizacao}" style="border: 0;">
						       <f:param name="idAutorizacao" value="#{autorizacao.id}"/>
						       <f:param name="avaliar" value="true"/>						       
						       <h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
					</c:if>
				</td>
					
			</tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${empty autorizacaoReconsideracao.autorizacoes}">            
			<tr>
				<td colspan="4"><center><font color="red">O usuário atual não possui Projetos pendentes de Autorização<br/></font> </center></td>
			</tr>
	</c:if>
			
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
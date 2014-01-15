<%@include file="/mobile/commons/cabecalho.jsp"%>

<f:view>

	<a href="http://wap.ufrn.br/">Menu Principal</a> <br/><br/>

	Pesquisar Disciplinas: <br/><br/>
	<h:form>
	
	Cod. Disc.:<h:inputText value="#{consultaServPubMobileMBean.codigoDisciplina}" maxlength="10" size="10" /> <br/>
	
	<h:commandButton value="Pesquisar" 
		action="#{consultaServPubMobileMBean.consultarComponentesCurric}" 
			style="width: 150px;  background-color: #EFF3FA"/> <br/>
			
	</h:form>
	
	<c:if test="${not empty consultaServPubMobileMBean.listaCC}">
		
		<hr/>
	
		<c:forEach items="#{consultaServPubMobileMBean.listaCC}" var="item">
			${item.detalhes.nome} - ${item.detalhes.chAula} horas.<br/><br/>
			<strong>Ementa: </strong><br/> ${item.detalhes.ementa} <br/><br/>
			
			<strong> Pre-requisito: </strong> ${item.detalhes.preRequisito} <br/><br/>
			<strong> Co-requisito: </strong> ${item.detalhes.coRequisito} <br/><br/>
			<strong> Equivalencia: </strong> ${item.detalhes.equivalencia} <br/><br/>
	
		</c:forEach>
	</c:if>
	
	
	
	<br/>
	<c:if test="${empty consultaServPubMobileMBean.listaCC}">
		Nenhum registro
	</c:if>
	<br/>
	
</f:view>
<%@include file="/mobile/commons/rodape.jsp"%>
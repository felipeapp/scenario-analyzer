<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Periódico</h2>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			 <h:commandLink action="#{tipoPeriodico.preCadastrar}"
			 value="Cadastrar"></h:commandLink>
		</div>
	</h:form>
	<h:outputText value="#{tipoPeriodico.create}" />
<c:set var="lista" value="${tipoPeriodico.allAtivos}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Periódico Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	
	
	
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Periódico</caption>
		<thead>
			<td>Descrição</td>
		</thead>
		<c:forEach items="${tipoPeriodico.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td style="width: 50%">${item.descricao}</td>
			</tr>
		</c:forEach>
	</table>
</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Região</h2>

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			 <h:commandLink action="#{tipoRegiao.preCadastrar}"
			 value="Cadastrar"></h:commandLink>
		</div>
	</h:form>
	<h:outputText value="#{tipoRegiao.create}" />
<c:set var="lista" value="${tipoRegiao.allAtivos}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Região Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	
	
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Regiões</caption>
		<thead>
			<td>Descrição</td>
		</thead>
		<c:forEach items="${tipoRegiao.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
			</tr>
		</c:forEach>
	</table>
</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
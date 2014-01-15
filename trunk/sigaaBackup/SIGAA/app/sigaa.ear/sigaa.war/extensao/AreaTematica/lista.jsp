	<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Cadastro de �rea Tem�tica </h2>

<h:outputText value="#{areaTematica.create}"/>
	
<center>
		<h:messages/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar dados da �rea Tem�tica
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover �rea Tem�tica<br/>
		</div>
</center>

<h:form>
	<table class=listagem>
	<caption class="listagem"> Lista de �rea Tem�ticas</caption>
	<thead>
	<tr>
		<th> Descri��o</th>
		<th colspan="2"></th>
	</tr>
	</thead>
	<c:forEach items="#{areaTematica.all}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						
						<td>${item.descricao}</td>
						<td  width="5%">
									<h:commandLink title="Alterar" action="#{areaTematica.atualizar}">
									         <f:param name="id" value="#{item.id}"/>
				                   			<h:graphicImage url="/img/alterar.gif" />
									</h:commandLink>
									
									<h:commandLink title="Remover" action="#{areaTematica.preRemover}">
						                   <f:param name="id" value="#{item.id}"/>
						                   <h:graphicImage url="/img/delete.gif" />
									</h:commandLink>									
						</td>
				</tr>
	</c:forEach>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
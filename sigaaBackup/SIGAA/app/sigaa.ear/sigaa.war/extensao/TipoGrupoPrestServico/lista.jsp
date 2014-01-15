<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Tipo de Grupo de Prestação de Serviço</h2>

<h:outputText value="#{tipoGrupoPrestServico.create}"/>

<center>
		<h:messages/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar dados do Tipo de Grupo de Prestação de Serviço
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Grupo de Prestação de Serviço<br/>
		</div>

</center>

<h:form>
<table class=listagem>
		<caption class="listagem"> Lista de Tipo de Grupo de Prestação de Serviço</caption>
		<thead>
			<tr>
				<th> Descrição</th>
				<th> Ajuda</th>
				<th></th>
			</tr>
		</thead>
		
		<c:forEach items="#{tipoGrupoPrestServico.all}" var="item"> varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${item.descricao}</td>
			<td>${item.ajuda}</td>
			<td  width="5%">
	
						<h:commandLink title="Alterar" action="#{tipoGrupoPrestServico.atualizar}" style="border: 0;">
					       <f:param name="id" value="#{item.id}"/>
			               <h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
				
						<h:commandLink title="Remover" action="#{tipoGrupoPrestServico.preRemover}" style="border: 0;">
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
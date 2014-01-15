<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Local de  Realiza��o</h2>

<h:outputText value="#{localRealizacao.create}"/>

<center>
		<h:messages/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar dados do Local de Realiza��o
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Local de Realiza��o<br/>
		</div>

</center>

<h:form>
<table class=listagem>
		<caption class="listagem"> Lista de Local de Realizac�es</caption>
		<thead>
		<tr>
			<th> Descri��o</th>
			<th> Munic�pio</th>
			<th></th>
		</tr>
		</thead>
		<c:forEach items="${localRealizacao.all}" var="item" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.municipio}</td>
				<td  width="5%">
												
							<h:commandLink title="Alterar" action="#{localRealizacao.atualizar}" style="border: 0;">
							         <f:param name="id" value="#{item.id}"/>
		                   			<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
							
							<h:commandLink title="Remover" action="#{localRealizacao.preRemover}" style="border: 0;">
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
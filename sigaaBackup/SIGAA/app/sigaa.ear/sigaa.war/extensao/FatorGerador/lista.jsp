<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Fator Gerador</h2>

<h:outputText value="#{fatorGerador.create}"/>
		
<center>
		<h:messages/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar dados do Fator Gerador
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Fator Gerador<br/>
		</div>

</center>


<h:form>
<table class=listagem>
		<caption class="listagem"> Lista de Fator Gerador</caption>
		<thead>
		<tr>
			<th> Descrição</th>
			<th></th>
		</tr>
		</thead>
		
		<c:forEach items="#{fatorGerador.all}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.descricao}</td>
						<td  width="5%">
										
								<h:commandLink title="Alterar" action="#{fatorGerador.atualizar}" style="border: 0;">
								         <f:param name="id" value="#{item.id}"/>
			                   			<h:graphicImage url="/img/alterar.gif" />
								</h:commandLink>
								
								<h:commandLink title="Remover" action="#{fatorGerador.preRemover}" style="border: 0;">
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
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@include file="/portais/discente/menu_discente.jsp"%>
<h2><ufrn:subSistema /> > Meus Requerimentos</h2>

	
<h:form>

	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Requerimento		
		    <h:graphicImage value="/img/edit.png" style="overflow: visible;" />: Editar Requerimento    		    
	</div>

		<table class=listagem>
			<caption class="listagem"> Lista de Requerimentos do Discente</caption>
			<thead>
				<tr>
					<th>Status do Requerimento</th>
					<th>Tipo do Requerimento</th>
					<th>Data</th>
					<th></th>					
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{requerimento.listaRequerimentos}" var="req" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${req.status.descricao}</td>
						<td>${fn:toUpperCase(req.tipo.descricao)}</td>
						<td>
							<fmt:formatDate value="${req.dataAtualizado}" pattern="dd/MM/yyyy 'às' HH:mm:ss"/>
						<td>
							<h:commandLink action="#{requerimento.exibirRequerimento}" style="border: 0;" title="Visualizar Requerimento" rendered="#{req.alunoEnviou}">
								<f:param name="idRequerimento" value="#{req.id}"/>
								<h:graphicImage url="/img/view.gif" />
							</h:commandLink>													
						
							<h:commandLink action="#{requerimento.abrirRequerimento}" style="border: 0;" title="Editar Requerimento" rendered="#{!req.alunoEnviou}">
								<f:param name="idRequerimento" value="#{req.id}"/>
								<h:graphicImage url="/img/edit.png" />
							</h:commandLink>							
						</td>
					</tr>
				</c:forEach>
			</tbody>	
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
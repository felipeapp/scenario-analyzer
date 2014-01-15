<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Lista de Planos de Trabalho da Ação Selecionada</h2>

<h:outputText value="#{planoTrabalhoExtensao.create}"/>
	
	<div class="descricaoOperacao">
		<b>Tipos de Vínculo</b><br/>
		<ul>
			<li>Bolsista FAEx:</b> bolsista mantido com recursos concedidos pelo FAEx.</li>
			<li>Bolsista Externo:</b> bolsista mantido com recursos de outros orgãos. CNPq, Petrobrás, Ministério da Saúde, etc.</li>
			<li>Voluntário:</b> são membros da equipe da ação de extensão que não são remunerados.</li>
			<li>Atividade Curricular:</b> são discentes não remunerados que fazem parte da equipe da ação de extensão.</li>
		<ul>				
	</div>
	
<h:form>

	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Plano de Trabalho	    		    
	</div>

		<table class=listagem>
				<caption class="listagem"> Lista de Planos Cadastrados</caption>
				<thead>
						<tr>
							<th>Discente</th>
							<th>Vínculo</th>
							<th>Situação</th>
							<th>Início do Plano</th>
							<th>Fim do Plano</th>							
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:set var="atividade" value=""/>
						<c:forEach items="#{planoTrabalhoExtensao.obj.atividade.planosTrabalho}" var="item"varStatus="status">
						
							<c:if test="${ atividade != item.atividade.id }">
								<c:set var="atividade" value="${ item.atividade.id }"/>
								<tr>
									<td colspan="6" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">${item.atividade.anoTitulo}</td>
								</tr>
							</c:if>
						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${(item.discenteExtensao.discente == null)?'<font color=red><i> DISCENTE NÃO DEFINIDO </i></font>': item.discenteExtensao.discente.nome}</td>
									<td>${item.discenteExtensao.tipoVinculo.descricao}</td>
   									<td>${item.discenteExtensao.situacaoDiscenteExtensao.descricao}</td>
									<td><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></td>
									<td><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>
									<td  width="2%">
										<h:commandLink action="#{planoTrabalhoExtensao.view}" style="border: 0;" immediate="true">
									       <f:param name="id" value="#{item.id}"/>
							               <h:graphicImage url="/img/view.gif" />
										</h:commandLink>
									</td>
							</tr>
			 		   </c:forEach>			 		   
			 		   <c:if test="${empty planoTrabalhoExtensao.obj.atividade.planosTrabalho}" >
			 		   		<tr><td colspan="5" align="center"><font color="red">Não há planos de trabalhos de cadastrados!</font></td></tr>
			 		   </c:if>			 		   
					</tbody>	
			 		   
					<tfoot>
						<tr>
							<td colspan="6" align="center">
								<input type="button" value="<< Voltar" onclick="javascript:history.back();" />
							</td>
						</tr>
					</tfoot>
					
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@include file="/portais/discente/menu_discente.jsp"%>
<h2><ufrn:subSistema /> > Minhas Ações como Membro da Equipe</h2>


	
<h:form>

	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação de Extensão		    		    		    
	</div>

		<table class="listagem">
				<caption class="listagem"> Lista de Ações como Membro da Equipe</caption>
					<thead>
						<tr>
							<th>Código</th>
							<th>Título</th>							
							<th>Data Cadastro</th>					
							<th></th>
						</tr>
					</thead>
					<tbody>					
						
						<c:forEach items="#{atividadeExtensao.atividadesMembroParticipa}" var="atividade" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td width="10%">${atividade.codigo}</td>
									<td>${atividade.titulo}</td>									
									<td width="20%">&nbsp;&nbsp;&nbsp;<fmt:formatDate value="${atividade.dataCadastro}" pattern="dd/MM/yyyy" /></td>									
									<td>
											<h:commandLink action="#{atividadeExtensao.view}" style="border: 0;" title="Visualizar Ação">
										       <f:param name="id" value="#{atividade.id}"/>
								               <h:graphicImage url="/img/view.gif" />
											</h:commandLink>
									</td>
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty atividadeExtensao.atividadesMembroParticipa}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">Não há Ações cadastradas para o usuário atual!</font></td></tr>
			 		   </c:if>			 		   
					</tbody>						
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
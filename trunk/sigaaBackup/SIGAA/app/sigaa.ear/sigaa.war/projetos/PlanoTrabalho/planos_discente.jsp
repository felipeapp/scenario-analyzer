<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@include file="/portais/discente/menu_discente.jsp"%>
<h2><ufrn:subSistema /> > Meus Planos de Trabalho</h2>

<h:form id="form">

	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação
			<h:graphicImage value="/img/extensao/businessman_view.png" style="overflow: visible;"/>: Visualizar Discente
		    <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Visualizar Plano		    		    
	</div>
	
		<table class="listagem">
			<caption class="listagem">Lista de participações como discente das Ações Ações Associadas (Planos de Trabalho)</caption>
			<thead>
				<tr>
					<th width="50%">Ação</th>
					<th>Vínculo</th>
					<th style="text-align: center;">Início</th>
					<th style="text-align: center;">Fim</th>
					<th>Situação</th>
					<th></th>
					<th></th>							
					<th></th>
				</tr>
			</thead>
			<tbody>
						
					<c:forEach items="#{ planoTrabalhoProjeto.discentesProjeto }" var="item" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${item.projeto.anoTitulo}</td>
									<td>${item.tipoVinculo.descricao}</td>
									<td style="text-align: center;"><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></td>
									<td style="text-align: center;"><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>
									<td>${item.situacaoDiscenteProjeto.descricao}</td>
									<td width="2%">
										<h:commandLink id="cmdLnkVisualizar" title="Visualizar Ação" action="#{ projetoBase.view }">
										    <f:param name="id" value="#{item.projeto.id}"/>
						               		<h:graphicImage url="/img/view.gif" />
										</h:commandLink>
									</td>									
									<td width="2%">								               
										<h:commandLink action="#{ discenteProjetoBean.view }" style="border: 0;" id="viewDiscenteProjeto" title="Visualizar Discente">
										   <f:param name="idDiscenteProjeto" value="#{item.id}"/>
								           <h:graphicImage url="/img/extensao/businessman_view.png" />
										</h:commandLink>
									</td>
									<td  width="2%">
											<h:commandLink action="#{planoTrabalhoProjeto.view}" style="border: 0;" title="Visualizar Plano" id="viewPlanoTrabalho">
										       <f:param name="id" value="#{item.id}"/>
								               <h:graphicImage url="/img/report.png" />
											</h:commandLink>
									</td>
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty planoTrabalhoProjeto.discentesProjeto}" >
			 		   		<tr><td colspan="7" align="center"><font color="red">Usuário atual não participa ou participou como discente de Ações Associadas.</font></td></tr>
			 		   </c:if>
			 		   
				</tbody>	
					
		</table>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
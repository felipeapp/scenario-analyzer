<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@include file="/portais/discente/menu_discente.jsp"%>
<h2><ufrn:subSistema /> > Meus Planos de Trabalho</h2>

<h:outputText value="#{planoTrabalhoExtensao.create}"/>
	
<h:form>

	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar A��o
			<h:graphicImage value="/img/extensao/businessman_view.png" style="overflow: visible;"/>: Visualizar discente
		    <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Visualizar Plano		    		    
	</div>
	
		<table class="listagem">
			<caption class="listagem">Lista de participa��es como discente das A��es de Extens�o (Planos de Trabalho)</caption>
			<thead>
				<tr>
					<th width="50%">A��o de Extens�o</th>
					<th>V�nculo</th>
					<th style="text-align: center;">In�cio</th>
					<th style="text-align: center;">Fim</th>
					<th>Situa��o</th>
					<th></th>
					<th></th>							
					<th></th>
				</tr>
			</thead>
			<tbody>
						
					<c:forEach items="#{planoTrabalhoExtensao.discentesExtensao}" var="item" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${item.atividade.codigoTitulo}</td>
									<td>${item.tipoVinculo.descricao}</td>
									<td style="text-align: center;"><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy" /></td>
									<td style="text-align: center;"><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy" /></td>
									<td>${item.situacaoDiscenteExtensao.descricao}</td>
									<td width="2%">
										<h:commandLink id="cmdLnkVisualizar" title="Visualizar A��o de Extens�o" action="#{ atividadeExtensao.view }">
										    <f:param name="id" value="#{item.atividade.id}"/>
						               		<h:graphicImage url="/img/view.gif" alt="Visualizar A��o de Extens�o"/>
										</h:commandLink>
									</td>									
									<td width="2%">								               
										<h:commandLink action="#{discenteExtensao.view}" style="border: 0;" id="view_discente_extensao_">
										   <f:param name="idDiscenteExtensao" value="#{item.id}"/>
								           <h:graphicImage url="/img/extensao/businessman_view.png" alt="Visualizar Discente"/>
										</h:commandLink>
									</td>
									<td  width="2%">
											<h:commandLink action="#{planoTrabalhoExtensao.view}" style="border: 0;" title="Visualizar Plano de Trabalho" rendered="#{not empty item.planoTrabalhoExtensao}" immediate="true">
										       <f:param name="id" value="#{item.planoTrabalhoExtensao.id}" />
								               <h:graphicImage url="/img/report.png" alt="Visualizar Plano de Trabalho"/>
											</h:commandLink>
									</td>
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty planoTrabalhoExtensao.discentesExtensao}" >
			 		   		<tr><td colspan="7" align="center"><font color="red">Usu�rio atual n�o participa ou participou como discente de a��es de extens�o.</font></td></tr>
			 		   </c:if>
			 		   
				</tbody>	
					
		</table>
		
		
		
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
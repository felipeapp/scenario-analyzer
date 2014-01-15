<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > Relatórios de Ações Acadêmicas Integradas</h2>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Visualizar Projeto
	    <h:graphicImage value="/img/extensao/form_green.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar Relatório	    
	    <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;" id="leg_devolver"/>: Devolver Relatório
	    <h:graphicImage value="/img/seta.gif"style="overflow: visible;" styleClass="noborder"/>: Analisar Relatório
	</div>

<a4j:keepAlive beanName="validacaoRelatorioBean" />
<h:form id="form">
		<table class="listagem">
				<caption class="listagem"> Relatórios de Ações Acadêmicas Integradas(${fn:length(validacaoRelatorioBean.relatoriosUnidade)})</caption>
					<thead>
						<tr>
							<th>Ano</th>
							<th width="40%">Título</th>
							<th>Tipo</th>
							<th>Analisado em</th>
							<th></th>
							<th></th>
							<th></th>							
							<th></th>
						</tr>
					</thead>
						
						<c:forEach items="#{validacaoRelatorioBean.relatoriosUnidade}" var="item" varStatus="status">
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${item.projeto.ano}</td>
									<td>${item.projeto.titulo}</td>
									<td>${item.tipoRelatorio.descricao}</td>
									<td><fmt:formatDate value="${item.dataValidacaoComite}" pattern="dd/MM/yyyy" /> <font color='red'>${item.dataValidacaoComite == null ? 'NÃO ANALISADO': ''}</font></td>
									
									<td  width="2%">											
											<h:commandLink title="Visualizar Projeto" 
											action="#{projetoBase.view}" style="border: 0;">											
										       <f:param name="id" value="#{item.projeto.id}"/>
								               <h:graphicImage url="/img/view.gif" />
											</h:commandLink>
									</td>
									
									<td  width="2%">											
											<h:commandLink title="Visualizar Relatório" 
											action="#{relatorioAcaoAssociada.view}" style="border: 0;">											
										       <f:param name="id" value="#{item.id}"/>
								               <h:graphicImage url="/img/extensao/form_green.png" />
											</h:commandLink>
									</td>

									<td  width="2%">											
											<h:commandLink title="Devolver Relatório"
											action="#{validacaoRelatorioBean.devolverRelatorio}" style="border: 0;"	
											rendered="#{!item.editavel}"
											onclick="return confirm('Tem certeza que deseja Devolver este Relatório para Coordenação do Projeto?');" id="devolver_relatorio_coord_">
										       <f:param name="id" value="#{item.id}"/>
								               <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;"/>
											</h:commandLink>
									</td>

									<td  width="2%">											
											<h:commandLink title="Analisar Relatório"
											action="#{validacaoRelatorioBean.analisarPorComite}" style="border: 0;"
											rendered="#{item.comitePodeAnalisar}">
										       <f:param name="id" value="#{item.id}"/>
								               <h:graphicImage url="/img/seta.gif" />
											</h:commandLink>
									</td>
									
																		
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty validacaoRelatorioBean.relatoriosUnidade}">
				 		   <tr>
				 		   		<td colspan="7"><center><font color="red">Não há relatórios de projetos submetidos pendentes de validação.</font></center></td>
				 		   </tr>
			 		   </c:if>

		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
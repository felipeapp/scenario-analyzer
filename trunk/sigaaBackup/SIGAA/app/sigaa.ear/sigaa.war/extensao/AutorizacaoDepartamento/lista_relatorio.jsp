<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > Relatórios de Projetos do Departamento</h2>


<h:outputText value="#{autorizacaoDepartamento.create}"/>
<h:outputText value="#{atividadeExtensao.create}"/>


	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Visualizar Projeto
	    <h:graphicImage value="/img/extensao/form_green.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar Relatório	    
	    <h:graphicImage value="/img/seta.gif"style="overflow: visible;" styleClass="noborder"/>: Analisar Relatório
	</div>


<h:form id="form">
		<table class="listagem">
				<caption class="listagem"> Relatorios de Ações Acadêmicas do Departamento (${fn:length(autorizacaoDepartamento.relatoriosUnidade)})</caption>
					<thead>
						<tr>
							<th>Código</th>
							<th width="40%">Título da Ação</th>
							<th>Tipo</th>
							<th>Analisado em</th>
							<th></th>
							<th></th>
							<th></th>							
						</tr>
					</thead>
						
						<c:forEach items="#{autorizacaoDepartamento.relatoriosUnidade}" var="item" varStatus="status">
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${item.atividade.codigo}</td>
									<td>${item.atividade.titulo}</td>
									<td>${item.tipoRelatorio.descricao}</td>
									<td><fmt:formatDate value="${item.dataValidacaoDepartamento}" pattern="dd/MM/yyyy" /> ${item.dataValidacaoDepartamento == null ? '<font color=red>NÃO ANALISADO</font>': ''}</td>
									
									<td  width="3%">											
											<h:commandLink title="Visualizar Projeto" 
											action="#{atividadeExtensao.view}" style="border: 0;">											
										       <f:param name="id" value="#{item.atividade.id}"/>
								               <h:graphicImage url="/img/view.gif" />
											</h:commandLink>
									</td>
									
									<td  width="3%">											
											<h:commandLink title="Visualizar Relatório" 
											action="#{relatorioAcaoExtensao.view}" style="border: 0;">											
										       <f:param name="id" value="#{item.id}"/>
								               <h:graphicImage url="/img/extensao/form_green.png" />
											</h:commandLink>
									</td>

									<td  width="3%">											
											<h:commandLink title="Analisar Relatório" 
											action="#{autorizacaoDepartamento.analisarRelatorio}" style="border: 0;"
											rendered="#{item.departamentoPodeAnalisar}">
										       <f:param name="idRelatorio" value="#{item.id}"/>
								               <h:graphicImage url="/img/seta.gif" />
											</h:commandLink>
									</td>
									
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty autorizacaoDepartamento.relatoriosUnidade}">
				 		   <tr>
				 		   		<td colspan="7"><center><font color="red">Não há relatórios de projetos submetidos para análise deste departamento</font></center></td>
				 		   </tr>
			 		   </c:if>

		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
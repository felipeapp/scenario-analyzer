<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > Relat�rios de A��es Acad�micas Integradas</h2>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Visualizar Projeto
	    <h:graphicImage value="/img/extensao/form_green.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar Relat�rio	    
	    <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;" id="leg_devolver"/>: Devolver Relat�rio
	    <h:graphicImage value="/img/seta.gif"style="overflow: visible;" styleClass="noborder"/>: Analisar Relat�rio
	</div>

<a4j:keepAlive beanName="validacaoRelatorioBean" />
<h:form id="form">
		<table class="listagem">
				<caption class="listagem"> Relat�rios de A��es Acad�micas Integradas(${fn:length(validacaoRelatorioBean.relatoriosUnidade)})</caption>
					<thead>
						<tr>
							<th>Ano</th>
							<th width="40%">T�tulo</th>
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
									<td><fmt:formatDate value="${item.dataValidacaoComite}" pattern="dd/MM/yyyy" /> <font color='red'>${item.dataValidacaoComite == null ? 'N�O ANALISADO': ''}</font></td>
									
									<td  width="2%">											
											<h:commandLink title="Visualizar Projeto" 
											action="#{projetoBase.view}" style="border: 0;">											
										       <f:param name="id" value="#{item.projeto.id}"/>
								               <h:graphicImage url="/img/view.gif" />
											</h:commandLink>
									</td>
									
									<td  width="2%">											
											<h:commandLink title="Visualizar Relat�rio" 
											action="#{relatorioAcaoAssociada.view}" style="border: 0;">											
										       <f:param name="id" value="#{item.id}"/>
								               <h:graphicImage url="/img/extensao/form_green.png" />
											</h:commandLink>
									</td>

									<td  width="2%">											
											<h:commandLink title="Devolver Relat�rio"
											action="#{validacaoRelatorioBean.devolverRelatorio}" style="border: 0;"	
											rendered="#{!item.editavel}"
											onclick="return confirm('Tem certeza que deseja Devolver este Relat�rio para Coordena��o do Projeto?');" id="devolver_relatorio_coord_">
										       <f:param name="id" value="#{item.id}"/>
								               <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;"/>
											</h:commandLink>
									</td>

									<td  width="2%">											
											<h:commandLink title="Analisar Relat�rio"
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
				 		   		<td colspan="7"><center><font color="red">N�o h� relat�rios de projetos submetidos pendentes de valida��o.</font></center></td>
				 		   </tr>
			 		   </c:if>

		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
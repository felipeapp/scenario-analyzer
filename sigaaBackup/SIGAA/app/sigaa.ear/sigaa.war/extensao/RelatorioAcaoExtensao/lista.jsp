<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Submissão do Relatórios de Ações de Extensão</h2>
		<h:outputText value="#{relatorioProjeto.create}"/>
	
	<div class="descricaoOperacao">
		<b>Atenção:</b> Caro coordenador, caso o relatório tenha sido "Aprovado com Recomendação" ou "Reprovado" pelo Departamento,
		o mesmo deve ser editado e reenviado, para que o Departamento reavalie. Caso tenha sido pela Pró-Reitoria de Extensão, 
		deve ser editado e reenviado, para que o Departamento e a Pró-Reitoria de Extensão reavaliem.
	</div>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_add.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relatório Parcial	    
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relatório Final<br/>
	    <h:graphicImage value="/img/extensao/document_edit.png"style="overflow: visible;" styleClass="noborder"/>: Editar/Enviar Relatório
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Remover Relatório
	    <h:graphicImage value="/img/extensao/document_view.png"style="overflow: visible;" styleClass="noborder"/>: Ver Relatório	    
	</div>
		
		
<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de relatórios de ações coordenadas pelo usuário atual</caption>
			<thead>
			<tr>
					<th width="15%">Tipo Relatório</th>
					<th style="text-align: center;">Data de Envio</th>
					<th>Validado Depto.</th>
					<th width="20%">Justificativa Depto.</th>					
					<th>Validado PROEx</th>
					<th width="20%">Justificativa Proex</th>
					<th></th>
					<th></th>
					<th></th>					
			</tr>
			</thead>
			<tbody>
			
					<c:set value="#{relatorioAcaoExtensao.acoesCoordenador}" var="acoes"/>			
					<c:forEach items="#{acoes}" var="acao" varStatus="status">						
						
						<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
							<td colspan="7" >${ acao.codigoTitulo }</td>
							<td width="2%">
							
								<h:commandLink action="#{relatorioAcaoExtensao.preAdicionarRelatorio}" style="border: 0;"	id="relParcial">
							       <f:param name="id" value="#{acao.id}"/>
							       <f:param name="relatorioFinal" value="false"/>
					               <h:graphicImage url="/img/extensao/document_add.png" title="Cadastrar Relatório Parcial" />
								</h:commandLink>							
								
							</td>	
							<td width="2%">
								<h:commandLink action="#{relatorioAcaoExtensao.preAdicionarRelatorio}" style="border: 0;"	id="relFinal">
							       <f:param name="id" value="#{acao.id}"/>
							       <f:param name="relatorioFinal" value="true"/>
					               <h:graphicImage url="/img/extensao/document_new.png" title="Cadastrar Relatório Final" />
								</h:commandLink>
							</td>																		
							
						</tr>
						
						<c:forEach items="#{acao.relatorios}" var="item" varStatus="status">
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			               			<td>${item.tipoRelatorio.descricao}</td>
									<td style="text-align: center;">
										<fmt:formatDate value="${item.dataEnvio}" pattern="dd/MM/yyyy" />
										<h:outputText value="CADASTRO EM ANDAMENTO" rendered="#{empty item.dataEnvio}"/>
									</td>
									<td>
										<font color="${(item.reprovadoDepartamento || item.aprovadoComRecomendacoesDepartamento) ? 'red' : 'black'}">
											${item.tipoParecerDepartamento.descricao}
										</font>
										
										<c:if test="${(item.reprovadoDepartamento || item.aprovadoComRecomendacoesDepartamento)}">
											<ufrn:help>Relatórios 'REPROVADOS' ou 'APROVADOS COM RECOMENDAÇÃO' devem ser alterados e enviados para nova validação.</ufrn:help>
										</c:if>
										
									</td>
									<td>${item.parecerDepartamento}</td>
									<td>
										<font color="${(item.reprovadoProex || item.aprovadoComRecomendacoesProex) ? 'red' : 'black'}">
											${item.tipoParecerProex.descricao}
										</font>										
										<c:if test="${(item.reprovadoProex || item.aprovadoComRecomendacoesProex)}">
											<ufrn:help>Relatórios 'REPROVADOS' ou 'APROVADOS COM RECOMENDAÇÃO' devem ser alterados e enviados para nova validação.</ufrn:help>
										</c:if>									
									</td>
									<td>${item.parecerProex}</td>
									<td width="2%">
										<h:commandLink action="#{relatorioAcaoExtensao.preAlterarRelatorio}" style="border: 0;" 
										id="alterarRelatorio" rendered="#{item.editavel}">
									       <f:param name="idRelatorio" value="#{item.id}"/>
							               <h:graphicImage url="/img/extensao/document_edit.png" title="Editar/Enviar Relatório" />
										</h:commandLink>
									</td>																		
									
									<td width="2%">
										<h:commandLink action="#{relatorioProjeto.removerRelatorio}" style="border: 0;" 
										id="removerRelatorio" rendered="#{item.editavel}" onclick="#{confirmDelete}">
									       <f:param name="idRelatorio" value="#{item.id}"/>
							               <h:graphicImage url="/img/extensao/document_delete.png" title="Remover Relatório"/>
										</h:commandLink>
									</td>
																		
									<td width="2%">								               
										<h:commandLink action="#{relatorioAcaoExtensao.view}" style="border: 0;" id="verRelatorio">
										   <f:param name="id" value="#{item.id}"/>
								           <h:graphicImage url="/img/extensao/document_view.png" title="Ver Relatório"/>
										</h:commandLink>
									</td>								
									
							</tr>
						</c:forEach>
						
						<c:if test="${empty acao.relatorios}" >
			 		   		<tr><td colspan="8" align="center"><font color="red">Não há relatórios cadastrados para esta ação.</font></td></tr>
			 		    </c:if>
						
					</c:forEach>
			 		   
			 		<c:if test="${empty acoes}" >
			 		 		<tr><td colspan="6" align="center"><font color="red">Não há ações de extensão ativas coordenadas pelo usuário atual.</font></td></tr>
			 		</c:if>
			 		   
			</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
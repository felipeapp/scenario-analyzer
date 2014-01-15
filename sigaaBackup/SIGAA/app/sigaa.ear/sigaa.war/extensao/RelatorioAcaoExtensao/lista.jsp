<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Submiss�o do Relat�rios de A��es de Extens�o</h2>
		<h:outputText value="#{relatorioProjeto.create}"/>
	
	<div class="descricaoOperacao">
		<b>Aten��o:</b> Caro coordenador, caso o relat�rio tenha sido "Aprovado com Recomenda��o" ou "Reprovado" pelo Departamento,
		o mesmo deve ser editado e reenviado, para que o Departamento reavalie. Caso tenha sido pela Pr�-Reitoria de Extens�o, 
		deve ser editado e reenviado, para que o Departamento e a Pr�-Reitoria de Extens�o reavaliem.
	</div>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_add.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Parcial	    
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Final<br/>
	    <h:graphicImage value="/img/extensao/document_edit.png"style="overflow: visible;" styleClass="noborder"/>: Editar/Enviar Relat�rio
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Remover Relat�rio
	    <h:graphicImage value="/img/extensao/document_view.png"style="overflow: visible;" styleClass="noborder"/>: Ver Relat�rio	    
	</div>
		
		
<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de relat�rios de a��es coordenadas pelo usu�rio atual</caption>
			<thead>
			<tr>
					<th width="15%">Tipo Relat�rio</th>
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
					               <h:graphicImage url="/img/extensao/document_add.png" title="Cadastrar Relat�rio Parcial" />
								</h:commandLink>							
								
							</td>	
							<td width="2%">
								<h:commandLink action="#{relatorioAcaoExtensao.preAdicionarRelatorio}" style="border: 0;"	id="relFinal">
							       <f:param name="id" value="#{acao.id}"/>
							       <f:param name="relatorioFinal" value="true"/>
					               <h:graphicImage url="/img/extensao/document_new.png" title="Cadastrar Relat�rio Final" />
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
											<ufrn:help>Relat�rios 'REPROVADOS' ou 'APROVADOS COM RECOMENDA��O' devem ser alterados e enviados para nova valida��o.</ufrn:help>
										</c:if>
										
									</td>
									<td>${item.parecerDepartamento}</td>
									<td>
										<font color="${(item.reprovadoProex || item.aprovadoComRecomendacoesProex) ? 'red' : 'black'}">
											${item.tipoParecerProex.descricao}
										</font>										
										<c:if test="${(item.reprovadoProex || item.aprovadoComRecomendacoesProex)}">
											<ufrn:help>Relat�rios 'REPROVADOS' ou 'APROVADOS COM RECOMENDA��O' devem ser alterados e enviados para nova valida��o.</ufrn:help>
										</c:if>									
									</td>
									<td>${item.parecerProex}</td>
									<td width="2%">
										<h:commandLink action="#{relatorioAcaoExtensao.preAlterarRelatorio}" style="border: 0;" 
										id="alterarRelatorio" rendered="#{item.editavel}">
									       <f:param name="idRelatorio" value="#{item.id}"/>
							               <h:graphicImage url="/img/extensao/document_edit.png" title="Editar/Enviar Relat�rio" />
										</h:commandLink>
									</td>																		
									
									<td width="2%">
										<h:commandLink action="#{relatorioProjeto.removerRelatorio}" style="border: 0;" 
										id="removerRelatorio" rendered="#{item.editavel}" onclick="#{confirmDelete}">
									       <f:param name="idRelatorio" value="#{item.id}"/>
							               <h:graphicImage url="/img/extensao/document_delete.png" title="Remover Relat�rio"/>
										</h:commandLink>
									</td>
																		
									<td width="2%">								               
										<h:commandLink action="#{relatorioAcaoExtensao.view}" style="border: 0;" id="verRelatorio">
										   <f:param name="id" value="#{item.id}"/>
								           <h:graphicImage url="/img/extensao/document_view.png" title="Ver Relat�rio"/>
										</h:commandLink>
									</td>								
									
							</tr>
						</c:forEach>
						
						<c:if test="${empty acao.relatorios}" >
			 		   		<tr><td colspan="8" align="center"><font color="red">N�o h� relat�rios cadastrados para esta a��o.</font></td></tr>
			 		    </c:if>
						
					</c:forEach>
			 		   
			 		<c:if test="${empty acoes}" >
			 		 		<tr><td colspan="6" align="center"><font color="red">N�o h� a��es de extens�o ativas coordenadas pelo usu�rio atual.</font></td></tr>
			 		</c:if>
			 		   
			</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
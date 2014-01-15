<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Busca de Relatórios de Ações de Extensão</h2> 
		<h:outputText value="#{relatorioAcaoExtensao.create}"/>
		<h:outputText value="#{relatorioProjeto.create}"/>
		
		
	<%@include file="/extensao/RelatorioAcaoExtensao/form_busca.jsp"%>
	
	<c:set value="#{relatorioAcaoExtensao.relatoriosLocalizados}" var="relatorios"/>
	<c:if test="${not empty relatorios}" >
		
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/form_green.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar Relatório
	    <h:graphicImage value="/img/arrow_undo.png"style="overflow: visible;" styleClass="noborder"/>: Devolver Relatório para Coordenador
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Remover Relatório
	</div>
		
		
<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de relatórios cadastrados (${ fn:length(relatorioAcaoExtensao.relatoriosLocalizados) })</caption>
			<thead>
				<tr>
						<th width="8%" >Código</th>
						<th>Título</th>
						<th width="15%">Tipo de Relatório</th>
						<th width="12%">Departamento</th>					
						<th width="12%">Pró-Reitoria</th>
						<th></th>
						<th></th>
						<th></th>
				</tr>
			</thead>
			<tbody>
			
					
					<c:set var="acao" value=""/>
					<c:forEach items="#{relatorios}" var="item" varStatus="status">
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			               			<td>${item.atividade.codigo}</td>
									<td>${item.atividade.titulo}</td>
			               			<td>${item.tipoRelatorio.descricao}</td> 
									<td>${item.tipoParecerDepartamento != null ? item.tipoParecerDepartamento.descricao : 'NÃO ANALISADO' }</td>
									<td>${item.tipoParecerProex != null ? item.tipoParecerProex.descricao  :  'NÃO ANALISADO'  }</td>

									<td width="2%">								               
										<h:commandLink action="#{relatorioAcaoExtensao.view}" style="border: 0;" id="btn_view_relatorio_" title="Visualizar Relatório">
										   <f:param name="id" value="#{item.id}"/>
								           <h:graphicImage url="/img/extensao/form_green.png"/>
										</h:commandLink>
									</td>
									
                                    <td width="2%">
                                        <h:commandLink id="devolverRel" title="Devolver Relatório para o Coordenador(a)" 
                                            action="#{ relatorioAcaoExtensao.devolverRelatorioCoordenador }"
                                            rendered="#{acesso.extensao}"
                                            onclick="return confirm('Tem certeza que deseja Devolver este relatório para Coordenador(a)?');">                                            
                                                <f:param name="id" value="#{item.id}"/>                        
                                                <h:graphicImage url="/img/arrow_undo.png"/>
                                        </h:commandLink>                                    
                                    </td>                                    
									
									<td width="2%">
                                        <h:commandLink action="#{relatorioAcaoExtensao.removerRelatorio}" style="border: 0;" onclick="#{confirmDelete}" 
                                            id="removerRelatorio" rendered="#{acesso.extensao}" title="Remover Relatório">
                                           <f:param name="idRelatorio" value="#{item.id}"/>
                                           <h:graphicImage url="/img/extensao/document_delete.png"/>
                                        </h:commandLink>
                                    </td>

							</tr>
					</c:forEach>					
			</tbody>
	</table>
	
	
</h:form>
</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
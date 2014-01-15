<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="assuntoManifestacao" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Assuntos
	</h2>
	
	<div class="descricaoOperacao">
		<p>Esta operação exibe a listagem de todos os assuntos cadastrados no sistema. Também permite alterar, ativar, desativar e remover os assuntos cadastrados.</p>
	</div>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Assunto
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Assunto
		<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />: Inativar Assunto
		<h:graphicImage value="/img/check.png" style="overflow: visible;" />: Ativar Assunto
	</div>


	<h:form id="listagem">
		<table class="formulario" width="80%" cellpadding="3px">
			<caption> Assuntos (${ fn:length(assuntoManifestacao.assuntos) })</caption>			
		
	
			<thead>
				<tr>
					<th>Assunto</th>
					<th>Categoria</th>
					<th>Situação</th>
					
					<th width="2%"></th>
					<th width="2%"></th>
					<th width="2%"></th>
					
				</tr>
				
			</thead>
			
			<tbody>
				<c:forEach items="#{assuntoManifestacao.assuntos }" var="assunto">
					<tr>
						<td style="text-align: left;"> ${assunto.descricao }</td>
						<td style="text-align: left;"> ${assunto.categoriaAssuntoManifestacao.descricao }</td>
												
						<c:if test="${assunto.ativo == true}">
							<td style="text-align: left;">ativo</td>
						</c:if>
						<c:if test="${assunto.ativo == false}">
							<td style="text-align: left;">inativo</td>
						</c:if>
						
						<td>
							<h:commandLink  action="#{assuntoManifestacao.preAtualizar}">
								<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Assunto"/>
								<f:param name="id" value="#{assunto.id}"/>
							</h:commandLink>
						</td>
						
						<td>
							<c:if test="${assunto.ativo == true}">
								<h:commandLink action="#{assuntoManifestacao.inativarOuAtivar}"  onclick="if  (!confirm('Deseja inativar esse assunto?')) return false;">
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" title="Inativar Assunto"/>
									<f:param name="id" value="#{assunto.id}"/>
							</h:commandLink>
							</c:if>
							
							
							<c:if test="${assunto.ativo == false}">
								<h:commandLink action="#{assuntoManifestacao.inativarOuAtivar}" onclick="if  (!confirm('Deseja ativar esse assunto?')) return false;">
									<h:graphicImage value="/img/check.png" style="overflow: visible;" title="Ativar Assunto"/>
									<f:param name="id" value="#{assunto.id}"/>
								</h:commandLink>
							</c:if>
						</td>
						
						<td>
							<h:commandLink action="#{assuntoManifestacao.remover}" onclick="#{confirmDelete}" >
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Assunto" />
								<f:param name="id" value="#{assunto.id}"/>
							</h:commandLink>
						</td>
				
				
				</tr>
			</c:forEach>
			</tbody>
		</table>
	
	
	</h:form>



</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
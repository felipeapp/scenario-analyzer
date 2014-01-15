<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="categoriaAssuntoManifestacao" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Categorias do Assunto
	</h2>

	<div class="descricaoOperacao">
		<p>Esta operação exibe a listagem de todas as categorias de assunto cadastradas no sistema. Também permite alterar, ativar, desativar e remover as categorias dos assuntos cadastradas.</p>
	</div>

	<div class="infoAltRem">
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Categoria
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Categoria
		<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />: Inativar Categoria
		<h:graphicImage value="/img/check.png" style="overflow: visible;" />: Ativar Categoria
	</div>
	
	
	<h:form id="listagem">
		<table class="formulario" width="70%" cellpadding="3px">
			<caption> Categorias (${ fn:length(categoriaAssuntoManifestacao.categorias) })</caption>
			
				<thead>
					<tr>
						<th >Categoria</th>
						<th>Situação</th>
						
						<th width="2%"></th>
						<th width="2%"></th>
						<th width="2%"></th>
						
					</tr>
				</thead>
				
				<tbody>
					<c:forEach items="#{categoriaAssuntoManifestacao.categorias }" var="categoria">
						<tr>
						    <td style="text-align: left;"> ${categoria.descricao }</td>
														
							<c:if test="${categoria.ativo == true}">
								<td style="text-align: left;">ativo</td>
							</c:if>
							<c:if test="${categoria.ativo == false}">
								<td style="text-align: left;">inativo</td>
							</c:if>
							
							<td>
								<h:commandLink  action="#{categoriaAssuntoManifestacao.preAtualizar}">
									<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Categoria"/>
									<f:param name="id" value="#{categoria.id}"/>
								</h:commandLink>
							</td>
							
							<td>
								<c:if test="${categoria.ativo == true}">
									<h:commandLink action="#{categoriaAssuntoManifestacao.inativarOuAtivar}" onclick="if  (!confirm('Deseja inativar essa categoria?')) return false;">
										<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" title="Inativar Categoria"/>
										<f:param name="id" value="#{categoria.id}"/>
								</h:commandLink>
								</c:if>
								
								
								<c:if test="${categoria.ativo == false}">
									<h:commandLink action="#{categoriaAssuntoManifestacao.inativarOuAtivar}" onclick="if  (!confirm('Deseja ativar essa categoria?')) return false;">
										<h:graphicImage value="/img/check.png" style="overflow: visible;" title="Ativar Categoria"/>
										<f:param name="id" value="#{categoria.id}"/>
									</h:commandLink>
								</c:if>
							</td>
							
							<td>
								<h:commandLink onclick="#{confirmDelete}" action="#{categoriaAssuntoManifestacao.remover}" >
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Categoria" />
									<f:param name="id" value="#{categoria.id}"/>
								</h:commandLink>
							</td>	
															
							
							
							
						</tr>
					</c:forEach>
				</tbody>
			
		</table>
	</h:form>
	
	
	</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
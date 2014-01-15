<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<style type="text/css">
	div.menu-botoes li.fasciculo-substituidor a p {
		background-image: url('/sigaa/img/refresh.png');
	}
 
</style>


	<h2>  <ufrn:subSistema /> &gt; Verificação da Associação entre Assinatura e Título. </h2>
	
	<div class="descricaoOperacao"> 
	
		 <p> Página para a verificação a qual Título a assinatura de periódicos pertence, 
		 caso a assinatura esteja em um Título errado, é possível também realizar a alteração
		 do Título da assinatura.</p> 
	
	</div>


	<f:view>

		<h:form>
	
				<%--  Caso o usuário utilize o botão voltar  --%>
				<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>
				<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
				<a4j:keepAlive beanName="associaAssinaturaATituloMBean"></a4j:keepAlive>
				
	
				<table class="visualizacao"  style="width: 70%">
					<caption>Informações da Associação</caption>
					
					<tr>
						<td>
							<table class="subFormulario" style="width: 100%">
								<caption>Assinatura</caption>
			
								<tr>
									<th style="width: 25%">Código:</th>				
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.codigo}" />
									</td>
								</tr>
					
								<tr>	
									<th>Título:</th>		
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.titulo}" />
									</td>
								</tr>
					
								<tr>	
									<th>ISSN:</th>		
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.issn}" />
									</td>
								</tr>
					
								<tr>	
									<th>Unidade de Destino:</th>		
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.unidadeDestino.descricao}" />
									</td>
								</tr>
					
								<tr>
									<th>Modalidade Aquisição:</th>
									<td>
										<c:if test="${associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.assinaturaDeCompra}">
											COMPRA
										</c:if>
										<c:if test="${associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.assinaturaDeDoacao}">
											DOAÇÃO
										</c:if>
										<c:if test="${! associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.assinaturaDeDoacao && ! associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.assinaturaDeCompra}">
											INDEFINIDO
										</c:if>
									</td>
								</tr>
								
								<tr>
									<th>Internacionalização</th>
									<td>
										<c:if test="${associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.internacional}">
											Internacional
										</c:if>
										<c:if test="${ ! associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.internacional}">
											Nacional
										</c:if>
									</td>
								</tr>
								
								<tr>
									<th>Criada por:</th>
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.nomeCriador}" />
									</td>
								</tr>
								
							</table>
						</td>
					</tr>
				
					<tr>
						<td>
							<table class="subFormulario" style="width: 100%">
								
								<caption>Título da Assinatura</caption>
			
								<c:if test="${associaAssinaturaATituloMBean.tituloAssociacao == null}">
									<tr>
										<th style="width: 100%; text-align: center; color:red;">Assinatura está associada a nenhum Título</th>				
									</tr>
								</c:if>
				
								<c:if test="${associaAssinaturaATituloMBean.tituloAssociacao != null}">
			
									<tr>
										<th style="width: 25%">Número do Sistema:</th>				
										<td>
											<h:outputText value="#{associaAssinaturaATituloMBean.tituloAssociacao.numeroDoSistema}" />
										</td>
									</tr>
						
									<tr>	
										<th>Autor:</th>
										<td>
											<h:outputText value="#{associaAssinaturaATituloMBean.tituloAssociacao.autor}" />
										</td>
									</tr>
						
									<tr>	
										<th>Título:</th>		
										<td>
											<h:outputText value="#{associaAssinaturaATituloMBean.tituloAssociacao.titulo}" />
											<h:outputText value="#{associaAssinaturaATituloMBean.tituloAssociacao.subTitulo}" />
										</td>
									</tr>			
						
									<tr>
										<th>Edição:</th>
										<td>
											<h:outputText value="#{associaAssinaturaATituloMBean.tituloAssociacao.edicao}" />
										</td>
									</tr>
						
									<tr>
										<th>Ano:</th>
										<td>
											<c:forEach var="ano" items="#{associaAssinaturaATituloMBean.tituloAssociacao.anosFormatados}">
												${ano}
											</c:forEach>
										</td>
									</tr>
								
								</c:if>
							</table>
						</td>
					</tr>
				
					
				
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
									<c:if test="${associaAssinaturaATituloMBean.tituloAssociacao != null}">
										<h:commandButton value="Alterar Associação" action="#{associaAssinaturaATituloMBean.iniciaAlteracaoAssociacao}" />
									</c:if>
								</ufrn:checkRole>
								<h:commandButton value="<< Voltar" action="#{associaAssinaturaATituloMBean.iniciaAlteracaoAssociacaoEntreAssinaturaETitulo}" />
								<h:commandButton value="Cancelar"  action="#{associaAssinaturaATituloMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
							</td>
						</tr>
					</tfoot>
					
				</table>
				
		</h:form>

	</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
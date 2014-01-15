<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<style type="text/css">
	div.menu-botoes li.fasciculo-substituidor a p {
		background-image: url('/sigaa/img/refresh.png');
	}
 
</style>


	<h2>  <ufrn:subSistema /> &gt; Verifica��o da Associa��o entre Assinatura e T�tulo. </h2>
	
	<div class="descricaoOperacao"> 
	
		 <p> P�gina para a verifica��o a qual T�tulo a assinatura de peri�dicos pertence, 
		 caso a assinatura esteja em um T�tulo errado, � poss�vel tamb�m realizar a altera��o
		 do T�tulo da assinatura.</p> 
	
	</div>


	<f:view>

		<h:form>
	
				<%--  Caso o usu�rio utilize o bot�o voltar  --%>
				<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>
				<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
				<a4j:keepAlive beanName="associaAssinaturaATituloMBean"></a4j:keepAlive>
				
	
				<table class="visualizacao"  style="width: 70%">
					<caption>Informa��es da Associa��o</caption>
					
					<tr>
						<td>
							<table class="subFormulario" style="width: 100%">
								<caption>Assinatura</caption>
			
								<tr>
									<th style="width: 25%">C�digo:</th>				
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.codigo}" />
									</td>
								</tr>
					
								<tr>	
									<th>T�tulo:</th>		
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
									<th>Modalidade Aquisi��o:</th>
									<td>
										<c:if test="${associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.assinaturaDeCompra}">
											COMPRA
										</c:if>
										<c:if test="${associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.assinaturaDeDoacao}">
											DOA��O
										</c:if>
										<c:if test="${! associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.assinaturaDeDoacao && ! associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.assinaturaDeCompra}">
											INDEFINIDO
										</c:if>
									</td>
								</tr>
								
								<tr>
									<th>Internacionaliza��o</th>
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
								
								<caption>T�tulo da Assinatura</caption>
			
								<c:if test="${associaAssinaturaATituloMBean.tituloAssociacao == null}">
									<tr>
										<th style="width: 100%; text-align: center; color:red;">Assinatura est� associada a nenhum T�tulo</th>				
									</tr>
								</c:if>
				
								<c:if test="${associaAssinaturaATituloMBean.tituloAssociacao != null}">
			
									<tr>
										<th style="width: 25%">N�mero do Sistema:</th>				
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
										<th>T�tulo:</th>		
										<td>
											<h:outputText value="#{associaAssinaturaATituloMBean.tituloAssociacao.titulo}" />
											<h:outputText value="#{associaAssinaturaATituloMBean.tituloAssociacao.subTitulo}" />
										</td>
									</tr>			
						
									<tr>
										<th>Edi��o:</th>
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
										<h:commandButton value="Alterar Associa��o" action="#{associaAssinaturaATituloMBean.iniciaAlteracaoAssociacao}" />
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
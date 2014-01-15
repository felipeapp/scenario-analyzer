<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>


<style type="text/css">
	div.menu-botoes li.fasciculo-substituidor a p {
		background-image: url('/sigaa/img/refresh.png');
	}
 
</style>


<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

	<h2>  <ufrn:subSistema /> &gt; Confirmação de atribuição da Assinatura ao Título Selecionado </h2>
	
	<div class="descricaoOperacao"> 
	
		 <p> Caro usuário, confirme a alteração da associação entre a Assinatura e o Título.</p> 
	
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
								<caption>Informações da Assinatura</caption>
			
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
									<th>Internacional:</th>
									<td>
										<c:if test="${associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.internacional}">
											SIM
										</c:if>
										<c:if test="${ ! associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.internacional}">
											NÃO
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
								
								<caption>Informações do Título Antigo da Assinatura</caption>
			
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
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.subTitulo}" />
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
							</table>
						</td>
					</tr>
				
					<tr>
						<td>
							<table class="subFormulario" style="width: 100%">
								
								<caption>Informações do Novo Título da Assinatura</caption>
			
								<tr>
									<th style="width: 25%">Número do Sistema:</th>				
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.numeroDoSistema}" />
									</td>
								</tr>
					
								<tr>	
									<th>Autor:</th>
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.autor}" />
									</td>
								</tr>
					
								<tr>	
									<th>Título:</th>		
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.titulo}" />
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.subTitulo}" />
									</td>
								</tr>			
					
								<tr>
									<th>Edição:</th>
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.edicao}" />
									</td>
								</tr>
					
								<tr>
									<th>Ano:</th>
									<td>
										<c:forEach var="ano" items="#{associaAssinaturaATituloMBean.novoTituloAssociacao.anosFormatados}">
											${ano}
										</c:forEach>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
								<h:commandButton value="Confirmar Nova Associação" action="#{associaAssinaturaATituloMBean.alteraAssociacaoEntreAssinaturaETitulo}" 
									onclick="return confirm('Confirma a mudança no Título da Assinatura ? Os fascículos serão mostrados ao usuário no novo Título. ');"/>
								<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTitulo}" />
								<h:commandButton value="Cancelar"  action="#{associaAssinaturaATituloMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
							</td>
						</tr>
					</tfoot>
					
				</table>
				
		</h:form>

	</f:view>


</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
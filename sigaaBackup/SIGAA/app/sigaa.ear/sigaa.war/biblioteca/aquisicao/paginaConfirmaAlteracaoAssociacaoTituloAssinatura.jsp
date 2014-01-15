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

	<h2>  <ufrn:subSistema /> &gt; Confirma��o de atribui��o da Assinatura ao T�tulo Selecionado </h2>
	
	<div class="descricaoOperacao"> 
	
		 <p> Caro usu�rio, confirme a altera��o da associa��o entre a Assinatura e o T�tulo.</p> 
	
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
								<caption>Informa��es da Assinatura</caption>
			
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
									<th>Internacional:</th>
									<td>
										<c:if test="${associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.internacional}">
											SIM
										</c:if>
										<c:if test="${ ! associaAssinaturaATituloMBean.assinaturaAssociacaoTitulo.internacional}">
											N�O
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
								
								<caption>Informa��es do T�tulo Antigo da Assinatura</caption>
			
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
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.subTitulo}" />
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
							</table>
						</td>
					</tr>
				
					<tr>
						<td>
							<table class="subFormulario" style="width: 100%">
								
								<caption>Informa��es do Novo T�tulo da Assinatura</caption>
			
								<tr>
									<th style="width: 25%">N�mero do Sistema:</th>				
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
									<th>T�tulo:</th>		
									<td>
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.titulo}" />
										<h:outputText value="#{associaAssinaturaATituloMBean.novoTituloAssociacao.subTitulo}" />
									</td>
								</tr>			
					
								<tr>
									<th>Edi��o:</th>
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
								<h:commandButton value="Confirmar Nova Associa��o" action="#{associaAssinaturaATituloMBean.alteraAssociacaoEntreAssinaturaETitulo}" 
									onclick="return confirm('Confirma a mudan�a no T�tulo da Assinatura ? Os fasc�culos ser�o mostrados ao usu�rio no novo T�tulo. ');"/>
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
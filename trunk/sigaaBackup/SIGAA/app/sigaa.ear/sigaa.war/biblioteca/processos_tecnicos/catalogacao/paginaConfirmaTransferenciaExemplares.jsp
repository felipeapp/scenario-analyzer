<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>


<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

<h2>  <ufrn:subSistema /> &gt; Remo��o de T�tulos </h2>
	
	<div class="descricaoOperacao"> 
		 <p> Confirme os dados para a Transfer�ncia dos Exemplares entre T�tulos .</p> 
	</div>


	<f:view>

		<h:form>
	
			<%-- Tem que guardar todos os bean da pagina de pesquisa, pois o usu�rio pode chegar aqui vindo de v�rias partes do sistema --%>	
			<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
			<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>
			<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>
			<a4j:keepAlive beanName="transfereExemplaresEntreTitulosMBean"></a4j:keepAlive>
			<a4j:keepAlive beanName="cooperacaoTecnicaExportacaoMBean"></a4j:keepAlive>
			<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
			<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>
	
			
	
			<table class="visualizacao"  style="width: 100%; margin-bottom: 20px;">
				<caption>Confirma��o dos dados para a transfer�ncia</caption>
			
				<c:if test="${fn:length(transfereExemplaresEntreTitulosMBean.exemplaresParaTransferincia) == 0}">
			
					<tr>
						<th style="text-align: center;">
							Nenhum exemplar foi selecionado para a transfer�ncia. 
						</th>
					</tr>
			
				</c:if>
				
				<c:if test="${fn:length(transfereExemplaresEntreTitulosMBean.exemplaresParaTransferincia) > 0}">
			
					<tr>
						<td>
							<table class="subFormulario" style="width: 100%">
								<caption>T�tulo Original dos Exemplares</caption>
								
								<tr>
									<th>N�mero do Sistema:</th>				
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloOriginal.numeroDoSistema}" />
									</td>
								</tr>
						
								<tr>	
									<th>T�tulo:</th>		
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloOriginal.titulo}" />
									</td>
								</tr>
					
								<tr>	
									<th>Autor:</th>
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloOriginal.autor}" />
									</td>
								</tr>
					
								<tr>
									<th>Edi��o:</th>
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloOriginal.edicao}" />
									</td>
								</tr>
					
								<tr>
									<th>Ano:</th>
									<td>
										<c:forEach var="ano" items="#{transfereExemplaresEntreTitulosMBean.tituloOriginal.anosFormatados}">
											${ano}
										</c:forEach>
									</td>
								</tr>
								
								<tr>
									<th style="width: 30%"> Quantidade de Materiais Informacionais:</th>
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloOriginal.quantidadeMateriaisAtivosTitulo}" />
									</td>
								</tr>
										
							</table>
							
						</td>
						
					</tr>
		
					<tr>
						<td>
							<table class="subFormulario" style="width: 100%">
								<caption>T�tulo Destinat�rio dos Exemplares</caption>
		
								<tr>
									<th>N�mero do Sistema:</th>				
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloDestinatario.numeroDoSistema}" />
									</td>
								</tr>
					
								<tr>	
									<th>T�tulo:</th>		
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloDestinatario.titulo}" />
									</td>
								</tr>
					
								<tr>	
									<th>Autor:</th>
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloDestinatario.autor}" />
									</td>
								</tr>
					
								<tr>
									<th>Edi��o:</th>
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloDestinatario.edicao}" />
									</td>
								</tr>
					
								<tr>
									<th>Ano:</th>
									<td>
										<c:forEach var="ano" items="#{transfereExemplaresEntreTitulosMBean.tituloDestinatario.anosFormatados}">
											${ano}
										</c:forEach>
									</td>
								</tr>
								
								<tr>
									<th style="width: 30%"> Quantidade de Materiais Informacionais:</th>
									<td>
										<h:outputText value="#{transfereExemplaresEntreTitulosMBean.tituloDestinatario.quantidadeMateriaisAtivosTitulo}" />
									</td>
								</tr>
							</table>
									
						</td>
					</tr>
					
					
								
					<tr>
						<td>
							<table class="subFormulario" style="width: 100%">
							
								<caption>Exemplares que v�o ser Tranferidos</caption>
					
								<c:forEach items="#{transfereExemplaresEntreTitulosMBean.exemplaresParaTransferincia}" var="exemplar" varStatus="loop">
									
									<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<th> C�digo de Barras:</th>
										<td>
											${exemplar.codigoBarras} <c:if test="${exemplar.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if>
										</td>					
					
										<th> Biblioteca:</th>
										<td>
											${exemplar.biblioteca.descricao}
										</td>
										
										<th> Cole��o:</th>
										<td>
											${exemplar.colecao.descricao}
										</td>
										
									</tr>
									
									<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										
										<th> Status:</th>
										<td>
											${exemplar.status.descricao}
										</td>
										
										<th> Situa��o:</th>
										<td colspan="3">
											${exemplar.situacao.descricao}
										</td>
										
									</tr>
									
								</c:forEach>
							
							
							</table>
							
						</td>
					</tr>
		
				</c:if>
	
				<tfoot>
					<tr>
						<td style="text-align: center;">
							
							<c:if test="${fn:length(transfereExemplaresEntreTitulosMBean.exemplaresParaTransferincia) > 0}">
								<h:commandButton value="Transferir Exemplares" action="#{transfereExemplaresEntreTitulosMBean.transferirMateriais}"
									onclick="return confirm(' Confirma a transfer�ncia dos Exemplares ? ');"/>
							</c:if>
							
							<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.telaPesquisaTitulo}"/>
							
							<h:commandButton value="Cancelar" action="#{transfereExemplaresEntreTitulosMBean.cancelar}" onclick="#{confirm}" immediate="true" />
						<td>
					</tr>
				</tfoot>
				
			</table>
				
		</h:form>

	</f:view>

</ufrn:checkRole>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
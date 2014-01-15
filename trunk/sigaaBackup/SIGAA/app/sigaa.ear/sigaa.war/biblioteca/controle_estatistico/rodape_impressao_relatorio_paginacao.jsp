


	<%-- Os botões para fazer a paginação, caso o relatório utilize  --%>

<c:if test="${_abstractRelatorioBiblioteca.utilizandoPaginacao}">
	
	<h:form id="formPaginacaoRodape">
		<div class="naoImprimir" style="text-align:center;margin-top:10px;">
			<table style="border-bottom: solid 1px; margin-bottom: 5px; width: 100%;">
				<tr>
					<c:if test="${_abstractRelatorioBiblioteca.paginaAtual > 1}">
						<td style="vertical-align: middle; text-align: left; width: 25%;">
							<h:commandLink action="#{ _abstractRelatorioBiblioteca.gerarProximosResultadosConsultaPaginada}" id="linkGerarPrimeiraPaginaRodape" >
								Primeira Página
								<f:param name="pagina_atual_relatorio_biblioteca" value="#{1}"/>
							</h:commandLink>
						</td>
						<td style="vertical-align: middle; text-align: left; width: 25%;">
							<h:commandLink action="#{ _abstractRelatorioBiblioteca.gerarProximosResultadosConsultaPaginada}" id="linkGerarPaginaAnteriorRodape" >
								<< Página Anterior
								<f:param name="pagina_atual_relatorio_biblioteca" value="#{_abstractRelatorioBiblioteca.paginaAtual - 1}"/>
							</h:commandLink>
						</td>
					</c:if>
					<c:if test="${_abstractRelatorioBiblioteca.paginaAtual == 1}">
						<td style="width: 50%;" colspan="2"></td>
					</c:if>
					
					<c:if test="${_abstractRelatorioBiblioteca.paginaAtual >= _abstractRelatorioBiblioteca.numeroTotalDePaginas}">
						<td style="width: 50%;" colspan="2"></td>
					</c:if>
					
					<c:if test="${_abstractRelatorioBiblioteca.paginaAtual < _abstractRelatorioBiblioteca.numeroTotalDePaginas}">
						<td style="vertical-align: middle; text-align: right; width: 25%;">
							<h:commandLink action="#{ _abstractRelatorioBiblioteca.gerarProximosResultadosConsultaPaginada}" id="linkGerarProximaPaginaRodape" >
								Próxima Página >>
								<f:param name="pagina_atual_relatorio_biblioteca" value="#{_abstractRelatorioBiblioteca.paginaAtual + 1}"/>
							</h:commandLink>
						</td>
						<td style="vertical-align: middle; text-align: right; width: 25%;">
							<h:commandLink action="#{ _abstractRelatorioBiblioteca.gerarProximosResultadosConsultaPaginada}" id="linkGerarUltimaPaginaRodape" >
								Última Página
								<f:param name="pagina_atual_relatorio_biblioteca" value="#{_abstractRelatorioBiblioteca.numeroTotalDePaginas}"/>
							</h:commandLink>
						</td>
					</c:if>
				</tr>
			</table>
		</div>
	</h:form>

</c:if>


	 <%-- Página de rodapé para ser utilizada nos relatórios onde a ação o botão voltar não poder ser history.back() padrão  --%>
     
     <%-- incluir essa página dentro de um <f:viwe>  @see relatorioInventarioporFaixaCodigoBarrasAnalitico.jsp  --%>

	<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
	</div> <%-- Fim do div relatorio  --%>
		<div class="clear"> </div>
		<br/>
		<div id="relatorio-rodape">
			<p>
				<table width="100%">
					<tr>
						<h:form>
							<td class="voltar" align="left"><h:commandLink id="voltarDocumentoQuitacao" value="Voltar" action="#{_abstractRelatorioBiblioteca.telaPadraoFiltrosRelatoriosBiblioteca}"></h:commandLink></td>
						</h:form>
						<td width="70%"  align="center">
						${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %>
						</td>
						<td class="naoImprimir" align="right">
							<a onclick="javascript:window.print();" href="#">Imprimir</a>  
						</td>
						<td class="naoImprimir" align="right">
							<a onclick="javascript:window.print();" href="#">							
								<img alt="Imprimir" title="Imprimir" src="/shared/javascript/ext-1.1/docs/resources/print.gif"/>
							</a>
						</td>
					</tr>
				</table>
			</p>
		</div>
	</div>  <%-- Fim do div 'container' --%>
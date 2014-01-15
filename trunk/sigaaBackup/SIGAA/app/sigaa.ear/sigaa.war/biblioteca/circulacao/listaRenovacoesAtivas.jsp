<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<%-- por padrao as informacoes dos detalhes do item nao sao mostradas --%>
<style>	
	table.listagem td.detalhesItem { display: none; padding: 0;}
</style>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="listaRenovacoesAtivasUsuarioMBean" />

	<h:form id="formulario">

		<h2> <ufrn:subSistema /> &gt; Renovações Ativas</h2>

		<%-- Modal panel com os detalhes de cada material selecionado pelo usuário --%>

		<p:resources />

		<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css" />	
	
		<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaMaterial">
			<c:set var="_material_selecionado" value="${detalhesMateriaisDeUmTituloMBean.materialSelecionado}" scope="request" />
			<c:set var="_artigos_do_fasciculo_selecionado" value="${detalhesMateriaisDeUmTituloMBean.artigosDoFasciculoSelecionado}" scope="request" />
			<c:set var="_reservas_do_material_selecionado" value="${detalhesMateriaisDeUmTituloMBean.reservasDoMaterial}" scope="request" />
			<c:set var="_qtd_emprestimos_materail_selecionado" value="${detalhesMateriaisDeUmTituloMBean.qtdEmprestimosMaterialSelecionado}" scope="request" />
			<c:set var="_is_fasciculo" value="${detalhesMateriaisDeUmTituloMBean.periodico}" scope="request" />
			<c:set var="_is_mostrar_informacoes_titulo" value="${detalhesMateriaisDeUmTituloMBean.mostrarInformacaoTituloDetalhesMaterial}" scope="request" />
			<c:set var="_titulo_do_material" value="${detalhesMateriaisDeUmTituloMBean.tituloDoMaterial}" scope="request" />
			<c:set var="_assinatura_do_material" value="${detalhesMateriaisDeUmTituloMBean.asssinaturaDoMaterial}" scope="request" />
			<%@include file="/public/biblioteca/paginaPadraoDetalhesMaterial.jsp"%>
		</a4j:outputPanel>

		<c:set var="_infoUsuarioCirculacao" value="${listaRenovacoesAtivasUsuarioMBean.infoUsuario}" scope="request" />
		<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>



		<%--  Mostra as renovações ativas do usuario acima   --%>
	
		<c:if test="${not empty listaRenovacoesAtivasUsuarioMBean.renovacoesAtivas  }">
			
			<div class="infoAltRem" style="margin-top: 10px">
			
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: 
					Estornar Renovação			 
				</ufrn:checkRole>
			</div> 
	
	
	
			<div id="divDadosRenovacoes" style="margin-top:30px">
				
				<table class="listagem" >
					<caption> Renovações Ativas(${fn:length(listaRenovacoesAtivasUsuarioMBean.renovacoesAtivas)}) </caption>
					<thead>
					<tr>
						<th width="15%" style="text-align:left"> Código de Barras</th>
						<th width="15%" style="text-align:center"> Data do Emprestimo</th>
						<th width="15%" style="text-align:center"> Data da Renovação </th>
						<th width="15%" style="text-align:center"> Tipo de Emprestimo </th>
						<th width="30%" style="text-align:center"> Prazo Devolução </th>
						<th width="6%" style="text-align:center"> Atrasado </th>
						<th width="2%"></th>
					</tr>
					</thead>
					<tbody>
	
	
						<c:forEach items="#{listaRenovacoesAtivasUsuarioMBean.renovacoesAtivas}" var="emprestimo" varStatus="status">
	
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								
								<td width="20%" style="text-align:left">
									<h:outputText value="#{emprestimo.material.codigoBarras}"></h:outputText>
								</td>
								
								<td width="20%" style="text-align:center">
									<h:outputText value="#{emprestimo.dataEmprestimo}" converter="convertData"></h:outputText>
								</td>
								<td width="20%" style="text-align:center">
									<h:outputText value="#{emprestimo.dataRenovacao}" converter="convertData"></h:outputText>
								</td>
								<td width="20%" style="text-align:center">
									${emprestimo.politicaEmprestimo.tipoEmprestimo.descricao}
								</td>
								<td width="30%" style="text-align:center">
									<h:outputText value="#{emprestimo.prazo}" converter="convertData">
										<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
									</h:outputText>
								</td>							

								<c:if test="${emprestimo.atrasado}">
									<td width="6%" style="text-align:center;color:red">
										SIM
									</td>
								</c:if>
								<c:if test="${ ! emprestimo.atrasado}">
									<td width="6%" style="text-align:center;color:green">
										NÃO
									</td>
								</c:if>

								<td width="2%">	
									
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
									
										<h:commandLink  action ="#{estornaRenovacaoMBean.estornar}"
											onclick="return confirm('Confirma estorno da renovação? ');">
		
											<h:graphicImage url="/img/biblioteca/estornar.gif"
												style="border:none"
												title="Clique aqui para Estornar essa Renovação" />
	
											<f:param name="idEmprestimo" value="#{emprestimo.id}"/>
											<f:param name="idUsuarioEmprestimo" value="#{listaRenovacoesAtivasUsuarioMBean.infoUsuario.idUsuarioBiblioteca}"/>
	
										</h:commandLink>
									</ufrn:checkRole>

								</td>


							</tr>


							<%-- A linha da tabela que mostra os detalhes do item e eh abilitado usando javascript--%>
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}"> 
								<td colspan="8" style="text-align: center;">
								
									<a4j:commandLink  value="Mostrar Detalhes "  actionListener="#{detalhesMateriaisDeUmTituloMBean.carregarDetalhesMaterialSelecionado}"
							 				ajaxSingle="true" oncomplete="modelPanelDetalhes.show();" style="font-weight: normal; font-style: italic; " 
							 				reRender="formResultadosPesquisaFasciculo">
								 		<f:param name="isPeriodicoVisualzarDetalhes"  value="#{! emprestimo.material.exemplar}" />
								 		<f:param name="isMostrarInformacaoTituloDetalhesMaterial" value="true" />			
							 			<f:param name="idMaterialMostrarDetalhes" value="#{emprestimo.material.id}"/>
								 	</a4j:commandLink>
								
								</td>		
							</tr>

						</c:forEach>
					
					</tbody>
				
					<tfoot>
						<tr>
							<td colspan="8" style="text-align: center; font-weight: bold;">
								<h:commandButton value="<< Voltar" action="#{listaRenovacoesAtivasUsuarioMBean.voltar}" />
							</td>
						</tr>
					</tfoot>

				</table>
	
			</div>
	
		</c:if>



		
		<%-- Busca sem resultados ou qtd de resultados utrapassados --%>
	
		<c:if test="${empty listaRenovacoesAtivasUsuarioMBean.renovacoesAtivas }">
			<div style="margin-top:30px;color:red;text-align:center">	
				Usuário Não Possui Renovações Ativas	
			</div>
			
			<div id="divLinkVoltar" style="width:70%; margin-left:auto;margin-right:auto;margin-top:20px; text-align:center;">
	
			<h:commandLink value="Voltar" action="#{listaRenovacoesAtivasUsuarioMBean.voltar}"></h:commandLink>	
	
			</div>
		</c:if>
		
		



	</h:form>



</f:view>






<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Títulos com Catalogações Incompletas</h2>

<div class="descricaoOperacao"> 
     <p>Página na qual é possível visualizar as catalogações incompletas de Títulos.</p> 
     <p>Catalogações incompletas são aquelas nas quais o catalogador salvou o Título, mas não digitou 
     todos os campos obrigatórios, ou foram importados vários Títulos em um mesmo arquivo, mas esses 
     Títulos não foram trabalhados ainda pelo catalogador.</p>
     <p> Materiais informacionais só podem ser adicionados a esses Títulos depois que suas 
catalogações forem finalizadas.</p>
</div>


<f:view>

	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>

	<%-- Para o usuário usar o botão voltar e as informação da pesquisa dos títulos ainda está lá. --%>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>

	<h:form id="form">
		
			<c:if test="${buscaCatalogacoesIncompletasMBean.qtdTitulosIncompletos > 0 || buscaCatalogacoesIncompletasMBean.pesquisaImportacao }">
				<div class="infoAltRem" style="width: 100%">
					<c:if test="${buscaCatalogacoesIncompletasMBean.pesquisaImportacao }">
						<h:graphicImage value="/img/biblioteca/importar.png" />
						<h:commandLink value="Importar Novos Títulos" action="#{cooperacaoTecnicaImportacaoMBean.iniciarImportacaoBibliograficaDiretamente}" />
					</c:if>	
					
					<c:if test="${buscaCatalogacoesIncompletasMBean.qtdTitulosIncompletos > 0}">
					
						<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
						Selecionar Título
						
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
						Apagar o Título não Catalogado
					
					</c:if>
					
				</div>
			</c:if>
			
			
			<table class=formulario width="100%">
			
				<caption>Catalogações não Finalizadas de Títulos (${buscaCatalogacoesIncompletasMBean.qtdTitulosIncompletos}) </caption>
				
				<tr style="text-align:center;">
					
					<th colspan="1" class="obrigatorio" style="width: 50%">Apenas Meus Títulos ? </th>
					
					<td colspan="6" style="margin-bottom:20px; text-align:left;">
						
						<h:selectOneRadio value="#{buscaCatalogacoesIncompletasMBean.apenasMinhasCatalogacoesIncompletas}" onclick="submit()"
									valueChangeListener="#{buscaCatalogacoesIncompletasMBean.atualizaTitulosIncompletos}">  
	        				<f:selectItem itemLabel="SIM" itemValue="true" />
							<f:selectItem itemLabel="NÃO" itemValue="false" />
	    				</h:selectOneRadio>
						
					</td>
				</tr>
				
				<c:if test="${buscaCatalogacoesIncompletasMBean.qtdTitulosIncompletos > 0}">
				
					<tr>
						<td colspan="7">
							<table class="subFormulario" style="width: 100%;">
								<thead>
									<tr>
										<td style="width: 10%; text-align: right;">Nº do Sistema</td>
										<td style="width: 20%">Autor</td>
										<td style="width: 30%">Título</td>
										<td>Ano</td>
										<td>Edição</td>
										<td>Importado</td>
										<td style="width: 20%">Criado por</td>
										<td width="1%"></td>
										<td width="1%"></td>
									</tr>
								</thead>
								
								<c:set var="titulosIncompletos" value="${buscaCatalogacoesIncompletasMBean.allTitulosIncompletos}" scope="request" />
								
								<c:forEach items="#{titulosIncompletos}" var="titulo" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td style="text-align: right;">${titulo.numeroDoSistema}</td>
										<td>${titulo.autor}</td>
										<td>${titulo.titulo}</td>
										<td> 
											<c:forEach var="ano" items="#{titulo.anosFormatados}">
												${ano}
											</c:forEach>
										</td>
										<td>
											${titulo.edicao} 
										</td>
										<td style="text-align: center;">
											${titulo.importado ? 'Sim' : 'Não'} 
										</td>
										<td>
											${titulo.nomeUsuario} 
										</td>
										<td>
											<%-- Vai chamar o método iniciaImportacao de catalogacaoMBean --%>
											<%-- Se o usuário iniciou pela catalogação por tombamento os dados do sipac já vão está lá e o usuário vai poder incluir materiais  --%>
											<h:commandLink styleClass="noborder" title="Selecionar Título" action="#{buscaCatalogacoesIncompletasMBean.iniciarCatalogacaoTitulos}">
												<f:param name="idTituloNaoFinalizado" value="#{titulo.idTituloCatalografico}"/>
												<h:graphicImage value="/img/seta.gif"/>
											</h:commandLink>
										</td>
										
										<td>											
											<h:commandLink action="#{removerEntidadeDoAcervoMBean.telaConfirmaRemocaoVindoPaginaCatalogacoesIncompletas}">
												<h:graphicImage url="/img/delete.gif" style="border:none" title="Clique aqui para remover o título não catalogado do sistema" />
												<f:param name="idTituloRemocao" value="#{titulo.idTituloCatalografico}"/>		
											</h:commandLink>
										</td>
										
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				
				</c:if>
				
				<tfoot>
					<tr>
						<td colspan="7">
							<c:if test="${buscaCatalogacoesIncompletasMBean.pesquisaImportacao}">
								<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.telaPesquisaTitulo}" />
							</c:if>
							
							<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true"  action="#{buscaCatalogacoesIncompletasMBean.cancelar}"/>
						</td>
					</tr>
				</tfoot>
				
			</table>
		
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
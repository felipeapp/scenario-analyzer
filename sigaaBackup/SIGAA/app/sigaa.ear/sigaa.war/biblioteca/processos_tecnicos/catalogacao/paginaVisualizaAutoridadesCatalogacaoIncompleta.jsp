<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>  <ufrn:subSistema /> &gt; Cataloga��o &gt; Autoridades com Cataloga��es Incompletas</h2>

<div class="descricaoOperacao"> 
     <p>P�gina na qual � poss�vel visualizar as cataloga��es de Autoridades incompletas.</p> 
     <p>Cataloga��es incompletas s�o aquelas nas quais o catalogador salvou a Autoridade, mas n�o digitou 
     todos os campos obrigat�rios, ou foram importadas v�rias Autoridades em um mesmo arquivo, mas essas 
     Autoridades n�o foram trabalhadas ainda pelo catalogador.</p>
</div>

<f:view>

	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	<%-- Para voltar para a p�gina de busca e manter os dados da busca de autoridades --%>
	<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
	
	
	<h:form id="form">	
		
			<c:if test="${buscaCatalogacoesIncompletasMBean.qtdAutoridadesIncompletas > 0 || buscaCatalogacoesIncompletasMBean.pesquisaImportacao }">
				<div class="infoAltRem" style="width: 100%">
				
					<c:if test="${buscaCatalogacoesIncompletasMBean.pesquisaImportacao }">
						<h:graphicImage value="/img/biblioteca/importar.png" />
					 	<h:commandLink value="Importar Novas Autoridades" action="#{cooperacaoTecnicaImportacaoMBean.iniciarImportacaoAutoridadesDiretamente}" />
					 </c:if>
					 
					 <c:if test="${buscaCatalogacoesIncompletasMBean.qtdAutoridadesIncompletas > 0}">
						<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
						Selecionar Autoridade
						
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
						Apagar a Autoridade
					</c:if>
					
				</div>
			</c:if>
		
			<table class=formulario width="90%">
				<caption>Cataloga��es n�o Finalizadas de Autoridades (${buscaCatalogacoesIncompletasMBean.qtdAutoridadesIncompletas})</caption>
				
				<tr style="text-align:center;">
					
					<th colspan="1" class="obrigatorio" style="width: 50%">Apenas Minhas Autoridades ? </th>
					
					<td colspan="6" style="margin-bottom:20px; text-align:left;">
						
						<h:selectOneRadio value="#{buscaCatalogacoesIncompletasMBean.apenasMinhasCatalogacoesIncompletas}" onclick="submit()"
									valueChangeListener="#{buscaCatalogacoesIncompletasMBean.atualizaAutoridadeIncompletas}">  
	        				<f:selectItem itemLabel="SIM" itemValue="true" />
							<f:selectItem itemLabel="N�O" itemValue="false" />
	    				</h:selectOneRadio>
						
					</td>
				</tr>
				
				<c:if test="${buscaCatalogacoesIncompletasMBean.qtdAutoridadesIncompletas > 0}">
				
					<tr>
						<td colspan="7">
							<table class="subFormulario" style="width: 100%;">
					
								<thead>
									<tr>
										<td style="width: 10%; text-align: right;">N� do Sistema</td>
										<td colspan="2">Entrada Autorizada</td>
										<td>Importada</td>
										<td style="width: 30%">Criado por</td>
										<td width="1%"></td>
										<td width="1%"></td>
									</tr>
								</thead>
								
								<c:set var="autoridadesIncompletos" value="${buscaCatalogacoesIncompletasMBean.allAutoridadesIncompletas}" scope="request" />
								
								<c:forEach items="#{autoridadesIncompletos}" var="autoridade" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td style="text-align: right;">${autoridade.numeroDoSistema}</td>
										<td>${autoridade.entradaAutorizadaAutorComIndicacaoCampo}</td>
										<td>${autoridade.entradaAutorizadaAssuntoComIndicacaoCampo}</td>
										<td>
											${autoridade.importado ? 'Sim' : 'N�o'} 
										</td>
										<td>
											${autoridade.nomeUsuario} 
										</td>
										<td>
											<%-- Vai chamar o m�todo iniciaImportacao de catalogacaoMBean --%>
											<%-- Se o usu�rio iniciou pela cataloga��o por tombamento os dados do sipac j� v�o est� l� e o usu�rio vai poder incluir materiais  --%>
											<h:commandLink styleClass="noborder" title="Selecionar Autoridade" action="#{buscaCatalogacoesIncompletasMBean.iniciarCatalogacaoAutoridades}">
												<f:param name="idAutoridadeNaoFinalizada" value="#{autoridade.idAutoridade}"/>
												<h:graphicImage value="/img/seta.gif"/>
											</h:commandLink>
											
											<td>											
												<h:commandLink action="#{removerEntidadeDoAcervoMBean.telaConfirmaRemocaoVindoPaginaCatalogacoesIncompletasAutoridades}">
													<h:graphicImage url="/img/delete.gif" style="border:none" title="Clique aqui para remover a autoridade n�o catalogada do sistema" />
													<f:param name="idAutoridadeRemocao" value="#{autoridade.idAutoridade}"/>		
												</h:commandLink>
											</td>
											
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
								<h:commandButton value="<< Voltar" action="#{catalogaAutoridadesMBean.telaPesquisaAutoridades}" />
							</c:if>
							<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true"  action="#{buscaCatalogacoesIncompletasMBean.cancelar}"/>
						</td>
					</tr>
				</tfoot>
				
			</table>
		
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
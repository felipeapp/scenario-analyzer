<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.util.StringUtils"%>

<f:view>

    <%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<style type="text/css">
		
		.colunaCabecalho{
			text-align: left;
		}
		
		.colunaNumero{
			text-align: left;
			width: 10%
		}
		
		.colunaTitulo{
			text-align: left;
			width: 20%
		}
		
		.colunaAutor{
			text-align: left;
			width: 20%
		}
		
		.colunaAno{
			text-align: left;
			width: 10%
		}
		
		.colunaEdicao{
			text-align: left;
			width: 10%
		}
		
		.colunaIcone{
			text-align: center;
			width: 0.5%
		}
		
		.colunaBotoes{
			background: #C8D5EC none repeat scroll 0 0 
		}
		
		.caption{
			background:#EDF1F8 none repeat scroll 0 0;
			border-color:-moz-use-text-color -moz-use-text-color #C8D5EC;
			border-style:none none solid;
			border-width:0 0 1px;
			color:#333366;
			font-variant:small-caps;
			font-weight:bold;
			letter-spacing:1px;
			margin:1px 0;
			padding:3px 0 3px 20px;
			text-align:center;
			width: 80%;
		}
		
		.imitaSubFormularioTop{
			text-align:center; 
			border-top: #DEDFE3 1px solid ;
			border-left: #DEDFE3 1px solid ;
			border-right: #DEDFE3 1px solid ; 
			width: 98%;
			margin-left:auto; 
			margin-right: auto;
		}
		
		.imitaSubFormularioCentro{
			text-align:center; 
			border-left: #DEDFE3 1px solid ;
			border-right: #DEDFE3 1px solid ; 
			width: 98%;
			margin-left:auto; 
			margin-right: auto;
		}
		
		.imitaSubFormularioBottom{
			text-align:center; 
			border-left: #DEDFE3 1px solid ;
			border-right: #DEDFE3 1px solid ;
			border-bottom : #DEDFE3 1px solid ;
			width: 98%;
			margin-left:auto; 
			margin-right: auto;
			font-size: 16px;
		}
		
		table.subFormulario caption{
			border-left: #DEDFE3 1px solid ;
			border-right: #DEDFE3 1px solid ;
		}
		
	</style>
	
	<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js"> </script>


	<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
		<h2>  <ufrn:subSistema /> &gt; Exportar T�tulos </h2>
	</c:if>
	
	<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
		<h2>  <ufrn:subSistema /> &gt; Exportar Autoridades </h2>
	</c:if>
	
	<div class="descricaoOperacao">
	
		<p> Biblioteca de opera��o � a biblioteca de onde
			<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
				os t�tulos ser�o exportados. 
			</c:if>
			<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
				as autoridades ser�o exportadas. 
			</c:if>
		</p>
		
		<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
			<p> Todos os T�tulos catalogados ou alterados que ainda n�o foram exportados est�o marcados para exporta��o automaticamente pelo sistema. 
			Ao serem exportados, essa marca��o � removida. Pode-se tamb�m remover essa marca��o clicando no link <i>Remover T�tulo da Exporta��o</i></p>
			<p> Para adicionar outros T�tulos de maneira avulsa, utilize a pesquisa da p�gina. </p>
		</c:if>
	
		<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
			<p> Todas as Autoridades catalogadas ou alteradas que ainda n�o foram exportadas est�o marcadas para exporta��o automaticamente pelo sistema.
			Ao serem exportadas, essa marca��o � removida. Pode-se tamb�m remover essa marca��o clicando no link <i>Remover Autoridade da Exporta��o</i></p>
			<p> Para adicionar outras Autoridades de maneira avulsa, utilize a pesquisa da p�gina.</p>
		</c:if>
		
		<p> <strong>ISO 2709</strong> � a codifica��o padr�o para interc�mbio de informa��es catalogr�ficas, por�m se o arquivo
	contiver palavras acentuadas, essas palavras n�o ser�o geradas corretamente. Para arquivos escritos na l�ngua portuguesa d� prefer�ncia ao padr�o <strong>UTF-8</strong>.</p>
	</div>

	
	<a4j:keepAlive beanName="cooperacaoTecnicaExportacaoMBean"></a4j:keepAlive>

	
	<a4j:status startText="Aguarde..." stopText="">
        <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>			                			                				            
    </a4j:status>

	<h:form id="formExportaTitulos">		
			
			<%-- parte onde busca manualmente os t�tulos e autoridades para exporta��o --%>
			
			<div class="infoAltRem">
				<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
					<h:graphicImage value="/img/buscar.gif" />
					<h:commandLink action="#{cooperacaoTecnicaExportacaoMBean.iniciarPesquisaExportacao}" value="Pesquisar um T�tulo para Exporta��o"></h:commandLink>
					
				</c:if>
				
				<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
					<h:graphicImage value="/img/buscar.gif" />
					<h:commandLink action="#{catalogaAutoridadesMBean.iniciarPesquisaExportacaoAutoridade}" value="Pesquisar uma Autoridade para Exporta��o"></h:commandLink>
				</c:if>
			</div>
			
			<h:inputHidden id="hiddenTipoBusca" value="#{cooperacaoTecnicaExportacaoMBean.tipoBusca}"></h:inputHidden>
			
			<table class="formulario" width="80%">
				
				<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
					<caption> Entre com os n�meros do sistema dos T�tulos</caption>
				</c:if>
				
				<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
					<caption> Entre com os n�meros do sistema das Autoridades</caption>
				</c:if>
				
				<tr>
					<td>
						<input id="radio1" type="radio" name="radio" onclick="getEl('formExportaTitulos:hiddenTipoBusca').dom.value = 1;">
						<h:outputText value="N�mero do Sistema Individual:"></h:outputText>
   					</td>
					<td>
					
						<h:inputText id="inputTextNumeroSistema" value="#{cooperacaoTecnicaExportacaoMBean.numeroDoSistema}" maxlength="9"  
						onkeyup="return formatarInteiro(this);" onkeypress="return executaClickBotao(event, 'formExportaTitulos:botaoAdicionar')"
						onfocus="getEl('radio1').dom.checked = true; getEl('formExportaTitulos:hiddenTipoBusca').dom.value = 1; " />
						
						<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
							<ufrn:help> Adicione um t�tuto individualmente.</ufrn:help>
						</c:if>
						
						<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
							<ufrn:help> Adicione uma autoridade individualmente.</ufrn:help>
						</c:if>
						
					</td>
				</tr>
				
				<tr>
					<td>
						<input id="radio2" type="radio" name="radio" onclick="getEl('formExportaTitulos:hiddenTipoBusca').dom.value = 2;">
						<h:outputText value="Faixa de N�meros do Sistema:"></h:outputText>
						
   					</td>
					
					<td>
						
						<h:inputText id="inputTextPrimeiroNumeroSistema" value="#{cooperacaoTecnicaExportacaoMBean.primeiroNumeroSistema}" maxlength="9" 
							onkeyup="return formatarInteiro(this);" onkeypress="return executaClickBotao(event, 'formExportaTitulos:botaoAdicionar')"
							onfocus="getEl('radio2').dom.checked = true; getEl('formExportaTitulos:hiddenTipoBusca').dom.value = 2; " /> 
						a
						<h:inputText id="inputTextUltimoNumeroSistema" value="#{cooperacaoTecnicaExportacaoMBean.ultimoNumeroSistema}" maxlength="9" 
							 onkeyup="return formatarInteiro(this);" onkeypress="return executaClickBotao(event, 'formExportaTitulos:botaoAdicionar')"
							 onfocus="getEl('radio2').dom.checked = true; getEl('formExportaTitulos:hiddenTipoBusca').dom.value = 2; " />
						
						<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
							<ufrn:help> Adicione v�rios t�tulos pelo intervalo dos n�meros do sistema.</ufrn:help>
						</c:if>
						
						<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
							<ufrn:help> Adicione v�rias autoridades pelo intervalo dos n�meros do sistema.</ufrn:help>
						</c:if>
						
					</td>
				</tr>
				
				
				<tfoot>
					<tr>
						<td colspan="3">
							<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
								<h:commandButton style="margin-left:10px;" id="botaoAdicionar" title="Adicionar N�mero do Sistema"
									value="Adicionar T�tulo" actionListener="#{cooperacaoTecnicaExportacaoMBean.adicionarNumerodoSistemaTitulo}" 
									 />
							</c:if>				
							
							<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
								<h:commandButton style="margin-left:10px;" id="botaoAdicionar" title="Adicionar N�mero do Sistema"
									value="Adicionar Autoridade" actionListener="#{cooperacaoTecnicaExportacaoMBean.adicionarNumerodoSistemaAutoridade}" 
									
									 />
							</c:if>
							
							
							<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{cooperacaoTecnicaExportacaoMBean.cancelar}" />
							
						</td>
					</tr>
				</tfoot>
				
			</table>
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			<%--   parte da p�gina onde  mostra as entidades escolhidas para exporta��o, sendo salvas pelo sistema ou que o usu�rio adicionou   --%>
			
			<a4j:outputPanel ajaxRendered="true">

				 
				<div class="infoAltRem" style="margin-top:30px">
					<h:graphicImage value="/img/delete.gif" />: 
					
					<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
						Remover T�tulo da Exporta��o
					</c:if>
					
					<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
						Remover Autoridades da Exporta��o
					</c:if>
					
				</div>
				
				<table style="width:98%" class="formulario">
					<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
						<caption>T�tulos pendentes para Exporta��o ( ${cooperacaoTecnicaExportacaoMBean.quantidadeEntidadesPendentes} ) </caption>
					</c:if>
					
					<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
						<caption>Autoridades pendentes para Exporta��o ( ${cooperacaoTecnicaExportacaoMBean.quantidadeEntidadesPendentes} ) </caption>
					</c:if>
					
					<tr style="text-align:center;">
							
							<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
								<th colspan="1" class="obrigatorio" style="width: 50%">Apenas Meus T�tulos ? </th>
							</c:if>
							
							<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
								<th colspan="1" class="obrigatorio" style="width: 50%">Apenas Minhas Autoridades ? </th>
							</c:if>
							
							<td colspan="6" style="margin-bottom:20px; text-align:left;">
								
								<h:selectOneRadio value="#{cooperacaoTecnicaExportacaoMBean.apenasMinhasEntidadesPedentes}" onclick="submit()"
										valueChangeListener="#{cooperacaoTecnicaExportacaoMBean.atualizaEntidadesPendentes}">
									<f:selectItem itemLabel="SIM" itemValue="true" />
									<f:selectItem itemLabel="N�O" itemValue="false" />
								</h:selectOneRadio>
								
							</td>
					</tr>
					
				</table>
				
		
				<t:dataTable var="titulosExportacao" value="#{cooperacaoTecnicaExportacaoMBean.dataModelTitulos}" 
						rendered="#{cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica && cooperacaoTecnicaExportacaoMBean.quantidadeEntidadesPendentes > 0}"
						style="width: 98%; margin-right:auto; margin-left:auto; border: 1px solid #DEDFE3;"
						rowIndexVar="posicao" 
						columnClasses="colunaIcone, colunaNumero, colunaTitulo, colunaAutor, colunaAno, colunaEdicao, colunaIcone"
						headerClass="colunaCabecalho"
						footerClass="colunaBotoes"
						rowStyleClass="linhaPar, linhaImpar}">
				
					<h:column>
						<f:facet name="header">
							<h:selectBooleanCheckbox value="#{cooperacaoTecnicaExportacaoMBean.selecionaTodos}">
								<a4j:support event="onclick" actionListener="#{cooperacaoTecnicaExportacaoMBean.selecionarDeselecionarTodos}" />
							</h:selectBooleanCheckbox>
						</f:facet>
						<h:selectBooleanCheckbox value="#{titulosExportacao.selecionada}"></h:selectBooleanCheckbox>
					</h:column>
				
					<h:column>
						<f:facet name="header">
							<h:outputText value="N� Sistema:" />
						</f:facet>
						<h:outputText value="#{titulosExportacao.numeroDoSistema}"></h:outputText>
					</h:column>
				
					<h:column>
						<f:facet name="header">
							<h:outputText value="T�tulo:" />
						</f:facet>
						<h:outputText value="#{titulosExportacao.titulo}"></h:outputText>
					</h:column>
					
					<h:column>
						<f:facet name="header">
							<h:outputText value="Autor:" />
						</f:facet>
						<h:outputText value="#{titulosExportacao.autor}"></h:outputText>
					</h:column>
					
					<h:column>
						<f:facet name="header">
							<h:outputText value="Ano:" />
						</f:facet>
						
						<h:dataTable var="ano" value="#{titulosExportacao.anosFormatados}">
							<h:column>
								<h:outputText value="#{ano}"></h:outputText>
							</h:column>
						</h:dataTable>
						
					</h:column>
				
					<h:column>
						<f:facet name="header">
							<h:outputText value="Edi��o:" />
						</f:facet>
						<h:outputText value="#{titulosExportacao.edicao}"></h:outputText>
					</h:column>
					 
					<h:column>
						<h:commandLink action="#{cooperacaoTecnicaExportacaoMBean.removerEntidadesExportacao}" onclick="#{confirmDelete}">
							<h:graphicImage url="/img/delete.gif" style="border:none" title="Clique aqui para remover da exporta��o o t�tulo " />				
						</h:commandLink>
					</h:column>
				
				</t:dataTable>	
				
				
					
				<t:dataTable var="autoridadesExportacao" value="#{cooperacaoTecnicaExportacaoMBean.dataModelAutoridades}"
						rendered="#{cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades && cooperacaoTecnicaExportacaoMBean.quantidadeEntidadesPendentes > 0}"
						style="width: 98%; margin-right:auto; margin-left:auto; border: 1px solid #DEDFE3;"
						rowIndexVar="posicao" 
						columnClasses="colunaIcone, colunaNumero, colunaTitulo, colunaIcone"
						headerClass="colunaCabecalho"
						footerClass="colunaBotoes"
						rowStyleClass="linhaPar, linhaImpar}">
				
					<h:column>
						<f:facet name="header">
							<h:selectBooleanCheckbox value="#{cooperacaoTecnicaExportacaoMBean.selecionaTodos}">
								<a4j:support event="onclick" actionListener="#{cooperacaoTecnicaExportacaoMBean.selecionarDeselecionarTodos}" />
							</h:selectBooleanCheckbox>
						</f:facet>
						<h:selectBooleanCheckbox value="#{autoridadesExportacao.selecionada}"></h:selectBooleanCheckbox>
					</h:column>
				
					<h:column>
						<f:facet name="header">
							<h:outputText value="N� Sistema:" />
						</f:facet>
						<h:outputText value="#{autoridadesExportacao.numeroDoSistema}"></h:outputText>
					</h:column>
				
					<h:column>
						<f:facet name="header">
							<h:outputText value="Entra Autorizada:" />
						</f:facet>
						
						<h:outputText value="#{autoridadesExportacao.entradaAutorizadaAutorComIndicacaoCampo}"></h:outputText>
						<h:outputText value="#{autoridadesExportacao.entradaAutorizadaAssuntoComIndicacaoCampo}"></h:outputText>
						
					</h:column>
					 
					<h:column>
						<h:commandLink action="#{cooperacaoTecnicaExportacaoMBean.removerEntidadesExportacao}" onclick="#{confirmDelete}">
							<h:graphicImage url="/img/delete.gif" style="border:none" title="Clique aqui para remover da exporta��o a Autoridade " />				
						</h:commandLink>
					</h:column>
				
				</t:dataTable>
				
				
				
				
				
				
				
				
				
				
				<%-- parte onde escolhe os parametros da exporta��o --%>
				
				<c:if test="${ cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica && cooperacaoTecnicaExportacaoMBean.quantidadeEntidadesPendentes > 0
				|| ( cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades && cooperacaoTecnicaExportacaoMBean.quantidadeEntidadesPendentes > 0 ) }">
				
					<table class="subFormulario" width="98%">
						<caption>Par�metros Exporta��o</caption>
					</table>
	
					<div class="imitaSubFormularioTop">
						<h:outputText styleClass="obrigatorio" value="Biblioteca de Opera��o: "/>
						<h:selectOneMenu value="#{cooperacaoTecnicaExportacaoMBean.biblioteca}" id="somBiblioteca" required="true">
							<c:if test="${fn:length(cooperacaoTecnicaExportacaoMBean.bibliotecaList) > 1}">
								<f:selectItem itemLabel=" -- Selecione -- " itemValue="-1" />
							</c:if>  
							<f:selectItems value="#{cooperacaoTecnicaExportacaoMBean.bibliotecaList}"/>
	    				</h:selectOneMenu>  
					</div>	
					
					<div class="imitaSubFormularioCentro">
						<h:selectOneRadio style="font-weight:bold;margin-left:auto; margin-right:auto" value="#{cooperacaoTecnicaExportacaoMBean.tipoCodificacao}" disabled="#{cooperacaoTecnicaExportacaoMBean.exportarFGV}">
										<f:selectItem itemLabel="ISO 2709" 
												itemValue="I" />
										<f:selectItem itemLabel="UTF-8" 
												itemValue="U"/>
						</h:selectOneRadio>
					</div>
			
					<div class="imitaSubFormularioCentro">
						<h:outputText value="Exporta para o sistema Bibliodata da FGV: "/>
						<h:selectBooleanCheckbox value="#{cooperacaoTecnicaExportacaoMBean.exportarFGV}">
							<a4j:support actionListener="#{cooperacaoTecnicaExportacaoMBean.abilitarExportacaoFGV}" event="onclick"></a4j:support>
						</h:selectBooleanCheckbox>
					</div>
		
					<div class="imitaSubFormularioBottom">
						<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
							<h:outputText value="Ser� gerado um arquivo .CC" rendered="#{ cooperacaoTecnicaExportacaoMBean.exportarFGV == true }" />
						</c:if>
						<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
							<h:outputText value="Ser� gerado um arquivo .AC" rendered="#{ cooperacaoTecnicaExportacaoMBean.exportarFGV == true }" />
						</c:if>
					</div>

				</c:if>

	
				<c:if test="${ ( cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica && cooperacaoTecnicaExportacaoMBean.quantidadeEntidadesPendentes > 0 )
					|| ( cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades && cooperacaoTecnicaExportacaoMBean.quantidadeEntidadesPendentes > 0 ) }">
					
					<div style="text-align:center;">
						<table class="formulario" width="98%">
							<tfoot>
								<tr>
									<td>
									
										<h:commandButton value="Exportar" actionListener="#{cooperacaoTecnicaExportacaoMBean.exportarArquivo}" />
										
										<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoBibliografica}">
											<h:commandButton value="Remover Selecionados" action="#{cooperacaoTecnicaExportacaoMBean.removerTitulosSelecionados}" onclick="#{confirmDelete}"/>
										</c:if>				
										
										<c:if test="${cooperacaoTecnicaExportacaoMBean.cooperacaoAutoridades}">
											<h:commandButton value="Remover Selecionados" action="#{cooperacaoTecnicaExportacaoMBean.removerAutoridadesSelecionadas}" onclick="#{confirmDelete}"/>
										</c:if>
									
									</td>
								</tr>
							</tfoot>
						</table>
					</div>
				
				</c:if>
				
				<h:commandButton id="cmdVisualizarArquivo" value="Visualizar Arquivo" style="display: none;"
						actionListener="#{cooperacaoTecnicaExportacaoMBean.visualizarArquivo}" 
						rendered="#{cooperacaoTecnicaExportacaoMBean.outputStreamArquivo != null}"/>

			</a4j:outputPanel>

	</h:form>


	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

</f:view>


<script type="text/javascript">
	
		function checarRadioButton(){
			document.getElementById('formExportaTitulos:inputTextNumeroSistema').focus();
		}

		checarRadioButton();

		<%--
		Chama o m�todo que mostra o arquivo para o usu�rio automaticamente, para o usu�rio n�o precisar clicar no bot�o.
		--%>
		if ( $('formExportaTitulos:cmdVisualizarArquivo') != null ) {
			visualizarRelatorio();
		}

		<%--
		Depois que o arquivo � gerado esta fun��o chama o m�todo que mostra o arquivo para o usu�rio
		--%>
		function visualizarRelatorio(){
			$('formExportaTitulos:cmdVisualizarArquivo').click();
		}
		
</script>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
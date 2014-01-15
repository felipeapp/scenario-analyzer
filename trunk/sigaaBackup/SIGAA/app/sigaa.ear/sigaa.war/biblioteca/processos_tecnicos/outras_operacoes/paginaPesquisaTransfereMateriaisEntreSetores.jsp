<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">
	
	.codigoBarras{
		width: 15%;
	}
	
	.informacao{
		width: 65%;
	}
	
	.descricao{
		width: 18%;
	}
	
	.acao{
		width: 2%;
	}
	
</style>

<f:view>

<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Transferir Materiais Entre Setores </h2>

<div class="descricaoOperacao"> 
     <p>Caro usuário,</p>
     <p>
     	Esta funcionalidade permite alterar a situação de vários materiais de uma só vez, sendo usada principalmente para 
     	a transferência de materiais entre setores da biblioteca. Basta informar o código de barras de cada material e 
     	adicioná-lo à lista. Em seguida, deve ser informada a nova situação para os mesmos.
     </p>
     <p> <strong>Ao final da operação, será 
     	gerado um relatório</strong> para impressão, contendo a listagem dos materiais alterados.
     </p>
</div>

	<a4j:keepAlive beanName="transferirMateriaisEntreSetoresBibliotecaMBean" />

	<h:form id="formTransferirMateriaisEntreSetores">
	
		<h:inputHidden id="hiddenTipoBusca" value="#{transferirMateriaisEntreSetoresBibliotecaMBean.tipoBusca}"></h:inputHidden>
	
		<table id="tableListaMateriaisTransferencia" class="formulario" style="margin-bottom:30px; width: 53%;">		
			
			<caption>Adicionar Material Informacional</caption>
			
			<tr>
				<td>
						<input id="radio1" type="radio" name="radio" onclick="getEl('formTransferirMateriaisEntreSetores:hiddenTipoBusca').dom.value = 1;">
						<h:outputText value="Código de Barras:"></h:outputText>						
   				</td>
  					
				<td colspan="2"; style="text-align: left;">
						<h:inputText id="inputTxtCodBarras"  value="#{transferirMateriaisEntreSetoresBibliotecaMBean.codigoBarras}" 
							onkeypress="return executaClickBotao(event, 'formTransferirMateriaisEntreSetores:botaoAdicionar' )"
							onfocus="getEl('radio1').dom.checked = true; getEl('radio1').dom.checked = true; getEl('formTransferirMateriaisEntreSetores:hiddenTipoBusca').dom.value = 1; "
							maxlength="20" onkeyup="this.value = this.value.toUpperCase();" 
							onchange="this.value = this.value.toUpperCase();" 
							onblur="this.value = this.value.toUpperCase();" />
				</td>
			</tr>
			
			<tr>
					<td>
						<input id="radio2" type="radio" name="radio" onclick="getEl('formTransferirMateriaisEntreSetores:hiddenTipoBusca').dom.value = 2;">
						<h:outputText value="Faixa de Códigos Barras:" />
   					</td>
				
					<td>	
						<h:inputText value="#{transferirMateriaisEntreSetoresBibliotecaMBean.codigoBarrasInicial}" id="inputTxtCodBarrasInicial" maxlength="15" 
									onfocus="getEl('radio2').dom.checked = true; getEl('formTransferirMateriaisEntreSetores:hiddenTipoBusca').dom.value = 2; "
									onkeyup="this.value = this.value.toUpperCase();" 
									onchange="this.value = this.value.toUpperCase();" 
									onblur="this.value = this.value.toUpperCase();"/>
					</td>
					
					<th>a</th>
					
					<td>
						<h:inputText value="#{transferirMateriaisEntreSetoresBibliotecaMBean.codigoBarrasFinal}" id="inputTxtCodBarrasFinal" maxlength="15" 
									onfocus="getEl('radio2').dom.checked = true; getEl('formTransferirMateriaisEntreSetores:hiddenTipoBusca').dom.value = 2; "
									onkeyup="this.value = this.value.toUpperCase();" 
									onchange="this.value = this.value.toUpperCase();" 
									onblur="this.value = this.value.toUpperCase();"/>
					</td>
					
					<td>
						<ufrn:help>Adicionar materiais que estejam entre o intervalo de códigos de barras informado.</ufrn:help>
					</td>
					
				</tr>
						
			<tfoot>
				<tr>
					<td colspan="5" align="center">
						<a4j:commandButton id="botaoAdicionar" value="Adicionar" actionListener="#{transferirMateriaisEntreSetoresBibliotecaMBean.pesquisarMaterial}" 
							reRender="formTransferirMateriaisEntreSetores" oncomplete="focarInputTexto();"/>
						<h:commandButton id="cancelar" value="Cancelar" action="#{transferirMateriaisEntreSetoresBibliotecaMBean.telaVoltar}" onclick="#{confirm}" immediate="true"  />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		<a4j:outputPanel ajaxRendered="true">
		
			<c:if test="${fn:length( transferirMateriaisEntreSetoresBibliotecaMBean.materiais) > 0}">
				
				
				
				<div class="infoAltRem" style="margin-top: 10px">		
				    <h:graphicImage value="/img/delete.gif" />: Remover Material da Lista
				</div>
				
				<table id="tabelaMateriais" class="listagem">
					<caption> Materiais escolhidos ( ${fn:length( transferirMateriaisEntreSetoresBibliotecaMBean.materiais)} )</caption>
					<thead>
						<th style="text-align: left; width: 15%;">Código de Barras</th>
						<th style="text-align: left; width: 65%;">Referência</th>
						<th style="text-align: left; width: 18%;"> Situação</th>
						<th style="width: 2%;"> </th>
					</thead>
				</table>	
				
				<t:dataTable var="material" value="#{transferirMateriaisEntreSetoresBibliotecaMBean.materiais}" style="width: 100%; border: 1px solid #DEDFE3; border-bottom: 0px; "
					columnClasses="codigoBarras,informacao, situacao, acao">
					<t:column>
						<h:outputText value="#{material.codigoBarras}" />
					</t:column>
					<t:column>
						<h:outputText escape="false" value="#{material.informacao}" />
					</t:column>
					<t:column>
						<h:outputText value="#{material.situacao.descricao}" />
					</t:column>
					<t:column>
						<h:commandLink action="#{transferirMateriaisEntreSetoresBibliotecaMBean.removerMaterialSelecionadoDaLista}" 
							onclick="return confirm('Confirma a remoção desse material da lista ?');" >
							<h:graphicImage url="/img/delete.gif" style="border:none" title="Remover Material da Lista" />
							<f:param name="idMaterialRemocao" value="#{material.id}"/>				
						</h:commandLink>
					</t:column>
				</t:dataTable>
					
					<table id="tabelaMateriaisRodape" class="listagem" style="border-top: 0px; ">
						<tr>
							<td colspan="3"></td>
						</tr>
						
						<tr>
							<td colspan="3">
								<table width="100%" style="text-align: center">
									<tr>
										<th class="obrigatorio" width="50%">Nova Situação:</th>
										<td style="text-align: left" width="50%">
											<h:selectOneMenu id="novaSituacao" required="true" 
													value="#{transferirMateriaisEntreSetoresBibliotecaMBean.situacao}">
												<f:selectItem itemValue="-1" itemLabel="--SELECIONE--" />
												<f:selectItems value="#{transferirMateriaisEntreSetoresBibliotecaMBean.situacoesCombo}" />
											</h:selectOneMenu>
										</td>
									</tr>
									<tr>
										<th width="50%">Mostrar Relatório Simplificado:</th>
										<td style="text-align: left" width="50%">
											<h:selectBooleanCheckbox value="#{transferirMateriaisEntreSetoresBibliotecaMBean.mostraResumido}" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						
						<tr>
							<td colspan="3"></td>
						</tr>
						
						<tfoot>
							<tr>
								<td colspan="6" style="text-align: center">
								
									<h:commandButton id="cmdAlterarSituacao" value="Transferir Materiais" action="#{transferirMateriaisEntreSetoresBibliotecaMBean.realizarAlteracaoMateriais}" 
										onclick="ativaBotaoFalso();"/>
									
									<h:commandButton id="fakecmdAlterarSituacao" value="Transferindo Materiais ..." style="display: none;" disabled="true" />
								
									<h:commandButton value="Limpar Lista" action="#{transferirMateriaisEntreSetoresBibliotecaMBean.removerTodosMateriaisDaLista}" onclick="return confirm('Confirma a remoção de todos os materiais adicionados à lista ?');" />
								
									<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{transferirMateriaisEntreSetoresBibliotecaMBean.cancelar}" />
									
								</td>
							</tr>
						</tfoot>
						
				</table>
				
				
				<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
				
				
			</c:if>	
	
			
			<h:commandButton id="fakelinkAlterarSituacao" style="display: none;"
					actionListener="#{transferirMateriaisEntreSetoresBibliotecaMBean.visualizarRelatorio}"
					rendered="#{transferirMateriaisEntreSetoresBibliotecaMBean.realizouAlteracaoMateriais}">
			</h:commandButton>
			
		</a4j:outputPanel>
		
	</h:form>

	

</f:view>


<script type="text/javascript">

	focarInputTexto();

	function focarInputTexto(){
		document.getElementById('formTransferirMateriaisEntreSetores:inputTxtCodBarras').focus();
	}
	
	<%--
	Chama o método que mostra o arquivo para o usuário automaticamente, para o usuário não precisar clicar no botão.

	
	if ( $('formTransferirMateriaisEntreSetores:fakelinkAlterarSituacao') != null ) {
		visualizarRelatorio();
	} 	--%>
	
	<%--
	Depois que o arquivo é gerado esta função chama o método que mostra o arquivo para o usuário, depois de meio segundo
	--%>
	function visualizarRelatorio() {
		if ( $('formTransferirMateriaisEntreSetores:fakelinkAlterarSituacao') != null ) {
			$('formTransferirMateriaisEntreSetores:fakelinkAlterarSituacao').click();
		}
	}

	function ativaBotaoFalso() {
		if ( $('formTransferirMateriaisEntreSetores:cmdAlterarSituacao')!= null ) {
			$('formTransferirMateriaisEntreSetores:cmdAlterarSituacao').hide();
		}
		
		if ( $('formTransferirMateriaisEntreSetores:fakecmdAlterarSituacao') != null ) {
			$('formTransferirMateriaisEntreSetores:fakecmdAlterarSituacao').show();
		}
		
	}
	
	ativaBotaoVerdadeiro();
	
	function ativaBotaoVerdadeiro() {
		if ( $('formTransferirMateriaisEntreSetores:cmdAlterarSituacao') != null ) {
			$('formTransferirMateriaisEntreSetores:cmdAlterarSituacao').show();
		}
		if ( $('formTransferirMateriaisEntreSetores:fakecmdAlterarSituacao') != null ) {
			$('formTransferirMateriaisEntreSetores:fakecmdAlterarSituacao').hide();
		}
		
		
	}
	
	
	//função que executa o click no botao passado quando o usuário pressiona o enter
	function executaClickBotao(evento, idBotao) {
		
		var tecla = "";
		if (isIe())
			tecla = evento.keyCode;
		else
			tecla = evento.which;

		if (tecla == 13){
			document.getElementById(idBotao).click();
			return false;
		}
		
		return true;
		
	}	

	// testa se é o IE ou não
	function isIe() {
		return (typeof window.ActiveXObject != 'undefined');
	}
		
</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
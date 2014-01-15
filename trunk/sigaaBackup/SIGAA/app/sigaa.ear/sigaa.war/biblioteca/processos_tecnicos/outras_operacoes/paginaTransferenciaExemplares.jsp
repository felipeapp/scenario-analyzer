<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>  <ufrn:subSistema /> &gt; Buscar Exemplar para Realizar a Transfer�ncia </h2>

<f:view>

<div class="descricaoOperacao"> 
   <p>P�gina para transfer�ncia de exemplares entre bibliotecas. </p>
   <p>Somente exemplares que n�o estejam emprestados podem ser transferidos, para n�o influenciar no empr�stimo.</p>
   <p><strong> Caso os exemplares transferidos estejam tombados no patrim�nio da institui��o, por padr�o ser� gerado um <i>Chamado Patrimonial</i> 
   para sincronizar essa informa��o com as informa��es do tombamento no <h:outputText value="#{transfereExemplaresEntreBibliotecasMBean.nomeSistemaPatrimonio}" />. </strong> </p>
</div>


	<a4j:keepAlive beanName="transfereExemplaresEntreBibliotecasMBean" />	

	<h:form id="formPesquisaExemplaresTransferencia">	

		<div class="infoAltRem" style="margin-top: 10px">		
		    <h:graphicImage value="/img/biblioteca/pesquisar_titulo.png" />
			<h:commandLink action="#{transfereExemplaresEntreBibliotecasMBean.buscarExemplaresPorTitulo}" value="Pesquisar Exemplares por T�tulo" />
		</div>
		
		<h:inputHidden id="hiddenTipoBusca" value="#{transfereExemplaresEntreBibliotecasMBean.tipoBusca}"></h:inputHidden>
	
	
		<table class=formulario style="margin-bottom:30px; width: 65%;">		
			
			<caption class="listagem">Adicionar Exemplar</caption>
			
			<tr>
				<td style="width: 38%;">
					<input id="radio1" type="radio" name="radio" onclick="getEl('formPesquisaExemplaresTransferencia:hiddenTipoBusca').dom.value = 1;">
					<h:outputText value="C�digo de Barras:"></h:outputText>						
  					</td>

				<td colspan="2" style="width: 1%;">
					<h:inputText id="inputTxtCodBarras"  value="#{transfereExemplaresEntreBibliotecasMBean.codigoBarras}" maxlength="20" onkeypress="return getCodigoTecla(event)" 
						onfocus="getEl('radio1').dom.checked = true; getEl('formPesquisaExemplaresTransferencia:hiddenTipoBusca').dom.value = 1; "/>
				</td>
				
			</tr>
			
			<tr>
				<td style="width: 38%;">
					<input id="radio2" type="radio" name="radio" onclick="getEl('formPesquisaExemplaresTransferencia:hiddenTipoBusca').dom.value = 2;">
					<h:outputText value="Faixa de C�digos de Barras:"></h:outputText>
  				</td>
			
				<td style="width: 1%;">	
					<h:inputText value="#{transfereExemplaresEntreBibliotecasMBean.codigoBarrasInicial}" id="inputTxtCodBarrasInicial" maxlength="15" 
						onfocus="getEl('radio2').dom.checked = true; getEl('formPesquisaExemplaresTransferencia:hiddenTipoBusca').dom.value = 2; "/>
				</td>
				
				<th style="text-align: center;">a</th>
				
				<td>
					<h:inputText value="#{transfereExemplaresEntreBibliotecasMBean.codigoBarrasFinal}" id="inputTxtCodBarrasFinal" maxlength="15" 
							onfocus="getEl('radio2').dom.checked = true; getEl('formPesquisaExemplaresTransferencia:hiddenTipoBusca').dom.value = 2; "/>
					<ufrn:help>Adicionar exemplares<br/> que estejam entre o<br/> intervalo de c�digos<br/> de barras informado.</ufrn:help>
				</td>
				
			</tr>
						
			<tfoot>
				<tr>
					<td colspan="4" align="center">
						<h:commandButton value="Adicionar" action="#{transfereExemplaresEntreBibliotecasMBean.adicionarCodigoBarras}" id="botaoAdicionarMaterial"/>
						<h:commandButton value="Cancelar" action="#{transfereExemplaresEntreBibliotecasMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
		<c:if test="${fn:length( transfereExemplaresEntreBibliotecasMBean.exemplares) > 0}">
			
			<div class="infoAltRem" style="margin-top: 10px">		
			    <h:graphicImage value="/img/delete.gif" />: Remover exemplar da lista dos exemplares que v�o ser transferidos	
			</div>
			
			<table class="listagem">
				<caption> Exemplares escolhidos para transfer�ncia de biblioteca ( ${fn:length( transfereExemplaresEntreBibliotecasMBean.exemplares)} )</caption>	
				
				<thead>
					<th style="text-align: left"> C�digo de Barras</th>
					<th style="text-align: left"> Biblioteca</th>
					<th style="text-align: left"> Status</th>
					<th style="text-align: left"> Situa��o</th>
					<th style="text-align: left"> Tipo Material</th>
					<th> </th>
				</thead>
				
				<c:forEach items="#{transfereExemplaresEntreBibliotecasMBean.exemplares}" var="exemplar" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td style="${exemplar.selecionado ? "color:red; font-weight:bold;" : " "}">${exemplar.codigoBarras}</td>
						<td style="${exemplar.selecionado ? "color:red; font-weight:bold;" : " "}">${exemplar.biblioteca.descricao}</td>
						<td style="${exemplar.selecionado ? "color:red; font-weight:bold;" : " "}">${exemplar.status.descricao}</td>
						<td style="${exemplar.selecionado ? "color:red; font-weight:bold;" : " "}">${exemplar.situacao.descricao}</td>
						<td style="${exemplar.selecionado ? "color:red; font-weight:bold;" : " "}">${exemplar.tipoMaterial.descricao}</td>
						<td>
							<h:commandLink action="#{transfereExemplaresEntreBibliotecasMBean.removerExemplarDaLista}" onclick="#{confirmDelete}">
								<h:graphicImage url="/img/delete.gif" style="border:none" title="Clique aqui para remover da lista o exemplar" />
								<f:param name="idExemplarRemocao" value="#{exemplar.id}"/>				
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>		
				
				<tr>
					<td colspan="6">
						<table class="subFormulario" style="width: 100%">
							<tr>
								<th style="width: 30%;">Gerar Chamado Patrimonial ? </th>
								<td style="width: 70%">
									<h:selectOneMenu value="#{transfereExemplaresEntreBibliotecasMBean.gerarChamadoPatrimonial}">  
				        				<f:selectItem itemLabel="SIM" itemValue="true" />
				        				<f:selectItem itemLabel="N�O" itemValue="false" />
				    				</h:selectOneMenu>
			    				</td>
			    			</tr>
			    		</table>
					</td>
				</tr>
				
				<tr>
					<td colspan="6">
						<table class="subFormulario" style="width: 100%">
							<tr>
								<th style="width: 30%">Biblioteca Destino dos Exemplares:</th>
								<td style="width: 70%">
									<h:selectOneMenu value="#{transfereExemplaresEntreBibliotecasMBean.bibliotecaDestino.id}">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
										<f:selectItems value="#{transfereExemplaresEntreBibliotecasMBean.bibliotecasInternasAtivas}"/>
									</h:selectOneMenu>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="6" style="text-align: center">
						
							<h:commandButton value="Transferir" action="#{transfereExemplaresEntreBibliotecasMBean.transfeExemplares}" 
										onclick="return confirm('Confirma transfer�ncia de Biblioteca? ');"/>
							<h:commandButton value="Remover Todos os Exemplares" action="#{transfereExemplaresEntreBibliotecasMBean.removerTodosExemplaresDaLista}" 
										onclick="return confirm('Confirma remo��o de todos os exemplares ? ');"/>
							<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{transfereExemplaresEntreBibliotecasMBean.cancelar}" />
							
						</td>
					</tr>
				</tfoot>
				
			</table>
			
		</c:if>	
			
	</h:form>

</f:view>



<script type="text/javascript">

	checarRadioButton();    // executa quando a p�gina � carregada

	function checarRadioButton(){
		document.getElementById('formPesquisaExemplaresTransferencia:inputTxtCodBarras').focus();
	}
	


	function selecionarTudo(chk){
	   for (i=0; i<document.resultadoPesquisaFasciculosTransferencia.elements.length; i++)
	      if(document.resultadoPesquisaFasciculosTransferencia.elements[i].type == "checkbox")
	         document.resultadoPesquisaFasciculosTransferencia.elements[i].checked= chk.checked;
	}

</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
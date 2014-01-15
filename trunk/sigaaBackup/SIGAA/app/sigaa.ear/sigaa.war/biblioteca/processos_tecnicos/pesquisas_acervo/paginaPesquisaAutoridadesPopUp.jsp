<%@include file="/biblioteca/cabecalho_popup.jsp"%>

<script type="text/javascript">

function habilitarVisualizarDadosMarc(idAutoridade) {
	var linha = 'linha_'+ idAutoridade; // o id da linha da tabela
	
	if ( $(linha).style.display != 'table-cell' && $(linha).style.display != 'inline-block' ) {       //$() == getElementById()
		if ( !Element.hasClassName(linha, 'populado') ) {
			
			
			new Ajax.Request("/sigaa/biblioteca/processos_tecnicos/pesquisas_acervo/infoMarc.jsf?idAutoridade=" + idAutoridade, {
				onComplete: function(transport) {
					$(linha).innerHTML = transport.responseText;
					Element.addClassName(linha, 'populado');
				}
			});
			
		}
		
		if (/msie/.test( navigator.userAgent.toLowerCase() )){
			$(linha).style.display = 'inline-block';
		}else{
			$(linha).style.display = 'table-cell';
		}
		
		
	} else {
		$(linha).style.display = 'none';
	}
}

</script>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>


<style type="text/css">

table.tabelaInterna tbody{
	background-color: transparent;
}
</style>

<h2> Biblioteca &gt;  Pesquisar Autoridades no Acervo </h2>


<f:view>

	<h:form id="pesquisaAutoridadesPopUp">
	
	
		<table id="tableDadosPesquisa" class="formulario" width="80%" style="margin-bottom: 10px">
		
			<caption>Selecione os campos para a busca</caption>	
		
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisaPopUpAutoridadeMBean.buscarNumeroSistema}" styleClass="noborder" id="checkNumeroSistema"/>
				</td>
				<th  width="30%" style="text-align:left"> Número do Sistema: </th>
		
				<td  width="70%" colspan="6">
					<h:inputText value="#{pesquisaPopUpAutoridadeMBean.numeroDoSistema}" size="10"  maxlength="9" 
								onchange="marcarCheckBox(this, 'pesquisaAutoridadesPopUp:checkNumeroSistema');"
								onkeyup="return formatarInteiro(this);" > </h:inputText>
				</td>
			</tr>
		
			<tr>
				<a4j:region rendered="#{! pesquisaPopUpAutoridadeMBean.pesquisaSelecaoAutoridadeAssunto}">
					<td width="2%">
						<input type="radio" name="radioCampoAutoridades" value="buscarAutor" id="radioAutorAutorizado"   ${pesquisaPopUpAutoridadeMBean.buscarAutor ? 'checked="checked"' : '' }  />
						<%-- <h:selectBooleanCheckbox value="#{pesquisaPopUpAutoridadeMBean.buscarAutor}" styleClass="noborder" id="checkAutorizadoAutor"/> --%>
					</td>
					<th style="text-align:left">
						Autor:
					</th>
			
					<td colspan="6">
						<h:inputText id="inputTxtEntraAutorizadaAutor" value="#{pesquisaPopUpAutoridadeMBean.autor}" 
							size="60" maxlength="50" 
							onchange="marcarCheckBox(this, 'radioAutorAutorizado');"> </h:inputText>
	
					</td>
				</a4j:region>
			</tr>
		
			<tr>
				<a4j:region rendered="#{!pesquisaPopUpAutoridadeMBean.pesquisaSelecaoAutoridadeAutor}">
					<td width="2%">
						<input type="radio" name="radioCampoAutoridades" value="buscarAssunto" id="radioAssuntoAutorizado"  ${pesquisaPopUpAutoridadeMBean.buscarAssunto ? 'checked="checked"' : '' } />
						<%-- <h:selectBooleanCheckbox value="#{pesquisaPopUpAutoridadeMBean.buscarAssunto}" styleClass="noborder" id="checkAutorizadoAssunto"/>  --%>
					</td>
					<th style="text-align:left">
						Assunto:
					</th>
			
					<td colspan="6">
						<h:inputText id="inputTxtEntraAutorizadaAssunto" value="#{pesquisaPopUpAutoridadeMBean.assunto}" 
							size="60" maxlength="50" 
							onchange="marcarCheckBox(this, 'radioAssuntoAutorizado');"> </h:inputText>
					</td>
					
				</a4j:region>
			</tr>
	
			<%-- 
			<tr>
				<a4j:region rendered="#{!pesquisaPopUpAutoridadeMBean.pesquisaSelecaoAutoridadeAssunto}">
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaPopUpAutoridadeMBean.buscarRemissivoAutor}" styleClass="noborder" id="checkRemissivoAutor"/>
					</td>
					<th style="text-align:left">
						Entradas Remissivas Autor:
					</th>
			
					<td  colspan="6">
						<h:inputText value="#{pesquisaPopUpAutoridadeMBean.nomeRemissivoAutor}" 
							size="60" maxlength="50" 
							onchange="marcarCheckBox(this, 'pesquisaAutoridadesPopUp:checkRemissivoAutor');"> </h:inputText>
					</td>
				</a4j:region>
			</tr>
			
			<tr>
				<a4j:region rendered="#{!pesquisaPopUpAutoridadeMBean.pesquisaSelecaoAutoridadeAutor}">
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaPopUpAutoridadeMBean.buscarRemissivoAssunto}" styleClass="noborder" id="checkRemissivoAssunto"/>
					</td>
					<th style="text-align:left">
						Entradas Remissivas Assunto:
					</th>
			
					<td  colspan="6">
						<h:inputText value="#{pesquisaPopUpAutoridadeMBean.nomeRemissivoAssunto}" 
							size="60"  maxlength="50" 
							onchange="marcarCheckBox(this, 'pesquisaAutoridadesPopUp:checkRemissivoAssunto');"> </h:inputText>
					</td>
				</a4j:region>
			</tr> --%>
			
			<tr>
				<td></td>
				<th style="text-align:left">Ordenação:</th>
				<td colspan="2">
					<h:selectOneMenu value="#{pesquisaPopUpAutoridadeMBean.valorCampoOrdenacao}">
						<f:selectItems value="#{pesquisaPopUpAutoridadeMBean.campoOrdenacaoResultadosComboBox}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td></td>
				<th style="text-align:left">Registros por página:</th>
				<td colspan="2">
					<h:selectOneMenu value="#{pesquisaPopUpAutoridadeMBean.quantideResultadosPorPagina}">
						<f:selectItems value="#{pesquisaPopUpAutoridadeMBean.qtdResultadosPorPaginaComboBox}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="8">
					
						<h:commandButton value="Pesquisar" action="#{pesquisaPopUpAutoridadeMBean.pesquisarAutoridadeSimples}"  />
						
						<h:commandButton value="Limpar" action="#{pesquisaPopUpAutoridadeMBean.apagarDadosPesquisa}" />
						
						<h:commandButton value="Fechar" onclick="javascript:window.close();" immediate="true" />
					</td>
				</tr>
			</tfoot>
		
		
		</table>
			
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
				
		<%--       Resultados da pesquisa     --%>	
			
			
		<t:div id="divResultadoPesquisaAutoridade" rendered="#{pesquisaPopUpAutoridadeMBean.quantidadeTotalResultados > 0}">
	
			<div class="infoAltRem" style="margin-top: 10px">

				<h:graphicImage value="/img/biblioteca/visualizarMarc.png" style="overflow: visible;" />: 
				Visualizar as Informações MARC da Autoridade 

			</div> 
	
	
	
			<%-- Tabela para mostrar o cabeçalho já que os dados estão sendo mostrados em uma rich datable eu não --%>
			<%-- E é mais difícil tentar formatar a rich dataTable para ter um estido igual do das tabelas padroes usadas --%>
	
			<table class="listagem">
				<caption>  Autoridades Encontradas ( <h:outputText value="#{pesquisaPopUpAutoridadeMBean.quantidadeResultadosMostrados}"/> )</caption>
				<thead>
					<tr>
						<th style="width: 1%; text-align: center">  </th>
						<th style="width: 10%; text-align: right;"> Nº do Sistema </th>
						<th style="width: 45%;"> Entrada Autorizada </th>
						<th style="width: 42%;"> Entradas Remissivas </th>
						
					
						<th style="width: 1%; text-align: center">  </th>
						
						
					</tr>
				</thead>

				

				<c:forEach items="#{pesquisaPopUpAutoridadeMBean.resultadosPaginadosEmMemoria}" var="autoridade" varStatus="status">

					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">


						<td width="1%" style="text-align:center">
							<a href="#buscarAutoridade" onClick="habilitarVisualizarDadosMarc(${autoridade.idAutoridade})" title="Visualizar dados MARC da Autoridade">
									<img src="${ctx}/img/biblioteca/visualizarMarc.png"/>
							</a>
						</td>

						<c:if test="${autoridade.entradaAutorizadaAutor != null }">

							<td style="${autoridade.catalogado ? " " : "color:red"}; vertical-align:top; text-align: right;" width="10%">
								${autoridade.numeroDoSistema}
							</td>

							<td style="${autoridade.catalogado ? " " : "color:red"}; vertical-align:top" width="12%">
								${autoridade.entradaAutorizadaAutorComIndicacaoCampo}
							</td>
							
							<td width="12%" style="text-align:center">
							
								<table width="100%" class="tabelaInterna">
									<c:forEach items="${autoridade.nomesRemissivosAutorFormatados}" var="nomeRemissivo">
									<tr>
										<td style="${autoridade.catalogado ? " " : "color:red"}">
											${nomeRemissivo}
										</td>
									</tr>
									</c:forEach>
								</table>
							</td>
						
						</c:if>

						<c:if test="${autoridade.entradaAutorizadaAutor == null }">	
							<td style="${autoridade.catalogado ? " " : "color:red"}; vertical-align:top; text-align: right;" width="10%">
								${autoridade.numeroDoSistema}
							</td>

							<td style="${autoridade.catalogado ? " " : "color:red"}; vertical-align:top" width="12%">
								${autoridade.entradaAutorizadaAssuntoComIndicacaoCampo}
							</td>
							
							<td width="12%" style="text-align:center">
							
								<table width="100%" class="tabelaInterna">
									<c:forEach items="${autoridade.nomesRemissivosAssuntoFormatados}" var="nomeRemissivo">
									<tr>
										<td style="${autoridade.catalogado ? " " : "color:red"}">
											${nomeRemissivo}
										</td>
									</tr>
									</c:forEach>
								</table>
							</td>
							
						</c:if>	

					</tr>
				
					<%-- A linha da tabela que mostra os detalhes do exemplar e é habilitado usando JavaScript. --%>
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"> 
						<td colspan="4" id="linha_${autoridade.idAutoridade}" style=" display: none;" ></td>
					</tr>
					
				
				</c:forEach>

				<tfoot>
					<tr>
						<td colspan="11" style="text-align: center; font-weight: bold;">
							<h:outputText value="#{pesquisaPopUpAutoridadeMBean.quantidadeResultadosMostrados}"/> autoridade(s).
						</td>
					</tr>
				</tfoot>

				
				
			</table>	
			</t:div>
	
	
	</h:form>
	
</f:view>


<%@include file="/biblioteca/rodape_popup.jsp"%>	

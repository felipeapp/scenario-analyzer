

<%--  Incluida na pagina de gerenciamento de materiais com o formulario de informacoes dos exemplares --%>


<style type="text/css">

table.listagem tr.biblioteca td{
		background: #C4D2EB;
		font-weight: bold;
		padding-left: 20px;
	}

table.listagem tr.qtdExemplares td{
		background: #EEEEEE;
		font-weight: bold;
		padding-left: 20px;
		text-align: center;
		font-style: italic;
	}

</style>


<table style="margin-bottom:30px; width: 100%; text-align: right;">
	<tr>
		<c:if test="${fn:length( materialInformacionalMBean.exemplaresDoTitulo) <= 0 }">
			<td style="text-align:center; color:red"> T�tulo n�o possui exemplares</td>
		</c:if>
		
		<c:if test="${fn:length( materialInformacionalMBean.exemplaresDoTitulo) > 0 }">
			<td> 
				<h:commandLink id="cmdLinkHabilitaVisualizacaoExemplares" value="#{materialInformacionalMBean.textoLinkHabilitarExemplares}">					
					<a4j:support event="onclick" actionListener="#{materialInformacionalMBean.habilitaVisualizacaoExemplares}" reRender="divInformacoesExemplaresTitulo"> </a4j:support>
				</h:commandLink>
		</c:if>
	</tr>
</table>


<t:div id="divInformacoesExemplaresTitulo"   rendered="#{materialInformacionalMBean.mostrarExemplaresDoTitulo}"> 

	<div class="infoAltRem" style="margin-top: 10px;" id="divLegendaTabela" >
		
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
		Alterar as informa��es do Exemplar		
		
		<h:graphicImage value="/img/biblioteca/anexo.png" style="overflow: visible;" />: 
		Incluir anexos ao exemplar
		
	</div>

	<%-- Tabela que mostra os exemplares ja cadastrado para aquele titulo --%>
	<table class="listagem" style="margin-bottom:30px; width: 100%" id="tabelaExemplaresDoTitulo">
		
		<caption> Exemplares j� inclu�dos para o T�tulo ( ${fn:length(materialInformacionalMBean.exemplaresDoTitulo)} )</caption>
		
		<thead >
			<tr align="center" style="none;">
				<th width="10%" style="text-align: right; padding-right: 10px;">N� Patrim�nio</th>	
				<th width="14%">C�d. de Barras</th>	
				<th width="15%">N� Chamada</th>
				<th width="15%">Cole��o</th>
				<th width="9%">Status</th>
				<th width="2%" style="text-align: center;">N� Vol.</th>
				<th width="2%" style="text-align: center;">Tomo</th>
				<th width="1%" style="text-align: center;"></th>
				<th width="1%" style="text-align: center;"></th>
			</tr>
		</thead>
		
		<tbody>
		
			<c:set var="idBibliotecaExemplarInclusao" value="-1" scope="request" />
			<c:set var="qtdExemplaresBiblioteca" value="0" scope="request" />
			<c:forEach items="#{materialInformacionalMBean.exemplaresDoTitulo}" var="exemplar" varStatus="loop">
	
				
	
				<c:set var="mostraQuantidadeExemplares" value="false" scope="request" />
	
				<c:if test="${ idBibliotecaExemplarInclusao != exemplar.biblioteca.id}">
					<c:set var="idBibliotecaExemplarInclusao" scope="request" value="${exemplar.biblioteca.id}" />
					
					<c:if test="${ requestScope.qtdExemplaresBiblioteca > 0 }">
						<tr class="qtdExemplares">
							<td colspan="9"> Quantidade de Exemplares ( ${requestScope.qtdExemplaresBiblioteca} ) </td>
						</tr>
					</c:if>
					
					<tr class="biblioteca">
						<td colspan="9">${exemplar.biblioteca.descricao} </td>
					</tr>
					
					<c:set var="qtdExemplaresBiblioteca" value="0" scope="request" />
					
				</c:if>
	
				<c:set var="qtdExemplaresBiblioteca" value="${requestScope.qtdExemplaresBiblioteca+1}" scope="request" />
	
	
				<tr>
	
					<td style="text-align: right; padding-right: 10px;  ">
						${exemplar.numeroPatrimonio}
					</td>
	
					<td style="text-align: left;">
						${exemplar.codigoBarras}
					</td>	
	
					<td>
						${exemplar.numeroChamada}
					</td>
	
					<td>
						${exemplar.colecao.descricao}
					</td>
	
					<td>
						${exemplar.status.descricao}
					</td>
	
					<td style="text-align: center;" >
						<c:if test="${exemplar.numeroVolume != null}">
							${exemplar.numeroVolume}
						</c:if>
						<c:if test="${exemplar.numeroVolume == null}">
							U <%-- U de �nico --%>
						</c:if>
					</td>
					
					<td style="text-align: center;" >
						<c:if test="${exemplar.tomo != null}">
							${exemplar.tomo}
						</c:if>
						<c:if test="${exemplar.tomo == null}">
							U <%-- U de �nico --%>
						</c:if>
					</td>
					
					<td>
						<h:commandLink id="cmdLinkEditarExemplar" action="#{editaMaterialInformacionalMBean.iniciarParaEdicaoExemplares}">
							<h:graphicImage url="/img/alterar.gif" style="border:none"
								title="Clique aqui para editar as informa��es desse Exemplar" />
	
							<f:param name="idExemplarParaEdicao" value="#{exemplar.id}"/>
							<f:param name="retornaPaginaInclusaoExemplar" value="true"/>				
						</h:commandLink>
					</td>
					
					<td>
						<%-- se nao � anexo pode adicionar algum anexo --%>
						<%-- por enquanto n�o vai existir anexo de anexo --%>
						<c:if test="${! exemplar.anexo}"> 
					
							<h:commandLink id="cmdLinkIncluirAnexoExemplar" action="#{materialInformacionalMBean.preparaIncluirAnexo}">
								<h:graphicImage url="/img/biblioteca/anexo.png" style="border:none" title="Clique aqui para incluir um anexo a esse exemplar" />
								<f:param name="idExemplarPrincipal" value="#{exemplar.id}"/>					
							</h:commandLink>
							
						</c:if>
					</td>
					
				</tr>
					
			</c:forEach>
		
			<%-- Precisa mostrar a quantidade de exemplares da �ltima biblioteca depois que o c:forEach acaba --%>
			<tr class="qtdExemplares">
				<td colspan="9"> Quantidade de Exemplares ( ${requestScope.qtdExemplaresBiblioteca} ) </td>
			</tr>
		
		<%--
			<c:if test="${fn:length( materialInformacionalMBean.exemplaresDoTitulo) <= 0 }">
				<tr>
					<td colspan="7" style="text-align:center; color:red"> T�tulo n�o possui exemplares</td>
				</tr>
			</c:if>  --%>
		
		</tbody>
		
	</table>


</t:div>

<%--   Formulario para inclusao de um novo exemplar   --%>


<c:if test="${materialInformacionalMBean.quanidadeMateriaisNaoUsados == 0 && ! materialInformacionalMBean.incluindoMateriaisSemTombamento }" >

	<table class="formulario" width="100%">
		<tfoot>
				<tr>
					<td colspan="4" align="center">
						
						<c:if test="${materialInformacionalMBean.incluindoMateriaisSemTombamento}">
							<c:if test="${materialInformacionalMBean.incluindoMateriaisSemTombamento}"> 
								<h:commandButton id="cmdButtonVoltarTelaPesquisaSemTombamentoTituloSemExemplares" value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisSemTombamento}" />
							</c:if>
						
							<c:if test="${! materialInformacionalMBean.incluindoMateriaisSemTombamento}"> 
									<h:commandButton  id="cmdButtonVoltarTelaPesquisaComTombamentoTituloSemExemplares" value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisComTombamento}" />
							</c:if>
						</c:if>
						
						<h:commandButton id="cmdBottonEditarCatalogacaoExemplaresNoItems" value="Editar a Cataloga��o dos Exemplares" action="#{materialInformacionalMBean.voltarTelaAtualizarTitulo}" />
						<h:commandButton value="Cancelar" action="#{materialInformacionalMBean.cancelar}" immediate="true" id="cancelar"  onclick="#{confirm}"/>			
					</td>
				</tr>
			</tfoot>
	</table>
	
</c:if>

<c:if test="${materialInformacionalMBean.quanidadeMateriaisNaoUsados > 0 || materialInformacionalMBean.incluindoMateriaisSemTombamento }" >

	<table class="formulario" width="100%">
				
		<tbody>
			
			<caption>Informa��es do Exemplar</caption>
			
			<%-- informacoes do patrimonio numca podem ser mudadas, se criou errado tem que apagar o item --%>		
			
				<tr>
					<c:if test="${! materialInformacionalMBean.incluindoMateriaisSemTombamento}">
						<th class="required">N�mero do Patrim�nio:</th>
						<td>
							<h:selectManyListbox   id="intputListCodigosBarras" value="#{materialInformacionalMBean.idsExemplaresEscolhidos}" size="5" style="font-size: 16px; width: 30%;">
					   			<f:selectItems value="#{materialInformacionalMBean.allNumeroPatrimonioNaoUsadosComboBox}"/>
							</h:selectManyListbox>
							<ufrn:help> <p>Pode-se selecionar v�rios c�digo de barras aleatoriamente pressionando "Ctrl". </p> <p> Selecionando-se mais de um, os exemplares v�o ser criados todos com as mesmas informa��es. </p></ufrn:help>
						</td>
					</c:if>
					
					<c:if test="${ materialInformacionalMBean.incluindoMateriaisSemTombamento }" >
						<th class="required">C�digo de Barras:</th>
						<td>
							<h:inputText id="intputTextCodigoBarras" value="#{ materialInformacionalMBean.exemplar.codigoBarras }" size="30" maxlength="20" onkeyup="CAPS(this)" />
						</td>
					</c:if>
					
				</tr>
				
				<tr>
					<th class="required">N�mero de Chamada (localiza��o):</th>
					<td colspan="3">
						<h:inputText id="intputTextNumeroChamada" value="#{ materialInformacionalMBean.exemplar.numeroChamada }" size="50" maxlength="200" />
					</td>
				</tr>
		
				<tr>
					<th>Segunda Localiza��o:</th>
					<td colspan="3">
						<h:inputText id="intputTextSegundaLocalizacao" value="#{ materialInformacionalMBean.exemplar.segundaLocalizacao }" size="50" maxlength="200" />
						<ufrn:help> <p>Alguns exemplares possuem uma segunda localiza��o, por exemplo MAPAS, para indicar a gaveta onde est�o localizados. Essa informa��o � atribu�da nesse campo.</ufrn:help>
					</td>
				</tr>
		
				<tr>
					<th class="required">Biblioteca:</th>
					<td colspan="3">
						<h:selectOneMenu id="comboBoxBibliotecaMaterial" value="#{materialInformacionalMBean.exemplar.biblioteca.id}" >
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{materialInformacionalMBean.bibliotecasInternasComPermissaoUsuario}"/>
						</h:selectOneMenu>
					</td>
				</tr>
		
				<tr>
					<th  class="required">Cole��o:</th>
					<td colspan="3">
						<h:selectOneMenu id="comboBoxColecaoMaterial" value="#{materialInformacionalMBean.exemplar.colecao.id}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{materialInformacionalMBean.colecoes}"/>
						</h:selectOneMenu>
					</td>
				</tr>
		
				<tr>
					<%-- Se o item esta emprestado a situacao eh mostrada em vermelho --%>
					
					<th class="required">Situa��o:</th>
					<td>
						<h:selectOneMenu id="comboBoxSituacaoMaterial" value="#{materialInformacionalMBean.exemplar.situacao.id}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{materialInformacionalMBean.situacoes}"/>
						</h:selectOneMenu>
						<%-- 
						<c:if test="${materialInformacionalMBean.itemEstaNaSituacaoNaoPermiteAlteracao == true}">
							<h:graphicImage url="/img/required_red.gif" style="border:none" />
						</c:if> --%>
					</td>
				</tr>
				
				<tr>	
					<th class="required">Status:</th>
					<td>
						<h:selectOneMenu id="comboBoxStatusMaterial" value="#{materialInformacionalMBean.exemplar.status.id}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{materialInformacionalMBean.statusAtivos}"/>
						</h:selectOneMenu>  
						
						<ufrn:help><p>ESPECIAL -> S� pode ser emprestado por 1 dia �til </p>  <br/><br/>  <p> N�O CIRCULA -> N�o pode ser emprestado </p> </ufrn:help>
		
					</td>
				</tr>
		
				
				
				
				
				<tr>
					<th class="required">Tipo Material:</th>
					<td colspan="3">
						<h:selectOneMenu id="comboBoxTipoMaterial" value="#{materialInformacionalMBean.exemplar.tipoMaterial.id}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems  value="#{materialInformacionalMBean.tiposMaterial}" />
						</h:selectOneMenu>
		
					</td>
		
				</tr>
				
				<tr>
					<th valign="top">Formas do Documento:</th>
					<td colspan="3">
						<h:selectManyListbox  id="comBoxFormaDocumento" value="#{materialInformacionalMBean.idsFormasDocumentoEscolhidos}" size="10">
			           		<f:selectItems value="#{formaDocumentoMBean.allCombo}"/>
			       		</h:selectManyListbox>
			       		<ufrn:help>Para selecionar mais de uma <strong>forma de documento</strong> mantenha pressionada a tecla "Ctrl", em seguida selecione os itens desejados. 
						Para retirar a sele��o, tamb�m � preciso pressionar a tecla "Ctrl". </ufrn:help>
					</td>
				</tr>
		
				<tr>
					<th>N�mero do Volume:</th>
					<td colspan="3">
						<h:inputText id="inputTextVolumeExemplar" value="#{ materialInformacionalMBean.exemplar.numeroVolume }" 
							size="7" maxlength="6" onkeyup="return formatarInteiro(this);" />
							
						<ufrn:help>Informe o n�mero do volume do exemplar. Se for volume �nico deixe esse campo em branco. 
						Esse n�mero � usado para controlar os empr�stimos, pois mais de um exemplar de um mesmo t�tulo s� podem ser emprestados
						a um mesmo usu�rio se tiverem volumes diferentes.</ufrn:help>
						
					</td>
				</tr>
		
				<tr>
					<th>Tomo:</th>
					<td colspan="3">
						<h:inputText id="inputTextTomo" value="#{ materialInformacionalMBean.exemplar.tomo }" 
							size="12" maxlength="10" />
							
						<ufrn:help>Informe o tomo do exemplar. Se for tomo �nico deixe esse campo em branco. </ufrn:help>
					</td>
				</tr>
		
				<tr>
					<th>Nota de Tese e Disserta��o:</th>
					<td colspan="3">
						<h:inputTextarea id="txtAreaNotaTeseDissertacao" value="#{ materialInformacionalMBean.exemplar.notaTeseDissertacao }" cols="57" rows="2" />
					</td>
		
				</tr>
		
				<tr>
					<th>Nota de Conte�do:</th>
					<td colspan="3">
						<h:inputTextarea id="txtAreaNotaConteudo" value="#{ materialInformacionalMBean.exemplar.notaConteudo }" cols="57" rows="2" />
					</td>
		
				</tr>
				
				<tr>
					<th>Nota Geral:</th>
					<td colspan="3">
						<h:inputTextarea id="txtAreaNotaGeral" value="#{ materialInformacionalMBean.exemplar.notaGeral }" cols="57" rows="2" />
					</td>
		
				</tr>
				
				<tr>
					<th>Nota ao Usu�rio:</th>
					<td colspan="3">
						<h:inputTextarea id="txtAreaNotaUsuario" value="#{ materialInformacionalMBean.exemplar.notaUsuario }" cols="57" rows="2"  />
						<ufrn:help> Informa��es que v�o aparecer para o usu�rio nas consultas p�blicas do sistema</ufrn:help>
					</td>
		
				</tr>
				
		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="4" align="center">
				
					<h:commandButton id="cmdBottonIncluirExemplar"  value="Incluir Exemplar" action="#{materialInformacionalMBean.cadastrarExemplar}" 
									rendered="#{materialInformacionalMBean.quanidadeMateriaisNaoUsados > 0 || materialInformacionalMBean.incluindoMateriaisSemTombamento }" />
					
					<h:commandButton id="cmdBottonCatalogarProximoTituloNaoFinalizado" value="Catalogar Pr�ximo T�tulo n�o Finalizado" action="#{materialInformacionalMBean.iniciaCatalogacaoProximoTituloIncompleto}" rendered="#{materialInformacionalMBean.abilitaBotaoVoltarTelaTitulosNaoFinalizados}" />
					
					<c:if test="${materialInformacionalMBean.incluindoMateriaisApartirTelaBusca}">
						
						<c:if test="${materialInformacionalMBean.incluindoMateriaisSemTombamento}"> 
								<h:commandButton id="cmdBottonVoltarTelaPesquisaSemTombamento" value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisSemTombamento}" />
						</c:if>
					
						<c:if test="${! materialInformacionalMBean.incluindoMateriaisSemTombamento}"> 
								<h:commandButton id="cmdBottonVoltarTelaPesquisaComTombamento" value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisComTombamento}" />
						</c:if>
					
					</c:if>
					
					<c:if test="${materialInformacionalMBean.incluindoMateriaisApartirTelaCatalogacao}">
						<h:commandButton id="cmdBottonVoltarTelaEditarCatalogacao" value="<< Voltar" action="#{materialInformacionalMBean.voltarTelaAtualizarTitulo}" />
					</c:if>
					
					<h:commandButton id="cmdBottonCancelar" value="Cancelar" action="#{materialInformacionalMBean.cancelar}" immediate="true"  onclick="#{confirm}"/>
			
											
				</td>
			</tr>
			
			<tr>
				<td colspan="4" align="center">
					
					<h:commandButton id="botaoIncluirNotaCirculacao" value="Incluir Exemplar e Adicionar Nota de Circula��o" action="#{materialInformacionalMBean.incluirNotaCirculacaoExemplar}"
						rendered="#{materialInformacionalMBean.quanidadeMateriaisNaoUsados > 0 || materialInformacionalMBean.incluindoMateriaisSemTombamento }" 
						onclick="return confirm('Confirma a inclus�o do exemplar no acervo ? ') " />
					
					<h:commandButton id="cmdBottonEditarCatalogacaoExemplares" value="Editar a Cataloga��o dos Exemplares" action="#{materialInformacionalMBean.voltarTelaAtualizarTitulo}" />
				</td>
			</tr>
		</tfoot>	
		
		
	</table>

</c:if>

		
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<link rel="stylesheet" media="all" href="/sigaa/css/biblioteca/estilo_botoes_pequenos.css" type="text/css"/>


<h2>  <ufrn:subSistema /> &gt; Inclus�o de Anexos</h2>

<div class="descricaoOperacao"> 
    <p>Os anexos v�o possuir o mesmo n�mero de patrim�nio dos exemplares principais e o c�digo de barras ser� o 
    c�digo de barras do exemplar principal seguido por uma letra.</p>
    <p>Por padr�o, o anexo vai pertencer ao mesmo t�tulo do exemplar principal, mas isso pode ser alterado clicando-se no bot�o <strong><i>Substituir T�tulo do Anexo</i></strong>.</p>
</div>


<f:view>

	<h:form>
	
	<%-- Usado quando se inclui um anexo a partir da tela de inclus�o de um exemplar --%>
	<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	
	<%-- A partir da pesquisa de exemplares pode-se se chegar tamb�m nessa p�gina, e voltar --%>
	<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>
	
	<%-- Quando o usu�rio come�a a cataloga��o e decide alterar as informa��es dos materiais do t�tulo, se ele voltar as informa��es da cataloga��o devem permanecer --%>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	
	<%-- Usado quando se inclui um anexo a partir da tela de destalhes dos materias de um t�tulo  --%> 
	<t:saveState value="#{detalhesMateriaisDeUmTituloMBean.obj}" />
	
	<div style="margin-bottom: 20px; text-align: center; margin-left: auto; margin-right: auto; width: 100%">
		
		<table class="visualizacao">
				<tr>
					<th style="text-align: center">T�tulo: </th>
				</tr>
				<tr>
					<td> ${materialInformacionalMBean.tituloDoAnexoEmFormatoReferencia} </td>
				</tr>
		</table>
	</div>
	
	<table  width="100%" >
		<tr>
			<td>					
				<ul class="listaOpcoes">
					<li id="btnSubstituirTitulo">
						<h:commandLink styleClass="noborder" title="Substituir t�tulo do anexo" 
								action="#{materialInformacionalMBean.abrirTelaSubstituicaoTitulo}">	
							Substituir T�tulo do Anexo
						</h:commandLink>
					</li>
				</ul>
			</td>
		</tr>
	</table>
	
	
	<table class="visualizacao" width="100%" style="margin-bottom: 20px;">
				
		<tbody>
			
			<caption>Informa��es do Exemplar</caption>
			
			<%-- informacoes do patrimonio numca podem ser mudadas, se criou errado tem que apagar o item --%>
			
			<tr>
				<th width="40%">C�digo de Barras:</th>
				<td colspan="5">
					<h:outputText value="#{ materialInformacionalMBean.exemplarPrincipal.codigoBarras }" />
				</td>
			</tr>
			
			<tr>
				<th>N�mero de Chamada (localiza��o):</th>
				<td colspan="5">
					<h:outputText value="#{ materialInformacionalMBean.exemplarPrincipal.numeroChamada }" />
				</td>
			</tr>
			
			<tr>
				<th>Segunda Localiza��o:</th>
				<td colspan="5">
					<h:outputText value="#{ materialInformacionalMBean.exemplarPrincipal.segundaLocalizacao }" />
				</td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.biblioteca.descricao}" /> </td>
			</tr>
			
			<tr>
				<th>Cole��o:</th>
				<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.colecao.descricao}" /> </td>
			</tr>
			
			<tr>
				<th>Tipo Material:</th>
				<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.tipoMaterial.descricao}" /> </td>
			</tr>
			
			<tr>
				<th valign="top">Formas do Documento:</th>
				<td colspan="5">
					<h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.descricaoFormasDocumento}" />
				</td>
			</tr>
			
			<tr>
				<th>Volume:</th>
				
				<c:if test="${empty materialInformacionalMBean.exemplarPrincipal.numeroVolume }">
					<td colspan="5"><h:outputText value="�nico" /> </td>
				</c:if>

				<c:if test="${not empty materialInformacionalMBean.exemplarPrincipal.numeroVolume }">
					<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.numeroVolume}" /> </td>
				</c:if>
				
			</tr>
			
			<tr>
				<th>Tomo:</th>
				
				<c:if test="${empty materialInformacionalMBean.exemplarPrincipal.tomo }">
					<td colspan="5"><h:outputText value="�nico" /> </td>
				</c:if>

				<c:if test="${not empty materialInformacionalMBean.exemplarPrincipal.tomo }">
					<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.tomo}" /> </td>
				</c:if>
				
			</tr>
			
			<tr>
				<th>Nota de Tese e Diserta��o:</th>
				<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.notaTeseDissertacao}" /> </td>
			</tr>
			
			<tr>
				<th>Nota de Conte�do:</th>
				<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.notaConteudo}" /> </td>
			</tr>
			
			<tr>
				<th>Nota Geral:</th>
				<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.notaGeral}" /> </td>
			</tr>
			
			<tr>
				<th>Nota ao Usu�rio:</th>
				<td colspan="5"><h:outputText value="#{materialInformacionalMBean.exemplarPrincipal.notaUsuario}" /> </td>
			</tr>
			
		</tbody>
		
	</table>
	
	
	<%--   Formulario para inclusao de um anexo  --%>
	
	<table class="formulario" width="100%">
				
		<tbody>
			
			<caption>Informa��es do Anexo</caption>
			
			<%-- informacoes do patrimonio numca podem ser mudadas, se criou errado tem que apagar o item --%>
			
			<tr>
				<th>N�mero do Patrim�nio:</th>
				<td>
					<h:outputText value="#{ materialInformacionalMBean.exemplarAnexo.numeroPatrimonio }" style="font-weight:bold"/>
				</td>
			</tr>
			
			<tr>
				<th class="required">C�digo de Barras:</th>
				<td>
					<h:outputText  value="#{ materialInformacionalMBean.exemplarPrincipal.codigoBarras }" style="font-weight:bold" />
					<h:outputText  value="+ (uma letra gerada ap�s inclus�o)" />
				</td>
			</tr>
			
			<tr>
				<th class="required">N�mero de Chamada (localiza��o):</th>
				<td colspan="3">
					<h:inputText value="#{ materialInformacionalMBean.exemplarAnexo.numeroChamada }" size="50" maxlength="200" />
				</td>
			</tr>
	
			<tr>
				<th>Segunda Localiza��o:</th>
				<td colspan="3">
					<h:inputText value="#{ materialInformacionalMBean.exemplarAnexo.segundaLocalizacao }" size="50" maxlength="200" />
					<ufrn:help> Alguns exemplares possuem uma segunda localiza��o, por exemplo MAPAS, para indicar a gaveta onde est�o localizados. Essa informa��o � atribu�da nesse campo.</ufrn:help>
				</td>
			</tr>
	
			<tr>
				<th class="required">Biblioteca:</th>
				<td colspan="3">
					<h:selectOneMenu value="#{materialInformacionalMBean.exemplarAnexo.biblioteca.id}" >
						<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
						<f:selectItems value="#{materialInformacionalMBean.bibliotecasInternasComPermissaoUsuario}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th  class="required">Cole��o:</th>
				<td colspan="3">
					<h:selectOneMenu value="#{materialInformacionalMBean.exemplarAnexo.colecao.id}">
						<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
						<f:selectItems value="#{materialInformacionalMBean.colecoes}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>	
				<th class="required">Situa��o:</th>
				<td>
					<h:selectOneMenu value="#{materialInformacionalMBean.exemplarAnexo.situacao.id}">
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
					<h:selectOneMenu value="#{materialInformacionalMBean.exemplarAnexo.status.id}">
						<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
						<f:selectItems value="#{materialInformacionalMBean.statusAtivos}"/>
					</h:selectOneMenu>  
					
					<ufrn:help><p>ESPECIAL -> S� pode ser emprestado por 1 dia �til </p>  <br/><br/>  <p> N�O CIRCULA -> N�o pode ser emprestado </p> </ufrn:help>
	
				</td>
			</tr>
			
			<tr>
				<th class="required">Tipo Material:</th>
				<td colspan="3">
					<h:selectOneMenu value="#{materialInformacionalMBean.exemplarAnexo.tipoMaterial.id}">
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
				<th>Volume do Exemplar:</th>
				<td colspan="3">
					<h:inputText value="#{ materialInformacionalMBean.exemplarAnexo.numeroVolume }" 
						size="7" maxlength="6" onkeyup="return formatarInteiro(this);" />
						
					<ufrn:help>Informe o n�mero do volume do exemplar. Se for volume �nico deixe esse campo em branco. 
					Esse n�mero � usado para controlar os empr�stimos, pois mais um exemplar de um mesmo t�tulo s� podem ser emprestados
					a um mesmo usu�rio se tiverem volumes diferentes.</ufrn:help>
					
				</td>
	
			</tr>
			
			
			<tr>
				<th>Tomo:</th>
				<td colspan="3">
					<h:inputText value="#{ materialInformacionalMBean.exemplarAnexo.tomo }" 
						size="12" maxlength="10" />
						
					<ufrn:help>Informe o tomo do anexo. Se for tomo �nico deixe esse campo em branco.</ufrn:help>
					
				</td>
	
			</tr>
	
			<tr>
				<th>Nota de Tese e Disserta��o:</th>
				<td colspan="3">
					<h:inputTextarea value="#{ materialInformacionalMBean.exemplarAnexo.notaTeseDissertacao }" cols="57" rows="2" />
				</td>
	
			</tr>
	
			<tr>
				<th>Nota de Conte�do:</th>
				<td colspan="3">
					<h:inputTextarea value="#{ materialInformacionalMBean.exemplarAnexo.notaConteudo }" cols="57" rows="2" />
				</td>
	
			</tr>
			
			<tr>
				<th>Nota Geral:</th>
				<td colspan="3">
					<h:inputTextarea value="#{ materialInformacionalMBean.exemplarAnexo.notaGeral }" cols="57" rows="2" />
				</td>
	
			</tr>
			
			<tr>
				<th>Nota ao Usu�rio:</th>
				<td colspan="3">
					<h:inputTextarea value="#{ materialInformacionalMBean.exemplarAnexo.notaUsuario }" cols="57" rows="2"  />
					<ufrn:help> Informa��es que v�o aparecer para o usu�rio nas consultas p�blicas do sistema</ufrn:help>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="4" align="center">
						
						<h:commandButton value="Incluir Anexo" action="#{materialInformacionalMBean.incluirAnexo}" />
						
						<c:if test="${ materialInformacionalMBean.incluirAnexoApartirBusca}">
							<%-- funciona se guardar o objeto "obj" do Mbean com t:saveStatus --%>
							<h:commandButton value="<< Voltar" action="#{detalhesMateriaisDeUmTituloMBean.telaInformacoesMateriais}" immediate="true" id="voltar"  />
						</c:if>
						
						<c:if test="${! materialInformacionalMBean.incluirAnexoApartirBusca}">
							<h:commandButton value="<< Voltar" action="#{materialInformacionalMBean.voltarTelaExemplar}" immediate="true" id="voltar"  />
						</c:if>
						
					</td>
				</tr>
			</tfoot>
			
		</tbody>
		
	</table>
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
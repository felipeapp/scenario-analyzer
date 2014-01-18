
<%--  Inclu�da na pagina de gerenciamento de materiais com o formul�rio de informa��es da Assinatura --%>

<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="ufrn" uri="/tags/ufrn" %>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<style type="text/css">
	.textoCentralizado{
		text-align:center;
	}
	
</style>


<c:if test="${ fn:length(materialInformacionalMBean.assinaturasPossiveisInclusaoFasciculo) > 0 }">

	<div class="infoAltRem" style="margin-top: 10px;">
					
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" />: 
		Visualizar fasc�culos n�o inclu�dos da assinatura 
						
	</div>


	<table class="listagem" width="100%">
		<caption> Assinaturas com poss�veis fasc�culos para a Cataloga��o selecionada ( ${fn:length(materialInformacionalMBean.assinaturasPossiveisInclusaoFasciculo)} ) </caption>	
		
		<c:if test="${fn:length(materialInformacionalMBean.assinaturasDoTituloSelecionado) > 0}">
		<tr>
			<td colspan="7">
				<table class="subformulario" style="width: 100%;">
					<caption>Assinaturas associadas � cataloga��o escolhida ( ${fn:length(materialInformacionalMBean.assinaturasDoTituloSelecionado)} ) </caption>
					
					<thead>
						<tr>
							<th style="text-align: left;">C�digo</th>
							<th style="text-align: left;">T�tulo</th>
							<th style="text-align: left;">Unidade Destino</th>
							<th style="text-align: center;">Internacional?</th>
							<th style="text-align: center;">Modalidade de Aquisi��o</th>
							<th style="text-align: left;">Usu�rio criou Assinatura</th>
							<th style="width: 1%;">Qtd</th>
							<th style="width: 1%;"> </th>
						</tr>
					</thead>
					
					<c:forEach items="#{materialInformacionalMBean.assinaturasDoTituloSelecionado}" var="assinatura" varStatus="loop">
						<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td> ${assinatura.codigo} </td>
							<td> ${assinatura.titulo}</td>
							<td> ${assinatura.unidadeDestino.descricao}</td>
							<td style="text-align: center">
								<c:if test="${assinatura.internacional}">
									SIM
								</c:if>
								<c:if test="${! assinatura.internacional}">
									N�O
								</c:if>
							</td>
							<td style="text-align: center">
								<c:if test="${assinatura.assinaturaDeCompra}">
									COMPRA
								</c:if>
								<c:if test="${assinatura.assinaturaDeDoacao}">
									DOA��O
								</c:if>
								<c:if test="${! assinatura.assinaturaDeCompra &&  ! assinatura.assinaturaDeDoacao  }">
									INDEFINIDO
								</c:if>
							</td>
							<td> ${assinatura.registroCriacao.usuario.nome}</td>
							<td style="text-align: right; ${assinatura.quantidadeFasciculos > 0 ? 'color:green;': 'color:red;'}"> ${assinatura.quantidadeFasciculos} </td> <%--  Aqui indica a quantidade registrada n�o inclu�da no acervo --%>
							<td>
								<a4j:commandLink actionListener="#{materialInformacionalMBean.visualizarFasciculosNaoIncluidosDaAssinatura}" reRender="fromIncluirItem" id="cmdLinkEscolheAssinaturaComTitulo">
									<h:graphicImage url="/img/view.gif" style="border:none" title="Clique aqui visualizar os fasc�culos da assinatura" />
									<f:param name="idAssinaturaSelecionada" value="#{assinatura.id}"/>	
								</a4j:commandLink>
							</td>
						</tr>
					</c:forEach>
				
				</table>
			</td>
		</tr>
		</c:if> 
		
		<tr>
			<td style="height: 20px;">
			</td>
		</tr>
		
		
		<c:if test="${fn:length(materialInformacionalMBean.assinaturasSemTitulo) > 20}">
			<tr>
				<td style="height: 20px; color: red; font-weight: bold; padding: 10px; text-align: justify;">
				&nbsp&nbsp&nbsp&nbsp&nbsp ATEN��O: A quantidade de assinaturas n�o associadas a T�tulos no acervo est� muito grande, 
				est�o sendo criadas muitas assinaturas que n�o est�o sendo usadas no sistema. 
				Por favor, remova essas assinaturas se elas realmente n�o forem ser usadas.
				</td>
			</tr>
			
		</c:if>
		
		<c:if test="${fn:length(materialInformacionalMBean.assinaturasSemTitulo) > 20}">
			<tr>
				<td style="height: 20px; font-weight: bold; padding: 10px; text-align: center;">
				A listagem de assinaturas n�o associadas a cataloga��es por padr�o est� oculta, clique a link abaixo para mostr�-la.
				</td>
			</tr>
			
		</c:if>
		
		
		<c:if test="${fn:length(materialInformacionalMBean.assinaturasSemTitulo) > 0}">
			<tr>
				<td colspan="7">
					<table class="subformulario" style="width: 100%;">
					 <caption>
					 	<%-- 
					 	<h:commandLink value="#{materialInformacionalMBean.exibeListagemAssinaturasSemAssociadas ? "Ocultar" : "Visualizar" } Assinaturas criadas que ainda n�o est�o associadas a nenhuma cataloga��o  ( #{materialInformacionalMBean.qtdAssinaturasSemTitulo} ) " actionListener="#{materialInformacionalMBean.atualizaExibicaoAssinaturasNaoAssociadas}" />
					 	--%>
					 	<h:commandLink value="#{materialInformacionalMBean.exibeListagemAssinaturasSemAssociadas ? 'Ocultar' : 'Visualizar' }  Assinaturas criadas que ainda n�o est�o associadas a nenhuma cataloga��o  ( #{materialInformacionalMBean.qtdAssinaturasSemTitulo} ) " actionListener="#{materialInformacionalMBean.atualizaExibicaoAssinaturasNaoAssociadas}" />
					 </caption>
					</table>
				</td>
			</tr>
			
			<c:if test="${materialInformacionalMBean.exibeListagemAssinaturasSemAssociadas}">
				<tr id="assinaturasSemTitulo">
					<td colspan="7">
						<table class="subformulario" style="width: 100%;">
						
						
							<thead>
								<tr>
									<th style="text-align: left;">C�digo</th>
									<th style="text-align: left;">T�tulo</th>
									<th style="text-align: left;">Unidade Destino</th>
									<th style="text-align: center;">Internacional?</th>
									<th style="text-align: center;">Modalidade de Aquisi��o</th>
									<th style="text-align: left;">Usu�rio criou Assinatura</th>
									<th style="width: 1%;">Qtd</th>
									<th style="width: 1%;"> </th>
								</tr>
							</thead>
						
							<c:forEach items="#{materialInformacionalMBean.assinaturasSemTitulo}" var="assinatura" varStatus="loop">
								<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									<td> ${assinatura.codigo} </td>
									<td> ${assinatura.titulo}</td>
									<td> ${assinatura.unidadeDestino.descricao}</td>
									<td style="text-align: center">
										<c:if test="${assinatura.internacional}">
											SIM
										</c:if>
										<c:if test="${! assinatura.internacional}">
											N�O
										</c:if>
									</td>
									<td style="text-align: center">
										<c:if test="${assinatura.assinaturaDeCompra}">
											COMPRA
										</c:if>
										<c:if test="${assinatura.assinaturaDeDoacao}">
											DOA��O
										</c:if>
										<c:if test="${! assinatura.assinaturaDeCompra &&  ! assinatura.assinaturaDeDoacao  }">
											INDEFINIDO
										</c:if>
									</td>
									<td> ${assinatura.registroCriacao.usuario.nome}</td>
									<td style="text-align: right; ${assinatura.quantidadeFasciculos > 0 ? 'color:green;' : 'color:red;'} "> ${assinatura.quantidadeFasciculos} </td> <%--  Aqui indica a quantidade registrada n�o inclu�da no acervo --%>
									<td>
										<a4j:commandLink actionListener="#{materialInformacionalMBean.visualizarFasciculosNaoIncluidosDaAssinatura}" reRender="fromIncluirItem" id="cmdLinkEscolheAssinaturaSemTitulo">
											<h:graphicImage url="/img/view.gif" style="border:none" title="Clique aqui visualizar os fasc�culos da assinatura" />
											<f:param name="idAssinaturaSelecionada" value="#{assinatura.id}"/>	
										</a4j:commandLink>
									</td>
								</tr>
							</c:forEach>
						
						</table>
					</td>
				</tr>
			</c:if>
			
		</c:if> 
		
		
	</table>

</c:if>




<c:if test="${materialInformacionalMBean.assinaturaSelecionada != null }">

	<c:if test="${materialInformacionalMBean.quantidadeFasciculosNaoInclusosNoAcervo == 0 }">
		
		<div style="margin-top: 10px"> </div>
		
		<table class="listagem" width="100%">
			<caption> Fasc�culos Registrados que ainda n�o est�o no Acervo. </caption>
			
			<tr>
				<td style="color: green; text-align: center; "> Todos os fasc�culos registrados para esta assinatura ent�o inclu�dos no acervo</td>
			</tr>
			
		</table>
	
	</c:if>		
			
	<c:if test="${materialInformacionalMBean.quantidadeFasciculosNaoInclusosNoAcervo > 0 }">
			
		<div class="infoAltRem" style="margin-top: 10px">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar fasc�culo		
		</div>
		
		<table class="listagem" width="100%">
				<caption> Fasc�culos Registrados que ainda n�o est�o no Acervo. </caption>	
				
				<thead>
					<tr>
						<th style="text-align: left">C�digo de Barras</th>
						<th style="text-align: center">Ano Cronol�gico</th>
						<th style="text-align: left">Dia/M�s</th>
						<th style="text-align: left">Ano</th>
						<th style="text-align: left">Volume</th>
						<th style="text-align: left">N�mero</th>
						<th style="text-align: left">Edi��o</th>
						<th style="text-align: left">Registrado por</th>
						<th style="text-align: center">Data do Registro</th>
						<th style="width: 1%"></th>
					</tr>
				</thead>
				
				<c:forEach items="#{materialInformacionalMBean.fasciculosDaAssinaturaNaoInclusos}" var="fasciculo" varStatus="loop">
					<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td>  ${fasciculo.codigoBarras}    </td>
						<td style="text-align: center"> ${fasciculo.anoCronologico}  </td>
						<td>  ${fasciculo.diaMes}          </td>
						<td>  ${fasciculo.ano}             </td>
						<td>  ${fasciculo.volume}          </td>
						<td>  ${fasciculo.numero}          </td>
						<td>  ${fasciculo.edicao}          </td>
						<td>  ${fasciculo.registroCriacao.usuario.nome}          </td>
						<td style="text-align: center;">  <ufrn:format type="dataHora" valor="${fasciculo.dataCriacao}"> </ufrn:format> </td>
						<td>
							<a4j:commandLink actionListener="#{materialInformacionalMBean.selecionarFasciculoParaInclusao}" reRender="fromIncluirItem">
								<h:graphicImage url="/img/seta.gif" style="border:none" title="Clique aqui para selecionar o fasc�culo" />
								<f:param name="idFasciculoSelecionadoInclusao" value="#{fasciculo.id}"/>	
							</a4j:commandLink>
						</td>
					
					</tr>
				</c:forEach>
				
				<%-- Formul�rio para inclus�o de um novo fasc�culo   --%>
						
				<c:if test="${materialInformacionalMBean.fasciculoSelecionado != null }">
			
					<tr>
						<td colspan="9">
			
							<table class="subFormulario" width="100%" style="maring-top:20px; border-left: 1px solid #DEDFE3; border-right: 1px solid #DEDFE3;">
										
								<tbody>
									
									<caption> Dados do Novo Fasc�culo </caption>
									
									<tr>
										<th>C�digo de Barras: </th> 
										<td>
											<h:inputText id="inputTextCodigoBarrasFasciculo"  value="#{materialInformacionalMBean.fasciculoSelecionado.codigoBarras}" disabled="true"/>
										</td>
									</tr>
									
									<tr>
										<th>Ano Cronol�gico: </th> 
										<td>
											<h:inputText id="inputTextAnoCronologicoFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.anoCronologico}" maxlength="20"  size="10"/>
											<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de um ano cronol�gico. Exemplo: 2009-2010</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Dia/M�s:</th>
										<td>
											<h:inputText id="inputTextDiaMesFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.diaMes}" maxlength="20" size="10"/>
											<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de um dia ou m�s. Exemplos: 10-20, jan-fev</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Ano:</th>
										<td>
											<h:inputText id="inputTextAnoFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.ano}" maxlength="20" size="10"/>
											<ufrn:help>N�mero que faz refer�ncia ao ano de cria��o do fasc�culo. Esse campo pode conter letras para fasc�culos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Volume:</th>
										<td>
											<h:inputText id="inputTextVolumeFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.volume}" maxlength="20"  size="10"/>
											<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>N�mero:</th>
										<td>
											<h:inputText id="inputTextNumeroFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.numero}" maxlength="50" size="12"/>
											<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de um n�mero. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Edi��o:</th>
										<td>
											<h:inputText id="inputTextEdicaoFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.edicao}" maxlength="20" size="10"/>
											<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de uma edi��o. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
									
									<tr>
									
									<th class="required">N�mero de Chamada (localiza��o):</th>
									<td colspan="3">
										<h:inputText id="inputTextNumeroChamadaFasciculo" value="#{ materialInformacionalMBean.fasciculoSelecionado.numeroChamada }" size="50" maxlength="200" />
									</td>
									
									</tr>
									
									<tr>
									
									<th>Segunda Localiza��o:</th>
									<td colspan="3">
										<h:inputText id="inputTextSegundaLocalizacaoFasciculo" value="#{ materialInformacionalMBean.fasciculoSelecionado.segundaLocalizacao }" size="50" maxlength="200" />
									</td>
									
									</tr>
									
									<tr>
										<th class="required">Biblioteca:</th>
										<td colspan="3">
											<h:selectOneMenu id="comboBoxBibliotecasFasciculos" value="#{materialInformacionalMBean.fasciculoSelecionado.biblioteca.id}" disabled="true"> <%-- Sempre tem que ser igual � unidade da assinatura, o usu�rio n�o pode alterar --%>
												<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
												<f:selectItems value="#{materialInformacionalMBean.bibliotecasInternasComPermissaoUsuario}"/>
											</h:selectOneMenu>
											<ufrn:help>A biblioteca que o fasc�culo vai ficar � determinada pela unidade da assinatura.</ufrn:help>
										</td>
									</tr>
									
									
									<tr>
										<th  class="required">Cole��o:</th>
										<td colspan="3">
											<h:selectOneMenu id="comboBoxColecaoFasciculos" value="#{materialInformacionalMBean.fasciculoSelecionado.colecao.id}">
												<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
												<f:selectItems value="#{materialInformacionalMBean.colecoes}"/>
											</h:selectOneMenu>
										</td>
									</tr>
									
									
									<tr>
									
										<th class="required">Situa��o:</th>
										<td>
											<h:selectOneMenu id="comboBoxSituacaoFasciculos" value="#{materialInformacionalMBean.fasciculoSelecionado.situacao.id}">
												<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
												<f:selectItems value="#{materialInformacionalMBean.situacoes}"/>
											</h:selectOneMenu>
										</td>
									</tr>
									<tr>	
										<th class="required">Status:</th>
										<td>
											<h:selectOneMenu id="comboBoxStatusFasciculos" value="#{materialInformacionalMBean.fasciculoSelecionado.status.id}">
												<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
												<f:selectItems value="#{materialInformacionalMBean.statusAtivos}"/>
											</h:selectOneMenu>  
										</td>
									</tr>
									
									<tr>
										<th class="required">Tipo Material:</th>
										<td colspan="3">
											<h:selectOneMenu id="comboBoxTipoMaterailFasciculos" value="#{materialInformacionalMBean.fasciculoSelecionado.tipoMaterial.id}">
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
										<th>Nota Geral:</th>
										<td colspan="3">
											<h:inputTextarea id="inputAreaNotaGeral" value="#{ materialInformacionalMBean.fasciculoSelecionado.notaGeral }" cols="57" rows="2"  />
										</td>
							
									</tr>
									
									<tr>
										<th>Nota ao Usu�rio:</th>
										<td colspan="3">
											<h:inputTextarea id="inputAreaNotaUsuario" value="#{ materialInformacionalMBean.fasciculoSelecionado.notaUsuario }" cols="57" rows="2"  />
											<ufrn:help> Informa��es que v�o aparecer para o usu�rio nas consultas p�blicas do sistema</ufrn:help>
										</td>
							
									</tr>
									
									<tr>
										<th>Suplemento que Acompanha o Fasc�culo:</th>
										<td colspan="3">
											<h:inputTextarea id="inputAreaSuplementoFasciculo" value="#{ materialInformacionalMBean.fasciculoSelecionado.descricaoSuplemento }" cols="57" rows="3" />
										</td>
									</tr>
									
							</table>
					
						</td>
					</tr>
					
					<tfoot>
						<tr>
							<td colspan="9" align="center">
								
								<h:commandButton id="cmdButaoIncluirFasciculo" value="Incluir Fasc�culo" action="#{materialInformacionalMBean.atualizarFasciculo}" 
										rendered="#{materialInformacionalMBean.fasciculoSelecionado != null }"/>
								
								<c:if test="${materialInformacionalMBean.incluindoMateriaisApartirTelaBusca}">
								
									<c:if test="${materialInformacionalMBean.incluindoMateriaisSemTombamento}"> 
											<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisSemTombamento}" />
									</c:if>
								
									<c:if test="${! materialInformacionalMBean.incluindoMateriaisSemTombamento}"> 
											<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisComTombamento}" />
									</c:if>
								
								</c:if>
								
								<c:if test="${materialInformacionalMBean.incluindoMateriaisApartirTelaCatalogacao}">
									<h:commandButton value="<< Voltar" action="#{materialInformacionalMBean.voltarTelaAtualizarTitulo}" />
								</c:if>
								
								
								<h:commandButton value="Cancelar" action="#{materialInformacionalMBean.cancelar}" immediate="true" id="cancelar"  onclick="#{confirm}"/>			
							</td>
						</tr>
						
						<tr>
							<td colspan="9" align="center">
								<h:commandButton id="botaoIncluirNotaCirculacao" value="Incluir Fasc�culo e Adicionar Nota de Circula��o" 
										action="#{materialInformacionalMBean.incluirNotaCirculacaoFasciculo}"
										rendered="#{materialInformacionalMBean.fasciculoSelecionado != null }"
										onclick="return confirm('Confirma a inclus�o do fasc�culo no acervo ? ') " />
							</td>
						</tr>
						
					</tfoot>
					
				</c:if>
				
		</table>
			
	</c:if>
	

</c:if>




<%-- Antes do usu�rio selecionar os fasc�culo desejado, tem que mostrar pelo menos o bot�o de voltar e cancelar --%>
<c:if test="${materialInformacionalMBean.fasciculoSelecionado == null }">
	
	<table class="listagem" width="100%">
		<tfoot>
			<tr>
				<td colspan="9" align="center">
					
					<c:if test="${materialInformacionalMBean.incluindoMateriaisApartirTelaBusca}">
					
						<c:if test="${materialInformacionalMBean.incluindoMateriaisSemTombamento}"> 
								<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisSemTombamento}" />
						</c:if>
					
						<c:if test="${! materialInformacionalMBean.incluindoMateriaisSemTombamento}"> 
								<h:commandButton value="<< Voltar" action="#{pesquisaTituloCatalograficoMBean.voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisComTombamento}" />
						</c:if>
					
					</c:if>
					
					<c:if test="${materialInformacionalMBean.incluindoMateriaisApartirTelaCatalogacao}">
						<h:commandButton value="<< Voltar" action="#{materialInformacionalMBean.voltarTelaAtualizarTitulo}" />
					</c:if>
					
					
					<h:commandButton value="Cancelar" action="#{materialInformacionalMBean.cancelar}" immediate="true" id="cancelar"  onclick="#{confirm}"/>			
				</td>
			</tr>
		</tfoot>
	</table>

</c:if>

			


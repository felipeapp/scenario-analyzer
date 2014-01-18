
<%--  Incluída na pagina de gerenciamento de materiais com o formulário de informações da Assinatura --%>

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
		Visualizar fascículos não incluídos da assinatura 
						
	</div>


	<table class="listagem" width="100%">
		<caption> Assinaturas com possíveis fascículos para a Catalogação selecionada ( ${fn:length(materialInformacionalMBean.assinaturasPossiveisInclusaoFasciculo)} ) </caption>	
		
		<c:if test="${fn:length(materialInformacionalMBean.assinaturasDoTituloSelecionado) > 0}">
		<tr>
			<td colspan="7">
				<table class="subformulario" style="width: 100%;">
					<caption>Assinaturas associadas à catalogação escolhida ( ${fn:length(materialInformacionalMBean.assinaturasDoTituloSelecionado)} ) </caption>
					
					<thead>
						<tr>
							<th style="text-align: left;">Código</th>
							<th style="text-align: left;">Título</th>
							<th style="text-align: left;">Unidade Destino</th>
							<th style="text-align: center;">Internacional?</th>
							<th style="text-align: center;">Modalidade de Aquisição</th>
							<th style="text-align: left;">Usuário criou Assinatura</th>
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
									NÃO
								</c:if>
							</td>
							<td style="text-align: center">
								<c:if test="${assinatura.assinaturaDeCompra}">
									COMPRA
								</c:if>
								<c:if test="${assinatura.assinaturaDeDoacao}">
									DOAÇÃO
								</c:if>
								<c:if test="${! assinatura.assinaturaDeCompra &&  ! assinatura.assinaturaDeDoacao  }">
									INDEFINIDO
								</c:if>
							</td>
							<td> ${assinatura.registroCriacao.usuario.nome}</td>
							<td style="text-align: right; ${assinatura.quantidadeFasciculos > 0 ? 'color:green;': 'color:red;'}"> ${assinatura.quantidadeFasciculos} </td> <%--  Aqui indica a quantidade registrada não incluída no acervo --%>
							<td>
								<a4j:commandLink actionListener="#{materialInformacionalMBean.visualizarFasciculosNaoIncluidosDaAssinatura}" reRender="fromIncluirItem" id="cmdLinkEscolheAssinaturaComTitulo">
									<h:graphicImage url="/img/view.gif" style="border:none" title="Clique aqui visualizar os fascículos da assinatura" />
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
				&nbsp&nbsp&nbsp&nbsp&nbsp ATENÇÃO: A quantidade de assinaturas não associadas a Títulos no acervo está muito grande, 
				estão sendo criadas muitas assinaturas que não estão sendo usadas no sistema. 
				Por favor, remova essas assinaturas se elas realmente não forem ser usadas.
				</td>
			</tr>
			
		</c:if>
		
		<c:if test="${fn:length(materialInformacionalMBean.assinaturasSemTitulo) > 20}">
			<tr>
				<td style="height: 20px; font-weight: bold; padding: 10px; text-align: center;">
				A listagem de assinaturas não associadas a catalogações por padrão está oculta, clique a link abaixo para mostrá-la.
				</td>
			</tr>
			
		</c:if>
		
		
		<c:if test="${fn:length(materialInformacionalMBean.assinaturasSemTitulo) > 0}">
			<tr>
				<td colspan="7">
					<table class="subformulario" style="width: 100%;">
					 <caption>
					 	<%-- 
					 	<h:commandLink value="#{materialInformacionalMBean.exibeListagemAssinaturasSemAssociadas ? "Ocultar" : "Visualizar" } Assinaturas criadas que ainda não estão associadas a nenhuma catalogação  ( #{materialInformacionalMBean.qtdAssinaturasSemTitulo} ) " actionListener="#{materialInformacionalMBean.atualizaExibicaoAssinaturasNaoAssociadas}" />
					 	--%>
					 	<h:commandLink value="#{materialInformacionalMBean.exibeListagemAssinaturasSemAssociadas ? 'Ocultar' : 'Visualizar' }  Assinaturas criadas que ainda não estão associadas a nenhuma catalogação  ( #{materialInformacionalMBean.qtdAssinaturasSemTitulo} ) " actionListener="#{materialInformacionalMBean.atualizaExibicaoAssinaturasNaoAssociadas}" />
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
									<th style="text-align: left;">Código</th>
									<th style="text-align: left;">Título</th>
									<th style="text-align: left;">Unidade Destino</th>
									<th style="text-align: center;">Internacional?</th>
									<th style="text-align: center;">Modalidade de Aquisição</th>
									<th style="text-align: left;">Usuário criou Assinatura</th>
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
											NÃO
										</c:if>
									</td>
									<td style="text-align: center">
										<c:if test="${assinatura.assinaturaDeCompra}">
											COMPRA
										</c:if>
										<c:if test="${assinatura.assinaturaDeDoacao}">
											DOAÇÃO
										</c:if>
										<c:if test="${! assinatura.assinaturaDeCompra &&  ! assinatura.assinaturaDeDoacao  }">
											INDEFINIDO
										</c:if>
									</td>
									<td> ${assinatura.registroCriacao.usuario.nome}</td>
									<td style="text-align: right; ${assinatura.quantidadeFasciculos > 0 ? 'color:green;' : 'color:red;'} "> ${assinatura.quantidadeFasciculos} </td> <%--  Aqui indica a quantidade registrada não incluída no acervo --%>
									<td>
										<a4j:commandLink actionListener="#{materialInformacionalMBean.visualizarFasciculosNaoIncluidosDaAssinatura}" reRender="fromIncluirItem" id="cmdLinkEscolheAssinaturaSemTitulo">
											<h:graphicImage url="/img/view.gif" style="border:none" title="Clique aqui visualizar os fascículos da assinatura" />
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
			<caption> Fascículos Registrados que ainda não estão no Acervo. </caption>
			
			<tr>
				<td style="color: green; text-align: center; "> Todos os fascículos registrados para esta assinatura então incluídos no acervo</td>
			</tr>
			
		</table>
	
	</c:if>		
			
	<c:if test="${materialInformacionalMBean.quantidadeFasciculosNaoInclusosNoAcervo > 0 }">
			
		<div class="infoAltRem" style="margin-top: 10px">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar fascículo		
		</div>
		
		<table class="listagem" width="100%">
				<caption> Fascículos Registrados que ainda não estão no Acervo. </caption>	
				
				<thead>
					<tr>
						<th style="text-align: left">Código de Barras</th>
						<th style="text-align: center">Ano Cronológico</th>
						<th style="text-align: left">Dia/Mês</th>
						<th style="text-align: left">Ano</th>
						<th style="text-align: left">Volume</th>
						<th style="text-align: left">Número</th>
						<th style="text-align: left">Edição</th>
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
								<h:graphicImage url="/img/seta.gif" style="border:none" title="Clique aqui para selecionar o fascículo" />
								<f:param name="idFasciculoSelecionadoInclusao" value="#{fasciculo.id}"/>	
							</a4j:commandLink>
						</td>
					
					</tr>
				</c:forEach>
				
				<%-- Formulário para inclusão de um novo fascículo   --%>
						
				<c:if test="${materialInformacionalMBean.fasciculoSelecionado != null }">
			
					<tr>
						<td colspan="9">
			
							<table class="subFormulario" width="100%" style="maring-top:20px; border-left: 1px solid #DEDFE3; border-right: 1px solid #DEDFE3;">
										
								<tbody>
									
									<caption> Dados do Novo Fascículo </caption>
									
									<tr>
										<th>Código de Barras: </th> 
										<td>
											<h:inputText id="inputTextCodigoBarrasFasciculo"  value="#{materialInformacionalMBean.fasciculoSelecionado.codigoBarras}" disabled="true"/>
										</td>
									</tr>
									
									<tr>
										<th>Ano Cronológico: </th> 
										<td>
											<h:inputText id="inputTextAnoCronologicoFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.anoCronologico}" maxlength="20"  size="10"/>
											<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de um ano cronológico. Exemplo: 2009-2010</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Dia/Mês:</th>
										<td>
											<h:inputText id="inputTextDiaMesFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.diaMes}" maxlength="20" size="10"/>
											<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de um dia ou mês. Exemplos: 10-20, jan-fev</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Ano:</th>
										<td>
											<h:inputText id="inputTextAnoFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.ano}" maxlength="20" size="10"/>
											<ufrn:help>Número que faz referência ao ano de criação do fascículo. Esse campo pode conter letras para fascículos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Volume:</th>
										<td>
											<h:inputText id="inputTextVolumeFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.volume}" maxlength="20"  size="10"/>
											<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Número:</th>
										<td>
											<h:inputText id="inputTextNumeroFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.numero}" maxlength="50" size="12"/>
											<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de um número. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Edição:</th>
										<td>
											<h:inputText id="inputTextEdicaoFasciculo" value="#{materialInformacionalMBean.fasciculoSelecionado.edicao}" maxlength="20" size="10"/>
											<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de uma edição. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
									
									<tr>
									
									<th class="required">Número de Chamada (localização):</th>
									<td colspan="3">
										<h:inputText id="inputTextNumeroChamadaFasciculo" value="#{ materialInformacionalMBean.fasciculoSelecionado.numeroChamada }" size="50" maxlength="200" />
									</td>
									
									</tr>
									
									<tr>
									
									<th>Segunda Localização:</th>
									<td colspan="3">
										<h:inputText id="inputTextSegundaLocalizacaoFasciculo" value="#{ materialInformacionalMBean.fasciculoSelecionado.segundaLocalizacao }" size="50" maxlength="200" />
									</td>
									
									</tr>
									
									<tr>
										<th class="required">Biblioteca:</th>
										<td colspan="3">
											<h:selectOneMenu id="comboBoxBibliotecasFasciculos" value="#{materialInformacionalMBean.fasciculoSelecionado.biblioteca.id}" disabled="true"> <%-- Sempre tem que ser igual à unidade da assinatura, o usuário não pode alterar --%>
												<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
												<f:selectItems value="#{materialInformacionalMBean.bibliotecasInternasComPermissaoUsuario}"/>
											</h:selectOneMenu>
											<ufrn:help>A biblioteca que o fascículo vai ficar é determinada pela unidade da assinatura.</ufrn:help>
										</td>
									</tr>
									
									
									<tr>
										<th  class="required">Coleção:</th>
										<td colspan="3">
											<h:selectOneMenu id="comboBoxColecaoFasciculos" value="#{materialInformacionalMBean.fasciculoSelecionado.colecao.id}">
												<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
												<f:selectItems value="#{materialInformacionalMBean.colecoes}"/>
											</h:selectOneMenu>
										</td>
									</tr>
									
									
									<tr>
									
										<th class="required">Situação:</th>
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
											Para retirar a seleção, também é preciso pressionar a tecla "Ctrl". </ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th>Nota Geral:</th>
										<td colspan="3">
											<h:inputTextarea id="inputAreaNotaGeral" value="#{ materialInformacionalMBean.fasciculoSelecionado.notaGeral }" cols="57" rows="2"  />
										</td>
							
									</tr>
									
									<tr>
										<th>Nota ao Usuário:</th>
										<td colspan="3">
											<h:inputTextarea id="inputAreaNotaUsuario" value="#{ materialInformacionalMBean.fasciculoSelecionado.notaUsuario }" cols="57" rows="2"  />
											<ufrn:help> Informações que vão aparecer para o usuário nas consultas públicas do sistema</ufrn:help>
										</td>
							
									</tr>
									
									<tr>
										<th>Suplemento que Acompanha o Fascículo:</th>
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
								
								<h:commandButton id="cmdButaoIncluirFasciculo" value="Incluir Fascículo" action="#{materialInformacionalMBean.atualizarFasciculo}" 
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
								<h:commandButton id="botaoIncluirNotaCirculacao" value="Incluir Fascículo e Adicionar Nota de Circulação" 
										action="#{materialInformacionalMBean.incluirNotaCirculacaoFasciculo}"
										rendered="#{materialInformacionalMBean.fasciculoSelecionado != null }"
										onclick="return confirm('Confirma a inclusão do fascículo no acervo ? ') " />
							</td>
						</tr>
						
					</tfoot>
					
				</c:if>
				
		</table>
			
	</c:if>
	

</c:if>




<%-- Antes do usuário selecionar os fascículo desejado, tem que mostrar pelo menos o botão de voltar e cancelar --%>
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

			


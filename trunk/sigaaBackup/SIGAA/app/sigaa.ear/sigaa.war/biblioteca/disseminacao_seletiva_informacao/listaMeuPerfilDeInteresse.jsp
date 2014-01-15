<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>
<%@page import="br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade"%>

<%-- 
<style>	
	
	/** classe para o botão de paginação quando não está selecionado  **/
	.button_pagination {
	    background: -moz-linear-gradient(center top , #FFFFFF, #EFEFEF) repeat scroll 0 0 #F6F6F6;
	    border: 1px solid #CCCCCC;
	    border-radius: 3px 3px 3px 3px;
	    height: 2.0833em;
	    overflow: visible;
	    padding: 0 0.5em;
	    vertical-align: middle;
	    white-space: nowrap;
	    font-weight:  bolder;
	    font-size: 12px; 
	}
	
	.assunto{
		width: 80%;
	}
	
	.marcar{
		width: 20%;
		text-align: center;
	}
	
	table.formulario thead tr th.marcar{
		width: 20%;
		text-align: center;
	}
	
	table.formulario thead tr th.assunto{
		width: 80%;
		text-align: left;
	}
	
</style> 

function marcaCheckBoxSelecionados() {

    J("table.listaInformacoesInteresse tr").each(  // para cada linha da tabela
        function(i) {
        	
    	    var  linha = J(this);
            var  coluna = J(this).find("td");   
            
            coluna.each( // para cada coluna da tabela
                    function(i) {
                        // se o texto html dentro da coluna contém a palavra checked é porque é um checkbox selecionada
                        if(J(this).html().indexOf("checked") > 0 ){
                        	linha.css("background-color","#B5EEB5"); 
                        	linha.css("font-weight","bold");
                            return false; // para a interacao
                        }
                    }
            );
         
        }
    );
                 
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
// funcao chamada quando o usuário clica no checkbox que possui a classe checkSelecionar
//////////////////////////////////////////////////////////////////////////////////////////////////////////
function prepararCheckBox(){
	
    J(".checkSelecionar").click(function() {
    	
    	var linha  = J(this).parent().parent(); // o pai do pai é a ***linha*** da tabela onde está o checkbox selecionado.
    	
    	var nomeClasse = linha.attr("className");
    	
    	
	    if( J(this).attr('checked') == true){ 
	    	////////////////////////////////////////////////////
	    	// selecionou o check box, pinta a linha de funto
	    	/////////////////////////////////////////////////////
	    	linha.css("background-color","#B5EEB5"); 
	    	linha.css("font-weight","bold");
	    }else{
			/////////////////////////////////////////////////////
	    	// se deselecionou o check box voltar o cor original da linha
	    	/////////////////////////////////////////////////////
	    	if(nomeClasse == 'linhaPar')
	    		linha.css("background-color","#F9FBFD");
	    	if(nomeClasse == 'linhaImpar')
	    		linha.css("background-color","#EDF1F8");
	    	
	    	linha.css("font-weight","normal");
	    	
	    }
       
    });
}

--%>


<script type="text/javascript">

// Executa a função do commadlink adicionar assunto selecioda automaticamente para o usuário.
function clickLinkAdicionarAutoridadeAssunto(){ 
	J(".botaoAdicionarAutoridadeAssunto").trigger('click');
	return false;
}

//Executa a função do commadlink adicionar assunto selecioda automaticamente para o usuário.
function clickLinkAdicionarAutoridadeAutor(){ 
	J(".botaoAdicionarAutoridadeAutor").trigger('click');
	return false;
}

function marcarLinhaTabela(){
	
	J(".pintarLinha tbody tr").mouseover(
			function(){
				J(this).css("backgroundColor", "#C4D2EB");
				J(this).css("font-weight", "bold");
    		}
	);
	
	J(".pintarLinha tbody tr").mouseout(
			function(){
				J(this).css("backgroundColor", "");
				J(this).css("font-weight", "");
    		}
	);
	
}

var J = jQuery.noConflict(); 

J(document).ready(function(){
	 marcarLinhaTabela();
});


// apaga os dados digitados pelo usuário depois que adicionou a listagem.
function limparDescricaoAutoridadeAssuntoBuscada() {
	$('formMeuPerfilDeInteresse:descricaoAutoridadeAssuntoBuscada').value = '';
	$('formMeuPerfilDeInteresse:descricaoAutoridadeAssuntoBuscada').focus();
}

//apaga os dados digitados pelo usuário depois que adicionou a listagem.
function limparDescricaoAutoridadeAutorBuscada() {
	$('formMeuPerfilDeInteresse:descricaoAutoridadeAutorBuscada').value = '';
	$('formMeuPerfilDeInteresse:descricaoAutoridadeAutorBuscada').focus();
}


</script>


<style type="text/css">

.numeracao{
	text-align: left;
	width: 5%;
}

.texto{
	text-align: left;
	width: 90%;
}

.icone{
	text-align: right;
	width: 5%;
}

</style>


<c:set var="confirmAtualizacao" value="if (!confirm('Confirma a atualização do seu Perfil ?')) return false" scope="request" />


<f:view>

	<h2><ufrn:subSistema/> &gt; Visualizar Meu Perfil de Interesse </h2>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<div class="descricaoOperacao"> 
	    
	    <p> Caro usuário, o ${configSistema['siglaSigaa']} oferece o serviço de disseminação seletiva da informação (DSI), um
			serviço personalizado que tem como intuito tornar os usuários informados e atualizados com informações compatíveis e personalizadas de acordo
			com seu perfil e linha de pesquisa, aperfeiçoando assim os serviços de alerta oferecidos pelas bibliotecas.
	    </p>
	    <br/>
	    
	    <table style="border: 1px solid;">
	    	<caption style="font-weight: bold; font-variant: small-caps;">Informações Importantes</caption>
	    	<tr>
	    	<td>
			    <p> O <strong>Informativo de Meu Interesse</strong> são avisos personalizados de acordo com o seu interesse enviados semanalmente pelo sistema, contendo os Títulos dos últimos materiais que foram incluídos no acervo. Para isso, escolha abaixo <strong>Assuntos</strong> e/ou <strong>Autores</strong> cadastrados na nossa base de dados. Bem como as <strong>Bibliotecas</strong> que você costuma freqüentar.  </p>
			    <br/>
			    <p> O <strong>Informativo de Novas Aquisições</strong> é um informativo mensal enviado aos usuários, ele contém os Títulos dos últimos materiais que foram incluídos no acervo das bibliotecas dentro da área de conhecimento escolhida.</p>
	   		</td>
	   		</tr>
	   	</table>
	    
	</div>
	


	<a4j:keepAlive beanName="configuraPerfilInteresseUsuarioBibliotecaMBean"/>
	
	<p:resources />

	<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css" />
	
	<%-- 
		* Carrega o perfil de interesse do usuário  
		* Utilizado porque o usuário também acessa a página diretamente quando vem redirecionado da parte pública do sistema
		* sem passar por nenhum método do MBean que carrege essa página. 
		--%>
	${configuraPerfilInteresseUsuarioBibliotecaMBean.carregarMeusPersilInteresse}
	
	
	<h:form id="formMeuPerfilDeInteresse">


			
		<div id="indicador" style="text-align: center; width: 50%; margin-left: auto; margin-right: auto; height: 15px; ">
	
			 <a4j:status id="teste">
			         <f:facet name="start">
			                <h:graphicImage  value="/img/indicator.gif"/>
			          </f:facet>
			 </a4j:status>
	
		</div>





		<%-- O formulário com os dados do perfil do usuário    --%>
		
		<div class="infoAltRem" style="margin-top: 10px; width: 100%;">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Informação do seu Perfil
		</div> 
		
		<table id="tablePerfilInteresseUsuario" class="formulario" style="width: 100%;">
			<caption> Perfil de Interesse do Usuário ${usuario.nome} </caption>
			
			<thead>
				<tr>
					<th></th>
					<th></th>
				</tr>
			</thead>
			
			<tbody>
			
				
				
				<%-- Os Campos de autocomplete   para a busca de assuntos ou autoridades --%>
				<tr>
					<td colspan="2">	
						<p:panel header="1 - Informativo de Meu Interesse">
						  	
						  	<table class="subFormulario" style="width: 100%; text-align: center;">
								<caption> Assuntos de Interesse </caption>
							
								<tr>
									<td>
											<div>
												<h:inputHidden id="idAutoridadeAssuntoSelecionada"   value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.idAutoridadeAssuntoBuscada}"/>
												
												Digite os Assuntos de seu Interesse:
												<h:inputText   id="descricaoAutoridadeAssuntoBuscada" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.descricaoAutoridadeAssuntoBuscada}" size="60" onkeyup="CAPS(this);" />
												
													
												<rich:suggestionbox id="suggestionAssunto"
													for="descricaoAutoridadeAssuntoBuscada"
													var="_autoridade" fetchValue="#{_autoridade.entradaAutorizadaAssunto }"
													suggestionAction="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaAutoridadeAssuntoAutocomplete}" 
													width="500" height="300" minChars="4">																																							
													<h:column>
														<h:outputText value="#{ _autoridade.entradaAutorizadaAssunto }" />
													</h:column>
													
													<a4j:support event="onselect" reRender="dtTblAutoridadesAssuntoDoUsuario, idAutoridadeAssuntoSelecionada"
															oncomplete="clickLinkAdicionarAutoridadeAssunto();">
														<f:setPropertyActionListener value="#{ _autoridade.id }" target="#{configuraPerfilInteresseUsuarioBibliotecaMBean.idAutoridadeAssuntoBuscada}"/>
													</a4j:support>
												</rich:suggestionbox>
												
												
												<span style="top: 20px; vertical-align: bottom">
													<a4j:commandLink  id="cmdLinkAdicionaAutoridadeAssuntoSelecionada" styleClass="botaoAdicionarAutoridadeAssunto"
															actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.adicionarAutoridadeAssuntoSelecionada}"
															reRender="dtTblAutoridadesAssuntoDoUsuario, idAutoridadeAssuntoSelecionada" oncomplete="limparDescricaoAutoridadeAssuntoBuscada(); marcarLinhaTabela();">
													</a4j:commandLink>
												</span>
												
											</div>
									
											
																													
											<a4j:outputPanel ajaxRendered="true" style="float: left; width: 100%; text-align: center; margin-top: 20px; margin-bottom: 20px;" >
												
												<t:div style="color:red;" rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.autoridadesAssuntoDataModel.rowCount == 0}">
													Sem Assuntos de seu Interesse.
												</t:div>
												
												<t:dataTable id="dtTblAutoridadesAssuntoDoUsuario" var="autoridade" rowIndexVar="i"  
														value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.autoridadesAssuntoDataModel}" 
														style="width: 100%; text-align: left;"
														styleClass="pintarLinha"
														rowClasses="linhaImpar, linhaPar" columnClasses="numeracao, texto, icone">
													<h:column>
														<h:outputText value="#{i + 1}."/>
													</h:column>
													<h:column>
														<h:outputText value="#{autoridade.entradaAutorizadaAssunto}"  />
													</h:column>
													<h:column rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.autoridadesAssuntoDataModel.rowCount > 0}" >
														<a4j:commandLink actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.removerAutoridadeAssuntoSelecionada}" 
																reRender="dtTblAutoridadesAssuntoDoUsuario" oncomplete="marcarLinhaTabela();">
															<h:graphicImage url="/img/delete.gif" style="border: none;" title="Remover Assunto do seu Perfil" />
														</a4j:commandLink>
													</h:column>
												</t:dataTable>
											</a4j:outputPanel>
									
									</td>
								</tr>
							
							</table>
						  	
						  	<table class="subFormulario" style="width: 100%; text-align: center;">
								<caption> Autores de Interesse </caption>
							
								<tr>
									<td>
											<div>
												<h:inputHidden id="idAutoridadeAutorSelecionado"   value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.idAutoridadeAutorBuscada}"/>
												
												Digite os Autores de Interesse:
												<h:inputText   id="descricaoAutoridadeAutorBuscada" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.descricaoAutoridadeAutorBuscada}" size="60" onkeyup="CAPS(this);" />
												
													
												<rich:suggestionbox id="suggestionAutor"
													for="descricaoAutoridadeAutorBuscada"
													var="_autoridade" fetchValue="#{_autoridade.entradaAutorizadaAutor }"
													suggestionAction="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaAutoridadeAutorAutocomplete}" 
													width="500" height="300" minChars="4">																																							
													<h:column>
														<h:outputText value="#{ _autoridade.entradaAutorizadaAutor }"/>
													</h:column>
													
													<a4j:support event="onselect" reRender="idAutoridadeAutorSelecionado" oncomplete="clickLinkAdicionarAutoridadeAutor();">
														<f:setPropertyActionListener value="#{ _autoridade.id }" target="#{configuraPerfilInteresseUsuarioBibliotecaMBean.idAutoridadeAutorBuscada}"/>
													</a4j:support>
												</rich:suggestionbox>
												
										
												<span style="top: 20px; vertical-align: bottom">
													<a4j:commandLink id="cmdAdicionaAssuntoAutorSelecionado" styleClass="botaoAdicionarAutoridadeAutor"
 															actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.adicionarAutoridadeAutorSelecionada}"
															reRender="dtTblAutoresAutorDoUsuario, idAutoridadeAutorSelecionado" oncomplete="limparDescricaoAutoridadeAutorBuscada(); marcarLinhaTabela();">
													</a4j:commandLink>
												</span>
												
												
											</div>
									
											
																													
											<a4j:outputPanel ajaxRendered="true" style="float: left; width: 100%; text-align: center; margin-top: 20px; margin-bottom: 20px;" >
												
												<t:div style="color:red;" rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.autoridadesAutorDataModel.rowCount == 0}">
													Sem Autores de seu Interesse.
												</t:div>
												
												<t:dataTable id="dtTblAutoresAutorDoUsuario" var="autoridade" rowIndexVar="i"  
														value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.autoridadesAutorDataModel}" 
														style="width: 100%; text-align: left;"
														styleClass="pintarLinha"
														rowClasses="linhaImpar, linhaPar" columnClasses="numeracao, texto, icone">
													<h:column>
														<h:outputText value="#{i + 1}."/>
													</h:column>
													<h:column>
														<h:outputText value="#{autoridade.entradaAutorizadaAutor }"  />
													</h:column>
													<h:column rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.autoridadesAutorDataModel.rowCount > 0}" >
														<a4j:commandLink actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.removerAutoridadeAutorSelecionada}" 
																reRender="dtTblAutoresDoUsuario" oncomplete="marcarLinhaTabela();">
															<h:graphicImage url="/img/delete.gif" style="border: none;" title="Remover Autor do seu Perfil" />
														</a4j:commandLink>
													</h:column>
												</t:dataTable>
											</a4j:outputPanel>
									
									</td>
								</tr>
							
							</table>
						  	
						  	<%-- Parte onde o usuário escolhe de qual biblioteca ele deseja receber informação  --%>  
						  	<table class="subFormulario" style="width: 100%; text-align: center;">
								
								<caption> Receber Informações sobre Materiais das Bibliotecas  
									<span style="padding-left: 40%;"> 
										TODAS <h:selectBooleanCheckbox id="checkBoxTodasBibliotecas" 
													value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.escolheuTodasAsBibliotecas}" onclick="deselecionarBibliotecas(this);" /> 
									</span> 
								</caption>
								
								<tr>
									<td colspan="2">
										<t:selectManyCheckbox id="checkBibliotecaEspecifica" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.bibliotecasSelecionadas}"
												onclick="deselecionarCheckTodasBibliotecas();"
												layoutWidth="2" layout="pageDirection" style="text-align: left;">
											<f:selectItems value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.allBibliotecasInternasAtivasSelectItem}" />  
										</t:selectManyCheckbox>
									</td>
								</tr>
								
								<%-- 	@deprecated Mudado a forma de visualizar as bibliotecas, mas não apague esse código. caso queira voltar a forma antiga.
								<tr>
									<td colspan="2">
										<table style="width: 20%; margin-left: auto; margin-right: auto;">
											<tr>
												<td style="text-align: left;">    
													<h:selectOneRadio value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAbrangecia}" style="margin-left: 10%; margin-right: 10%;" >
								        				<f:selectItems value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.abrangenciasDisseminacao}" />  
								   						<a4j:support  event="onclick" oncomplete="modelPanelBibliotecasInteresse.show(); marcarLinhaTabela();"
								   								reRender="dialogEscolheBibliotecas" status="teste" />
								   					</h:selectOneRadio>
							
												</td>
											</tr>
										</table>
									</td>
								</tr> --%>
							
							
							</table>
						  	
						</p:panel>
					</td>
				</tr>
				
					
					
					
					
					
				<%-- Parte onde o usuário escolhe receber o informativo mensal de novas aquisições  --%> 
				
				<tr>
					<td colspan="2" style="height: 20px;">
					</td>
				</tr>	
					
				
				<tr>
					<td colspan="2">
					
						<p:panel header="2 - Informativo de Novas Aquisições">  
						
					
							<table class="subFormulario" style="width: 100%; text-align: center;">
								
					
								<tr>
									<td colspan="2">
										<table style="width: 80%; margin-left: auto; margin-right: auto;">
											<tr>
												<td style="text-align: right;"> 
													<h:selectBooleanCheckbox id="checkReceberInformativoMensal" 
														label="Desejo receber o Informativo de Novas Aquisições"
														value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.perfilInteresse.receberInformativoNovasAquisicoes}">
														<a4j:support event="onclick" reRender="comboAreaConhecimntoInformativo" oncomplete="marcarLinhaTabela();"/>
													</h:selectBooleanCheckbox>
												 </td>
												<td style="text-align: left; width: 60%; "> Desejo receber o Informativo de Novas Aquisições </td>
											 </tr>
										</table>
									</td>
								</tr>
								
								<tr>
									<td colspan="2">
										<table style="width: 80%; margin-left: auto; margin-right: auto;">
											<tr>
												<td style="text-align: right;  width: 60%;"> Áreas de Conhecimento do Informativo: </td>
												<td style="text-align: left;"> 
													<h:selectOneMenu id="comboAreaConhecimntoInformativo" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.perfilInteresse.areaDoInformativo.id}"
														disabled="#{! configuraPerfilInteresseUsuarioBibliotecaMBean.perfilInteresse.receberInformativoNovasAquisicoes}">
														<f:selectItem itemValue="-1" itemLabel="Todas"/>
														<f:selectItems value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.areasCNPQCombo}" />
													</h:selectOneMenu>
													<ufrn:help> A área específica dos materiais que estarão no Informativo de Novas Aquisições enviado. </ufrn:help>
												</td>
											</tr>
										</table>
									</td>
								</tr>
						
					
							</table>
						
						</p:panel>
					</td>
				</tr>
				
				
				<tr>
					<td colspan="2" style="height: 40px;">
					</td>
				</tr>	
					
					
				
				
			
			</tbody>
			
			
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center;">
						<h:commandButton id="cmdAtualizarPerfil" value="Atualizar Perfil de Interesse" action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.atualizarPerfil}" onclick="#{confirmAtualizacao}" />
						<h:commandButton id="cmdCancelarAtualizacaoPerfil" value="Cancelar" action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
			
			
		</table>


		
		
		
		<%-- @deprecated Mudado a forma de visualizar as bibliotecas, mas não apague esse código. caso queira voltar a forma antiga. 
		O model Panel para o usuário escolher de qual biblioteca deseja receber informação. 
		<a4j:outputPanel   ajaxRendered="true" id="painelEscolherBibliotecassInteresse"> 
		
			
			<t:div id="divEscolheBibliotecas">
		
				<p:dialog id="dialogEscolheBibliotecas" header="Bibliotecas de seu Interesse" 
						widgetVar="modelPanelBibliotecasInteresse" modal="true" width="600" height="550"
						rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAbragenciaBibliotecaEspecifica}">
					
					<h:selectManyCheckbox
							id="selectManyCheckBibliotecas" title="Bibliotecas"
							value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.bibliotecasSelecionadas}"
							layout="pageDirection" style="text-align: left;">
						<f:selectItems value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.allBibliotecasInternasAtivasSelectItem}" />
					</h:selectManyCheckbox>
					
				</p:dialog>
			</t:div>
			
			
		</a4j:outputPanel>  --%>	
		
		
		
		
		





		<%-- 
		
	 	PÁGINA ANTIGA NA QUAL O USUÁRIO SELECIONA O ASSUNTO OU AUTOR ENTRE UM LISTA PAGINADA COM TODOS AS AUTORIDADES CARREGADAS POR DEMANDA
		
		
		<a4j:outputPanel ajaxRendered="true">
			<table id="tablePerfilInteresseUsuario" class="formulario" style="width: 85%;">
				<caption> Perfil de Interesse do Usuário ${usuario.nome} </caption>
			
				<thead>
					<tr>
						<th></th>
						<th></th>
					</tr>
				</thead>
			
				<tbody>
			
					<%-- Parte na qual o usuário escolhe se quer cadastrar interesse em assuntos ou autores.  
					<tr>
						<td colspan="2">
							<table style="width: 80%; margin-left: auto; margin-right: auto;">
								<tr>
									<th style="text-align: right; width: 60%;"> Tipo de Informação de seu Interesse: </th>
									<td> 
										<h:selectOneMenu id="comboTipoAutoridade" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAutoridadeEscolhida}"
											valueChangeListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.alterouTipoAutoridade}"  onchange="submit();">
											<f:selectItems value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAutoridadeComboBox}" />
										</h:selectOneMenu>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					
					<tr>
						<td colspan="2">
							<hr style="width: 80%"/>
						</td>
					</tr>
			
					<%-- Parte onde o usuário escolhe receber o informativo mensagem de novas aquisições   
					<tr>
						<td colspan="2">
							<table style="width: 80%; margin-left: auto; margin-right: auto;">
								<tr>
									<td style="text-align: right; width: 60%; "> Desejo receber o <span style="font-style: italic;">Informativo Mensal de Novas Aquisições: </span> </td>
									<td style="text-align: left;"> 
										<h:selectBooleanCheckbox id="checkReceberInformativoMensal" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.perfilInteresse.receberInformativoNovasAquisicoes}">
											<a4j:support event="onclick" reRender="comboAreaConhecimntoInformativo" oncomplete="marcaCheckBoxSelecionados(); prepararCheckBox();"/>
										</h:selectBooleanCheckbox>
										<ufrn:help> Será enviado mensalmente um informativo com as aquisições do último mês que estão disponíveis para a sua consulta no catálogo. </ufrn:help>
									 </td>
								 </tr>
							</table>
						</td>
					</tr>
			
					<tr>
						<td colspan="2">
							<table style="width: 80%; margin-left: auto; margin-right: auto;">
								<tr>
									<td style="text-align: right;  width: 60%;"> Áreas de Conhecimento do Informativo: </td>
									<td style="text-align: left;"> 
										<h:selectOneMenu id="comboAreaConhecimntoInformativo" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.perfilInteresse.areaDoInformativo.id}"
											disabled="#{! configuraPerfilInteresseUsuarioBibliotecaMBean.perfilInteresse.receberInformativoNovasAquisicoes}">
											<f:selectItem itemValue="-1" itemLabel="Todas"/>
											<f:selectItems value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.areasCNPQCombo}" />
										</h:selectOneMenu>
										<ufrn:help> A área específica dos materiais que estarão no Informativo de Novas Aquisições enviado. </ufrn:help>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				
			
					<tr>
						<td colspan="2">
							<hr style="width: 80%"/>
						</td>
					</tr>
					
					
					
			
					<%-- Parte onde o usuário escolhe de qual assunto da base de autoridades deseja receber informações.   
					<tr>
						<td colspan="2">
							
							<div style="width: 90%; margin-left: auto; margin-right: auto;">
							
			                
				                 <t:div style="width: 100%; text-align: center; margin-top: 10px; margin-bottom: 10px;">
				
									<h:commandLink value="<<" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == 1}"
											rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas > 1}"
											styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="1"/>
									</h:commandLink> &nbsp;&nbsp;
										
									<h:commandLink value="<" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == 1}"
												rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas > 1}"
												styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual -1}"/>
									</h:commandLink>	&nbsp;&nbsp;
								
									<a4j:repeat var="pagina" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.listaPaginasVisiveis}" >
										<h:commandLink value="#{pagina}" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == pagina}"
												styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="#{pagina}"/>
										</h:commandLink>  &nbsp;&nbsp;
									</a4j:repeat>
									
									<h:commandLink value=">" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas}"
											rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas > 1}"
											styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual +1}"/>
									</h:commandLink> &nbsp;&nbsp;
									
									<h:commandLink value=">>" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas}"
											rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas > 1}"
											styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas}" />
									</h:commandLink>	
									
								</t:div>
			              
			               
				               <t:div style="text-align: center;">
				               		
				               		<a4j:repeat var="letra" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.listaLetrasFiltrarAssunto}" >
				               			<h:commandLink value="#{letra}" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.verificaAlteracaoFiltroLetra}">
											<f:param name="_letra_selecionada" value="#{letra}"/>
										</h:commandLink> &nbsp
				               		</a4j:repeat>
									
								</t:div>
			               
			                    
			                    <%-- 
			                     -- Interação sobre os assuntos  caso o usuário escolha o tipo assunto  
			                     
								<h:dataTable  id="dataTableAssuntos" var="assunto"  styleClass="listaInformacoesInteresse"
												rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAutoridadeEscolhida == configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAutoridadeAssunto}"
											value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.assuntosAutorizados}"
											first="#{configuraPerfilInteresseUsuarioBibliotecaMBean.retornaPosicaoAutoridadeDentroDoTotalAutoridades}" 
											rows="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadeResuldosPorPagina}"
											rowClasses="linhaPar, linhaImpar" columnClasses="assunto, marcar"  headerClass="assunto, marcar" style="width: 100%;">
									
									<f:facet name="caption">
										<h:outputText value="Assuntos de Interesse"/> 
									</f:facet>
									
									
									<h:column>
										<f:facet name="header">
											<h:outputText value="Assunto"/> 
										</f:facet>
										<h:outputText value="#{assunto.entradaAutorizadaAssunto} "/>
									</h:column>	
										
									<h:column>
										<f:facet name="header">
											<h:outputText value="Selecionar"/> 
										</f:facet>
										 <h:selectBooleanCheckbox id="checkSelecionaAssunto" value="#{assunto.selecionada}" rendered="#{assunto.id > 0 }" styleClass="checkSelecionar" />
									</h:column>		
											
								
								</h:dataTable> 
			               
			               
			               		 <%-- 
			               		  -- Interação sobre os autores caso o usuário escolha o tipo autor  
			               		  
								<h:dataTable  id="dataTableAutores" var="autor" styleClass="listaInformacoesInteresse"
										rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAutoridadeEscolhida == configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAutoridadeAutor}"
										value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.autoresAutorizados}"
										first="#{configuraPerfilInteresseUsuarioBibliotecaMBean.retornaPosicaoAutoridadeDentroDoTotalAutoridades}" 
										rows="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadeResuldosPorPagina}"
										rowClasses="linhaPar, linhaImpar" columnClasses="assunto, marcar" headerClass="assunto, marcar" style="width: 100%;">
								
									<f:facet name="caption">
										<h:outputText value="Autores de Interesse"/> 
									</f:facet>
									
									
									<h:column>
										<f:facet name="header">
											<h:outputText value="Assunto"/> 
										</f:facet>
										<h:outputText value="#{autor.entradaAutorizadaAutor} "/>
									</h:column>	
										
									<h:column>
										<f:facet name="header">
											<h:outputText value="Selecionar"/> 
										</f:facet>
										 <h:selectBooleanCheckbox id="checkSelecionaAutor" value="#{autor.selecionada}" rendered="#{autor.id > 0 }" styleClass="checkSelecionar" />
									</h:column>		
										
								
								</h:dataTable> 
			               
			               
				                <div style="width: 100%; text-align: center; margin-top: 10px; margin-bottom: 10px;">
				
									<h:commandLink value="<<" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == 1}"
											rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas > 1}"
											styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="1"/>
									</h:commandLink> &nbsp;&nbsp;
										
									<h:commandLink value="<" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == 1}"
												rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas > 1}"
												styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual -1}"/>
									</h:commandLink>	&nbsp;&nbsp;
								
									<a4j:repeat var="pagina" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.listaPaginasVisiveis}" >
										<h:commandLink value="#{pagina}" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == pagina}"
												styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="#{pagina}"/>
										</h:commandLink>  &nbsp;&nbsp;
									</a4j:repeat>
									
									<h:commandLink value=">" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas}"
											rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas > 1}"
											styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual +1}"/>
									</h:commandLink> &nbsp;&nbsp;
									
									<h:commandLink value=">>" actionListener="#{configuraPerfilInteresseUsuarioBibliotecaMBean.buscaProximosAssuntosAutorizados}" disabled="#{configuraPerfilInteresseUsuarioBibliotecaMBean.paginaAtual == configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas}"
											rendered="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas > 1}"
											styleClass="button_pagination">
											<f:param name="_numero_pagina_atual" value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.quantidadePaginas}" />
									</h:commandLink>	
									
								</div>
			                 
			                 
			                </div>
							
						</td>
					</tr>
			
			
					<%-- Parte onde o usuário escolhe de qual biblioteca ele deseja receber informação   
					<tr>
						<td colspan="2">
							<table style="width: 100%; margin-left: auto; margin-right: auto;">
								<tr>
									<th style="width: 45%;">Das Bibliotecas: </th>
									<td style="width: 55%; text-align: center;">    
										<h:selectOneRadio value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.tipoAbrangecia}" style="margin-left: 10%; margin-right: 10%;" >
					        				<f:selectItems value="#{configuraPerfilInteresseUsuarioBibliotecaMBean.abrangenciasDisseminacao}" />  
					   						<a4j:support  event="onclick" oncomplete="modelPanelBibliotecasInteresse.show(); marcaCheckBoxSelecionados(); prepararCheckBox();" 
					   										reRender="painelEscolherBibliotecassInteresse" status="teste" />
					   					</h:selectOneRadio>
				
									</td>
							</tr>
							</table>
						</td>
					</tr>
					
					
			
				</tbody>
			
				<tfoot>
					<tr>
						<td colspan="6" style="text-align: center;">
							<h:commandButton id="cmdAtualizarPerfil" value="Atualizar Perfil de Interesse" action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.atualizarPerfil}" onclick="#{confirmAtualizacao}" />
							<h:commandButton id="cmdCancelarAtualizacaoPerfil" value="Cancelar" action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.cancelar}" immediate="true" onclick="#{confirm}" />
						</td>
					</tr>
				</tfoot>
			
			</table>
		</a4j:outputPanel>
		
		 --%>

	</h:form>

</f:view>



<script type="text/javascript">

	//Chamado quando o usuário seleciona o combo box todas, descarma as outras bibliotecas
	function deselecionarBibliotecas(chk){
	   for (i=0; i<document.formMeuPerfilDeInteresse.elements.length; i++){
		  
		  elemento = document.formMeuPerfilDeInteresse.elements[i];
	      
		  if(elemento.type == "checkbox" 
	    		  && elemento.id != chk.id
	    		  && elemento.id != 'formMeuPerfilDeInteresse:checkReceberInformativoMensal'){
	          document.formMeuPerfilDeInteresse.elements[i].checked = false;
	      }
		}
	}
	
	// Chamado quando o usuário seleciona uma biblioteca especifica, desmarca o combox todas as bibliotecas
	function deselecionarCheckTodasBibliotecas(){
		document.getElementById('formMeuPerfilDeInteresse:checkBoxTodasBibliotecas').checked = false;
	}
	
	
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@page import="br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.AtividadeExtensao"%>

<c:set var="PROJETO" value="<%= String.valueOf(TipoAtividadeExtensao.PROJETO) %>" scope="session" />
<c:set var="PROGRAMA" value="<%= String.valueOf(TipoAtividadeExtensao.PROGRAMA) %>" scope="session" />
<c:set var="PRODUTO" value="<%= String.valueOf(TipoAtividadeExtensao.PRODUTO) %>" scope="session" />
<c:set var="CURSO" value="<%= String.valueOf(TipoAtividadeExtensao.CURSO) %>" scope="session" />
<c:set var="PRESTACAO_SERVICO" value="<%= String.valueOf(TipoAtividadeExtensao.PRESTACAO_SERVICO) %>" scope="session" />
<c:set var="ABRANGENCIA_INTERNACIONAL" value="<%= String.valueOf(TipoRegiao.INTERNACIONAL) %>" scope="session" />

<f:view>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2>
		<ufrn:subSistema />
		&gt; Informa��es Gerais da Atividade
	</h2>

	<div class="descricaoOperacao">
		<table width="100%">
			<tr>
				<td>Nesta tela devem ser informados os dados gerais de uma
					A��o.</td>
				<td><%@include file="passos_atividade.jsp"%>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<p><strong>OBSERVA��O:</strong> Os dados informados s� s�o cadastrados na base de dados quando clica-se em "Avan�ar >>".</p> 
				</td>
			</tr>
		</table>
	</div>

	<c:if
		test="${ atividadeExtensao.obj != null && atividadeExtensao.obj.tipoAtividadeExtensao != null }">
		<h:form id="form">
			<h:inputHidden value="#{atividadeExtensao.confirmButton}"
				id="confirmButton" />
			<h:inputHidden value="#{atividadeExtensao.obj.id}" id="id" />

			<table class="formulario" width="100%">
				<caption class="listagem">Informe os dados Gerais da A��o</caption>

				<tr>
					<th width="25%"><b>Tipo da A��o:<b />
					</th>
					<td>${atividadeExtensao.obj.tipoAtividadeExtensao.descricao} <c:if
							test="${ atividadeExtensao.obj.registro }">
							<font color="red"> (REGISTRO)</font>
							<ufrn:help img="/img/ajuda.gif">A op��o de registro � reservada para A��es que j� ocorreram, mas que ainda n�o est�o registradas no sistema.</ufrn:help>
						</c:if></td>
				</tr>

				<tr>
					<th class="obrigatorio">T�tulo:</th>
					<td><h:inputTextarea id="titulo"
							value="#{atividadeExtensao.obj.titulo}" cols="2"
							style="width: 95%"
							rendered="#{!atividadeExtensao.readOnly  && !atividadeExtensao.obj.projetoAssociado}" />
						<b><h:outputText id="_titulo"
								value="#{atividadeExtensao.obj.titulo}"
								rendered="#{atividadeExtensao.readOnly  || atividadeExtensao.obj.projetoAssociado}" />
					</b></td>
				</tr>

				<tr>
					<th class="obrigatorio">Ano:</th>
					<td>
						<h:inputText id="anoAtividade" value="#{atividadeExtensao.obj.ano}" maxlength="4"
							rendered="#{!atividadeExtensao.readOnly  && !atividadeExtensao.obj.projetoAssociado}"
							size="5" onkeyup="return formatarInteiro(this)" disabled="#{!atividadeExtensao.permiteAlterarAnoProjeto}" /> 
						<b>
							<h:outputText id="_anoAtividade" value="#{atividadeExtensao.obj.ano}" 
								rendered="#{atividadeExtensao.readOnly  || atividadeExtensao.obj.projetoAssociado}" />
						</b>
					</td>
				</tr>

				<tr>
					<th class="obrigatorio">Per�odo de Realiza��o:</th>
					<td>
						<t:inputCalendar id="dataInicio" value="#{atividadeExtensao.obj.dataInicio}" renderAsPopup="true"
							renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10" 
							onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupTodayString="Hoje �" 
							displayValueOnly="#{atividadeExtensao.readOnly || atividadeExtensao.obj.projetoAssociado}">
							<f:converter converterId="convertData" />
						</t:inputCalendar> a 
						<t:inputCalendar id="dataFim" value="#{atividadeExtensao.obj.dataFim}" renderAsPopup="true"
							renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10" popupTodayString="Hoje �"
							displayValueOnly="#{atividadeExtensao.readOnly || atividadeExtensao.obj.projetoAssociado}">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
					</td>
				</tr>

				<tr>
					<th class="obrigatorio">�rea de Conhecimento CNPQ:</th>
					<td>
						<h:selectOneMenu id="areaCNPQ" value="#{atividadeExtensao.obj.areaConhecimentoCnpq.id}" 
							readonly="#{atividadeExtensao.readOnly}" 
							rendered="#{!atividadeExtensao.readOnly && !atividadeExtensao.obj.projetoAssociado}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{area.allGrandesAreasCombo}" />
						</h:selectOneMenu> 
						<b>
							<h:outputText id="_areaCNPQ" value="#{atividadeExtensao.obj.areaConhecimentoCnpq.nome}"
								rendered="#{atividadeExtensao.readOnly  || atividadeExtensao.obj.projetoAssociado}" />
						</b>
					</td>
				</tr>

				<a4j:region rendered="#{ not atividadeExtensao.obj.tipoAtividadeExtensao.produto }">
					<tr>
						<th class="obrigatorio">Abrang�ncia:</th>
						<td>
							<h:selectOneMenu id="tipoRegiao" value="#{atividadeExtensao.obj.tipoRegiao.id}" readonly="#{atividadeExtensao.readOnly}"
								rendered="#{!atividadeExtensao.readOnly && !atividadeExtensao.obj.projetoAssociado}"
								onchange="javascript:abrangenciaInternacional(this);">
								<f:selectItem itemValue="0" itemLabel="-- SELECINE --" />
								<f:selectItems value="#{atividadeExtensao.allTipoRegiaoCombo}" />
							</h:selectOneMenu> 
							<b>
								<h:outputText id="_tipoRegiao" value="#{atividadeExtensao.obj.tipoRegiao.descricao}"
									rendered="#{atividadeExtensao.readOnly  || atividadeExtensao.obj.projetoAssociado}" />
							</b>
						</td>
					</tr>
				</a4j:region>

				<tr>
					<th  class="obrigatorio">�rea Tem�tica de Extens�o:</th>
					<td>
						<h:selectOneMenu id="areaTematicaPrincipal"	
							value="#{atividadeExtensao.obj.areaTematicaPrincipal.id}"	
							readonly="#{atividadeExtensao.readOnly}" >
							<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
							<f:selectItems value="#{areaTematica.allCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>

				<tr>
					<th width="35%" class="obrigatorio">Coordenador:</th>
					<td>
						<a4j:region>
						<h:inputText id="nomeDocente" value="#{ atividadeExtensao.obj.projeto.coordenador.servidor.pessoa.nome }" size="70" />
						<rich:suggestionbox id="suggestion_docente" width="430" height="100" minChars="3" for="nomeDocente"
							suggestionAction="#{ servidorAutoCompleteMBean.autocompleteNomeServidor }" var="_docente" fetchValue="#{_docente.pessoa.nome}"
							onsubmit="$('indicatorDocente').style.display='';" oncomplete="$('indicatorDocente').style.display='none';"
							reRender="indicatorDocente">
							
							<f:param name="apenasAtivos" value="true" />

							<h:column>
								<h:outputText value="#{_docente.siape} - #{ _docente.pessoa.nome }" />
							</h:column>

							<a4j:support event="onselect" actionListener="#{ atividadeExtensao.carregarServidor }" reRender="unidadeProponente">
								<f:param name="apenasAtivos" value="true" />
								<f:attribute name="docenteAutoComplete" value="#{ _docente }" />
							</a4j:support>

						</rich:suggestionbox>
						</a4j:region>
						<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
					</td>
				</tr>

				<tr>
					<th style="vertical-align: middle;"> A��o vinculada a Programa Estrat�gico de Extens�o:</th>
					<td>
						<table>
							<tr>
								<td>
									<h:selectOneRadio value="#{ atividadeExtensao.obj.vinculadoProgramaEstrategico }" id="projetoVinculado" layout="lineDirection">
										<f:selectItem itemLabel="SIM" itemValue="true" />
										<f:selectItem itemLabel="N�O" itemValue="false" />
										<a4j:support reRender="form" event="onchange" />
									</h:selectOneRadio>
								</td>
								<td>
									<ufrn:help img="/img/ajuda.gif">
											Programa Estrat�gico de Extens�o trata-se de um conjunto de a��es de extens�o com atua��o continuada, abrang�ncia, complexidade e 
											interesse espec�fico para a ${ configSistema['siglaInstituicao'] }. Possui car�ter org�nico-institucional e em fun��o de sua 
											interatividade com a sociedade contextualiza-se como um canal de demandas e intera��o com a sociedade.
									</ufrn:help>
								</td>
							</tr>
						</table>
					</td>
				</tr>

				<a4j:region id="tipoProjeto" rendered="#{ atividadeExtensao.obj.vinculadoProgramaEstrategico }">
					<tr>
						<th>Selecione o Programa Estrat�gico de Extens�o:</th>
						<td>
							<table>
								<tr>
									<td>
										<h:selectOneMenu  id="programa" value="#{ atividadeExtensao.obj.programaEstrategico.id }">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
											<f:selectItems value="#{ programaEstrategicoMBean.allCombo }" />
										</h:selectOneMenu>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</a4j:region>

				<a4j:region rendered="#{ atividadeExtensao.obj.tipoAtividadeExtensao.produto }">
					<tr>
						<th width="35%">Produto Gerado atrav�s de qual a��o de extens�o/Atividade?:</th>
						<td>
							<h:inputText id="produtoGerado" value="#{ atividadeExtensao.obj.produtoExtensao.produtoGerado }" size="70" maxlength="200" />
						</td>
				</a4j:region>

				<tr>
					<th style="vertical-align: middle;">Projeto Vinculado a a��o de forma��o continuada e permanente:</th>
					<td>
						<table>
							<tr>
								<td>
									<h:selectOneRadio value="#{atividadeExtensao.obj.projetoVinculadoFormacaoContinuada}" id="aa" layout="lineDirection">
										<f:selectItem itemLabel="SIM" itemValue="true" />
										<f:selectItem itemLabel="N�O" itemValue="false" />
									</h:selectOneRadio>
								</td>
								<td>
									<ufrn:help img="/img/ajuda.gif">
										Um a��o de extens�o de forma��o continuada e permanente trata-se de qualquer um 
										das modalidades de extens�o praticada na UFRN, que esteja voltada para atender demandas espec�ficas de profissionais 
										formados que atuam no mercado de trabalho.
									</ufrn:help>
								</td>
							</tr>
						</table>
					</td>
				</tr>

				<tr>
					<th>A��o vinculada a Grupo Permanente de Arte e Cultura:</th>
					<td>
						<table>
							<tr>
								<td>
									<h:selectOneRadio value="#{atividadeExtensao.obj.permanente}" id="grupoPermanente">
										<f:selectItem itemLabel="SIM" itemValue="true" />
										<f:selectItem itemLabel="N�O" itemValue="false" />
									</h:selectOneRadio>
								</td>
								<td>
									<ufrn:help img="/img/ajuda.gif">
										Grupo Permanente de Arte e Cultura se constitui num conjunto de projetos de extens�o com atua��o espec�fica 
										em arte e caracterizado por uma atua��o permanente. 
									</ufrn:help>
								</td>
							</tr>
						</table>
					</td>
				</tr>

				<tr>
					<td class="subFormulario" colspan="3">P�blico Alvo do Projeto</td>
				</tr>

				<tr>
					<th class="obrigatorio">Discriminar P�blico Alvo Interno:</th>
					<td>
						<h:inputText value="#{atividadeExtensao.obj.publicoAlvo}" style="width: 95%;" id="publicoAlvoInterno" />
						<ufrn:help img="/img/ajuda.gif">
							Qualificar o p�blico interno que ser� atendido com o projeto. Por Exemplo: discentes do curso, T�cnicos do DAS e etc.
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th class="obrigatorio">Quantificar P�blico Alvo Interno:</th>
					<td>
						<h:inputText id="totalInterno" value="#{atividadeExtensao.obj.publicoEstimado}" maxlength="9"
							readonly="#{atividadeExtensao.readOnly}" size="5" onkeyup="return(formatarInteiro(this))" onblur="submit()">
							<a4j:support reRender="totalGeral" event="onchange" onsubmit="true"/>
						</h:inputText> 
						<ufrn:help img="/img/ajuda.gif">
							Quantidade de pessoas atendidas diretamente pelo projeto.
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th>Discriminar P�blico Alvo Externo:</th>
					<td>
						<h:inputText value="#{atividadeExtensao.obj.publicoAlvoExterno}" style="width: 95%;" id="publicoAlvoExterno" />
						<ufrn:help img="/img/ajuda.gif">
							Qualificar o p�blico Externo que ser� atendido com o projeto. 
							Por Exemplo: agricultores familiares, merendeiras e etc.
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th>Quantificar P�blico Alvo Externo:</th>
					<td>
						<h:inputText id="totalExterno" value="#{atividadeExtensao.obj.publicoExterno}" maxlength="9"
							readonly="#{atividadeExtensao.readOnly}" size="5" onkeyup="return(formatarInteiro(this))" onblur="submit()">
							<a4j:support reRender="totalGeral" event="onchange" onsubmit="true" />
						</h:inputText> 
						<ufrn:help img="/img/ajuda.gif">
							Quantidade de pessoas atendidas diretamente pelo projeto.
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th>Total de participantes estimados:</th>
					<td>
						<a4j:outputPanel ajaxRendered="true" id="totalGeral">
							<h:outputText value="#{ atividadeExtensao.obj.publicoTotal }" /> 
						</a4j:outputPanel>
					</td>
				</tr>
				
				<tr>
					<td class="subFormulario" colspan="3">Local de Realiza��o</td>
				</tr>

				<tr>
					<th class="obrigatorio">Estado:</th>
					<td>
						<h:selectOneMenu id="estado" value="#{atividadeExtensao.obj.localRealizacao.municipio.unidadeFederativa.id}">
							<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
							<f:selectItems value="#{ atividadeExtensao.allEstadosCombo }" />
							<a4j:support event="onchange" reRender="municipio" />
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">
							Estado que ocorrer� a a��o de extens�o
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th class="obrigatorio">Munic�pio:</th>
					<td>
						<h:selectOneMenu id="municipio" value="#{atividadeExtensao.obj.localRealizacao.municipio.id}">
							<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
							<f:selectItems value="#{ atividadeExtensao.allMunicipio }" />
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">
							Munic�pio que ocorrer� a a��o de extens�o
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th>Bairro:</th>
					<td>
						<h:inputText id="nomeBairro" value="#{atividadeExtensao.obj.localRealizacao.bairro}" maxlength="50" size="35" />
						<ufrn:help img="/img/ajuda.gif">
							Descrever o(s) bairro(s) onde ser�(�o) realizadas as atividades do projeto.
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th> Espa�o de Realiza��o:</th>
					<td>
						<h:inputText id="espacoRealizacao" value="#{atividadeExtensao.obj.localRealizacao.descricao}"
							maxlength="255" style="width: 95%"/>
						<ufrn:help img="/img/ajuda.gif">
							Descrever o(s) local(is) espec�ficos onde ser�o realizadas as atividades de projeto. 
							Por Exemplo: Escola Municipal, Assentamento de Reforma Agr�ria, Unidade b�sica de Sa�de, etc.
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th>Latitude:</th>
					<td>
						<h:inputText id="latitude" value="#{atividadeExtensao.obj.localRealizacao.latitude}" maxlength="50" size="35" />
						<ufrn:help img="/img/ajuda.gif">
							Informe a Latitude onde ocorrer� a a��o.
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<th>Longitude:</th>
					<td>
						<h:inputText id="longitude" value="#{atividadeExtensao.obj.localRealizacao.longitude}" maxlength="50" size="35" />
						<ufrn:help img="/img/ajuda.gif">
							Informe a Longitude onde ocorrer� a a��o.
						</ufrn:help>
					</td>
				</tr>

				<tr>
					<td></td>
					<td>
						<a4j:commandButton value="Adicionar Local de Realiza��o" action="#{ atividadeExtensao.adicionarLocalRealizacao }" 
							id="btAdd" reRender="locaisRealizacao, estado, municipio, nomeBairro, espacoRealizacao, latitude, longitude"/>
					</td>
				</tr>

				<tr>
					<td></td>
					<td>
						<h:panelGroup id="locaisRealizacao" rendered="#{ not empty atividadeExtensao.locaisRealizacao }">
							<t:dataTable id="realizacao" value="#{ atividadeExtensao.locaisRealizacao }"
								var="local" align="center" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
			
								<t:column>
									<f:facet name="header">
										<f:verbatim>Estado</f:verbatim>
									</f:facet>
									<h:outputText value="#{ local.municipio.unidadeFederativa.descricao }" />
								</t:column>
			
								<t:column>
									<f:facet name="header">
										<f:verbatim>Munic�pio</f:verbatim>
									</f:facet>
									<h:outputText value="#{ local.municipio.nome }" />
								</t:column>
	
								<t:column>
									<f:facet name="header">
										<f:verbatim>Bairro</f:verbatim>
									</f:facet>
									<h:outputText value="#{ local.bairro }" />
								</t:column>

								<t:column>
									<f:facet name="header">
										<f:verbatim>Espa�o de Realiza��o</f:verbatim>
									</f:facet>
									<h:outputText value="#{ local.descricao }" />
								</t:column>

								<t:column width="5%" styleClass="centerAlign">
									<a4j:commandButton image="/img/delete.gif" title="Remover Local de Realiza��o"  
										reRender="locaisRealizacao, estado, municipio, nomeBairro" action="#{atividadeExtensao.removeLocalRealizacao}" />
								</t:column>
								
							</t:dataTable>
						</h:panelGroup>
					</td>
				</tr>
				
				<tr>
					<td class="subFormulario" colspan="3">Formas de Financiamento do Projeto</td>
				</tr>

				<tr>
					<th>Auto-Financiado:</th>
					<td><h:selectBooleanCheckbox value="#{atividadeExtensao.obj.autoFinanciado}"
							id="chk_autoFinanciado" rendered="#{ !atividadeExtensao.obj.projetoAssociado }">
						<a4j:support reRender="form" event="onclick" />
						</h:selectBooleanCheckbox>
						<ufrn:help img="/img/ajuda.gif">Marque esta op��o se sua proposta for financiada com recursos pr�prios.</ufrn:help>
					</td>
				</tr>

				<a4j:region id="autoFinanciada" rendered="#{ !atividadeExtensao.obj.autoFinanciado }">
					<tr>
						<th valign="top">Financiado pela ${ configSistema['siglaInstituicao'] }:</th>
						<td>
							<h:selectBooleanCheckbox value="#{atividadeExtensao.obj.financiamentoInterno}"
								id="chk_financiamentoInterno">
								<a4j:support reRender="form" event="onclick" />
							</h:selectBooleanCheckbox>
						</td>
					</tr>

					<a4j:region id="editalExtensao" rendered="#{ atividadeExtensao.obj.financiamentoInterno }">
						<tr id="lineEdital">
							<th></th>
							<td>
								<table id="finInt" width="100%">
	
									<tr>
										<th width="35%">Financiado pela Unidade Proponente:</th>
										<td>
											<h:selectBooleanCheckbox value="#{atividadeExtensao.obj.financUnidProponente}" id="chk_financiadoUnidProp"
												rendered="#{!atividadeExtensao.readOnly  && !atividadeExtensao.obj.projetoAssociado }" />
											<ufrn:help img="/img/ajuda.gif">Marque esta op��o se sua proposta for financiada com recursos da Unidade Proponente.</ufrn:help>
										</td>
									</tr>
	
									<tr id="lineInterno">
										<th valign="top">Financiamento FAEX/PROEX:</th>
										<td>	
											<h:selectBooleanCheckbox value="#{atividadeExtensao.obj.financProex}" id="chk_financiamentoProReitoria" >
												<a4j:support reRender="form" event="onclick" />
											</h:selectBooleanCheckbox>
										</td>
									</tr>
									<a4j:region rendered="#{ atividadeExtensao.obj.financProex }" id="internoFaex">				
										<tr>
											<th class="obrigatorio">Edital de Extens�o:</th>
											<td>
												<h:selectOneMenu id="editalFaex" value="#{atividadeExtensao.obj.editalExtensao.id}" valueChangeListener="#{atividadeExtensao.carregarEditalExtensao}">
													<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
													<f:selectItems value="#{atividadeExtensao.editaisCombo}" />
													<a4j:support reRender="form" event="onchange" />
												</h:selectOneMenu> 
												<c:if test="${ atividadeExtensao.obj.registro }">
													<ufrn:help img="/img/ajuda.gif">Nos casos de A��es para registro, somente editais j� finalizados s�o exibidos.</ufrn:help>
												</c:if></td>
										</tr>
										<a4j:region rendered="#{ not empty atividadeExtensao.linhasCombo }" id="linhasAtuacao">
											<tr>
												<th class="obrigatorio">Linha de Atua��o:</th>
												<td>
													<h:selectOneMenu id="linhaAtuacao" value="#{atividadeExtensao.obj.linhaAtuacao.id}">
														<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
														<f:selectItems value="#{atividadeExtensao.linhasCombo}" />
													</h:selectOneMenu> 
												</td>
											</tr>
										</a4j:region>
										<tr>
											<th class="obrigatorio">N� Bolsas Solicitadas:</th>
											<td>
												<h:inputText id="nBolsas" value="#{atividadeExtensao.obj.bolsasSolicitadas}"
													maxlength="4" readonly="#{atividadeExtensao.readOnly}" size="5" onkeyup="formatarInteiro(this)" />
											</td>
										</tr>
									</a4j:region>
								</table></td>
						</tr>
					</a4j:region>

					<tr id="lineExterno">
						<th>Financiamento Externo:</th>
						<td>
							<h:selectBooleanCheckbox value="#{atividadeExtensao.obj.financiamentoExterno}" id="chk_financiamentoExterno">
								<a4j:support reRender="form" event="onclick" />
							</h:selectBooleanCheckbox>
						</td>
					</tr>

					<a4j:region id="finaciador" rendered="#{ atividadeExtensao.obj.financiamentoExterno }">
						<tr id="lineFinanciador">
							<th></th>
							<td>
								<table id="finExt" width="100%">
									<tr id="lineExterno">
										<th width="35%">Oriundo de Edital:</th>
										<td >
											<h:selectBooleanCheckbox value="#{atividadeExtensao.obj.financExternoEdital}" id="chk_oriundoEdital" 
												disabled="#{ atividadeExtensao.obj.financExternoEspecial }">
												<a4j:support reRender="form" event="onclick" />
											</h:selectBooleanCheckbox>
										</td>
									</tr>

									<tr id="lineExterno">
										<th>Outros:</th>
										<td>
											<h:selectBooleanCheckbox value="#{atividadeExtensao.obj.financExternoEspecial}" id="chk_outros" 
												disabled="#{ atividadeExtensao.obj.financExternoEdital }">
												<a4j:support reRender="form" event="onclick" />
											</h:selectBooleanCheckbox>
										</td>
									</tr>
									
									<a4j:region id="finaciadorExternoEdital" rendered="#{ atividadeExtensao.obj.financExternoEdital }">
										<tr>
											<th class="obrigatorio">Edital Externo:</th>
											<td>
												<h:inputText id="editalExterno" value="#{atividadeExtensao.obj.editalExterno}" maxlength="255" readonly="#{atividadeExtensao.readOnly}"
													size="48"/>
												<ufrn:help img="/img/ajuda.gif">Detalhes da entidade externa financiadora do projeto (N� do Edital e/ou Nome da entidade).</ufrn:help>
											</td>
										</tr>
									</a4j:region>
									
									<a4j:region id="finaciadorExternoEspecialEdital" 
											rendered="#{ atividadeExtensao.obj.financExternoEdital || atividadeExtensao.obj.financExternoEspecial}">
										<tr>
											<th class="obrigatorio">Financiador:</th>
											<td>
												<h:selectOneMenu id="classeFinanciamento" value="#{atividadeExtensao.obj.classificacaoFinanciadora.id}"
													readonly="#{atividadeExtensao.readOnly}" rendered="#{!atividadeExtensao.readOnly  && !atividadeExtensao.obj.projetoAssociado}">
													<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
													<f:selectItems value="#{classificacaoFinanciadora.allCombo}" />
												</h:selectOneMenu> 
											</td>
										</tr>
										<tr>
											<th class="obrigatorio">Bolsas Concedidas:</th>
											<td>
												<h:inputText id="bolsaConcedidasEditalExterno" value="#{atividadeExtensao.obj.bolsasConcedidas}" maxlength="3" readonly="#{atividadeExtensao.readOnly}"
													size="5" rendered="#{!atividadeExtensao.readOnly  && !atividadeExtensao.obj.projetoAssociado}" />
												<ufrn:help img="/img/ajuda.gif">N�mero de bolsas concedidas.</ufrn:help>
											</td>
										</tr>
										
									</a4j:region>
									
								</table></td>
						</tr>
				
					</a4j:region>
				
				</a4j:region>

				<tr>
					<td colspan="2" class="subFormulario"> Unidades Envolvidas na Execu��o </td>
				</tr>
				
				<tr>
					<th>Unidade Proponente:</th>
					<td>
						<h:outputText value="#{ atividadeExtensao.obj.projeto.coordenador.servidor.unidade.nome }" id="unidadeProponente" />
					</td>
				</tr>

				<tr>
					<th width="35%">Executor Financeiro:</th>
					<td>
			        	<h:selectOneMenu id="executorFinanceiroProjeto" value="#{ atividadeExtensao.obj.executorFinanceiro.id }" style="width: 70%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ executorFinanceiroMBean.allCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>

				<tr>
					<th>Unidade Co-Executoras Externa:</th>
					<td>
						<h:inputText id="coExecutorExterno" value="#{atividadeExtensao.obj.unidadeExecutoraExterna}" maxlength="100" 
							readonly="#{atividadeExtensao.readOnly}" size="48"/>
					</td>
				</tr>

				<tr>
					<th style="vertical-align: top;">Unidade(s) Co-Executoras:</th>
					<td>
			        	<h:selectOneMenu id="outraUnidadeProponente" value="#{atividadeExtensao.outraUnidade.id}" readonly="#{atividadeExtensao.readOnly}" style="width: 70%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}" />
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif"> 
							Unidades acad�micas da ${ configSistema['siglaInstituicao'] } ou institui��es governamentais e n�o governamentais que participam 
							do projeto em regime de c�-gest�o. 
						</ufrn:help>	
						<a4j:commandButton reRender="out" action="#{atividadeExtensao.adicionarOutraUnidadeProponente}" image="/img/adicionar.gif" title="Adicionar Unidade Co-Executora" />
					<br />
					<br />

						<h:panelGroup id="out">
							<c:if test="${not empty atividadeExtensao.obj.unidadesProponentes}">
								<div class="infoAltRem">
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;" /> : Remover Unidade Envolvida
								</div>
							</c:if>
							<t:dataTable id="unidadesEnvolvidas" value="#{atividadeExtensao.obj.unidadesProponentes}" 
								var="atividadeUnidade" align="center" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
								rendered="#{not empty atividadeExtensao.obj.unidadesProponentes}">
		
								<t:column>
									<f:facet name="header">
										<f:verbatim>Lista de Outras Unidades Envolvidas / Parceiras</f:verbatim>
									</f:facet>
									<h:outputText value="#{atividadeUnidade.unidade.nome}" />
								</t:column>
		
								<t:column>
									<f:facet name="header">
										<f:verbatim>Unidade Gestora</f:verbatim>
									</f:facet>
									<h:outputText value="#{atividadeUnidade.unidade.gestora.sigla}" />
								</t:column>
		
								<t:column width="5%" styleClass="centerAlign">
									<a4j:commandButton image="/img/delete.gif" title="Remover Unidade Envolvida" 
										action="#{atividadeExtensao.removeUnidadeProponente}" reRender="out">
										<f:param name="idAtividadeUnidade" value="#{atividadeUnidade.id}" />
										<f:param name="idUnidade" value="#{atividadeUnidade.unidade.id}" />
									</a4j:commandButton>
								</t:column>
							</t:dataTable>
						</h:panelGroup>
				</td>
				
			</tr>

				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="<< Voltar"action="#{atividadeExtensao.voltar}" id="btVoltar" rendered="#{ atividadeExtensao.obj.id == 0 }" /> 
							<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" id="btCancelar" /> 
							<h:commandButton value="Avan�ar >>" action="#{atividadeExtensao.submeterDadosGerais}" id="btAvancar" />
						</td>
					</tr>
				</tfoot>
			</table>
		</h:form>
		<br />
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
			<span class="fontePequena"> Campos de preenchimento
				obrigat�rio. </span>
		</center>
		<br />
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
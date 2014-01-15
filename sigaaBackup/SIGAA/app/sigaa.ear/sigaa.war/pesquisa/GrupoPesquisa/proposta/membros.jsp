<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>

<style>
	.rich-tabhdr-side-cell, .rich-tabhdr-side-border { padding: 1px !important; }
	.rich-tab-bottom-line td { padding: 0 !important; }
	.fc-header-title {
		margin-top:0;
		text-align:center;
		white-space:nowrap;
		width:250px;
	}
	.ui-autocomplete-loading {
		size: 80;
	}
</style>

<f:view>

<rich:modalPanel id="painelLattes" height="100" width="500">
		<f:facet name="header">
			<h:panelGroup>
				<h:outputText value="Atualizar Endereço do CV Lattes"></h:outputText>
	        </h:panelGroup>
	     </f:facet>
	     <f:facet name="controls">
	         <h:panelGroup>
	          <h:outputLink value="#" id="btn1">  
	 		       <h:graphicImage value="/img/close.png"  style="margin-left:5px; cursor:pointer; border: none" />  
	               <rich:componentControl for="painelLattes" attachTo="btn1" operation="hide" event="onclick" />  
	          </h:outputLink>
	         </h:panelGroup>
	     </f:facet>
	
		<h:form id="formLattes">
	     	 <h:inputText id="endLattesPainel" value="#{propostaGrupoPesquisaMBean.membroPainel.enderecoLattes}" size="60"/> <br /><br />
		     <h:commandButton value="Adicionar Lattes" actionListener="#{ propostaGrupoPesquisaMBean.adicionarLattes }" rendered="#{ empty propostaGrupoPesquisaMBean.membroPainel.enderecoLattes }"/>
		     <h:commandButton value="Alterar Lattes" actionListener="#{ propostaGrupoPesquisaMBean.adicionarLattes }" rendered="#{ not empty propostaGrupoPesquisaMBean.membroPainel.enderecoLattes }"/>
	     </h:form>
	     
</rich:modalPanel>

<p:resources/>
<a4j:region rendered="#{ propostaGrupoPesquisaMBean.portalDocente }">
<%@include file="/portais/docente/menu_docente.jsp"%>
</a4j:region>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

<h2><ufrn:subSistema /> &gt; Proposta de Criação de Grupos de Pesquisa</h2>

	<div class="descricaoOperacao">
		<p>
			"Art. 3º, Parágrafo 1º: Os membros permanentes são necessariamente docentes do quadro
			permanente ativo ou do Programa de Professor Colaborador Voluntário da ${ configSistema['siglaInstituicao'] },
			sendo exigida a presença de, no mínimo, 02 (dois) membros permanentes em cada Grupo." 
		</p>
		<p>
			<strong>Atenção,</strong> caso o sistema não localize o Currículo Lattes de algum membro,
			fato este indicado pelo ícone <h:graphicImage value="/img/prodocente/question.png" style="overflow: visible;"/>,
			é possível clicar sobre esse mesmo ícone para informar diretamente o endereço do Currículo Lattes daquele membro.
		</p>
	</div>

	<h:form id="form">
		<table class="formulario">
			<caption>Membros do Grupo de Pesquisa</caption>
			<tbody>

				<tr>
					<td colspan="2" class="subFormulario" style="padding-top: 10px;">Membros Permanentes</td>
				</tr>

		        <tr>
					<th class="obrigatorio" width="17%">Docente:</th>
					<td>
						<a4j:region id="membroPermanenteReg">
							<h:inputText id="membroPermanente" value="#{propostaGrupoPesquisaMBean.docentePermanente.pessoa.nome}"
			                	size="80" maxlength="150"/>
							<rich:suggestionbox id="suggestionMembroPermanente"  width="400" height="100" minChars="3" 
							    for="membroPermanente" suggestionAction="#{propostaGrupoPesquisaMBean.autoCompleteDocentePermanente}" 
							    var="_docente" fetchValue="#{_docente.siapeNome}"
							    onsubmit="$('indicatorMembroPerm').style.display='inline';" 
						      	oncomplete="$('indicatorMembroPerm').style.display='none';">
						      	<h:column>
							      	<h:outputText value="#{_docente.siapeNome}"/>
						      	</h:column>
						      	<a4j:support event="onselect" reRender="dtPermanentes"
							      	actionListener="#{propostaGrupoPesquisaMBean.carregaDocentePermanente}" >
							      	<f:attribute name="docenteAutoComplete" value="#{_docente}"/>
						      	</a4j:support>
							</rich:suggestionbox>
							<img id="indicatorMembroPerm" src="/sigaa/img/indicator.gif" style="display: none;">
						</a4j:region>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div class="infoAltRem">
				        	<h:graphicImage value="/img/prodocente/lattes.gif" style="overflow: visible;"/>: Currículo do Pesquisador na Plataforma Lattes
				        	<br/><h:graphicImage value="/img/prodocente/question.png" style="overflow: visible;"/>: Endereço do CV não registrado no sistema
				        	<br/><h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Pesquisador
						</div>
						<rich:dataTable id="dtPermanentes" value="#{propostaGrupoPesquisaMBean.obj.membrosPermanentes}" 
								var="membro" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar"
								binding="#{propostaGrupoPesquisaMBean.membrosPermanentes}">
							<rich:column width="35%">
								<f:facet name="header"><f:verbatim>Pesquisador</f:verbatim></f:facet>
								<h:outputText value="#{membro.pessoa.nome}"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
								<h:outputText value="#{ membro.categoriaString }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
								<h:outputText value="<font color='red'>" rendered="#{membro.classificacao == 1}"  escape="false"/>
									<h:outputText value="#{membro.classificacaoString}" />
								<h:outputText value="</font>" rendered="#{membro.classificacao == 1}"  escape="false"/>
							</rich:column>
							<rich:column width="30%">
								<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
								<h:outputText value="#{ membro.tipoMembroGrupoPesqString }"/>
							</rich:column>
							<rich:column width="30%">
								<f:facet name="header"><f:verbatim>E-mail</f:verbatim></f:facet>
								<h:outputText value="#{ membro.pessoa.email }"/>
							</rich:column>
							<rich:column width="5%">
								<f:facet name="header"><h:outputText value="Lattes" /></f:facet>
								<a4j:commandLink id="showPanelOn" actionListener="#{propostaGrupoPesquisaMBean.carregarPainelMembroPermanente}" 
		               					oncomplete="Richfaces.showModalPanel('painelLattes')" immediate="true" reRender="painelLattes">  
									<h:graphicImage value="/img/prodocente/lattes.gif" style="overflow: visible;" rendered="#{not empty membro.enderecoLattes}" title="Currículo do Pesquisador na Plataforma Lattes"/>
									<h:graphicImage value="/img/prodocente/question.png" style="overflow: visible;" rendered="#{empty membro.enderecoLattes}" title="Endereço do CV não registrado no sistema"/>
							    </a4j:commandLink>
							</rich:column>
							<rich:column width="2%">
								<h:commandButton image="/img/delete.gif" action="#{ propostaGrupoPesquisaMBean.removeMembroEquipePermanente }" id="btRemove" rendered="#{!membro.coordenador}"
									alt="Remover Pesquisador" title="Remover Pesquisador" onclick="#{confirmDelete}"/>
							</rich:column>
						</rich:dataTable>
					</td>
				</tr>
                
   				<tr>
					<td colspan="2" class="subFormulario" style="padding-top: 10px;">Membros Associados</td>
				</tr>
                
                <tr>
					<td colspan="2">
						<div class="descricaoOperacao">
							<p>
								"Art. 3º, Parágrafo 4º: Os membros associados são professores aposentados, visitantes, substitutos, bolsistas
								de programas de fixação, servidores técnico-administrativos com Mestrado ou Doutorado, discentes de graduação
								e pós-graduação da ${ configSistema['siglaInstituicao'] }, pesquisadores mestres ou doutores de outras Instituições
								e profissionais de reconhecida competência técnico-científica na área do conhecimento."
							</p>
						</div>
					</td>
				</tr>
		        <tr>
					<td colspan="2">
						<p:tabView id="tabPanelAssociados" activeIndex="#{ propostaGrupoPesquisaMBean.abaAtivaMembrosAssociados }">
							<p:tab id="tabDocente" title="Docente">
								<table width="100%">
									<tr>
										<th width="15%" class="required">Docente:</th>
										<td>
											<a4j:region id="membroAssociadoReg">
												<h:inputText id="membroAssociado" value="#{ propostaGrupoPesquisaMBean.docenteAssociado.pessoa.nome }"
								                	size="80" maxlength="150"/>
								                <rich:suggestionbox id="suggestionMembroAssociado" width="400" height="100" minChars="3" 
												    for="membroAssociado" suggestionAction="#{propostaGrupoPesquisaMBean.autoCompleteDocenteAssociado}" 
												    var="_docente" fetchValue="#{_docente.siapeNome}"
												    onsubmit="$('indicatorDocente').style.display='';" 
											      	oncomplete="$('indicatorDocente').style.display='none';" 
											      	reRender="indicatorDocente">
											      	<h:column>
												      	<h:outputText value="#{_docente.siapeNome}"/>
											      	</h:column>
											      	<a4j:support event="onselect" focus="membroAssociado"  reRender="dtAssociados"
												      	actionListener="#{propostaGrupoPesquisaMBean.carregaDocenteAssociado}" >
												      	<f:attribute name="docenteAssoAutoComplete" value="#{_docente}"/>
											      	</a4j:support>
												</rich:suggestionbox>
												<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
											</a4j:region>
										</td>
									</tr>
								</table>
							</p:tab>
							<p:tab id="tabServidor" title="Servidor Técnico-Administrativo">
								<table width="100%">
									<tr>
										<th width="15%" class="required">Servidor:</th>
										<td>
											<a4j:region id="membroTecnAssociadoReg">
												<h:inputText id="membroTecnAssociado" value="#{ propostaGrupoPesquisaMBean.docenteAssociado.pessoa.nome }"
								                	size="80" maxlength="150"/>
								                <rich:suggestionbox id="suggestionMembroTecnAssociado" width="400" height="100" minChars="3" 
												    for="membroTecnAssociado" suggestionAction="#{propostaGrupoPesquisaMBean.autoCompleteTecnicoAssociado}" 
												    var="_docente" fetchValue="#{_docente.siapeNome}"
												    onsubmit="$('indicatorServidorTecn').style.display='';" 
											      	oncomplete="$('indicatorServidorTecn').style.display='none';" 
											      	reRender="indicatorServidorTecn">
											      	<h:column>
												      	<h:outputText value="#{_docente.siapeNome}"/>
											      	</h:column>
											      	<a4j:support event="onselect" focus="membroTecnAssociado"  reRender="dtAssociados"
												      	actionListener="#{propostaGrupoPesquisaMBean.carregaDocenteTecnAssociado}" >
												      	<f:attribute name="docenteAssoTecnAutoComplete" value="#{_docente}"/>
											      	</a4j:support>
												</rich:suggestionbox>
												<img id="indicatorServidorTecn" src="/sigaa/img/indicator.gif" style="display: none;">
											</a4j:region>
										</td>
									</tr>
								</table>
							</p:tab>
							<p:tab id="tabDiscente" title="Discente de Graduação ou Pós-Graduação">
								<table width="100%">
									<tr>
										<th width="15%" class="required">Discente:</th>
										<td>
											<a4j:region id="membroDiscAssociadoReg">
												<h:inputText id="membroDiscAssociado" value="#{ propostaGrupoPesquisaMBean.discenteAssociado.pessoa.nome }"
								                	size="80" maxlength="150"/>
								                <rich:suggestionbox id="suggestionMembroDiscAssociado" width="400" height="100" minChars="3" 
												    for="membroDiscAssociado" suggestionAction="#{propostaGrupoPesquisaMBean.autoCompleteDiscenteAssociado}" 
												    var="_discente" fetchValue="#{_discente.nome}"
												    onsubmit="$('indicatorDisc').style.display='';" 
											      	oncomplete="$('indicatorDisc').style.display='none';" 
											      	reRender="indicatorDisc">
											      	<h:column>
												      	<h:outputText value="#{_discente.nome}"/>
											      	</h:column>
											      	<a4j:support event="onselect" focus="membroDiscAssociado" reRender="dtAssociados"
												      	actionListener="#{propostaGrupoPesquisaMBean.carregaDiscenteAssociado}" >
												      	<f:attribute name="discenteAssoAutoComplete" value="#{_discente}"/>
											      	</a4j:support>
												</rich:suggestionbox>
												<img id="indicatorDisc" src="/sigaa/img/indicator.gif" style="display: none;">
											</a4j:region>
										</td>
									</tr>
								</table>
							</p:tab>
							<p:tab id="tabExterno" title="Pesquisador Externo">
								<h:panelGroup id="pnlPesqExterno">
									<table class="subFormulario" width="100%" id="tableExterno">
								   		<tr>
											<th>Estrangeiro:</th>
								   			<td>
												<h:selectOneRadio value="#{propostaGrupoPesquisaMBean.estrangeiro}" id="estrangeiro" 
															valueChangeListener="#{propostaGrupoPesquisaMBean.changeEstrangeiro}">
													<f:selectItems value="#{propostaGrupoPesquisaMBean.simNao}"/>
													<a4j:support event="onclick" reRender="pnlPesqExterno" />
												</h:selectOneRadio>
								   			</td>
								   		</tr>
								   		<tr>
								   			<th class="obrigatorio">
									   			<h:outputText id="cpf" rendered="#{!propostaGrupoPesquisaMBean.estrangeiro}">CPF:</h:outputText>
									   			<h:outputText id="passaporte" rendered="#{propostaGrupoPesquisaMBean.estrangeiro}" >Passaporte:</h:outputText>
								   			</th>
											<td>
												<h:inputText value="#{ propostaGrupoPesquisaMBean.docenteExterno.pessoa.cpf_cnpj }" size="14" maxlength="14"
					      							onkeypress="return formataCPF(this, event, null)" id="txtCPF" disabled="#{propostaGrupoPesquisaMBean.readOnly}" 
					      							rendered="#{!propostaGrupoPesquisaMBean.estrangeiro}">
					          						<f:converter converterId="convertCpf"/>
					          						<f:param id="paraCpf" name ="type" value="cpf" />
					          						<a4j:support action="#{propostaGrupoPesquisaMBean.carregaDocenteExterno}" event="onchange" 
					          							reRender="txtCPF, nomeDocenteExterno, emailDocenteExterno, sexo" />
					     						</h:inputText>
												<h:inputText value="#{propostaGrupoPesquisaMBean.docenteExterno.pessoa.passaporte}" 
													size="22" maxlength="20" id="passaporteDocenteExterno" rendered="#{propostaGrupoPesquisaMBean.estrangeiro}" >
					       						<a4j:support action="#{propostaGrupoPesquisaMBean.carregaDocenteExterno}" event="onchange" 
					       							reRender="txtCPF, nomeDocenteExterno, emailDocenteExterno, sexo" rendered="#{propostaGrupoPesquisaMBean.estrangeiro}"/>
												</h:inputText>
								   			</td>
								   		</tr>
								   		<tr>
								   			<th class="obrigatorio">Nome:</th>
											<td>
												<h:inputText value="#{propostaGrupoPesquisaMBean.docenteExterno.pessoa.nome}" size="70" 
												maxlength="100" id="nomeDocenteExterno" onkeyup="CAPS(this);" disabled="#{propostaGrupoPesquisaMBean.cpfEncontrado}"/>
											</td>
								   		</tr>
								   		<tr>
								   			<th class="obrigatorio">Email:</th>
								   			<td>
												<h:inputText value="#{propostaGrupoPesquisaMBean.docenteExterno.pessoa.email}" size="70" 
												maxlength="100" id="emailDocenteExterno" />
											</td>
								   		</tr>
								   		<tr>
											<th class="obrigatorio">Sexo:</th>
											<td>
												<h:selectOneRadio value="#{propostaGrupoPesquisaMBean.docenteExterno.pessoa.sexo}" id="sexo" 
													disabled="#{propostaGrupoPesquisaMBean.cpfEncontrado}">
													<f:selectItems value="#{propostaGrupoPesquisaMBean.mascFem}" />
												</h:selectOneRadio>
								   			</td>
								   		</tr>
								   		
								   		<tr>
								   			<th class="obrigatorio">Formação:</th>
								   			<td>
												<h:selectOneMenu value="#{propostaGrupoPesquisaMBean.docenteExterno.formacao.id}" id="formacao" >
													<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
													<f:selectItems value="#{formacao.allCombo}"/>
												</h:selectOneMenu>
								   			</td>
								   		</tr>
								   		<tr>
								   			<th class="obrigatorio">Instituição:</th>
								   			<td>
					
												<h:selectOneMenu value="#{propostaGrupoPesquisaMBean.docenteExterno.instituicao.id}" id="instituicao" >
													<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
													<f:selectItems value="#{instituicoesEnsino.allCombo}"/>
												</h:selectOneMenu>
								   			</td>
								   		</tr>
								   		<tr>
								   			<td align="center" colspan="5">
								   				<a4j:commandButton value="Adicionar" action="#{propostaGrupoPesquisaMBean.adicionarDocenteExterno}" id="adicionarExterno" reRender="dtAssociados, pnlPesqExterno"/>
								   			</td>
								   		</tr>
								   	</table>
							   	</h:panelGroup>
							</p:tab>
						</p:tabView>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div class="infoAltRem">
				        	<h:graphicImage value="/img/prodocente/lattes.gif" style="overflow: visible;"/>: Currículo do Pesquisador na Plataforma Lattes
				        	<br/><h:graphicImage value="/img/delete_old.gif" style="overflow: visible;"/>: Pesquisador não possui endereço eletrônico do currículo registrado no sistema
				        	<br/><h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Pesquisador
						</div>
						<rich:dataTable id="dtAssociados" value="#{propostaGrupoPesquisaMBean.obj.membrosAssociados}" 
								binding="#{propostaGrupoPesquisaMBean.membrosAssociados}" 
								var="membro" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
							<rich:column>
								<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
								<h:outputText value="#{ membro.pessoa.nome }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
								<h:outputText value="#{ membro.categoriaString }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
								<h:outputText value="<font color='red'>" rendered="#{membro.classificacao == 1}"  escape="false"/>
									<h:outputText value="#{membro.classificacaoString}" />
								<h:outputText value="</font>" rendered="#{membro.classificacao == 1}"  escape="false"/>
							</rich:column>
							<rich:column width="30%">
								<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
								<h:outputText value="#{ membro.tipoMembroGrupoPesqString }"/>
							</rich:column>
							<rich:column width="30%">
								<f:facet name="header"><f:verbatim>E-mail</f:verbatim></f:facet>
								<h:outputText value="#{ membro.pessoa.email }"/>
							</rich:column>
							<rich:column width="5%">
								<f:facet name="header"><h:outputText value="Lattes" /></f:facet>
								<a4j:commandLink id="showPanelOn" actionListener="#{propostaGrupoPesquisaMBean.carregarPainel}" 
		               					oncomplete="Richfaces.showModalPanel('painelLattes')" immediate="true" reRender="painelLattes">  
									<h:graphicImage value="/img/prodocente/lattes.gif" style="overflow: visible;" rendered="#{not empty membro.enderecoLattes}" title="Currículo do Pesquisador na Plataforma Lattes"/>
									<h:graphicImage value="/img/prodocente/question.png" style="overflow: visible;" rendered="#{empty membro.enderecoLattes}" title="Endereço do CV não registrado no sistema"/>
							    </a4j:commandLink>
							</rich:column>
							<rich:column width="5%" styleClass="centerAlign">
								<h:commandButton image="/img/delete.gif" action="#{ propostaGrupoPesquisaMBean.removeMembroEquipeAssociado }" id="bt_remove"
									alt="Remover Pesquisador" title="Remover Pesquisador" onclick="#{confirmDelete}"/>
							</rich:column>
						</rich:dataTable>
					</td>
				</tr>

			</tbody>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton id="btnVoltar" value="<< Voltar" action="#{ propostaGrupoPesquisaMBean.telaDadosGerais }" />
						<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{ confirm }" action="#{ propostaGrupoPesquisaMBean.cancelar }" />
						<h:commandButton id="btnAvancar" value="Avançar >>" action="#{ propostaGrupoPesquisaMBean.submeterMembros }" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
</f:view>

<script type="text/javascript">
	
	var verificaEstrangeiro = function() {
		if ( $('equipe:checkEstrangeiro').checked ) {
			$('equipe:cpfExterno').disable();
			$('equipe:nomeExterno').enable();
			$('equipe:sexoExterno').enable();
			$('equipe:cpfExterno').value = "";
			$('equipe:nomeExterno').value = "";
		} else {
			$('equipe:cpfExterno').enable();
			$('equipe:nomeExterno').disable();
			$('equipe:sexoExterno').disable();
		}
	}
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
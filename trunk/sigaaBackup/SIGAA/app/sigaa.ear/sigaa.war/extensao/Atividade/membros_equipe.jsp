<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>

<f:view >
	<h:messages showDetail="true"></h:messages>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<c:set var="COORDENADOR" value="<%= String.valueOf(FuncaoMembro.COORDENADOR) %>" scope="application" />

	<h2><ufrn:subSistema /> &gt; Servidores da Ação de Extensão</h2>

	<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			<ul>
				<li>As ações de Extensão Universitária são coordenadas por docente ou técnico-administrativo 
					com nível superior pertencente ao quadro permanente da ${ configSistema['siglaInstituicao'] }, lotado em Departamento Acadêmico, 
					Unidade Acadêmica Especializada ou Unidade Suplementar da ${ configSistema['siglaInstituicao'] }, 
					nos termos do Estatuto e do Regimento Geral da ${ configSistema['siglaInstituicao'] }.(Art. 19º da Res. 053/2008 - CONSEPE)
					<br />
				</li>
				<li>Cada Coordenador só poderá assumir, simultaneamente, duas ações de Extensão Universitária 
					da mesma modalidade. (Art. 20º da Res. 053/2008 - CONSEPE)
				</li>
			</ul>
			</td>
			<td><%@include file="passos_atividade.jsp"%></td>
		</tr>
		<tr>
			<td  colspan="2">
				<p><strong>OBSERVAÇÃO:</strong>  Para alterar os membros da equipe, inclusive o coordenador, utilize a opção "Alterar Membros da Equipe".</p>
				<p>Os dados informados só são cadastrados na base de dados quando clica-se em "Avançar >>".</p>
			</td>
		</tr>
		
	</table>
	</div>

	<h:form id="equipe" >
		
		<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}" />

		<table class="formulario" width="100%">
			<caption class="listagem">Informar membros da equipe da ação de extensão</caption>
		</table>
		<rich:tabPanel switchType="client" selectedTab="#{atividadeExtensao.tab}">
			<p style="text-align: center; font-style: italic; padding: 5px;">
				Selecione a categoria do membro para realizar a busca de acordo com os critérios específicos
			</p>
				<rich:tab label="Docente" name="DOCENTE">
						<table>
							<tr>
								<th width="35%" class="obrigatorio">Docente:</th>
								<td>
									<h:inputText id="nomeDocente" value="#{atividadeExtensao.docente.pessoa.nome}"
										size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.DOCENTE}" />
									<rich:suggestionbox id="suggestion_docente"  width="430" height="100" minChars="3" 
									    for="nomeDocente" suggestionAction="#{atividadeExtensao.autoCompleteNomeDocente}" 
									    var="_docente" fetchValue="#{_docente.siapeNome}"
									    onsubmit="$('indicatorDocente').style.display='';" 
								      	oncomplete="$('indicatorDocente').style.display='none';" 
								      	reRender="indicatorDocente">
									  	<f:param name="apenasAtivos" value="true" />
							    	  	<f:param name="apenasDocentes" value="true" />
								      	<h:column>
									      	<h:outputText value="#{_docente.siapeNome}"/>
								      	</h:column>
								      	<a4j:support event="onselect" focus="nomeDocente" 
									      	actionListener="#{atividadeExtensao.carregaDocente}" >
									      	<f:attribute name="docenteAutoComplete" value="#{_docente}"/>
								      	</a4j:support>
									</rich:suggestionbox>
										<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
								</td>
							</tr>
		
							<tr>
								<th width="35" class="obrigatorio">Função:</th>
								<td align="left">
									<h:selectOneMenu id="funcaoMembroEquipeDocente" value="#{atividadeExtensao.funcaoDocente.id}">
										<f:selectItem itemValue="0"
											itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{funcaoMembroEquipe.allServidoresCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							
							<tr id="linhaMembroGerenciador">
							<th width="35%" class="obrigatorio" align="right">Permitir Gerenciar Participantes:</th>
								<td align="left">
									<h:selectOneRadio
										value="#{atividadeExtensao.docenteGerenciaParticipantes}"
										id="membroGerenciador">
										<f:selectItem itemValue="true" itemLabel="Sim" />
										<f:selectItem itemValue="false" itemLabel="Não" />
									</h:selectOneRadio>
								</td>
							</tr>
							
						</table>
					</rich:tab>
					
					<rich:tab label="Técnico Administrativo" name="SERVIDOR">
						<table>
							<tr>
								<th width="15%" class="obrigatorio">Técnico Administrativo:</th>
								<td>
									<h:inputText id="nomeServidor" value="#{atividadeExtensao.servidor.pessoa.nome}" size="70"
										onfocus="$('categoriaMembro').value=#{categoriaMembro.SERVIDOR}" />
									<rich:suggestionbox id="suggestion_servidor"  width="430" height="100" minChars="3" 
									      for="nomeServidor" suggestionAction="#{atividadeExtensao.autoCompleteNomeServidorTecnico}" 
									      var="_servidor" fetchValue="#{_servidor.siapeNome}"
									      onsubmit="$('indicatorServidor').style.display='';" 
									      oncomplete="$('indicatorServidor').style.display='none';" 
									      reRender="indicatorServidor">
										<h:column>
											<h:outputText value="#{_servidor.siapeNome}" />
										</h:column>
										<a4j:support event="onselect" focus="nomeServidor" 
									      	actionListener="#{atividadeExtensao.carregaServidorTecnico}" >
									      	<f:attribute name="servidorAutoComplete" value="#{_servidor}"/>
								     	</a4j:support>
									</rich:suggestionbox>
									<img id="indicatorServidor" src="/sigaa/img/indicator.gif" style="display: none;">
									<ufrn:help img="/img/ajuda.gif">Apenas os servidores do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
								</td>
							</tr>
							<tr>
								<th width="15%" class="obrigatorio">Função:</th>
								<td align="left">
									<h:selectOneMenu id="funcaoMembroEquipeServidores" value="#{atividadeExtensao.funcaoServidor.id}" >
										<f:selectItem itemValue="0"
											itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{funcaoMembroEquipe.allServidoresCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>

							<tr id="linhaServidorGerenciador">
							<th width="35%" class="obrigatorio" align="right">Permitir Gerenciar Participantes:</th>
								<td align="left">
									<h:selectOneRadio value="#{ atividadeExtensao.docenteGerenciaParticipantes }"
										id="servidorGerenciador">
										<f:selectItem itemValue="true" itemLabel="Sim" />
										<f:selectItem itemValue="false" itemLabel="Não" />
									</h:selectOneRadio>
								</td>
							</tr>
												
						</table>
					</rich:tab>
					
					<rich:tab label="Discente" name="DISCENTE">
						<table>
							<tr>
								<th width="15%" class="obrigatorio">Discente:</th>
								<td>
									<h:inputText id="nomeDiscente" value="#{atividadeExtensao.discente.pessoa.nome}" size="70"
										onfocus="$('categoriaMembro').value=#{categoriaMembro.DISCENTE}" />
									<rich:suggestionbox id="suggestion_discente"  width="430" height="100" minChars="3" 
									      for="nomeDiscente" suggestionAction="#{atividadeExtensao.autoCompleteNomeDiscente}" 
									      var="_discente" fetchValue="#{_discente.matriculaNome}" 
									      onsubmit="$('indicatorDiscente').style.display='';" 
									      oncomplete="$('indicatorDiscente').style.display='none';" 
									      reRender="indicatorDiscente">
										<h:column>
											<h:outputText value="#{_discente.matriculaNome}" />
										</h:column>
										<a4j:support event="onselect" focus="nomeDiscente" 
									      	actionListener="#{atividadeExtensao.carregaDiscente}">
									      	<f:attribute name="discenteAutoComplete" value="#{_discente}"/>
								     	</a4j:support>
									</rich:suggestionbox>
									<img id="indicatorDiscente" src="/sigaa/img/indicator.gif" style="display: none;"> 
									<ufrn:help img="/img/ajuda.gif">Apenas os Discentes Ativos da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
								</td>
							</tr>
							<tr>
								<th width="15%" class="obrigatorio">Função:</th>
								<td align="left">
									<h:selectOneMenu id="funcaoMembroEquipeDiscente" value="#{atividadeExtensao.funcaoDiscente.id}" >
										<f:selectItem itemValue="0"
											itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{funcaoMembroEquipe.allDiscentesCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>		
						</table>
					</rich:tab>
					
					<rich:tab label="Participante Externo" name="EXTERNO">
						<table>
							<tr>
								<th width="15%" id="nome_participanteExterno" class="obrigatorio">Nome:</th>
								<td>
									<h:inputText id="nomeParticipanteExterno" value="#{atividadeExtensao.participanteExterno.pessoa.nome}" size="70"
										onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}" />
									<rich:suggestionbox id="suggestion_participante_externo"  width="430" height="100" minChars="3" 
									      for="nomeParticipanteExterno" suggestionAction="#{atividadeExtensao.autoCompleteNomeParticipanteExterno}" 
									      var="_participanteExterno" fetchValue="#{_participanteExterno.nomeCPF}" 
									      onsubmit="$('indicatorParticipanteExterno').style.display='';" 
									      oncomplete="$('indicatorParticipanteExterno').style.display='none';" 
									      reRender="indicatorParticipanteExterno">
										<h:column>
											<h:outputText value="#{_participanteExterno.nomeCPF}" />
										</h:column>
										<a4j:support event="onselect" focus="nomeParticipanteExterno"
											reRender="cpfExterno, ajaxErros, emailExterno, sexoExterno, formacaoExterno, instituicaoExterno, _CPF, checkEstrangeiro, nomeParticipanteExterno" 
											actionListener="#{atividadeExtensao.carregaParticipanteExterno}">
										<f:attribute name="participanteExternoAutoComplete" value="#{_participanteExterno}"/>
									</a4j:support>
									</rich:suggestionbox>
									<ufrn:help img="/img/ajuda.gif">Apenas participantes já cadastrados pelo usuário atual serão listados</ufrn:help>
									<img id="indicatorParticipanteExterno" src="/sigaa/img/indicator.gif" style="display: none;">
								</td>
							</tr>
							<tr>
								<th id="_CPF" class=obrigatorio>CPF:</th>
								<td align="left">
									<h:panelGroup id="ajaxErros">
										<h:dataTable value="#{atividadeExtensao.avisosAjax}" var="msg"
											rendered="#{not empty atividadeExtensao.avisosAjax}">
											<t:column>
												<h:outputText value="<font color='red'>#{msg.mensagem}</font>"
													escape="false" />
											</t:column>
										</h:dataTable>
									</h:panelGroup> 
									<h:inputText onkeypress="formataCPF(this, event, null)"
										id="cpfExterno" value="#{atividadeExtensao.cpf}" size="14"
										maxlength="14"
										onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}"
										disabled="#{atividadeExtensao.participanteExterno.pessoa.internacional}"
										style="#{atividadeExtensao.membroEquipe.selecionado ? 'background: #F0F0F0' : 'background:' }">
										<a4j:support event="onblur"
											reRender="cpfExterno, ajaxErros, emailExterno, sexoExterno, formacaoExterno, instituicaoExterno, _CPF, checkEstrangeiro, nomeParticipanteExterno"
											action="#{atividadeExtensao.buscarParticipanteExternoByCPF}" />
										<%-- 
				    						<f:converter converterId="convertCpf" />
				    					--%>
										<f:param value="#{categoriaMembro.EXTERNO}"
											name="categoriaMembro" />
									</h:inputText> 
									<h:selectBooleanCheckbox
										value="#{atividadeExtensao.participanteExterno.pessoa.internacional}"
										id="checkEstrangeiro" styleClass="noborder"
										onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}"
										onmousedown="( ( $('_CPF').className=='obrigatorio' ) ? $('_CPF').className='' : $('_CPF').className='obrigatorio' )">
										<a4j:support  event="onclick"
											reRender="cpfExterno, ajaxErros, emailExterno, sexoExterno, formacaoExterno, instituicaoExterno, _CPF, checkEstrangeiro, nomeParticipanteExterno"
											action="#{atividadeExtensao.buscarParticipanteExternoByCPF}" />
										<f:param value="#{categoriaMembro.EXTERNO}"
											name="categoriaMembro" />
									</h:selectBooleanCheckbox> <label for="checkEstrangeiro"> ESTRANGEIRO (sem CPF)</label>
								</td>
							</tr>
							<tr>
								<th id="email_externo" width="15%">E-Mail</th>
								<td align="left">
									<h:inputText id="emailExterno" size="70" 
										value="#{atividadeExtensao.participanteExterno.pessoa.email}" 
										disabled="#{!atividadeExtensao.membroEquipe.selecionado}" 
										style="#{!atividadeExtensao.membroEquipe.selecionado ? 'background: #F0F0F0' : 'background:' }"/>
								</td>
							</tr>
							<tr>
								<th width="15%" class="obrigatorio">Função:</th>
								<td align="left">
									<h:selectOneMenu id="funcaoMembroEquipeExterno" onchange="emailExterno();"
										value="#{atividadeExtensao.funcaoExterno.id}">
										<f:selectItem itemValue="0" 
											itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{funcaoMembroEquipe.allServidoresCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th class="obrigatorio">Sexo:</th>
								<td colspan="3" align="left">
									<h:selectOneMenu id="sexoExterno"
										value="#{atividadeExtensao.participanteExterno.pessoa.sexo}"
										readonly="#{atividadeExtensao.readOnly}"
										disabled="#{!atividadeExtensao.membroEquipe.selecionado}"
										style="#{!atividadeExtensao.membroEquipe.selecionado ? 'background: #F0F0F0' : 'background:' }">
										<f:selectItem itemValue="M" itemLabel="MASCULINO" />
										<f:selectItem itemValue="F" itemLabel="FEMININO" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th class="obrigatorio">Formação:</th>
								<td colspan="3" align="left">
									<h:selectOneMenu id="formacaoExterno"
										value="#{atividadeExtensao.participanteExterno.formacao.id}"
										readonly="#{atividadeExtensao.readOnly}">
										<f:selectItem itemValue="0"
											itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{formacao.allCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th class="obrigatorio">Instituição:</th>
								<td colspan="3" align="left">
									<h:inputText
										value="#{atividadeExtensao.participanteExterno.instituicao}"
										size="50" maxlength="250" id="instituicaoExterno" /> 
									<ufrn:help img="/img/ajuda.gif">Instituição de origem do participante externo</ufrn:help>
								</td>
							</tr>
						</table>
					</rich:tab>
		</rich:tabPanel>
		
		<table class="formulario" width="100%">
				<tr>
					<th width="15%" class="obrigatorio" align="left">Remuneração:</th>
					<td align="left">
						<h:selectOneRadio
							value="#{atividadeExtensao.membroEquipe.remunerado}"
							id="membroRemunerado">
							<f:selectItem itemValue="true" itemLabel="Sim" />
							<f:selectItem itemValue="false" itemLabel="Não" />
						</h:selectOneRadio>
					</td>
				</tr>
	
				<tr style="background: #DEDFE3;">
					<td colspan="2" align="center">
						<h:commandButton action="#{atividadeExtensao.adicionarMembroEquipe}" value="Adicionar Membro" />
					</td>
				</tr>
	
				<tr>
					<td colspan="2" align="center"><br />
					<div class="infoAltRem"><h:graphicImage
						value="/img/delete.gif" style="overflow: visible;" />: Remover Membro<br />
					</div>
					</td>
				</tr>
	
				<tr>
					<td colspan="2" class="subFormulario">Membros da Equipe da Ação de Extensão (${ fn:length(atividadeExtensao.membrosEquipe) })</td>
				</tr>
	
				<tr>
					<td colspan="2"><input type="hidden" name="idMembro" value="0" id="idMembro" /> 
					<t:dataTable id="dt_membro_equipe" value="#{atividadeExtensao.membrosEquipe}" var="membro"
						align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	
						<t:column>
							<f:facet name="header">
								<f:verbatim>Nome</f:verbatim>
							</f:facet>
							<h:outputText value="#{membro.pessoa.nome}"
								rendered="#{not empty membro.pessoa}" id="nome" />
						</t:column>
						<t:column>
							<f:facet name="header">
								<f:verbatim>Função</f:verbatim>
							</f:facet>
							<h:outputText value="<font color='red'>"
								rendered="#{membro.coordenadorAtivo}" escape="false" />
							<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
							<h:outputText value="</font>" rendered="#{membro.coordenadorAtivo}" escape="false" />
						</t:column>
						<t:column>
							<f:facet name="header">
								<f:verbatim>Categoria</f:verbatim>
							</f:facet>
							<h:outputText value="#{membro.categoriaMembro.descricao}" id="categoria"
								rendered="#{not empty membro.categoriaMembro}" />
						</t:column>
						<t:column width="30%">
							<f:facet name="header">
								<f:verbatim>Departamento</f:verbatim>
							</f:facet>
							<h:outputText value="#{membro.unidade.nome}"  />
						</t:column>
	
						<t:column width="5%" styleClass="centerAlign">
							<h:commandButton rendered="#{not membro.coordenadorAtivo}" image="/img/delete.gif"
								action="#{atividadeExtensao.removeMembroEquipe}" id="bt_remove"
								alt="Remover Membro da Ação" title="Remover Membro"
								onclick="$(idMembro).value=#{membro.id};return confirm('Deseja Remover este Membro da Equipe da Ação de Extensão?')" />
						</t:column>
	
					</t:dataTable>
					</td>
				</tr>
	
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton id="btn_irAnteriorServidores" value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" />
							<h:commandButton id="btn_cancelar" value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" /> 
							<h:commandButton id="btn_irProximoServidores" value="Avançar >>" action="#{atividadeExtensao.submeterServidores}" />
						</td>
					</tr>
				</tfoot>
		</table>
		<br />
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>

<script type="text/javascript">



	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-membro');
	        tabs.addTab('membro-docente', "Docente")
	        tabs.addTab('membro-servidor', "Servidor Técnico-Administrativo");
	        tabs.addTab('membro-discente', "Discente");
			tabs.addTab('membro-externo', "Participante Externo");

   		        tabs.activate('membro-docente');	////padrão

   		      <c:if test="${sessionScope.aba != null}">
				    	tabs.activate('${sessionScope.aba}');
			 </c:if>

	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
	
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

	function emailExterno(){
		if( $('equipe:funcaoMembroEquipeExterno').value == 1 ){
			$('email_externo').className = 'obrigatorio';
		}else{
			$('email_externo').className = '';
		}
	}

	window.onload(emailExterno());
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
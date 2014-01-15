<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="membroProjeto" />
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	
	<h2><ufrn:subSistema /> > Cadastrar Membro na Equipe</h2>

	<h:form id="equipe">
	
		<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}"/>
		<input type="hidden" name="lista_origem" id="lista_origem" value="${listaOrigem}"/>
		
		<table class="formulario" width="100%">
			<caption class="listagem">Dados do Membro da Equipe</caption>
		</table>
		<rich:tabPanel switchType="client">
			<p style="text-align: center; font-style: italic; padding: 5px;">
						Selecione a categoria do membro para realizar a busca de acordo com os critérios específicos
			</p>
						<rich:tab label="Docente">
							<table>
								<tr>
									<th width="15%"  class="required">Docente:</th>
									<td>
									    <h:inputText value="#{membroProjeto.docente.pessoa.nome}" id="docente" size="65" onfocus="$('categoriaMembro').value=#{categoriaMembro.DOCENTE}"/>
										<rich:suggestionbox id="suggestion_docente"  width="430" height="100" minChars="3" 
												frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
											    for="docente" suggestionAction="#{membroProjeto.autoCompleteNomeDocente}" 
											    var="_docente" fetchValue="#{_docente.siapeNome}"
											    onsubmit="$('indicatorDocente').style.display='';" 
										      	oncomplete="$('indicatorDocente').style.display='none';" 
										      	reRender="indicatorDocente">
											  <f:param name="apenasAtivos" value="true" />
									    	  <f:param name="apenasDocentes" value="true" />
										      <h:column>
											      <h:outputText value="#{_docente.siapeNome}"/>
										      </h:column>
										      <a4j:support event="onselect" focus="docente" 
											      actionListener="#{membroProjeto.carregaDocente}" >
											      <f:attribute name="docenteAutoComplete" value="#{_docente}"/>
										      </a4j:support>
										</rich:suggestionbox>
										<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
									</td>
								</tr>
								<tr>
									<th width="15%"  class="required">Função:</th>
									<td align="left">
										<h:selectOneMenu id="funcaoMembroEquipeDocente" value="#{membroProjeto.funcaoDocente.id}">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
											<f:selectItems value="#{membroProjeto.allFuncaoDocente}"/>
										</h:selectOneMenu>
									</td>
								</tr>										
							</table>
						</rich:tab>
						
						<rich:tab label="Servidor">
							<table>
								<tr>
									<th width="15%" class="required">Servidor:</th>
									<td>
										<h:inputText id="servidor"	value="#{membroProjeto.servidor.pessoa.nome}" size="80" onfocus="$('categoriaMembro').value=#{categoriaMembro.SERVIDOR}"/>
										<rich:suggestionbox id="suggestion_servidor"  width="430" height="100" minChars="3" 
											frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
										      for="servidor" suggestionAction="#{membroProjeto.autoCompleteNomeServidorTecnico}" 
										      var="_servidor" fetchValue="#{_servidor.siapeNome}"
										      onsubmit="$('indicatorServidor').style.display='';" 
										      oncomplete="$('indicatorServidor').style.display='none';" 
										      reRender="indicatorServidor">
											<h:column>
												<h:outputText value="#{_servidor.siapeNome}" />
											</h:column>
											<a4j:support event="onselect" focus="servidor" 
										      	actionListener="#{membroProjeto.carregaServidorTecnico}" >
										      	<f:attribute name="servidorAutoComplete" value="#{_servidor}"/>
									     	</a4j:support>
										</rich:suggestionbox>
										<img id="indicatorServidor" src="/sigaa/img/indicator.gif" style="display: none;">
										<ufrn:help img="/img/ajuda.gif">Apenas os servidores do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
									</td>
								</tr>
								<tr>
									<th width="15%"  class="required">Função:</th>
									<td align="left">
										<h:selectOneMenu id="funcaoMembroEquipeServidor" value="#{membroProjeto.funcaoServidor.id}">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
											<f:selectItems value="#{membroProjeto.allFuncaoServidor}"/>
										</h:selectOneMenu>
									</td>
								</tr>									
							</table>
						</rich:tab>
						
						<rich:tab label="Discente">
	                        <table>
			                     <tr>
			                        <th width="15%" class="required">Discente:</th>
			                        <td>
			                            <h:inputText   id="nomeDiscente" value="#{membroProjeto.discente.pessoa.nome}" size="80" onfocus="$('categoriaMembro').value=#{categoriaMembro.DISCENTE}" /> 
										<rich:suggestionbox id="suggestion_discente"  width="430" height="100" minChars="3" 
											frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
										      for="nomeDiscente" suggestionAction="#{membroProjeto.autoCompleteNomeDiscente}" 
										      var="_discente" fetchValue="#{_discente.matriculaNome}" 
										      onsubmit="$('indicatorDiscente').style.display='';" 
										      oncomplete="$('indicatorDiscente').style.display='none';" 
										      reRender="indicatorDiscente">
											<h:column>
												<h:outputText value="#{_discente.matriculaNome}" />
											</h:column>
											<a4j:support event="onselect" focus="nomeDiscente" 
										      	actionListener="#{membroProjeto.carregaDiscente}">
										      	<f:attribute name="discenteAutoComplete" value="#{_discente}"/>
									     	</a4j:support>
										</rich:suggestionbox>
										<img id="indicatorDiscente" src="/sigaa/img/indicator.gif" style="display: none;">
			                            <ufrn:help img="/img/ajuda.gif">Apenas os Discentes Ativos da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
			                        </td>
			                    </tr>
								<tr>
									<th width="15%"  class="required">Função:</th>
									<td align="left">
										<h:selectOneMenu id="funcaoMembroEquipeDiscente" value="#{membroProjeto.funcaoDiscente.id}">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
											<f:selectItems value="#{membroProjeto.allFuncaoDiscente}"/>
										</h:selectOneMenu>
									</td>
								</tr>		                    
		                   </table>
						</rich:tab>
						
						<rich:tab label="Participante Externo">
								<table>
									<tr colspan="3">
										<th  class="required">CPF:</th>
										<td align="left">
										
											<h:panelGroup id="ajaxErros">
												<h:dataTable  value="#{membroProjeto.avisosAjax}" var="msg" rendered="#{not empty membroProjeto.avisosAjax}">
													<t:column><h:outputText value="<font color='red'>#{msg.mensagem}</font>" escape="false"/></t:column>
												</h:dataTable>
											</h:panelGroup>							
										
											<h:inputText id="cpfExterno" value="#{membroProjeto.cpf}" size="14" maxlength="14"  
													onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}" 
													disabled="#{membroProjeto.membroEquipe.selecionado}"
													style="#{membroProjeto.membroEquipe.selecionado ? 'background: #F0F0F0' : 'background:' }"
													onkeypress="return formataCPF(this, event, null)">
		    										<a4j:support event="onblur" reRender="ajaxErros, nomeExterno, emailExterno, sexoExterno, formacaoExterno, instituicaoExterno" 
		    										action="#{membroProjeto.buscarParticipanteExternoByCPF}" />
		    										<f:param value="#{categoriaMembro.EXTERNO}" name="categoriaMembro"/>
											</h:inputText>
											
											<h:selectBooleanCheckbox value="#{membroProjeto.participanteExterno.pessoa.internacional}" id="checkEstrangeiro" styleClass="noborder" onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}">
													<a4j:support event="onclick" reRender="cpfExterno, ajaxErros, nomeExterno, emailExterno, sexoExterno, formacaoExterno, instituicaoExterno" 
		    										action="#{membroProjeto.buscarParticipanteExternoByCPF}"/>
		    										<f:param value="#{categoriaMembro.EXTERNO}" name="categoriaMembro"/>
											</h:selectBooleanCheckbox>
											<label for="checkEstrangeiro"> ESTRANGEIRO (sem CPF)</label> 
											
										</td>
									</tr>
									
									<tr>
										<th width="15%"  class="required">Nome:</th>
										<td colspan="3">
											<h:inputText value="#{membroProjeto.participanteExterno.pessoa.nome}" size="75" 
											maxlength="75"  id="nomeExterno" disabled="#{!membroProjeto.membroEquipe.selecionado}" 
											style="#{!membroProjeto.membroEquipe.selecionado ? 'background: #F0F0F0' : 'background:' }"	/>
										</td>
									</tr>
									<tr>
										<th id="email_externo" width="15%">E-Mail</th>
										<td align="left">
											<h:inputText id="emailExterno" size="70" 
												value="#{membroProjeto.participanteExterno.pessoa.email}" 
												disabled="#{!membroProjeto.membroEquipe.selecionado}"
												style="#{!membroProjeto.membroEquipe.selecionado ? 'background: #F0F0F0' : 'background:' }" />
										</td>
									</tr>
									<tr>
										<th width="15%"  class="required">Função:</th>
										<td align="left">
											<h:selectOneMenu id="funcaoMembroEquipeExterno" onchange="emailExterno();" 
												value="#{membroProjeto.funcaoExterno.id}" >
												<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
												<f:selectItems value="#{membroProjeto.allFuncaoExterno}"/>
											</h:selectOneMenu>
										</td>
									</tr>
	
									<tr>
										<th  class="required">Sexo:</th>
										<td colspan="3" align="left">
											<h:selectOneMenu id="sexoExterno"
												value="#{membroProjeto.participanteExterno.pessoa.sexo}" 	
												readonly="#{membroProjeto.readOnly}" 
												disabled="#{!membroProjeto.membroEquipe.selecionado}" 
												style="#{!membroProjeto.membroEquipe.selecionado ? 'background: #F0F0F0' : 'background:' }">
												<f:selectItem itemValue="M" itemLabel="MASCULINO"/>
												<f:selectItem itemValue="F" itemLabel="FEMININO"/>
											</h:selectOneMenu>
										</td>
									</tr>
									
									<tr>
										<th  class="required">Formação:</th>
										<td colspan="3" align="left">
											<h:selectOneMenu id="formacaoExterno"
												value="#{membroProjeto.participanteExterno.formacao.id}" 	readonly="#{membroProjeto.readOnly}">
													<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
													<f:selectItems value="#{formacao.allCombo}"/>
											</h:selectOneMenu>
										</td>
									</tr>
									<tr>
										<th  class="required">Instituição:</th>
										<td colspan="3" align="left">
											<h:inputText value="#{membroProjeto.participanteExterno.instituicao}" size="50" maxlength="250" id="instituicaoExterno"/>
											<ufrn:help img="/img/ajuda.gif">Instituição de origem do participante externo</ufrn:help>
										</td>
									</tr>
	
								</table>
						</rich:tab>
			</rich:tabPanel>
			<table class="formulario" width="100%">
			<tr>
				<th width="15%"  class="required">Remuneração / Bolsa:</th>
				<td>
					<table>
						<tr>
							<td>
								<h:selectOneRadio value="#{membroProjeto.membroEquipe.remunerado}" id="membroRemunerado">
									<f:selectItem itemValue="true" itemLabel="Sim"/>
									<f:selectItem itemValue="false" itemLabel="Não"/>
								</h:selectOneRadio>
							</td>
							<td>
								<ufrn:help img="/img/ajuda.gif">Marque SIM caso o membro da equipe possua bolsa ou outro tipo de remuneração.</ufrn:help>
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<th width="15%" class="required">Data Início:</th>
				<td><t:inputCalendar size="10" value="#{membroProjeto.membroEquipe.dataInicio}" popupDateFormat="dd/MM/yyyy"
				renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))"  
				maxlength="10" id="inicio"/></td>
			</tr>

			<tr>
				<th width="15%" class="required">CH Semanal:</th>
				<td> 
					<h:inputText id="chSemanalDedicada" value="#{membroProjeto.membroEquipe.chDedicada}" maxlength="3" 
						size="5" onkeyup="formatarInteiro(this)" rendered="#{ membroProjeto.chPassivoAlteracao }" /> 
					<h:outputText value="0" rendered="#{ not membroProjeto.chPassivoAlteracao }" /> horas						
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{membroProjeto.adicionarMembroEquipe}" value="#{membroProjeto.confirmButton}"/>
						<h:commandButton value="<< Voltar" action="#{membroProjeto.voltarLista}" />
						<h:commandButton id="btn_cancelar" value="Cancelar" action="#{membroProjeto.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
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

function emailExterno(){
	if( $('equipe:funcaoMembroEquipeExterno').value == 1 ){
		$('email_externo').className = 'required';
	}else{
		$('email_externo').className = '';
	}
}

window.onload(emailExterno());
</script> 
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
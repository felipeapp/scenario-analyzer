<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<c:set var="COORDENADOR" value="<%= String.valueOf(FuncaoMembro.COORDENADOR) %>" 	scope="application"/>
	
	<h2><ufrn:subSistema /> &gt; Equipe do Projeto</h2>
	
	<div class="descricaoOperacao">
		<table width="100%">
			<tr>
				<td width="50%">
						<ul>
							<li>
								Todo Projeto de Ação Acadêmica Integrada deve ter obrigatoriamente um Coordenador.
							</li>
							<li>
								Todo Coordenador deverá ter um e-mail cadastrado/atualizado no SIGPRH.
							</li>						
						</ul>
				</td>
				<td>
					<%@include file="passos_projeto.jsp"%>
				</td>
			</tr>
		</table>
	</div>

	<h:form id="equipe">
		<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}"/>

		<table class="formulario" width="100%">
			<caption>Informar membros da equipe do projeto</caption>
		</table>		
		<rich:tabPanel switchType="client" >
				<p style="text-align: center; font-style: italic; padding: 5px;">
					Selecione a categoria do membro para realizar a busca de acordo com os critérios específicos
				</p>
				<rich:tab label="Docente">
					<table>
						<tr>
							<th width="15%"  class="required">Docente:</th>
							<td>
								<h:inputText id="nomeDocente"	value="#{projetoBase.docente.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.DOCENTE}"/>
								<rich:suggestionbox id="suggestion_docente"  width="430" height="100" minChars="3" 
										frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
									    for="nomeDocente" suggestionAction="#{projetoBase.autoCompleteNomeDocente}" 
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
									      	actionListener="#{projetoBase.carregaDocente}" >
									      	<f:attribute name="docenteAutoComplete" value="#{_docente}"/>
								      	</a4j:support>
								</rich:suggestionbox>
								<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
								<ufrn:help img="/img/ajuda.gif">Apenas os docentes do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
							</td>
						</tr>
						<tr>
							<th width="15%"  class="required">Função:</th>
							<td align="left">
								<h:selectOneMenu id="funcaoMembroEquipeDocente" value="#{projetoBase.funcaoDocente.id}">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
										<f:selectItems value="#{funcaoMembroEquipe.allServidoresCombo}"/>
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
								<h:inputText id="nomeServidor"	value="#{projetoBase.servidor.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.SERVIDOR}"/>
								 <rich:suggestionbox id="suggestion_servidor"  width="430" height="100" minChars="3" 
										  frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
									      for="nomeServidor" suggestionAction="#{projetoBase.autoCompleteNomeServidorTecnico}" 
									      var="_servidor" fetchValue="#{_servidor.siapeNome}"
									      onsubmit="$('indicatorServidor').style.display='';" 
									      oncomplete="$('indicatorServidor').style.display='none';" 
									      reRender="indicatorServidor">
										<h:column>
											<h:outputText value="#{_servidor.siapeNome}" />
										</h:column>
										<a4j:support event="onselect" focus="nomeServidor" 
									      	actionListener="#{projetoBase.carregaServidorTecnico}" >
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
								<h:selectOneMenu id="funcaoMembroEquipeServidor" value="#{projetoBase.funcaoServidor.id}">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
										<f:selectItems value="#{funcaoMembroEquipe.allServidoresCombo}"/>
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
								<h:inputText id="nomeDiscente"	value="#{projetoBase.discente.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.DISCENTE}"/>
								 <rich:suggestionbox id="suggestion_discente"  width="430" height="100" minChars="3" 
										frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
									      for="nomeDiscente" suggestionAction="#{projetoBase.autoCompleteNomeDiscente}" 
									      var="_discente" fetchValue="#{_discente.matriculaNome}" 
									      onsubmit="$('indicatorDiscente').style.display='';" 
									      oncomplete="$('indicatorDiscente').style.display='none';" 
									      reRender="indicatorDiscente">
										<h:column>
											<h:outputText value="#{_discente.matriculaNome}" />
										</h:column>
										<a4j:support event="onselect" focus="nomeDiscente" 
									      	actionListener="#{projetoBase.carregaDiscente}">
									      	<f:attribute name="discenteAutoComplete" value="#{_discente}"/>
								     	</a4j:support>
								</rich:suggestionbox>
								<img id="indicatorDiscente" src="/sigaa/img/indicator.gif" style="display: none;">
							</td>								
						</tr>
						<tr>
							<th width="15%"  class="required">Função:</th>
							<td align="left">
								<h:selectOneMenu id="funcaoMembroEquipeDiscente" value="#{projetoBase.funcaoDiscente.id}">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
										<f:selectItems value="#{funcaoMembroEquipe.discentesColaboradoresCombo}"/>
								</h:selectOneMenu>
							</td>
						</tr>
					</table>
				</rich:tab>
				
				<rich:tab label="Participante Externo">
						<table>
							<tr>
								<th  class="required" id="_CPF">CPF:</th>
								<td align="left">
									<h:inputText  onkeypress="formataCPF(this, event, null)" id="cpfExterno" value="#{projetoBase.cpf}" 
											size="14" maxlength="14"  onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}" 
											disabled="#{projetoBase.membroEquipe.selecionado}" 
											style="#{projetoBase.membroEquipe.selecionado ? 'background:#F0F0F0' : 'background:'}">
    										<a4j:support event="onblur" reRender="nomeExterno, emailExterno, sexoExterno, formacaoExterno, instituicaoExterno" 
    										action="#{projetoBase.buscarParticipanteExternoByCPF}"/>
    										<f:param value="#{categoriaMembro.EXTERNO}" name="categoriaMembro"/>
									</h:inputText>
									
									<h:selectBooleanCheckbox value="#{projetoBase.participanteExterno.pessoa.internacional}" id="checkEstrangeiro" styleClass="noborder" onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}"
											onmousedown="( ($('_CPF').className == 'required') ? $('_CPF').className = '' : $('_CPF').className = 'required' )">
											<a4j:support event="onclick" reRender="cpfExterno, ajaxErros, nomeExterno, emailExterno, sexoExterno, formacaoExterno, instituicaoExterno, _CPF" 
    										action="#{projetoBase.buscarParticipanteExternoByCPF}"/>
    										<f:param value="#{categoriaMembro.EXTERNO}" name="categoriaMembro"/>
									</h:selectBooleanCheckbox>
									<label for="checkEstrangeiro"> ESTRANGEIRO (sem CPF)</label> 
								</td>
							</tr>

							<tr>
								<th width="15%"  class="required">Nome:</th>
								<td align="left" colspan="3">
									<h:inputText value="#{projetoBase.participanteExterno.pessoa.nome}" size="70" maxlength="75"  
									id="nomeExterno" disabled="#{!projetoBase.membroEquipe.selecionado}" 
									style="#{!projetoBase.membroEquipe.selecionado ? 'background:#F0F0F0' : 'background:'}"/>
								</td>
							</tr>
							<tr>
								<th id="email_externo" width="15%">E-Mail</th>
								<td align="left">
									<h:inputText id="emailExterno" size="70" 
										value="#{projetoBase.participanteExterno.pessoa.email}" 
										disabled="#{!projetoBase.membroEquipe.selecionado}" 
										style="#{!projetoBase.membroEquipe.selecionado ? 'background:#F0F0F0' : 'background:'}"/>
								</td>
							</tr>
							<tr>
								<th width="15%"  class="required">Função:</th>
								<td align="left">
									<h:selectOneMenu id="funcaoMembroEquipeExterno" onchange="emailExterno();"
										value="#{projetoBase.funcaoExterno.id}">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
											<f:selectItems value="#{funcaoMembroEquipe.allServidoresCombo}"/>
									</h:selectOneMenu>
								</td>
							</tr>

							<tr>
								<th  class="required">Sexo:</th>
								<td align="left" colspan="3">
									<h:selectOneMenu id="sexoExterno"
										value="#{projetoBase.participanteExterno.pessoa.sexo}" 	readonly="#{projetoBase.readOnly}" 
										disabled="#{!projetoBase.membroEquipe.selecionado}" 
										style="#{!projetoBase.membroEquipe.selecionado ? 'background:#F0F0F0' : 'background:'}" >
										<f:selectItem itemValue="M" itemLabel="MASCULINO"/>
										<f:selectItem itemValue="F" itemLabel="FEMININO"/>
									</h:selectOneMenu>
								</td>
							</tr>
							
							<tr>
								<th  class="required">Formação:</th>
								<td colspan="3">
									<h:selectOneMenu id="formacaoExterno"
										value="#{projetoBase.participanteExterno.formacao.id}" 	readonly="#{projetoBase.readOnly}">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>																								
											<f:selectItems value="#{formacao.allCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th  class="required">Instituição:</th>
								<td colspan="3">
									<h:inputText value="#{projetoBase.participanteExterno.instituicao}" size="50" maxlength="250" id="instituicaoExterno"/>
									<ufrn:help img="/img/ajuda.gif">Instituição de origem do participante externo</ufrn:help>
								</td>
							</tr>

						</table>
				</rich:tab>
		</rich:tabPanel>
		
		<table class="formulario" width="100%">
				<tr>
					<th width="15%"  class="required">Remuneração:</th>
					<td>
							<h:selectOneRadio value="#{projetoBase.membroEquipe.remunerado}" id="membroRemunerado">
								<f:selectItem itemValue="true" itemLabel="Sim"/>
								<f:selectItem itemValue="false" itemLabel="Não"/>
							</h:selectOneRadio>
					</td>				
				</tr>	
	
				<tr>
					<th  width="15%" class="required">CH Semanal:</th>
					<td>
						<h:inputText id="chSemanalDedicada" maxlength="3" value="#{projetoBase.membroEquipe.chDedicada}" size="5" onkeyup="formatarInteiro(this)" /> horas
					</td>
				</tr>
	
	   			<tr style="background: #DEDFE3;">
					<td colspan="2" align="center">
						<h:commandButton action="#{projetoBase.adicionarMembroEquipe}" value="Adicionar Membro"/>
					</td>
				</tr>
					
				<tr>
					<td colspan="2" align="center">
					<br />
					<div class="infoAltRem">	    			
		    			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover<br/>
					</div>
					</td>
				</tr>
					
				<tr>
					<td colspan="2" class="subFormulario">
						Membros da Equipe do Projeto
					</td>
				</tr>
		
				<tr>
					<td colspan="2">
						<input type="hidden" name="idMembro" value="0" id="idMembro"/>
						<t:dataTable id="dt_membro_equipe" value="#{projetoBase.obj.equipe}" var="membro" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
		
								<t:column>
									<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
									<h:outputText value="#{membro.pessoa.nome}" rendered="#{not empty membro.pessoa}" id="nome" />
								</t:column>
								<t:column>
									<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
									<h:outputText value="#{membro.categoriaMembro.descricao}" rendered="#{not empty membro.categoriaMembro}" id="categoria" />
								</t:column>
								<t:column>
									<f:facet name="header"><f:verbatim>Função</f:verbatim></f:facet>
									<h:outputText value="<font color='red'>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
												<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
									<h:outputText value="</font>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
								</t:column>
								<t:column width="30%">
									<f:facet name="header"><f:verbatim>Departamento</f:verbatim></f:facet>
									<h:outputText value="#{membro.servidor.unidade.sigla}" rendered="#{not empty membro.servidor}" />
									<h:outputText value="#{membro.discente.curso.unidade.sigla}" rendered="#{(not empty membro.discente) && (not empty membro.discente.curso)}" />
									<h:outputText value="--" rendered="#{not empty membro.participanteExterno}" />
								</t:column>
								
								<t:column width="5%" styleClass="centerAlign">
									<h:commandButton image="/img/delete.gif" action="#{projetoBase.removeMembroEquipe}" id="bt_remove"
										alt="Remover Servidor da Ação" title="Remover"  
										onclick="$(idMembro).value=#{membro.id};return confirm('Deseja Remover este Membro da Equipe?')"/>
								</t:column>
		
						</t:dataTable>
					</td>
				</tr>
		
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton id="btPassoAnteriorEquipe" value="<< Voltar" action="#{projetoBase.passoAnterior}" />
							<h:commandButton id="btCancelar" value="Cancelar" action="#{projetoBase.cancelar}" onclick="#{confirm}"/>
							<h:commandButton id="btSubmeterEquipe" value="Gravar e Avançar >>" action="#{projetoBase.submeterEquipe}"/>
						</td>
					</tr>
				</tfoot>
  	  </table>
  	  <br />
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
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Notificação de Invenção &gt; Autores da Invenção</h2>

	<h:form id="autores">
		<h:inputHidden value="#{invencao.confirmButton}" />
		<h:inputHidden value="#{invencao.obj.id}" />
		<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}"/>

		<table class="formulario" width="90%">
			<caption>Autores</caption>
			
			<tr>
				<td class="subFormulario" colspan="2"> Autores da Invenção </td>
			</tr>
			
			<tr>
			<td colspan="2">
				<p style="text-align: center; font-style: italic; padding: 5px;">
					Selecione uma das abas para realizar a busca de acordo com os critérios específicos
				</p>

			<div id="tabs-autores">
					<div id="autor-docente">
						<table width="100%" class="formulario">
							<tr>
								<th width="15%"  class="required">Docente:</th>
								<td>
									<h:inputHidden id="idDocente" value="#{invencao.docente.id}"></h:inputHidden>
									<h:inputText id="nomeDocente" value="#{invencao.docente.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.DOCENTE}">
										<a4j:support event="onblur" reRender="enderecoDocente, nacionalidadeDocente, telefoneDocente, emailDocente" action="#{invencao.buscarDadosDocente}"/>
									</h:inputText>
 
										<ajax:autocomplete
										source="autores:nomeDocente" target="autores:idDocente"
										baseUrl="/sigaa/ajaxDocente" className="autocomplete"
										indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
										parser="new ResponseXmlToHtmlListParser()" 
										/> <span id="indicator"
										style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
										<ufrn:help img="/img/ajuda.gif">Apenas os docentes do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
										
								</td>
							</tr>
							<tr>
								<th width="15%" class="required">Endereço:</th>
								<td><h:inputText id="enderecoDocente" size="70" value="#{invencao.docente.pessoa.endereco}"/></td>
							</tr>
							<tr>
								<th width="15%" class="required">Nacionalidade:</th>
								<td colspan="3">
									<h:selectOneMenu id="nacionalidadeDocente" value="#{invencao.docente.pessoa.pais.id}">
										<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
										<f:selectItems value="#{pais.allCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th width="15%" class="required">Telefone:</th>
								<td> <h:inputText id="telefoneDocente" size="10" value="#{invencao.docente.pessoa.telefone}"/></td>
							</tr>
							<tr>
								<th width="15%" class="required">E-mail:</th>
								<td> <h:inputText id="emailDocente" size="50" value="#{invencao.docente.pessoa.email}"/></td>
							</tr>
						</table>
					</div>

					<div id="autor-discente">
						<table width="100%">
							<tr>
								<th width="15%" class="required">Discente:</th>
								<td>
									<h:inputHidden id="idDiscente" value="#{invencao.discente.id}"></h:inputHidden>
									<h:inputText id="nomeDiscente"	value="#{invencao.discente.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.DISCENTE}">
										<a4j:support event="onblur" reRender="enderecoDiscente, nacionalidadeDiscente, telefoneDiscente, emailDiscente" action="#{invencao.buscaDadosDiscente}"/>
									</h:inputText>

									 <ajax:autocomplete
										source="autores:nomeDiscente" target="autores:idDiscente"
										baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
										indicator="indicator" minimumCharacters="3" parameters="nivel=ufrn,status=todos"
										parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
										style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
										<ufrn:help img="/img/ajuda.gif">Apenas alunos da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
								</td>
							</tr>
							<tr>
								<th width="15%" class="required">Endereço:</th>
								<td><h:inputText id="enderecoDiscente" size="70" value="#{invencao.discente.pessoa.endereco}"/></td>
							</tr>
							<tr>
								<th width="15%" class="required">Nacionalidade:</th>
								<td colspan="3">
									<h:selectOneMenu id="nacionalidadeDiscente" value="#{invencao.discente.pessoa.pais.id}">
										<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
										<f:selectItems value="#{pais.allCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th width="15%" class="required">Telefone:</th>
								<td> <h:inputText id="telefoneDiscente" size="10" value="#{invencao.discente.pessoa.telefone}"/></td>
							</tr>
							<tr>
								<th width="15%" class="required">E-mail:</th>
								<td> <h:inputText id="emailDiscente" size="50" value="#{invencao.discente.pessoa.email}"/></td>
							</tr>
						</table>
					</div>
					
					<div id="autor-servidor">
						<table width="100%">
							<tr>
								<th width="15%" class="required">Servidor:</th>
								<td>
									<h:inputHidden id="idServidor" value="#{invencao.servidor.id}"></h:inputHidden>
									<h:inputText id="nomeServidor"	value="#{invencao.servidor.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.SERVIDOR}">
										<a4j:support event="onblur" reRender="enderecoServidor, nacionalidadeServidor, telefoneServidor, emailServidor" action="#{invencao.buscaDadosServidor}"/>
									</h:inputText>

									 <ajax:autocomplete
										source="autores:nomeServidor" target="autores:idServidor"
										baseUrl="/sigaa/ajaxServidor" className="autocomplete"
										indicator="indicator" minimumCharacters="3" parameters="tipo=todos,situacao=ativo"
										parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
										style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
										<ufrn:help img="/img/ajuda.gif">Apenas os servidores do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
								</td>
							</tr>
							<tr>
								<th width="15%" class="required">Endereço:</th>
								<td><h:inputText id="enderecoServidor" size="70" value="#{invencao.servidor.pessoa.endereco}"/></td>
							</tr>
							<tr>
								<th width="15%" class="required">Nacionalidade:</th>
								<td colspan="3">
									<h:selectOneMenu id="nacionalidadeServidor" value="#{invencao.servidor.pessoa.pais.id}">
										<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
										<f:selectItems value="#{pais.allCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th width="15%" class="required">Telefone:</th>
								<td> <h:inputText id="telefoneServidor" size="10" value="#{invencao.servidor.pessoa.telefone}"/></td>
							</tr>
							<tr>
								<th width="15%" class="required">E-mail:</th>
								<td> <h:inputText id="emailServidor" size="50" value="#{invencao.servidor.pessoa.email}"/></td>
							</tr>
						</table>
					</div>

					<div id="autor-externo">

							<table width="100%">
							
								<tr colspan="3">
									<th  class="required">CPF:</th>
									<td>
									
										<h:panelGroup id="ajaxErros">
											<h:dataTable  value="#{invencao.avisosAjax}" var="msg" rendered="#{not empty invencao.avisosAjax}">
												<t:column><h:outputText value="<font color='red'>#{msg.mensagem}</font>" escape="false"/></t:column>
											</h:dataTable>
										</h:panelGroup>							
									
										<h:inputText id="cpfExterno" value="#{invencao.cpf}" size="11" maxlength="11" onkeyup="formatarInteiro(this);" onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}" disabled="#{invencao.inventor.selecionado}">
	    										<a4j:support event="onblur" reRender="ajaxErros, nomeExterno, sexoExterno, enderecoExterno, nacionalidadeExterno, telefoneExterno, emailExterno, formacaoExterno, instituicaoExterno" 
	    										action="#{invencao.buscarParticipanteExternoByCPF}"/>
	    										<f:param value="#{categoriaMembro.EXTERNO}" name="categoriaMembro"/>
										</h:inputText>
										
										<h:selectBooleanCheckbox value="#{invencao.docenteExterno.pessoa.internacional}" id="checkEstrangeiro" styleClass="noborder" onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}">
												<a4j:support event="onclick" reRender="cpfExterno, ajaxErros, nomeExterno, sexoExterno, enderecoExterno, nacionalidadeExterno, telefoneExterno, emailExterno, formacaoExterno, instituicaoExterno" 
	    										action="#{invencao.buscarParticipanteExternoByCPF}"/>
	    										<f:param value="#{categoriaMembro.EXTERNO}" name="categoriaMembro"/>
										</h:selectBooleanCheckbox>
										<label for="checkEstrangeiro"> ESTRANGEIRO (sem CPF)</label> 
										
									</td>
								</tr>
								
								<tr>
									<th width="15%"  class="required">Nome:</th>
									<td colspan="3">
										<h:inputText value="#{invencao.docenteExterno.pessoa.nome}" size="75" maxlength="75"  id="nomeExterno" disabled="#{!invencao.inventor.selecionado}" />
									</td>
								</tr>
								<tr>
									<th  class="required">Sexo:</th>
									<td colspan="3">
										<h:selectOneMenu id="sexoExterno"
											value="#{invencao.docenteExterno.pessoa.sexo}" 	readonly="#{invencao.readOnly}" disabled="#{!invencao.inventor.selecionado}" >
											<f:selectItem itemValue="M" itemLabel="MASCULINO"/>
											<f:selectItem itemValue="F" itemLabel="FEMININO"/>
										</h:selectOneMenu>
									</td>
								</tr>
								
								<tr>
									<th width="15%" class="required">Endereço:</th>
									<td><h:inputText id="enderecoExterno" size="70" value="#{invencao.docenteExterno.pessoa.endereco}"/></td>
								</tr>
								<tr>
								<th width="15%" class="required">Nacionalidade:</th>
								<td colspan="3">
									<h:selectOneMenu id="nacionalidadeExterno" value="#{invencao.docenteExterno.pessoa.pais.id}">
										<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
										<f:selectItems value="#{pais.allCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
								<tr>
									<th width="15%" class="required">Telefone:</th>
									<td> <h:inputText id="telefoneExterno" size="10" value="#{invencao.docenteExterno.pessoa.telefone}"/></td>
								</tr>
								<tr>
									<th width="15%" class="required">E-mail:</th>
									<td> <h:inputText id="emailExterno" size="50" value="#{invencao.docenteExterno.pessoa.email}"/></td>
								</tr>
								
								<tr>
									<th  class="required">Formação:</th>
									<td colspan="3">
										<h:selectOneMenu id="formacaoExterno"
											value="#{invencao.docenteExterno.formacao.id}" >
												<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA FORMAÇÃO --"/>
												<f:selectItems value="#{formacao.allCombo}"/>
										</h:selectOneMenu>
									</td>
								</tr>
								<tr>
									<th  class="required">Instituição/Empresa:</th>
									<td colspan="3">
										<h:selectOneMenu id="instituicaoExterno"
											value="#{invencao.docenteExterno.instituicao.id}" >
												<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA INSTITUIÇÃO --"/>
												<f:selectItems value="#{instituicoesEnsino.allCombo}"/>
										</h:selectOneMenu>
										<ufrn:help img="/img/ajuda.gif">Instituição/Empresa de origem do participante externo</ufrn:help>
									</td>
								</tr>
							</table>
							
					</div>

				</div>
			</td>
		</tr>
			
			<tr>
				<td colspan="2" align="center">
					<br/>
					<h:commandButton action="#{invencao.adicionarInventor}" value="Adicionar" id="btAdicionar" />
					<br/>
				</td>
			</tr>
			
			<c:if test="${not empty invencao.obj.inventores}">
				<tr>
					<td colspan="2">
						<div class="infoAltRem">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Autor
						</div>
					</td>
				</tr>
			
				<tr>
					<td colspan="2" class="subFormulario">	Lista de autores da invenção </td>
				</tr>
				
				<input type="hidden" value="0" id="idInventor" name="idInventor"/>
				<input type="hidden" value="0" id="idPessoa" name="idPessoa"/>
				
				<h:panelGroup id="autoresUFRN">
				  <c:if test="${invencao.autorUfrn}">
					<tr>
						<td colspan="2" class="subFormulario">	Autores da Instituição </td>
					</tr>
					<tr>
						<td colspan="2">
					
							<t:dataTable id="dataTableInventoresUfrn" value="#{invencao.obj.inventores}" var="inventor" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
								<t:column>
									<f:facet name="header">
										<h:outputText>Nome</h:outputText>
									</f:facet>
									<h:outputText value="#{inventor.pessoa.nome}" rendered="#{not empty inventor.pessoa and inventor.ufrn}"/>
								</t:column>
								
								<t:column>
									<f:facet name="header">
										<h:outputText>Categoria</h:outputText>
									</f:facet>
									<h:outputText value="#{inventor.categoriaMembro.descricao}" rendered="#{not empty inventor.categoriaMembro and inventor.ufrn}" />
								</t:column>
								
								<t:column>
									<f:facet name="header">
										<h:outputText>Departamento / Curso</h:outputText>
									</f:facet>
								 	<h:outputText value="#{inventor.origem}" rendered="#{inventor.ufrn}" />
								</t:column>
	
								<t:column width="5%" styleClass="centerAlign">
									<h:commandButton image="/img/delete.gif" action="#{invencao.removerInventor}"
										title="Remover Autor"  alt="Remover Autor"   
										onclick="$(idInventor).value=#{inventor.id}; $(idPessoa).value=#{inventor.pessoa.id}; return confirm('Deseja Remover este Autor da Invenção?')" id="remAutor" 
										rendered="#{not empty inventor.pessoa and inventor.ufrn}"/>
								</t:column>
							</t:dataTable>
						</td>
					</tr>
				</c:if>
				</h:panelGroup>
				
				<h:panelGroup id="autoresExternos">
				<c:if test="${invencao.autorExterno}">
					<tr>
						<td colspan="2" class="subFormulario">	Autores Externos </td>
					</tr>
					<tr>
						<td colspan="2">
				
							<t:dataTable id="dataTableInventoresExterno" value="#{invencao.obj.inventores}" var="inventor" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				
								<t:column>
									<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
									<h:outputText value="#{inventor.pessoa.nome}" rendered="#{not empty inventor.pessoa and not inventor.ufrn}"/>
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
									<h:outputText value="#{inventor.categoriaMembro.descricao}" rendered="#{not empty inventor.categoriaMembro and not inventor.ufrn}" />
								</t:column>
								
								<t:column>
									<f:facet name="header"><f:verbatim>Instituição</f:verbatim></f:facet>
										<h:outputText value="#{inventor.origem}" rendered="#{not inventor.ufrn}" />
									<%--
										<h:outputText value="#{inventor.docenteExterno.instituicao.nome}" rendered="#{not empty inventor.docenteExterno and not inventor.ufrn}" />
									--%>
								</t:column>
	
								<t:column width="5%" styleClass="centerAlign">
									<h:commandButton image="/img/delete.gif" action="#{invencao.removerInventor}"
										title="Remover Autor"  alt="Remover Autor"   
										onclick="$(idInventor).value=#{inventor.id}; $(idPessoa).value=#{inventor.pessoa.id}; return confirm('Deseja Remover este Autor da Invenção?')" id="remAutor" 
										rendered="#{not empty inventor.pessoa and not inventor.ufrn}"/>
								</t:column>
				
							</t:dataTable>
						</td>
					</tr>
				</c:if>
				</h:panelGroup>
			</c:if>
			
			<tfoot>
			<tr>
				<td colspan="4">
					<h:panelGroup id="botoes">
						<h:commandButton value="<< Voltar" action="#{invencao.telaFinanciamentos}" />
						<h:commandButton value="Cancelar" action="#{invencao.cancelar}" onclick="#{confirm}" />
						<h:commandButton value="Avançar >>" action="#{invencao.submeterInventores}" />
					</h:panelGroup>
				</td>
			</tr>
			</tfoot>
		</table>
		
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	</h:form>
</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-autores');
	        tabs.addTab('autor-docente', "Docente")
	        tabs.addTab('autor-discente', "Discente");
	        tabs.addTab('autor-servidor', "Servidor");
			tabs.addTab('autor-externo', "Autor externo");

   		        tabs.activate('autor-docente');	////padrão

   		      <c:if test="${sessionScope.aba != null}">
				    	tabs.activate('${sessionScope.aba}');
			 </c:if>

	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
	
	var verificaEstrangeiro = function() {
		if ( $('autores:checkEstrangeiro').checked ) {
			$('autores:cpfExterno').disable();
			$('autores:nomeExterno').enable();
			$('autores:sexoExterno').enable();
			$('autores:cpfExterno').value = "";
			$('autores:nomeExterno').value = "";
		} else {
			$('autores:cpfExterno').enable();
			$('autores:nomeExterno').disable();
			$('autores:sexoExterno').disable();
		}
	}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

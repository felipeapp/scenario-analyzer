<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema/> > Banca de Pós > Membros da Banca</h2>

	<h:form id="form" prependId="false">
		<table class="formulario" width="100%">
			<caption class="formulario">Membros da Banca</caption>
			<tr>
				<th width="10%">Função:</th>
				<td>
					<h:selectOneRadio value="#{bancaPos.membroBanca.funcao}" id="funcao" onclick="submit();">
						<f:selectItems value="#{bancaPos.funcoesCombo }" id="itensFuncoes"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<a4j:status id="carregando">
						<f:facet name="start">
							<h:graphicImage value="/img/indicator.gif" />
						</f:facet>
					</a4j:status>
					<table class="subFormulario" width="100%" id="geral">
					<tr><td>
					<h:panelGroup rendered="#{bancaPos.membroBanca.presidenteBanca or bancaPos.membroBanca.examinadorInterno }">
						<table class="subFormulario" width="100%" id="interno">
							<caption>Membro Interno do Programa</caption>
							<tr>
								<th width="20%">Docente do Programa:</th>
								<td>
								<h:selectOneMenu value="#{bancaPos.docenteEquipe}" id="docente">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{bancaPos.equipeDocente}"/>
								</h:selectOneMenu>
								</td>
							</tr>
						</table>
					</h:panelGroup>
					<h:panelGroup rendered="#{bancaPos.membroBanca.examinadorExternoPrograma }">
						<table class="subFormulario" width="100%" id="externoPrograma">
							<caption>Membro Externo ao Programa</caption>
							<tr>
								<th width="20%">Docente da ${ configSistema['siglaInstituicao'] }:</th>
								<td>
									<h:inputHidden value="#{bancaPos.membroBanca.docenteExternoPrograma.id}" id="idDocente"/>
									<c:set var="idAjax" value="idDocente"/>
									<c:set var="cedidos" value="true"/>
									<c:set var="nomeAjax" value="bancaPos.membroBanca.docenteExternoPrograma.nome"/>
									<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>
								</td>
							</tr>
						</table>
					</h:panelGroup>
				
					<h:panelGroup id="panelExterno" rendered="#{bancaPos.membroBanca.externo }">
						
						<table class="subFormulario" width="100%" id="externo">
							<caption>Membro Externo à Instituição</caption>
						
							<h:panelGroup rendered="#{bancaPos.cadastroNovoMembro}">
								<tr id="trNacionalidade">
									<th>Nacionalidade:</th>
									<td>
										<h:selectOneRadio value="#{ bancaPos.membroBanca.pessoaMembroExterno.internacional }" 
											id="nacionalidade" valueChangeListener="#{bancaPos.carregaNacionalidade}" 
											onchange="submit();">
											<f:selectItem itemValue="false" itemLabel="Nacional"/>
											<f:selectItem itemValue="true" itemLabel="Estrangeira"/>
										</h:selectOneRadio>
										
									</td>
								</tr>
								<tr id="trCpf">
									<th width="20%" id="labelCpf" class="required">CPF:</th>
									<td>
									<h:inputText id="cpf" onkeypress="formataCPF(this, event, null)"
										value="#{bancaPos.membroBanca.pessoaMembroExterno.cpf_cnpj}" size="19" maxlength="14">
										<f:converter converterId="convertCpf"/>
									</h:inputText>
									</td>
								</tr>
								<a4j:region rendered="#{ bancaPos.membroBanca.pessoaMembroExterno.internacional }">
								<tr id="trPassaporte">
									<th width="20%">Passaporte:</th>
									<td>
										<h:inputText id="passaporte" value="#{bancaPos.membroBanca.pessoaMembroExterno.passaporte}"
											 size="19" maxlength="14"/>
										<ufrn:help>Caso o membro não possua CPF, o campo Passaporte deverá ser preenchido.</ufrn:help>
									</td>
								</tr>
								</a4j:region>
								<tr id="trNome">
									<th class="required">Nome:</th>
									<td>								
										<h:inputText id="nome" value="#{bancaPos.membroBanca.pessoaMembroExterno.nome}" 
											onkeyup="CAPS(this);" size="60" maxlength="80"></h:inputText>
										<ufrn:help>O docente já está cadastrado? Clique na opção "Buscar Membro" ao lado, para localizá-lo.</ufrn:help>
										<a4j:commandButton value="Buscar Membro" id="btBuscaMembro" 
											reRender="form" actionListener="#{bancaPos.clearMembroBancaPos}">
											<f:setPropertyActionListener value="#{false}" target="#{bancaPos.cadastroNovoMembro}"/>
										</a4j:commandButton>
									</td>
								</tr>
							</h:panelGroup>
							
							<h:panelGroup rendered="#{!bancaPos.cadastroNovoMembro}">
								<tr id="trBuscaNome">
									<th class="required">Nome:</th>
									<td>																																				
										<a4j:region>
											
											<h:inputText value="#{bancaPos.membroBanca.pessoaMembroExterno.nome}" 
												id="buscaNome"	size="60" maxlength="80" onkeyup="CAPS(this);" />
											
											<rich:suggestionbox for="buscaNome" width="450" height="100" 
												minChars="5" id="suggestionDocenteExterno"  var="_p"  status="carregando"
												suggestionAction="#{bancaPos.autoCompleteMembroExterno}" 
												fetchValue="#{_p.nome}" >
												<h:column>
													<h:outputText value="#{_p.nome}" />
												</h:column>
											 	
												<a4j:support event="onselect" reRender="form" status="carregando" 
													actionListener="#{bancaPos.carregaMembroExterno}">
													<f:attribute name="pessoaExterna" value="#{_p}"/>
												</a4j:support>
												 
											</rich:suggestionbox>
											
										</a4j:region>
																																					
										<ufrn:help>Não encontrou o docente na listagem? Cadastre-o clicando na opção "Cadastrar novo Membro" ao lado.</ufrn:help>
										<a4j:commandButton value="Cadastrar novo Membro" actionListener="#{bancaPos.clearMembroBancaPos}" 
											id="btNovoMembro" reRender="form">
											<f:setPropertyActionListener value="#{true}" target="#{bancaPos.cadastroNovoMembro}"/>
										</a4j:commandButton>
									</td>
								</tr>
							</h:panelGroup>
							
							<tr>
								<th class="required">Email:</th>
								<td>
								<h:inputText id="email" value="#{bancaPos.membroBanca.pessoaMembroExterno.email}" size="60" maxlength="50" />
								</td>
							</tr>
							<tr>
								<th class="required">Instituição de Ensino:</th>
								<td>
								<h:selectOneMenu value="#{bancaPos.membroBanca.instituicao.id}" id="instituicao" >
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{instituicoesEnsino.allCombo}" id="itensInstituicoesEnsino"/>
								</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th class="required">Maior Formação:</th>
								<td>
								<h:selectOneMenu value="#{bancaPos.membroBanca.maiorFormacao.id}"   id="formacao">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{formacao.allCombo}" id="itensFormacao"/>
								</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th>Ano de Conclusão:</th>
								<td>
								<h:inputText id="anoConclusao" value="#{bancaPos.membroBanca.anoConclusao}" 
									size="4" maxlength="4" onkeypress="formatarInteiro(this); return ApenasNumeros(event);"/>
								</td>
							</tr>
							
							<tr>
								<th class="required">Sexo:</th>
								<td><h:selectOneRadio value="#{bancaPos.membroBanca.pessoaMembroExterno.sexo}" id="sexo">
									<f:selectItems value="#{bancaPos.mascFem}"/>
								</h:selectOneRadio>
								</td>
							</tr>								
						</table>
					</h:panelGroup>
					</td></tr>
					<tfoot>
						<tr>
							<td><h:commandButton id="add" value="Adicionar Membro"
								actionListener="#{bancaPos.adicionarMembroBanca}" />
							</td>
						</tr>
					</tfoot>			
				</table>
			</td>
			</tr>
			<tr>
				<td class="subFormulario" colspan="2">Listagem dos Membros da Banca</td>
			</tr>		
				
				
			<tr>
				<td colspan="2">
					<c:if test="${not empty bancaPos.obj.membrosBanca}">
						<div class="infoAltRem">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
							Remover Membro
						</div>
						
						<t:dataTable var="membro" styleClass="subFormulario" rowClasses="linhaPar,linhaImpar" 
							value="#{ bancaPos.obj.membrosBanca }" rowIndexVar="row" id="listaMembrosBancaPos" width="100%">						
							<f:facet name="caption"><h:outputText value="Membros da Banca"/></f:facet>
							<t:column width="20%">
								<f:facet name="header"><h:outputText value="Função"/></f:facet>
								<h:outputText value="#{membro.tipoDescricao}"/>
							</t:column>
							<t:column>
								<f:facet name="header"><h:outputText value="Membro"/></f:facet>
								<h:outputText value="#{membro.membroIdentificacao}"/>
							</t:column>
							<t:column width="1%">
								<h:commandLink  actionListener="#{bancaPos.removerMembroBanca}"	title="Remover Membro" 
									id="linkRemoverMembroDaBanca" onclick="#{confirmDelete}">
									<h:graphicImage url="/img/delete.gif" />
									<f:attribute name="pessoaMembroBancaPos" value="#{membro.pessoa}"/>
								</h:commandLink>	
							</t:column>
						</t:dataTable>
					</c:if>				
				</td>
			</tr>	
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="dados" value="<< Dados Gerais"	action="#{bancaPos.telaDadosBanca}" immediate="true" />
						<h:commandButton value="Cancelar" id="cancelamento" onclick="#{confirm}" action="#{bancaPos.cancelar}"  />
						<h:commandButton value="Próximo Passo >> " id="confirmar" action="#{bancaPos.submeterMembros}" />
					</td>
				</tr>
			</tfoot>					
			
		</table>
	</h:form>

	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

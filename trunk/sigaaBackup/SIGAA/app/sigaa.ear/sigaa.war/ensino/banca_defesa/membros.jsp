<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
	<a4j:keepAlive beanName="membroBancaMBean"/>	
	<a4j:keepAlive beanName="buscaBancaDefesaMBean"/>
	<a4j:keepAlive beanName="bancaDefesaMBean"/>
	<h2 class="title"><ufrn:subSistema/> &gt; Banca de Defesa &gt; Membros da Banca</h2>

	<h:form id="form" prependId="false">
		<table class="formulario" width="100%">
			<caption class="formulario">Membros da Banca</caption>
			<tr>
				<th width="20%">Tipo do Membro da Banca:</th>
				<td>
					<h:selectOneRadio onclick="submit();" value="#{bancaDefesaMBean.membro.tipo.id}" id="funcao">
						<f:selectItems value="#{tipoMembroBancaMBean.tipoMembroCombo }" id="itensFuncoes"/>
					</h:selectOneRadio>
				</td>
			</tr>			
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
						<tr>
							<td>
								<h:panelGroup rendered="#{bancaDefesaMBean.membro.interno}">
									<table class="subFormulario" width="100%" id="interno">
										<caption>Membro Interno</caption>
										<tr>
											<th width="20%">Docente da ${ configSistema['siglaInstituicao'] }:</th>
											<td>
												<h:inputHidden value="#{bancaDefesaMBean.membro.docenteExterno.id}" id="idDocente"/>
												<c:set var="idAjax" value="idDocente"/>
												<c:set var="cedidos" value="true"/>
												<c:set var="nomeAjax" value="bancaDefesaMBean.membro.docenteExterno.nome"/>
												<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>											
											</td>
										</tr>			
									</table>
								</h:panelGroup>
								<h:panelGroup rendered="#{bancaDefesaMBean.membro.externoInstituicao}">
									<table class="subFormulario" width="100%" id="externo">
										<caption>Membro Externo à Instituição</caption>
										<tr id="trNacionalidade">
											<th>Nacionalidade:</th>
											<td>
												<h:selectOneRadio value="#{ bancaDefesaMBean.membro.pessoaMembroExterno.internacional }" id="nacionalidade" onclick="selecionaNacionalidade(this.value);">
													<f:selectItem itemValue="false" itemLabel="Nacional"/>
													<f:selectItem itemValue="true" itemLabel="Estrangeira"/>
													<a4j:support event="onclick" reRender="form"></a4j:support>
												</h:selectOneRadio>
											</td>
										</tr>
									 	<tr id="trCpf">
											<th width="20%" id="labelCpf">CPF:</th>
											<td>
												<h:inputText value="#{ bancaDefesaMBean.membro.pessoaMembroExterno.cpf_cnpj }" size="16" maxlength="14" 
													onkeypress="return formataCPF(this, event, null);" id="cpf">
													<f:converter converterId="convertCpf"/>
													<f:param name="type" value="cpf" />
												</h:inputText>											
											</td>
										</tr>
										<tr id="trPassaporte">
											<th width="20%" class="required">Passaporte:</th>
											<td><h:inputText id="passaporte" value="#{bancaDefesaMBean.membro.pessoaMembroExterno.passaporte}" size="19" maxlength="14"/></td>
										</tr>
										<tr id="trBuscaNome">
											<th class="required">Nome:</th>
											<td>																																				
												<a4j:region>
													<h:inputHidden id="idPessoa" value="#{bancaDefesaMBean.membro.pessoaMembroExterno.id}"></h:inputHidden>
													<h:inputText id="buscaNome" value="#{bancaDefesaMBean.membro.pessoaMembroExterno.nome}" onkeyup="CAPS(this);" size="60" maxlength="80"></h:inputText>
													<ajax:autocomplete source="buscaNome"
															parameters="tipo=externoInstituicao"
															target="idPessoa"
															baseUrl="/sigaa/ajaxDocente"	
															className="autocomplete" minimumCharacters="3"
															parser="new ResponseXmlToHtmlListParser()"/>			
													<a4j:status>
														<f:facet name="start">
															<h:graphicImage value="/img/indicator.gif" />
														</f:facet>
													</a4j:status>
													
													<ufrn:help>Não encontrou o docente na listagem? Cadastre-o clicando na opção "Cadastrar novo Membro" ao lado.</ufrn:help>
													
													<a4j:commandButton value="Cadastrar novo Membro" id="btNovoMembro" onclick="selecionarNovo();"></a4j:commandButton>
												</a4j:region>																											
											</td>
										</tr>
										<tr id="trNome">
											<th class="required">Nome:</th>
											<td>								
												<h:inputText id="nome" value="#{bancaDefesaMBean.membro.pessoaMembroExterno.nome}" onkeyup="CAPS(this);" size="60" maxlength="80"></h:inputText>
												<ufrn:help>O docente já está cadastrado? Clique na opção "Buscar Membro" ao lado, para localizá-lo.</ufrn:help>
												<a4j:commandButton value="Buscar Membro" id="btBuscaMembro" onclick="selecionarBusca();"></a4j:commandButton>
											</td>
										</tr>
										<tr>
											<th class="required">Email:</th>
											<td>
											<h:inputText id="email" value="#{bancaDefesaMBean.membro.pessoaMembroExterno.email}" size="60" maxlength="50" />
											</td>
										</tr>
										<tr>
											<th class="required">Instituição de Ensino:</th>
											<td>
											<h:selectOneMenu value="#{bancaDefesaMBean.membro.instituicao.id}" id="instituicao" >
												<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
												<f:selectItems value="#{instituicoesEnsino.allCombo}" id="itensInstituicoesEnsino"/>
											</h:selectOneMenu>
											</td>
										</tr>
										<tr>
											<th class="required">Maior Formação:</th>
											<td>
											<h:selectOneMenu value="#{bancaDefesaMBean.membro.maiorFormacao.id}"   id="formacao">
												<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
												<f:selectItems value="#{formacao.allCombo}" id="itensFormacao"/>
											</h:selectOneMenu>
											</td>
										</tr>
										<tr>
											<th>Ano de Conclusão:</th>
											<td>
											<h:inputText id="anoConclusao" value="#{bancaDefesaMBean.membro.anoConclusao}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>
											</td>
										</tr>
										
										<tr>
											<th>Sexo:</th>
											<td><h:selectOneRadio value="#{bancaDefesaMBean.membro.pessoaMembroExterno.sexo}" id="sexo">
												<f:selectItem itemValue="M" itemLabel="Masculino" />
												<f:selectItem itemValue="F" itemLabel="Feminino" />
											</h:selectOneRadio>
											</td>
										</tr>								
									</table>	
								</h:panelGroup>																					
							</td>					
						</tr>	
						<tfoot>
							<tr>
								<td><h:commandButton id="add" value="Adicionar Membro" action="#{bancaDefesaMBean.addMembro}" />
								</td>
							</tr>
						</tfoot>			
					</table>				
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:if test="${not empty bancaDefesaMBean.obj.membrosBanca}">
						<div class="infoAltRem">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
							Remover Membro
						</div>
						<t:dataTable var="membro" styleClass="listagem" rowClasses="linhaPar,linhaImpar" 
							value="#{ bancaDefesaMBean.obj.membrosBanca }" rowIndexVar="row" id="membros" width="100%">						
							<t:column style="width:20%;">
								<f:facet name="header"><f:verbatim>Tipo do Membro</f:verbatim></f:facet>
								<h:outputText value="#{membro.tipo.descricao}"/>
							</t:column>
							<t:column>
								<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
								<h:outputText value="#{membro.membroIdentificacao}"/>
							</t:column>
							<t:column style="width:2%;">
								<h:commandLink action="#{bancaDefesaMBean.removerMembro}" title="Remover Membro" onclick="#{confirmDelete}">
									<h:graphicImage url="/img/delete.gif" />
									<f:param name="indice" value="#{row}" id="parametroIndice"/>
								</h:commandLink>	
							</t:column>
						</t:dataTable>
					</c:if>				
				</td>
			</tr>	
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="dados" value="<< Dados Gerais" immediate="true" action="#{bancaDefesaMBean.telaDadosBanca}" />
						<h:commandButton value="Cancelar" id="cancelamento"  immediate="true" onclick="#{confirm}" action="#{bancaDefesaMBean.cancelar}"  />
						<h:commandButton value="Próximo Passo >> " id="confirmar" action="#{bancaDefesaMBean.submeterMembros}" />
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


<script>

function selecionaNacionalidade(opcao) {
	var escolha = opcao == 'true';
	if (escolha) {
		$('trPassaporte').show();
		$('labelCpf').className = '';
	} else {
		$('trPassaporte').hide();
		$('labelCpf').className = 'required';
	}
}

selecionaNacionalidade((!$('nacionalidade:0').checked) + '');

function selecionarNovo(){
	
	$('trNacionalidade').show();
	$('trCpf').show();
	$('trNome').show();	
	$('trBuscaNome').hide();
	
}

function selecionarBusca() {
	
	$('trNacionalidade').hide();
	$('trCpf').hide();
	$('trPassaporte').hide();
	$('trNome').hide();	
	$('trBuscaNome').show();

	limpaDados();
}

function limpaDados(){
	$('nacionalidade:0').checked = true;
	selecionaNacionalidade('false');
	$('cpf').value = "";
	$('passaporte').value = "";
	$('nome').value = "";
	$('buscaNome').value = "";
}


if ($('cpf').value != "" || $('passaporte').value != "")
	selecionarNovo();
else
	selecionarBusca();

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

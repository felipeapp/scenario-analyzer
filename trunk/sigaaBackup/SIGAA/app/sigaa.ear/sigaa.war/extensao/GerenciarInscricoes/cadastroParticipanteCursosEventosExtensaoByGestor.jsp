<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>

<f:view>
	
	
	<a4j:keepAlive beanName="buscaPadraoParticipanteExtensaoMBean"/>
	
	<%-- Sempre que um novo caso de uso chamar a busca padrão de participantes, colocar os MBens dos casos de uso aqui. --%>
	
	<a4j:keepAlive beanName="realizaInscricaoParticipanteCoordenadorMBean"/>
	<a4j:keepAlive beanName="gerenciarInscritosCursosEEventosExtensaoMBean"/>
	
	<a4j:keepAlive beanName="gerenciarParticipantesExtensaoMBean"/>
	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean"/>


	<h2>Novo Cadastro para as atividades de Extensão</h2>
	
	<div class="descricaoOperacao">
	
		<c:if test="${! buscaPadraoParticipanteExtensaoMBean.alterando}">
			<p>Realize um novo cadastro para poder inscrever o participante em algum dos cursos ou eventos de extensão oferecidos.</p>
			<br/>
			<p><strong>Observação:</strong> Será enviado um e-mail para o usuário com o senha gerada pelo sistema para acesso ao mesmo. Após realizar 
			o <i>login</i> o participante poderá alterar a senha para uma de sua escolha.
			</p>
		</c:if>
		
		
		<c:if test="${buscaPadraoParticipanteExtensaoMBean.alterando}">
			<p>Operação que permite a alteração das informações cadastrais dos participantes de extensão.
			</p>
			<p>Ela deve ser usada principalmente para permitir acesso ao sistema dos participantes que se cadastraram ou foram cadastrados
			com e-mails inválidos, e não conseguem solicitar o reenvio de uma nova senha. Em outras situações, não há necessidade já que o próprio 
			participante pode fazer essa alteração.</p>
			<br/>
			<p><strong>Observação:</strong> Como medida de segurança, será enviado um e-mail para o usuário avisando que seus dados cadastrais foram alterados. 
			</p>
		</c:if>
		
	</div>

	<h:form id="formCadastroParticipantes" enctype="multipart/form-data">
		
		<table class="formulario" style="width: 90%;">
		<caption>Formulário de Cadastro</caption>
		
			<tbody>
			
				<c:if test="${! buscaPadraoParticipanteExtensaoMBean.alterando}">
					<tr>
						<td colspan="4" class="subFormulario">Buscar Usuários Internos</td>
					</tr>
					
					
					<%-- BUSCA NO SISTEMA PELO NOME PARA PRÉ-PREENCHER O FORMULÁRIO E FACILITAR SEU PREENCHIMENTO  --%>
					<tr>
						<td colspan="4" style="text-align: center;">
						
							<h:inputHidden id="idPessoa"   value="#{buscaPadraoParticipanteExtensaoMBean.idpessoaBusca}"/>
							Nome: <h:inputText   id="nomePessoa" value="#{buscaPadraoParticipanteExtensaoMBean.nomePessoaBusca}" size="64" onkeyup="CAPS(this);" />
							<rich:suggestionbox id="suggestionNomePessoa"
									for="nomePessoa"
									var="_pessoa" fetchValue="#{ _pessoa.nome }"
									suggestionAction="#{buscaPadraoParticipanteExtensaoMBean.autocompleteNomePessoa}"
									width="400" height="100" minChars="5">
								
								<h:column>
									<h:outputText value="#{ _pessoa.nome }"/> ( <h:outputText value="#{_pessoa.cpf_cnpj }" /> )
								</h:column>
								
								<a4j:support event="onselect" reRender="formCadastroParticipantes"
										oncomplete="document.getElementById('formCadastroParticipantes:cmdPreencharInformacoes').click();">
									<f:setPropertyActionListener value="#{ _pessoa.id }" target="#{buscaPadraoParticipanteExtensaoMBean.idpessoaBusca}"/>
								</a4j:support>
								
							</rich:suggestionbox>
							
							<span id="indicador" style="display:none; "> 
								<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
							</span>
							
							<a4j:commandButton  id="cmdPreencharInformacoes" value="PreencharInformacoes" style="display:none;" reRender="formCadastroParticipantes" 
										actionListener="#{buscaPadraoParticipanteExtensaoMBean.preencheCadastroInformacoesPessoaSelecionada}">
							</a4j:commandButton>
								
							
						</td>
					</tr>
				</c:if>
				
				<%--   O PREENCIMENTO DOS DADOS PESSOAIS COMEÇA AQUI    --%>
			
				<tr>
					<td colspan="4" class="subFormulario">Dados Pessoais
					
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					
					</td>
				</tr>
				
				<tr>	
					<th width="20%">Estrangeiro: </th>
					<td colspan="3">
						<h:selectBooleanCheckbox immediate="true" id="checkEstrangeiro" value="#{buscaPadraoParticipanteExtensaoMBean.obj.estrangeiro}" >
							<a4j:support id="suportCheck" event="onclick" reRender="divCPF, divPassaporte"/>
						</h:selectBooleanCheckbox>
					</td>				
				</tr>
				
				
				<tr>
					<td colspan="4">
						<t:div id="divCPF">
							<table style="width:100%;">
								<tr>
								<th ${ buscaPadraoParticipanteExtensaoMBean.obj.estrangeiro ? '' : 'class="obrigatorio"' } style="width:18%;"> CPF:</th>
								<td colspan="3" >
									<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.cpf}" size="14" id="cpf" maxlength="14" onkeypress="formataCPF(this, event, null);" >
										<f:converter converterId="convertCpf" />
										<f:param id="paraCpf" name ="type" value="cpf" />
									</h:inputText>
								</td>
							</tr>
						</table>
					</t:div>
					</td>
				</tr>
				
				<tr>
					<td colspan="4" >
					<t:div id="divPassaporte">
						<table style="width:100%;">
							<tr>
								<th ${ buscaPadraoParticipanteExtensaoMBean.obj.estrangeiro ? 'class="obrigatorio"' : '' } style="width:18%;">Passaporte:</th>
								<td colspan="3">
								<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.passaporte}" maxlength="20" size="20" id="passaporte" />
								&nbsp;&nbsp;
								</td>
							</tr>
						</table>
					</t:div>
					</td>
				</tr>
				
				<tr>
					<th class="required">Nome Completo:</th>
					<td colspan="3">
						<h:inputText id="inputTextNome" value="#{buscaPadraoParticipanteExtensaoMBean.obj.nome}" size="70" maxlength="100" />
					</td>
				</tr>
				
				<tr>
					<th class="required">Data de Nascimento:</th>
					<td colspan="3">
						<t:inputCalendar id="dataNascimento" value="#{buscaPadraoParticipanteExtensaoMBean.obj.dataNascimento}" 
								renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
								size="10" onkeypress="formatarMascara(this,event,'##/##/####')" maxlength="10">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
						<span style="color: gray">Ex.:(dd/mm/aaaa)</span>
					</td>
				</tr>
				
				<tr>
					<td colspan="4">&nbsp;</td>
				</tr>
			
			
				<tr>
					<td colspan="4" class="subFormulario">Endereço</td>
				</tr>
				<tr class="linhaCep">
					<th>CEP:</th>
					<td colspan="3">
						<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.cep}" 
								maxlength="10" size="10" id="cep" 
								onkeyup="return formatarInteiro(this);"
								onblur="formataCEP(this, event, null); ConsultadorCep.consultar();" />
								
								<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
									<img src="/sigaa/img/buscar.gif" alt="Buscar Endereço" />
								</a> 
						<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span> 
						<span id="cepIndicator" style="display: none;"><img src="/sigaa/img/indicator.gif" /> Buscando endereço... </span>			
					</td>
				</tr>
				<tr>
					<th style="width: 10%;">Rua/Av.:</th>
					<td style="width: 40%;">
						<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.logradouro}" id="logradouro" size="60" maxlength="60" />
					</td>
					<th width="10%">Número:</th>
					<td style="width: 40%;">
						<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.numero}" id="numero" maxlength="6" size="7"  onkeyup="formatarInteiro(this);" />
					</td>
				</tr>
				<tr>
					<th>Bairro:</th>
					<td colspan="3">
						<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.bairro}" id="bairro" size="60" maxlength="50" />
					</td>
				</tr>
				
				<tr>
					<th>Complemento:</th>
					<td colspan="3">
						<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.complemento}" id="complemento" size="100" maxlength="200" />
					</td>
				</tr>
				
				<tr>
					<th>UF:</th>
					<td><h:selectOneMenu value="#{buscaPadraoParticipanteExtensaoMBean.obj.unidadeFederativa.id}" id="uf">
							<f:selectItems value="#{buscaPadraoParticipanteExtensaoMBean.unidadesFederativasCombo}" />
							<a4j:support event="onchange" reRender="municipio" actionListener="#{buscaPadraoParticipanteExtensaoMBean.carregarMunicipios}"/>
						</h:selectOneMenu>
					</td>
					<th>Município:</th>
					<td>
						<h:selectOneMenu value="#{buscaPadraoParticipanteExtensaoMBean.obj.municipio.id}" id="municipio">
							<f:selectItems value="#{buscaPadraoParticipanteExtensaoMBean.municipiosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				
				<tr>
					<td colspan="4">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="4" class="subFormulario">Contato</td>
				</tr>
				
				<tr>
					<th>Telefone Fixo:</th>
					<td><h:inputText
						value="#{buscaPadraoParticipanteExtensaoMBean.obj.telefone}"
						onkeyup="formatarTelefone(this, event );" 
						maxlength="15" size="20" id="txtTelefone"  />
						<i>(xx) xxxx-xxxx</i>
					</td> 
					
					<th>Celular:</th>
					<td><h:inputText
						value="#{buscaPadraoParticipanteExtensaoMBean.obj.celular}"
						onkeyup="formatarTelefone(this, event);"
						maxlength="15" size="20" id="txtCelular" /> <i>(xx) xxxx-xxxx</i>
					</td>
				</tr>
				
				
				<tr>
					<td colspan="4">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="4" class="subFormulario">Autenticação</td>
				</tr>
				
				<tr>
					<td colspan="4">
						<c:if test="${! buscaPadraoParticipanteExtensaoMBean.alterando}">
							<table style="width: 100%;">
								
								<tr>
									<th class="required" style="width: 20%;">E-mail:</th>
									<td colspan="2">
										<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.email}" id="email" size="60" maxlength="40" />
									</td>
									<td>
										<span id="spanMensagemEmail" style="color: red;"> </span>
									</td>
								</tr>
								
								<tr>
									<th class="required">Confirmação de E-mail:</th>
									<td colspan="3">
										<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.emailConfirmacao}" id="emailConfirmacao" size="60" maxlength="40" 
											onkeyup="verificaEmailIgual();"/>
									</td>
								</tr>
								
								<tr>
									<th class="required">Senha:</th>
											<td colspan="2" style="color: blue; font-style: italic; font-weight: bold;">
												<h:outputText value="Gerada Pelo Sistema" id="senha" />
											</td>
									<td>
										<span id="spanMensagemSenha"> </span>
									</td>
								</tr>
								
							</table>
						</c:if>
						
						<c:if test="${buscaPadraoParticipanteExtensaoMBean.alterando}">
							<table style="width: 100%;">
							<tr>
								<th class="required" style="width: 20%;">E-mail:</th>
								<td colspan="2">
									<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.obj.email}" id="email" size="60" maxlength="40" />
								</td>
								<td>
									<span id="spanMensagemEmail" style="color: red;"> </span>
								</td>
							</tr>
							
							<c:if test="${buscaPadraoParticipanteExtensaoMBean.permiteRedefinirSenha}">
								<tr>
									<th style="width: 20%;">Redefinir a Senha do Participante:</th>
									<td colspan="2" style="width: 40%;">
										<h:inputSecret value="#{buscaPadraoParticipanteExtensaoMBean.obj.senha}" id="senha" size="25" maxlength="20" />
									</td>
									<td style="width: 40%;">
										<ufrn:help>Deixe esse campo em branco para o participante continuar usando a senha atual.</ufrn:help>
										<span id="spanMensagemSenha"> </span>
									</td>
								</tr>
							</c:if>
							
						</table>
						</c:if>
						
					</td>
				</tr>
				
				<tr>
					<td colspan="4">&nbsp;</td>
				</tr>
				
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton id="cmdButtonCadastrar" value="Cadastrar" action="#{buscaPadraoParticipanteExtensaoMBean.cadastrarNovoParticipante}" 
								onclick="return confirm('Confirma o cadastro no sistema ? ');" rendered="#{! buscaPadraoParticipanteExtensaoMBean.alterando}"  />
						
						<h:commandButton id="cmdButtonAlterar" value="Alterar" action="#{buscaPadraoParticipanteExtensaoMBean.alterarCadastroParticipante}" 
								onclick="return confirm('Confirma alteração do cadastro no sistema ? ');"  rendered="#{buscaPadraoParticipanteExtensaoMBean.alterando}" />
								
						<h:commandButton id="cmdButtonCancelar" value="Cancelar" action="#{buscaPadraoParticipanteExtensaoMBean.telaBuscaPadraoParticipantesExtensao}" 
								immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena">Campos de preenchimento obrigatório.</span> <br /><br />
		</center>
	
	</h:form>

	
</f:view>



<script type="text/javascript">



function selecionaParticipante(valor) {

	var divServidor = document.getElementById("servidor") ;
	var divDiscente = document.getElementById("discente") ;

	if (valor == 1){
		divServidor.style.display = "none";
		divDiscente.style.display = "";
	}
	if (valor == 2){
		divServidor.style.display = "";
		divDiscente.style.display = "none";
	}
}

selecionaParticipante(1);


ConsultadorCep.init('/sigaa/consultaCep','formCadastroParticipantes:cep','formCadastroParticipantes:logradouro','formCadastroParticipantes:bairro','formCadastroParticipantes:municipio','formCadastroParticipantes:uf',posProcessamentoCep);


var posProcessamentoCep = function() {
	$('formCadastroParticipantes:municipio').value = $('formCadastroParticipantes:municipio').options[0].value;
	$('formCadastroParticipantes:uf').onchange();
}

/* Verifica se os e-emails digitados pelo usuário são iguais. */
function verificaEmailIgual(){
	
	email = document.getElementById('formCadastroParticipantes:email');
	emailConfirmacao = document.getElementById('formCadastroParticipantes:emailConfirmacao');
	
	if(emailConfirmacao.value == email.value){
		document.getElementById('spanMensagemEmail').innerHTML = '<span style="color: green;"> E-emais conferem! </span> ';
	}else{
		document.getElementById('spanMensagemEmail').innerHTML = '<span style="color: red;"> E-emais não conferem! </span>';
	}
}

/* Verifica se as senhas digitados pelo usuário são iguais. */
function verificaSenhaIgual(){
	senha = document.getElementById('formCadastroParticipantes:senha');
	senhaConfirmacao = document.getElementById('formCadastroParticipantes:senhaConfirmacao');
	
	if(senhaConfirmacao.value == senha.value){
		document.getElementById('spanMensagemSenha').innerHTML = '<span style="color: green;"> Senhas conferem! </span> ';
	}else{
		document.getElementById('spanMensagemSenha').innerHTML = '<span style="color: red;"> Senhas não conferem! </span>';
	}
}


/** Formata o telefone no formato (84) 8888-8888 ou (84) 98888-8888 
 *  Deve ser chamada na função onkeyup
 */
function formatarTelefone(campo, event)	{
	
	var tecla = (event!=null)? event.keyCode: 0;
	
	if( tecla == 8 )
		return true;
	
    var rExp = /[^\0-\9]|\./g;
    
    var vr = campo.value.replace(rExp, "")
    if( vr.length > 15 ){
    	return false;
    }

    if(vr.length == 1)   // quando tiver tamanho 1 coloca o abre parenteses ( 
    	vr = '(' + vr;

    if(vr.length == 3)   // quando chegar em 9 coloca o fecha parenteses (84) 
    	  vr = vr + ') ';

    if(vr.length == 9) // quando chegar em 9 coloca um traço (84) 8888-
    	vr = vr + '-';
    
    // se o cara digitou o 9 dígio e não tá com o traço no lugar correta ainda, formate
    if(vr.length == 15 && vr.substring(10, 11) != '-'){ // quando chegar em 15 (novo formato de 9 dígitos) coloca pula o traço para 1 dígito depois (84) 8888-88888 -> (84) 88888-8888
    	inicio = vr.substring(0, 9);
    	fim = vr.substring(10, 15);
    	vr = inicio + fim.substring(0, 1) + '-' + fim.substring(1, 6);
    }
    
     campo.value = vr;

}

</script>	

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
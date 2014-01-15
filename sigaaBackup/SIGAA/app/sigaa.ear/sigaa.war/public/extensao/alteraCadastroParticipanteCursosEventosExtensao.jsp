
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
	
	<h2>Cadastro nos Cursos e Eventos de Extensão</h2>
	
	<div class="descricaoOperacao">
		<p>Altere os seus dados registrados no sistema.</p>
		<br/>
	</div>



	<h:form id="formCadastroParticipantes" enctype="multipart/form-data">
		
		<table class="formulario" style="width: 90%;">
		<caption>Formulário de Cadastro</caption>
		
			<tbody>
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
						<h:selectBooleanCheckbox immediate="true" id="checkEstrangeiro" value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro}" >
							<a4j:support id="suportCheck" event="onclick" reRender="divCPF, divPassaporte"/>
						</h:selectBooleanCheckbox>
					</td>				
				</tr>
				
				
				<tr>
					<td colspan="4" >
					<t:div id="divCPF">
						<table style="width:100%;">
							<tr>
								<th ${ cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro ? '' : 'class="obrigatorio"' } style="width:16%;"> CPF:</th>
								<td colspan="3" >
									<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.cpf}" size="14" id="cpf" maxlength="14" onkeypress="formataCPF(this, event, null)">
										<f:converter converterId="convertCpf" />
										<f:param id="paraCpf" name ="type" value="cpf" />
									</h:inputText>
									
									<c:if test="${ cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro}">
										<ufrn:help>É importante o preenchimento desse campo para possibilitar o eventual pagamento de taxas de inscrição.</ufrn:help>
									</c:if>
									
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
							<th ${ cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro ? 'class="obrigatorio"' : '' } style="width:16%;">Passaporte:</th>
							<td colspan="3">
								<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.passaporte}" maxlength="20" size="20" id="passaporte" />
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
						<h:inputText id="inputTextNome" value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.nome}" size="70" maxlength="100" />
					</td>
				</tr>
				
				<tr>
					<th class="required">Data de Nascimento:</th>
					<td colspan="3">
						<t:inputCalendar id="dataNascimento" value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.dataNascimento}" 
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
					<th class="required">CEP:</th>
					<td colspan="3">
						<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.cep}" 
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
					<th class="required" style="width: 10%;">Rua/Av.:</th>
					<td style="width: 40%;">
						<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.logradouro}" id="logradouro" size="60" maxlength="60" />
					</td>
					<th class="required" width="10%">Número:</th>
					<td style="width: 40%;">
						<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.numero}" id="numero" maxlength="6" size="7"  onkeyup="formatarInteiro(this);" />
					</td>
				</tr>
				<tr>
					<th class="required">Bairro:</th>
					<td colspan="3">
						<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.bairro}" id="bairro" size="60" maxlength="50" />
					</td>
				</tr>
				
				<tr>
					<th>Complemento:</th>
					<td colspan="3">
						<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.complemento}" id="complemento" size="100" maxlength="200" />
					</td>
				</tr>
				
				<tr>
					<th class="required">UF:</th>
					<td><h:selectOneMenu value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.unidadeFederativa.id}" id="uf">
							<f:selectItems value="#{cadastroParticipanteAtividadeExtensaoMBean.unidadesFederativasCombo}" />
							<a4j:support event="onchange" reRender="municipio" actionListener="#{cadastroParticipanteAtividadeExtensaoMBean.carregarMunicipios}"/>
						</h:selectOneMenu>
					</td>
					<th class="required">Município:</th>
					<td>
						<h:selectOneMenu value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.municipio.id}" id="municipio">
							<f:selectItems value="#{cadastroParticipanteAtividadeExtensaoMBean.municipiosCombo}" />
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
						value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.telefone}"
						onkeyup="formatarTelefone(this, event );" 
						maxlength="15" size="20" id="txtTelefone"  />
						<i>(xx) xxxx-xxxx</i>
					</td> 
					
					<th>Celular:</th>
					<td><h:inputText
						value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.celular}"
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
						<table style="width: 100%;">
							<tr>
								<th class="required" style="width: 20%;">E-mail:</th>
								<td colspan="2">
									<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.email}" id="email" size="60" maxlength="40" />
								</td>
								<td>
									<span id="spanMensagemEmail" style="color: red;"> </span>
								</td>
							</tr>
							
							<tr>
								<th class="required">Confirmação de E-mail:</th>
								<td colspan="3">
									<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.emailConfirmacao}" id="emailConfirmacao" size="60" maxlength="40" 
										onkeyup="verificaEmailIgual();"/>
								</td>
							</tr>
							
							<tr>
								<th class="required">Senha:</th>
								<td colspan="2">
									<h:inputSecret value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.senha}" id="senha" size="25" maxlength="20" />
								</td>
								<td>
									<span id="spanMensagemSenha"> </span>
								</td>
							</tr>
							
							<tr>
								<th class="required">Confirmação de Senha:</th>
								<td colspan="3">
									<h:inputSecret value="#{cadastroParticipanteAtividadeExtensaoMBean.senhaConfirmacao}" id="senhaConfirmacao" size="25" maxlength="20" 
										onkeyup="verificaSenhaIgual();"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr>
					<td colspan="4">&nbsp;</td>
				</tr>
				
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton id="cmdButtonCadastrar" value="Alterar" action="#{cadastroParticipanteAtividadeExtensaoMBean.alteraDadosParticipante}" 
								onclick="return confirm('Confirma a alteração dos seus dados no sistema ? ');" />
						
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena">Campos de preenchimento obrigatório.</span> <br /><br />
		</center>
	
	</h:form>
	
	
	
<script type="text/javascript">


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
		document.getElementById('spanMensagemEmail').innerHTML = '<span style="color: green;"> E-mails conferem! </span> ';
	}else{
		document.getElementById('spanMensagemEmail').innerHTML = '<span style="color: red;"> E-mails não conferem! </span>';
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
	
	
<%@include file="/public/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>

<style type="text/css">
	strong{font-weight: bold;}
</style>

<f:view>
	
	<a4j:keepAlive beanName="cadastroParticipanteAtividadeExtensaoMBean" />
	
	<h2>Cadastro nos Cursos e Eventos de Extensão</h2>
	
	<div class="descricaoOperacao">
		<p>Essa opção permite o reenvio da senha de acesso à área privada dos cursos e eventos de extensão.</p>
		<br/>
		<p><strong>Observação : </strong> Ao solicitar o reenvio da senha, será gerada e enviada <strong>uma nova senha</strong> de acesso para o e-email 
		informado, caso esse esteja cadastrado na nossa base.
		<br/>
		</p>
		<p>
		Ao realizar o <i>login</i>, será possível alterar a senha gerada pelo sistema por uma de sua escolha.
		</p>
	</div>

	<h:form id="formReenviaSenha" enctype="multipart/form-data">
		
		
		<table class="formulario" style="width: 70%;">
			<caption>Dados para Reenvio da Senha de Acesso</caption>
		
			<tbody>
				
				<tr>
					<td colspan="4">
						<table style="width: 100%;">
							<tr>
								<th class="required" style="width: 30%;">E-mail:</th>
								<td colspan="3">
									<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.email}" id="email" size="60" maxlength="40" />
								</td>
							</tr>
							
						</table>
					</td>
				</tr>
				
				<tr>
					<th style="width: 30%;" class="required">Data de Nascimento:</th>
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
					<th>Estrangeiro: </th>
					<td colspan="3">
						<h:selectBooleanCheckbox immediate="true" id="checkEstrangeiro" value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro}" >
							<a4j:support id="suportCheck" event="onclick" reRender="formReenviaSenha"/>
						</h:selectBooleanCheckbox>
					</td>				
				</tr>
				
				
				<tr>	
					<td colspan="4">
						<t:div rendered="#{! cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro}">
							<table style="width: 100%;"> 
								<tr>
									<th style="width: 30%;" ${ cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro ? '' : 'class="obrigatorio"' }> CPF:</th>
									<td colspan="3" >
										<h:inputText value="#{cadastroParticipanteAtividadeExtensaoMBean.obj.cpf}" size="14" id="cpf" maxlength="14" onkeypress="formataCPF(this, event, null)">
											<f:converter converterId="convertCpf" />
											<f:param id="paraCpf" name ="type" value="cpf" />
										</h:inputText>
									</td>
								</tr>
							</table>
						</t:div>
						
						<t:div rendered="#{cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro}">
							<table style="width: 100%;"> 
								<tr>
									<th style="width: 30%;" ${ cadastroParticipanteAtividadeExtensaoMBean.obj.estrangeiro ? 'class="obrigatorio"' : '' }>Passaporte:</th>
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
					<td colspan="4">&nbsp;</td>
				</tr>
				
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton id="cmdButtonCadastrar" value="Reenviar Senha" action="#{cadastroParticipanteAtividadeExtensaoMBean.reenviarSenhaParticipante}" 
								onclick="return confirm('Confirma o reenvio da senha para o e-mail informado ? ');" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena">Campos de preenchimento obrigatório.</span> <br /><br />
		</center>
		
		<br />
		<div style="margin: 0pt auto; width: 80%; text-align: center;">
			<h:commandLink action="#{logonCursosEventosExtensaoMBean.telaLoginCursosEventosExtensao}" immediate="true"> &lt;&lt; Voltar </h:commandLink>
		</div>
		<br />
	
	</h:form>
	
</f:view>





<%@include file="/public/include/rodape.jsp" %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="usuarioExternoBibliotecaMBean" />

	<h2><ufrn:subSistema /> &gt; Usu�rio Externo da Biblioteca</h2>

	<h:form>
	
	<div class="descricaoOperacao">
		<p>Utilize este formul�rio para cadastrar um v�nculo de usu�rio externo no sistema de bibliotecas para a pessoa selecionada. </p> 
		<p> Se ela j� possuir cadastro como usu�rio externo, as informa��es poder�o ser alteradas.</p>
	</div>
	
	
		<table class="formulario" width="80%">
	
			<caption>Cadastro de Usu�rio Externo da Biblioteca</caption>
				
				<c:if test="${usuarioExternoBibliotecaMBean.obj.usuarioBiblioteca.pessoa.internacional}">
					<tr><th style="font-weight: bold;">Passaporte:</th><td>${usuarioExternoBibliotecaMBean.obj.usuarioBiblioteca.pessoa.passaporte}</td></tr>
				</c:if>
				<c:if test="${! usuarioExternoBibliotecaMBean.obj.usuarioBiblioteca.pessoa.internacional}">
					<tr><th style="font-weight: bold;">CPF:</th><td>${usuarioExternoBibliotecaMBean.obj.usuarioBiblioteca.pessoa.cpf_cnpj}</td></tr>
				</c:if>
				
				<tr><th style="font-weight: bold;">Nome:</th><td>${usuarioExternoBibliotecaMBean.obj.usuarioBiblioteca.pessoa.nome}</td></tr>

				<tr>
					<th class="obrigatorio">Final do Prazo:</th>
					<td>
						<t:inputCalendar id="Prazo" value="#{usuarioExternoBibliotecaMBean.obj.prazoVinculo}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
						<ufrn:help>Ap�s esta data, o usu�rio externo da biblioteca n�o poder� mais realizar empr�stimos.</ufrn:help>
					</td>
				</tr>

				<tr><th>Documento:</th><td><h:inputText value="#{usuarioExternoBibliotecaMBean.obj.documento}" maxlength="15" /></td></tr>

				<tr>
					<th>Unidade:</th>
					<td>
						<h:selectOneMenu value="#{usuarioExternoBibliotecaMBean.obj.unidade.id}" style="width:400px;">
							<f:selectItem itemValue="-2" itemLabel=" -- SELECIONE -- " />
							<f:selectItems value="#{unidade.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th>Conv�nio Acad�mico:</th>
					<td>
						<h:selectOneMenu value="#{usuarioExternoBibliotecaMBean.obj.convenio.id}" style="width:400px;">
							<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- " />
							<f:selectItems value="#{convenioBiblioteca.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				
				<%-- Est� sendo criado o usu�rio biblioteca agora, ent�o precisa definir a senha dos empr�stimos --%>
				
				<tr>
					<th ${usuarioExternoBibliotecaMBean.obj.usuarioBiblioteca.id <= 0 ? "class='obrigatorio'" : ""}>Senha:</th>
					<td>
						<h:inputSecret value="#{usuarioExternoBibliotecaMBean.senha}" onkeyup="return formatarInteiro(this);" maxlength="8" />
						<ufrn:help>A senha deve conter de seis a oito d�gitos.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th ${usuarioExternoBibliotecaMBean.obj.usuarioBiblioteca.id <= 0 ? "class='obrigatorio'" : ""}>Confirma��o da Senha:</th>
					<td><h:inputSecret value="#{usuarioExternoBibliotecaMBean.confirmarSenha}" onkeyup="return formatarInteiro(this);" maxlength="8" /></td>
				</tr>
				
				
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{usuarioExternoBibliotecaMBean.cadastrar}" value="#{usuarioExternoBibliotecaMBean.confirmButton}" />
							<h:commandButton value="<< Voltar" action="#{usuarioExternoBibliotecaMBean.voltarPagina}" />
							<h:commandButton action="#{usuarioExternoBibliotecaMBean.cancelar}" value="Cancelar" onclick="#{confirm}" />
						</td>
				</tfoot>
		</table>
		
		<c:set var="exibirApenasSenha" value="true" scope="request"/>
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
		
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
		
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
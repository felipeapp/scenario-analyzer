<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>Cadastro de Discente</h2>

<f:view>
<a4j:keepAlive beanName="cadastroDiscente"></a4j:keepAlive>

	<h:form id="form">
		<p>Para cadastrar-se no SIGAA é necessário preencher o formulário
		abaixo. O cadastro só será validado se os dados digitados forem <em>iguais
		aos dados informados no processo seletivo</em>.</p>
		<br />

		<table class="formulario">
			<caption>Dados do Discente</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Matrícula:</th>
					<td><h:inputText id="matricula" 
						value="#{ cadastroDiscente.usuario.discente.matricula }" maxlength="15" size="15" onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nível:</th>
					<td>
						<h:selectOneMenu value="#{cadastroDiscente.usuario.discente.nivel}" id="nivel"> 
							<f:selectItems value="#{cadastroDiscente.niveisEnsinoCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th></th>
					<td>
						<h:selectBooleanCheckbox id="internacional" value="#{cadastroDiscente.internacional}" onclick="submit();" immediate="true" />
						A pessoa é estrangeira e não possui CPF
					</td>
				</tr>				
				<tr>
					<th class="obrigatorio">
						<h:outputText rendered="#{!cadastroDiscente.internacional}">CPF:</h:outputText> 
						<h:outputText rendered="#{cadastroDiscente.internacional}">Passaporte:</h:outputText>
					</th>
					<td>			
						<h:inputText value="#{ cadastroDiscente.usuario.pessoa.passaporte }" maxlength="20" size="20" id="passaporte" rendered="#{cadastroDiscente.internacional}" />
						<h:inputText value="#{ cadastroDiscente.usuario.pessoa.cpf_cnpj }" size="14" maxlength="14" onkeypress="return formataCPF(this, event, null);" id="cpf" rendered="#{! cadastroDiscente.internacional}">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
					</td>
				</tr>						
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td><h:inputText id="nome"
						value="#{ cadastroDiscente.usuario.pessoa.nome }" size="50" /></td>
				</tr>

				<c:if test="${not cadastroDiscente.internacional}">
					<tr>
						<th class="obrigatorio">RG:</th>
						<td><h:inputText id="rg"
							value="#{ cadastroDiscente.usuario.pessoa.identidade.numero }"
							size="15" maxlength="15" onkeyup="return formatarInteiro(this);"/> (Digite apenas os números)</td>
					</tr>
				</c:if>
				<tr>
					<th class="obrigatorio">Data de Nascimento:</th>
					<td><t:inputCalendar id="nascimento"
						value="#{ cadastroDiscente.usuario.pessoa.dataNascimento}"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="12"
						maxlength="10" onkeypress="return(formataData(this, event))">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</t:inputCalendar></td>
				</tr>
				<tr>
					<th class="obrigatorio">E-Mail:</th>
					<td><h:inputText id="email" value="#{ cadastroDiscente.usuario.email }" size="45" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano/Semestre Inicial</th>
					<td><h:inputText id="ano"
						value="#{ cadastroDiscente.usuario.discente.anoIngresso }"
						size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> - <h:inputText id="semestre"
						value="#{ cadastroDiscente.usuario.discente.periodoIngresso }"
						size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/> (Ex.: 2006-2)</td>
				</tr>

				<tr>
					<th class="obrigatorio">Login:</th>
					<td><h:inputText value="#{ cadastroDiscente.usuario.login }" id="login"
						size="15" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Senha:</th>
					<td><h:inputSecret value="#{ cadastroDiscente.usuario.senha }" id="senha"
						size="15" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Confirmar Senha:</th>
					<td><h:inputSecret id="confirmSenha"
						value="#{ cadastroDiscente.usuario.confirmaSenha }" size="15" /></td>
				</tr>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Cadastrar"
						action="#{ cadastroDiscente.cadastrar }" /> 
						<h:commandButton id="cancelar" action="#{cadastroDiscente.cancelar}" value="Cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>

		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

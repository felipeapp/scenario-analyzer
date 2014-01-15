<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>Cadastro do Familiar</h2>

<f:view>
<a4j:keepAlive beanName="cadastroFamiliar"></a4j:keepAlive>

	<h:form id="form">
		<p>Para cadastrar-se no SIGAA é necessário preencher o formulário
		abaixo. O cadastro só será validado se os dados digitados forem <em>iguais
		aos dados informados no processo seletivo</em>.</p>
		<br />

		<table class="formulario">
			<caption>Dados para Cadastro</caption>
			<tbody>
				<tr>
					<td class="subFormulario" colspan="2">Dados do Aluno</td>
				</tr>
			
				<tr>
					<th class="obrigatorio">Matrícula:</th>
					<td><h:inputText id="matricula" 
						value="#{ cadastroFamiliar.discente.discente.matricula }" maxlength="15" size="15" onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>
				<tr>
					<th><b>Nível:</b></th>
					<td>MÉDIO</td>
				</tr>
				<tr>
					<th></th>
					<td>
						<h:selectBooleanCheckbox id="internacional" value="#{cadastroFamiliar.internacionalDiscente}" onclick="submit();" immediate="true" />
						<label for="form:internacional">O Aluno é estrangeiro e não possui CPF</label>
					</td>
				</tr>				
				<tr>
					<th class="obrigatorio">
						<h:outputText rendered="#{!cadastroFamiliar.internacionalDiscente}">CPF:</h:outputText> 
						<h:outputText rendered="#{cadastroFamiliar.internacionalDiscente}">Passaporte:</h:outputText>
					</th>
					<td>			
						<h:inputText value="#{ cadastroFamiliar.discente.pessoa.passaporte }" maxlength="20" size="20" id="passaporte" rendered="#{cadastroFamiliar.internacionalDiscente}" />
						<h:inputText value="#{ cadastroFamiliar.discente.pessoa.cpf_cnpj }" size="14" maxlength="14" onkeypress="return formataCPF(this, event, null);" id="cpf" rendered="#{! cadastroFamiliar.internacionalDiscente}">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
					</td>
				</tr>						
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td><h:inputText id="nome"
						value="#{ cadastroFamiliar.discente.pessoa.nome }" size="50" /></td>
				</tr>

				<c:if test="${not cadastroFamiliar.internacionalDiscente}">
					<tr>
						<th class="obrigatorio">RG:</th>
						<td><h:inputText id="rg"
							value="#{ cadastroFamiliar.discente.pessoa.identidade.numero }"
							size="15" maxlength="15" onkeyup="return formatarInteiro(this);"/> (Digite apenas os números)</td>
					</tr>
				</c:if>
				<tr>
					<th class="obrigatorio">Data de Nascimento:</th>
					<td><t:inputCalendar id="nascimento"
						value="#{ cadastroFamiliar.discente.pessoa.dataNascimento}"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="12"
						maxlength="10" onkeypress="return(formataData(this, event))">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</t:inputCalendar></td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano Inicial</th>
					<td><h:inputText id="ano"
						value="#{ cadastroFamiliar.discente.discente.anoIngresso }"
						size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>
				</tr>

				<tr>
					<td class="subFormulario" colspan="2">Dados do Familiar (Pai, Mãe, Avô, Avó...)</td>
				</tr>

				<tr>
					<th class="obrigatorio">Nome:</th>
					<td><h:inputText id="nomeFamiliar"
						value="#{ cadastroFamiliar.obj.pessoa.nome }" size="50" /></td>
				</tr>
				<tr>
					<th></th>
					<td>
						<h:selectBooleanCheckbox id="internacionalFamiliar" value="#{cadastroFamiliar.internacionalPai}" onclick="submit();" immediate="true" />
						<label for="form:internacionalFamiliar">O Familiar é estrangeiro e não possui CPF</label>
					</td>
				</tr>				
				<tr>
					<th class="obrigatorio">
						<h:outputText rendered="#{!cadastroFamiliar.internacionalPai}">CPF:</h:outputText> 
						<h:outputText rendered="#{cadastroFamiliar.internacionalPai}">Passaporte:</h:outputText>
					</th>
					<td>			
						<h:inputText value="#{ cadastroFamiliar.obj.pessoa.passaporte }" maxlength="20" size="20" id="passaporteFamiliar" rendered="#{cadastroFamiliar.internacionalPai}" />
						<h:inputText value="#{ cadastroFamiliar.obj.pessoa.cpf_cnpj }" size="14" maxlength="14" onkeypress="return formataCPF(this, event, null);" id="cpfFamiliar" rendered="#{! cadastroFamiliar.internacionalPai}">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
					</td>
				</tr>				
				<tr>
					<th class="obrigatorio">E-Mail:</th>
					<td><h:inputText id="email" value="#{ cadastroFamiliar.obj.email }" size="45" /></td>
				</tr>				
				<tr>
					<th class="obrigatorio">Login:</th>
					<td><h:inputText value="#{ cadastroFamiliar.obj.login }" id="login"
						size="15" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Senha:</th>
					<td><h:inputSecret value="#{ cadastroFamiliar.obj.senha }" id="senha"
						size="15" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Confirmar Senha:</th>
					<td><h:inputSecret id="confirmSenha"
						value="#{ cadastroFamiliar.obj.confirmaSenha }" size="15" /></td>
				</tr>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Cadastrar"
						action="#{ cadastroFamiliar.cadastrar }" /> 
						<h:commandButton id="cancelar" action="#{cadastroFamiliar.cancelar}" value="Cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>

		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

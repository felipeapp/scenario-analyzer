<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Vincular Aluno </h2>
	<h:form id="form">
		<div class="descricaoOperacao">
			<p>Para vincular um discente ao seu usuário é necessário preencher o formulário
			abaixo. O cadastro só será validado se os dados digitados forem <em>iguais
			aos dados informados no processo seletivo</em>.</p>
		</div>

		<table class="formulario">
			<caption>Dados para Cadastro</caption>
			<tbody>
				<tr>
					<td class="subFormulario" colspan="2">Dados do Aluno</td>
				</tr>
			
				<tr>
					<th class="obrigatorio">Matrícula:</th>
					<td><h:inputText id="matricula" 
						value="#{ portalFamiliar.obj.discenteMedio.discente.matricula }" maxlength="15" size="15" onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>
				<tr>
					<th></th>
					<td>
						<h:selectBooleanCheckbox id="internacional" value="#{portalFamiliar.internacional}" onclick="submit();" immediate="true" />
						<label for="form:internacional">O Aluno é estrangeiro e não possui CPF</label>
					</td>
				</tr>				
				<tr>
					<th class="obrigatorio">
						<h:outputText rendered="#{!portalFamiliar.internacional}">CPF:</h:outputText> 
						<h:outputText rendered="#{portalFamiliar.internacional}">Passaporte:</h:outputText>
					</th>
					<td>			
						<h:inputText value="#{ portalFamiliar.obj.discenteMedio.pessoa.passaporte }" maxlength="20" size="20" id="passaporte" rendered="#{portalFamiliar.internacional}" />
						<h:inputText value="#{ portalFamiliar.obj.discenteMedio.pessoa.cpf_cnpj }" size="14" maxlength="14" onkeypress="return formataCPF(this, event, null);" id="cpf" rendered="#{! portalFamiliar.internacional}">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
					</td>
				</tr>						
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td><h:inputText id="nome"
						value="#{ portalFamiliar.obj.discenteMedio.pessoa.nome }" size="50" /></td>
				</tr>

				<c:if test="${not portalFamiliar.internacional}">
					<tr>
						<th class="obrigatorio">RG:</th>
						<td><h:inputText id="rg"
							value="#{ portalFamiliar.obj.discenteMedio.pessoa.identidade.numero }"
							size="15" maxlength="15" onkeyup="return formatarInteiro(this);"/> (Digite apenas os números)</td>
					</tr>
				</c:if>
				<tr>
					<th class="obrigatorio">Data de Nascimento:</th>
					<td><t:inputCalendar id="nascimento"
						value="#{ portalFamiliar.obj.discenteMedio.pessoa.dataNascimento}"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="12"
						maxlength="10" onkeypress="return(formataData(this, event))">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</t:inputCalendar></td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano Inicial</th>
					<td><h:inputText id="ano"
						value="#{ portalFamiliar.obj.discenteMedio.discente.anoIngresso }"
						size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>
				</tr>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Cadastrar"
						action="#{ portalFamiliar.cadastrarVinculo }" /> 
						<h:commandButton id="cancelar" action="#{portalFamiliar.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>

		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

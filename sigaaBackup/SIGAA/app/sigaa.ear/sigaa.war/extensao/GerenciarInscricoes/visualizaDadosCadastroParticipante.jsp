<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Visualizar Dados do Participante</h2>
	
	<table class="tabelaRelatorio" style="width: 100%;">
		<caption>Informações do Cadastro do Participante</caption>
		<tbody>

			<tr>
				<td colspan="2" class="subFormulario">Dados Pessoais</td>
			</tr>
			<c:if test="${_cadastrosParticipanteExtensao.cpf != null}">
				<tr>
					<th width="30%">CPF:</th>
					<td><ufrn:format type="cpf_cnpj" valor="${_cadastrosParticipanteExtensao.cpf}"/></td>
				</tr>
			</c:if>
			<c:if test="${_cadastrosParticipanteExtensao.passaporte != null}">
				<tr>
					<th width="30%">Passaporte:</th>
					<td>${_cadastrosParticipanteExtensao.passaporte}</td>
				</tr>
			</c:if>
			<tr>
				<th>Nome Completo:</th>
				<td>${_cadastrosParticipanteExtensao.nome}</td>
			</tr>
			<tr>
				<th>Data de Nascimento:</th>
				<td>
					<h:outputText value="#{_cadastrosParticipanteExtensao.dataNascimento}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText>
				</td>
			</tr>
			
			
			<tr>
				<td colspan="2" class="subFormulario">Endereço</td>
			</tr>
			<tr>
				<th>Rua/Av.:</th>
				<td>${_cadastrosParticipanteExtensao.logradouro}, <i>Nº.</i> ${_cadastrosParticipanteExtensao.numero}</td>
			</tr>
			
			<tr>
				<th>Complemento:</th>
				<td>${_cadastrosParticipanteExtensao.complemento}</td>
			</tr>
			
			<tr>
				<th>Bairro:</th>
				<td>${_cadastrosParticipanteExtensao.bairro}</td>
			</tr>
			<tr>
				<th>Município/UF:</th>
				<td>${_cadastrosParticipanteExtensao.municipio.nome}/${_cadastrosParticipanteExtensao.unidadeFederativa.sigla}</td>
			</tr>
			<tr>
				<th>CEP:</th>
				<td>${_cadastrosParticipanteExtensao.cep}</td>
			</tr>
			
			
			
			<tr>
				<td colspan="2" class="subFormulario">Dados para Contato</td>
			</tr>
			<tr>
				<th>E-mail:</th>
				<td>${_cadastrosParticipanteExtensao.email}</td>
			</tr>
			<tr>
				<th>Telefone Fixo:</th>
				<td>${_cadastrosParticipanteExtensao.telefone}</td>
			</tr>
			<tr>
				<th>Celular:</th>
				<td>${_cadastrosParticipanteExtensao.celular}</td>
			</tr>
			
			
			
			<tr>
				<td colspan="2" class="subFormulario">Informações Sobre o Cadastro</td>
			</tr>
			<tr>
				<th>Data de Cadastro:</th>
				<td> <ufrn:format type="data" valor="${_cadastrosParticipanteExtensao.dataCadastro}"/> </td>
			</tr>
			
			<tr>
				<th>Data da Última Atualização:</th>
				<td><ufrn:format type="data" valor="${_cadastrosParticipanteExtensao.dataUltimaAtualizacao}"/> </td>
			</tr>
			
			
		</tbody>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

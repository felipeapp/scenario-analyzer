<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Visualizar Inscrição</h2>
	<c:set var="p" value="${inscricaoAtividade.participante}" />
	
	<table class="tabelaRelatorio">
		<caption>Informações do Inscrito</caption>
		<tbody>

			<tr>
				<td colspan="2" class="subFormulario">Dados Pessoais</td>
			</tr>
			<c:if test="${ p.passaporte == null}">
				<tr>
					<th width="20%">CPF:</th>
					<td><ufrn:format type="cpf_cnpj" valor="${p.cpf}"/></td>
				</tr>
			</c:if>
			<c:if test="${p.passaporte != null}">
				<tr>
					<th width="20%">Passaporte:</th>
					<td>${p.passaporte}</td>
				</tr>
			</c:if>
			<tr>
				<th>Nome Completo:</th>
				<td>${p.nome}</td>
			</tr>
			<tr>
				<th>Data de Nascimento:</th>
				<td>
					<h:outputText value="#{inscricaoAtividade.participante.dataNascimento}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText>
				</td>
			</tr>
			<tr>
				<th>Instituição:</th>
				<td>${empty p.instituicao ? 'Não informada' : p.instituicao}</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Endereço</td>
			</tr>
			<tr>
				<th>Rua/Av.:</th>
				<td>${p.logradouro}, <i>Nº.</i> ${p.numero}</td>
			</tr>
			<tr>
				<th>Bairro:</th>
				<td>${p.bairro}</td>
			</tr>
			<tr>
				<th>Município/UF:</th>
				<td>${p.municipio.nome}/${p.unidadeFederativa.sigla}</td>
			</tr>
			<tr>
				<th>CEP:</th>
				<td>${p.cep}</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Contato</td>
			</tr>
			<tr>
				<th>E-mail:</th>
				<td>${p.email}</td>
			</tr>
			<tr>
				<th>Telefone Fixo:</th>
				<td>${p.telefone}</td>
			</tr>
			<tr>
				<th>Celular:</th>
				<td>${p.celular}</td>
			</tr>
		</tbody>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<h2>Relatório de Dados Bancários de Discentes de Extensão</h2>
	<div id="parametrosRelatorio">
	<table>
		<c:if
			test="${discenteExtensao.checkBuscaTituloAtividade and not empty discenteExtensao.buscaTituloAtividade}">
			<tr>
				<th>Título da Ação:</th>
				<td><h:outputText id="buscaTitulo"
					value="#{discenteExtensao.buscaTituloAtividade}" /></td>
			</tr>
		</c:if>

		<c:if
			test="${discenteExtensao.checkBuscaAno and not empty discenteExtensao.anoReferencia}">
			<tr>
				<th>Ano da Ação:</th>
				<td><h:outputText value="#{discenteExtensao.anoReferencia}" /></td>
			</tr>
		</c:if>

		<c:if
			test="${discenteExtensao.checkBuscaAnoInicio and not empty discenteExtensao.dataInicio and not empty discenteExtensao.dataFim}">
			<tr>
				<th>Período:</th>
				<td><h:outputText value="#{discenteExtensao.dataInicio}" /> a <h:outputText
					value="#{discenteExtensao.dataFim}" /></td>
			</tr>
		</c:if>

		<c:if
			test="${discenteExtensao.checkBuscaDiscente and not empty discenteExtensao.discente.pessoa.nome}">
			<tr>
				<th>Discente:</th>
				<td><h:outputText id="nomeDiscente"
					value="#{ discenteExtensao.discente.pessoa.nome }" /></td>
			</tr>
		</c:if>

		<c:if test="${discenteExtensao.checkBuscaSituacao}">
			<tr>
				<th>Situação:</th>
				<td><h:outputText
					value="#{discenteExtensao.situacaoDiscenteExtensao.descricao}" /></td>
			</tr>
		</c:if>

		<c:if test="${discenteExtensao.checkBuscaVinculo}">
			<tr>
				<th>Vínculo:</th>
				<td><h:outputText
					value="#{discenteExtensao.vinculoDiscente.descricao}" /></td>
			</tr>
		</c:if>

		<c:if test="${discenteExtensao.checkBuscaCurso}">
			<tr>
				<th>Curso do Discente:</th>
				<td><h:outputText value="#{discenteExtensao.curso.descricao}" /></td>
			</tr>
		</c:if>
	</table>
	</div>
	<br />
	<br />
	<h3 class="tituloTabelaRelatorio">Ordenado por data de cadastro do
	discente na ação de extensão</h3>

	<c:set var="discentes" value="${discenteExtensao.discentesExtensao}" />

	<c:if test="${empty discentes}">
		<center><i> Nenhum Discentes Localizado </i></center>
	</c:if>

	<c:if test="${not empty discentes}">

		<table class="tabelaRelatorio">
			<tbody>
				<c:forEach items="${discentes}" var="item" varStatus="status">
					<tr>
						<td colspan="2">
						<table style="width:100%; border:none;">

							<tr	style="background-color: #d1cfd7; font-weight: bold; padding: 2px 0 2px 5px;">
								<td colspan="2" style="text-align: center">${item.discente.nome}</td>
							</tr>
							<tr>
								<td colspan="2" style="font-weight: bold; background-color: #f2e5e8">DADOS PESSOAIS</td>
							</tr>
							<tr>
								<th width="20%"><b>Nome:</b></th>
								<td>${item.discente.nome}</td>
							</tr>
							<tr>
								<th width="20%"><b>Matrícula:</b></th>
								<td>${item.discente.matricula}</td>
							</tr>
							<tr>
								<th><b>Curso:</b></th>
								<td>${item.discente.curso.descricao}</td>
							</tr>
							<tr>
								<th><b>CPF:</b></th>
								<td><ufrn:format type="cpf_cnpj" name="item"
									property="discente.pessoa.cpf_cnpj"></ufrn:format></td>
							</tr>
							<tr>
								<th><b>Data Nascimento:</b></th>
								<td><fmt:formatDate
									value="${item.discente.pessoa.dataNascimento}"
									pattern="dd/MM/yyyy" /></td>
							</tr>
							<tr>
								<th><b>Sexo:</b></th>
								<td>${item.discente.pessoa.sexo}</td>
							</tr>
							<tr><td colspan="2">&nbsp;</td></tr>
							<tr>
								<td colspan="2" style="font-weight: bold; background-color: #f2e5e8;">ENDEREÇO</td>
							</tr>
							<tr>
								<th><b>Endereço:</b></th>
								<td>${item.discente.pessoa.enderecoContato.logradouro}</td>
							</tr>
							<tr>
								<th><b>Número:</b></th>
								<td>${item.discente.pessoa.enderecoContato.numero}</td>
							</tr>
							<tr>
								<th><b>Complemento:</b></th>
								<td>${item.discente.pessoa.enderecoContato.complemento}</td>
							</tr>
							<tr>
								<th><b>Bairro:</b></th>
								<td>${item.discente.pessoa.enderecoContato.bairro}</td>
							</tr>
							<tr>
								<th><b>CEP:</b></th>
								<td>${item.discente.pessoa.enderecoContato.cep}</td>
							</tr>
							<tr>
								<th><b>Município:</b></th>
								<td>${item.discente.pessoa.enderecoContato.municipio.nome}</td>
							</tr>
							<tr>
								<th><b>UF:</b></th>
								<td>${item.discente.pessoa.enderecoContato.municipio.unidadeFederativa.sigla}</td>
							</tr>
							<tr>
								<th><b>Telefone Fixo:</b></th>
								<td>${item.discente.pessoa.telefone}</td>
							</tr>
							<tr>
								<th><b>Telefone Celular:</b></th>
								<td>${item.discente.pessoa.celular}</td>
							</tr>
							<tr>
								<th><b>E-mail:</b></th>
								<td>${item.discente.pessoa.email}</td>
							</tr>
							<tr><td colspan="2">&nbsp;</td></tr>

							<tr>
								<td colspan="2" style="font-weight: bold; background-color: #f2e5e8">DADOS BANCÁRIOS</td>
							</tr>
							<tr>
								<th><b>Banco:</b></th>
								<c:if test="${not empty item.banco.denominacao}">
									<td>${item.banco.denominacao}</td>
								</c:if>
								<c:if test="${empty item.banco.denominacao}">
									<td>Banco não informado</td>
								</c:if>
							</tr>
							<tr>
								<th><b>Agência:</b></th>
								<td>${item.agencia}</td>
							</tr>
							<tr>
								<th><b>Conta:</b></th>
								<td>${item.conta}</td>
							</tr>
							<c:if test="${item.operacao != null}">
							<tr>
								<th><b>Operação:</b></th>
								<td>${item.operacao}</td>
							</tr>
							</c:if>
							<tr><td colspan="2">&nbsp;</td></tr>
							<tr>
								<td colspan="2" style="font-weight: bold; background-color: #f2e5e8">DADOS DA AÇÃO DE EXTENSÃO</td>
							</tr>
							<tr>
								<th><b>Ação de Extensão:</b></th>
								<td>${item.planoTrabalhoExtensao.atividade.titulo}</td>
							</tr>
							<tr>
								<th><b>Coordenador(a):</b></th>
								<td>${item.planoTrabalhoExtensao.atividade.coordenacao.pessoa.nome}</td>
							</tr>
							<tr>
								<th><b>Vínculo:</b></th>
								<td>${item.tipoVinculo.descricao}</td>
							</tr>
							<tr>
								<th><b>Situação:</b></th>
								<td>${item.situacaoDiscenteExtensao.descricao}</td>
							</tr>
							<tr>
								<th><b>Data do Cadastro:</b></th>
								<td><fmt:formatDate value="${item.dataCadastro}"
									pattern="dd/MM/yyyy HH:mm" /></td>
							</tr>
						</table>
						</td>
					</tr>
					<tr>
						<td colspan="2">
						<br />
						<br />
						<br />
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td style="font-weight: bold; width: 40%;">Total de Discentes Encontrados:</td>
					<td style="text-align: right;">${ fn:length(discentes) }</td>
				</tr>
			</tfoot>
		</table>

	</c:if>



</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
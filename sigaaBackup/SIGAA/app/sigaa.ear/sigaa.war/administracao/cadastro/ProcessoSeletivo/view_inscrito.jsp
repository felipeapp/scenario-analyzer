<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
#abas-inscricao {
	width: 80%;
	margin: 0 auto;
}

h3 {
	margin: 2px 0 10px;
}

h4 {
	margin: 5px 0;
}

.descricaoOperacao th {
	font-weight: bold;
	padding: 0 2px 5px 2px;
}

.curso,.nivel {
	text-align: center;
	display: block;
}

.nivel {
	font-size: 0.9em;
	text-transform: uppercase;
	color: #555;
}

.arquivo a {
	text-decoration: underline;
	color: #404E82;
	font-variant: small-caps;
}

.periodo {
	color: #292;
	font-weight: bold;
}

#form :sexo {
	border: 0;
}
</style>

<f:view>
	<h2>Resumo de Inscrição em Processo Seletivo</h2>

	<div class="descricaoOperacao">
	<h3>
	<c:choose>
		<%-- SE PROCESSO SELETIVO CURSO LATOS, PÓS E TÉCNICO --%>
		<c:when test="${not empty processoSeletivo.obj.curso}">
			<span class="curso">CURSO DE ${processoSeletivo.obj.curso.descricao}</span>
			<span class="nivel">(${processoSeletivo.obj.curso.nivelDescricao}) </span>
		</c:when>
		<%-- SE PROCESSO SELETIVO CURSO GRADUAÇÃO --%>
		<c:otherwise>
			<span class="curso">CURSO DE ${processoSeletivo.obj.matrizCurricular.curso.descricao}</span>
			<span class="nivel">(${processoSeletivo.obj.matrizCurricular.curso.nivelDescricao}) </span>
		</c:otherwise>	
	</c:choose>	
	</h3>
	</div>

	<h:form id="form">
		<h3 class="tituloTabela">Dados do Candidato Inscrito</h3>
		<table class="visualizacao" style="width: 100%;">
			<tr>
				<td colspan="4" class="subFormulario">Dados da Inscrição</td>
			</tr>
			<tr>
				<th  width="25%">Número de Inscrição:</th>
				<td colspan="3"><h:outputText value="#{inscricaoSelecao.obj.numeroInscricao}" /></td>
			</tr>
			<tr>
				<th>Situação:</th>
				<td colspan="3"><h:outputText value="#{inscricaoSelecao.obj.descricaoStatus}" /></td>
			</tr>
			<tr>
				<th>Data de Inscrição:</th>
				<td colspan="3"><ufrn:format type="dataHora" valor="${inscricaoSelecao.obj.dataInscricao}" /></td>
			</tr>
			<tr>
				<td colspan="4" class="subFormulario">Dados Pessoais</td>
			</tr>
			<tr>
				<th>CPF:</th>
				<td><ufrn:format type="cpf_cnpj"
					valor="${inscricaoSelecao.obj.pessoaInscricao.cpf}" /></td>

				<th>Sexo:</th>
				<td>${inscricaoSelecao.obj.pessoaInscricao.sexo ==
				'M'?'Masculino':'Feminino'}</td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td colspan="3"><h:outputText
					value="#{inscricaoSelecao.obj.pessoaInscricao.nome}" /></td>
			</tr>
			<tr>
				<th>Estado Civil:</th>
				<td>${inscricaoSelecao.obj.pessoaInscricao.estadoCivil.descricao}</td>

				<th>Email:</th>
				<td><h:outputText
					value="#{inscricaoSelecao.obj.pessoaInscricao.email}" /></td>
			</tr>
			<tr>
				<th>Data de Nascimento:</th>
				<td><ufrn:format type="data"
					valor="${inscricaoSelecao.obj.pessoaInscricao.dataNascimento}" /></td>
				<th>Raça:</th>
				<td>${inscricaoSelecao.obj.pessoaInscricao.tipoRaca.descricao}</td>
			</tr>
			<tr>
				<th>Nome da Mãe:</th>
				<td colspan="3"><h:outputText
					value="#{inscricaoSelecao.obj.pessoaInscricao.nomeMae}" /></td>
			</tr>
			<tr>
				<th>Nome do Pai:</th>
				<td colspan="3"><h:outputText
					value="#{inscricaoSelecao.obj.pessoaInscricao.nomePai}" /></td>
			</tr>
			<tr>
				<td colspan="4" class="subFormulario">Naturalidade</td>
			</tr>
			<tr>
				<td colspan="4">
				<table width="100%" class="subFormulario">
					<tr>
						<th width="20%">País:</th>
						<td width="25%">
						${inscricaoSelecao.obj.pessoaInscricao.pais.nome}</td>

						<th width="20%">UF:</th>
						<td>
						${inscricaoSelecao.obj.pessoaInscricao.unidadeFederativa.descricao}
						</td>
					</tr>

					<tr>
						<th>Município:</th>
						<td colspan="3">
						${inscricaoSelecao.obj.pessoaInscricao.municipio.nome}</td>
					</tr>

				</table>
				</td>
			</tr>

			<tr>
				<td colspan="4" class="subFormulario">Documentos</td>
			</tr>
			<tr>
				<td colspan="4">
				<table width="100%" class="subFormulario">
					<tr>
						<th width="20%">RG:</th>
						<td width="25%"><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.identidade.numero}" /></td>
						<th width="20%">Órgão de Expedição:</th>
						<td><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.identidade.orgaoExpedicao}" /></td>
					<tr>
						<th>UF:</th>
						<td>${inscricaoSelecao.obj.pessoaInscricao.identidade.unidadeFederativa.descricao}</td>
						<th>Data de Expedição:</th>
						<td><ufrn:format type="data"
							valor="${inscricaoSelecao.obj.pessoaInscricao.identidade.dataExpedicao}" /></td>
					</tr>
					<tr>
						<th>Título de Eleitor:</th>
						<td><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.tituloEleitor.numero}" />
						Zona: <h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.tituloEleitor.zona}" />
						</td>
						<th>Seção:</th>
						<td><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.tituloEleitor.secao}" />
						UF:
						${inscricaoSelecao.obj.pessoaInscricao.tituloEleitor.unidadeFederativa.descricao}
						</td>
					</tr>
					<tr>
						<th>Passaporte:</th>
						<td><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.passaporte}" /></td>
					</tr>
				</table>
				</td>
			</tr>

			<tr>
				<td colspan="4" class="subFormulario">Endereço</td>
			</tr>
			<tr>
				<td colspan="4">
				<table width="100%" class="subFormulario">
					<tr class="linhaCep">
						<th>CEP:</th>
						<td colspan="5"><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.cep}" />
						</td>
					</tr>
					<tr>
						<th>Logradouro:</th>
						<td colspan="3">
						${inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.tipoLogradouro.descricao}
						<h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.logradouro}" />
						</td>
						<th>N.&deg;:</th>
						<td><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.numero}" /></td>
					</tr>

					<tr>
						<th>Bairro:</th>
						<td><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.bairro}" /></td>
						<th>Complemento:</th>
						<td><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.complemento}" />
						</td>
					</tr>

					<tr>
						<th>UF:</th>
						<td><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.unidadeFederativa.sigla}" />
						</td>
						<th>Município:</th>
						<td colspan="4"><h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.municipio.nome}" />
						</td>
					</tr>

					<tr>
						<th>Tel. Fixo:</th>

						<td>(<h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.codigoAreaNacionalTelefoneFixo}" />)
						<h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.telefone}" /></td>
						<th>Tel. Celular:</th>
						<td colspan="3">(<h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.codigoAreaNacionalTelefoneCelular}" />)
						<h:outputText
							value="#{inscricaoSelecao.obj.pessoaInscricao.celular}" /></td>
					</tr>

					<c:if test="${not empty inscricaoSelecao.obj.orientador or not empty inscricaoSelecao.obj.linhaPesquisa}">
					<tr>
						<td colspan="6" class="subFormulario"> Outras Informações para o Processo Seletivo </td>
					</tr>
					</c:if>				

					
					<c:if test="${not empty inscricaoSelecao.obj.orientador}">
					<tr>
						<th colspan="2" width="20%">Orientador:</th>
						<td colspan="2"><h:outputText
							value="#{inscricaoSelecao.obj.orientador.nome}" /></td>
					</tr>
					</c:if>
		
					<c:if test="${not empty inscricaoSelecao.obj.linhaPesquisa}">
					<tr>
						<th colspan="2">Linha de pesquisa:</th>
						<td colspan="2"><h:outputText
							value="#{inscricaoSelecao.obj.linhaPesquisa.denominacao}" />
						</td>
					</tr>
					</c:if>
					
					<c:if test="${not empty inscricaoSelecao.obj.idArquivoProjeto}">
					<tr>
						<th colspan="2">Projeto de Pesquisa:</th>
						<td colspan="2">
							<a href="${ctx}/verProducao?idProducao=${ inscricaoSelecao.obj.idArquivoProjeto}&key=${ sf:generateArquivoKey(inscricaoSelecao.obj.idArquivoProjeto) }" target="_blank">
								<h:graphicImage value="/img/icones/document_view.png" style="overflow: visible;vertical-align:middle" />Clique aqui para abrir o arquivo
							</a>
						</td>
					</tr>
					</c:if>
		
					<%-- Respostas do Questionário  --%>
					<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.questionario}">
						<tr>
							<td colspan="6" class="subFormulario"> ${inscricaoSelecao.obj.processoSeletivo.questionario.titulo} </td>
						</tr>
						<tr>
							<td colspan="6">
								<%@include file="/geral/questionario/_respostas.jsp" %>
							</td> 
						</tr>
					</c:if>

					<%-- Observações  --%>
					<c:if test="${not empty inscricaoSelecao.obj.observacoes}">
					<tr>
						<td colspan="6" class="subFormulario"> Observações do candidato </td>
					</tr>
					<tr>
						<td colspan="6">
							<ufrn:format type="texto"valor="${inscricaoSelecao.obj.observacoes}" /></td>
						</td>
					</tr>	
					</c:if>			

				</table>
				</td>
			</tr>

		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
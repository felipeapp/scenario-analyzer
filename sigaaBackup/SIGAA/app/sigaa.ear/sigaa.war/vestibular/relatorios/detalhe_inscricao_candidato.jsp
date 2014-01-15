<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > ${ relatoriosVestibular.nomeRelatorio }</h2>

		<table class="formulario" width="95%">
			<caption>Dados Pessoais</caption>
			<tbody>
				<tr>
					<td colspan="6" class="subFormulario">Dados Pessoais</td>
				</tr>
				<tr>
					<th class="rotulo" >CPF:</th>
					<td colspan="4">
						<ufrn:format type="cpf_cnpj" valor="${relatoriosVestibular.inscricao.pessoa.cpf_cnpj}"></ufrn:format>
					</td>
					<td rowspan="8" valign="top" style="text-align: right;">
						<c:if test="${not empty relatoriosVestibular.inscricao.pessoa.idFoto}">
							<img src="${ctx}/verFoto?idArquivo=${relatoriosVestibular.inscricao.pessoa.idFoto}&key=${ sf:generateArquivoKey(relatoriosVestibular.inscricao.pessoa.idFoto) }" style="width: 150px; height: 200px"/>
						</c:if>
					</td>
				</tr>
				<c:if test="${not empty relatoriosVestibular.inscricao.pessoa.passaporte}">
					<tr>
						<th class="rotulo">Passaporte:</th>
						<td colspan="5"><h:outputText
							value="#{relatoriosVestibular.inscricao.pessoa.passaporte}" /></td>
					</tr>
				</c:if>
				<tr>
					<th class="rotulo">Nome:</th>
					<td colspan="4"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.nome}" /></td>
				</tr>
				<tr>
					<th class="rotulo">E-Mail:</th>
					<td colspan="4"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.email}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Nome da Mãe:</th>
					<td colspan="4"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.nomeMae}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Nome do Pai:</th>
					<td colspan="4"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.nomePai}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Sexo:</th>
					<td><c:if
						test="${relatoriosVestibular.inscricao.pessoa.sexo == 'F'}">
						<h:outputText value="Feminino" />
					</c:if><c:if test="${relatoriosVestibular.inscricao.pessoa.sexo == 'M'}">
						<h:outputText value="Masculino" />
					</c:if></td>
					<th class="rotulo">Data de Nascimento:</th>
					<td colspan="2"><t:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.dataNascimento}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Estado Civil:</th>
					<td><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.estadoCivil.descricao}" /></td>
					<th class="rotulo">Raça:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.tipoRaca.descricao}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Tipo de Necessidade Especial:</th>
					<td colspan="4">
						<h:outputText value="#{relatoriosVestibular.inscricao.pessoa.tipoNecessidadeEspecial.descricao}"rendered="#{not empty relatoriosVestibular.inscricao.pessoa.tipoNecessidadeEspecial.descricao}" />
						<h:outputText value="Não informado" rendered="#{empty relatoriosVestibular.inscricao.pessoa.tipoNecessidadeEspecial.descricao}" />
					</td>
				</tr>

				<tr>
					<td colspan="6" class="subFormulario">Escola de Conclusão do Ensino Médio</td>
				</tr>
				<tr>
					<th class="rotulo">Escola de Conclusão do EM:</th>
					<td>
						<h:outputText value="#{relatoriosVestibular.inscricao.pessoa.nomeEscolaConclusaoEnsinoMedio}" />
					</td>
					<th class="rotulo">Ano de Conclusão:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.anoConclusaoEnsinoMedio}" /></td>
				</tr>
				
				<!-- ============= NATURALIDADE  -->
				<tr>
					<td colspan="6" class="subFormulario">Naturalidade</td>
				</tr>
				<tr>
					<th class="rotulo">País:</th>
					<td colspan="2"><h:outputText value="#{relatoriosVestibular.inscricao.pessoa.pais.nome}" /></td>
					<th class="rotulo">Nacionalidade:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.pais.nacionalidade}" /></td>
				</tr>
				<tr>
					<th class="rotulo">UF:</th>
					<td colspan="2"><h:outputText value="#{relatoriosVestibular.inscricao.pessoa.unidadeFederativa.descricao}" /></td>
					<th class="rotulo">Município:</th>
					<td colspan="5">
						<h:outputText value="#{relatoriosVestibular.inscricao.pessoa.municipio.nome}" />
						<h:outputText value="#{relatoriosVestibular.inscricao.pessoa.municipioNaturalidadeOutro}" />
					</td>
				</tr>

				<!-- ============= DOCUMENTAÇÃO  -->
				<tr>
					<td colspan="6" class="subFormulario">Documento de Identidade</td>
				</tr>
				<tr>
					<th class="rotulo">Nº do Doc. de Identificação:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.identidade.numero}" /></td>
					<th class="rotulo">Órgão de Expedição:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.identidade.orgaoExpedicao}" /></td>
				</tr>
				<tr>
					<th class="rotulo">UF:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.identidade.unidadeFederativa.descricao}" /></td>
					<th class="rotulo">Data de Expedição:</th>
					<td colspan="2"><t:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.identidade.dataExpedicao}" /></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Título de Eleitor</td>
				</tr>
				<tr>
					<th class="rotulo">Nº do Título:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.tituloEleitor.numero}" /></td>
					<th class="rotulo">Zona:</th> 
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.tituloEleitor.zona}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Seção:</th>
					<td colspan="2"><h:outputText value="#{relatoriosVestibular.inscricao.pessoa.tituloEleitor.secao}" /></td>
					<th class="rotulo">UF:</th>
					<td colspan="2"> <h:outputText value="#{relatoriosVestibular.inscricao.pessoa.tituloEleitor.unidadeFederativa.descricao}" /></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Certificado Militar</td>
				</tr>
				<tr>
					<th class="rotulo"> Nº do Certificado Militar:</th>
					<td> <h:outputText value="#{ relatoriosVestibular.inscricao.pessoa.certificadoMilitar.numero }" /> </td>
					<th class="rotulo"> Data de Expedição: </th>
					<td><h:outputText value="#{ relatoriosVestibular.inscricao.pessoa.certificadoMilitar.dataExpedicao }" /></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th class="rotulo"> Série:</th>
					<td> <h:outputText value="#{ relatoriosVestibular.inscricao.pessoa.certificadoMilitar.serie }" /> </td>
					<th class="rotulo"> Categoria: </th>
					<td> <h:outputText value="#{ relatoriosVestibular.inscricao.pessoa.certificadoMilitar.categoria }" /> </td>
					<th class="rotulo"> Órgão:</th>
					<td> <h:outputText value="#{ relatoriosVestibular.inscricao.pessoa.certificadoMilitar.orgaoExpedicao }"/></td>
				</tr>

				<!-- ============= ENDEREÇO DE CONTATO -->
				<tr>
					<td colspan="6" class="subFormulario">Informações Para Contato</td>
				</tr>
				<tr class="linhaCep">
					<th class="rotulo">CEP:</th>
					<td colspan="5"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.enderecoContato.cep}" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Logradouro:</th>
					<td colspan="2">
						<h:outputText value="#{relatoriosVestibular.inscricao.pessoa.enderecoContato.tipoLogradouro} " />  
						<h:outputText value="#{relatoriosVestibular.inscricao.pessoa.enderecoContato.logradouro }" />
					</td>
					<th class="rotulo">N&deg;:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.enderecoContato.numero}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Bairro:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.enderecoContato.bairro}" /></td>
					<th class="rotulo">Complemento:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.enderecoContato.complemento}" /></td>
				</tr>
				<tr>
					<th class="rotulo">UF:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.enderecoContato.unidadeFederativa.descricao}" /></td>
					<th class="rotulo">Município:</th>
					<td colspan="2"><h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.enderecoContato.municipio.nome}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Tel. Fixo:</th>
					<td colspan="2">(<h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.codigoAreaNacionalTelefoneFixo}" />)
					<h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.telefone}" /></td>
					<th class="rotulo">Tel. Celular:</th>
					<td colspan="2">(<h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.codigoAreaNacionalTelefoneCelular}" />)
					<h:outputText
						value="#{relatoriosVestibular.inscricao.pessoa.celular}" /></td>
				</tr>
				<tr>
				<td colspan="6"></td>
				</tr>
		
			<tr>
				<td colspan="6" class="subFormulario">Inscrição</td>
			</tr>
			<tbody>
				<tr>
					<th><b>Área de Conhecimento:</b></th>
					<td><h:outputText
						value="#{relatoriosVestibular.inscricao.opcoesCurso[0].curso.areaVestibular.descricao}" /></td>
				</tr>
				<tr>
					<th><b>Primeira Opção:</b></th>
					<td><h:outputText
						value="#{relatoriosVestibular.inscricao.opcoesCurso[0].curso.municipio}" />
					- <h:outputText
						value="#{relatoriosVestibular.inscricao.opcoesCurso[0]}" /></td>
				</tr>
				<tr>
					<th><b>Segunda Opção:</b></th>
					<td><h:outputText
						value="#{relatoriosVestibular.inscricao.opcoesCurso[1].curso.municipio}" />
					- <h:outputText
						value="#{relatoriosVestibular.inscricao.opcoesCurso[1]}" /></td>
				</tr>
				<tr>
					<th><b>Língua Estrangeira:</b></th>
					<td><h:outputText
						value="#{relatoriosVestibular.inscricao.linguaEstrangeira.denominacao}" /></td>
				</tr>
				<tr>
					<th><b>Região Preferencial de Prova:</b></th>
					<td><h:outputText
						value="#{relatoriosVestibular.inscricao.regiaoPreferencialProva.denominacao}" /></td>
				</tr>
				<tr>
					<th valign="top"><b>Situação da Inscrição:</b></th>
					<td>
						<c:choose>
							<c:when test="${relatoriosVestibular.inscricao.validada and 
												relatoriosVestibular.inscricao.valorInscricao == 0 && 
												relatoriosVestibular.inscricao.processoSeletivo.inscricoesCandidatoAbertas}">
								Pré-Validada.<br/> Em caso de nova inscrição, esta será invalidada e a nova tornar-se-á validada. Consulte a a validação definitiva desta inscrição a partir do dia
								<ufrn:format type="data" valor="${relatoriosVestibular.inscricao.processoSeletivo.fimInscricaoCandidato}" /> 
							</c:when>
							<c:when test="${relatoriosVestibular.inscricao.validada and (relatoriosVestibular.inscricao.valorInscricao > 0 or relatoriosVestibular.inscricao.valorInscricao == 0 && not relatoriosVestibular.inscricao.processoSeletivo.inscricoesCandidatoAbertas)}">
								Validada
							</c:when>
							<c:when test="${not relatoriosVestibular.inscricao.validada}">
								Aguardando confirmação da COMPERVE
							</c:when>
						</c:choose>
					</td>
				</tr>
			</tbody>		
		</table>
	
	<br />
	<center>
		<form>
			<a href="javascript:history.go(-1)"> << Voltar</a>
		</form>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
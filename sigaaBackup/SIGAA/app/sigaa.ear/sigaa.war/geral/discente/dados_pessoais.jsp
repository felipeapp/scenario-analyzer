<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<table class="formulario" width="100%">

	<%-- nao tire a matricula daqui. ela precisa ser impressa 2x na tela.
	veja, Log Tarefa 6128 - Cadastro de alunos - ${ configSistema['siglaUnidadeGestoraGraduacao'] } - Ref. Chamado 16458 
	--%>

	<caption>Dados do Aluno ${discente.matricula}</caption>
	<tbody>
		<tr>
			<th width="22%"> Matrícula: </th>
			<td colspan="3"> ${discente.matricula}</td>
		</tr>
		<tr>
			<th> Nome: </th>
			<td colspan="3"> ${discente.pessoa.nome}</td>
		</tr>
		<c:if test="${discente.pessoa.nome ne discente.pessoa.nomeOficial}">
			<tr>
				<th> Nome Oficial: </th>
				<td colspan="3"> ${discente.pessoa.nome}</td>
			</tr>
		</c:if>
		<tr>
			<th width="22%"> Status: </th>
			<td colspan="3"> ${discente.statusString}</td>
		</tr>
		<c:choose>
			<c:when test="${discente.graduacao}">
				<tr>
					<th width="22%"> Forma de Ingresso: </th>
					<td> ${discente.formaIngresso.descricao}</td>
					<th> Cola Grau: </th>
					<td colspan="3">${(discente.colaGrau?'Sim':'Não')}</td>
				</tr>
				<tr>
					<th> Ano/Período de Ingresso: </th>
					<td colspan="3">${discente.anoIngresso}.${discente.periodoIngresso}</td>
				</tr>
				<tr>
					<th> Curso: </th>
					<td colspan="3"> ${discente.matrizCurricular.descricao}</td>
				</tr>
				<tr>
					<th> Estrutura Curricular: </th>
					<td colspan="3"> ${discente.curriculo}</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<th width="22%"> Forma de Ingresso: </th>
					<td colspan="3"> ${discente.formaIngresso.descricao}</td>
				</tr>
				<tr>
					<th> Curso: </th>
					<td colspan="3"> ${discente.curso.descricao}</td>
				</tr>
			</c:otherwise>
		</c:choose>
	<c:if test="${not acesso.consulta or acesso.tecnico}">
		<tr>
			<td colspan="4" class="subFormulario"> Dados Pessoais </td>
		</tr>
		<tr>
			<th> Sexo: </th>
			<td width="25%"> ${discente.pessoa.sexo}</td>
			<th> Estado Civil: </th>
			<td> ${discente.pessoa.estadoCivil.descricao} </td>
		</tr>
		<tr>
			<th> Data de Nascimento:</th>
			<td> <fmt:formatDate pattern="dd/MM/yyyy" value="${discente.pessoa.dataNascimento}"/> </td>
			<th> Naturalidade: </th>
			<td>
				<c:choose>
					<c:when test="${discente.pessoa.internacional}">
						${discente.pessoa.municipioNaturalidadeOutro}
					</c:when>
					<c:otherwise>
						${discente.pessoa.municipio.nome} / ${discente.pessoa.unidadeFederativa.descricao}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<th> Raça:</th>
			<td>${discente.pessoa.tipoRaca.descricao}</td>
			<th> Tipo Sanguíneo:</th>
			<td>${discente.pessoa.tipoSanguineo}</td>
		</tr>
		<tr>
			<th> Nacionalidade:</th>
			<td> ${discente.pessoa.paisNacionalidade eq null ? discente.pessoa.pais.nacionalidade : discente.pessoa.paisNacionalidade} </td>
			<th> País: </th>
			<td>${discente.pessoa.pais.nome}</td>
		</tr>
		<tr>
			<th> Nome do Pai:</th>
			<td colspan="3"> ${discente.pessoa.nomePai}  </td>
		</tr>
		<tr>
			<th> Nome da Mãe:</th>
			<td> ${discente.pessoa.nomeMae}  </td>
			<th> Necessidade Especial:</th>
			<td> ${discente.pessoa.tipoNecessidadeEspecial.descricao}</td>
		</tr>
		<tr>
			<th>Escola de Conclusão do Ensino Médio:</th>
			<td> ${discente.pessoa.escolaConclusaoEnsinoMedio} </td>
			<th>Ano de Conclusão:</th>
			<td> ${discente.pessoa.anoConclusaoEnsinoMedio}</td>
		</tr>

		<tr>
			<td colspan="4" class="subFormulario"> Documentos </td>
		</tr>
		<tr>
			<th> CPF:</th>
			<td>
				<c:choose>
					<c:when test="${ not empty discente.pessoa.cpf_cnpj }">
						<ufrn:format type="cpf_cnpj" valor="${discente.pessoa.cpf_cnpj}"/>
					</c:when>
					<c:when test="${ discente.pessoa.internacional and empty discente.pessoa.cpf_cnpj }">
						<i> Esta pessoa é estrangeira e não possui CPF </i>
					</c:when>
					<c:otherwise>
						&nbsp;
					</c:otherwise>
				</c:choose>
			</td>
			<th> RG: </th>
			<td>${discente.pessoa.identidade.numero} - ${discente.pessoa.identidade.orgaoExpedicao}/${discente.pessoa.identidade.unidadeFederativa.sigla}</td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
			<th>Data de Expedição:</th> 
			<td><fmt:formatDate value="${ discente.pessoa.identidade.dataExpedicao }" pattern="dd/MM/yyyy"/></td>
		</tr>
		<tr>
			<th> Título de Eleitor:</th>
			<td> ${ discente.pessoa.tituloEleitor.numero } </td>
			<th> Zona:</th>
			<td> ${ discente.pessoa.tituloEleitor.zona }</td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
			<th> Seção: </th>
			<td> ${discente.pessoa.tituloEleitor.secao} </td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
			<th> UF: </th>
			<td> ${discente.pessoa.tituloEleitor.unidadeFederativa.descricao}</td>
		</tr>
		<tr>
			<th> Certificado Militar:</th>
			<td> ${ discente.pessoa.certificadoMilitar.numero } </td>
			<th> Data de Expedição: </th>
			<td><fmt:formatDate value="${ discente.pessoa.certificadoMilitar.dataExpedicao }" pattern="dd/MM/yyyy"/></td>
		</tr>
		<tr>
			<th> Série:</th>
			<td> ${ discente.pessoa.certificadoMilitar.serie } </td>
			<th> Categoria: </th>
			<td> ${ discente.pessoa.certificadoMilitar.categoria } </td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
			<th> Órgão:</th> 
			<td> ${ discente.pessoa.certificadoMilitar.orgaoExpedicao } </td>
		</tr>
		<tr>
			<th>Passaporte:</th>
			<td>
				${discente.pessoa.passaporte}
			</td>
		</tr>
	</c:if>
		<tr>
			<td colspan="4" class="subFormulario"> Endereço </td>
		</tr>
		<tr>
			<th> Endereço:</th>
			<td colspan="3">
				${discente.pessoa.enderecoContato.tipoLogradouro}
				${discente.pessoa.enderecoContato.logradouro}
			</td>
		</tr>
		<tr>
			<th> Número: </th>
			<td> ${discente.pessoa.enderecoContato.numero}</td>
			<th> Complemento: </th>
			<td/> ${discente.pessoa.enderecoContato.complemento}</td>
		</tr>
		<tr>
			<th> Bairro: </th>
			<td colspan="3">
				${discente.pessoa.enderecoContato.bairro}
			</td>
		</tr>
		<tr>
			<th> Unidade Federativa:</th>
			<td>
				${discente.pessoa.enderecoContato.unidadeFederativa.descricao}
			</td>
			<th> Município:</th>
			<td>
				${discente.pessoa.enderecoContato.municipio.nome}
			</td>
		</tr>
		<tr>
			<th> CEP: </th>
			<td colspan="3"> ${discente.pessoa.enderecoContato.cep} </td>
		</tr>
		<tr>
			<td colspan="4" class="subFormulario"> Contatos </td>
		</tr>
		<tr>
			<th> Telefone: </th>
			<td colspan="3"> ${discente.pessoa.telefone} </td>
		</tr>
		<tr>
			<th> Celular: </th>
			<td colspan="3"> ${discente.pessoa.celular} </td>
		</tr>
		<tr>
			<th> E-Mail:</th>
			<td colspan="3"> ${discente.pessoa.email} </td>
		</tr>
		<tr>
			<td colspan="4" class="subFormulario"> Dados Bancários </td>
		</tr>
		<tr>
			<th> Banco: </th>
			<td colspan="3"> ${discente.pessoa.contaBancaria.banco.codigoNome} </td>
		</tr>
		<tr>
			<th> Conta: </th>
			<td colspan="3"> ${discente.pessoa.contaBancaria.numero} </td>
		</tr>
		<tr>
			<th> Agência:</th>
			<td colspan="3"> ${discente.pessoa.contaBancaria.agencia} </td>
		</tr>
		<c:if test="${discente.pessoa.contaBancaria.operacao != null}">
		<tr>
			<th> Operação:</th>
			<td colspan="3"> ${discente.pessoa.contaBancaria.operacao} </td>
		</tr>
		</c:if>
	</tbody>
</table>
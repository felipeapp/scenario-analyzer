<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<table class="formulario" width="100%">

	<caption>
		<c:if test="${ not empty nomeOperacao_}">
			${nomeOperacao_}
		</c:if>
		<c:if test="${ empty nomeOperacao_}">
			Dados da Pessoa
		</c:if>
	</caption>
	<tbody>
		<tr>
			<th> Nome: </th>
			<td colspan="3"> ${pessoa_.nome}</td>
		</tr>
		<c:if test="${pessoa_.nome ne pessoa_.nomeOficial}">
			<tr>
				<th> Nome Oficial: </th>
				<td colspan="3"> ${pessoa_.nome}</td>
			</tr>
		</c:if>
		<c:if test="${docenteRede_}">
			<%@include file="/ensino_rede/docente_rede/include/dadosEspecificos.jsp"%>
		</c:if>
		<tr>
			<td colspan="4" class="subFormulario"> Dados pessoais </td>
		</tr>
		<tr>
			<th> Sexo: </th>
			<td width="25%"> ${pessoa_.sexo}</td>
			<th> Estado Civil: </th>
			<td> ${pessoa_.estadoCivil.descricao} </td>
		</tr>
		<tr>
			<th> Data de Nascimento:</th>
			<td> <fmt:formatDate pattern="dd/MM/yyyy" value="${pessoa_.dataNascimento}"/> </td>
			<th> Naturalidade: </th>
			<td>
				<c:choose>
					<c:when test="${pessoa_.internacional}">
						${pessoa_.municipioNaturalidadeOutro}
					</c:when>
					<c:otherwise>
						${pessoa_.municipio.nome} / ${pessoa_.unidadeFederativa.descricao}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<th> Raça:</th>
			<td>${pessoa_.tipoRaca.descricao}</td>
			<th> Tipo Sanguíneo:</th>
			<td>${pessoa_.tipoSanguineo}</td>
		</tr>
		<tr>
			<th> Nacionalidade:</th>
			<td> ${pessoa_.paisNacionalidade eq null ? pessoa_.pais.nacionalidade : pessoa_.paisNacionalidade} </td>
			<th> País: </th>
			<td>${pessoa_.pais.nome}</td>
		</tr>
		<tr>
			<th> Nome do Pai:</th>
			<td colspan="3"> ${pessoa_.nomePai}  </td>
		</tr>
		<tr>
			<th> Nome da Mãe:</th>
			<td> ${pessoa_.nomeMae}  </td>
			<th> Necessidade Especial:</th>
			<td> ${pessoa_.tipoNecessidadeEspecial.descricao}</td>
		</tr>
		<tr>
			<th>Escola de Conclusão do Ensino Médio:</th>
			<td> ${pessoa_.escolaConclusaoEnsinoMedio} </td>
			<th>Ano de Conclusão:</th>
			<td> ${pessoa_.anoConclusaoEnsinoMedio}</td>
		</tr>

		<tr>
			<td colspan="4" class="subFormulario"> Documentos </td>
		</tr>
		<tr>
			<th> CPF:</th>
			<td>
				<c:choose>
					<c:when test="${ not empty pessoa_.cpf_cnpj }">
						<ufrn:format type="cpf_cnpj" valor="${pessoa_.cpf_cnpj}"/>
					</c:when>
					<c:when test="${ pessoa_.internacional and empty pessoa_.cpf_cnpj }">
						<i> Esta pessoa_ é estrangeira e não possui CPF </i>
					</c:when>
					<c:otherwise>
						&nbsp;
					</c:otherwise>
				</c:choose>
			</td>
			<th> RG: </th>
			<td>${pessoa_.identidade.numero} - ${pessoa_.identidade.orgaoExpedicao}/${pessoa_.identidade.unidadeFederativa.sigla}</td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
			<th>Data de Expedição:</th> 
			<td><fmt:formatDate value="${ pessoa_.identidade.dataExpedicao }" pattern="dd/MM/yyyy"/></td>
		</tr>
		<tr>
			<th> Título de Eleitor:</th>
			<td> ${ pessoa_.tituloEleitor.numero } </td>
			<th> Zona:</th>
			<td> ${ pessoa_.tituloEleitor.zona }</td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
			<th> Seção: </th>
			<td> ${pessoa_.tituloEleitor.secao} </td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
			<th> UF: </th>
			<td> ${pessoa_.tituloEleitor.unidadeFederativa.descricao}</td>
		</tr>
		<tr>
			<th> Certificado Militar:</th>
			<td> ${ pessoa_.certificadoMilitar.numero } </td>
			<th> Data de Expedição: </th>
			<td><fmt:formatDate value="${ pessoa_.certificadoMilitar.dataExpedicao }" pattern="dd/MM/yyyy"/></td>
		</tr>
		<tr>
			<th> Série:</th>
			<td> ${ pessoa_.certificadoMilitar.serie } </td>
			<th> Categoria: </th>
			<td> ${ pessoa_.certificadoMilitar.categoria } </td>
		</tr>
		<tr>
			<th>&nbsp;</th>
			<td>&nbsp;</td>
			<th> Órgão:</th> 
			<td> ${ pessoa_.certificadoMilitar.orgaoExpedicao } </td>
		</tr>
		<tr>
			<th>Passaporte:</th>
			<td>
				${pessoa_.passaporte}
			</td>
		</tr>
		<tr>
			<td colspan="4" class="subFormulario"> Endereço </td>
		</tr>
		<tr>
			<th> Endereço:</th>
			<td colspan="3">
				${pessoa_.enderecoContato.tipoLogradouro}
				${pessoa_.enderecoContato.logradouro}
			</td>
		</tr>
		<tr>
			<th> Número: </th>
			<td> ${pessoa_.enderecoContato.numero}</td>
			<th> Complemento: </th>
			<td/> ${pessoa_.enderecoContato.complemento}</td>
		</tr>
		<tr>
			<th> Bairro: </th>
			<td colspan="3">
				${pessoa_.enderecoContato.bairro}
			</td>
		</tr>
		<tr>
			<th> Unidade Federativa:</th>
			<td>
				${pessoa_.enderecoContato.unidadeFederativa.descricao}
			</td>
			<th> Município:</th>
			<td>
				${pessoa_.enderecoContato.municipio.nome}
			</td>
		</tr>
		<tr>
			<th> CEP: </th>
			<td colspan="3"> ${pessoa_.enderecoContato.cep} </td>
		</tr>
		<tr>
			<td colspan="4" class="subFormulario"> Contatos </td>
		</tr>
		<tr>
			<th> Telefone: </th>
			<td colspan="3"> ${pessoa_.telefone} </td>
		</tr>
		<tr>
			<th> Celular: </th>
			<td colspan="3"> ${pessoa_.celular} </td>
		</tr>
		<tr>
			<th> E-Mail:</th>
			<td colspan="3"> ${pessoa_.email} </td>
		</tr>
		<tr>
			<td colspan="4" class="subFormulario"> Dados Bancários </td>
		</tr>
		<tr>
			<th> Banco: </th>
			<td colspan="3"> ${pessoa_.contaBancaria.banco.codigoNome} </td>
		</tr>
		<tr>
			<th> Conta: </th>
			<td colspan="3"> ${pessoa_.contaBancaria.numero} </td>
		</tr>
		<tr>
			<th> Agência:</th>
			<td colspan="3"> ${pessoa_.contaBancaria.agencia} </td>
		</tr>
		<c:if test="${pessoa_.contaBancaria.operacao != null}">
		<tr>
			<th> Operação:</th>
			<td colspan="3"> ${pessoa_.contaBancaria.operacao} </td>
		</tr>
		</c:if>
	</tbody>
</table>
<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
table.formulario th {font-weight: bold;}
-->
</style>
<h2><ufrn:steps /></h2>

<html:form action="/ensino/discente/wizard" enctype="multipart/form-data" method="post" styleId="form">
<html:hidden property="discente.id" />

	<table class="formulario" width="99%">
	<caption>Confira os dados do aluno antes de confirmar a operação</caption>

	<tbody>

			<c:if test="${discenteForm.discente.id > 0}">
			<tr>
			<th width="20%">Matrícula:</th>
			<td colspan="3">
			${discenteForm.discente.matricula}
			</td>
			</tr>
			</c:if>

			<tr>
			<th width="20%">Nome:</th>
			<td colspan="3">
			${discenteForm.discente.pessoa.nome }
			</td>
			</tr>

			<tr>
			<th>E-mail:</th>
			<td colspan="3">${discenteForm.discente.pessoa.email }</td>
			</tr>

			<tr>
			<th>Nome da Mãe:</th>
			<td colspan="3">
			${discenteForm.discente.pessoa.nomeMae }
			</td>
			</tr>

			<tr>
			<th>Nome do Pai:</th>
			<td colspan="3">
			${discenteForm.discente.pessoa.nomePai }
			</td>
			</tr>

			<tr>
			<th>Data de Nascimento:</th>
			<td width="30%">
			<ufrn:format type="data" valor="${discenteForm.discente.pessoa.dataNascimento }" />
			</td>
			<th width="15%">CPF:</th>
			<td>
			<ufrn:format type="cpf_cnpj" valor="${discenteForm.discente.pessoa.cpf_cnpj }" />
			</td>
			</tr>

			<tr>
			<th>Estado Civil:</th>
			<td>
			${discenteForm.discente.pessoa.estadoCivil.descricao}
			</td>
			<th>Rede de Ensino:</th>
			<td>
			${discenteForm.discente.pessoa.tipoRedeEnsino.descricao}
			</td>
			</tr>

			<tr>
			<th>Sexo: </th>
			<td>
			<ufrn:format type="sexo" valor="${discenteForm.discente.pessoa.sexo}" />
			</td>
			<th>Passaporte:</th>
			<td>
			${discenteForm.discente.pessoa.passaporte}
			</td>
			</tr>

			<tr>
			<th>Raça:</th>
			<td colspan="3">
			${discenteForm.discente.pessoa.tipoRaca.descricao }
			</td>
			</tr>

			<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
				<caption>Documento de Identidade</caption>
				<tr>
				<th width="20%">RG:</th>
				<td width="25%">${discenteForm.discente.pessoa.identidade.numero}</td>
				<th width="20%">Órgão de Expedição:</th>
				<td>${discenteForm.discente.pessoa.identidade.orgaoExpedicao }</td>
				</tr>

				<tr>
				<th>UF:</th>
				<td>
				${discenteForm.discente.pessoa.identidade.unidadeFederativa.descricao }
				</td>
				<th>Data de Expedição:</th>
				<td><ufrn:format type="data" valor="${discenteForm.discente.pessoa.identidade.dataExpedicao }" /></td>
				</tr>
				</table>
			</td>
			</tr>

			<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
				<caption>Naturalidade</caption>
					<tr>
					<th width="20%">País:</th>
					<td width="20%">
					${discenteForm.discente.pessoa.pais.nome}
					</td>
					<th width="3%">UF:</th>
					<td width="5%">
					${discenteForm.discente.pessoa.unidadeFederativa.sigla}
					</td>
					<th width="10%">Município:</th>
					<td>
					${discenteForm.discente.pessoa.municipio.nome}
					</td>
					</tr>
				</table>
			</td>
			</tr>

		<tr>
		<td colspan="4">
			<table width="100%" class="subFormulario">
			<caption>Informações Para Contato</caption>
			<tr>
			<th width="20%">Logradouro:</th>
			<td colspan="3">
			${discenteForm.discente.pessoa.enderecoContato.tipoLogradouro.descricao}&nbsp;&nbsp;
			${discenteForm.discente.pessoa.enderecoContato.logradouro}
			</td>
			<th>
			N.&deg;:
			</th>
			<td>
			${discenteForm.discente.pessoa.enderecoContato.numero}
			</td>
			</tr>

			<tr>
			<th>Bairro:</th>
			<td>
			${discenteForm.discente.pessoa.enderecoContato.bairro}
			</td>
			<th width="20%">Complemento:</th>
			<td>
			${discenteForm.discente.pessoa.enderecoContato.complemento}
			</td>
			<th>CEP:</th>
			<td>
			${discenteForm.discente.pessoa.enderecoContato.cep}
			</td>
			</tr>

			<tr>
			<th>UF:</th>
			<td>
			${discenteForm.discente.pessoa.enderecoContato.unidadeFederativa.sigla}
			</td>
			<th>Município:</th>
			<td colspan="4">
			${discenteForm.discente.pessoa.enderecoContato.municipio.nome}
			</td>
			</tr>

			<tr>
			<th>Tel. Fixo:</th>
			<td>
			(${discenteForm.discente.pessoa.codigoAreaNacionalTelefoneFixo})
			${discenteForm.discente.pessoa.telefone}
			</td>
			<th>Tel. Celular:</th>
			<td colspan="4">
			(${discenteForm.discente.pessoa.codigoAreaNacionalTelefoneCelular})
			${discenteForm.discente.pessoa.celular}
			</td>
			</tr>
			</table>
		</td>
		</tr>
		<tr>
		<td colspan="4">
			<table width="100%" class="subFormulario">
			<caption>Dados Acadêmicos</caption>
			<tr>
				<th width="20%">Unidade Responsável:</th>
				<td colspan="4">${discenteForm.discente.gestoraAcademica }</td>
			</tr>
			<c:if test="${sessionScope.discenteAntigo}">
				<tr>
					<th>Matrícula:</th>
					<td colspan="4">${discenteForm.discente.matricula}</td>
				</tr>
				<tr>
					<th> Ano-Período Inicial: </th>
					<td colspan="4"> ${discenteForm.discente.anoIngresso}.${discenteForm.discente.periodoIngresso} </td>
				</tr>
				<tr>
					<th> Status:</th>
					<td colspan="4">${discenteForm.discente.statusString}</td>
				</tr>
			</c:if>
			<tr>
				<th>Curso:</th>
				<td colspan="4">
				<ufrn:subSistema teste="tecnico">${discenteForm.discente.cursoTecnico.nome}</ufrn:subSistema>
				<ufrn:subSistema teste="lato">${discenteForm.discente.cursoLato.nome}</ufrn:subSistema>
				</td>
			</tr>
			<ufrn:subSistema teste="tecnico">
				<tr>
					<th>Currículo:</th>
					<td colspan="4">${discenteForm.discente.turmaEntradaTecnico.estruturaCurricularTecnica.descricaoResumida}</td>
				</tr>
			</ufrn:subSistema>
			<tr>
				<th>Turma de Entrada:</th>
				<td colspan="4">
				<ufrn:subSistema teste="tecnico">${discenteForm.discente.turmaEntradaTecnico.descricao}</ufrn:subSistema>
				<ufrn:subSistema teste="lato">${discenteForm.discente.turmaEntrada.descricao}</ufrn:subSistema>
				</td>
			</tr>

			<tr>
				<ufrn:subSistema teste="tecnico">
					<th>Forma de Ingresso:</th>
					<td>
					${discenteForm.discente.formaIngresso.descricao}
					</td>
					<th>Regime do Aluno:</th>
					<td>
					${discenteForm.discente.tipoRegimeAluno.descricao}
					</td>
				</ufrn:subSistema>
				<ufrn:subSistema teste="lato">
					<th>Procedência do Aluno:</th>
					<td>
					${discenteForm.discente.tipoProcedenciaAluno.descricao}
					</td>
				</ufrn:subSistema>
			</tr>

			<tr>
				<ufrn:subSistema teste="not tecnico">
				<th>Ano - Período de Ingresso:</th>
				<td>
				${discenteForm.discente.anoIngresso} - ${discenteForm.discente.periodoIngresso}
				</td>
				</ufrn:subSistema>
				<ufrn:subSistema teste="tecnico">
					<th>Concluiu o Ensino Médio?</th>
					<td>
					<ufrn:format type="bool_sn" valor="${discenteForm.discente.concluiuEnsinoMedio}" />
					</td>
				</ufrn:subSistema>
			</tr>

			<tr>
				<th valign="top">Observação:</th>
				<td colspan="4">
				${discenteForm.discente.observacao}
				</td>
			</tr>
 			<c:if test="${sessionScope.discenteAntigo}">
				<tr>
					<th style="height: 35px;"> Arquivo do Histórico Digitalizado (<i>Opcional</i>): </th>
					<td> <html:file property="arquivoHistorico" size="55"></html:file> </td>
				</tr>
			</c:if>

			</table>
		</td>
		</tr>
	</tbody>


	<tfoot>
		<tr>
		<td colspan="8">
   		<html:button dispatch="chamaModelo" value="Confirmar"/>&nbsp;&nbsp;
   		<html:button dispatch="cancelar" cancelar="true" value="Cancelar"/><br><br>
   		<c:if test="${param.dispatch != 'remove'}">
			<html:button view="dadosPessoais">&lt;&lt; Dados Pessoais</html:button>
			<html:button view="dadosDiscente">&lt;&lt; Dados Acadêmicos</html:button>
   		</c:if>
		</td>
		</tr>
	</tfoot>

	</table>

</html:form>

<br><br>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2> <ufrn:subSistema /> > Resumo dos Dados do Aluno</h2>

	<c:if test="${discenteTecnico.obj.id > 0}">
		<c:set value="#{discenteTecnico.obj}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>
	</c:if>
	<table class="visualizacao" width="99%">
	<caption>Confira os dados do aluno antes de confirmar a operação</caption>


	<h:form id="formResumo">
			<c:if test="${discenteTecnico.obj.discente.id > 0}">
			<tr>
			<th width="20%">Matrícula:</th>
			<td colspan="3">
			${discenteTecnico.obj.discente.matricula}
			</td>
			</tr>
			</c:if>

			<tr>
			<th width="20%">Nome:</th>
			<td colspan="3">
			${discenteTecnico.obj.discente.pessoa.nome }
			</td>
			</tr>

			<tr>
			<th>E-mail:</th>
			<td colspan="3">${discenteTecnico.obj.discente.pessoa.email }</td>
			</tr>

			<tr>
			<th>Nome da Mãe:</th>
			<td colspan="3">
			${discenteTecnico.obj.discente.pessoa.nomeMae }
			</td>
			</tr>

			<tr>
			<th>Nome do Pai:</th>
			<td colspan="3">
			${discenteTecnico.obj.discente.pessoa.nomePai }
			</td>
			</tr>

			<tr>
			<th>Data de Nascimento:</th>
			<td width="30%">
			<ufrn:format type="data" valor="${discenteTecnico.obj.discente.pessoa.dataNascimento }" />
			</td>
			<th width="15%">CPF:</th>
			<td>
			<ufrn:format type="cpf_cnpj" valor="${discenteTecnico.obj.discente.pessoa.cpf_cnpj }" />
			</td>
			</tr>

			<tr>
			<th>Estado Civil:</th>
			<td>
			${discenteTecnico.obj.discente.pessoa.estadoCivil.descricao}
			</td>
			<th></th>
			<td>
			</td>
			</tr>

			<tr>
			<th>Sexo: </th>
			<td>
			<ufrn:format type="sexo" valor="${discenteTecnico.obj.discente.pessoa.sexo}" />
			</td>
			<th>Passaporte:</th>
			<td>
			${discenteTecnico.obj.discente.pessoa.passaporte}
			</td>
			</tr>

			<tr>
			<th>Raça:</th>
			<td colspan="3">
			${discenteTecnico.obj.discente.pessoa.tipoRaca.descricao }
			</td>
			</tr>

			<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
				<caption>Documento de Identidade</caption>
				<tr>
				<th width="20%">RG:</th>
				<td width="25%">${discenteTecnico.obj.discente.pessoa.identidade.numero}</td>
				<th width="20%">Órgão de Expedição:</th>
				<td>${discenteTecnico.obj.discente.pessoa.identidade.orgaoExpedicao }</td>
				</tr>

				<tr>
				<th>UF:</th>
				<td>
				${discenteTecnico.obj.discente.pessoa.identidade.unidadeFederativa.descricao }
				</td>
				<th>Data de Expedição:</th>
				<td><ufrn:format type="data" valor="${discenteTecnico.obj.discente.pessoa.identidade.dataExpedicao }" /></td>
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
					${discenteTecnico.obj.discente.pessoa.pais.nome}
					</td>
					<th width="3%">UF:</th>
					<td width="5%">
					${discenteTecnico.obj.discente.pessoa.unidadeFederativa.sigla}
					</td>
					<th width="10%">Município:</th>
					<td>
					${discenteTecnico.obj.discente.pessoa.municipio.nome}
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
			${discenteTecnico.obj.discente.pessoa.enderecoContato.tipoLogradouro.descricao}&nbsp;&nbsp;
			${discenteTecnico.obj.discente.pessoa.enderecoContato.logradouro}
			</td>
			<th>
			N.&deg;:
			</th>
			<td>
			${discenteTecnico.obj.discente.pessoa.enderecoContato.numero}
			</td>
			</tr>

			<tr>
			<th>Bairro:</th>
			<td>
			${discenteTecnico.obj.discente.pessoa.enderecoContato.bairro}
			</td>
			<th width="20%">Complemento:</th>
			<td>
			${discenteTecnico.obj.discente.pessoa.enderecoContato.complemento}
			</td>
			<th>CEP:</th>
			<td>
			${discenteTecnico.obj.discente.pessoa.enderecoContato.cep}
			</td>
			</tr>

			<tr>
			<th>UF:</th>
			<td>
			${discenteTecnico.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}
			</td>
			<th>Município:</th>
			<td colspan="4">
			${discenteTecnico.obj.discente.pessoa.enderecoContato.municipio.nome}
			</td>
			</tr>

			<tr>
			<th>Tel. Fixo:</th>
			<td>
			(${discenteTecnico.obj.discente.pessoa.codigoAreaNacionalTelefoneFixo})
			${discenteTecnico.obj.discente.pessoa.telefone}
			</td>
			<th>Tel. Celular:</th>
			<td colspan="4">
			(${discenteTecnico.obj.discente.pessoa.codigoAreaNacionalTelefoneCelular})
			${discenteTecnico.obj.discente.pessoa.celular}
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
				<td colspan="4">${discenteTecnico.obj.discente.gestoraAcademica }</td>
			</tr>
			<c:if test="${sessionScope.discenteAntigo}">
				<tr>
					<th>Matrícula:</th>
					<td colspan="4">${discenteTecnico.obj.discente.matricula}</td>
				</tr>
				<tr>
					<th> Ano-Período Inicial: </th>
					<td colspan="4"> ${discenteTecnico.obj.discente.anoIngresso}.${discenteTecnico.obj.discente.periodoIngresso} </td>
				</tr>
				<tr>
					<th> Status:</th>
					<td colspan="4">${discenteTecnico.obj.discente.statusString}</td>
				</tr>
			</c:if>
			<tr>
				<th>Curso:</th>
				<td colspan="4">
				<h:outputText rendered="true" value="#{discenteTecnico.obj.cursoTecnico.nome}"/>
				<h:outputText rendered="false" value="#{discenteLato.obj.cursoLato.nome}"/>
				</td>
			</tr>
			<tr>
				<th>Currículo:</th>
				<td colspan="4">${discenteTecnico.obj.estruturaCurricularTecnica.descricaoResumida}</td>
			</tr>
	
			<tr>
				<th>Turma de Entrada:</th>
				<td colspan="4">
				<h:outputText rendered="true" value="#{discenteTecnico.obj.turmaEntradaTecnico.descricao}"/>
				<h:outputText rendered="false" value="#{discenteLato.obj.turmaEntrada.descricao}"/>
				</td>
			</tr>

			<tr>
				<th>Forma de Ingresso:</th>
				<td>
					${discenteTecnico.obj.discente.formaIngresso.descricao}
				</td>
				<th>Regime do Aluno:</th>
				<td>
					${discenteTecnico.obj.tipoRegimeAluno.descricao}
				</td>

			</tr>

			<tr>
				<th>Concluiu o Ensino Médio?</th>
				<td>
					<ufrn:format type="bool_sn" valor="${discenteTecnico.obj.concluiuEnsinoMedio}" />
				</td>

			</tr>

			<tr>
				<th valign="top">Observação:</th>
				<td colspan="4">
				${discenteTecnico.obj.discente.observacao}
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
			<td colspan="20">
		   		<h:commandButton value="Confirmar" id="Confirmar" action="#{ discenteTecnico.cadastrar }" rendered="#{!acesso.pedagogico }"/>
		   		<c:if test="${discenteTecnico.obj.id <= 0}">
					<h:commandButton value="<< Dados Pessoais" id="DadosPessoais" action="#{ discenteTecnico.telaDadosPessoais}"/>
				</c:if>
				<h:commandButton value="<< Dados Acadêmicos" id="DadosDiscente" action="#{ discenteTecnico.submeterDadosPessoais}" rendered="#{!acesso.pedagogico}"/>
				<h:commandButton value="<< Voltar" action="#{discenteTecnico.telaBuscaDiscentes}"/>
		   		<h:commandButton value="Cancelar" id="Cancelar" action="#{ discenteTecnico.cancelar }" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>

	</table>
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2> <ufrn:subSistema /> > Resumo dos Dados do Aluno</h2>

	<c:if test="${discenteLato.obj.id > 0}">
		<c:set value="#{discente}" var="discenteMBean" />
		<c:set value="#{discenteLato.obj}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>
		<c:set value="#{discenteMBean}" var="discente" />
	</c:if>
	<table class="formulario" width="99%">
	<caption>Confira os dados do aluno antes de confirmar a operação</caption>


	<h:form id="formResumo">
			<c:if test="${discenteLato.obj.discente.id > 0}">
			<tr>
			<th width="20%"><b>Matrícula:</b></th>
			<td colspan="3">
			${discenteLato.obj.discente.matricula}
			</td>
			</tr>
			</c:if>

			<tr>
			<th width="20%"><b>Nome:</b></th>
			<td colspan="3">
			${discenteLato.obj.discente.pessoa.nome }
			</td>
			</tr>

			<tr>
			<th><b>E-mail:</b></th>
			<td colspan="3">${discenteLato.obj.discente.pessoa.email }</td>
			</tr>

			<tr>
			<th><b>Nome da Mãe:</b></th>
			<td colspan="3">
			${discenteLato.obj.discente.pessoa.nomeMae }
			</td>
			</tr>

			<tr>
			<th><b>Nome do Pai:</b></th>
			<td colspan="3">
			${discenteLato.obj.discente.pessoa.nomePai }
			</td>
			</tr>

			<tr>
			<th><b>Data de Nascimento:</b></th>
			<td width="30%">
			<ufrn:format type="data" valor="${discenteLato.obj.discente.pessoa.dataNascimento }" />
			</td>
			<th width="15%"><b>CPF:</b></th>
			<td>
			<ufrn:format type="cpf_cnpj" valor="${discenteLato.obj.discente.pessoa.cpf_cnpj }" />
			</td>
			</tr>

			<tr>
			<th><b>Estado Civil:</b></th>
			<td>
			${discenteLato.obj.discente.pessoa.estadoCivil.descricao}
			</td>
			<th></th>
			<td>
			</td>
			</tr>

			<tr>
			<th><b>Sexo:</b></th>
			<td>
			<ufrn:format type="sexo" valor="${discenteLato.obj.discente.pessoa.sexo}" />
			</td>
			<th><b>Passaporte:</b></th>
			<td>
			${discenteLato.obj.discente.pessoa.passaporte}
			</td>
			</tr>

			<tr>
			<th><b>Raça:</b></th>
			<td colspan="3">
			${discenteLato.obj.discente.pessoa.tipoRaca.descricao }
			</td>
			</tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
				<caption>Documento de Identidade</caption>
				<tr>
				<th width="20%"><b>RG:</b></th>
				<td width="25%">${discenteLato.obj.discente.pessoa.identidade.numero}</td>
				<th width="20%"><b>Órgão de Expedição:</b></th>
				<td>${discenteLato.obj.discente.pessoa.identidade.orgaoExpedicao }</td>
				</tr>
				<tr>
				<th><b>UF:</b></th>
				<td>
				${discenteLato.obj.discente.pessoa.identidade.unidadeFederativa.descricao }
				</td>
				<th><b>Data de Expedição:</b></th>
				<td><ufrn:format type="data" valor="${discenteLato.obj.discente.pessoa.identidade.dataExpedicao }" /></td>
				</tr>
				</table>
			</td>
			</tr>

			<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
				<caption>Naturalidade</caption>
					<tr>
					<th width="20%"><b>País:</b></th>
					<td width="20%">
					${discenteLato.obj.discente.pessoa.pais.nome}
					</td>
					<th width="3%"><b>UF:</b></th>
					<td width="5%">
					${discenteLato.obj.discente.pessoa.unidadeFederativa.sigla}
					</td>
					<th width="10%"><b>Município:</b></th>
					<td>
					${discenteLato.obj.discente.pessoa.municipio.nome}
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
			<th width="20%"><b>Logradouro:</b></th>
			<td colspan="3">
				${discenteLato.obj.discente.pessoa.enderecoContato.tipoLogradouro.descricao}&nbsp;&nbsp;
				${discenteLato.obj.discente.pessoa.enderecoContato.logradouro}
			</td>
			</tr>
			<tr>
			<th><b>N.&deg;:</b></th>
			<td width="30%">
			${discenteLato.obj.discente.pessoa.enderecoContato.numero}
			</td>
			<th width="15%"><b>Bairro:</b></th>
			<td>
			${discenteLato.obj.discente.pessoa.enderecoContato.bairro}
			</td>
			</tr>

			<tr>
			<th width="20%"><b>Complemento:</b></th>
			<td>
			${discenteLato.obj.discente.pessoa.enderecoContato.complemento}
			</td>
			<th><b>CEP:</b></th>
			<td>
			${discenteLato.obj.discente.pessoa.enderecoContato.cep}
			</td>
			</tr>

			<tr>
			<th><b>UF:</b></th>
			<td>
			${discenteLato.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}
			</td>
			<th><b>Município:</b></th>
			<td colspan="4">
			${discenteLato.obj.discente.pessoa.enderecoContato.municipio.nome}
			</td>
			</tr>

			<tr>
			<th><b>Tel. Fixo:</b></th>
			<td>
			(${discenteLato.obj.discente.pessoa.codigoAreaNacionalTelefoneFixo})
			${discenteLato.obj.discente.pessoa.telefone}
			</td>
			<th><b>Tel. Celular:</b></th>
			<td colspan="4">
			(${discenteLato.obj.discente.pessoa.codigoAreaNacionalTelefoneCelular})
			${discenteLato.obj.discente.pessoa.celular}
			</td>
			</tr>
			</table>
		</td>
		</tr>
		
		<tr>
			<td colspan="4"> 
			<table width="100%" class="subFormulario">
				<caption>Dados Bancários</caption>
				<c:if test="${ discenteLato.obj.discente.pessoa.contaBancaria.banco.id == 0}">
				<tr>
				<th width="20%"><b>Banco:</b></th>
					<td colspan="3">Não informado</td>
				</tr>
				<tr>
				<th><b>Conta:</b></th>
					<td colspan="3">Não informado</td>
				</tr>				
				<tr>
				<th><b>Agência:</b></th>
					<td colspan="3">Não informado</td>
				</tr>
				</c:if>
				<c:if test="${ discenteLato.obj.discente.pessoa.contaBancaria.banco.id > 0}">
					<tr>
						<th width="20%"><b>Banco:</b></th>
						<td colspan="3"> ${discenteLato.obj.discente.pessoa.contaBancaria.banco.codigoNome} </td>
					</tr>
					<tr>
						<th><b>Conta:</b></th>
						<td colspan="3"> ${discenteLato.obj.discente.pessoa.contaBancaria.numero} </td>
					</tr>
					<tr>
						<th><b>Agência:</b></th>
						<td colspan="3"> ${discenteLato.obj.discente.pessoa.contaBancaria.agencia} </td>					
					</tr>
					<c:if test="${discenteLato.obj.discente.pessoa.contaBancaria.operacao != null}">
					<tr>
						<th><b>Operação</b></th>
						<td colspan="3"> ${discenteLato.obj.discente.pessoa.contaBancaria.operacao} </td>					
					</tr>
					</c:if>	
				</c:if>			
			</table>
			</td>
		</tr>
		
		
		<tr>
		<td colspan="4">
			<table width="100%" class="subFormulario">
			<caption>Dados Acadêmicos</caption>
			<tr>
				<th width="20%"><b>Unidade Responsável:</b></th>
				<td colspan="4">${discenteLato.obj.discente.gestoraAcademica }</td>
			</tr>
			
			<c:if test="${discenteLato.discenteAntigo}">
				<tr>
					<th><b>Matrícula:</b></th>
					<td colspan="4"> <h:outputText rendered="true" value="#{discenteLato.obj.discente.matricula}"/></td>
				</tr>
			</c:if>
			<tr>
				<th><b>Ano-Período Inicial:</b></th>
				<td colspan="4"> ${discenteLato.obj.discente.anoIngresso}.${discenteLato.obj.discente.periodoIngresso} </td>
			</tr>

			<c:if test="${discenteLato.discenteAntigo}">	
				<tr>
					<th><b>Status:</b></th>
					<td colspan="4"><h:outputText rendered="true" value="#{discenteLato.obj.discente.statusString}"/></td>
				</tr>
			</c:if>
			
			<tr>
				<th><b>Curso:</b></th>
				<td colspan="4">
				<h:outputText rendered="true" value="#{discenteLato.obj.turmaEntrada.cursoLato.nome}"/>
				</td>
			</tr>
			<tr>
				<th><b>Turma de Entrada:</b></th>
				<td colspan="4">
				<h:outputText rendered="true" value="#{discenteLato.obj.turmaEntrada.descricao}"/>
				</td>
			</tr>
			<tr>
				<th><b>Procedência do Aluno:</b></th>
				<td>
					${discenteLato.obj.tipoProcedenciaAluno.descricao}
				</td>
			</tr>
			<tr>
				<th valign="top"><b>Observação:</b></th>
				<td colspan="4">
				${discenteLato.obj.discente.observacao}
				</td>
			</tr>
			</table>
		</td>
		</tr>
	</tbody>


	<tfoot>
		<tr>
		<td colspan="8" tyle="padding: 3px 0px 2px;">
			<h:commandButton value="Confirmar" id="Cadastrar" action="#{ discenteLato.cadastrar }" />
	   		<h:commandButton value="<< Dados Pessoais" id="DadosPessoais" action="#{ discenteLato.telaDadosPessoais}"/>
			<h:commandButton value="<< Dados Acadêmicos" id="DadosDiscente" action="#{ discenteLato.submeterDadosPessoais}"/>	
	   		<h:commandButton value="Cancelar" id="Cancelar" action="#{ discenteLato.cancelar }" onclick="#{confirm}"/>
		</td>
		</tr>
		
		
	</tfoot>
	</table>
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
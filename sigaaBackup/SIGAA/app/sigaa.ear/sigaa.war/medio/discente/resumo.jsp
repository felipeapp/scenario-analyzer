<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> > Resumo dos Dados do Aluno</h2>

	<table class="visualizacao" width="99%">
	<caption>Confira os dados do aluno antes de confirmar a operação</caption>

	<h:form id="formResumo">
		<c:if test="${discenteMedio.obj.discente.id > 0}">
			<tr>
				<th width="20%">Matrícula:</th>
				<td colspan="3">
					${discenteMedio.obj.discente.matricula}
				</td>
			</tr>
		</c:if>

		<tr>
			<th width="20%">Nome:</th>
			<td colspan="3">
				${discenteMedio.obj.discente.pessoa.nome }
			</td>
		</tr>

		<tr>
			<th>E-mail:</th>
			<td colspan="3">
				${discenteMedio.obj.discente.pessoa.email }
			</td>
		</tr>

		<tr>
			<th>Nome da Mãe:</th>
			<td colspan="3">
				${discenteMedio.obj.discente.pessoa.nomeMae }
			</td>
		</tr>

		<tr>
			<th>Nome do Pai:</th>
			<td colspan="3">
				${discenteMedio.obj.discente.pessoa.nomePai }
			</td>
		</tr>

		<tr>
			<th>Data de Nascimento:</th>
			<td width="30%">
				<ufrn:format type="data" valor="${discenteMedio.obj.discente.pessoa.dataNascimento }" />
			</td>
			<th width="15%">CPF:</th>
			<td>
				<ufrn:format type="cpf_cnpj" valor="${discenteMedio.obj.discente.pessoa.cpf_cnpj }" />
			</td>
		</tr>

		<tr>
			<th>Estado Civil:</th>
			<td>
				${discenteMedio.obj.discente.pessoa.estadoCivil.descricao}
			</td>
			<th></th>
			<td>
			</td>
		</tr>

		<tr>
			<th>Sexo: </th>
			<td colspan="${discenteMedio.obj.discente.pessoa.internacional ? '1' : '3' }">
				<ufrn:format type="sexo" valor="${discenteMedio.obj.discente.pessoa.sexo}" />
			</td>
			<c:if test="${discenteMedio.obj.discente.pessoa.internacional}">
				<th>Passaporte:</th>
				<td>
					${discenteMedio.obj.discente.pessoa.passaporte}
				</td>			
			</c:if>
		</tr>

		<tr>
			<th>Raça:</th>
			<td colspan="3">
				${discenteMedio.obj.discente.pessoa.tipoRaca.descricao }
			</td>
		</tr>

		<tr>
		<td colspan="4">
			<table width="100%" class="subFormulario">
			<caption>Documento de Identidade</caption>
			<tr>
				<th width="20%">RG:</th>
				<td width="25%">${discenteMedio.obj.discente.pessoa.identidade.numero}</td>
				<th width="20%">Órgão de Expedição:</th>
				<td>${discenteMedio.obj.discente.pessoa.identidade.orgaoExpedicao }</td>
			</tr>

			<tr>
				<th>UF:</th>
				<td>
				${discenteMedio.obj.discente.pessoa.identidade.unidadeFederativa.descricao }
				</td>
				<th>Data de Expedição:</th>
				<td><ufrn:format type="data" valor="${discenteMedio.obj.discente.pessoa.identidade.dataExpedicao }" /></td>
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
						${discenteMedio.obj.discente.pessoa.pais.nome}
					</td>
					<th width="3%">UF:</th>
					<td width="5%">
						${discenteMedio.obj.discente.pessoa.unidadeFederativa.sigla}
					</td>
					<th width="10%">Município:</th>
					<td>
						${discenteMedio.obj.discente.pessoa.municipio.nome}
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
				${discenteMedio.obj.discente.pessoa.enderecoContato.tipoLogradouro.descricao}&nbsp;&nbsp;
				${discenteMedio.obj.discente.pessoa.enderecoContato.logradouro}
			</td>
			<th>
			N.&deg;:
			</th>
			<td>
				${discenteMedio.obj.discente.pessoa.enderecoContato.numero}
			</td>
			</tr>

			<tr>
			<th>Bairro:</th>
			<td>
				${discenteMedio.obj.discente.pessoa.enderecoContato.bairro}
			</td>
			<th width="20%">Complemento:</th>
			<td>
				${discenteMedio.obj.discente.pessoa.enderecoContato.complemento}
			</td>
			<th>CEP:</th>
			<td>
				${discenteMedio.obj.discente.pessoa.enderecoContato.cep}
			</td>
			</tr>

			<tr>
				<th>UF:</th>
				<td>
					${discenteMedio.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}
				</td>
				<th>Município:</th>
				<td colspan="4">
					${discenteMedio.obj.discente.pessoa.enderecoContato.municipio.nome}
				</td>
			</tr>

			<tr>
				<th>Tel. Fixo:</th>
				<td>
					(${discenteMedio.obj.discente.pessoa.codigoAreaNacionalTelefoneFixo})
					${discenteMedio.obj.discente.pessoa.telefone}
				</td>
				<th>Tel. Celular:</th>
				<td colspan="4">
					(${discenteMedio.obj.discente.pessoa.codigoAreaNacionalTelefoneCelular})
					${discenteMedio.obj.discente.pessoa.celular}
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
				<td colspan="4">${discenteMedio.obj.discente.gestoraAcademica }</td>
			</tr>
			<c:if test="${sessionScope.discenteAntigo}">
				<tr>
					<th>Matrícula:</th>
					<td colspan="4">${discenteMedio.obj.discente.matricula}</td>
				</tr>
				<tr>
					<th> Ano de Igresso: </th>
					<td colspan="4"> ${discenteMedio.obj.discente.anoIngresso}</td>
				</tr>
				<tr>
					<th> Status:</th>
					<td colspan="4">${discenteMedio.obj.discente.statusString}</td>
				</tr>
			</c:if>
			<tr>
				<th>Curso de Ingresso:</th>
				<td colspan="4">
					<h:outputText value="#{discenteMedio.obj.curso.nome}"/>
				</td>
			</tr>
			<tr>
				<th>Série de Ingresso:</th>
				<td colspan="4">
					<h:outputText value="#{discenteMedio.obj.serieIngresso.descricao}"/>
				</td>
			</tr>		
			
			<tr>
				<th>Escola Anterior:</th>
				<td colspan="4">
					<h:outputText value="#{discenteMedio.obj.escolaAnterior}"/>
				</td>
			</tr>	
			
			<tr>
				<th>Opção de Turno:</th>
				<td colspan="4">
					<h:outputText value="#{discenteMedio.obj.opcaoTurno.descricao}"/>
				</td>
			</tr>				

			<tr>
				<th>Forma de Ingresso:</th>
				<td colspan="4">
					${discenteMedio.obj.discente.formaIngresso.descricao}
				</td>

			</tr>

			<tr>
				<th>Participa do Bolsa Família?</th>
				<td colspan="4">
					<ufrn:format type="bool_sn" valor="${discenteMedio.obj.participaBolsaFamilia}" />
				</td>
			</tr>
			<tr>
				<th>Utiliza Transporte Escolar Público?</th>
				<td colspan="4">
					<ufrn:format type="bool_sn" valor="${discenteMedio.obj.utilizaTransporteEscolarPublico}" />
				</td>
			</tr>

			</table>
		</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="4">
			   		<h:commandButton value="Confirmar" id="Confirmar" action="#{ discenteMedio.cadastrar }" rendered="#{!acesso.pedagogico }"/>
					<h:commandButton value="<< Dados Pessoais" action="#{ dadosPessoais.voltarDadosPessoais}" 
						id="btnVoltar" rendered="#{discenteMedio.obj.id == 0}"/>
					<h:commandButton value="<< Dados Acadêmicos" id="DadosDiscente" action="#{ discenteMedio.submeterDadosPessoais}" rendered="#{!acesso.pedagogico}"/>
					<h:commandButton value="<< Voltar" action="#{discenteMedio.telaBuscaDiscentes}" rendered="#{acesso.pedagogico}"/>
			   		<h:commandButton value="Cancelar" id="Cancelar" action="#{ discenteMedio.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>

	</table>
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
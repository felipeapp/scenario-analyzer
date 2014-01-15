<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<style>
th.rotulo{ text-align: right; font-weight: bold; }
</style>
<f:view>
	<h2>Confirmação das Informações</h2>
	<div class="descricaoOperacao">
		<b>Atenção: </b>verifique corretamente os dados abaixo. Serão de inteira responsabilidade do candidato os prejuízos advindos de informações incorretas.<br/>
		<c:if test="${acompanhamentoVestibular.obj.tipoNecessidadeEspecial.id != 0}">
			Caso necessite, verifique o Edital do Processo Seletivo como solicitar condições especiais de realização de provas.
		</c:if>
	</div>
	<h:form id="form">
		<table class="formulario" width="95%">
			<caption>Confirme os Dados Pessoais</caption>
			<tbody>
				<tr>
					<td colspan="6" class="subFormulario">Dados Pessoais</td>
				</tr>
				<tr>
					<th class="rotulo" >CPF:</th>
					<td colspan="4">
						<ufrn:format type="cpf_cnpj" valor="${acompanhamentoVestibular.obj.cpf_cnpj}"></ufrn:format>
					</td>
					<td rowspan="8" valign="top" style="text-align: right;">
						<c:if test="${not empty acompanhamentoVestibular.arquivoUpload}">
							<rich:paint2D id="painter" width="150" height="200" paint="#{acompanhamentoVestibular.imagemFoto}" />
						</c:if>
						<c:if test="${empty acompanhamentoVestibular.arquivoUpload and not empty acompanhamentoVestibular.obj.idFoto}">
							<img src="${ctx}/verFoto?idArquivo=${acompanhamentoVestibular.obj.idFoto}&key=${ sf:generateArquivoKey(acompanhamentoVestibular.obj.idFoto) }" style="width: 150px; height: 200px"/>
						</c:if>
					</td>
				</tr>
				<c:if test="${not empty acompanhamentoVestibular.obj.passaporte}">
					<tr>
						<th class="rotulo">Passaporte:</th>
						<td colspan="5"><h:outputText
							value="#{acompanhamentoVestibular.obj.passaporte}" /></td>
					</tr>
				</c:if>
				<tr>
					<th class="rotulo">Nome:</th>
					<td colspan="4"><h:outputText
						value="#{acompanhamentoVestibular.obj.nome}" /></td>
				</tr>
				<tr>
					<th class="rotulo">E-Mail:</th>
					<td colspan="4"><h:outputText
						value="#{acompanhamentoVestibular.obj.email}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Nome da Mãe:</th>
					<td colspan="4"><h:outputText
						value="#{acompanhamentoVestibular.obj.nomeMae}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Nome do Pai:</th>
					<td colspan="4"><h:outputText
						value="#{acompanhamentoVestibular.obj.nomePai}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Sexo:</th>
					<td><c:if
						test="${acompanhamentoVestibular.obj.sexo == 'F'}">
						<h:outputText value="Feminino" />
					</c:if><c:if test="${acompanhamentoVestibular.obj.sexo == 'M'}">
						<h:outputText value="Masculino" />
					</c:if></td>
					<th class="rotulo">Data de Nascimento:</th>
					<td colspan="2"><t:outputText
						value="#{acompanhamentoVestibular.obj.dataNascimento}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Estado Civil:</th>
					<td><h:outputText
						value="#{acompanhamentoVestibular.obj.estadoCivil.descricao}" /></td>
					<th class="rotulo">Raça:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.tipoRaca.descricao}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Tipo de Necessidade Especial:</th>
					<td colspan="4">
						<h:outputText value="#{acompanhamentoVestibular.obj.tipoNecessidadeEspecial.descricao}"rendered="#{not empty acompanhamentoVestibular.obj.tipoNecessidadeEspecial.descricao}" />
						<h:outputText value="Não informado" rendered="#{empty acompanhamentoVestibular.obj.tipoNecessidadeEspecial.descricao}" />
					</td>
				</tr>

				<tr>
					<td colspan="6" class="subFormulario">Escola de Conclusão do Ensino Médio</td>
				</tr>
				<tr>
					<th class="rotulo">Escola de Conclusão do EM:</th>
					<td>
						<h:outputText value="#{acompanhamentoVestibular.obj.nomeEscolaConclusaoEnsinoMedio}" />
					</td>
					<th class="rotulo">Ano de Conclusão:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.anoConclusaoEnsinoMedio}" /></td>
				</tr>
				
				<!-- ============= NATURALIDADE  -->
				<tr>
					<td colspan="6" class="subFormulario">Naturalidade</td>
				</tr>
				<tr>
					<th class="rotulo">País:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.pais.nome}" /></td>
					<th class="rotulo">Nacionalidade:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.pais.nacionalidade}" /></td>
				</tr>
				<tr>
					<th class="rotulo">
						<c:if test="${acompanhamentoVestibular.brasil}">UF:</c:if>
					</th>
					<td colspan="2"><h:outputText rendered="#{acompanhamentoVestibular.brasil}"
						value="#{acompanhamentoVestibular.obj.unidadeFederativa.descricao}" /></td>
					<th class="rotulo">Município:</th>
					<td colspan="5">
						<h:outputText rendered="#{acompanhamentoVestibular.brasil}"
								value="#{acompanhamentoVestibular.obj.municipio.nome}" />
						<h:outputText rendered="#{!acompanhamentoVestibular.brasil}"
								value="#{acompanhamentoVestibular.obj.municipioNaturalidadeOutro}" />
					</td>
				</tr>

				<!-- ============= DOCUMENTAÇÃO  -->
				<tr>
					<td colspan="6" class="subFormulario">Documento de Identidade</td>
				</tr>
				<tr>
					<th class="rotulo">Nº do Doc. de Identificação:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.identidade.numero}" /></td>
					<th class="rotulo">Órgão de Expedição:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.identidade.orgaoExpedicao}" /></td>
				</tr>
				<tr>
					<th class="rotulo">UF:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.identidade.unidadeFederativa.descricao}" /></td>
					<th class="rotulo">Data de Expedição:</th>
					<td colspan="2"><t:outputText
						value="#{acompanhamentoVestibular.obj.identidade.dataExpedicao}" /></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Título de Eleitor</td>
				</tr>
				<tr>
					<th class="rotulo">Nº do Título:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.tituloEleitor.numero}" /></td>
					<th class="rotulo">Zona:</th> 
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.tituloEleitor.zona}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Seção:</th>
					<td colspan="2"><h:outputText value="#{acompanhamentoVestibular.obj.tituloEleitor.secao}" /></td>
					<th class="rotulo">UF:</th>
					<td colspan="2"> <h:outputText value="#{acompanhamentoVestibular.obj.tituloEleitor.unidadeFederativa.descricao}" /></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Certificado Militar</td>
				</tr>
				<tr>
					<th class="rotulo"> Nº do Certificado Militar:</th>
					<td> <h:outputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.numero }" /> </td>
					<th class="rotulo"> Data de Expedição: </th>
					<td><h:outputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.dataExpedicao }" /></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th class="rotulo"> Série:</th>
					<td> <h:outputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.serie }" /> </td>
					<th class="rotulo"> Categoria: </th>
					<td> <h:outputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.categoria }" /> </td>
					<th class="rotulo"> Órgão:</th>
					<td> <h:outputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.orgaoExpedicao }"/></td>
				</tr>

				<!-- ============= ENDEREÇO DE CONTATO -->
				<tr>
					<td colspan="6" class="subFormulario">Informações Para Contato</td>
				</tr>
				<tr class="linhaCep">
					<th class="rotulo">CEP:</th>
					<td colspan="5"><h:outputText
						value="#{acompanhamentoVestibular.obj.enderecoContato.cep}" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Logradouro:</th>
					<td colspan="2">
						<h:outputText value="#{acompanhamentoVestibular.obj.enderecoContato.tipoLogradouro} " />  
						<h:outputText value="#{acompanhamentoVestibular.obj.enderecoContato.logradouro }" />
					</td>
					<th class="rotulo">N&deg;:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.enderecoContato.numero}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Bairro:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.enderecoContato.bairro}" /></td>
					<th class="rotulo">Complemento:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.enderecoContato.complemento}" /></td>
				</tr>
				<tr>
					<th class="rotulo">UF:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.enderecoContato.unidadeFederativa.descricao}" /></td>
					<th class="rotulo">Município:</th>
					<td colspan="2"><h:outputText
						value="#{acompanhamentoVestibular.obj.enderecoContato.municipio.nome}" /></td>
				</tr>
				<tr>
					<th class="rotulo">Tel. Fixo:</th>
					<td colspan="2">(<h:outputText
						value="#{acompanhamentoVestibular.obj.codigoAreaNacionalTelefoneFixo}" />)
					<h:outputText
						value="#{acompanhamentoVestibular.obj.telefone}" /></td>
					<th class="rotulo">Tel. Celular:</th>
					<td colspan="2">(<h:outputText
						value="#{acompanhamentoVestibular.obj.codigoAreaNacionalTelefoneCelular}" />)
					<h:outputText
						value="#{acompanhamentoVestibular.obj.celular}" /></td>
				</tr>
				<tr>
				<td colspan="6"></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6" align="center">
						<h:commandButton value="Confimar o Cadastro" action="#{acompanhamentoVestibular.cadastrar}" rendered="#{acompanhamentoVestibular.alterarDadosPessoais}" id="cadastrar"/>&ensp;
						<h:commandButton value="<< Alterar Dados Pessoais" action="#{acompanhamentoVestibular.formDadosPessoais}" id="alterarDadosPessoais" rendered="#{!acompanhamentoVestibular.novoCadastro || acompanhamentoVestibular.alterarDadosPessoais}" />&ensp;
						<h:commandButton value="<< Alterar Dados Pessoais" action="#{acompanhamentoVestibular.formDadosPessoais}" id="alterarDadosPessoaisCadastro" rendered="#{acompanhamentoVestibular.novoCadastro && !acompanhamentoVestibular.alterarDadosPessoais}" />&ensp;
						<h:commandButton value="<< Alterar Foto" action="#{acompanhamentoVestibular.formUploadFoto}" id="alterarFoto"/>&ensp;
						<h:commandButton value="<< Alterar Senha" action="#{acompanhamentoVestibular.formSenha}" rendered="#{acompanhamentoVestibular.novoCadastro}" id="alterarSenha"/>&ensp;
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{acompanhamentoVestibular.cancelar}" id="cancelar"/>&ensp;
						<h:commandButton value="Próximo Passo >>" action="#{acompanhamentoVestibular.cadastrar}" rendered="#{acompanhamentoVestibular.novaInscricao || acompanhamentoVestibular.novoCadastro}" id="cadastrarBotao2"/> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/public/include/rodape.jsp"%>
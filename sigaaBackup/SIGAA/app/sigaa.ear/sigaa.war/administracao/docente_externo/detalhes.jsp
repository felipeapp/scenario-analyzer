<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<style>
<!--
table.formulario th {font-weight: bold;}
-->
</style>
<f:view>
	<h2><ufrn:subSistema /> > Docente Externo</h2>
	<h:outputText value="#{docenteExterno.create}" />
	<h:messages showDetail="true" />
	<table class="formulario" width="700">
		<h:form id="form">
			<caption class="listagem">Detalhes de Docente Externo</caption>
			<h:inputHidden value="#{docenteExterno.confirmButton}" />
			<h:inputHidden value="#{docenteExterno.obj.id}" />
			<tbody>
				<!-- dados pessoais -->
				<tr>
					<th>Nome:</th>
					<td colspan="3"><h:outputText value="#{docenteExterno.obj.pessoa.nome}" /></td>
				</tr>
				<tr>
					<th>Email:</th>
					<td colspan="3"><h:outputText value="#{docenteExterno.obj.pessoa.email}" /></td>
				</tr>
				<tr>
					<th>Nome da Mãe:</th>
					<td colspan="3"><h:outputText value="#{docenteExterno.obj.pessoa.nomeMae}" /></td>
				</tr>
				<tr>
					<th>Nome do Pai:</th>
					<td colspan="3"><h:outputText value="#{docenteExterno.obj.pessoa.nomePai}" /></td>
				</tr>
				<tr>
					<th>Data de Nascimento:</th>
					<td width="180"><ufrn:format type="data"
						valor="${docenteExterno.obj.pessoa.dataNascimento}" /></td>
					<th width="140">CPF:</th>
					<td><ufrn:format type="cpf_cnpj" valor="${docenteExterno.obj.pessoa.cpf_cnpj}" /></td>
				</tr>

				<tr>
					<th>Passaporte:</th>
					<td colspan="3"><h:outputText value="#{docenteExterno.obj.pessoa.passaporte}" /></td>
				</tr>
				<tr>
					<th>Estado Civil:</th>
					<td><h:outputText value="#{docenteExterno.obj.pessoa.estadoCivil.descricao}" /></td>
					<th>Raça:</th>
					<td><h:outputText value="#{docenteExterno.obj.pessoa.tipoRaca.descricao}" /></td>
				</tr>

				<tr>
					<th>Sexo:</th>
					<td><ufrn:format type="sexo" valor="${docenteExterno.obj.pessoa.sexo}" /></td>
					</td>
				</tr>
				<!-- formação -->
				<tr>
					<th>Formação:</th>
					<td colspan="3"><h:outputText value="#{docenteExterno.obj.formacao.denominacao}" /></td>
				</tr>

				<!-- instituição de ensino -->
				<tr>
					<th>Instituição de Ensino:</th>
					<td colspan="3"><h:outputText value="#{docenteExterno.obj.instituicao.nome}" /></td>
				</tr>


				<tr>
					<td colspan="4">
					<table width="100%" class="subFormulario">
						<caption>Documento de Identidade</caption>
						<tr>
							<th width="100">RG:</th>
							<td width="150"><h:outputText value="#{docenteExterno.obj.pessoa.identidade.numero}" /></td>
							<th width="150" >Órgão de Expedição:</th>
							<td><h:outputText
								value="#{docenteExterno.obj.pessoa.identidade.orgaoExpedicao}" /></td>
						</tr>

						<tr>
							<th>UF:</th>
							<td><h:outputText
								value="#{docenteExterno.obj.pessoa.identidade.unidadeFederativa.descricao}" /></td>
							<th>Data de Expedição:</th>
							<td><ufrn:format type="data" valor="${docenteExterno.obj.pessoa.identidade.dataExpedicao}" /></td>
						</tr>
					</table>
					</td>
				</tr>

				<tr>
					<td colspan="4">
					<table width="100%" class="subFormulario">
						<caption>Naturalidade</caption>
						<tr>
							<th width="100">País:</th>
							<td width="150"><h:outputText value="#{docenteExterno.obj.pessoa.pais.nome}"/></td>
							<th width="150">UF:</th>
							<td ><h:outputText value="#{docenteExterno.obj.pessoa.unidadeFederativa.descricao}"/></td>
						</tr>

						<tr>
							<th>Município:</th>
							<td colspan="3"><h:outputText value="#{docenteExterno.obj.pessoa.municipio.nome}"/></td>
						</tr>

					</table>
					</td>
				</tr>

				<tr>
					<td colspan="4">
					<table width="100%" class="subFormulario">
						<caption>Informações Para Contato</caption>
						<tr>
							<th>Logradouro:</th>
							<td colspan="3"><h:outputText value="#{docenteExterno.obj.pessoa.enderecoContato.tipoLogradouro.descricao}"/>&nbsp;&nbsp; 
							<h:outputText value="#{docenteExterno.obj.pessoa.enderecoContato.logradouro}"/></td>
							<th>N.&deg;:</th>
							<td><h:outputText value="#{docenteExterno.obj.pessoa.enderecoContato.numero}"/></td>
						</tr>

						<tr>
							<th>Bairro:</th>
							<td><h:outputText value="#{docenteExterno.obj.pessoa.enderecoContato.bairro}"/></td>
							<th>Complemento:</th>
							<td><h:outputText value="#{docenteExterno.obj.pessoa.enderecoContato.complemento}"/></td>
							<th>CEP:</th>
							<td>${docenteExterno.obj.pessoa.enderecoContato.cep}</td>
						</tr>

						<tr>
							<th>UF:</th>
							<td><h:outputText value="#{docenteExterno.obj.pessoa.enderecoContato.unidadeFederativa.descricao}"/></td>
							<th>Município:</th>
							<td colspan="4"><h:outputText value="#{docenteExterno.obj.pessoa.enderecoContato.municipio.nome}"/></td>
						</tr>
						<tr>
							<th>Tel. Fixo:</th>
							<td>
							(${docenteExterno.obj.pessoa.codigoAreaNacionalTelefoneFixo})
							${docenteExterno.obj.pessoa.telefone}
							</td>
							<th>Tel. Celular:</th>
							<td colspan="4">
							(${docenteExterno.obj.pessoa.codigoAreaNacionalTelefoneCelular})
							${docenteExterno.obj.pessoa.celular}
							</td>
						</tr>

					</table>
					</td>
				</tr>

			</tbody>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="#{docenteExterno.confirmButton}" action="#{docenteExterno.cadastrar}" id="btnCadastrar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{docenteExterno.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>

<script type="text/javascript">
	/* Se o país escolhido for diferente de Brasil,
	 desabilita a escolha de municipio e UF */
	function testaPais(sel) {
		var val = sel.options[sel.selectedIndex].value;
		if (val != "31") {
			$('form:naturUF').disabled=true;
			$('form:naturMunicipio').disabled=true;
		} else {
			$('form:naturUF').disabled=false;
			$('form:naturMunicipio').disabled=false;
		}
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

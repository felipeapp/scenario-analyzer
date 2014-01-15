<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<h2 class="tituloPagina"><ufrn:steps /></h2>

<script type="text/javascript">
	/* Se o país escolhido for diferente de Brasil,
	 desabilita a escolha de municipio e UF */
	function testaPais() {
		var val = $('pais').options[$('pais').selectedIndex].value;
		if (val != "31") {
			$('municipioNaturalidadeId').disabled=true;
			$('ufNaturalidadeId').disabled=true;
		} else {
			$('municipioNaturalidadeId').disabled=false;
			$('ufNaturalidadeId').disabled=false;
		}
	}
	
	function toggleCPF() {
		if ( getEl('estrangeiro').dom.checked )
			getEl('cpf').dom.disabled = true;
		else
			getEl('cpf').dom.disabled = false;
	}
</script>

<html:form  action="/pessoa/wizard"  method="post" focus="pessoa.nome" >
<html:hidden property="pessoa.id" />

	<table class="formulario" width="100%">
	<c:choose>
		<c:when test="${pessoaForm.pessoa.PF}">
			<caption>Dados Pessoais</caption>
		</c:when>
		<c:otherwise>
			<caption>Dados de Pessoa Jurídica</caption>
		</c:otherwise>
	</c:choose>

	<tbody>
		<%-- Bloco de dados de Pessoa Física --%>
		<c:if test="${pessoaForm.pessoa.PF}">
			<div class="descricaoOperacao" style="text-align: center;">
				O botão Carregar Dados tem como finalidade carregar as informações sobre 
					o aluno que possui o CPF informado.
			</div>
			
			<tr>
			<th width="17%">CPF:
			<span class="required">&nbsp;</span>
			</th>
			<td>
			<html:text property="cpf_cnpj" maxlength="14" size="14" styleId="cpf"  onkeyup="return formatarInteiro(this);"/>
			</td>
			<td>
				<html:button dispatch="carregarDados">Carregar Dados</html:button>	
			</td>
			<td style=" font-style: italic; ">
			<html:checkbox property="pessoa.internacional" value="true" styleId="estrangeiro" onchange="toggleCPF();"/>
			<label for="estrangeiro"> A pessoa é estrangeira e não possui CPF </label>
			</td>
			</tr>
			
			<tr>
			<th width="17%">Nome:
			<span class="required">&nbsp;</span>
			</th>
			<td colspan="3">
			<html:text property="pessoa.nome" maxlength="100" size="80" onkeyup="CAPS(this)"/>
			</td>
			</tr>

			<tr>
			<th>E-mail:</th>
			<td colspan="3"><html:text property="pessoa.email" maxlength="60" size="80" /></td>
			</tr>

			<tr>
			<th>Nome da Mãe:
			<span class="required">&nbsp;</span></th>
			<td colspan="3">
			<html:text property="pessoa.nomeMae" maxlength="100" size="80" onkeyup="CAPS(this)"/>
			</td>
			</tr>

			<tr>
			<th>Nome do Pai:</th>
			<td colspan="3">
			<html:text property="pessoa.nomePai" maxlength="100" size="80" onkeyup="CAPS(this)"/>
			</td>
			</tr>

			<tr>
			<th>Data de Nascimento: <span class="required">&nbsp;</span></th>
			<td width="180">
			<ufrn:calendar property="dataNascimento"  />
			</td>

			<th>Sexo: 
			<span>&nbsp;</span></th>
			<td>
			<html:radio property="pessoa.sexo" value="M" label="Masculino" />
			<html:radio property="pessoa.sexo" value="F" label="Feminino" />
			</td>
			</tr>

			<tr>
			<th>Estado Civil:</th>
			<td>
			<html:select property="pessoa.estadoCivil.id">
			<html:options collection="estadosCivil" property="id" labelProperty="descricao"/>
			</html:select>
			</td>
			<th>Rede de Ensino:</th>
			<td>
			<html:select property="pessoa.tipoRedeEnsino.id" >
			<html:options collection="redesEnsino" property="id" labelProperty="descricao"/>
			</html:select>
			<ufrn:help img="/img/ajuda.gif">Onde Concluiu o Ensino Médio</ufrn:help>
			</td>
			</tr>

			<tr>
			<th>Passaporte:</th>
			<td>
			<html:text property="pessoa.passaporte" maxlength="20" size="20" />
			</td>
			<th>Raça:</th>
			<td>
			<html:select property="pessoa.tipoRaca.id">
			<html:options collection="racas" property="id" labelProperty="descricao"/>
			</html:select>
			</td>
			</tr>

			<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
				<caption>Documento de Identidade</caption>
				<tr>
				<th width="17%">RG: <span class="required">&nbsp;</span></th>
				<td width="25%"><html:text property="pessoa.identidade.numero" maxlength="15" size="15" onkeyup="return formatarInteiro(this);"/></td>
				<th width="20%">Órgão de Expedição: <span class="required">&nbsp;</span></th>
				<td><html:text property="pessoa.identidade.orgaoExpedicao" maxlength="5" size="5" /></td>
				</tr>

				<tr>
				<th>UF: <span class="required">&nbsp;</span></th>
				<td>
				<html:select property="pessoa.identidade.unidadeFederativa.id" >
				<html:options collection="ufs" property="id" labelProperty="descricao"/>
				</html:select>
				</td>
				
				<th>Data de Expedição: <span class="required">&nbsp;</span></th>
				<td><ufrn:calendar property="dataExpedicaoIdentidade" /></td>
				</tr>
				</table>
			</td>
			</tr>

			<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
				<caption>Naturalidade</caption>
					<tr>
					<th width="17%">País:</th>
					<td width="25%">
					<html:select property="pessoa.pais.id" onchange="testaPais()" styleId="pais">
					<html:options collection="paises" property="id" labelProperty="nome"/>
					</html:select>
					</td>
					<th width="20%">UF:</th>
					<td>
					<html:select property="pessoa.unidadeFederativa.id" styleId="ufNaturalidadeId">
					<html:options collection="ufs" property="id" labelProperty="descricao"/>
					</html:select>
					</td>
					</tr>

					<tr>
					<th>Município:</th>
					<td colspan="3">
					<html:select property="pessoa.municipio.id" styleId="municipioNaturalidadeId">
					<html:options collection="municipios" property="id" labelProperty="nome"/>
					</html:select>
					</td>
					</tr>
					<ajax:select baseUrl="${applicationScope.contexto}/ajaxMunicipios"
					parameters="ufId={ufNaturalidadeId}"   executeOnLoad="true"
					source="ufNaturalidadeId" target="municipioNaturalidadeId" defaultOptions="${pessoaForm.pessoa.municipio.id}" postFunction="testaPais"/>

				</table>
			</td>
			</tr>
		</c:if>

		<%-- FIM - Bloco de dados de Pessoa Física --%>

		<%-- Bloco de dados de Pessoa Jurídica --%>
		<c:if test="${ pessoaForm.pessoa.PJ }">
			<tr>
			<th width="140">CNPJ:</th>
			<td colspan="3">
			<html:text property="cpf_cnpj" maxlength="25" size="25" onkeypress="formataCpfCnpj(this, event, null)" />
		    <span class="required">&nbsp;</span>
			</td>
			</tr>

			<tr>
			<th>Nome Fantasia:</th>
			<td colspan="3">
			<html:text property="pessoa.nome" maxlength="100" size="80" onkeyup="CAPS(this)"/>
			<span class="required">&nbsp;</span>
			</td>
			</tr>

			<tr>
			<th>E-mail:</th>
			<td colspan="3"><html:text property="pessoa.email" maxlength="60" size="80" /></td>
			</tr>

			<tr>
			<th>
			Razão Social:
			</th>
			<td colspan="3">
			<html:text property="pessoaJuridica.razaoSocial" size="80" maxlength="80" onkeyup="CAPS(this)" />
			</td>
			</tr>

			<tr>
			<th>
			Nome do Dirigente:
			</th>
			<td colspan="3">
			<html:text property="pessoaJuridica.nomeDirigente" size="80" maxlength="80" onkeyup="CAPS(this)" />
			<span class="required">&nbsp;</span>
			</td>
			</tr>

			<tr>
			<th>
			Cargo do Dirigente:
			</th>
			<td colspan="3">
			<html:text property="pessoaJuridica.cargoDirigente" size="60" maxlength="40" onkeyup="CAPS(this)" />
			</td>
			</tr>

			<tr>
			<th>
			Atividade Fim:
			</th>
			<td colspan="3">
			<html:text property="pessoaJuridica.atividadeFim" size="40" maxlength="40" onkeyup="CAPS(this)" />
			</td>
			</tr>

			<tr>
			<th>
			Esfera Administrativa:
			</th>
			<td colspan="3">
			<html:select property="pessoaJuridica.tipoEsferaAdministrativa.id">
            <html:options collection="esferasAdministrativas" property="id" labelProperty="descricao" />
            </html:select>
            &nbsp;&nbsp;&nbsp;
			Outras:
			<html:text property="pessoaJuridica.esferaAdministrativaOutra" size="40" maxlength="40" onkeyup="CAPS(this)" />
			</td>
			</tr>

			<tr>
			<th>
			Empresa Privada:
			</th>
			<td width="150">
			<html:radio property="pessoaJuridica.privada" value="true" label="Sim"/>
			<html:radio property="pessoaJuridica.privada" value="false" label="Não" />
			</td>
			<th width="150">
			Fins lucrativos:
			</th>
			<td>
			<html:radio property="pessoaJuridica.finsLucrativos" value="true" label="Sim"/>
			<html:radio property="pessoaJuridica.finsLucrativos" value="false" label="Não" />
			</td>
			</tr>

			<tr>
			<th>
			Registro no CNSS:
			</th>
			<td>
			<html:text property="pessoaJuridica.registroCnss" size="20" maxlength="30" />
			</td>
			<th>
			N.&deg; Declaração Federal:
			</th>
			<td>
			<html:text property="pessoaJuridica.numDeclaracaoFederal" size="20" maxlength="30" />
			</td>
			</tr>

			<tr>
			<th>
			N.&deg; Declaração Estadual:
			</th>
			<td>
			<html:text property="pessoaJuridica.numDeclaracaoEstadual" size="20" maxlength="30" />
			</td>
			<th>
			N.&deg; Declaração Municipal:
			</th>
			<td>
			<html:text property="pessoaJuridica.numDeclaracaoMunicipal" size="20" maxlength="30" />
			</td>
			</tr>

		</c:if>
		<%-- FIM - Bloco de dados de Pessoa Jurídica --%>

		<tr>
		<td colspan="4">
			<table width="100%" class="subFormulario">
			<caption>Informações Para Contato</caption>
			<tr>
			<th>Logradouro:</th>
			<td colspan="3">
			<html:select property="pessoa.enderecoContato.tipoLogradouro.id" style="width: 80px">
			<html:options collection="tiposLogradouro" property="id" labelProperty="descricao"/>
			</html:select>&nbsp;&nbsp;
			<html:text property="pessoa.enderecoContato.logradouro" maxlength="40" size="60" onkeyup="CAPS(this)" />
			</td>
			<th>
			N.&deg;:
			</th>
			<td>
			<html:text property="pessoa.enderecoContato.numero" maxlength="8" size="6" />
			</td>
			</tr>

			<tr>
			<th>Bairro:</th>
			<td>
			<html:text property="pessoa.enderecoContato.bairro" maxlength="20" size="20" onkeyup="CAPS(this)"/>
			</td>
			<th>Complemento:</th>
			<td>
			<html:text property="pessoa.enderecoContato.complemento" maxlength="80" size="20" />
			</td>
			<th>CEP:</th>
			<td>
			<html:text property="pessoa.enderecoContato.cep" maxlength="10" size="10" onkeyup="formataCEP(this, event, null)" />
			</td>
			</tr>

			<tr>
			<th>UF:</th>
			<td>
			<html:select property="pessoa.enderecoContato.unidadeFederativa.id" styleId="enderecoUFId">
			<html:options collection="ufs" property="id" labelProperty="descricao"/>
			</html:select>
			</td>
			<th>Município:</th>
			<td colspan="4">
			<html:select property="pessoa.enderecoContato.municipio.id" styleId="enderecoMunicipioId">
			<html:options collection="municipios" property="id" labelProperty="nome"/>
			</html:select>
			</td>
			</tr>

			<tr>
			<th>Tel. Fixo:</th>
			<td>
			(<html:text property="pessoa.codigoAreaNacionalTelefoneFixo" maxlength="2" size="1" />)
			<html:text property="pessoa.telefone" maxlength="9" size="9" onkeyup="formataTelefone(this, event, null)" />
			</td>
			<th>Tel. Celular:</th>
			<td colspan="4">
			(<html:text property="pessoa.codigoAreaNacionalTelefoneCelular" maxlength="2" size="1" />)
			<html:text property="pessoa.celular" maxlength="9" size="9" onkeyup="formataTelefone(this, event, null)" />
			</td>
			</tr>
			<ajax:select baseUrl="${applicationScope.contexto}/ajaxMunicipios"
			parameters="ufId={enderecoUFId}"  executeOnLoad="true"
			source="enderecoUFId" target="enderecoMunicipioId" defaultOptions="${pessoaForm.pessoa.enderecoContato.municipio.id}"  />
			</table>
		</td>
		</tr>

	</tbody>

	<tfoot>
		<tr>
		<td colspan="4">
		<c:choose><c:when test="${ (empty param.nextView) and (empty sessionScope.nextView) }">
			<html:button dispatch="submeterDados">Confirmar</html:button>
			<html:button dispatch="cancelar">Cancelar</html:button>
		</c:when><c:otherwise>
			<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
			<html:button dispatch="submeterDados"> Próximo &gt;&gt;</html:button>
			<c:if test="${proximoId > 0}">
			<br><br>
			<html:button dispatch="confirmarDadosDiscente"> Alterar Dados Pessoais</html:button>
			</c:if>
		</c:otherwise></c:choose>
		</td>
		</tr>
	</tfoot>

	</table>

<script>
	testaPais();
	toggleCPF();
</script>

</html:form>

<center>
<br>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>

<br><br>
</center>



<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
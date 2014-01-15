<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="manifestacaoOuvidoria" />

<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Cadastro de Manifestação
	</h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p>Para prosseguir com o cadastro da manifestação, informe os dados do solicitante no formulário abaixo.</p>
	</div>

	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Informações pessoais</caption>
			<tbody>
			<tr>
				<th width="25%" class="required">Nome:</th>
				<td>
					<h:inputText id="nome" value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.nome }" style="width: 95%" onkeyup="CAPS(this);" />
				</td>
			</tr>
			<tr>
				<th width="25%" class="required">E-Mail:</th>
				<td>
					<h:inputText id="email" value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.email }" style="width: 95%" maxlength="50" />
				</td>
			</tr>
			<tr>
				<th>Telefone:</th>
				<td>
					(<h:inputText onchange="return formatarInteiro(this);" onkeyup="return formatarInteiro(this);" value="#{manifestacaoOuvidoria.codArea}" maxlength="2" size="2" id="Codigo_de_Area" />)
					 <h:inputText onkeyup="return formatarInteiro(this);" value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.telefone}" maxlength="9" size="9" id="telefone" />
				 </td>
			</tr>
			<tr>
				<td colspan="2">
					<table width="100%" class="subFormulario">
						<caption>Endereço</caption>
						<tr class="linhaCep">
							<th width="20%">CEP:</th>
							<td colspan="5">
								<h:inputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.cep}" onkeyup="return formataCEP(this, event, null);"
									maxlength="10" size="10"
									id="endCEP" onblur="formataCEP(this, event, null); ConsultadorCep.consultar();"/>
								<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
									<img src="/sigaa/img/buscar.gif"/>
								</a>
								<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span>
								<span id="cepIndicator" style="display: none;">
									<img src="/sigaa/img/indicator.gif"/> Buscando endereço...
								</span>
							</td>
						</tr>
						<tr>
							<th>Logradouro:</th>
							<td nowrap="nowrap">
								<h:selectOneMenu value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.tipoLogradouro.id}"
									id="tipoLogradouro">
									<f:selectItems value="#{tipoLogradouro.allCombo}" />
								</h:selectOneMenu>
								<h:inputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.logradouro }"
									maxlength="70" id="logradouro" size="45" style="margin-left: 5px;"/>
							</td>
							<th>N.&deg;:</th>
							<td><h:inputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.numero}" maxlength="8" size="10"
								id="endNumero" /></td>
						</tr>

						<tr>
							<th>Bairro:</th>
							<td><h:inputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.bairro}" maxlength="60" size="30"
								id="endBairro"/></td>
							<th>Complemento:</th>
							<td><h:inputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.complemento}" maxlength="80" size="20"
								id="endComplemento" /></td>
						</tr>

						<tr>
							<a4j:region>
								<th>UF:</th>
								<td>
									<h:selectOneMenu value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.unidadeFederativa.id}" id="ufEnd" valueChangeListener="#{manifestacaoOuvidoria.carregarMunicipios }">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
										<f:selectItems value="#{unidadeFederativa.allCombo}" />
										<a4j:support event="onchange" reRender="endMunicipio" />
									</h:selectOneMenu>
								</td>
							</a4j:region>
							<th>Município:</th>
							<td>
								<h:selectOneMenu id="endMunicipio" value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.municipio.id}" valueChangeListener="#{manifestacaoOuvidoria.setarMunicipio }">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
									<f:selectItems value="#{manifestacaoOuvidoria.municipiosCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
					</table>
					</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_anterior" value="<< Passo Anterior" action="#{manifestacaoOuvidoria.paginaTipoSolicitante }" />
						<h:commandButton id="btn_cancelar" value="Cancelar"  action="#{manifestacaoOuvidoria.cancelar }" onclick="#{confirm}"/>
						<h:commandButton id="btn_proximo" value="Próximo Passo >>" action="#{manifestacaoOuvidoria.submeterDadosSolicitante }" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro', 'form:endBairro', 'form:endMunicipio', 'form:ufEnd', function() {
	$('form:ufEnd').onchange();
} );
</script>
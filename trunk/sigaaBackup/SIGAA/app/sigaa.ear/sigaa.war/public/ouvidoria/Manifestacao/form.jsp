<%@ include file="../../include/cabecalho.jsp" %>
<a4j:keepAlive beanName="manifestacaoComunidadeExterna" />

<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>

<div class="descricaoOperacao">
	<p>Caro Usuário(a),</p>
	<p>A Ouvidoria é denominado assim, por prestar atendimento e encaminhamento dos problemas, sugestões, reclamações, críticas e porque não, elogios referentes à universidade.
	   Um trabalho que é oferecido não só à comunidade interna, mas a toda sociedade.</p>
	<p>Através dessa operação é possível entrar em contato com a Ouvidoria para se manifestar, de forma positiva ou negativa, a respeito de assuntos pré-definidos.</p>
	<p>Apenas os dados marcados com uma estrela são obrigatórios para realizar o cadastro da manifestação. Assim sendo, dados como telefone e endereço são opcionais.</p>
	<p style="left: 5%;">Obs: O arquivo que pode ser anexado a manifestação deverá ter no máximo 50Mb de tamanho.</p>
	<br/>
	<p style="font-weight:bold;color: red;">Caso a manifestação seja referente ao sistema, entrar em contato com ${ configSistema['emailSuporte']}<p>
</div>

<f:view>
	<input type="hidden" value="${manifestacaoComunidadeExterna.iniciarCadastro }" />
	<h2>
		Entrar em Contato com a Ouvidoria
	</h2>

	<h:form enctype="multipart/form-data" id="formManifestacao">
		<table class="formulario" width="90%">
			<caption>Cadastro de Manifestação</caption>
			<tr><td colspan="2" class="subFormulario" style="text-align: center; font-size: small;">Informações pessoais</td></tr>
			<tbody>
			<tr>
				<th width="25%" class="required">Nome:</th>
				<td>
					<h:inputText id="nome" value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.nome }" style="width: 95%" onkeyup="CAPS(this);" />
				</td>
			</tr>
			<tr>
				<th width="25%" class="required">E-Mail:</th>
				<td>
					<h:inputText id="email" value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.email }" style="width: 95%" maxlength="50" />
				</td>
			</tr>
			<tr>
				<th>Telefone:</th>
				<td>
					(<h:inputText onchange="return formatarInteiro(this);" onkeyup="return formatarInteiro(this);" value="#{manifestacaoComunidadeExterna.codArea}" maxlength="2" size="2" id="Codigo_de_Area" />)
					 <h:inputText onkeyup="return formatarInteiro(this);" value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.telefone}" maxlength="9" size="9" id="telefone" />
				 </td>
			</tr>
			<tr>
				<td colspan="2">
					<table width="100%" class="subFormulario">
						<caption>Endereço</caption>
						<tr class="linhaCep">
							<th width="20%">CEP:</th>
							<td colspan="5">
								<h:inputText value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.endereco.cep}" onkeyup="return formataCEP(this, event, null);"
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
								<h:selectOneMenu value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.endereco.tipoLogradouro.id}"
									id="tipoLogradouro">
									<f:selectItems value="#{tipoLogradouro.allCombo}" />
								</h:selectOneMenu>
								<h:inputText value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.endereco.logradouro }"
									maxlength="70" id="logradouro" size="45" style="margin-left: 5px;"/>
							</td>
							<th>N.&deg;:</th>
							<td><h:inputText value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.endereco.numero}" maxlength="8" size="10"
								id="endNumero" /></td>
						</tr>

						<tr>
							<th>Bairro:</th>
							<td><h:inputText value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.endereco.bairro}" maxlength="60" size="30"
								id="endBairro"/></td>
							<th>Complemento:</th>
							<td><h:inputText value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.endereco.complemento}" maxlength="80" size="20"
								id="endComplemento" /></td>
						</tr>

						<tr>
							<a4j:region>
								<th>UF:</th>
								<td>
									<h:selectOneMenu value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.endereco.unidadeFederativa.id}" id="ufEnd" valueChangeListener="#{manifestacaoComunidadeExterna.carregarMunicipios }">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
										<f:selectItems value="#{unidadeFederativa.allCombo}" />
										<a4j:support event="onchange" reRender="endMunicipio" />
									</h:selectOneMenu>
								</td>
							</a4j:region>
							<th>Município:</th>
							<td>
								<h:selectOneMenu id="endMunicipio" value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado.endereco.municipio.id}" valueChangeListener="#{manifestacaoComunidadeExterna.setarMunicipio }">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
									<f:selectItems value="#{manifestacaoComunidadeExterna.municipiosCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
					</table>
					</td>
			</tr>
			</tbody>
		</table>
		<table class="formulario" width="90%">
			<tr><td colspan="2" class="subFormulario" style="text-align: center; font-size: small;">Informações sobre a manifestação</td></tr>
			<tbody>
			<tr>
				<th class="required" width="25%">Categoria do Assunto:</th>
				<td>
					<h:selectOneMenu id="categoria" value="#{manifestacaoComunidadeExterna.obj.assuntoManifestacao.categoriaAssuntoManifestacao.id }" onchange="submit()" style="width: 95%">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{categoriaAssuntoManifestacao.allCategoriasAtivasCombo }" />
						<a4j:support event="onselect" reRender="assunto" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Assunto:</th>
				<td>
					<h:selectOneMenu id="assunto" value="#{manifestacaoComunidadeExterna.obj.assuntoManifestacao.id }" style="width: 95%">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{manifestacaoComunidadeExterna.allAssuntosByCategoriaCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Tipo da Manifestação:</th>
				<td>
					<h:selectOneRadio id="tipo" value="#{manifestacaoComunidadeExterna.obj.tipoManifestacao.id }" style="width: 95%">
						<f:selectItems value="#{tipoManifestacao.allTiposManifestacaoCombo }" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td>
					<h:inputText id="titulo" value="#{manifestacaoComunidadeExterna.obj.titulo }" maxlength="50" style="width: 95%" />
				</td>
			</tr>
							
			<tr>
				<th>Arquivo:</th>
				<td>
					<t:inputFileUpload id="ifuArquivo" size="70" storage="file" value="#{manifestacaoComunidadeExterna.arquivo}" />
				</td>
			</tr>
				
			<tr>
				<td style="text-align: right; vertical-align: top;">
					<h:selectBooleanCheckbox value="#{manifestacaoComunidadeExterna.obj.anonima}" styleClass="noborder" id="anonima" />
				</td>
				<td>
					<label for="anonima" onclick="$('formManifestacao:anonima').checked = !$('formManifestacao:anonima').checked;">
						Desejo que meu sigilo seja mantido.
					</label>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao" style="margin: 0; text-align: center;">
						<i>Caso solicitado, o sigilo <b>NÃO</b> valerá para a Ouvidoria, mas <b>APENAS</b> para a Unidade responsável por resposta, se esse for o caso.<br />
						Segundo a resolução 024/02 - CONSAD, Art 3º. Inciso VII), cabe à Ouvidoria: "Guardar sigilo quanto à identidade dos denunciantes, se solicitado ou quando entender que a identificação possa-lhes causar transtornos."</i>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
						<caption style="text-align: center;">Mensagem <h:graphicImage url="/img/required.gif" /></caption>
						<tr>
							<td style="text-align: center;">
								<h:inputTextarea id="mensagem" value="#{manifestacaoComunidadeExterna.obj.mensagem }" rows="15" style="width: 99%;" />
							</td>
						</tr>
					</table>
				</td>
			</tr>	
			</tbody>				
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_notificar" value="Cadastrar Manifestação" action="#{manifestacaoComunidadeExterna.cadastrar}" />
						&nbsp;
						<h:commandButton id="btn_cancelar" value="Cancelar"  action="#{manifestacaoComunidadeExterna.cancelar}" onclick="#{confirm}"/>
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
	
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; Voltar ao menu principal</a>
	</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
ConsultadorCep.init('/sigaa/consultaCep', 'formManifestacao:endCEP', 'formManifestacao:logradouro', 'formManifestacao:endBairro', 'formManifestacao:endMunicipio', 'formManifestacao:ufEnd', function() {
	$('formManifestacao:ufEnd').onchange();
} );
</script>
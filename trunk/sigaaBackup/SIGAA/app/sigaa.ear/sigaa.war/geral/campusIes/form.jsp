<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"> </script>
<f:view>
	<h2><ufrn:subSistema /> &gt; Campus</h2>

	<a4j:keepAlive beanName="campusIes" />
	<h:form id="form">
		<table class="formulario">
			<caption>Cadastro de Campus</caption>
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText id="nome" value="#{ campusIes.obj.nome }"
					size="50" maxlength="80" readonly="#{campusIes.readOnly}"
					disabled="#{campusIes.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Sigla:</th>
				<td><h:inputText id="sigla" value="#{ campusIes.obj.sigla }"
					size="20" maxlength="20" readonly="#{campusIes.readOnly}"
					disabled="#{campusIes.readOnly}" /></td>
			</tr>
			<tr>
				<td colspan="2" >
					<table width="100%" class="subFormulario">
						<caption>Endereço</caption>
						<tr class="linhaCep">
							<th class="obrigatorio">CEP:</th>
							<td colspan="3">
								<h:inputText value="#{campusIes.obj.endereco.cep}" maxlength="10" size="10" id="endCEP"
									onkeyup="return formatarInteiro(this);" onblur="formataCEP(this, event, null); ConsultadorCep.consultar();" />
								<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
									<img src="/sigaa/img/buscar.gif" alt="" title="Busca o endereço do CEP informado" /> 
								</a> 
								<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span> 
								<span id="cepIndicator" style="display: none;"> 
								<img src="/sigaa/img/indicator.gif" alt="" /> Buscando... </span>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Logradouro:</th>
							<td colspan="3">
								<h:selectOneMenu value="#{campusIes.obj.endereco.tipoLogradouro.id}" id="tipoLogradouro">
									<f:selectItems value="#{tipoLogradouro.allCombo}" />
								</h:selectOneMenu> 
								<h:inputText value="#{campusIes.obj.endereco.logradouro }" maxlength="60" id="logradouro" size="60" />
							</td>
							<th class="obrigatorio">N.&deg;:</th>
							<td>
								<h:inputText value="#{campusIes.obj.endereco.numero}" maxlength="8" size="6" id="endNumero" onkeypress="return formatarInteiro(this);" />
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Bairro:</th>
							<td>
								<h:inputText value="#{campusIes.obj.endereco.bairro}" maxlength="20" size="20" id="endBairro" />
							</td>
							<th>Complemento:</th>
							<td>
								<h:inputText value="#{campusIes.obj.endereco.complemento}"	maxlength="80" size="20" id="endComplemento" />
							</td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<th class="obrigatorio">UF:</th>
							<td>
								<h:selectOneMenu value="#{campusIes.obj.endereco.unidadeFederativa.id}" id="ufEnd" onchange="submit()"
										valueChangeListener="#{campusIes.carregarMunicipios }">
									<f:selectItems value="#{unidadeFederativa.allCombo}" />
								</h:selectOneMenu>
							</td>
							<th class="obrigatorio">Município:</th>
							<td>
								<h:selectOneMenu value="#{campusIes.obj.endereco.municipio.id}" id="endMunicipio">
									<f:selectItems value="#{campusIes.municipiosEndereco}" />
								</h:selectOneMenu>
							</td>
							<td></td>
							<td></td>
						</tr>
					</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden id="idCampusIes"	value="#{campusIes.obj.id}" /> 
						<h:commandButton id="btnConfirmar" value="#{campusIes.confirmButton}" action="#{campusIes.cadastrar}" /> 
						<c:if test="${campusIes.obj.id > 0}">
							<h:commandButton id="btnVoltar" value="<< Voltar" action="#{campusIes.listar}" immediate="true" />
						</c:if> 
						<h:commandButton id="btnCancelar" value="Cancelar" action="#{campusIes.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>
<script type="text/javascript">
	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro',
			'form:endBairro', 'form:endMunicipio', 'form:ufEnd');
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

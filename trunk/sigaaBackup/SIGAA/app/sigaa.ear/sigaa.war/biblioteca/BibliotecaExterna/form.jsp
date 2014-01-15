<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>
<script type="text/javascript" src="/shared/javascript/formatador.js"></script>

<f:view>
	<h2><ufrn:subSistema /> &gt; Biblioteca Externa</h2>
	<br>
	
	<a4j:keepAlive beanName="bibliotecaExternaMBean"></a4j:keepAlive>
	
	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Biblioteca Externa</caption>

			<tr>
				<th class="obrigatorio" style="width:150px;">Identificador:</th>
				<td><h:inputText value="#{bibliotecaExternaMBean.obj.identificador}" onkeyup="CAPS(this)"
					readonly="#{bibliotecaExternaMBean.readOnly}" maxlength="20" size="20" /></td>
			</tr>
			<tr>
				<th class="obrigatorio" style="width:150px;">Descrição:</th>
				<td><h:inputText value="#{bibliotecaExternaMBean.obj.descricao}"
					readonly="#{bibliotecaExternaMBean.readOnly}" maxlength="200" size="45" /></td>
			</tr>
			<tr>
				<th>Nome do Responsável:</th>
				<td><h:inputText value="#{bibliotecaExternaMBean.obj.responsavel}"
					readonly="#{bibliotecaExternaMBean.readOnly}" maxlength="100" size="45" /></td>
			</tr>
			<tr>
				<th>Email:</th>
				<td><h:inputText value="#{bibliotecaExternaMBean.obj.email}"
					readonly="#{bibliotecaExternaMBean.readOnly}" maxlength="100" size="45" /></td>
			</tr>
			<tr>
				<th>Telefone:</th>
				<td><h:inputText value="#{bibliotecaExternaMBean.obj.telefone}"
					readonly="#{bibliotecaExternaMBean.readOnly}" maxlength="50" size="45" /></td>
				</tr>




<tr><td colspan="2">



<table class="subformulario" width="100%">

	<caption>Endereço</caption>

	<tr class="linhaCep">
		<th>CEP:</th>
		<td colspan="3">
			<h:inputText value="#{bibliotecaExternaMBean.obj.endereco.cep}"
				maxlength="10" size="10" disabled="#{bibliotecaExternaMBean.readOnly}"
				id="endCEP" onblur="formataCEP(this, event, null); ConsultadorCep.consultar();"/>
			<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
				<img src="/sigaa/img/buscar.gif"/>
			</a>
			<span style="font-size:7pt;">(clique na lupa para buscar o endereço do CEP informado)</span>
			<span id="cepIndicator" style="display: none;font-size:7pt;">
				<img src="/sigaa/img/indicator.gif"/>Buscando ...
			</span>
		</td>
	</tr>

	<tr>
		<th>Logradouro:</th>
		<td colspan="3">
			<h:selectOneMenu value="#{bibliotecaExternaMBean.obj.endereco.tipoLogradouro.id}" id="tipoLogradouro">
				<f:selectItems value="#{tipoLogradouro.allCombo}" />
			</h:selectOneMenu>
			<h:inputText style="margin-left:10px;width:350px;" value="#{bibliotecaExternaMBean.obj.endereco.logradouro }" maxlength="50" id="logradouro" size="70" />
		</td>
	</tr>
	
	<tr>
		<th>Bairro:</th>
		<td>
			<h:inputText value="#{bibliotecaExternaMBean.obj.endereco.bairro}" maxlength="60" size="20" id="endBairro"/>
		</td>

		<th>Complemento:</th>
		<td>
			<h:inputText value="#{bibliotecaExternaMBean.obj.endereco.complemento}" maxlength="40" size="20" id="endComplemento" />
			N&deg;: <h:inputText value="#{bibliotecaExternaMBean.obj.endereco.numero}" maxlength="8" size="6" id="endNumero" />
		</td>
	</tr>


	<tr>
		<th>UF:</th>
		<td>
			<h:selectOneMenu value="#{bibliotecaExternaMBean.obj.endereco.unidadeFederativa.id}" id="ufEnd"
				valueChangeListener="#{bibliotecaExternaMBean.carregarMunicipios }">
				<f:selectItems value="#{unidadeFederativa.allCombo}" />
				<a4j:support event="onchange" reRender="endMunicipio" />
			</h:selectOneMenu>
		</td>
		<th>Município:</th>
		<td>
			<h:selectOneMenu value="#{bibliotecaExternaMBean.obj.endereco.municipio.id}" id="endMunicipio">
				<f:selectItem itemValue="0" itemLabel=" -- Selecione -- " />
				<f:selectItems value="#{bibliotecaExternaMBean.municipiosEndereco}" />
			</h:selectOneMenu>
		</td>
	</tr>
</table>
</td></tr>



				<tfoot>
					<tr>
						<td colspan="2" align="center">
						
						<h:commandButton value="#{bibliotecaExternaMBean.confirmButton}" action="#{bibliotecaExternaMBean.cadastrar}" id="cmdCadastrarBibliotecaExterna" />
						
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{bibliotecaExternaMBean.listar}" immediate="true" id="cancelar" />
						
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>

</f:view>

<script type="text/javascript">
	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro', 'form:endBairro', 'form:endMunicipio', 'form:ufEnd', function() {
		$('form:ufEnd').onchange();
	} );
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
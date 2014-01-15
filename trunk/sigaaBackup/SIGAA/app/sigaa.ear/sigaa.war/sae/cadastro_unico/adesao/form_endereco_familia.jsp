<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js">

</script>
<script>
/* Máscaras ER */  
function mascara(o,f){  
    v_obj=o  
    v_fun=f  
    setTimeout("execmascara()",1)  
}  
function execmascara(){  
    v_obj.value=v_fun(v_obj.value)  
}  
function mtel(v){  
    v=v.replace(/\D/g,"");             //Remove tudo o que não é dígito  
    v=v.replace(/(\d)(\d{4})$/,"$1-$2");    //Coloca hífen entre o quarto e o quinto dígitos  
    return v;  
}  
</script>
<h2><ufrn:subSistema /> > Cadastro Único de Bolsistas > Endereco da Família</h2>

	<div class="descricaoOperacao">
		<center><strong>Endereço da Família</strong></center>
		<br />
		<p>
			Caso voce não more com sua família, é necessário informar o endereço. Durante a triagem feita pelo DEAE este dado será relevante para determinar as condições sócio econômicas do candidato.
		</p>
	</div>

<f:view>
	<h:form id="form">

	<table class="visualizacao" style="width: 85%;">
		<tbody>
			<tr>
				<th>CEP:</th>
				<td><h:outputText value="#{adesaoCadastroUnico.discente.pessoa.enderecoContato.cep}"/></td>
				<th>Bairro:</th>
				<td>
					<h:outputText value="#{adesaoCadastroUnico.discente.pessoa.enderecoContato.bairro}"/>
				</td>				
			</tr>
			<tr>
				<th>Rua:</th>
				<td>
					<h:outputText value="#{adesaoCadastroUnico.discente.pessoa.enderecoContato.logradouro}"/> 
				</td>
				<th>Número:</th>
				<td>
					<h:outputText value="#{adesaoCadastroUnico.discente.pessoa.enderecoContato.numero}"/>
				</td>
			</tr>			
			<tr>
				<th>Cidade:</th>
				<td>
					<h:outputText value="#{adesaoCadastroUnico.discente.pessoa.enderecoContato.municipio.nome}"/> 
				</td>
				<th>UF:</th>
				<td>
					<h:outputText value="#{adesaoCadastroUnico.discente.pessoa.enderecoContato.unidadeFederativa.sigla}"/> 
				</td>				
			</tr>			
		</tbody>
	</table>

	<br/>
	
	<h:messages />

	<table width="100%">
		<tbody align="center">
			<tr>
				<td>
					<div class="descricaoOperacao">
						Endereço da sua família é diferente do endereço acima?
						<h:selectOneRadio id="checkMesmaMoradia" value="#{adesaoCadastroUnico.mesmoEndereco}" onclick="javascript:mostrarTabela(this);">
							<f:selectItems value="#{adesaoCadastroUnico.simNao}"/>
						</h:selectOneRadio>
					</div>
				</td>
			</tr>
				<tr id="trPrimeiraTabela">
					<td><h:commandButton value="Continuar >>"
						action="#{adesaoCadastroUnico.iniciarQuestionario}" /></td>
				</tr>
			</tbody>
	</table>

	<table id="tblEndereco" width="90" class="formulario" style="display: none;">
			<caption>Endereço da família</caption>

			<tbody>
				<tr class="linhaCep">
					<th>CEP: <span class="required"></span></th>
					<td colspan="5"><h:inputText
						value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.cep}"
						maxlength="10" size="10" id="endCEP"
						onblur="formataCEP(this, event, null); ConsultadorCep.consultar(); " />

					<a href="javascript://nop/"
						onclick="ConsultadorCep.consultar(); $('form:ufEnd').onChange();">
					<img src="/sigaa/img/buscar.gif" /> </a> <span class="info">(clique
					na lupa para buscar o endereço do CEP informado)</span> <span
						id="cepIndicator" style="display: none;"> <img
						src="/sigaa/img/indicator.gif" /> Buscando endereço... </span></td>
				</tr>

				<tr>
					<th>Logradouro: <span class="required"></span></th>
					<td colspan="4"><h:selectOneMenu
						value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.tipoLogradouro.id}"
						id="tipoLogradouro">
						<f:selectItems value="#{tipoLogradouro.allCombo}" />
					</h:selectOneMenu> <h:inputText
						value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.logradouro }"
						maxlength="50" id="logradouro" size="70" /></td>

					<th>N.&deg;:</th>
					<td><h:inputText
						value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.numero}"
						maxlength="8" size="6" id="endNumero" /></td>
				</tr>

				<tr>
					<th>Bairro: <span class="required"></span></th>
					<td><h:inputText
						value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.bairro}"
						maxlength="20" size="20" id="endBairro" /></td>

					<th>Complemento:</th>
					<td><h:inputText
						value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.complemento}"
						maxlength="40" size="20" id="endComplemento" /></td>
				</tr>


				<tr>
					<th>UF: <span class="required"></span></th>
					<td><h:selectOneMenu
						value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.unidadeFederativa.id}"
						id="ufEnd"
						valueChangeListener="#{adesaoCadastroUnico.carregarMunicipios }">
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
						<a4j:support event="onchange" reRender="endMunicipio" />
					</h:selectOneMenu></td>

					<th>Município: <span class="required"></span></th>
					<td colspan="4"><h:selectOneMenu
						value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.municipio.id}"
						id="endMunicipio">
						<f:selectItems value="#{adesaoCadastroUnico.municipiosEndereco}" />
					</h:selectOneMenu></td>
				</tr>

				<tr>
					<th>Tel. Fixo:</th>

					<td><h:inputText disabled="#{dadosPessoais.readOnly}"
						value="#{adesaoCadastroUnico.obj.contatoFamilia.telefone}"
						maxlength="9" size="9" id="telFixoNumero"
						onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" />(somente números)</td>

					<th>Tel. Celular:</th>
					<td colspan="3"><h:inputText id="telCelNumero"
						disabled="#{dadosPessoais.readOnly}"
						value="#{adesaoCadastroUnico.obj.contatoFamilia.celular}"
						maxlength="10" size="10" onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" />(somente
					números)</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="7"><h:commandButton value="Continuar >>>"	action="#{adesaoCadastroUnico.iniciarQuestionario}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
<script type="text/javascript">
ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro', 'form:endBairro', 'form:endMunicipio', 'form:ufEnd' );

	function mostrarTabela(sel) {
		var val = null; 
		if( sel != null) 
			val = sel.value;

		if (val == null || val == undefined) {
			val = getValue();
		}
			
		if (val == 'true') {
			$('tblEndereco').show();
			$('trPrimeiraTabela').hide();			
		} else {
			$('tblEndereco').hide();
			$('trPrimeiraTabela').show();
		}
	}
	
	function getValue(){
        for(var i=0; i < document.getElementsByName('form:checkMesmaMoradia').length; i++)
            if(document.getElementsByName('form:checkMesmaMoradia')[i].checked == true)
                return document.getElementsByName('form:checkMesmaMoradia')[i].value;
    }

	mostrarTabela(null);
	
	
</script>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
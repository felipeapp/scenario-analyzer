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
<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Local de Aplicação de Prova</h2>

	<h:form id="form">
		<table class="formulario">
			<caption class="listagem">Cadastro de Local de Aplicação de
			Provas</caption>
			<tr>
				<th class="required">Nome do Local:</th>
				<td><h:inputText value="#{localAplicacaoProva.obj.nome}"
					size="60" disabled="#{localAplicacaoProva.readOnly}" id="obj_nome"
					maxlength="120" /></td>
			</tr>
			<tr>
				<td colspan="2">
				<table width="100%" class="subFormulario">
					<caption>Informações Para Contato</caption>
					<tr>
						<th>Nome do Contato:</th>
						<td colspan="3"><h:inputText
							value="#{localAplicacaoProva.obj.contato}" size="60"
							id="obj_contato" maxlength="120"
							disabled="#{localAplicacaoProva.readOnly}" /></td>
					</tr>
					<tr>
						<th>E-Mail:</th>
						<td colspan="3"><h:inputText
							value="#{localAplicacaoProva.obj.email}" size="60" id="obj_email"
							maxlength="80" disabled="#{localAplicacaoProva.readOnly}" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table width="100%" class="subFormulario">
					<caption>Endereço</caption>
					<tr class="linhaCep">
						<th class="required">CEP:</th>
						<td colspan="5"><h:inputText
							value="#{localAplicacaoProva.obj.endereco.cep}" maxlength="10"
							onkeyup="return formatarInteiro(this)" size="10"
							disabled="#{localAplicacaoProva.readOnly}" id="endCEP"
							onblur="formataCEP(this, event, null); ConsultadorCep.consultar();" />
						<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
						<img src="/sigaa/img/buscar.gif" /> </a> <span class="info">(clique
						na lupa para buscar o endereço do CEP informado)</span> <span
							id="cepIndicator" style="display: none;"> <img
							src="/sigaa/img/indicator.gif" /> Buscando endereço... </span></td>
					</tr>
					<tr>
						<th class="required">Logradouro:</th>
						<td colspan="3"><h:selectOneMenu
							value="#{localAplicacaoProva.obj.endereco.tipoLogradouro.id}"
							disabled="#{localAplicacaoProva.readOnly}" id="tipoLogradouro">
							<f:selectItems value="#{tipoLogradouro.allCombo}" />
						</h:selectOneMenu> <h:inputText
							value="#{localAplicacaoProva.obj.endereco.logradouro }"
							disabled="#{localAplicacaoProva.readOnly}" maxlength="70"
							id="logradouro" size="60" /></td>
						<th class="required">N.&deg;:</th>
						<td><h:inputText
							value="#{localAplicacaoProva.obj.endereco.numero}" maxlength="8"
							size="6" disabled="#{localAplicacaoProva.readOnly}"
							onkeyup="return formatarInteiro(this)" id="endNumero" /></td>
					</tr>
					<tr>
						<th class="required">Bairro:</th>
						<td><h:inputText
							value="#{localAplicacaoProva.obj.endereco.bairro}" maxlength="20"
							size="20" disabled="#{localAplicacaoProva.readOnly}"
							id="endBairro" /></td>
						<th>Complemento:</th>
						<td><h:inputText
							value="#{localAplicacaoProva.obj.endereco.complemento}"
							maxlength="80" size="20"
							disabled="#{localAplicacaoProva.readOnly}" id="endComplemento" /></td>
					</tr>
					<tr>
						<th>UF:</th>
						<td><h:selectOneMenu
							value="#{localAplicacaoProva.obj.endereco.unidadeFederativa.id}"
							id="ufEnd" disabled="#{localAplicacaoProva.readOnly}"
							immediate="true"
							valueChangeListener="#{localAplicacaoProva.carregarMunicipios}">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
							<a4j:support event="onchange" reRender="endMunicipio" />
						</h:selectOneMenu></td>
						<th>Município:</th>
						<td colspan="4"><h:selectOneMenu
							value="#{localAplicacaoProva.obj.endereco.municipio.id}"
							disabled="#{localAplicacaoProva.readOnly}" id="endMunicipio">
							<f:selectItems value="#{localAplicacaoProva.municipiosEndereco}" />
						</h:selectOneMenu></td>
					</tr>
					<tr>
						<th>Tel. Fixo:</th>
						<td>(<h:inputText
							value="#{localAplicacaoProva.obj.codigoAreaNacionalTelefoneFixo}"
							onkeyup="return formatarInteiro(this)"
							disabled="#{localAplicacaoProva.readOnly}" maxlength="2" size="1"
							id="telFixoDDD" />) <h:inputText
							disabled="#{localAplicacaoProva.readOnly}"
							value="#{localAplicacaoProva.obj.telefone}" maxlength="9"
							onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" size="9"
							id="telFixoNumero" /></td>
						<th>Tel. Celular:</th>
						<td colspan="3">(<h:inputText
							disabled="#{localAplicacaoProva.readOnly}"
							onkeyup="return formatarInteiro(this)"
							value="#{localAplicacaoProva.obj.codigoAreaNacionalTelefoneCelular}"
							maxlength="2" size="1" id="telCelDDD" />) <h:inputText
							id="telCelNumero" disabled="#{localAplicacaoProva.readOnly}"
							onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );"
							value="#{localAplicacaoProva.obj.celular}" maxlength="10" size="10" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2" valign="top">
				<table width="100%" class="subFormulario">
					<caption>Salas</caption>
					<tr>
						<td width="60%" valign="top">
						<table>
							<tr>
								<th class="required">Número:</th>
								<td><h:inputText
									value="#{localAplicacaoProva.salaAdicionada.numero}" size="5"
									disabled="#{localAplicacaoProva.readOnly}" maxlength="10"
									 id="sala_numero" /></td>
								<th>Área (m2):</th>
								<td><h:inputText
									value="#{localAplicacaoProva.salaAdicionada.area}" size="5"
									style="text-align: right"
									onkeydown="return(formataValor(this, event, 2))"
									disabled="#{localAplicacaoProva.readOnly}" maxlength="5"
									id="sala_area">
									<f:convertNumber minFractionDigits="2" />
								</h:inputText></td>
								<td rowspan="2" valign="middle"><h:commandButton value="Adicionar Sala >>"
									action="#{localAplicacaoProva.adicionaSala}" id="adicionaSala"
									rendered="#{not localAplicacaoProva.readOnly}" /></td>
							</tr>
							<tr>
								<th>Capacidade Máxima:</th>
								<td><h:inputText
									value="#{localAplicacaoProva.salaAdicionada.capacidadeMaxima}"
									size="5" disabled="#{localAplicacaoProva.readOnly}"
									onkeyup="return formatarInteiro(this)" maxlength="5"
									converter="#{ intConverter }" id="sala_capacidadeMax" /></td>
								<th>Capacidade Ideal:</th>
								<td><h:inputText
									value="#{localAplicacaoProva.salaAdicionada.capacidadeIdeal}"
									onkeyup="return formatarInteiro(this)" size="5"
									disabled="#{localAplicacaoProva.readOnly}" maxlength="5"
									converter="#{ intConverter }" id="sala_capacidadeIdeal" /></td>
								<td></td>
							</tr>
						</table>
						</td>
						<td valign="top">
						<table>
							<tr class="subFormulario">
								<td>Salas Cadastradas: ${fn:length(localAplicacaoProva.obj.salas)}</td>
							</tr>
							<tr>
								<td>
								<table>
									<c:forEach items="#{localAplicacaoProva.obj.salas}" var="item"
										varStatus="status">
										<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
											<td>${item.descricao}</td>
											<td width="2%"><h:commandLink title="Remover"
											rendered="#{not localAplicacaoProva.readOnly}"
												action="#{localAplicacaoProva.removeSala}"
												style="border: 0;">
												<f:param name="id" value="#{item.numero}" />
												<h:graphicImage url="/img/delete.png" />
											</h:commandLink></td>
										</tr>
									</c:forEach>
								</table>
								</td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table width="100%" class="subFormulario">
					<caption>Total de fiscais, apoio e seguranças</caption>
					<tr class="subFormulario">
						<th>Fiscais de Sala:</th>
						<td colspan="5"><h:inputText
							value="#{localAplicacaoProva.obj.numFiscaisSala}" maxlength="5"
							size="5" disabled="#{localAplicacaoProva.readOnly}"
							onkeyup="return formatarInteiro(this)" id="fiscaisSala"
							style="text-align: right;" converter="#{ intConverter }" /></td>
						<th>Fiscais de Corredor:</th>
						<td><h:inputText
							value="#{localAplicacaoProva.obj.numFiscaisCorredor}"
							onkeyup="return formatarInteiro(this)" maxlength="5" size="5"
							disabled="#{localAplicacaoProva.readOnly}" id="fiscaisCorredor"
							style="text-align: right;" converter="#{ intConverter }" /></td>
						<th>Apoio:</th>
						<td><h:inputText value="#{localAplicacaoProva.obj.numApoio}"
							onkeyup="return formatarInteiro(this)" maxlength="5" size="5"
							disabled="#{localAplicacaoProva.readOnly}" id="apoio"
							style="text-align: right;" converter="#{ intConverter }" /></td>
						<th>Seguranças:</th>
						<td><h:inputText
							value="#{localAplicacaoProva.obj.numSegurancas}" maxlength="5"
							onkeyup="return formatarInteiro(this)" size="5"
							disabled="#{localAplicacaoProva.readOnly}" id="segurancas"
							style="text-align: right;" converter="#{ intConverter }" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{localAplicacaoProva.confirmButton}"
						action="#{localAplicacaoProva.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{localAplicacaoProva.cancelar}"
						onclick="#{confirm}" immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span></center>
	</h:form>
</f:view>
<script type="text/javascript">
	/* Se o país escolhido for diferente de Brasil,
	 desabilita a escolha de municipio e UF
	 */
	function testaPais(sel) {
		var val = sel.options[sel.selectedIndex].value;
		if (val != "31") {
			$('form:naturMunicipio').disabled = true;
			$('form:ufIdNatur').disabled = true;
		} else {
			$('form:naturMunicipio').disabled = false;
			$('form:ufIdNatur').disabled = false;
		}
	}
</script>
<script type="text/javascript">
	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro',
			'form:endBairro', 'form:endMunicipio', 'form:ufEnd');
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
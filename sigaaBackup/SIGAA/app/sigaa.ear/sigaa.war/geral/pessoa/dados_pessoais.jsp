<%@page pageEncoding="ISO-8859-1" %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"> </script>
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

<style>
	#cepIndicator {
		padding: 0 25px;
		color: #999;
	}
	span.info {
		font-size: 0.9em;
		color: #888;
	}
</style>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="tituloPagina"> <ufrn:subSistema /> &gt; Dados Pessoais </h2>
	<h:outputText value="#{dadosPessoais.create}" />
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Dados Pessoais</caption>
			<tbody>
				<tr>
					<th width="20%">
						<t:div id="labelCPF">
							CPF:
							<c:if test="${ !dadosPessoais.obj.internacional }">
								<html:img page="/img/required.gif" />
							</c:if>
						</t:div>
					</th>
					<td colspan="3">
							<c:if test="${dadosPessoais.passivelAlterarCpf}">
								<h:inputText value="#{ dadosPessoais.obj.cpf_cnpj }" size="14" maxlength="14"
									onkeypress="return formataCPF(this, event, null);" id="txtCPF" disabled="#{dadosPessoais.readOnly}">
									<f:converter converterId="convertCpf"/>
									<f:param name="type" value="cpf" />
								</h:inputText>
								<c:if test="${ dadosPessoais.obj.internacional }">
									<i>(opcional por ser estrangeiro)</i>
								</c:if>
							</c:if>
							<c:if test="${!dadosPessoais.passivelAlterarCpf}">
								<ufrn:format type="cpf_cnpj" valor="${dadosPessoais.obj.cpf_cnpj}"></ufrn:format>
							</c:if>
					</td>
				</tr>
				<tr>
					<th></th>
					<td colspan="5">
						<h:selectBooleanCheckbox id="internacional" valueChangeListener="#{dadosPessoais.resetPais}"
							 value="#{dadosPessoais.obj.internacional}" onclick="submit();" />
						A pessoa é estrangeira e não possui CPF
					</td>
				</tr>
				<tr>
					<th width="20%" class="obrigatorio">Nome:</th>
					<td colspan="3">
						<h:inputText value="#{dadosPessoais.obj.nome}" id="nome" size="80" maxlength="80" disabled="#{!dadosPessoais.permiteAlterarNome}"/>
					</td>
				</tr>
				<tr>
					<th width="20%" class="obrigatorio">Nome Oficial:</th>
					<td colspan="3">
						<h:inputText value="#{dadosPessoais.obj.nomeOficial}" id="nomeOficial" size="80" maxlength="80" disabled="#{!dadosPessoais.permiteAlterarNome}"/>
					</td>
				</tr>
				<tr>
					<th>E-Mail:</th>
					<td colspan="3">
						<h:inputText value="#{dadosPessoais.obj.email}" id="email" size="60" maxlength="80" disabled="#{dadosPessoais.readOnly}"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome da Mãe:</th>
					<td colspan="3">
						<h:inputText value="#{dadosPessoais.obj.nomeMae}" id="mae" size="80" maxlength="80" disabled="#{dadosPessoais.readOnly}"/>
					</td>
				</tr>
				<tr>
					<th>Nome do Pai:</th>
					<td colspan="3">
						<h:inputText value="#{dadosPessoais.obj.nomePai}" id="pai" size="80" maxlength="80" disabled="#{dadosPessoais.readOnly}"/>
					</td>
				</tr>
				<tr>
					<th width="20%">Sexo:</th>
					<td width="25%;" style="width: 25: !important;"><h:selectOneRadio value="#{dadosPessoais.obj.sexo}" id="sexo" disabled="#{dadosPessoais.readOnly}">
						<f:selectItem itemValue="M" itemLabel="Masculino" />
						<f:selectItem itemValue="F" itemLabel="Feminino" />
					</h:selectOneRadio></td>
					
					<th width="20%;" class="obrigatorio">Data de Nascimento:</th>
					<td ><t:inputCalendar value="#{dadosPessoais.obj.dataNascimento}" size="10" maxlength="10" disabled="#{dadosPessoais.readOnly}"
							 onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy"
						id="Nascimento" renderAsPopup="true" renderPopupButtonAsImage="true" >
						</t:inputCalendar>
					</td>
				</tr>
				<tr>
					<th>Estado Civil:</th>
					<td><h:selectOneMenu value="#{dadosPessoais.obj.estadoCivil.id}" id="estadoCivil" disabled="#{dadosPessoais.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{estadoCivil.allCombo}" />
					</h:selectOneMenu></td>
					<th>Raça:</th>
					<td><h:selectOneMenu value="#{dadosPessoais.obj.tipoRaca.id}" id="raca" disabled="#{dadosPessoais.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoRaca.allCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<c:if test="${!dadosPessoais.medio}">
				<tr>
					<th>Escola de Conclusão do Ensino Médio:</th>
					<td>
						<h:inputText value="#{dadosPessoais.obj.escolaConclusaoEnsinoMedio}" id="escolaConclusaoEnsinoMedio" size="50" maxlength="50" disabled="#{dadosPessoais.readOnly}"/>
					</td>
					<th>Ano de Conclusão:</th>
					<td>
						<h:inputText value="#{dadosPessoais.obj.anoConclusaoEnsinoMedio}" id="anoConclusaoEnsinoMedio" size="5" maxlength="4" disabled="#{dadosPessoais.readOnly}" 
						onkeyup="return formatarInteiro(this);" onblur="return formatarInteiro(this);"/>
					</td>
				</tr>
				</c:if>
				<tr>
					<th>Tipo Sanguíneo:</th>
					<td>
						<h:selectOneMenu id="tipoSanguineo" value="#{dadosPessoais.obj.tipoSanguineo}">
							<f:selectItem itemValue="" itemLabel="-- SELECIONE --"/>
							<f:selectItem itemValue="O-" itemLabel="O-"/>
							<f:selectItem itemValue="O+" itemLabel="O+"/>
							<f:selectItem itemValue="A-" itemLabel="A-"/>
							<f:selectItem itemValue="A+" itemLabel="A+"/>
							<f:selectItem itemValue="B-" itemLabel="B-"/>
							<f:selectItem itemValue="B+" itemLabel="B+"/>
							<f:selectItem itemValue="AB-" itemLabel="AB-"/>
							<f:selectItem itemValue="AB+" itemLabel="AB+"/>
						</h:selectOneMenu>
					</td>
				</tr>

				<!-- ============= NATURALIDADE ============= -->
				<tr>
					<td colspan="4">
					<t:div id="tableNaturalidade">	
					<table width="100%" class="subFormulario" >
						
						<caption>Naturalidade</caption>
						
						<tr>
							<a4j:region>
							<th width="20%;">País:</th>
							<td width="${!dadosPessoais.obj.internacional ? '33' : '40'}%;">
								<h:selectOneMenu value="#{dadosPessoais.obj.pais.id}" id="naturPais" disabled="#{dadosPessoais.readOnly}"
									valueChangeListener="#{dadosPessoais.alterarPais}"  >
									<f:selectItems value="#{pais.allCombo}" />
									<a4j:support event="onchange" reRender="tableNaturalidade,tableDocumentacao,labelCPF" />
								</h:selectOneMenu>
							</td>
							</a4j:region>
							
							<a4j:region>
							<th width="20%">
								<h:outputLabel value="UF:" rendered="#{dadosPessoais.brasil}" id="naturUf" />
							</th>
							<td>
								<h:selectOneMenu
									value="#{dadosPessoais.obj.unidadeFederativa.id}"
									id="ufIdNatur" disabled="#{dadosPessoais.readOnly}"
									valueChangeListener="#{dadosPessoais.carregarMunicipios }"
									rendered="#{dadosPessoais.brasil}">
									<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{unidadeFederativa.allCombo}" />
									<a4j:support event="onchange" reRender="naturMunicipio" />
								</h:selectOneMenu>
							</td>
							</a4j:region>
						</tr>
					
						<tr>
							<th>Município:</th>
							<td colspan="3">
								<h:selectOneMenu value="#{dadosPessoais.obj.municipio.id}" id="naturMunicipio" disabled="#{dadosPessoais.readOnly}"  rendered="#{dadosPessoais.brasil}">
									<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{dadosPessoais.municipiosNaturalidade}" />
								</h:selectOneMenu>
							
								<h:inputText value="#{dadosPessoais.obj.municipioNaturalidadeOutro}" id="naturMunicipioOutros" disabled="#{dadosPessoais.readOnly}" size="60" maxlength="50" onkeyup="CAPS(this)" rendered="#{!dadosPessoais.brasil}"/>
							</td>
						</tr>
						
						
						<tr>
							<th>Nacionalidade:</th>
							<td colspan="3">
								<h:inputText value="#{dadosPessoais.obj.paisNacionalidade}" id="paisNacionalidade" disabled="#{dadosPessoais.readOnly}" size="60" maxlength="50" onkeyup="CAPS(this)"/>
							</td>
						</tr>
						
					</table>
					</t:div>
					</td>
				</tr>

				<!-- ============= DOCUMENTAÇÃO ============= -->
				<tr>
					<td colspan="4">
					<t:div id="tableDocumentacao">	
					<table width="100%" class="subFormulario">
						<caption>Documentação</caption>
						<tr>
							<th width="20%">RG:</th>
							<td width="33%"><h:inputText value="#{dadosPessoais.obj.identidade.numero}" id="rg" disabled="#{!dadosPessoais.permiteAlterarIdentidade}"
								size="15" maxlength="15" onkeyup="return formatarInteiro(this);" onblur="return formatarInteiro(this);"/></td>
							<th width="20%">Órgão de Expedição:</th>
							<td><h:inputText value="#{dadosPessoais.obj.identidade.orgaoExpedicao}" size="10" disabled="#{!dadosPessoais.permiteAlterarIdentidade}" id="orgaoExpedicao" maxlength="20" /></td>
						</tr>
						<tr>
							<th>UF:</th>
							<td><h:selectOneMenu value="#{dadosPessoais.obj.identidade.unidadeFederativa.id}" id="ufRG" disabled="#{!dadosPessoais.permiteAlterarIdentidade}">															
								<f:selectItems value="#{unidadeFederativa.allCombo}" />
							</h:selectOneMenu></td>
							<th>Data de Expedição:</th>
							<td>
							<t:inputCalendar value="#{dadosPessoais.obj.identidade.dataExpedicao}" size="10" disabled="#{!dadosPessoais.permiteAlterarIdentidade}" popupDateFormat="dd/MM/yyyy"
								id="Expedicao" maxlength="10" renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return formataData(this,event)">
							</t:inputCalendar>
							</td>
						</tr>
						<tr>
							<th> Título de Eleitor:</th>
							<td>
								<h:inputText value="#{dadosPessoais.obj.tituloEleitor.numero}" id="numeroTitulo" disabled="#{dadosPessoais.readOnly}" size="8" maxlength="12" onkeyup="return formatarInteiro(this);" onblur="return formatarInteiro(this);"/>
								Zona:
								<h:inputText value="#{dadosPessoais.obj.tituloEleitor.zona}" id="zonaTitulo" disabled="#{dadosPessoais.readOnly}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" onblur="return formatarInteiro(this);"/>
							</td>
							<th> Seção: </th>
							<td>
								<h:inputText value="#{dadosPessoais.obj.tituloEleitor.secao}" id="secaoTitulo" disabled="#{dadosPessoais.readOnly}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" onblur="return formatarInteiro(this);"/>							
							<h:outputText value=" UF:"/>
								<h:selectOneMenu value="#{dadosPessoais.obj.tituloEleitor.unidadeFederativa.id}" 
									 id="ufIdTitulo" disabled="#{dadosPessoais.readOnly}">
									 <f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{unidadeFederativa.allCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th> Certificado Militar:</th>
							<td> <h:inputText value="#{ dadosPessoais.obj.certificadoMilitar.numero }" maxlength="20" size="20" id="numero_certificadomilitar"  disabled="#{dadosPessoais.readOnly}"/></td>
							<th> Data de Expedição: </th>
							<td>
								<t:inputCalendar value="#{dadosPessoais.obj.certificadoMilitar.dataExpedicao}" size="10" disabled="#{dadosPessoais.readOnly}" popupDateFormat="dd/MM/yyyy"
									id="Expedicao_certificadomilitar" maxlength="10" renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return formataData(this,event)">
								</t:inputCalendar>
							</td>
						</tr>
						<tr>
							<th> Série:</th>
							<td> 
								<h:inputText value="#{ dadosPessoais.obj.certificadoMilitar.serie }" maxlength="2" size="2" id="serie_certificadomilitar"  disabled="#{dadosPessoais.readOnly}"/>
							</td>
							<th> Categoria: </th>
							<td>
								<h:inputText value="#{ dadosPessoais.obj.certificadoMilitar.categoria }" maxlength="2" size="2" id="categoria_certificadomilitar"  disabled="#{dadosPessoais.readOnly}"/> 
								Órgão: <h:inputText value="#{ dadosPessoais.obj.certificadoMilitar.orgaoExpedicao }" maxlength="5" size="5" id="orgaoExpedicao_certificadomilitar"  disabled="#{dadosPessoais.readOnly}"/> 
							</td>
						</tr>
						<tr>
							<th ${dadosPessoais.obj.internacional? 'class="obrigatorio"' : ''}>Passaporte:</th>
							<td>
								<h:inputText value="#{dadosPessoais.obj.passaporte}" maxlength="20" size="20" id="passaporte"  disabled="#{dadosPessoais.readOnly}"/>
								
							</td>							
						</tr>
					</table>
					</t:div>
					</td>
				</tr>


				<!-- ============= ENDEREÇO DE CONTATO ============ -->
				<tr>
					<td colspan="4">
					<table width="100%" class="subFormulario">
						<caption>Informações Para Contato</caption>
						<tr class="linhaCep">
							<th width="20%">CEP:</th>
							<td colspan="5">
								<h:inputText value="#{dadosPessoais.obj.enderecoContato.cep}" onkeyup="return formataCEP(this, event, null);"
									maxlength="10" size="10" disabled="#{dadosPessoais.readOnly}"
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
							<td>
								<h:selectOneMenu value="#{dadosPessoais.obj.enderecoContato.tipoLogradouro.id}" disabled="#{dadosPessoais.readOnly}"
									id="tipoLogradouro">
									<f:selectItems value="#{tipoLogradouro.allCombo}" />
								</h:selectOneMenu>
								<h:inputText value="#{dadosPessoais.obj.enderecoContato.logradouro }" disabled="#{dadosPessoais.readOnly}"
									maxlength="70" id="logradouro" size="45" style="margin-left: 5px;"/>
							</td>
							<th>N.&deg;:</th>
							<td><h:inputText value="#{dadosPessoais.obj.enderecoContato.numero}" maxlength="8" size="10" disabled="#{dadosPessoais.readOnly}"
								id="endNumero" /></td>
						</tr>

						<tr>
							<th>Bairro:</th>
							<td><h:inputText value="#{dadosPessoais.obj.enderecoContato.bairro}" maxlength="60" size="30" disabled="#{dadosPessoais.readOnly}"
								id="endBairro"/></td>
							<th>Complemento:</th>
							<td><h:inputText value="#{dadosPessoais.obj.enderecoContato.complemento}" maxlength="80" size="20" disabled="#{dadosPessoais.readOnly}"
								id="endComplemento" /></td>
						</tr>

						<tr>
							<a4j:region>
								<th>UF:</th>
								<td>
									<h:selectOneMenu value="#{dadosPessoais.obj.enderecoContato.unidadeFederativa.id}" id="ufEnd" disabled="#{dadosPessoais.readOnly}"
										valueChangeListener="#{dadosPessoais.carregarMunicipios }">
										<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{unidadeFederativa.allCombo}" />										
										<a4j:support event="onchange" reRender="endMunicipio" />
									</h:selectOneMenu>
								</td>
							</a4j:region>
							<th>Município:</th>
							<td>
								<h:selectOneMenu id="endMunicipio" value="#{dadosPessoais.obj.enderecoContato.municipio.id}" disabled="#{dadosPessoais.readOnly}">
									<f:selectItems value="#{dadosPessoais.municipiosEndereco}" />
								</h:selectOneMenu>
							</td>
						</tr>

						<tr>
							<th>Tel. Fixo:</th>

							<td>(<h:inputText value="#{dadosPessoais.obj.codigoAreaNacionalTelefoneFixo}" disabled="#{dadosPessoais.readOnly}"
								maxlength="2" size="2" id="telFixoDDD" onkeyup="return formatarInteiro(this);"  onblur="return formatarInteiro(this);"/>)
								 <h:inputText disabled="#{dadosPessoais.readOnly}" onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );"
								 value="#{dadosPessoais.obj.telefone}" maxlength="9" size="9" id="telFixoNumero" /></td>
							<th>Tel. Celular:</th>
							<td colspan="3">(<h:inputText disabled="#{dadosPessoais.readOnly}" onkeyup="return formatarInteiro(this);"  onblur="return formatarInteiro(this);"
							    value="#{dadosPessoais.obj.codigoAreaNacionalTelefoneCelular}" maxlength="2" size="2" id="telCelDDD" />) 
								<h:inputText id="telCelNumero" disabled="#{dadosPessoais.readOnly}" onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );"
								value="#{dadosPessoais.obj.celular}" maxlength="10" size="10" />
							</td>
						</tr>
					</table>
					</td>
				</tr>

				<%-- DADOS BANCÁRIOS --%>
				<tr>
					<td colspan="4">
					<table width="100%" class="subFormulario">
						<caption>Dados Bancários</caption>
						<tr>
							<td width="180px;" align="right"> Banco: </td>
							<td colspan="5">
								<h:selectOneMenu value="#{dadosPessoais.obj.contaBancaria.banco.id}" id="contaBanco" 
									disabled="#{dadosPessoais.readOnly}" onchange="exibeDadosBancarios(this)">
									<f:selectItems value="#{banco.allCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<td id="labelAgencia" align="right"> N° Agência: </td>
							<td width="5%;" id="agencia">
								<h:inputText disabled="#{dadosPessoais.readOnly}"
									value="#{dadosPessoais.obj.contaBancaria.agencia}" size="12" maxlength="12"
									id="contaAgencia" />
							</td>
							<td width="15%;" align="right" id="labelConta"> N° Conta Corrente: </td>
							<td width="12%;" align="left" id="conta">
								<h:inputText disabled="#{dadosPessoais.readOnly}"
									value="#{dadosPessoais.obj.contaBancaria.numero}" size="12" maxlength="12"
									id="contaNumero" />
							</td>
							<td width="15%;" align="right" id="labelOperacao"> N° de Operação: </td>
							<td align="left" id="operacao">
								<h:inputText disabled="#{dadosPessoais.readOnly}"
									value="#{dadosPessoais.obj.contaBancaria.operacao}" size="4" maxlength="12"
									id="operacaoNumero" />
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4" align="center">
						<t:div rendered="#{!dadosPessoais.ordemBotoes}">
							<h:commandButton value="#{dadosPessoais.submitButton}" action="#{dadosPessoais.submeterDados}" id="submeter" />
							<h:commandButton value="Cancelar" action="#{dadosPessoais.cancelar}" id="cancelarOperacao" immediate="true" onclick="#{confirm}" />
						</t:div>
						<t:div rendered="#{dadosPessoais.ordemBotoes}">
							<h:commandButton value="#{dadosPessoais.submitButton}" action="#{dadosPessoais.cadastrar}" id="submetera" />
							<h:commandButton value="#{dadosPessoais.backButton}" action="#{dadosPessoais.voltar}" rendered="#{dadosPessoais.exibirVoltar}" id="voltara" /> 
							<h:commandButton value="Cancelar" action="#{dadosPessoais.cancelar}" id="cancelarOperacaoa" immediate="true" onclick="#{confirm}" />
						</t:div>
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
	<c:if test="${dadosPessoais.exibirPainel }">
		<div id="div-form">
			<div class="ydlg-hd">Identificação da Pessoa</div>
			<div class="ydlg-bd">
			<h:form id="cpfForm">
				<table class="formulario" width="100%" style="border: 0;">
					<caption>Por favor, informe o CPF</caption>
					<tr>
						<td colspan="2" style="color: red; font-style: italic; text-align: center;">
						${dadosPessoais.erroCPF}</td>
					</tr>
					<tr>
						<th width="50%">CPF:</th>
						<td><h:inputText value="#{dadosPessoais.obj.cpf_cnpj}" size="14" maxlength="14"
							converter="convertCpf" id="cpf" onblur="formataCPF(this, event, null)"
							onkeypress="return formataCPF(this, event, null)" >
									<f:converter converterId="convertCpf" />
							</h:inputText>
							</td>
					</tr>
					<tr>
						<td colspan="2" style="text-align: center; font-style: italic; background: #EEE;">
							<h:selectBooleanCheckbox value="#{dadosPessoais.obj.internacional}" id="estrangeiro" />
							<label for="cpfForm:estrangeiro"> A pessoa é estrangeira e não possui CPF</label>
						</td>
					</tr>
					<tfoot>
						<tr>
							<td colspan="2" align="center">
								<h:commandButton value="Enviar"
									actionListener="#{dadosPessoais.submeterCPF}" id="submeterCPF" />
								<h:commandButton value="Cancelar" onclick="#{confirm}"
									action="#{dadosPessoais.cancelar}" id="cancelarCPF" immediate="true" />
							</td>
						</tr>
					</tfoot>
				</table>
			</h:form>
			</div>
		</div>
	</c:if>
	
	
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
/* Se o país escolhido for diferente de Brasil,
 desabilita a escolha de municipio e UF
 */
function testaPais(sel) {
	var val = sel.options[sel.selectedIndex].value;
	if (val != "31") {
		$('form:naturMunicipio').disabled=true;
		$('form:ufIdNatur').disabled=true;
	} else {
		$('form:naturMunicipio').disabled=false;
		$('form:ufIdNatur').disabled=false;
	}
}

function exibeDadosBancarios(sel) {
	var val = sel.options[sel.selectedIndex].value;
	if (val > 0) {
		$('labelConta').show();
		$('conta').show();
		$('labelAgencia').show();
		$('agencia').show();
		$('labelOperacao').show();
		$('operacao').show();
	} else {
		$('labelConta').hide();
		$('conta').hide();
		$('labelAgencia').hide();
		$('agencia').hide();
		$('labelOperacao').hide();
		$('operacao').hide();

	}
}
exibeDadosBancarios($('form:contaBanco'));

var PainelCPF = (function() {
	var painel;
	return {
        show : function(){
   	 		painel = new YAHOO.ext.BasicDialog("div-form", {
                modal: true,
                width: 400,
                height: 170,
                shadow: false,
                fixedcenter: true,
                resizable: false,
                closable: false
            });
       	 	painel.show();
        }
	};
})();

ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro', 'form:endBairro', 'form:endMunicipio', 'form:ufEnd', function() {
	$('form:ufEnd').onchange();
} );

<c:if test="${dadosPessoais.exibirPainel }">
		PainelCPF.show();
		$('cpfForm:cpf').value = '';
		$('cpfForm:cpf').focus();
</c:if>


</script>
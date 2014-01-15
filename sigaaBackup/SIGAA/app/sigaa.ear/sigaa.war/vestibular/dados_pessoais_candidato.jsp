<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>
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
<style type="text/css">
th.rotulo{ text-align: right; font-weight: bold; }
#abas-inscricao {
	width: 80%;
	margin: 0 auto;
}

h3 {
	margin: 2px 0 10px;
}

h4 {
	margin: 5px 0;
}

.descricaoOperacao th {
	font-weight: bold;
	padding: 0 2px 5px 2px;
}

.curso,.nivel {
	text-align: center;
	display: block;
}

.nivel {
	font-size: 0.9em;
	text-transform: uppercase;
	color: #555;
}

.arquivo a {
	text-decoration: underline;
	color: #404E82;
	font-variant: small-caps;
}

.periodo {
	color: #292;
	font-weight: bold;
}

#form :sexo {
	border: 0;
}

</style>

<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2 class="tituloPagina"><ufrn:subSistema /> &gt; Dados Pessoais</h2>

	<h:form id="form">
		<a4j:keepAlive beanName="dadosPessoaisCandidatoMBean"></a4j:keepAlive>
		<table class="formulario" style="width: 100%;">
			<caption>Dados do Candidato</caption>
			<tbody>
				<tr>
					<td colspan="6" class="subFormulario">Dados Pessoais</td>
				</tr>
				<tr>
					<th width="20%">CPF:</th>
					<td colspan="5">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.cpf_cnpj}" size="16" maxlength="14" id="txtCPF" onkeypress="return formataCPF(this, event, null)">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf"/>
							<a4j:support actionListener="#{dadosPessoaisCandidatoMBean.validaCPFCadastrado}" event="onblur"></a4j:support>
						</h:inputText>
					</td>
				</tr>
				<tr>
					<th>Passaporte:</th>
					<td colspan="5">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.passaporte}" onkeyup="return formatarInteiro(this);" maxlength="20" size="20" id="passaporte" disabled="#{dadosPessoaisCandidatoMBean.readOnly}" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td colspan="5">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.nome}" size="80" maxlength="100" id="nome" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">E-mail:</th>
					<td colspan="5">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.email}" size="60" maxlength="60" id="email" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome da Mãe:</th>
					<td colspan="5">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.nomeMae}" size="80" maxlength="100" id="nomeMae" /> 
					</td>
				</tr>
				<tr>
					<th>Nome do Pai:</th>
					<td colspan="5">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.nomePai}" size="80" maxlength="100" id="nomePai" />
					</td>
				</tr>
				<tr>
					<th>Sexo:</th>
					<td>
						<h:selectOneRadio value="#{dadosPessoaisCandidatoMBean.obj.sexo}" id="sexo" style="border:0px;">
							<f:selectItem itemValue="M" itemLabel="Masculino" />
							<f:selectItem itemValue="F" itemLabel="Feminino" />
						</h:selectOneRadio>
					</td>
					<th class="obrigatorio">Data de Nascimento:</th>
					<td>
						<t:inputCalendar value="#{dadosPessoaisCandidatoMBean.obj.dataNascimento}" title="Data de Nascimento" size="10" maxlength="10" onkeypress="return formataData(this,event);" id="nascimento" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" >
							<f:converter converterId="convertData" />
						</t:inputCalendar>
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th class="obrigatorio">Estado Civil:</th>
					<td>
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.estadoCivil.id}" id="estadoCivil">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{estadoCivil.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th>Raça:</th>
					<td>
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.tipoRaca.id}" id="raca">
							<f:selectItems value="#{tipoRaca.allValidoCombo}" />
						</h:selectOneMenu>
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th valign="top">Tipo de Necessidade Especial:</th>
					<td colspan="5">
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.tipoNecessidadeEspecial.id}" id="necessidadeEspecial">
							<f:selectItem itemValue="0" itemLabel="-- NENHUMA --" />
							<f:selectItems value="#{dadosPessoaisCandidatoMBean.allTipoNecessidadeEspecialCombo}" />
						</h:selectOneMenu>
						<br/>
					</td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Escola de Conclusão do Ensino Médio</td>
				</tr>
				<tr>
					<th class="obrigatorio">UF:</th>
					<td>
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.ufConclusaoEnsinoMedio.id}" id="ufConclusaoEM" onchange="submit()" 
							valueChangeListener="#{dadosPessoaisCandidatoMBean.setaUfConclusaoEnsinoMedio }" immediate="false">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th class="obrigatorio">Ano de Conclusão:</th>
					<td colspan="3">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.anoConclusaoEnsinoMedio}" id="anoConclusao" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome da Escola:</th>
					<td colspan="6">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.nomeEscolaConclusaoEnsinoMedio}" id="escolaConclusao" size="40" maxlength="160"/>
						<rich:suggestionbox for="escolaConclusao" height="100"	width="500" minChars="3" id="suggestion"
							suggestionAction="#{dadosPessoaisCandidatoMBean.autocompleteEscolaConclusao}"  
							 var="_escolaInep" fetchValue="#{_escolaInep.nome}">
							<h:column>
								<h:outputText value="#{_escolaInep.endereco.municipio.nome}"/>
							</h:column>
							<h:column>
								<h:outputText value="#{_escolaInep.nome}"/>
							</h:column>
							<h:column>
								<h:outputText value="#{_escolaInep.endereco.bairro}"/>
							</h:column>
							<a4j:support event="onselect" actionListener="#{dadosPessoaisCandidatoMBean.setEscolaInep}" reRender="isServidor">
								<f:attribute name="idEscola" value="#{_escolaInep.id}"/>
							</a4j:support>
						</rich:suggestionbox> 
			            <ufrn:help>
			            	Digite o nome da escola (ex.: Machado) e escolha entre as sugestões de nomes de escolas dadas. <br/>
			            	Caso a sua escola não esteja na lista de sugestões, digite o nome completo dela (ex.: Escola Estadual Machado de Assis).
			            </ufrn:help>
						<a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Naturalidade</td>
				</tr>
				<tr>
					<th width="20%">País:</th>
					<td width="25%">
						<h:selectOneMenu 
						value="#{dadosPessoaisCandidatoMBean.obj.pais.id}" id="naturPais" 
						valueChangeListener="#{dadosPessoaisCandidatoMBean.alterarPais}" onchange="submit()">
							<f:selectItems value="#{pais.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th width="20%">
						<c:if test="${dadosPessoaisCandidatoMBean.brasil}">UF:</c:if>
					</th>
					<td colspan="3">
						<h:selectOneMenu 
						value="#{dadosPessoaisCandidatoMBean.obj.unidadeFederativa.id}" 
						id="ufIdNatur" onchange="submit()" immediate="true" 
						valueChangeListener="#{dadosPessoaisCandidatoMBean.carregarMunicipios }" 
						rendered="#{dadosPessoaisCandidatoMBean.brasil}" >
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Município:</th>
					<td colspan="5">
						
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.municipio.id}" immediate="true" 
						id="naturMunicipio" rendered="#{dadosPessoaisCandidatoMBean.brasil}">
							<f:selectItems value="#{dadosPessoaisCandidatoMBean.municipiosNaturalidade}" />
						</h:selectOneMenu>
					
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.municipioNaturalidadeOutro}" 
						id="naturMunicipioOutros" size="80" maxlength="80" rendered="#{!dadosPessoaisCandidatoMBean.brasil}"/>
						
					</td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Documento de Identificação</td>
				</tr>
				<tr>
					<th class="obrigatorio" width="20%">Nº do Doc. de Identificação:</th>
					<td>
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.identidade.numero}" id="rg" disabled="#{dadosPessoaisCandidatoMBean.readOnly}" size="12" maxlength="20" onkeyup="return formatarInteiro(this);"/>
					</td>
					<th class="obrigatorio" width="20%">Órgão de Expedição:</th>
					<td>
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.identidade.orgaoExpedicao}" size="10" disabled="#{dadosPessoaisCandidatoMBean.readOnly}" id="orgaoExpedicao" maxlength="20" />
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th>UF:</th>
					<td>
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.identidade.unidadeFederativa.id}" id="ufRG" disabled="#{dadosPessoaisCandidatoMBean.readOnly}">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th class="obrigatorio">Data de Expedição:</th>
					<td>
						<t:inputCalendar value="#{dadosPessoaisCandidatoMBean.obj.identidade.dataExpedicao}"  title="Data de Expedição do Doc. de Identificação" size="10" maxlength="10" onkeypress="return formataData(this,event);" id="dataExpedicao" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Título de Eleitor</td>
				</tr>
				<tr>
					<th >Nº do Título:</th>
					<td>
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.tituloEleitor.numero}" id="numeroTitulo" disabled="#{dadosPessoaisCandidatoMBean.readOnly}" maxlength="20" size="10" onkeyup="return formatarInteiro(this);" /> 
						Zona:<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.tituloEleitor.zona}" id="zonaTitulo" onkeyup="return formatarInteiro(this);" disabled="#{dadosPessoaisCandidatoMBean.readOnly}" size="4" maxlength="4" />
					</td>
					<th>Seção:</th>
					<td colspan="4">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.tituloEleitor.secao}" onkeyup="return formatarInteiro(this);" id="secaoTitulo" disabled="#{dadosPessoaisCandidatoMBean.readOnly}" size="4" maxlength="4" />
						UF: 
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.tituloEleitor.unidadeFederativa.id}" id="ufIdTitulo" disabled="#{dadosPessoaisCandidatoMBean.readOnly}">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
						</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Certificado Militar</td>
				</tr>
				<tr>
					<th> Nº do Certificado Militar:</th>
					<td> <h:inputText value="#{ dadosPessoaisCandidatoMBean.obj.certificadoMilitar.numero }" id="certificadoMilitar_numero" size="34" maxlength="30"/> </td>
					<th> Data de Expedição: </th>
					<td><t:inputCalendar value="#{ dadosPessoaisCandidatoMBean.obj.certificadoMilitar.dataExpedicao }" title="Data de Expedição do Certificado Militar" size="10" maxlength="10" onkeypress="return formataData(this,event);" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy"  id="certificadoMilitar_data">
							<f:converter converterId="convertData"/>
						</t:inputCalendar></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th> Série:</th>
					<td> <h:inputText value="#{ dadosPessoaisCandidatoMBean.obj.certificadoMilitar.serie }" size="10" maxlength="10" id="certificadoMilitar_serie" /> </td>
					<th> Categoria: </th>
					<td> <h:inputText value="#{ dadosPessoaisCandidatoMBean.obj.certificadoMilitar.categoria }" size="10" maxlength="10" id="certificadoMilitar_categoria"/> </td>
					<th> Órgão:</th>
					<td> <h:inputText value="#{ dadosPessoaisCandidatoMBean.obj.certificadoMilitar.orgaoExpedicao }" size="10" maxlength="10" id="certificadoMilitar_orgaoExpedicao"/></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Endereço</td>
				</tr>
				<tr class="linhaCep">
					<th class="obrigatorio">CEP:</th>
					<td colspan="5">
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.enderecoContato.cep}" maxlength="10" size="10" id="endCEP" onkeyup="return formatarInteiro(this);" onblur="formataCEP(this, event, null); ConsultadorCep.consultar();" />
						<a href="javascript://nop/" onclick="ConsultadorCep.consultar();"><img src="/sigaa/img/buscar.gif" alt="" /></a>
						<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span> 
						<span id="cepIndicator" style="display: none;"> 
						<img src="/sigaa/img/indicator.gif" alt="" /> Buscando endereço... </span>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Logradouro:</th>
					<td colspan="3">
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.enderecoContato.tipoLogradouro.id}" id="tipoLogradouro">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{tipoLogradouro.allCombo}" />
						</h:selectOneMenu>
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.enderecoContato.logradouro }" maxlength="60" id="logradouro" size="60" />
						</td>
					<th class="obrigatorio" width="25px">N&deg;:</th>
					<td>
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.enderecoContato.numero}" maxlength="8" size="6" id="endNumero" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Bairro:</th>
					<td>
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.enderecoContato.bairro}" maxlength="20" size="20" id="endBairro" />
					</td>
					<th>Complemento:</th>
					<td>
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.enderecoContato.complemento}" maxlength="80" size="20" id="endComplemento" />
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th>UF:</th>
					<td>
						<h:selectOneMenu 
						value="#{dadosPessoaisCandidatoMBean.obj.enderecoContato.unidadeFederativa.id}" 
						id="ufEnd" onchange="submit()" immediate="false"  
						valueChangeListener="#{dadosPessoaisCandidatoMBean.carregarMunicipios }">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th>Município:</th>
					<td>
						<h:selectOneMenu value="#{dadosPessoaisCandidatoMBean.obj.enderecoContato.municipio.id}" id="endMunicipio">
							<f:selectItems value="#{dadosPessoaisCandidatoMBean.municipiosEndereco}" />
						</h:selectOneMenu>
					</td>
					<td></td>
					<td></td>
				</tr>

				<tr>
					<th>Tel. Fixo:</th>
					<td>
						(<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.codigoAreaNacionalTelefoneFixo}" maxlength="2" size="1" id="telFixoDDD" converter="#{ shortConverter }" onkeyup="return formatarInteiro(this);" />) 
						<h:inputText value="#{dadosPessoaisCandidatoMBean.obj.telefone}" maxlength="9" size="9" onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );"id="telFixoNumero" />
					</td>
					<th>Tel. Celular:</th>
					<td>
						(<h:inputText onkeyup="return formatarInteiro(this);" converter="#{ shortConverter }" value="#{dadosPessoaisCandidatoMBean.obj.codigoAreaNacionalTelefoneCelular}" maxlength="2" size="1" id="telCelDDD" />)
						<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="telCelNumero" value="#{dadosPessoaisCandidatoMBean.obj.celular}" maxlength="10" size="10" />
					</td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton id="btnAtualizarDados" value="Atualizar Dados" action="#{dadosPessoaisCandidatoMBean.atualizarDados}"/> 
						<h:commandButton id="btnCancelar" value="Cancelar" action="#{dadosPessoaisCandidatoMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</div>
		<br/>
		<br/>
	</h:form>
	<c:if test="${dadosPessoaisCandidatoMBean.exibirPainel }">
		<div id="div-form">
			<div class="ydlg-hd">Identificação do Candidato</div>
			<div class="ydlg-bd">
			<h:form id="cpfForm">
				<a4j:keepAlive beanName="dadosPessoaisCandidatoMBean"></a4j:keepAlive>
				<table class="formulario" width="100%" style="border: 0;">
					<caption>Por favor, informe o CPF do Candidato</caption>
					<tr>
						<td colspan="2" style="color: red; font-style: italic; text-align: center;">
						${dadosPessoaisCandidatoMBean.erroCPF}</td>
					</tr>
					<tr>
						<th width="50%">CPF:</th>
						<td><h:inputText value="#{dadosPessoaisCandidatoMBean.obj.cpf_cnpj}" size="14" maxlength="14"
							converter="convertCpf" id="cpf" onblur="formataCPF(this, event, null)"
							onkeypress="return formataCPF(this, event, null)" >
									<f:converter converterId="convertCpf" />
							</h:inputText>
							</td>
					</tr>
					<tfoot>
						<tr>
							<td colspan="2" align="center">
								<h:commandButton value="Enviar"
									actionListener="#{dadosPessoaisCandidatoMBean.submeterCPF}" id="submeterCPF" />
								<h:commandButton value="Cancelar" onclick="#{confirm}"
									action="#{dadosPessoaisCandidatoMBean.cancelar}" id="cancelarCPF" immediate="true" />
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

<script>
	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro',	'form:endBairro', 'form:endMunicipio', 'form:ufEnd');

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

<c:if test="${dadosPessoaisCandidatoMBean.exibirPainel }">
		PainelCPF.show();
		$('cpfForm:cpf').value = '';
		$('cpfForm:cpf').focus();
</c:if>

</script>
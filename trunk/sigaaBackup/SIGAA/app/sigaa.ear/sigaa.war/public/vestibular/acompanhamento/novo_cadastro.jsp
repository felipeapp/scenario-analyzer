<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>
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
	<h2>Vestibular > Dados Pessoais</h2>

	<div class="descricaoOperacao">
		<b>Atenção: </b>preencha corretamente os dados abaixo. Serão de inteira responsabilidade do candidato os prejuízos advindos de informações incorretas. 
	</div>

	<h:form id="form">
		<table class="formulario" style="width: 100%;">
			<caption>Dados do Candidato</caption>
			<tbody>
				<tr>
					<td colspan="6" class="subFormulario"><a href="#">Dados Pessoais</a></td>
				</tr>
				<tr>
					<th class="${acompanhamentoVestibular.obj.id == 0 ? 'obrigatorio' : 'rotulo'} " width="20%">CPF:</th>
					<td colspan="5">
						<h:inputText value="#{acompanhamentoVestibular.obj.cpf_cnpj}" size="16" maxlength="14" id="txtCPF" onkeyup="return formataCPF(this, event, null)" 
							onkeypress="return formataCPF(this, event, null)"
							rendered="#{acompanhamentoVestibular.obj.id == 0}" title="*CPF">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf"/>
							<a4j:support actionListener="#{acompanhamentoVestibular.validaCPFCadastrado}" event="onchange"></a4j:support>
						</h:inputText>
						<h:outputText value="#{acompanhamentoVestibular.obj.cpfCnpjFormatado}" rendered="#{acompanhamentoVestibular.obj.id != 0}"/>
					</td>
				</tr>
				<tr>
					<th>Passaporte:</th>
					<td colspan="5">
						<h:inputText value="#{acompanhamentoVestibular.obj.passaporte}" onkeyup="return formatarInteiro(this);" maxlength="20" size="20" id="passaporte" disabled="#{acompanhamentoVestibular.readOnly}" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td colspan="5">
						<h:inputText value="#{acompanhamentoVestibular.obj.nome}" size="80" maxlength="100" id="nome" title="*Nome"
						 	onkeyup="CAPS(this)"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">E-mail:</th>
					<td colspan="5">
						<h:inputText value="#{acompanhamentoVestibular.obj.email}" size="60" maxlength="60" id="email" title="*E-mail"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome da Mãe:</th>
					<td colspan="5">
						<h:inputText value="#{acompanhamentoVestibular.obj.nomeMae}" size="80" maxlength="100" id="nomeMae" title="*Nome da Mãe"
							onkeyup="CAPS(this)"/> 
					</td>
				</tr>
				<tr>
					<th>Nome do Pai:</th>
					<td colspan="5">
						<h:inputText value="#{acompanhamentoVestibular.obj.nomePai}" size="80" maxlength="100" id="nomePai" 
							onkeyup="CAPS(this)"/>
					</td>
				</tr>
				<tr>
					<th>Sexo:</th>
					<td>
						<h:selectOneRadio value="#{acompanhamentoVestibular.obj.sexo}" id="sexo" style="border:0px;">
							<f:selectItem itemValue="M" itemLabel="Masculino" />
							<f:selectItem itemValue="F" itemLabel="Feminino" />
						</h:selectOneRadio>
					</td>
					<th class="obrigatorio">Data de Nascimento:</th>
					<td>
						<t:inputCalendar value="#{acompanhamentoVestibular.obj.dataNascimento}" title="*Data de Nascimento" size="10" 
							maxlength="10" onkeypress="return formataData(this,event);" id="nascimento" renderAsPopup="true" 
							renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" />
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th class="obrigatorio">Estado Civil:</th>
					<td>
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.estadoCivil.id}" id="estadoCivil" title="*Estado Civil">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{estadoCivil.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th>Raça:</th>
					<td>
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.tipoRaca.id}" id="raca">
							<f:selectItems value="#{tipoRaca.allValidoCombo}" />
						</h:selectOneMenu>
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th valign="top">Tipo de Necessidade Especial:</th>
					<td colspan="5">
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.tipoNecessidadeEspecial.id}" id="necessidadeEspecial">
							<f:selectItem itemValue="0" itemLabel="-- NENHUMA --" />
							<f:selectItems value="#{acompanhamentoVestibular.allTipoNecessidadeEspecialCombo}" />
						</h:selectOneMenu>
						<br/>
					</td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario"><a href="#">Escola de Conclusão do Ensino Médio</a></td>
				</tr>
				<tr>
					<th class="obrigatorio">UF:</th>
					<td>
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.ufConclusaoEnsinoMedio.id}" id="ufConclusaoEM" onchange="submit()" 
							valueChangeListener="#{acompanhamentoVestibular.setaUfConclusaoEnsinoMedio }" immediate="false" title="*UF">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th class="obrigatorio">Ano de Conclusão:</th>
					<td colspan="3">
						<h:inputText value="#{acompanhamentoVestibular.obj.anoConclusaoEnsinoMedio}" id="anoConclusao" size="4" maxlength="4" 
							onkeyup="return formatarInteiro(this);" title="*Ano de Conclusão"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome da Escola:</th>
					<td colspan="6">
						<t:panelGroup id="panelEscola">
						<h:inputText value="#{acompanhamentoVestibular.obj.nomeEscolaConclusaoEnsinoMedio}" id="escolaConclusao" size="40" maxlength="160"
						title="*Nome da Escola"/>
						<rich:suggestionbox for="escolaConclusao" height="100"	width="500" minChars="3" id="suggestion"
							suggestionAction="#{acompanhamentoVestibular.autocompleteEscolaConclusao}"  
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
							<a4j:support event="onselect" actionListener="#{acompanhamentoVestibular.setEscolaInep}" reRender="isServidor">
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
			            </t:panelGroup>
					</td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario"><a href="#">Naturalidade</a></td>
				</tr>
				<tr>
					<th width="20%">País:</th>
					<td width="25%">
						<h:selectOneMenu 
						value="#{acompanhamentoVestibular.obj.pais.id}" id="naturPais" 
						valueChangeListener="#{acompanhamentoVestibular.alterarPais}" onchange="submit()">
							<f:selectItems value="#{pais.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th width="20%">Nacionalidade:</th>
					<td width="25%">
						<h:outputText value="#{acompanhamentoVestibular.obj.pais.nacionalidade}" id="nacionalidade" />
					</td>
				</tr>
				<tr>
					<c:if test="${acompanhamentoVestibular.brasil}">
					<th width="20%">UF:</th>
					<td>
						<h:selectOneMenu 
						value="#{acompanhamentoVestibular.obj.unidadeFederativa.id}" 
						id="ufIdNatur" onchange="submit()" immediate="true" 
						valueChangeListener="#{acompanhamentoVestibular.carregarMunicipios }" 
						rendered="#{acompanhamentoVestibular.brasil}">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</td>
					</c:if>
					<th>Município:</th>
					<td colspan="5">
						
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.municipio.id}" immediate="true"
						id="naturMunicipio" rendered="#{acompanhamentoVestibular.brasil}">
							<f:selectItems value="#{acompanhamentoVestibular.municipiosNaturalidade}" />
						</h:selectOneMenu>
						 
						<h:inputText value="#{acompanhamentoVestibular.obj.municipioNaturalidadeOutro}"
						id="naturMunicipioOutros" size="80" maxlength="80" rendered="#{!acompanhamentoVestibular.brasil}"/>
					</td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario"><a href="#">Documento de Identificação</a></td>
				</tr>
				<tr>
					<th class="obrigatorio" width="20%">Nº do Doc. de Identificação:</th>
					<td>
						<h:inputText value="#{acompanhamentoVestibular.obj.identidade.numero}" id="rg" disabled="#{acompanhamentoVestibular.readOnly}" 
							size="12" maxlength="20"  title="*Nº do Doc. de Identificação"/>
					</td>
					<th class="obrigatorio" width="20%">Órgão de Expedição:</th>
					<td>
						<h:inputText value="#{acompanhamentoVestibular.obj.identidade.orgaoExpedicao}" size="10" 
							disabled="#{acompanhamentoVestibular.readOnly}" id="orgaoExpedicao" maxlength="20" title="*Órgão de Expedição"/>
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th>UF:</th>
					<td>
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.identidade.unidadeFederativa.id}" id="ufRG" disabled="#{acompanhamentoVestibular.readOnly}">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th class="obrigatorio">Data de Expedição:</th>
					<td>
						<t:inputCalendar value="#{acompanhamentoVestibular.obj.identidade.dataExpedicao}"  title="*Data de Expedição do Doc. de Identificação"  
							id="dataExpedicao" renderAsPopup="true"
							renderPopupButtonAsImage="true" size="10" maxlength="10"
							onkeypress="return formataData(this,event)"
							readonly="#{processoSeletivoVestibular.readOnly}"
							popupDateFormat="dd/MM/yyyy"/></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario"><a href="#">Título de Eleitor</a></td>
				</tr>
				<tr>
					<th >Nº do Título:</th>
					<td>
						<h:inputText value="#{acompanhamentoVestibular.obj.tituloEleitor.numero}" id="numeroTitulo" disabled="#{acompanhamentoVestibular.readOnly}" maxlength="20" size="10" onkeyup="return formatarInteiro(this);" /> 
						Zona:<h:inputText value="#{acompanhamentoVestibular.obj.tituloEleitor.zona}" id="zonaTitulo" onkeyup="return formatarInteiro(this);" disabled="#{acompanhamentoVestibular.readOnly}" size="4" maxlength="4" />
					</td>
					<th>Seção:</th>
					<td colspan="4">
						<h:inputText value="#{acompanhamentoVestibular.obj.tituloEleitor.secao}" onkeyup="return formatarInteiro(this);" id="secaoTitulo" disabled="#{acompanhamentoVestibular.readOnly}" size="4" maxlength="4" />
						UF: 
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.tituloEleitor.unidadeFederativa.id}" id="ufIdTitulo" disabled="#{acompanhamentoVestibular.readOnly}">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
						</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario"><a href="#">Certificado Militar</a></td>
				</tr>
				<tr>
					<th> Nº do Certificado Militar:</th>
					<td> <h:inputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.numero }" id="certificadoMilitar_numero" size="34" maxlength="30"/> </td>
					<th> Data de Expedição: </th>
					<td><t:inputCalendar value="#{ acompanhamentoVestibular.obj.certificadoMilitar.dataExpedicao }" title="Data de Expedição do Certificado Militar" size="10" maxlength="10" onkeypress="return formataData(this,event);" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy"  id="certificadoMilitar_data"/></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th> Série:</th>
					<td> <h:inputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.serie }" size="10" maxlength="10" id="certificadoMilitar_serie" /> </td>
					<th> Categoria: </th>
					<td> <h:inputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.categoria }" size="10" maxlength="10" id="certificadoMilitar_categoria"/> </td>
					<th> Órgão:</th>
					<td> <h:inputText value="#{ acompanhamentoVestibular.obj.certificadoMilitar.orgaoExpedicao }" size="10" maxlength="10" id="certificadoMilitar_orgaoExpedicao"/></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario"><a href="#">Endereço</a></td>
				</tr>
				<tr class="linhaCep">
					<th class="obrigatorio">CEP:</th>
					<td colspan="5">
						<h:inputText value="#{acompanhamentoVestibular.obj.enderecoContato.cep}" maxlength="10" size="10" id="endCEP" onkeyup="return formatarInteiro(this);" 
							onblur="formataCEP(this, event, null); ConsultadorCep.consultar();" title="*CEP"/>
						<a href="javascript://nop/" onclick="ConsultadorCep.consultar();"><img src="/sigaa/img/buscar.gif" alt="" /></a>
						<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span> 
						<span id="cepIndicator" style="display: none;"> 
						<img src="/sigaa/img/indicator.gif" alt="" /> Buscando endereço... </span>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Logradouro:</th>
					<td colspan="3">
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.enderecoContato.tipoLogradouro.id}" id="tipoLogradouro" title="*Logradouro">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{tipoLogradouro.allCombo}" />
						</h:selectOneMenu>
						<h:inputText value="#{acompanhamentoVestibular.obj.enderecoContato.logradouro }" maxlength="60" id="logradouro" size="60" />
						</td>
					<th class="obrigatorio" width="25px">N&deg;:</th>
					<td>
						<h:inputText value="#{acompanhamentoVestibular.obj.enderecoContato.numero}" maxlength="8" size="6" id="endNumero" title="*Número"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Bairro:</th>
					<td>
						<h:inputText value="#{acompanhamentoVestibular.obj.enderecoContato.bairro}" maxlength="20" size="20" id="endBairro" title="*Bairro"/>
					</td>
					<th>Complemento:</th>
					<td>
						<h:inputText value="#{acompanhamentoVestibular.obj.enderecoContato.complemento}" maxlength="80" size="20" id="endComplemento" />
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th>UF:</th>
					<td>
						<h:selectOneMenu 
						value="#{acompanhamentoVestibular.obj.enderecoContato.unidadeFederativa.id}" 
						id="ufEnd" onchange="submit()" immediate="true"  
						valueChangeListener="#{acompanhamentoVestibular.carregarMunicipios }">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</td>
					<th>Município:</th>
					<td>
						<h:selectOneMenu value="#{acompanhamentoVestibular.obj.enderecoContato.municipio.id}" id="endMunicipio" immediate="false">
							<f:selectItems value="#{acompanhamentoVestibular.municipiosEndereco}" />
						</h:selectOneMenu>
					</td>
					<td></td>
					<td></td>
				</tr>

				<tr>
					<th>Tel. Fixo:</th>
					<td>
						(<h:inputText value="#{acompanhamentoVestibular.obj.codigoAreaNacionalTelefoneFixo}" maxlength="2" size="1" id="telFixoDDD" converter="#{ shortConverter }"
						 	onkeyup="return formatarInteiro(this);" alt="Código Nacional" title="Código Nacional"/>) 
						<h:inputText value="#{acompanhamentoVestibular.obj.telefone}" maxlength="9" size="9" 
							onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" 
							id="telFixoNumero" alt="Telefone Fixo" title="Telefone Fixo"/>
					</td>
					<th>Tel. Celular:</th>
					<td>
						(<h:inputText onkeyup="return formatarInteiro(this);" converter="#{ shortConverter }" value="#{acompanhamentoVestibular.obj.codigoAreaNacionalTelefoneCelular}" 
							maxlength="2" size="1" id="telCelDDD" alt="Código Nacional" title="Código Nacional" />)
						<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="telCelNumero" value="#{acompanhamentoVestibular.obj.celular}" maxlength="10" 
							size="10" alt="Número do Celular" title="Número do Celular"/>
					</td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton value="Cancelar" action="#{acompanhamentoVestibular.cancelar}" onclick="#{confirm}" id="buttonCancelar"/> &ensp;
						<h:commandButton value="Próximo Passo >>" action="#{acompanhamentoVestibular.submeterDadosPessoais}" id="buttonSubmeterDadosPessoais" /> 
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</div>
		<br>
		<br>
	</h:form>

</f:view>

<script type="text/javascript">
	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro',	'form:endBairro', 'form:endMunicipio', 'form:ufEnd');
</script>

<%@include file="/public/include/rodape.jsp"%>
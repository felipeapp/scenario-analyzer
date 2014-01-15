<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>

<% int[] papeis = {SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG}; %>

<ufrn:checkRole usuario="${sessionScope.usuario}" papeis="<%= papeis %>">
	<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
	<h2><ufrn:subSistema /> > Alterar Dados da Inscrição </h2>
</ufrn:checkRole>

<ufrn:checkNotRole usuario="${ sessionScope.usuario }" papeis="<%= papeis %>">
	<%@include file="/public/include/cabecalho.jsp"%>
</ufrn:checkNotRole>

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
<style type="text/css">

th.obrigatorio{ background: url(/sigaa/img/required.gif) no-repeat right; padding-right: 13px;}

span.obrigatorio{ background: url(/sigaa/img/required.gif) no-repeat right; padding-right: 13px;}

#abas-inscricao {
	width: 80%;
	margin: 0 auto;
}

h3 {
	margin: 2px 0 10px;
} ${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.nome)} 

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

.popUp {
	padding: 5px;
	border: 1px solid #FFC30E;
	width: 200px;
	color: #9C7600;
	background-color: #FFFBB8;
	text-align: left;
	position: absolute;
	left: 0;
	top: -5px;
	visibility: hidden;
	overflow: visible;
	z-index: 100;
}

</style>

<f:view>
	<%-- VERIFICA SE O USUÁRIO ACESSO DIRETAMENTE PELA URL --%>
	<c:if test="${empty inscricaoSelecao.obj || empty inscricaoSelecao.obj.processoSeletivo
	 || inscricaoSelecao.obj.processoSeletivo.id == 0 }">
		<c:redirect context="/sigaa" url="/public"  />
	</c:if>

<br/>

<ufrn:checkNotRole usuario="${ sessionScope.usuario }" papeis="<%= papeis %>">
	<h2> Inscrição em Processo Seletivo</h2>
		<div class="descricaoOperacao">
		<h3>
			<c:choose>
				<%-- SE PROCESSO SELETIVO CURSO LATOS, PÓS E TÉCNICO --%>
				<c:when test="${not empty processoSeletivo.obj.curso}">
					<span class="curso">CURSO DE ${processoSeletivo.obj.curso.descricao}</span> 
					<span class="nivel"> (${processoSeletivo.obj.curso.nivelDescricao})	</span>
				</c:when>
				<%-- SE PROCESSO SELETIVO TRANSFERÊNCIA VOLUNTÁRIA --%>
				<c:otherwise>
					<span class="curso">CURSO DE ${processoSeletivo.obj.matrizCurricular.curso.descricao}</span> 
					<span class="nivel"> (${processoSeletivo.obj.matrizCurricular.curso.nivelDescricao})	</span>
				</c:otherwise>
			</c:choose>
		</h3>
	
		<table>
			<tr>
				<th>Período de Inscrições:</th>
				<td class="periodo"><ufrn:format type="data"
					name="processoSeletivo" property="obj.editalProcessoSeletivo.inicioInscricoes" /> a <ufrn:format
					type="data" name="processoSeletivo" property="obj.editalProcessoSeletivo.fimInscricoes" />
				</td>
			</tr>
	
			<c:if test="${not empty processoSeletivo.obj.editalProcessoSeletivo.idEdital}">
				<tr>
					<th>Edital do Processo:</th>
					<td class="arquivo"><a
						href="${ctx}/verProducao?idProducao=${ processoSeletivo.obj.editalProcessoSeletivo.idEdital}&key=${ sf:generateArquivoKey(processoSeletivo.obj.editalProcessoSeletivo.idEdital) }"
						target="_blank"> Fazer download do arquivo </a></td>
				</tr>
			</c:if>
	
			<c:if test="${not empty processoSeletivo.obj.editalProcessoSeletivo.idManualCandidato}">
				<tr>
					<th>Manual do Candidato:</th>
					<td class="arquivo"><a
						href="${ctx}/verProducao?idProducao=${ processoSeletivo.obj.editalProcessoSeletivo.idManualCandidato}"
						target="_blank"> Fazer download do arquivo </a></td>
				</tr>
			</c:if>
		</table>
		<p>
			<b>Atenção candidato</b>, para confirmar sua inscrição no processo seletivo, informe todos os dados corretamente. 
			Serão de inteira responsabilidade do candidato os prejuízos advindos de informações incorretas.
			A sua inscrição só será finalizada com o <b>comprovante</b>, que deve ser <b>impresso</b> para sua maior segurança.
		</p>
		</div>
</ufrn:checkNotRole>

	<h:form id="form" enctype="multipart/form-data">
		<table class="formulario" style="width: 85%;">
			<caption>Formulário de Inscrição</caption>
			<tbody>
				<tr>
					<td colspan="6" class="subFormulario">Dados Pessoais</td>
				</tr>
				<tr>
					<th width="20%" class="${pessoaInscricao.obj.estrangeiro?'':'obrigatorio'}">CPF:</th>
					<td colspan="5">
						<h:inputText
						value="#{pessoaInscricao.obj.cpf}" size="14" maxlength="14"
						onkeypress="return formataCPF(this, event, null)"  id="txtCPF">
							<f:converter converterId="convertCpf" />
						</h:inputText>
					</td>
				</tr>
				<tr>
					<th></th>
					<td colspan="5">
						<h:selectBooleanCheckbox id="estrangeiro" value="#{pessoaInscricao.obj.estrangeiro}"
						onclick="submit()" immediate="true">
							
						</h:selectBooleanCheckbox> A pessoa é estrangeira e não possui CPF
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td colspan="5"><h:inputText
						value="#{pessoaInscricao.obj.nome}" size="80" maxlength="100"
						id="nome" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">E-mail:</th>
					<td colspan="5"><h:inputText
						value="#{pessoaInscricao.obj.email}" size="60" maxlength="60"
						id="email" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome da Mãe:</th>
					<td colspan="5"><h:inputText
						value="#{pessoaInscricao.obj.nomeMae}" size="80" maxlength="100"
						id="nomeMae" /></td>
				</tr>
				<tr>
					<th>Nome do Pai:</th>
					<td colspan="5"><h:inputText
						value="#{pessoaInscricao.obj.nomePai}" size="80" maxlength="100"
						id="nomePai" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Sexo:</th>
					<td><h:selectOneRadio value="#{pessoaInscricao.obj.sexo}"
						id="sexo" style="border:0px;">
						<f:selectItem itemValue="M" itemLabel="Masculino" />
						<f:selectItem itemValue="F" itemLabel="Feminino" />
					</h:selectOneRadio></td>
					<th width="25%" class="obrigatorio">Data de Nascimento:</th>
					<td ><t:inputCalendar
						value="#{pessoaInscricao.obj.dataNascimento}" size="10"
						maxlength="10" onkeypress="return formataData(this,event);"
						id="nascimento" renderAsPopup="true" 
						renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" /></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th class="obrigatorio">Estado Civil:</th>
					<td><h:selectOneMenu
						value="#{pessoaInscricao.obj.estadoCivil.id}" id="estadoCivil">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{estadoCivil.allCombo}" />
					</h:selectOneMenu></td>
					<th>Etnia:</th>
					<td><h:selectOneMenu
						value="#{pessoaInscricao.obj.tipoRaca.id}" id="raca">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoRaca.allCombo}" />
					</h:selectOneMenu></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="5" height="5px"></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Naturalidade</td>
				</tr>
				<tr>
					<th class="obrigatorio" width="22%">País:</th>
					<td width="25%"><h:selectOneMenu
						value="#{pessoaInscricao.obj.pais.id}" id="naturalPais"
						valueChangeListener="#{pessoaInscricao.alterarPais}"
						onchange="submit()">
						<f:selectItems value="#{pais.allCombo}" />
					</h:selectOneMenu></td>

					<th class="${(!pessoaInscricao.obj.estrangeiro && pessoaInscricao.brasil)?'obrigatorio':''}" width="20%"><c:if test="${pessoaInscricao.brasil}">UF:</c:if>
					
					<td colspan="2"><c:if test="${pessoaInscricao.brasil}">
						<h:selectOneMenu
							value="#{pessoaInscricao.obj.unidadeFederativa.id}"
							id="ufIdNatural" onchange="submit()" immediate="true"
							valueChangeListener="#{pessoaInscricao.carregarMunicipios }"
							rendered="#{pessoaInscricao.brasil}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
						</h:selectOneMenu>
					</c:if>
					<td></td>
				</tr>

				<tr>
					<th class="obrigatorio">Município:</th>
					<td colspan="3"><c:if test="${pessoaInscricao.brasil}">
						<h:selectOneMenu value="#{pessoaInscricao.obj.municipio.id}" immediate="true"
							id="naturalMunicipio" label="Município de Naturalidade" 
							rendered="#{pessoaInscricao.brasil}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{pessoaInscricao.municipiosNaturalidade}" />
						</h:selectOneMenu>
					</c:if> <c:if test="${not pessoaInscricao.brasil}">
						<h:inputText
							value="#{pessoaInscricao.obj.municipioNaturalidadeOutro}"
							id="naturalMunicipioOutros" size="80" maxlength="80" />
					</c:if></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="5" height="5px"></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Documentos</td>
				</tr>
				<tr>
					<th  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}" width="20%">RG:</th>
					<td width="25%"><h:inputText
						value="#{pessoaInscricao.obj.identidade.numero}" id="rg"
						disabled="#{pessoaInscricao.readOnly}" size="12" maxlength="20"
						onkeyup="return formatarInteiro(this);"	 /></td>
					<th  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}" width="20%">Órgão de Expedição:</th>
					<td><h:inputText
						value="#{pessoaInscricao.obj.identidade.orgaoExpedicao}" size="10"
						disabled="#{pessoaInscricao.readOnly}" id="orgaoExpedicao"
						maxlength="20" /></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}">UF:</th>
					<td><h:selectOneMenu
						value="#{pessoaInscricao.obj.identidade.unidadeFederativa.id}"
						id="ufRG" disabled="#{pessoaInscricao.readOnly}">
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
					</h:selectOneMenu></td>
					<th  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}">Data de Expedição:</th>
					<td><t:inputCalendar
						value="#{pessoaInscricao.obj.identidade.dataExpedicao}" size="10"
						maxlength="10" onkeypress="return formataData(this,event);" id="dataExpedicaoIdentidade"
						renderAsPopup="true" 
						renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" /></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="5" height="5px"></td>
				</tr>
				<tr>
					<th >Título de Eleitor:</th>
					<td><h:inputText
						value="#{pessoaInscricao.obj.tituloEleitor.numero}"
						id="numeroTitulo" disabled="#{pessoaInscricao.readOnly}"
						maxlength="20" size="15"
						onkeyup="return formatarInteiro(this);" /></td>
					<td align="left">Zona:
					 	<h:inputText
						value="#{pessoaInscricao.obj.tituloEleitor.zona}" id="zonaTitulo"
						onkeyup="return formatarInteiro(this);"
						disabled="#{pessoaInscricao.readOnly}" size="4" maxlength="4" />
					</td>
					<td>
						Seção: <h:inputText
						value="#{pessoaInscricao.obj.tituloEleitor.secao}"
						onkeyup="return formatarInteiro(this);" id="secaoTitulo"
						disabled="#{pessoaInscricao.readOnly}" size="4" maxlength="4" />
					</td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th >UF:</th>
					<td><h:selectOneMenu
						value="#{pessoaInscricao.obj.tituloEleitor.unidadeFederativa.id}"
						id="ufIdTitulo" disabled="#{pessoaInscricao.readOnly}">
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
					</h:selectOneMenu>
					</td>
					<th>Data de Emissão:</th>
					<td><t:inputCalendar
						value="#{pessoaInscricao.obj.tituloEleitor.dataExpedicao}" size="10"
						maxlength="10" onkeypress="return formataData(this,event);" id="dataExpedicaoTitulo"
						renderAsPopup="true" 
						renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" /></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="5" height="5px"></td>
				</tr>
				
				<tr>
					<th class="${pessoaInscricao.obj.estrangeiro?'obrigatorio':''}">
						Passaporte:
					</th>
					<td colspan="5">
						<h:inputText
						value="#{pessoaInscricao.obj.passaporte}"  maxlength="20"
						size="20" id="passaporte" disabled="#{pessoaInscricao.readOnly}" />
					</td>
				</tr>
			
				<tr>
					<td colspan="5" height="5px"></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario">Endereço</td>
				</tr>
				<tr class="linhaCep">
					<th  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}">CEP:</th>
					<td colspan="5"><h:inputText
						value="#{pessoaInscricao.obj.enderecoResidencial.cep}"
						maxlength="10" size="10" id="endCEP"
						onkeyup="return formatarInteiro(this);"
						onblur="formataCEP(this, event, null); ConsultadorCep.consultar();"/>
					<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
					<img src="/sigaa/img/buscar.gif" alt="" /> </a> <span class="info">(clique
					na lupa para buscar o endereço do CEP informado)</span> <span
						id="cepIndicator" style="display: none;"> <img
						src="/sigaa/img/indicator.gif" alt="" /> Buscando endereço... </span></td>
				</tr>
				<tr>
					<th  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}">Logradouro:</th>
					<td colspan="3">
					<h:selectOneMenu
						value="#{pessoaInscricao.obj.enderecoResidencial.tipoLogradouro.id}" id="tipoLogradouro">
						<f:selectItems value="#{tipoLogradouro.allCombo}" />
					</h:selectOneMenu>
					&nbsp;
					<h:inputText
						value="#{pessoaInscricao.obj.enderecoResidencial.logradouro }"
						maxlength="60" id="logradouro" size="60" /></td>
					<th  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}" align="left">N.&deg;:</th>
					<td><h:inputText
						value="#{pessoaInscricao.obj.enderecoResidencial.numero}"
						maxlength="8" size="6" id="endNumero"
						onkeyup="return formatarInteiro(this);" /></td>
				</tr>

				<tr>
					<th  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}">Bairro:</th>
					<td><h:inputText
						value="#{pessoaInscricao.obj.enderecoResidencial.bairro}"
						maxlength="20" size="20" id="endBairro" /></td>
					<th>Complemento:</th>
					<td><h:inputText
						value="#{pessoaInscricao.obj.enderecoResidencial.complemento}"
						maxlength="80" size="20" id="endComplemento" /></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<a4j:region>
					<th class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}">UF:</th>
					<td>
						<h:selectOneMenu
						value="#{pessoaInscricao.obj.enderecoResidencial.unidadeFederativa.id}"
						id="ufEnd" disabled="#{pessoaInscricao.readOnly}"  valueChangeListener="#{pessoaInscricao.carregarMunicipios}"
						onchange="submit()" immediate="true">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />	
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
						<a4j:support event="onchange" reRender="endMunicipio" />
						</h:selectOneMenu>
					</td>
					</a4j:region>
					<th width="100px"  class="${!pessoaInscricao.obj.estrangeiro?'obrigatorio':''}">Município:</th>
					<td colspan="3">
						<h:selectOneMenu value="#{pessoaInscricao.obj.enderecoResidencial.municipio.id}" 
							id="endMunicipio" label="Município do Endereço">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{pessoaInscricao.municipiosEndereco}" />
						</h:selectOneMenu>
					</td>
				
				</tr>

				<tr>
					<th>Tel. Fixo:</th>
					<td>(<h:inputText
						value="#{pessoaInscricao.obj.codigoAreaNacionalTelefoneFixo}"
						maxlength="3" size="1" id="telFixoDDD"
						onkeyup="return formatarInteiro(this);" />) <h:inputText
						onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" maxlength="9" size="9"
						onkeyup="return formatarInteiro(this);" id="telFixoNumero" /></td>
					<th>Tel. Celular:</th>
					<td>(<h:inputText onkeyup="return formatarInteiro(this);"
						value="#{pessoaInscricao.obj.codigoAreaNacionalTelefoneCelular}"
						maxlength="3" size="1" id="telCelDDD" />) <h:inputText
						onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="telCelNumero"
						value="#{pessoaInscricao.obj.celular}" maxlength="10" size="10" /></td>
					<td></td>
					<td></td>
				</tr>
				<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.curso}" >
					<c:if test="${processoSeletivo.parametrosProgramaPos.solicitarAreaLinhaProcessoSeletivo or processoSeletivo.parametrosProgramaPos.solicitarOrientadorProcessoSeletivo}">
					<tr>
						<td colspan="6" class="subFormulario"> Outras Informações para o Processo Seletivo </td>
					</tr>
					</c:if>				
	
					<c:if test="${processoSeletivo.parametrosProgramaPos.solicitarAreaLinhaProcessoSeletivo}">
					<tr>
						<th class="obrigatorio" width="25%">Linha de Pesquisa:</th>
						<td colspan="5">
							<a4j:region>
								<h:selectOneMenu id="linhaPesquisa" value="#{inscricaoSelecao.obj.linhaPesquisa.id}">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{processoSeletivo.possiveisLinhas}" />
									<a4j:support event="onchange"
										action="#{inscricaoSelecao.atualizaAreaConcentracao}"
										reRender="areaConcentracao, labelAreaConcentracao" />
								</h:selectOneMenu>
								<a4j:status>
									<f:facet name="start">
										<h:graphicImage value="/img/indicator.gif" />
									</f:facet>
								</a4j:status>
							</a4j:region>
						</td>
					</tr>
					<tr>
						<th width="25%">
							<h:panelGrid id="labelAreaConcentracao" width="100%">
								<c:if test="${inscricaoSelecao.obj.linhaPesquisa.id != 0}">
									<b>Área de Concentração:</b>
								</c:if>
							</h:panelGrid>
						</th>
						<td colspan="5"><h:panelGrid id="areaConcentracao">
							<c:if test="${inscricaoSelecao.obj.linhaPesquisa.id != 0}">
								<c:if
									test="${not empty inscricaoSelecao.obj.areaConcentracao}">
									<h:outputText
										value="#{inscricaoSelecao.obj.areaConcentracao.denominacao}" />
								</c:if>
								<c:if
									test="${empty inscricaoSelecao.obj.areaConcentracao}">
									<h:outputText
										value="Não há área de concentração cadastrada para a linha de pesquisa escolhida" />
								</c:if>
							</c:if>
						</h:panelGrid></td>
					</tr>
					</c:if>
	 
					<c:if test="${processoSeletivo.parametrosProgramaPos.solicitarOrientadorProcessoSeletivo}">
					<tr>
						<th width="20%">Indique provável orientador:</th>
						<td colspan="5">
							<h:selectOneMenu id="orientador" value="#{inscricaoSelecao.obj.orientador.id}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{processoSeletivo.equipeDoPrograma}" />
							</h:selectOneMenu>
						</td>
					</tr>
					</c:if> 
					
					<c:if test="${processoSeletivo.parametrosProgramaPos.solicitaProjetoNaInscricao}">
					<tr>
						<th class="${inscricaoSelecao.obj.id == 0 ? 'obrigatorio' : ''}">Projeto de Trabalho de Pesquisa (arquivo PDF):</th>
						<td colspan="5">
							<t:inputFileUpload value="#{inscricaoSelecao.arquivoProjeto}" />	
							<ufrn:help img="/img/ajuda.gif" over="true">
								É obrigatório o envio de um arquivo, no formato PDF, de um Projeto de Trabalho de Pesquisa. 
								Caso seu processador de texto não exporte no formato PDF, recomendamos instalar o
								<a href="${inscricaoSelecao.enderecoExportarPDF}" target="_blank">PDFCreator</a> 
								(${inscricaoSelecao.enderecoExportarPDF}). 
							</ufrn:help>					
						</td>
					</tr>
					</c:if> 
				</c:if>

				<%-- SE HOUVER QUESTIONÁRIO, CASO SEJA TRANSFERÊNCIA VOLUNTÁRIA E ÁREA HUMANÍSTICA II  --%>
				<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.questionario && processoSeletivo.obj.questionario.ativo}">
					<tr>
						<td colspan="5" height="5px"></td>
					</tr>
					<tr>
						<td colspan="6" class="subFormulario"> ${inscricaoSelecao.obj.processoSeletivo.questionario.titulo} </td>
					</tr>
					<tr>
						<td colspan="6">
							<%@include file="/geral/questionario/_formulario_respostas.jsp" %>
						</td> 
					</tr>
				</c:if>
				
				<%-- SE PROCESSO SELETIVO TRANSFERÊNCIA VOLUNTÁRIA --%>
				<c:if test="${not empty processoSeletivo.obj.matrizCurricular && processoSeletivo.obj.possuiAgendamento }">
					<tr>
						<td colspan="5" height="5px"></td>
					</tr>
					<tr>
						<td colspan="6" class="subFormulario">Agendamento para Entrega dos Documentos</td>
					</tr>
					<tr>
						<th class="obrigatorio">Data:</th>
						<td colspan="5">
							<h:selectOneMenu id="datasDisponiveis" value="#{inscricaoSelecao.obj.agenda.id}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{agendaProcessoSeletivo.datasAgendamento}" />
							</h:selectOneMenu>
						</td>
					</tr>	
				</c:if>
				<tr>
					<td colspan="5" height="5px"></td>
				</tr>
				<tr>
					<td colspan="6" class="subFormulario"> Observações </td>
				</tr>
				<tr>
					<td colspan="6" align="center">
						<p style="margin: 5px auto; text-align: center; color: #666;">
							Utilize o campo abaixo para informar eventuais observações solicitadas na descrição deste processo seletivo 
						</p>					
						<h:inputTextarea value="#{inscricaoSelecao.obj.observacoes}" rows="3" 
							style="width: 95%; margin: 5px auto;" />
					</td>
				</tr>

			</tbody>
			<tfoot>
				<tr>
					<td colspan="6">
						
						<h:commandButton value="#{inscricaoSelecao.confirmButton}" action="#{inscricaoSelecao.inscrever}" 
							onclick="return confirm('Confirma a submissão de sua inscrição para este processo seletivo?');"/> 
							&nbsp;
						<h:commandButton value="Cancelar" immediate="true" action="#{inscricaoSelecao.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>
	</h:form>

	<c:if test="${not empty sessionScope.usuario}">
		<c:if test="${hideSubsistema == null}">
		<br />
		<div style="width: 80%; text-align: center; margin: 0 auto;">
			<ufrn:subSistema/>
		</div>
		<br />
		</c:if>
	</c:if>

</f:view>

<script type="text/javascript">
	var posProcessamento = function() {
	
	};

	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro',
			'form:endBairro', 'form:endMunicipio', 'form:ufEnd', posProcessamento);
</script>

<%@include file="/public/include/rodape.jsp"%>
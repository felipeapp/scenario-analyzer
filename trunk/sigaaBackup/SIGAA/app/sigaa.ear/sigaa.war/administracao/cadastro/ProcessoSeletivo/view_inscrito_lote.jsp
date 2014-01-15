<%@page isELIgnored ="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/struts-logic" prefix="logic"  %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/ajax" prefix="ajax" %>
<%@taglib uri="/tags/jawr" prefix="jwr"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@page contentType="text/html; charset=ISO-8859-1" %>

<jsp:useBean id="dataAtual" class="java.util.Date" scope="request" />
<c:set var="ctx" value="<%= request.getContextPath() %>"/>

<html class="background">

<head> 
	<title>Sistema Integrado de Gestão de Atividades Acadêmicas</title>
	
	<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
	<script type="text/javascript">
		JAWR.loader.style('/bundles/css/sigaa_base.css','all');
		JAWR.loader.style('/css/ufrn_relatorio.css','all');
		JAWR.loader.style('/css/ufrn_print.css', 'print');
		JAWR.loader.script('/bundles/js/sigaa_base.js');
	</script>
	<jwr:style src="/css/geral.css" media="all" />
	<link rel="stylesheet" media="all" href="/shared/css/ufrn.css"/>
	<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />
	<style media="screen">
	  .esconder {
	     display : none;
	  }	
	</style>
	<style media="print">
	  .esconder {
	     display : inline;
	     font-family: "courier new";
	  }	
	</style>					
	<style>
		#abas-inscricao {width: 80%;margin: 0 auto;}
		h3 {margin: 2px 0 10px;}
		h4 {margin: 5px 0;}
		.descricaoOperacao th {font-weight: bold;padding: 0 2px 5px 2px;}
		.curso,.nivel {	text-align: center;	display: block;	}
		.nivel {font-size: 0.9em;text-transform: uppercase;	color: #555;}
		.arquivo a {text-decoration: underline;	color: #404E82;font-variant: small-caps;}
		.periodo {color: #292;font-weight: bold;}
		#form :sexo {border: 0;}
	</style>
</head>
<body>
<f:view>

<c:set var="loteIS" value="#{impressaoInscricaoSelecao.loteInscricaoSelecao}"/>
<c:set var="aqui" value="0"/>
<c:forEach items="#{loteIS}" var="item" varStatus="status">
<div style="page-break-after: always !important;">
<div id="relatorio-container">

		<div id="relatorio-cabecalho">
			<div id="ufrn"><img src="/shared/img/ufrn.gif"/><br/><ufrn:subSistema/></div>
			<div id="sinfo"><img src="/shared/img/sinfo.gif"/>  </div>
			<div id="texto">
				${ configSistema['nomeInstituicao'] }<br>
				Sistema Integrado de Gestão de Atividades Acadêmicas<br>

				<c:if test="${not empty complemento}">
					<c:forEach items="${complemento}" var="complem" varStatus="loop">
						${complem }
					</c:forEach>
				</c:if>
				<br />
				<span class="dataAtual"> Emitido em <ufrn:format type="dataHora" name="dataAtual" /> </span>
			</div>
			<div class="clear"> </div>
		</div>
		<div id="relatorio">

		<br/>

	<h2>Resumo de Inscrição em Processo Seletivo</h2>

	<div class="descricaoOperacao">
	<h3>
	<c:choose>
		<%-- SE PROCESSO SELETIVO CURSO LATOS, PÓS E TÉCNICO --%>
		<c:when test="${not empty item.inscricaoSelecao.processoSeletivo.curso}">
			<span class="curso">CURSO DE ${item.inscricaoSelecao.processoSeletivo.curso.descricao}</span>
			<span class="nivel">(${item.inscricaoSelecao.processoSeletivo.curso.nivelDescricao}) </span>
		</c:when>
		<%-- SE PROCESSO SELETIVO CURSO GRADUAÇÃO --%>
		<c:otherwise>
			<span class="curso">CURSO DE ${item.inscricaoSelecao.processoSeletivo.matrizCurricular.curso.descricao}</span>
			<span class="nivel">(${item.inscricaoSelecao.processoSeletivo.matrizCurricular.curso.nivelDescricao}) </span>
		</c:otherwise>	
	</c:choose>	
	</h3>
	</div>

	<h:form id="form">
		<h3 class="tituloTabela">Dados do Candidato Inscrito</h3>
		<table class="visualizacao" style="width: 100%;" align="center">
			<tr>
				<td colspan="4" class="subFormulario" >Dados da Inscrição</td>
			</tr>
			<tr>
				<th  width="25%">Número de Inscrição:</th>
				<td colspan="3"><h:outputText value="#{item.inscricaoSelecao.numeroInscricao}" /></td>
			</tr>
			<tr>
				<th>Situação:</th>
				<td colspan="3"><h:outputText value="#{item.inscricaoSelecao.descricaoStatus}" /></td>
			</tr>
			<tr>
				<th>Data de Inscrição:</th>
				<td colspan="3"><ufrn:format type="dataHora" valor="${item.inscricaoSelecao.dataInscricao}" /></td>
			</tr>
			<tr>
				<td colspan="4" class="subFormulario">Dados Pessoais</td>
			</tr>
			<tr>
				<th>CPF:</th>
				<td><ufrn:format type="cpf_cnpj"
					valor="${item.inscricaoSelecao.pessoaInscricao.cpf}" /></td>

				<th>Sexo:</th>
				<td>${item.inscricaoSelecao.pessoaInscricao.sexo ==
				'M'?'Masculino':'Feminino'}</td>

			</tr>
			<tr>
				<th>Nome:</th>
				<td colspan="3"><h:outputText
					value="#{item.inscricaoSelecao.pessoaInscricao.nome}" /></td>

			</tr>
			<tr>
				<th>Estado Civil:</th>
				<td>${item.inscricaoSelecao.pessoaInscricao.estadoCivil.descricao}</td>

				<th>Email:</th>
				<td><h:outputText value="#{item.inscricaoSelecao.pessoaInscricao.email}" /></td>
			</tr>
			<tr>
				<th>Data de Nascimento:</th>
				<td><ufrn:format type="data"
					valor="${item.inscricaoSelecao.pessoaInscricao.dataNascimento}" /></td>
			
				<th>Raça:</th>
				<td>${item.inscricaoSelecao.pessoaInscricao.tipoRaca.descricao}</td>
			</tr>
			<tr>
				<th>Nome da Mãe:</th>
				<td colspan="3"><h:outputText
					value="#{item.inscricaoSelecao.pessoaInscricao.nomeMae}" /></td>
			</tr>
			<tr>
				<th>Nome do Pai:</th>
				<td colspan="3"><h:outputText
					value="#{item.inscricaoSelecao.pessoaInscricao.nomePai}" /></td>
			</tr>
			<tr>
				<td colspan="4" class="subFormulario">Naturalidade</td>
			</tr>
			<tr>
				<td colspan="4">
				<table width="100%" class="subFormulario">
					<tr>
						<th width="20%">País:</th>
						<td width="25%">
						${item.inscricaoSelecao.pessoaInscricao.pais.nome}</td>

						<th width="20%">UF:</th>
						<td>
						${item.inscricaoSelecao.pessoaInscricao.unidadeFederativa.descricao}
						</td>
					</tr>

					<tr>
						<th>Município:</th>
						<td colspan="3">
						${item.inscricaoSelecao.pessoaInscricao.municipio.nome}</td>
					</tr>

				</table>
				</td>
			</tr>

			<tr>
				<td colspan="4" class="subFormulario">Documentos</td>
			</tr>
			<tr>
				<td colspan="4">
				<table width="100%" class="subFormulario">
					<tr>
						<th width="20%">RG:</th>
						<td width="25%"><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.identidade.numero}" /></td>
						<th width="20%">Órgão de Expedição:</th>
						<td><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.identidade.orgaoExpedicao}" /></td>
					<tr>
						<th>UF:</th>
						<td>${item.inscricaoSelecao.pessoaInscricao.identidade.unidadeFederativa.descricao}</td>
						<th>Data de Expedição:</th>
						<td><ufrn:format type="data"
							valor="${item.inscricaoSelecao.pessoaInscricao.identidade.dataExpedicao}" /></td>
					</tr>
					<tr>
						<th>Título de Eleitor:</th>
						<td><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.tituloEleitor.numero}" />
						Zona: <h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.tituloEleitor.zona}" />
						</td>
						<th>Seção:</th>
						<td><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.tituloEleitor.secao}" />
						UF:
						
						
						</td>
					</tr>
					<tr>
						<th>Passaporte:</th>
						<td><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.passaporte}" /></td>
					</tr>
				</table>
				</td>
			</tr>

			<tr>
				<td colspan="4" class="subFormulario">Endereço</td>
			</tr>
			<tr>
				<td colspan="4">
				<table width="100%" class="subFormulario">
					<tr class="linhaCep">
						<th>CEP:</th>
						<td colspan="5"><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.enderecoResidencial.cep}" />
						</td>
					</tr>
					<tr>
						<th>Logradouro:</th>
						<td colspan="3">
						${item.inscricaoSelecao.pessoaInscricao.enderecoResidencial.tipoLogradouro.descricao}
						<h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.enderecoResidencial.logradouro}" />
						</td>
						<th>N.&deg;:</th>
						<td><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.enderecoResidencial.numero}" /></td>
					</tr>

					<tr>
						<th>Bairro:</th>
						<td><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.enderecoResidencial.bairro}" /></td>
						<th>Complemento:</th>
						<td><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.enderecoResidencial.complemento}" />
						</td>
					</tr>

					<tr>
						<th>UF:</th>
						<td><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.enderecoResidencial.unidadeFederativa.sigla}" />
						</td>
						<th>Município:</th>
						<td colspan="4"><h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.enderecoResidencial.municipio.nome}" />
						</td>
					</tr>

					<tr>
						<th>Tel. Fixo:</th>

						<td>(<h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.codigoAreaNacionalTelefoneFixo}" />)
						<h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.telefone}" /></td>
						<th>Tel. Celular:</th>
						<td colspan="3">(<h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.codigoAreaNacionalTelefoneCelular}" />)
						<h:outputText
							value="#{item.inscricaoSelecao.pessoaInscricao.celular}" /></td>
					</tr>

					<c:if test="${not empty item.inscricaoSelecao.orientador or not empty item.inscricaoSelecao.linhaPesquisa}">
					<tr>
						<td colspan="6" class="subFormulario"> Outras Informações para o Processo Seletivo </td>
					</tr>
					</c:if>				

					
					<c:if test="${not empty item.inscricaoSelecao.orientador}">
					<tr>
						<th colspan="2" width="20%">Orientador:</th>
						<td colspan="2"><h:outputText
							value="#{item.inscricaoSelecao.orientador.nome}" /></td>
					</tr>
					</c:if>
		
					<c:if test="${not empty item.inscricaoSelecao.linhaPesquisa}">
					<tr>
						<th colspan="2">Linha de pesquisa:</th>
						<td colspan="2"><h:outputText
							value="#{item.inscricaoSelecao.linhaPesquisa.denominacao}" />
						</td>
					</tr>
					</c:if>
					
					<c:if test="${not empty item.inscricaoSelecao.idArquivoProjeto}">
					<tr>
						<th colspan="2">Projeto de Pesquisa:</th>
						<td colspan="2">
							<a href="${ctx}/verProducao?idProducao=${ item.inscricaoSelecao.idArquivoProjeto}&key=${ sf:generateArquivoKey(item.inscricaoSelecao.idArquivoProjeto) }" target="_blank">
								<h:graphicImage value="/img/icones/document_view.png" style="overflow: visible;vertical-align:middle" />Clique aqui para abrir o arquivo
							</a>
						</td>
					</tr>
					</c:if>
		
					<%-- Respostas do Questionário  --%>
	
					<c:if test="${not empty item.inscricaoSelecao.processoSeletivo.questionario}">
						<tr>
							<td colspan="6" class="subFormulario"> ${item.inscricaoSelecao.processoSeletivo.questionario.titulo} </td>
						</tr>
						<tr>
							<td colspan="6">
							<%@include file="/geral/questionario/_respostas_lote.jsp" %>
							</td> 
						</tr>
					</c:if>

					<%-- Observações  --%>
					<c:if test="${not empty item.inscricaoSelecao.observacoes}">
					<tr>
						<td colspan="6" class="subFormulario"> Observações do candidato </td>
					</tr>
					<tr>
						<td colspan="6">
							<ufrn:format type="texto"valor="${item.inscricaoSelecao.observacoes}" /></td>
						</td>
					</tr>	
					</c:if>			

				</table>
				</td>
			</tr>

		</table>
	</h:form>
	</div> 
	<%-- Fim do div relatorio  --%>
	
	<div class="clear"> </div>
	<br/>
	<div id="relatorio-rodape">
		<p>
			<table width="100%">
				<tr>
					<td class="voltar" align="left"><a href="javascript:history.back();"> Voltar </a></td>
					<td width="70%"  align="center">${ configSistema['siglaSigaa']} | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['nomeResponsavelInformatica']} - ${configSistema['siglaInstituicao']} - ${ configSistema['telefoneHelpDesk'] } - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %></td>
					<td class="naoImprimir" align="right">
						<a onclick="javascript:window.print();" href="#">Imprimir</a>
					</td>
					<td class="naoImprimir" align="right">
						<a onclick="javascript:window.print();" href="#">							
							<img alt="Imprimir" title="Imprimir" src="/shared/javascript/ext-1.1/docs/resources/print.gif"/>
						</a>
					</td>
				</tr>
			</table>
		</p>
	</div>
</div>  <%-- Fim do div 'container' --%>
<br/>
</div>  <%-- Fim do div page-break --%>
</c:forEach>
</f:view>
</body>
</html>
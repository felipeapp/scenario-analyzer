<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/jawr" prefix="jwr"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
	<head>
		<title>SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas</title>
		<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
		<script type="text/javascript">
			JAWR.loader.style('/bundles/css/comum_base.css','all');
			JAWR.loader.style('/css/ufrn_print.css', 'print');
	
			JAWR.loader.script('/bundles/css/turma.js');
			JAWR.loader.script('/bundles/js/comum_base.js');
		</script>
		
		<style>
			@import"/sigaa/ava/css/turma.css";
			@import"/sigaa/css/turma-virtual/frequencia-lote.css";
		</style>
	</head>
	
	<body>
	
		<div id="container" style="width: 670px;">
		
			<div id="conteudo">
				<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
		
				<%@ taglib uri="/tags/a4j" prefix="a4j" %>
				<f:view>
					<a4j:keepAlive beanName="frequenciaAluno" />
				
					<h2>Cadastrar frequência da turma utilizando uma planilha</h2>
		
					<fieldset>
						<legend>${frequenciaAluno.turma}</legend>
						
						<c:if test="${frequenciaAluno.cadastrado}">
							<div class="descricaoOperacao">Por favor, feche esta janela.</div>
						</c:if>
						<c:if test="${not frequenciaAluno.cadastrado}">
							<script>
								JAWR.loader.script('/javascript/jquery/jquery.js');
							</script>
						
							<script>
								// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
								var J = jQuery.noConflict();
								var FREQUENCIA_MINIMA = ${frequenciaAluno.parametrosGestora.frequenciaMinima};
								// Recebe as aulas do bean.
								var auxAulas = "${frequenciaAluno.dadosDatasAulasPlanilha}";
								// Recebe as frequências do bean.
								var auxAlunos = "${frequenciaAluno.dadosFrequenciaPlanilha}";
							</script>
					
							<jwr:script src="/javascript/turma-virtual/frequencia-lote.js"/>
														
							<script>
								var aulas = converteEmVetor(auxAulas);
								var alunos = converteEmVetor(auxAlunos);
								
								var totalAulas = ${ frequenciaAluno.turma.numAulas };
					
								// Assim que terminar de receber a página, inicializa a planilha.
								J("document").ready(function (){
									setTimeout("inicializa()", 10);
								});
							</script>
						
							<div id="dialog">
								<br/><br/>
								<p>Por favor, aguarde enquanto a planilha de frequência é gerada ...</p><br/><br/>
								<h:graphicImage value="/img/loading.gif" />
							</div>
							<div id="mascara"></div>
						
							<div class="descricaoOperacao">
								<p>Através deste recurso é possível registrar a frequência dos alunos desta turma para todo o período.</p>
								<p>Para indicar a quantidade de faltas que um aluno recebeu em uma aula, clique sobre a célula que referencia o dia da aula e o nome do aluno. Continue clicando para ir reduzindo a quantidade de faltas até que chegue em zero, indicando que o aluno estava presente.</p>
								<p>Para indicar todas as presenças para uma aula, basta clicar sobre a célula que representa o dia da aula, no cabeçalho da planilha.</p>
								<p>Alunos com mais de ${100-frequenciaAluno.parametrosGestora.frequenciaMinima}% de faltas serão marcados em <span style="color:#FF0000;">vermelho</span>, indicando reprovação por falta.</p>
								
								<c:if test="${ frequenciaAluno.turmaChamadaBiometrica }">
									<p>
										As frequencias dos discentes podem ser <b> lançadas pelo professor manualmente para cada aluno ou os próprios discentes podem registrar sua presença através </b> de computadores
										instalados em sala de aula. O ícone abaixo representa os discentes que registraram sua presença através da própria digital.
									</p>
									
									<p style="text-align:center;margin-top:10px;"><img src="${ ctx }/img/digital1.png" alt="Presença registrada por digital" title="Presença registrada por digital" /></p>
								</c:if>
							</div>
							
							<div style="text-align:center;font-size:15pt;">Planilha de Frequência da Turma</div>
				
							<%-- Div que conterá a planilha. --%>
							<div id="basePlanilha"></div>
							
							<div style="margin-left:auto;margin-right:auto;border:1px solid #CCCCCC;margin-top:10px;width:500px;padding:10px;">
								<table id="legenda">
									<caption style="text-align:left;font-weight:bold;margin-bottom:10px;">Legenda:</caption>
									<tr>
										<td><input value="T" disabled="disabled" style="width:15px;" /></td><td style="padding-right:15px;text-align:center;">: Aluno trancado</td>
										<td class="feriado">1</td><td style="padding-right:15px;">: Feriado</td>	
										<td class="cancelada">1</td><td style="padding-right:15px;">: Aula Cancelada</td>
										<td class="consolidado">1</td><td>: Presenças lançadas</td>
									</tr>
								</table>
							</div>
						
							<h:form id="form" onsubmit="atualizarFrequencias();">
								<h:inputHidden id="frequencias" value="#{frequenciaAluno.dadosFrequenciaPlanilha}" />
								<a4j:poll id="polling" interval="#{frequenciaAluno.tempoSalvamentoPlanilha }" action="#{ frequenciaAluno.cadastrarPlanilhaAutomatico }" onsubmit="avisoSalvoAutomatico()" oncomplete="avisoSalvoAutomaticoCompleto()"/>
								
								<div class="botoes" style="text-align:center;">
									<h:commandButton value="Gravar Frequências" action="#{ frequenciaAluno.cadastrarPlanilha }" onclick="salvar = true;"/> &nbsp;
									<input type="submit" value="Cancelar" onclick="if (!confirm('Tem certeza que deseja cancelar esta operação?')) return false; window.close();" /> 
								</div>
							</h:form>
							
							<script type="text/javascript" charset="UTF-8">
								function avisoSalvoAutomatico() {
									dialogSalvando.show();
								}

								function avisoSalvoAutomaticoCompleto() {
									window.setTimeout("dialogSalvando.hide();", 2000);
								}
							</script>
							
						</c:if>
					</fieldset>
					<p:resources/>
					<link rel="stylesheet" type="text/css" href="/sigaa/ava/primefaces/redmond/skin.css" />
					<p:dialog resizable="false" draggable="false" header="Aviso" widgetVar="dialogSalvando" modal="true" width="350" height="60"> Essa planilha está sendo salva automaticamente! </p:dialog>
					
				</f:view>
		
			</div> <%-- Fim do div 'conteudo' --%>
				
			<div class="clear"></div>

			<br/><br/>
			
			<div style="text-align:center;">
				<br/>
				<a href="#" onclick='<c:if test="${not frequenciaAluno.cadastrado}">if (!confirm("Tem certeza que deseja cancelar esta operação? Todos os dados digitados serão perdidos!")) return false;</c:if> window.close();' class="naoImprimir"><img src="<%= request.getContextPath() %>/img/fechar.jpg" width="85" height="16" alt="Fechar" /></a>
			</div>
			
			<br/><br/><br/>
			
			<div id="rodape" class="naoImprimir">
				<p>	${ configSistema['siglaSigaa']} | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['nomeResponsavelInformatica']} - ${configSistema['siglaInstituicao']}  - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %></p>
			</div>
			
		</div>  <%-- Fim do div 'container' --%>
		
	</body>
</html>
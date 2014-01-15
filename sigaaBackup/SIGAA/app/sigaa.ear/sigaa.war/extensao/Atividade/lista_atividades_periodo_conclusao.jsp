<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript">var J = jQuery.noConflict();</script>
<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h:messages showDetail="true" showSummary="true"/>
	<h2><ufrn:subSistema /> > Monitoramento de Finaliza��o de A��es de Extens�o</h2>

	<div class="descricaoOperacao">Caro Usu�rio,<br />
	<br />
	Esta � a tela de sinaliza��o de final de A��es de Extens�o. Para o
	per�odo informado na busca, o sistema verifica as a��es de extens�o que
	ir�o concluir ou que j� conclu�ram e lista os relat�rios (parcial e final) de cada a��o de extens�o, caso
	eles tenham sido cadastrados e enviados para avalia��o.<br />
	<br />
	<b>Aten��o:</b><br/>
		O sistema permite ainda o envio de e-mails para os Coordenadores de a��es que n�o enviaram o relat�rio final. 
		No e-mail, os coordenadores dos projetos s�o convidados a enviarem o relat�rio final.<br/><br/>
		
		Para enviar os e-mails para os Coordenadores basta selecionar a A��o de Extens�o e clicar no bot�o 'Notificar Coordenadores'. 
		Ser� permitido notificar o mesmo Coordenador somente uma vez a cada 10 (dez) dias. 
		Portanto, a caixinha para selecionar a a��o de extens�o (cujo coordenador ser� notificado) s� estar� dispon�vel se o coordenador
		da a��o n�o tiver sido notificado nos �ltimos 10 (dez) dias e se o coordenador ainda n�o enviou	o relat�rio Final.
		<br/>
		<br/>
		<b>Obs:</b> A��es de Extens�o que n�o possuem o email do coordenador para contato n�o podem ser selecionadas.		      
	 </div>

	<h:form id="form">

		<table class="formulario" width="90%" id="busca">
			<caption>Busca por A��es de Extens�o</caption>
			<tbody>
				<tr>
					<th class="required">Per�odo de Conclus�o:</th>

					<td><t:inputCalendar
						value="#{atividadeExtensao.buscaInicioConclusao}"
						renderAsPopup="true" renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" size="10" id="dataInicioConclusao"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						maxlength="10" popupTodayString="Hoje �">
						<f:converter converterId="convertData" />
					</t:inputCalendar> a <t:inputCalendar value="#{atividadeExtensao.buscaFimConclusao}"
						renderAsPopup="true" renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" size="10" id="dataFimConclusao"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						maxlength="10" popupTodayString="Hoje �">
						<f:converter converterId="convertData" />
					</t:inputCalendar></td>
				</tr>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar"	action="#{ atividadeExtensao.buscarAtividadesPeriodoConclusao }" id="btBuscar" />
						<h:commandButton value="Cancelar" action="#{ atividadeExtensao.cancelar }" onclick="#{confirm}" id="btCancelar" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>

		<br />
		<br />

		<c:if test="${ not empty atividadeExtensao.atividadesLocalizadas}">

			<div class="infoAltRem">
				<h:graphicImage width="19px" value="/img/warning.gif" 
					style="overflow: visible;" />: Coordenador N�o Possui Email
				<h:graphicImage value="/img/email_disabled.png" 
					style="overflow: visible;" />: �ltima Data de Notifica��o
				<h:graphicImage value="/img/view.gif"
					style="overflow: visible;" />: Visualizar A��o de Extens�o 
				<br>
				<h:graphicImage
					value="/img/extensao/printer.png" style="overflow: visible;" />:
				Vers�o para Impress�o
				<h:graphicImage
					value="/img/extensao/form_green.png" style="overflow: visible;" />:
				Relat�rio Enviado
				<h:graphicImage
					value="/img/extensao/form_green_forbidden.png" style="overflow: visible;" />:
				Relat�rio cadastrado, mas n�o enviado
			</div>
			<br />
			<table class="listagem tablesorter" id="listagem">
				<caption>A��es de extens�o localizadas (${fn:length(atividadeExtensao.atividadesLocalizadas) })</caption>

				<thead>
					<tr>
						<th><h:selectBooleanCheckbox value="false"  onclick="checkAll(this)"/></th>
						<th>Notif.</th>
						<th width="10%">C�digo</th>
						<th>T�tulo</th>
						<th width="8%">In�cio</th>
						<th width="8%">Fim</th>
						<th width="10%">Situa��o</th>
						<th width="5%">Relat�rios</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>

					<c:forEach items="#{atividadeExtensao.atividadesLocalizadas}" var="atividade" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">

							<td>
               					<h:selectBooleanCheckbox value="#{atividade.selecionado}" styleClass="check" rendered="#{atividade.permitidoEnviarEmailNotificacaoFaltaDeRelatorioFinal}"/>
		            	   	</td>
		            	   	<td>
		            	   		<h:graphicImage value="/img/email_disabled.png" rendered="#{atividade.dataEmailCobrancaRelatorioFinal != null && atividade.coordenacao.pessoa.email != null}" title="�ltima notifica��o enviada em: #{atividade.dataCobrancaRelFormatada}"/>
		            	   		<h:graphicImage width="19px" value="/img/warning.gif" rendered="#{atividade.coordenacao.pessoa.email == null}" title="Coordenador n�o possui email." />
		            	   	</td>
							<td>${atividade.codigo}</td>
							<td><b>${atividade.titulo}</b> 
								<h:outputText
									value="<br/><i>Coordenador(a): #{atividade.coordenacao.pessoa.nome}</i>"
									rendered="#{not empty atividade.coordenacao}" escape="false" id="nomeCoord"/>
								<h:outputText
									value="<br/><i>Email Coordenador(a): #{atividade.coordenacao.pessoa.email}</i>"
									rendered="#{not empty atividade.coordenacao}" escape="false" id="emailCoord"/>
								<h:outputText
									value="<br/><i>Dimens�o Acad�mica: #{atividade.projetoAssociado ? 'ASSOCIADO' : 'EXTENS�O'}</i>"
									escape="false" id="dimensao"/>
								<h:outputText
									value="<br/><i>Situa��o: #{atividade.projeto.situacaoProjeto.descricao}</i>"
									escape="false" id="situacao"/>										
							</td>

							<td align="center"><fmt:formatDate value="${atividade.projeto.dataInicio}" pattern="dd/MM/yyyy" /></td>
							<td align="center"><fmt:formatDate value="${atividade.projeto.dataFim}" pattern="dd/MM/yyyy" /></td>
							<td>
								<c:if test="${!atividade.enviouRelatorioFinal}">
									<c:if test="${!atividade.prazoExpiradoParaConclusao}">Finalizar�<br/> em <b>${atividade.totalDiasConclusao}</b> dias </c:if>
									<c:if test="${atividade.prazoExpiradoParaConclusao}">Finalizou<br/> a <font color='red'><b>${atividade.totalDiasConclusao * -1}</b></font> dias</c:if>
								</c:if>
								<c:if test="${atividade.enviouRelatorioFinal}">
									Conclu�do
								</c:if>			
							</td>

							<td align="center">
								<c:if test="${not empty atividade.relatorios}">
										<c:forEach items="#{atividade.relatorios}" var="rel" >
													<c:if test="${not empty rel.dataEnvio}">
														<h:commandLink title="Visualizar #{rel.tipoRelatorio.descricao}"	action="#{ relatorioAcaoExtensao.view }" id="vwRelatorioEnviado">
															<f:param name="id" value="#{rel.id}" />
															<h:graphicImage url="/img/extensao/form_green.png" />
														</h:commandLink>
													</c:if> 
													<c:if test="${empty rel.dataEnvio}">
														<h:commandLink title="Visualizar #{rel.tipoRelatorio.descricao}"	action="#{ relatorioAcaoExtensao.view }" id="vwRelatorioCadastrado">
															<f:param name="id" value="#{rel.id}" />
															<h:graphicImage url="/img/extensao/form_green_forbidden.png"/>
														</h:commandLink>														
													</c:if>													
										</c:forEach>
								</c:if>
							</td>

							<td><h:commandLink title="Visualizar A��o"
								action="#{ atividadeExtensao.view }" id="btViewAcao">
								<f:param name="id" value="#{atividade.id}" />
								<h:graphicImage url="/img/view.gif" />
							</h:commandLink></td>

							<td><h:commandLink title="Vers�o para impress�o"
								action="#{ atividadeExtensao.view }" id="btViewImpessao">
								<f:param name="id" value="#{atividade.id}" />
								<f:param name="print" value="true" />
								<h:graphicImage url="/img/extensao/printer.png" />
							</h:commandLink></td>
						</tr>
					</c:forEach>
				</tbody>
				
				<tfoot>
						<tr>
							<td colspan="10">
								<center>
									<h:commandButton id="notificarCoordenadores" value="Notificar Coordenadores >>" action="#{atividadeExtensao.redirecionarPaginaNotificacaoCoordenador}" />
									<h:commandButton value="Cancelar"	action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" />
								</center>
							</td>
						</tr>
				</tfoot>
			</table>			
		</c:if>
		<center><i><h:outputText rendered="#{empty atividadeExtensao.atividadesLocalizadas}" value="Nenhuma A��o localizada"/></i></center>
		<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: {0: { sorter: false }, 1: { sorter: false }, 4: {sorter: 'shortDate'}, 5: {sorter: 'shortDate'}, 6: {sorter: false}, 7: {sorter: false}, 8: { sorter: false }, 9: { sorter: false } } });" timing="onload" />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>



<script type="text/javascript">
function checkAll(elem) {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = elem.checked;
	});
}
</script>
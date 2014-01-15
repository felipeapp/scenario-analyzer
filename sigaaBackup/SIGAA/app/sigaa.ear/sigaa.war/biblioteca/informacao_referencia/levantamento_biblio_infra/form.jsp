<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.Linguas" %>
<%@page import="br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.PeriodoLevantBibliografico" %>

<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Levantamento Bibliogr�fico</h2>
	
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao" style="width:80%;">
		<c:if test="${levantamentoBibliograficoInfraMBean.alterar}">
			<p>
			Utilize este formul�rio para atender esta solicita��o. A pesquisa pode ser modificada e salva v�rias vezes
			at� que voc� escolha <em>Sim</em> no campo <em>Finalizar pesquisa e enviar e-mail para o solicitante</em>.
			</p>
			<p> 
			Tamb�m � poss�vel anexar um arquivo se for necess�rio.
			</p>
		</c:if>
		
		<c:if test="${not levantamentoBibliograficoInfraMBean.alterar}">
			<p>
			Utilize este formul�rio para solicitar o levantamento bibliogr�fico de interesse individual,
			ou para compor relat�rios do MEC, Capes, etc.
			</p>
		</c:if>
	</div>
	
	<h:form id="form" enctype="multipart/form-data">
	
		<h:inputHidden value="#{levantamentoBibliograficoInfraMBean.alterar}" />
		<h:inputHidden value="#{levantamentoBibliograficoInfraMBean.obj.id}" />
	
		<table class="formulario" width="80%">
			<caption>Solicita��o de Levantamento Bibliogr�fico</caption>
			<tbody>
				
				<!-- SE FOR USU�RIO DO SIR PREENCHENDO OS DADOS DO LEVANTAMENTO QUE FOI SOLICITADO, DESABILITO O FORM -->
				<tr>
					<td>
						<table width="100%" class="subFormulario">
							<tr>
								<th class='${not levantamentoBibliograficoInfraMBean.alterar ? "obrigatorio" : ""}'>
									Biblioteca:
								</th>
								<td>
									<c:if test="${not levantamentoBibliograficoInfraMBean.alterar}">
										<h:selectOneMenu value="#{levantamentoBibliograficoInfraMBean.obj.bibliotecaResponsavel.id}">
											<f:selectItem itemLabel=" == Selecione == " itemValue="0" />
											<f:selectItems value="#{levantamentoBibliograficoInfraMBean.bibliotecasDoUsuarioCombo}" />
										</h:selectOneMenu>
									</c:if>
									
									<c:if test="${levantamentoBibliograficoInfraMBean.alterar}">
										${levantamentoBibliograficoInfraMBean.obj.bibliotecaResponsavel.descricao }
									</c:if>
								</td>
							</tr>
							
							<c:if test="${levantamentoBibliograficoInfraMBean.alterar}">
								<tr>
									<th>Assunto solicitado:</th>
									<td colspan="2">
										<h:outputText value="#{levantamentoBibliograficoInfraMBean.obj.detalhesAssunto}" />
									</td>
								</tr>
							</c:if>
							
							<c:if test="${not levantamentoBibliograficoInfraMBean.alterar}">
								<tr><td colspan="2">Especifique detalhes do seu assunto: <span class='obrigatorio'>&nbsp;</span>
										<ufrn:help>Descrever o t�pico principal, definindo qualquer termo que tenha especial significado.</ufrn:help></td></tr>
								<tr><td colspan="2">
									<h:inputTextarea
										value="#{levantamentoBibliograficoInfraMBean.obj.detalhesAssunto}"
										cols="100" rows="12" />
								</td></tr>
							</c:if>
							
							<tr>
								<th class='${not levantamentoBibliograficoInfraMBean.alterar ? "obrigatorio" : ""}'>
									L�nguas aceitas:
								</th>
								<td>
									<h:selectManyCheckbox 
											id="linguas"
											onclick="linguasOnClick(this);"
											value="#{levantamentoBibliograficoInfraMBean.linguasSelecionadas}"
											disabled="#{levantamentoBibliograficoInfraMBean.alterar}"
											enabledClass="linguas"
											disabledClass="linguas">
										<f:selectItems value="#{levantamentoBibliograficoInfraMBean.allLinguas}" />
									</h:selectManyCheckbox>
								</td>
							</tr>
							
							<tr id="outrasLinguas" style="display: ${ empty(levantamentoBibliograficoInfraMBean.obj.outraLinguaDescricao) ? 'none' : 'table-row'};">
								<c:if test="${ not levantamentoBibliograficoInfraMBean.alterar }">
									<th class="obrigatorio">Informe as outras l�nguas:</th>
									<td>
										<h:inputText
												value="#{levantamentoBibliograficoInfraMBean.obj.outraLinguaDescricao}"
												maxlength="75" disabled="#{levantamentoBibliograficoInfraMBean.alterar}" />
									</td>
								</c:if>
								<c:if test="${ levantamentoBibliograficoInfraMBean.alterar }">
									<th>Outras l�nguas:</th>
									<td>
										<h:outputText value="#{levantamentoBibliograficoInfraMBean.obj.outraLinguaDescricao}" />
									</td>
								</c:if>
							</tr>
							
							<tr>
								<th class='${not levantamentoBibliograficoInfraMBean.alterar ? "obrigatorio" : ""}'>Per�odo:</th>
								<td>
									<h:selectOneRadio
											id="periodo"
											value="#{levantamentoBibliograficoInfraMBean.obj.periodo}"
											disabled="#{levantamentoBibliograficoInfraMBean.alterar}"
											onclick="periodoOnClick(this);">
										<f:selectItems value="#{levantamentoBibliograficoInfraMBean.allPeriodos}"/>
									</h:selectOneRadio>
									
									<span id="outroPeriodo" style="display: ${
												empty(levantamentoBibliograficoInfraMBean.obj.outroPeriodoDescricao) ? 'none' : 'inline'}">
										<c:if test="${not levantamentoBibliograficoInfraMBean.alterar}">
											<strong>Informe o per�odo: </strong>
											<h:inputText
													value="#{levantamentoBibliograficoInfraMBean.obj.outroPeriodoDescricao}"
													maxlength="40"
													disabled="#{levantamentoBibliograficoInfraMBean.alterar}" />
										</c:if>
										<c:if test="${levantamentoBibliograficoInfraMBean.alterar}">
											<h:outputText value="#{levantamentoBibliograficoInfraMBean.obj.outroPeriodoDescricao}"/>
										</c:if>
									</span>
								</td>
							</tr>
							
							<c:if test="${levantamentoBibliograficoInfraMBean.autorizadoInfra && !levantamentoBibliograficoInfraMBean.alterar}">
								<tr>
									<th>Solicita��o de Infra-estrutura?</th>
									<td>
										<h:selectOneRadio
												value="#{levantamentoBibliograficoInfraMBean.obj.infra}"
												disabled="#{levantamentoBibliograficoInfraMBean.alterar}">
											<f:selectItem itemValue="true" itemLabel="Sim" />
											<f:selectItem itemValue="false" itemLabel="N�o" />
										</h:selectOneRadio>
									</td>
								</tr>
							</c:if>
							<c:if test="${levantamentoBibliograficoInfraMBean.alterar}">
								<tr>
									<th>Solicita��o de Infra-estrutura?</th>
									<td>${levantamentoBibliograficoInfraMBean.obj.infra ? "Sim" : "N�o"}</td>
								</tr>
							</c:if>
							
							
						</table>
						
						<c:if test="${levantamentoBibliograficoInfraMBean.alterar}">
							<table width="100%" class="subFormulario">
								<caption>Atendimento da Solicita��o</caption>
								
								<tr>
									<td>
										<strong>Fontes pesquisadas:</strong><span class="obrigatorio">&nbsp;</span><br />
										<h:inputTextarea value="#{levantamentoBibliograficoInfraMBean.fontesPesquisadas}" cols="100" rows="12" />
									</td>
								</tr>
								
								<tr>
									<td>
										<strong>Observa��es:</strong><br />
										<h:inputTextarea value="#{levantamentoBibliograficoInfraMBean.observacoes}" cols="100" rows="12" />
									</td>
								</tr>
								
								<tr>
									<td><strong>Arquivo:</strong> <t:inputFileUpload value="#{levantamentoBibliograficoInfraMBean.arquivo}" /></td>
								</tr>
								
								<tr>
									<td>
										<b>Finalizar pesquisa e enviar e-mail para o solicitante?</b>
										<t:selectOneMenu id="email" value="#{levantamentoBibliograficoInfraMBean.finalizarEnviarEmail}" styleClass="noborder">
											<f:selectItem itemLabel="N�o" itemValue="false"/>
											<f:selectItem itemLabel="Sim" itemValue="true"/>
										</t:selectOneMenu>
									</td>
								</tr>
							</table>
						</c:if>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
					
						<c:if test="${levantamentoBibliograficoInfraMBean.alterar}">
							<h:commandButton value="Cadastrar Pesquisa Realizada" action="#{levantamentoBibliograficoInfraMBean.alterar}" />
							<h:commandButton value="<< Voltar" action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantFuncBiblioteca}" />
						</c:if>
						
						<c:if test="${not levantamentoBibliograficoInfraMBean.alterar}">
							<h:commandButton value="Solicitar Levantamento Bibliografico" action="#{levantamentoBibliograficoInfraMBean.cadastrarSolicitacaoIndividual}" />
							<h:commandButton value="<< Voltar" action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantIndividualPorUsuario}" />
						</c:if>
						
						<h:commandButton value="Cancelar" action="#{levantamentoBibliograficoInfraMBean.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</f:view>

<script type="text/javascript" >
<!--

function linguasOnClick( elem ) {
	elem = $(elem);
	if ( elem.value == <%= Linguas.OUTRAS %> ) {
		if ( elem.checked )
			$('outrasLinguas').show();
		else
			$('outrasLinguas').hide();
	}
}

function periodoOnClick( elem ) {
	elem = $(elem);
	if ( elem.value == <%= PeriodoLevantBibliografico.OUTROS %> )
		$('outroPeriodo').show();
	else
		$('outroPeriodo').hide();
}

-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var JQ = jQuery.noConflict();
</script>
<f:view>
	<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>
	<h2> <ufrn:subSistema /> > Reposição de Avaliação > Analisar Solicitações</h2>	
	<c:set var="exibirApenasSenha" value="true" scope="request" />	
	
<h:form id="form">
			
<div class="descricaoOperacao">
	<p><b>Caro Professor,</b></p>
	<p>Nesta tela serão exibidas todas as solicitações de reposição de avaliação.</p>
	<p>Solicitações que já tiverem sido submetidas à chefia serão listadas, mas não poderá ser dado um novo parecer.</p>
	<p>Após o parecer ser dado, será enviado um e-mail para a chefia do departamento e para os discentes envolvidos no processo.</p>
	<p>As reposições só serão confirmadas após serem homologadas pela chefia do departamento.</p>
	<p>Após a reposição ser homologada pela chefia, será enviado um e-mail para os discentes informando a data, hora e local da prova.</p>
</div>

<c:if test="${empty solicitacaoReposicaoProva.listaSolicitacoes}">		
<table class="listagem" width="100%">
	<tr>
		<td align="center"><i>Nenhum Solicitação Cadastrada.</i></td>
	</tr>
</table>
</c:if>			
	
<c:if test="${not empty solicitacaoReposicaoProva.listaSolicitacoes}">			
<div class="infoAltRem">
	<h:graphicImage value="/img/link.gif" style="overflow: visible;"/>: Ver Anexo
	<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicitação
</div>

<table class="formulario" width="100%">
	<caption>Informe os Dados da Análise</caption>
	<tbody>
		<tr>
			<th width="20%" class="obrigatorio">Situação:</th>
			<td>
				<h:selectOneMenu value="#{solicitacaoReposicaoProva.novoStatus.id}" id="status">
					<f:selectItem itemValue="0" itemLabel="-- Selecione a Situação --"/>
					<f:selectItems value="#{solicitacaoReposicaoProva.statusCombo}"/>
					<a4j:support event="onchange" reRender="dataProva, labeldataProva, labelData, labelHora, hora, labelLocal, localProva, labelObs"/>						
				</h:selectOneMenu>			
			</td>
		</tr>
			<tr>
				<td colspan="2">
					<table width="100%">					
						<tr>
							<th width="20%">Data da Avaliação:
								<h:panelGroup id="labeldataProva">
									<h:outputText styleClass="obrigatorio" escape="false" rendered="#{solicitacaoReposicaoProva.novoStatus.deferido}"/>
								</h:panelGroup>							
							</th>
							<td>
								<h:panelGroup id="labelData">
									<t:inputCalendar id="dataAvaliacao" value="#{solicitacaoReposicaoProva.dataProva}" disabled="#{!solicitacaoReposicaoProva.novoStatus.deferido}" 
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupTodayString="Hoje é" maxlength="10" title="Data da Avaliação">
										<f:convertDateTime pattern="dd/MM/yyyy"/>
									</t:inputCalendar>
								</h:panelGroup>
								&nbsp;
								Hora da Avaliação:
								<h:panelGroup id="labelHora">
									<h:outputText styleClass="obrigatorio" escape="false" rendered="#{solicitacaoReposicaoProva.novoStatus.deferido}"/>
								</h:panelGroup>								
								<h:inputText value="#{solicitacaoReposicaoProva.horaProva}" disabled="#{!solicitacaoReposicaoProva.novoStatus.deferido}" size="5" maxlength="5" title="Hora" onkeypress="return formataHora(this, event)" onkeyup="return formataHora(this, event)" id="hora">
									<f:convertDateTime pattern="HH:mm" />
								</h:inputText>HH:mm 							
							</td>			
						</tr>	
							<tr>
								<th>Local:
									<h:panelGroup id="labelLocal">
										<h:outputText styleClass="obrigatorio" escape="false" rendered="#{solicitacaoReposicaoProva.novoStatus.deferido}"/>
									</h:panelGroup>								
								</th>
								<td>
									<h:inputText id="localProva" disabled="#{!solicitacaoReposicaoProva.novoStatus.deferido}" value="#{solicitacaoReposicaoProva.obj.localProva}" size="60"/>
								</td>
							</tr>																	
					</table>				
				</td>
			</tr>
		<tr>
			<th style="width: 20%;">
				Observação:
				<h:panelGroup id="labelObs">
					<h:outputText styleClass="obrigatorio" escape="false" rendered="#{solicitacaoReposicaoProva.novoStatus.indeferido}"/>
				</h:panelGroup>				
			</th>
			<td>
				<h:inputTextarea cols="100" rows="5" id="observacao" value="#{ solicitacaoReposicaoProva.observacao}"/>
			</td>
		</tr>			
		<tr>
			<td colspan="2">
				<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp" %>	
			</td>
		</tr>
	</tbody>
</table>
	<br/>
	<table class="listagem,subFormulario" width="100%">
		<caption style="text-align:center;" class="subFormulario">Selecione os Alunos que se enquadraram na Situação Informada</caption>
		<thead>
			<tr>
				<th><input type="checkbox" title="Selecionar Todos" id="checkTodos" onclick="checkAll()"/></th>
				<th>Data da Solicitação</th>
				<th style="width:35%;">Discente</th>
				<th style="text-align: center;">Curso</th>
				<th style="text-align: center;width:10%;">Avaliação</th>
				<th style="text-align: center;">Data da Avaliação</th>
				<th  style="text-align: center;">Status</th>
				<th colspan="2"></th>
			</tr>
		</thead>
		<tbody>
		<c:set var="turma" value="0"/>
		<c:forEach items="#{solicitacaoReposicaoProva.listaSolicitacoes}" var="solicitacoes" varStatus="loop">

			<c:if test="${turma != solicitacoes.turma.id}">
				<tr>
					<td colspan="9" style="background-color: #C8D5EC;">									
						<b>${solicitacoes.turma.descricaoCodigo}</b>
					</td>
				</tr>		
			</c:if>	
			<c:set var="turma" value="#{solicitacoes.turma.id}"/>
			
			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="height: 40px;">
				<td>	
					<c:if test="${empty solicitacoes.statusParecer}">
					 	<h:selectBooleanCheckbox id="selecionado" styleClass="check" value="#{solicitacoes.selecionado}"/>	
					 </c:if>	
				</td>
				<td><ufrn:format type="dataHora" valor="${solicitacoes.dataCadastro}"/></td>
				<td>
					<h:outputText value="#{solicitacoes.discente.matriculaNome}"/>
				</td>
				<td  style="text-align: center;">${solicitacoes.discente.curso.descricao}</td>
				<td style="text-align: center;">
				   ${solicitacoes.dataAvaliacao.descricao}
				</td>
				<td style="text-align: center;">
					<ufrn:format type="data" valor="${solicitacoes.dataAvaliacao.data}"/>
				</td>							
				<td>${solicitacoes.descricaoStatusDocente}</td>
				<td style="width: 1px;">
					<c:if test="${not empty solicitacoes.idArquivo}">
						<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${solicitacoes.idArquivo}">
							<img src="/sigaa/img/link.gif" title="Ver Anexo" /> 
						</html:link>
					</c:if>									
				</td>
				<td style="width: 1px;">
					<h:commandButton image="/img/view.gif" title="Visualizar Solicitação"
							action="#{solicitacaoReposicaoProva.view}" styleClass="noborder">
							<f:setPropertyActionListener value="#{solicitacoes}" target="#{solicitacaoReposicaoProva.obj}"/>
					</h:commandButton>							
				</td>							
			</tr>	
			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td colspan="9">
					<b>Justificativa:</b> <i>${solicitacoes.justificativa}</i>							
				</td>						
			</tr>													
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="9" style="text-align:center;background-color:#C8D5EC;padding: 3px;">
					<h:commandButton value="Confirmar" action="#{solicitacaoReposicaoProva.confirmarParecer}"/>
					<h:commandButton value="Cancelar" action="#{solicitacaoReposicaoProva.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>	
		</tfoot>	
	</table>

<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</c:if>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script>

function checkAll() {
	JQ('.check').each(function(e) {
		var checkAll = JQ("#checkTodos").attr("checked");
		if (!checkAll)
			checkAll = false;
		JQ(this).attr("checked",checkAll);
	});
}

</script>
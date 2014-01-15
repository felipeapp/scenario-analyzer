<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var JQ = jQuery.noConflict();
</script>
<f:view>
	<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>
	<h2> <ufrn:subSistema /> > Reposição de Avaliação > Apreciar Solicitações</h2>
<h:form id="form">
<div class="infoAltRem">
	<h:graphicImage value="/img/link.gif" style="overflow: visible;"/>: Ver Anexo
	<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicitação
	<h:graphicImage value="/img/prodocente/baixo.gif" style="overflow: visible;"/>: Visualizar Detalhes
	<h:graphicImage value="/img/prodocente/cima.gif" style="overflow: visible;"/>: Ocultar Detalhes
</div>
<table class="formulario" width="100%">
	<caption>Informe os Dados para Apreciação</caption>
	<tbody>
		<tr>
			<th style="width: 20%;" class="obrigatorio">Situação:</th>
			<td>
				<h:selectOneMenu value="#{solicitacaoReposicaoProva.novoStatus.id}" id="status">
					<f:selectItem itemValue="0" itemLabel="-- Selecione a Situação --"/>
					<f:selectItems value="#{solicitacaoReposicaoProva.statusCombo}"/>			
					<a4j:support event="onchange" reRender="labelObs"/>		
				</h:selectOneMenu>			
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
				<h:inputTextarea cols="100" rows="5" id="observacao" value="#{ solicitacaoReposicaoProva.observacao }"/>
			</td>
		</tr>			
		<tr>
			<td colspan="2" class="subFormulario">Selecione os Alunos que se enquadraram na Situação Informada</td>
		</tr>	
		<tr>
			<td colspan="2">
				<table class="listagem">
					<thead>
						<tr>
							<th><input type="checkbox" id="checkTodos" onclick="checkAll()"/></th>
							<th>Data da Solicitação</th>
							<th>Discente</th>
							<th>Curso</th>
							<th>Situação</th>
							<th colspan="3"></th>
						</tr>
					</thead>
					
					<c:set var="aval" value="0"/>
					<c:forEach items="#{solicitacaoReposicaoProva.listaSolicitacoes}" var="solicitacoes" varStatus="loop">
						<c:if test="${aval != solicitacoes.dataAvaliacao.id}">
							<tr>
								<td colspan="8" style="background-color: #C8D5EC">
									<c:set var="aval" value="#{solicitacoes.dataAvaliacao.id}"/>
									<b>${solicitacoes.turma.descricaoCodigo} - 
									Avaliação: ${solicitacoes.dataAvaliacao.descricao}</b>
								</td>
							</tr>		
						</c:if>
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>	
								<h:selectBooleanCheckbox id="selecionado" styleClass="check" value="#{solicitacoes.selecionado}"/>			
							</td>
							<td><ufrn:format type="dataHora" valor="${solicitacoes.dataCadastro}"/></td>
							<td>
								<h:outputText value="#{solicitacoes.discente.matriculaNome}"/>
							</td>
							<td>
								<h:outputText value="#{solicitacoes.discente.curso.descricao}"/>
							</td>
							<td>
								<c:if test="${empty solicitacoes.statusParecer}">
									<span style="color: red; font-weight: bold;">Parecer Pendente</span>
								</c:if> 
								<c:if test="${not empty solicitacoes.statusParecer}">
									${solicitacoes.statusParecer.descricao}
								</c:if> 							
							</td>
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
							<td nowrap="nowrap"> 
								<a href="javascript: void(0);" id="link_${solicitacoes.id}" onclick="habilitarDetalhes(${solicitacoes.id});" title="Visualizar Detalhes">							
									<img id="img1_${solicitacoes.id}" style="display: inline;" src="${ctx}/img/prodocente/baixo.gif" />
									<img id="img2_${solicitacoes.id}" style="display: none;" src="${ctx}/img/prodocente/cima.gif" />
								</a>
							</td>								
						</tr>
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td colspan="8">
								<b>Justificativa:</b> <i>${solicitacoes.justificativa}</i>							
							</td>						
						</tr>						
						<tr align="center">
							<td colspan="8">
								<div id="linha_${solicitacoes.id}" style="display: none; width: 80%;" >
									<table width="100%" class="subFormulario">
										<caption>Dados do Parecer do Docente</caption>
										<c:if test="${empty solicitacoes.statusParecer}">
											<tr>
												<td style="text-align: center;">
													<i style="color: red;">Não foi registrado parecer para esta solicitação.</i>
												<td>
											</tr>
										</c:if>
										<c:if test="${not empty solicitacoes.statusParecer}">
											<c:if test="${solicitacoes.statusParecer.deferido}">
												<tr>							
													<th style="width: 30%; font-weight: bold;">Data da Avaliação Sugerida:</th>
													<td>
														<ufrn:format type="data" valor="${solicitacoes.dataProvaSugerida }"/>
													</td>	
												</tr>	
												<tr>							
													<th style="width: 30%; font-weight: bold;">Hora da Avaliação Sugerida:</th>
													<td>
														<ufrn:format type="hora" valor="${solicitacoes.dataProvaSugerida }"/>
													</td>	
												</tr>		
												<tr>							
													<th  style="width: 30%; font-weight: bold;">Local da Avaliação:</th>
													<td>
														<p>${solicitacoes.localProva}</p>
													</td>	
												</tr>																																				
											</c:if>
											<c:if test="${not empty solicitacoes.observacaoDocente}">
												<tr>							
													<th  style="width: 30%; font-weight: bold;">Observação:</th>
													<td>
														<p>${solicitacoes.observacaoDocente}</p>
													</td>	
												</tr>	
											</c:if>
										</c:if>							
									</table>
								</div>							
							</td>
						</tr>											
					</c:forEach>
				</table>
			</td>
		</tr>
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Próximo Passo >>" action="#{solicitacaoReposicaoProva.confirmarHomologacao}"/>
			<h:commandButton value="<< Selecionar outra Turma" action="#{solicitacaoReposicaoProva.voltarHomologacao}"/>
			<h:commandButton value="Cancelar" action="#{solicitacaoReposicaoProva.cancelar}" onclick="#{confirm}"/>
		</td>
	</tr>	
	</tfoot>
</table>
<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script>
function habilitarDetalhes(id) {
	var linha = 'linha_'+ id;
	if ( JQ('#'+linha).css('display') == 'none' ) {
		JQ('#'+linha).css('display', 'inline-block');
		JQ('#img1_'+id).css('display', 'none');			
		JQ('#img2_'+id).css('display', 'block');
		JQ('#link_'+id).attr('title', 'Ocultar Detalhes');				
	} else {
		JQ('#'+linha).css('display', 'none');
		JQ('#img2_'+id).css('display', 'none');			
		JQ('#img1_'+id).css('display', 'inline-block');
		JQ('#link_'+id).attr('title', 'Visualizar Detalhes');
	}
}

function checkAll() {
	JQ('.check').each(function(e) {
		JQ(this).attr("checked",JQ("#checkTodos").attr("checked"));
	});
}

</script>
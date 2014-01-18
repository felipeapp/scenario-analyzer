<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var JQ = jQuery.noConflict();
</script>
<f:view>
	<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>
	<h2> <ufrn:subSistema /> > Homologar Solicitações de Docente</h2>
<h:form id="form">
<div class="infoAltRem">
	<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicitação
</div>
<table class="formulario" width="100%">
	<caption>Informe os Dados para Homologação</caption>
	<tbody>
		<tr>
			<th style="width: 20%;" class="obrigatorio">Situação:</th>
			<td>
				<h:selectOneMenu value="#{solicitacaoDocenteRedeMBean.novoStatus.id}" id="status">
					<f:selectItems value="#{solicitacaoDocenteRedeMBean.statusCombo}"/>			
				</h:selectOneMenu>			
			</td>
		</tr>
		<tr>
			<th style="width: 20%;">
				Parecer:	
			</th>
			<td>
				<h:inputTextarea cols="100" rows="5" id="observacao" value="#{ solicitacaoDocenteRedeMBean.atendimentoGeral }"/>
			</td>
		</tr>			
		<tr>
			<td colspan="2" class="subFormulario">Selecione os Docentes que se enquadraram na Situação Informada</td>
		</tr>	
		<tr>
			<td colspan="2">
				<table class="listagem">
					<thead>
						<tr>
							<th width="2%"><input type="checkbox" id="checkTodos" onclick="checkAll()"/></th>
							<th width="15%">Data da Solicitação</th>
							<th>Docente</th>
							<th width="7%">Tipo</th>
							<th width="7%">Situação</th>
							<th width="2%"></th>
						</tr>
					</thead>
					
					<c:set var="aval" value="0"/>
					<c:forEach items="#{solicitacaoDocenteRedeMBean.solicitacoes}" var="s" varStatus="loop">
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>	
								<input type="checkbox" name="selecionados" id="check_${s.id}" value="${s.id}" class="check"/>
							</td>
							<td><ufrn:format type="dataHora" valor="${s.dataSolicitacao}"/></td>
							<td>
								<h:outputText value="#{s.docente.nome}"/>
							</td>
							<td>
								<h:outputText value="#{s.docente.tipo.descricao}"/>
							</td>
							<td>
								<h:outputText value="#{s.docente.situacao.descricao}"/>
							</td>
							<td style="width: 1px;">
								<h:commandLink title="Visualizar Solicitação" action="#{solicitacaoDocenteRedeMBean.viewSolicitacao}">
									<h:graphicImage value="/ava/img/zoom.png" width="16" />
									<f:param name="idSolicitacao" value="#{s.id}"></f:param>
								</h:commandLink>						
							</td>																			
						</tr>
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td colspan="6">
								<b>Justificativa:</b> <i>${s.observacao}</i>							
							</td>						
						</tr>
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td colspan="6">
								<b>Tipo Requerido:</b> <span style="color:red;font-weight:bold;"><i>${s.tipoRequerido.descricao}</i></span>							
							</td>						
						</tr>							
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td colspan="6">
								<b>Situação Requerida:</b> <span style="color:red;font-weight:bold;"><i>${s.situacaoRequerida.descricao}</i></span>							
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
			<h:commandButton value="<< Selecionar outra Instituição" action="#{solicitacaoDocenteRedeMBean.telaInstituicoes}"/>
			<h:commandButton value="Cancelar" action="#{solicitacaoDocenteRedeMBean.cancelar}" onclick="#{confirm}"/>
			<h:commandButton value="Próximo Passo >>" action="#{solicitacaoDocenteRedeMBean.selecionarSolicitacoes}"/>
		</td>
	</tr>	
	</tfoot>
</table>
<br/>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script>

function checkAll() {
	JQ('.check').each(function(e) {
		JQ(this).attr("checked",JQ("#checkTodos").attr("checked"));
	});
}

</script>
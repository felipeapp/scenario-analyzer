<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var JQ = jQuery.noConflict();
</script>
<f:view>
	<h2> <ufrn:subSistema /> > Homologar Solicitações de Docente</h2>
<h:form id="form">

	<div class="descricaoOperacao">
		<b>Caro usuário,</b> 
		<br/><br/>
		Esta é a tela final para homologação de docentes. Após clicar em "confirmar" todas as solicitações listadas abaixo serão <h:outputText value="#{ solicitacaoDocenteRedeMBean.novoStatus.descricao }"/>S
	</div>	

<table class="formulario" width="100%">
	<caption>Homologação de Solicitações de Docentes</caption>
	<tbody>
		<tr>
			<td colspan="2" class="subFormulario">Dados para Homologação</td>
		</tr>	
		<tr>
			<th width="20%" style="font-weight:bold">Nova Situação:</th>
			<td>
				<h:outputText value="#{ solicitacaoDocenteRedeMBean.novoStatus.descricao }"/>
			</td>
		</tr>
		<tr>
			<th style="font-weight:bold">Parecer:</th>
			<td>
				<h:outputText value="#{ solicitacaoDocenteRedeMBean.atendimentoGeral }"/>
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
							<th width="15%">Data da Solicitação</th>
							<th>Docente</th>
							<th width="20%">Tipo</th>
							<th width="20%">Situação</th>
						</tr>
					</thead>
					
					<c:set var="aval" value="0"/>
					<c:forEach items="#{solicitacaoDocenteRedeMBean.solicitacoesEscolhidas}" var="s" varStatus="loop">
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
		
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
																							
						</tr>
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td colspan="4">
								<b>Justificativa:</b> <i>${s.observacao}</i>							
							</td>						
						</tr>	
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td colspan="4">
								<b>Tipo Requerido:</b> <span style="color:red;font-weight:bold;"><i>${s.tipoRequerido.descricao}</i></span>							
							</td>						
						</tr>						
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td colspan="4">
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
			<h:commandButton value="Confirmar" action="#{solicitacaoDocenteRedeMBean.confirmarHomologacao}"/>
			<h:commandButton value="<< Selecionar outra Instituição" action="#{solicitacaoDocenteRedeMBean.telaInstituicoes}"/>
			<input type="button" value="<< Selecionar outras Solicitações" onclick="javascript:history.go(-1)" />
			<h:commandButton value="Cancelar" action="#{solicitacaoDocenteRedeMBean.cancelar}" onclick="#{confirm}"/>
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
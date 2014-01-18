<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var JQ = jQuery.noConflict();
</script>
<f:view>
	<h2> <ufrn:subSistema /> > Homologar Solicitações de Docente</h2>
<h:form id="form">

	<table class="formulario" width="70%">
		<caption>Solicitação</caption>
		
		<c:set var="aval" value="0"/>
		<c:forEach items="#{solicitacaoDocenteRedeMBean.solicitacoesEscolhidas}" var="s" varStatus="loop">
			<tr>
				<th width="20%"><b>Solicitador:</b></th>
				<td><h:outputText value="#{s.usuario.nome}"/></td>
			</tr>
			<tr>
				<th><b>Data da Solicitação:</b></th>
				<td><ufrn:format type="dataHora" valor="${s.dataSolicitacao}"/></td>
			</tr>
			<tr>	
				<th><b>Docente:</b></th>
				<td>
					<h:outputText value="#{s.docente.nome}"/>
				</td>
			</tr>
			<tr>
				<th><b>Tipo:</b></th>
				<td>
					<h:outputText value="#{s.docente.tipo.descricao}"/>
				</td>
			</tr>
			<tr>
				<th><b>Situação:</b></th>
				<td>
					<h:outputText value="#{s.docente.situacao.descricao}"/>
				</td>
			</tr>																	
			<tr>
				<th><b>Justificativa:</b></th>
				<td>
					${s.observacao}							
				</td>						
			</tr>
			<tr>
				<th><b>Tipo Requerida:</b> </th>
				<td>
					<span style="color:red;font-weight:bold;"><i>${s.tipoRequerido.descricao}</i></span>							
				</td>						
			</tr>						
			<tr>
				<th><b>Situação Requerida:</b> </th>
				<td>
					<span style="color:red;font-weight:bold;"><i>${s.situacaoRequerida.descricao}</i></span>							
				</td>						
			</tr>			
		</c:forEach>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Selecionar Solicitações" action="#{solicitacaoDocenteRedeMBean.telaSolicitacoes}"/>
					<h:commandButton value="Cancelar" action="#{solicitacaoDocenteRedeMBean.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>	
		</tfoot>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
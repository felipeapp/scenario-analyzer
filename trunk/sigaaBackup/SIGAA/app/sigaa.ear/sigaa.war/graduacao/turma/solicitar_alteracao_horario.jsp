<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form id="formDocentes">
	<h:outputText value="#{analiseSolicitacaoTurma.create }" />
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema/> &gt; <h:outputText value="#{analiseSolicitacaoTurma.descricaoOperacao}"/></h2>

	<table class="visualizacao" >

		<tr>
			<th>Componente:</th>
			<td>${analiseSolicitacaoTurma.obj.componenteCurricular.descricao}</td>
		</tr>

		<tr>
			<th>Horário solicitado:</th>
			<td>${analiseSolicitacaoTurma.obj.horario}</td>
		</tr>
		<c:if test="${analiseSolicitacaoTurma.obj.turmaRegular}">
			<tr>
				<th>Vagas:</th>
				<td>${analiseSolicitacaoTurma.obj.vagas}</td>
			</tr>
		</c:if>
	</table>
	<br />

	<table class="formulario" width="650">
	<caption><h:outputText value="#{analiseSolicitacaoTurma.descricaoOperacao}"/></caption>

	<tr>
		<th class="required">
			<h:outputText value="Motivo/Observação:" rendered="#{analiseSolicitacaoTurma.negarSolicitacaoTurma}" />
			<h:outputText value="Sugestão/Observação:" rendered="#{not analiseSolicitacaoTurma.negarSolicitacaoTurma}" />
		</th>
		<td>
			<h:inputTextarea id="txtSolicitacao" rows="4" cols="80" value="#{ analiseSolicitacaoTurma.obj.observacoes }"/>
		</td>
	</tr>

	<tfoot>
		<tr><td colspan="2">
			<h:commandButton value="#{analiseSolicitacaoTurma.descricaoOperacao}" action="#{ analiseSolicitacaoTurma.gravarOperacao }"/>
			<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ analiseSolicitacaoTurma.cancelar }"/>
		</td></tr>
	</tfoot>
	</table>
	</h:form>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
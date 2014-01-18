<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
		<a4j:keepAlive beanName="alterarSituacaoMatriculaRede"/>
<h2> 	<ufrn:subSistema /> &gt; Consolidar Turma
</h2>

	<h:form> 
	
	<div class="descricaoOperacao">
		<b>Caro usuário,</b> 
		<br/><br/>
		<c:if test="${alterarSituacaoMatriculaRede.turma.aberta}">
			Confirme as matrículas para a consolidação da turma.
		</c:if>
		<c:if test="${!alterarSituacaoMatriculaRede.turma.aberta}">
			Esta turma já foi consolidada.
		</c:if>
		<br/><br/>
	</div>
	
	<c:set var="turma" value="${alterarSituacaoMatriculaRede.turma}" />
	<%@include file="/ensino_rede/modulo/turma/info_turma.jsp"%>
	
	
	<table class="formulario" style="width: 80%">
		<c:if test="${alterarSituacaoMatriculaRede.turma.aberta}">
			<caption> Confira os dados para efetuar a consolidação de turma </caption>
		</c:if>
		<c:if test="${!alterarSituacaoMatriculaRede.turma.aberta}">
			<caption> Situação dos Discentes </caption>
		</c:if>
		<tbody>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Matrículas escolhidas</caption>
					<thead>
						<tr>
							<td>Discente</td>
							<td width="15%">Status</td>
						</tr>
					</thead>
					<c:forEach items="${alterarSituacaoMatriculaRede.matriculasEscolhidas}" var="mat" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>${mat.discente.nome}</td>
						<td>${mat.situacao.descricao }</td>
					</tr>
					</c:forEach>
				</table>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" action="#{alterarSituacaoMatriculaRede.confirmar}" rendered="#{alterarSituacaoMatriculaRede.turma.aberta}" id="efetuar"/>
					<h:commandButton value="<< Voltar" action="#{alterarSituacaoMatriculaRede.voltarAlterarSituacao}" rendered="#{alterarSituacaoMatriculaRede.turma.aberta}" id="voltar"/>
					<h:commandButton value="<< Selecionar Outra Turma" action="#{alterarSituacaoMatriculaRede.voltarListarTurmas}" rendered="#{!alterarSituacaoMatriculaRede.acessoPortal}" id="outra"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alterarSituacaoMatriculaRede.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
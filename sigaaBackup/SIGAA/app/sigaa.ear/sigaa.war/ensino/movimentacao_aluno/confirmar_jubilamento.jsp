<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="jubilamentoMBean"></a4j:keepAlive>
<h2><ufrn:subSistema /> > Cancelamento de Alunos em Lote</h2>
<div class="descricaoOperacao">
		<p>
			Este caso de uso permite ao gestor de ensino Cancelar o Programa de Discentes. O Cancelamento de programa é a desvinculação de aluno regular do curso de graduação sem que tenha integralizado as exigências mínimas para sua conclusão. O cancelamento de programa acarreta o cancelamento da matrícula em todos os componentes curriculares nos quais o aluno esteja matriculado.
	
			Os cancelamentos podem ser por dois motivos(tipos):	Abandono do Curso ou por decurso de prazo máximo para conclusão do curso.
		</p>
			<p>
				<b>Abandono do Curso</b> - Caracteriza-se abandono de curso por parte do aluno quando, em um período letivo regular no qual o programa não esteja trancado, o aluno não realizar sua matricula online no período estabelecido no calendário acadêmico, ou ainda, trancar sua matricula ou reprovar em todos os componentes curriculares no qual esteja matriculado.
			</p>
		
		<c:if test="${jubilamentoMBean.nivelEnsino == 'G'}">
			<p>
				<b>Decurso de prazo máximo para conclusão do curso</b> - Já o cancelamento por prazo máximo se aplica aos alunos que não concluíram o curso no prazo máximo estabelecido pelo projeto político-pedagógico do curso.
				 Serão compreendidos os discentes com status de <i>ATIVO</i> ou <i>FORMANDO</i> cujo prazo máximo seja igual ou inferior ao ano e período informados para a construção da listagem.
			</p>
			<p>
				<b>Não confirmação de vínculo de ingressantes</b> - O cancelamento por não confirmação de vínculo se aplica aos alunos ingressantes que não tiveram seu vínculo confirmado. Serão compreendidos os discentes com status de <i>ATIVO</i> cujo o ano e período de ingresso sejam informados para construção da listagem.
			</p>
		</c:if>
</div>

<div class="infoAltRem">
	<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />: Visualizar Histórico
</div>

<h:form>

<table class="listagem">
	<caption>Discentes encontrados (${fn:length(jubilamentoMBean.discentesPassiveisJubilamento)})</caption>
	<thead>
		<tr>
			<th style="text-align: center;">Matrícula</th>
			<th>Nome</th>
			<th>Nível</th>
			<th>Status</th>
			<c:if test="${jubilamentoMBean.mostrarUltimaMatriculaValida}"><th style="text-align: center;">Última Matrícula Válida</th></c:if>
			<c:if test="${jubilamentoMBean.mostraPrazoConclusao}"><th style="text-align: center;">Prazo de Conclusão</th></c:if>
			<th></th>
		</tr>
	</thead>
	
	<c:set var="cursoAtual" value="-1" />
	
	<c:set var="colspan" value="6" />
	 
	<c:if test="${jubilamentoMBean.mostrarUltimaMatriculaValida}">
		<c:set var="colspan" value="${colspan+1}" /> 
	</c:if>
	<c:if test="${jubilamentoMBean.mostraPrazoConclusao}">
		<c:set var="colspan" value="${colspan+1}" /> 
	</c:if>
	
	<c:forEach items="#{jubilamentoMBean.discentesPassiveisJubilamento}" var="discente" varStatus="loop">
	
		<c:if test="${cursoAtual ne discente.curso.descricao}">
			<tr>
				<td colspan="${colspan}" class="subFormulario"><h:outputText value="#{discente.curso.descricao}"/></td>
			</tr>		
				<c:set var="cursoAtual" value="${discente.curso.descricao}" />
		</c:if>
	
	
	
		<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td style="text-align: center;"><h:outputText value="#{discente.matricula}" /></td>
			<td><h:outputText value="#{discente.pessoa.nome}"/></td>
			<td><h:outputText value="#{discente.nivelDesc}"/></td>
			<td><h:outputText value="#{discente.statusString}"/></td>
			<c:if test="${jubilamentoMBean.mostrarUltimaMatriculaValida}"> 
				<td style="text-align: center;"><h:outputText value="#{ discente.anoUltimaMatriculaValida }.#{ discente.periodoUltimaMatriculaValida }" /></td> 
			</c:if>
			<c:if test="${jubilamentoMBean.mostraPrazoConclusao}"> 
				<td style="text-align: center;"><c:out value="${fn:substring(discente.prazoConclusao,0,4)}"/>.<c:out value="${fn:substring(discente.prazoConclusao,4,5)}"/></td>
			</c:if>	
			<td>
				<h:commandLink action="#{jubilamentoMBean.verHistorico}" target="_blank" title="Visualizar Histórico" id="btaoVerHistorico">
					<h:graphicImage value="/img/listar.gif"/>
					<f:param name="idDiscente" value="#{discente.id}"/>
				</h:commandLink>			
			</td>
		</tr>
	</c:forEach>	
	<tfoot>
	<tr>
		<td colspan="${colspan}" style="text-align: center;">
		<h:commandButton value="<< Voltar" action="#{ jubilamentoMBean.telaDiscentesPassiveisJubilamento }" id="btaoVoltar"/>		
		<h:commandButton value="Confirmar Cancelamento de Programas" action="#{ jubilamentoMBean.confirmar }" id="btaoCancelamentoPrograma" onclick="return(confirm('Deseja realmente confirmar o cancelamento do programa destes discentes?'));"/>
		</td>
	</tr>
	</tfoot>
</table>

<br/>


</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
function checkAll() {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = !e.checked;
	});
}
</script>
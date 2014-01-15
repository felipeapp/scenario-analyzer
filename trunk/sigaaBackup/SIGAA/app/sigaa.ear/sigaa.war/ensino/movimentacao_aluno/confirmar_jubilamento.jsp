<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="jubilamentoMBean"></a4j:keepAlive>
<h2><ufrn:subSistema /> > Cancelamento de Alunos em Lote</h2>
<div class="descricaoOperacao">
		<p>
			Este caso de uso permite ao gestor de ensino Cancelar o Programa de Discentes. O Cancelamento de programa � a desvincula��o de aluno regular do curso de gradua��o sem que tenha integralizado as exig�ncias m�nimas para sua conclus�o. O cancelamento de programa acarreta o cancelamento da matr�cula em todos os componentes curriculares nos quais o aluno esteja matriculado.
	
			Os cancelamentos podem ser por dois motivos(tipos):	Abandono do Curso ou por decurso de prazo m�ximo para conclus�o do curso.
		</p>
			<p>
				<b>Abandono do Curso</b> - Caracteriza-se abandono de curso por parte do aluno quando, em um per�odo letivo regular no qual o programa n�o esteja trancado, o aluno n�o realizar sua matricula online no per�odo estabelecido no calend�rio acad�mico, ou ainda, trancar sua matricula ou reprovar em todos os componentes curriculares no qual esteja matriculado.
			</p>
		
		<c:if test="${jubilamentoMBean.nivelEnsino == 'G'}">
			<p>
				<b>Decurso de prazo m�ximo para conclus�o do curso</b> - J� o cancelamento por prazo m�ximo se aplica aos alunos que n�o conclu�ram o curso no prazo m�ximo estabelecido pelo projeto pol�tico-pedag�gico do curso.
				 Ser�o compreendidos os discentes com status de <i>ATIVO</i> ou <i>FORMANDO</i> cujo prazo m�ximo seja igual ou inferior ao ano e per�odo informados para a constru��o da listagem.
			</p>
			<p>
				<b>N�o confirma��o de v�nculo de ingressantes</b> - O cancelamento por n�o confirma��o de v�nculo se aplica aos alunos ingressantes que n�o tiveram seu v�nculo confirmado. Ser�o compreendidos os discentes com status de <i>ATIVO</i> cujo o ano e per�odo de ingresso sejam informados para constru��o da listagem.
			</p>
		</c:if>
</div>

<div class="infoAltRem">
	<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />: Visualizar Hist�rico
</div>

<h:form>

<table class="listagem">
	<caption>Discentes encontrados (${fn:length(jubilamentoMBean.discentesPassiveisJubilamento)})</caption>
	<thead>
		<tr>
			<th style="text-align: center;">Matr�cula</th>
			<th>Nome</th>
			<th>N�vel</th>
			<th>Status</th>
			<c:if test="${jubilamentoMBean.mostrarUltimaMatriculaValida}"><th style="text-align: center;">�ltima Matr�cula V�lida</th></c:if>
			<c:if test="${jubilamentoMBean.mostraPrazoConclusao}"><th style="text-align: center;">Prazo de Conclus�o</th></c:if>
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
				<h:commandLink action="#{jubilamentoMBean.verHistorico}" target="_blank" title="Visualizar Hist�rico" id="btaoVerHistorico">
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
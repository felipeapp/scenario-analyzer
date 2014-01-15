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

<h:form>
	<table class="formulario" width="95%" cellpadding="8">
		<tr>
			<th>Observação:</th>
			<td>
			<h:inputTextarea id="obsevacoes" value="#{jubilamentoMBean.observacoes}" style="width:99%" cols="96"/>
			</td>
			<td  width="8%">	
				<ufrn:help>
					Digite a observação que deverá aparecer no histórico do aluno. Caso nenhuma informação seja digitada
					a observação que aparecerá será: <b>Cancelamento coletivo realizado em 'data da operação'</b>.
				</ufrn:help>
			</td>
		</tr>
	</table>
	<br/>
<div class="infoAltRem">
	<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />: Visualizar Histórico
</div>


<table class="listagem">
	<caption>${fn:length(jubilamentoMBean.discentes)} discentes encontrados</caption>
	<thead>
		<tr>
			<th><input type="checkbox" onclick="checkAll()" title="Selecionar Todos"/></th>
			<th style="text-align: center;">Matrícula</th>
			<th>Nome</th>
			<th>Nível</th>
			<th>Status</th>
			<th  style="text-align: center;">Pendência na Biblioteca</th>
			<c:if test="${jubilamentoMBean.mostrarUltimaMatriculaValida}"><th style="text-align: center;">Última Matrícula Válida</th></c:if>
			<c:if test="${jubilamentoMBean.mostraPrazoConclusao}"><th style="text-align: center;">Prazo de Conclusão</th></c:if>
			<th></th>
		</tr>
	</thead>
	
	<c:set var="cursoAtual" value="-1" />
	
	<c:set var="colspan" value="7" />
	 
	<c:if test="${jubilamentoMBean.mostrarUltimaMatriculaValida}">
		<c:set var="colspan" value="${colspan+1}" /> 
	</c:if>
	<c:if test="${jubilamentoMBean.mostraPrazoConclusao}">
		<c:set var="colspan" value="${colspan+1}" /> 
	</c:if>
	
	<c:forEach items="#{jubilamentoMBean.discentes}" var="discente" varStatus="loop">
	
		<c:if test="${cursoAtual ne discente.curso.descricao}">
			<tr>
				<td colspan="${colspan}" class="subFormulario"><h:outputText value="#{discente.curso.descricao}"/></td>
			</tr>		
				<c:set var="cursoAtual" value="${discente.curso.descricao}" />
		</c:if>
	
		<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" style="${discente.marcado ? 'color:red;font-weight:bold' : ''}">
			<td><h:selectBooleanCheckbox value="#{discente.selecionado}" styleClass="check" rendered="#{!discente.matricular}" /></td>
			<td style="text-align: center;"><h:outputText value="#{discente.matricula}" /></td>
			<td><h:outputText value="#{discente.pessoa.nome}"/></td>
			<td><h:outputText value="#{discente.nivelDesc}"/></td>
			<td><h:outputText value="#{discente.statusString}"/></td>
			<td  style="text-align: center;">
				<h:outputText value="SIM" rendered="#{discente.marcado}" style="font-weight: bold;"/>
				<h:outputText value="NÃO" rendered="#{!discente.marcado}"/>
			</td>	
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
		<input type="hidden" value="false" name="ignoraPendenciasAdmDae"/>
		<h:commandButton value="<< Voltar" action="#{ jubilamentoMBean.telaInicial }" id="btaoVoltar"/>		
		<h:commandButton value="Cancelar Alunos sem Pendência na Biblioteca" action="#{ jubilamentoMBean.cancelarAlunosSemPendencia }" id="btaoJubilarAlunosSemPendencia"/>
		<ufrn:help>
			<p>Dentre os discentes selecionados, serão filtrados apenas aqueles que não estão com pendências na biblioteca.</p>
		</ufrn:help>
		<h:commandButton value="Próximo >>" action="#{ jubilamentoMBean.cancelarAlunos }" id="btaoJubilarAlunos"/>
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
	marcar = !marcar;
	$A(document.getElementsByClassName('check')).each(function(e) {
		if (!marcar)			
			e.checked = false;
		else
			e.checked = true;
	});
}
var marcar = false;
</script>
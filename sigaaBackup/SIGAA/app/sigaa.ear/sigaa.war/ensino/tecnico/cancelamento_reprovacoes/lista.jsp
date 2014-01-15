<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoTecnicoMBean"/>
<h2> <ufrn:subSistema /> &gt; Cancelamento por Reprova��es</h2>
	
<h:form id="form">

<div class="descricaoOperacao">
	<p>
		Este caso de uso permite ao gestor de ensino Cancelar o Programa de Discentes. O Cancelamento de programa � a desvincula��o de aluno regular do curso de t�cnico sem que tenha integralizado as exig�ncias m�nimas 
		para sua conclus�o. O cancelamento de programa acarreta o cancelamento da matr�cula em todos os componentes curriculares nos quais o aluno esteja matriculado.

	<br />
		Os cancelamentos podem ser por dois motivos(tipos):	Reprova��es em componentes distintos ou Reprova��es no mesmo Componente.
	<br />	<br />
	
		<p>
			<b>Reprova��es em componentes distintos</b> - Caracteriza-se por 4(quatro) reprova��es em componentes distintos.
		</p>

		<p>
			<b>Reprova��es no mesmo Componente</b> - Caracterizado por 3(tr�s) reprova��es em um mesmo componente.
		</p>
	</p>		
</div>
	
	<table class="listagem">
	  <caption>Discentes Passiveis de Cancelamento de Programa Encontrados</caption>
		
		<tr>
			<td colspan="2">
				<table class="subFormulario" width="100%">
				<caption> Discentes com reprova��es no mesmo Componente (${fn:length(cancelamentoReprovacoesTecnicoMBean.discenteReprovacoesMesmoComponente)}) </caption>
					<thead>
						<tr>
							<th width="10%">Matricula</th>
							<th width="70%">Nome</th>
						</tr>
					</thead>
		
					<tbody>
					   <c:forEach var="linha" items="${cancelamentoReprovacoesTecnicoMBean.discenteReprovacoesMesmoComponente}" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>${linha.matricula}</td>
								<td>${linha.nome}</td>
							</tr>
					   </c:forEach>
					</tbody>
					
				</table>
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				<table class="subFormulario" width="100%">
				  <caption>Discentes Reprova��es em Componentes Distintos (${fn:length(cancelamentoReprovacoesTecnicoMBean.discenteReprovacoesComponenteDistinto)})</caption>
					<thead>
						<tr>
							<th width="10%">Matricula</th>
							<th width="70%">Nome</th>
						</tr>
					</thead>
					<tbody>
					   <c:forEach var="linha" items="${cancelamentoReprovacoesTecnicoMBean.discenteReprovacoesComponenteDistinto}" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>${linha.matricula}</td>
								<td>${linha.nome}</td>
							</tr>
					   </c:forEach>
					</tbody>
				</table>
			</td>
		</tr>
		
		<tfoot>
			<tr>
		    	<td colspan="2" style="text-align: center;">
		   			<h:commandButton value="Cancelar Discentes" action="#{cancelamentoReprovacoesTecnicoMBean.cancelarDiscentes}" immediate="true" id="btnCancelarProg" />
		   			<h:commandButton value="Cancelar" immediate="true" action="#{cancelamentoReprovacoesTecnicoMBean.cancelar}" onclick="#{confirm}" id="btnCancelar" />
		   		</td>
			</tr>
		</tfoot>
		
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
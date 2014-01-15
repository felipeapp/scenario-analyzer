<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Planos de Aula Elaborados </h2>
<f:view>

	<div class="descricaoOperacao">
		<p>
			Nesta página estão todos os planos de aulas que foram elaborados
		</p>
		
	</div>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Plano de Aula
	</div>

	<h:form>
		<table class="listagem">
			<caption>Todos os Planos de Aula</caption>
			<thead>
				<tr>
					<th>Disciplina</th>
					<th>Turma</th>
					<th>Data da Falta</th>  
					<th></th>	  	   	
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="#{planoReposicaoAula.listaPlanoAula}" var="plano" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${ plano.faltaHomologada.dadosAusencia.docenteTurma.turma.disciplina.detalhes.nome }</td>
					<td>${ plano.faltaHomologada.dadosAusencia.docenteTurma.turma.codigo }</td>
					<td><fmt:formatDate value="${ plano.faltaHomologada.dadosAusencia.dataAula }" pattern="dd/MM/yyyy"/></td>
					<td>
						<h:commandLink action="#{planoReposicaoAula.view}" style="border: 0;">
							<h:graphicImage url="/img/view.gif"/>
							<f:param name="id" value="#{plano.id}"/>
						</h:commandLink>					
						<h:commandLink action="#{planoReposicaoAula.iniciarAlterar}" style="border: 0;" rendered="#{empty plano.parecer}">
							<h:graphicImage url="/img/alterar.gif"/>
							<f:param name="id" value="#{plano.id}"/>
						</h:commandLink>						
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
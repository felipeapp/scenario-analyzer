<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> Planos de Aula Pendentes </h2>
<f:view>

<a4j:keepAlive beanName="faltaHomologada"></a4j:keepAlive>
<a4j:keepAlive beanName="planoReposicaoAula"></a4j:keepAlive>

	<div class="descricaoOperacao">
		<p>
			Caro Professor,
		</p>	
		<br/>
		<p>
			Esta p�gina lista as suas faltas homologadas pelo chefe de seu departamento e que ainda n�o possuem planos de reposi��o de aula.
		</p>
	</div>

	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Criar Plano de Aula
	</div>

	<br />
	
	
	<h:form>
		<table class="listagem">
			<caption>Planos de Aulas Pendentes</caption>
			<thead>
				<tr>
					<th width="15%" nowrap="nowrap">Data da Falta</th>
					<th width="15%">Turma</th>
					<th>Disciplina</th>
					<th></th>
				</tr>			
			</thead>
			<tbody>
				<c:forEach items="#{faltaHomologada.homologacoesSemPlanos}" var="h" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td><fmt:formatDate value="${ h.dadosAusencia.dataAula }" pattern="dd/MM/yyyy"/> </td>
						<td>${ h.dadosAusencia.docenteTurma.turma.codigo }</td>
						<td>${ h.dadosAusencia.docenteTurma.turma.disciplina.codigoNome }</td>
						<td align="right">
							<h:commandLink action="#{planoReposicaoAula.iniciarPlanoAulaFaltaHomologada}" style="border: 0;">
								<h:graphicImage url="/img/seta.gif"/>
								<f:param name="id" value="#{h.id}"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
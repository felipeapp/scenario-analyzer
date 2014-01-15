<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Gerencias Avisos de Falta Homologados </h2>
<f:view>

	<div class="descricaoOperacao">
		<p>
			A primeira tabela exibe todos os docentes que tiveram seus avisos de faltas homologados e ainda n�o possui um plano de aula aprovado pelo chefe do departamento. 
		</p>
		<p>
			Se o docente n�o apresentar uma plano de aula, � possivel lan�ar uma aus�ncia no SIGPRH clicando no �cone <h:graphicImage value="/img/pesquisa/user_delete.gif" style="overflow: visible;" />.
		</p>
		<br />
		<p>
			A segunda tabela exibe os planos de aulas que foram submetido pelos docentes e est�o agurdando an�lise do chefe do departamento. 
		</p>
		<p>
			� necess�rio que o chefe analise o plano de aula (atrav�s do �cone <h:graphicImage value="/img/seta.gif" style="overflow: visible;" />) e emita um parecer a favor ou contr�rio. Caso seja a favor, um email ser� enviado � turma informando o plano de aula do professor. No caso do plano de aula ser negado, o professor ser� obrigado a elaborar um novo e resubmeter a sua an�lise.
		</p>
	</div>
	
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Analisar Plano de Aula
		<h:graphicImage value="/img/pesquisa/user_delete.gif" style="overflow: visible;" />: Registrar Aus�ncia
	</div>	
	
	<c:set var="homologacoesSemPlanosByDepartamento" value="#{faltaHomologada.homologacoesSemPlanosByDepartamento}"/>
	<c:set var="planoPendentesByDepartamento" value="#{planoReposicaoAula.planoPendentesByDepartamento}"/>
	
	
	<h:form>
		<table class="formulario" width="100%">
			<caption>Gerenciar Avisos de Falta</caption>
			<tr>
				<td>
					<table class="subListagem">
						<caption>Docentes que ainda n�o submeteram planos de aula</caption>
						<thead>
							<tr>
								<th>Nome</th>
								<th>Disciplina</th>
								<th>Turma</th>
								<th>Aviso Homologado em</th>  
								<th></th>
							</tr>
						</thead>
						
						<tbody>
							<c:forEach items="${homologacoesSemPlanosByDepartamento}" var="fh" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>${ fh.dadosAusencia.docenteTurma.docente.pessoa.nome }</td>
								<td>${ fh.dadosAusencia.docenteTurma.turma.disciplina.detalhes.nome }</td>
								<td>${ fh.dadosAusencia.docenteTurma.turma.codigo }</td>
								<td><fmt:formatDate value="${ fh.dataCadastro }" pattern="dd/MM/yyyy"/></td>
								<td>
									<h:commandLink action="#{faltaHomologada.lancarAusenciaSIGRH}" style="border: 0;" immediate="true" target="_blank">
										<h:graphicImage url="/img/pesquisa/user_delete.gif"/>
									</h:commandLink>								
								</td>
							</tr>
							</c:forEach>
							<c:if test="${empty homologacoesSemPlanosByDepartamento}">
								<td colspan="5" align="center">
									<i>Nenhum docente est� devendo plano de aula.</i>
								</td>							
							</c:if>
						</tbody>
					</table>				
				</td>
			</tr>
			<tr>
				<td>
					<table class="subListagem">
						<caption>Planos Pendentes de Aprova��o</caption>
						<thead>
							<tr>
								<th>Nome</th>
								<th>Disciplina</th>
								<th>Turma</th>
								<th>Data da Falta</th>  
								<th></th>	  	   	
							</tr>
						</thead>
						
						<tbody>
							<c:forEach items="#{planoPendentesByDepartamento}" var="plano" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>${ plano.faltaHomologada.dadosAusencia.docenteTurma.docente.pessoa.nome }</td>
								<td>${ plano.faltaHomologada.dadosAusencia.docenteTurma.turma.disciplina.detalhes.nome }</td>
								<td>${ plano.faltaHomologada.dadosAusencia.docenteTurma.turma.codigo }</td>
								<td><fmt:formatDate value="${ plano.faltaHomologada.dadosAusencia.dataAula }" pattern="dd/MM/yyyy"/></td>
								<td>
									<h:commandLink action="#{parecerPlanoReposicaoAula.iniciarParecer}" style="border: 0;">
										<h:graphicImage url="/img/seta.gif"/>
										<f:param name="id" value="#{plano.id}"/>
									</h:commandLink>					
								</td>
							</tr>
							</c:forEach>
							<c:if test="${empty planoPendentesByDepartamento}">
								<td colspan="5" align="center">
									<i>Nenhum plano de aula foi encontrado.</i>				
								</td>							
							</c:if>							
						</tbody>
					</table>				
				</td>
			</tr>
		</table>
		
	</h:form>	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<f:view>
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</f:subview>
	<h2> <ufrn:subSistema /> > Gerencias Avisos de Falta Homologados </h2>


	<div class="descricaoOperacao">
		<p>
			A lista a seguir exibe os avisos de falta já homologados pendentes de análise do chefe de departamento.
		</p>
		<%--
		<p>
			Se o docente não apresentar uma plano de aula, é possivel lançar uma ausência no SIGPRH clicando no ícone <h:graphicImage value="/img/pesquisa/user_delete.gif" style="overflow: visible;" />.
		</p>
		 --%>
		<p>
			Para os planos já apresentados, é necessário que o chefe analise-os e emita um parecer a favor ou contrário. Caso seja a favor, um email será enviado à turma informando o plano de aula do professor e um tópico de aula será criado na turma virtual. No caso do plano de aula ser negado, o professor será obrigado a elaborar um novo e submeter novamente a sua análise.
		</p>
	</div>
	
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Analisar Plano de Aula
		<%--<h:graphicImage value="/img/pesquisa/user_delete.gif" style="overflow: visible;" />: Registrar Ausência--%>
		<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: Estornar Homologação de Aviso de Falta
		<h:graphicImage value="/img/graduacao/matriculas/zoom.png" style="overflow: visible;" />: Ver Detalhes da turma
	</div>	
	
	<c:set var="homologacoesPendentesAnalise" value="#{avisoFaltaHomologada.homologacoesPendentesAnalise}"/>
	<c:set var="planosPendentesAnalise" value="#{planoReposicaoAula.planoPendentesByDepartamento}"/>
	
	<h:form>
		<table class="formulario" width="100%">
			<caption>Gerenciar Avisos de Falta</caption>
			<tr>
				<td>
					<table class="subListagem">
						<caption>Avisos de Falta Pendentes de Análise</caption>
						<thead>
							<tr>
								<th width="3%"></th>
								<th>Nome</th>
								<th>Disciplina</th>
								<th>Turma&nbsp;</th>
								<th nowrap="nowrap"><center>Data da Aula&nbsp;</center></th>  
								<th nowrap="nowrap"><center>Aviso Homologado em&nbsp;</center></th>  
								<th>Status</th>
								<th width="5%"></th>
							</tr>
						</thead>
						
						<tbody>
							<c:forEach items="#{homologacoesPendentesAnalise}" var="fh" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>
									<a href="javascript:void(0);" onclick="PainelTurma.show(${fh.dadosAvisoFalta.turma.id})" title="Ver Detalhes da turma">
										<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt="" class="noborder" />
									</a>
								</td>
								<td>${ fh.dadosAvisoFalta.docente.pessoa.nome }</td>
								<td>${ fh.dadosAvisoFalta.turma.disciplina.detalhes.nome }</td>
								<td>${ fh.dadosAvisoFalta.turma.codigo }</td>
								<td><center><fmt:formatDate value="${ fh.dadosAvisoFalta.dataAula }" pattern="dd/MM/yyyy"/></center></td>
								<td><center><fmt:formatDate value="${ fh.dataCadastro }" pattern="dd/MM/yyyy"/></center></td>
								<td>${ fh.movimentacao.descricao }</td>
								<td>
									<%--<h:commandLink action="#{avisoFaltaHomologada.lancarAusenciaSIGRH}" style="border: 0;" immediate="true"  rendered="#{fh.dadosAvisoFalta.servidor && !fh.frequenciaEletronica}">
										<f:param name="id" value="#{fh.dadosAvisoFalta.docente.id}"/>
										<h:graphicImage url="/img/pesquisa/user_delete.gif"/>
									</h:commandLink>--%>
									<h:commandLink action="#{avisoFaltaHomologada.iniciarEstornar}" title="Estornar Homologação de Aviso de Falta">
										<f:param name="id" value="#{fh.id}"/>
										<h:graphicImage value="/img/biblioteca/estornar.gif"/>
									</h:commandLink>
								</td>
							</tr>
							</c:forEach>
							<c:forEach items="#{planosPendentesAnalise}" var="plano" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>
									<a href="javascript:void(0);" onclick="PainelTurma.show(${plano.faltaHomologada.dadosAvisoFalta.turma.id})" title="Ver Detalhes da turma">
										<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt="" class="noborder" />
									</a>
								</td>
								<td>${ plano.faltaHomologada.dadosAvisoFalta.docente.pessoa.nome }</td>
								<td>${ plano.faltaHomologada.dadosAvisoFalta.turma.disciplina.detalhes.nome }</td>
								<td>${ plano.faltaHomologada.dadosAvisoFalta.turma.codigo }</td>
								<td><center><fmt:formatDate value="${ plano.faltaHomologada.dadosAvisoFalta.dataAula }" pattern="dd/MM/yyyy"/></center></td>
								<td><center><fmt:formatDate value="${ plano.faltaHomologada.dataCadastro }" pattern="dd/MM/yyyy"/></center></td>
								<td>${ plano.faltaHomologada.movimentacao.descricao }</td>
								<td>
									<h:commandLink action="#{parecerPlanoReposicaoAula.iniciarParecer}" style="border: 0;" title="Analisar Plano de Aula">
										<h:graphicImage url="/img/seta.gif"/>
										<f:param name="id" value="#{plano.id}"/>
									</h:commandLink>
									<h:commandLink action="#{avisoFaltaHomologada.iniciarEstornar}" title="Estornar Homologação de Aviso de Falta" style="margin-left: 3px;">
										<f:param name="id" value="#{plano.faltaHomologada.id}"/>
										<h:graphicImage value="/img/biblioteca/estornar.gif"/>
									</h:commandLink>														
								</td>
							</tr>
							</c:forEach>
							<c:if test="${empty homologacoesPendentesAnalise && empty planosPendentesAnalise}">
								<td colspan="5" align="center">
									<i>Não existem avisos de falta pendentes de análise.</i>
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
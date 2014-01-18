<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<script type="text/javascript"
	src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js">
</script>



<style>
tr.selecionada td {
	background: #C4D2EB;
}
</style>

<h2>
	<ufrn:subSistema />
	> Consolidação Final
</h2>


<a4j:keepAlive beanName="consolidacaoFinalIMD" />
<f:view>
	<h:form>
	
		<div class="descricaoOperacao">
			<p>Informe as notas de recuperação dos alunos nas disciplinas descritas a seguir e selecione a opção "Salvar" para cadastrar as notas no sistema. Após informar todas as notas, selecione a opção "Consolidar Turma" para iniciar o processo de consolidação da turma.</p>
		</div>
		
		<table class="formulario" style="width: 100%">
			<caption>Consolidação Final</caption>
			<tbody>
				<tr>
					<th class="rotulo">Turma:</th>
					<td>${consolidacaoFinalIMD.turmaEntrada.especializacao.descricao}</td>
				</tr>

				<tr>
					<th class="rotulo">Tutor:</th>
					<td>
						<c:forEach items="#{consolidacaoFinalIMD.tutorias}" var="item" varStatus="i">
							<c:choose>
								<c:when test="${i.index==0}">
									${item.tutor.pessoa.nome}
								</c:when>
								<c:otherwise>
									, ${item.tutor.pessoa.nome}								
								</c:otherwise>							
							</c:choose>							
						</c:forEach>
					</td>
				</tr>
				<tr>
					<th class="rotulo">Ano-Período:</th>
					<td>${consolidacaoFinalIMD.turmaEntrada.anoReferencia} - ${consolidacaoFinalIMD.turmaEntrada.periodoReferencia} </td>
				</tr>
				<tr>
					<th class="rotulo">Módulo:</th>
					<td>${consolidacaoFinalIMD.turmaEntrada.dadosTurmaIMD.cronograma.modulo.descricao}</td>
				</tr>
				<tr>
					<table class="subFormulario" style="width: 100%;">
					<caption>Alunos Matriculados</caption>
					<thead>
						<tr>
							<th style="text-align: center;">Matrícula</th>
							<th style="text-align: center;">Nome</th>							
							<c:forEach items="${consolidacaoFinalIMD.listaDisciplinas}" var="d">
								<th>${d.disciplina.detalhes.codigo}</th>							
							</c:forEach>
							<th></th>
						</tr>
					</thead>

					<tbody>
						<c:forEach items="#{consolidacaoFinalIMD.alunosMatriculados}"
							var="item" varStatus="i">
							<tr class="${i.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }"
								onMouseOver="$(this).addClassName('selecionada')"
								onMouseOut="$(this).removeClassName('selecionada')">
								<td>${item.discente.matricula}</td>
								
								<td>${item.discente.pessoa.nome}</td>	
								<a4j:repeat value="#{consolidacaoFinalIMD.listaDisciplinas}" var="d">
									<td>
										<h:inputText value="#{item.matriculasComponentes[d.id].recuperacao}" onkeypress="return(formataValorNota(this, event, 1))" maxlength="4" size="3" 
										 style="text-align:right;" disabled="#{item.matriculasComponentes[d.id]==null ? 'true' : 'false'}">
											<f:converter converterId="convertNota" />
 										</h:inputText>
 									</td>								
								</a4j:repeat>								
							</tr>
						</c:forEach>
					</tbody>
				</table>
					
				
				</tr>
			</tbody>
			
						
			<div class="opcoes" align="right">
			<table style="width: 100%;">
			
				<tfoot>					
					<tr style="height: auto; overflow: hidden; height: 35px;">
						<td style="width:73%; display:block;"></td>
														
						<td style="width:9%; text-align: center;"><h:commandButton
								action="#{consolidacaoFinalIMD.voltarPortal}"
								image="/img/consolidacao/nav_left_red.png"
								alt="Voltar" title="Voltar" /></td>
								
						<td style="width:9%; text-align: center;"><h:commandButton	
								action="#{consolidacaoFinalIMD.cadastrarNotasRecuperacao }"
								image="/img/consolidacao/disk_green.png" alt="Consolidar"
								title="Consolidação Final" /></td>														
								
						<td style="width:9%; text-align: center;"><h:commandButton
								action="#{consolidacaoFinalIMD.relatorioConsolidacaoFinal}"
								image="/img/consolidacao/nav_right_green.png"
								alt="Consolidar turma" title="Consolidar Turma" /></td>
																												
																
					</tr>
					
					<tr style="height: auto; overflow: hidden;">
						<td style="float:none; width:73%; display:block;"></td>
						
						<td style="width:9%;text-align: center;"><h:commandLink
								action="#{consolidacaoFinalIMD.voltarPortal}"
								value="Voltar" /></td>
													
						<td style="width:9%; text-align: center;"><h:commandLink
								action="#{consolidacaoFinalIMD.cadastrarNotasRecuperacao}" value="Salvar" /></td>
								
						<td style="width:9%; text-align: center;"><h:commandLink
								action="#{consolidacaoFinalIMD.relatorioConsolidacaoFinal}" value="Consolidar Turma" /></td>								
								
																						
					</tr>
					
					<!-- Legenda das disciplinas -->
					<a4j:repeat value="#{consolidacaoFinalIMD.listaDisciplinas}" var="d">
						<tr><td colspan="3" style="text-align: left;">
							<strong>
								<h:outputText value="#{d.disciplina.detalhes.codigo}"/> -
							</strong> 
							<h:outputText value="#{d.disciplina.detalhes.nome}"/>
 						</td></tr>							
					</a4j:repeat>
					
				</tfoot>
			</table>
		</div>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
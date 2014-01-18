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
	> Consolida��o Final
</h2>


<a4j:keepAlive beanName="consolidacaoFinalIMD" />
<f:view>
	<h:form>
		<div class="descricaoOperacao">
			<p>Realize a confer�ncia das notas e em seguida selecione a op��o "Consolida��o Final" para realizar a consolida��o da turma. Ap�s realizar a consolida��o, a turma n�o poder� ser mais alterada.</p>
		</div>
		<table class="formulario" style="width: 100%">
			<caption>Consolida��o Final</caption>
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
					<th class="rotulo">Ano-Per�odo:</th>
					<td>${consolidacaoFinalIMD.turmaEntrada.anoReferencia} - ${consolidacaoFinalIMD.turmaEntrada.periodoReferencia} </td>
				</tr>
				<tr>
					<th class="rotulo">M�dulo:</th>
					<td>${consolidacaoFinalIMD.turmaEntrada.dadosTurmaIMD.cronograma.modulo.descricao}</td>
				</tr>
				<tr>
					<table class="subFormulario" style="width: 100%;">
					<caption>Alunos Matriculados</caption>
					<thead>
						<tr>
							<th style="text-align: center;">Matr�cula</th>
							<th style="text-align: center;"></th>
							<th style="text-align: center;">Nome</th>
							<th style="text-align: center;">M�dia Parcial</th>
							<th style="text-align: center;">M�dia Final</th>
							<th style="text-align: center;">CHNF</th>
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
								<td>
									<c:if test="${item.situacaoMatriculaTurma.id == 5 }">
										<h:graphicImage value="/img/consolidacao/situacao_rec.png" title="Reprovado por Frequ�ncia"/>
									</c:if>
									<c:if test="${item.situacaoMatriculaTurma.id == 4}">
										<h:graphicImage value="/img/consolidacao/situacao_rep.png" title="Reprovado Nota"/>
									</c:if>
									<c:if test="${item.situacaoMatriculaTurma.id == 3}">
										<h:graphicImage value="/img/consolidacao/situacao_apr.png" title="Aprovado"/>
									</c:if>																		
								</td>
								<td>${item.discente.pessoa.nome}</td>											
								<td style="text-align: right;"><fmt:formatNumber value="${item.notaParcial}"  pattern="#0.0"/></td>
								<td style="text-align: right;"><fmt:formatNumber value="${item.notaFinal}"  pattern="#0.0"/></td>
								<td style="text-align: right;"><fmt:formatNumber value="${item.chnf}"  pattern="0"/></td>

							</tr>
						</c:forEach>
					</tbody>
				</table>
					
				
				</tr>
			</tbody>
			<div class="opcoes" align="right">
				<table style="width: 100%;">
					<tfoot>
						<tr>
							<td width="90" valign="top"
								style="float: right; text-align: center; margin: 5px 0 3px;"><h:commandButton	
									action="#{consolidacaoFinalIMD.consolidarNotas }"
									image="/img/consolidacao/disk_blue_ok.png" alt="Consolidar"
									title="Consolida��o Final" /></td>
	
							<td width="70" valign="top"
								style="float: right; text-align: center; margin: 5px 0 3px;"><h:commandButton
									action="#{consolidacaoFinalIMD.voltarFormConsolidacao}"
									image="/img/consolidacao/nav_left_red.png"
									alt="Voltar" title="Voltar" /></td>
						</tr>
						<tr>
							<td width="90" valign="top"
								style="float: right; text-align: center;"><h:commandLink
									action="#{consolidacaoFinalIMD.consolidarNotas}" value="Consolida��o Final" /></td>
							<td width="70" valign="top"
								style="float: right; text-align: center;"><h:commandLink
									action="#{consolidacaoFinalIMD.voltarFormConsolidacao}"
									value="Voltar" /></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</table>
	</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
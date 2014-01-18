<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<style>
tr.selecionada td {
	background: #C4D2EB;
}
</style>

<h2>
	<ufrn:subSistema />
	> Consolidação Parcial
</h2>


<a4j:keepAlive beanName="consolidacaoParcialIMD" />
<f:view>
	<h:form>
		<div class="infoAltRem">
			<html:img page="/img/consolidacao/situacao_rec.png" style="overflow: visible;"/>: Recuperação
			<html:img page="/img/consolidacao/situacao_rep.png" style="overflow: visible;"/>: Reprovado
			<html:img page="/img/consolidacao/situacao_apr.png" style="overflow: visible;"/>: Aprovado
			<html:img page="/img/view.gif" style="overflow: visible;"/>: Detalhar Notas
		</div>		
		<table class="formulario" style="width: 100%">
			
			<caption>Consolidação Parcial</caption>
			<tbody>
				<tr>
					<th class="rotulo">Turma:</th>
					<td>${consolidacaoParcialIMD.turmaEntrada.especializacao.descricao}</td>
				</tr>

				<tr>
					<th class="rotulo">Tutor:</th>
					<td>
						<c:forEach items="#{consolidacaoParcialIMD.tutores}" var="item" varStatus="i">
							<c:choose>
								<c:when test="${i.index==0}">
									${item.pessoa.nome}
								</c:when>
								<c:otherwise>
									, ${item.pessoa.nome}								
								</c:otherwise>							
							</c:choose>							
						</c:forEach>
					</td>
				</tr>
				<tr>
					<th class="rotulo">Ano-Período:</th>
					<td>${consolidacaoParcialIMD.turmaEntrada.anoReferencia} - ${consolidacaoParcialIMD.turmaEntrada.periodoReferencia} </td>
				</tr>
				<tr>
					<th class="rotulo">Módulo:</th>
					<td>${consolidacaoParcialIMD.turmaEntrada.dadosTurmaIMD.cronograma.modulo.descricao}</td>
				</tr>
				<tr>
					<table class="subFormulario" style="width: 100%;">
					<caption>Alunos Matriculados</caption>
					<thead>
						<tr>
							<th style="text-align: left;">Matrícula</th>
							<th style="text-align: center;"></th>
							<th style="text-align: left;">Nome</th>
							<th style="text-align: right;">Média</th>
							<th style="text-align: right;">CHNF</th>
							<th></th>
						</tr>
					</thead>

					<tbody>
						<c:forEach items="#{consolidacaoParcialIMD.alunosMatriculados}"
							var="item" varStatus="i">
							<tr class="${i.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }"
								onMouseOver="$(this).addClassName('selecionada')"
								onMouseOut="$(this).removeClassName('selecionada')">
								<td>${item.discente.matricula}</td>
								<td>
									<c:if test="${item.notaParcial>=3 && item.notaParcial < 5}">
										<h:graphicImage value="/img/consolidacao/situacao_rec.png" title="Recuperação"/>
									</c:if>
									<c:if test="${item.notaParcial < 3}">
										<h:graphicImage value="/img/consolidacao/situacao_rep.png" title="Reprovado"/>
									</c:if>
									<c:if test="${item.notaParcial >= 5}">
										<h:graphicImage value="/img/consolidacao/situacao_apr.png" title="Aprovado"/>
									</c:if>																		
								</td>
								<td>${item.discente.pessoa.nome}</td>			
								
								<td style="text-align: right;"><fmt:formatNumber value="${item.notaParcial}"  pattern="#0.0"/></td>
								<td style="text-align: right;"><fmt:formatNumber value="${item.chnf}"  pattern="0"/></td>
								<td align="right">
									<h:commandLink action="#{consolidacaoParcialIMD.detalharNotas}" >
										<h:graphicImage value="/img/view.gif" title="Detalhar Notas"/>
 										<f:param name="idDiscente" value="#{item.discente.id}"/>
									</h:commandLink>																
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
					
				
				</tr>
			</tbody>
			<div align="center">
				<table style="margin-top: 10;  text-align: center; width: 30%" >
					<tr><td><strong>CHNF</strong> Carga Horária não Frequentada</td></tr>				  			
				</table>
			</div>
			<div class="opcoes" align="right">
			<table style="width: 100%;">
				<tfoot>
					<tr>
						<td width="90" valign="top"
							style="float: right; text-align: center; margin: 5px 0 3px;"><h:commandButton	
								action="#{consolidacaoParcialIMD.consolidarNotas }"
								image="/img/consolidacao/disk_blue_ok.png" alt="Consolidar"
								title="Consolidação Parcial" /></td>

						<td width="70" valign="top"
							style="float: right; text-align: center; margin: 5px 0 3px;"><h:commandButton
								action="#{consolidacaoParcialIMD.voltarPortal}"
								image="/img/consolidacao/nav_left_red.png"
								alt="Voltar" title="Voltar" /></td>
					</tr>
					<tr>
						<td width="90" valign="top"
							style="float: right; text-align: center;"><h:commandLink
								action="#{consolidacaoParcialIMD.consolidarNotas}" value="Consolidação Parcial" /></td>
						<td width="70" valign="top"
							style="float: right; text-align: center;"><h:commandLink
								action="#{consolidacaoParcialIMD.voltarPortal}"
								value="Voltar" /></td>
					</tr>
				</tfoot>
			</table>
		</div>


			<tfoot>
				
				
			</tfoot>
		</table>		
	</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
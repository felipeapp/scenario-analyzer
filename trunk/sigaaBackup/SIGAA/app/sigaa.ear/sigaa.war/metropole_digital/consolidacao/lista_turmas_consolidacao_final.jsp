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
	> Consolidação Final > Lista de Turmas
</h2>


<a4j:keepAlive beanName="consolidacaoParcialIMD" />
<f:view>
	<div class="descricaoOperacao">
		<p>Selecione a turma que deseja consolidar.</p>
	</div>
	<h:form>
		<div class="infoAltRem">

			<html:img page="/img/seta.gif" style="overflow: visible;" />
			: Selecionar Turma

		</div>
		<table class="formulario" style="width: 100%">
			<caption>Turmas não consolidadas</caption>
			<c:forEach items="#{consolidacaoFinalIMD.listaTurmasConsolidacao}"
				var="turma" varStatus="i">
				<tr class="${i.count%2==0? 'linhaPar': 'linhaImpar' }"
					onMouseOver="$(this).addClassName('selecionada')"
					onMouseOut="$(this).removeClassName('selecionada')">
					<td style="width: 10%;"><h:outputText value="#{turma.anoReferencia}" /> - <h:outputText value="#{turma.periodoReferencia}" /></td>
					<td><h:outputText value="#{turma.especializacao.descricao}" /></td>

					<td align="right"><h:commandLink action="#{consolidacaoFinalIMD.cadastroNotasRecuperacao}">
							<h:graphicImage value="/img/seta.gif"
 								title="Selecionar Disciplina" /> 
 							<f:param name="id" value="#{turma.id}" /> 
						</h:commandLink>
					</td> 
				</tr>
			</c:forEach>
		</table>
	</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
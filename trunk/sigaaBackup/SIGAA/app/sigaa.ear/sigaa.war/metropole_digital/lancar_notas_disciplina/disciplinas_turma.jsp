<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<h2><ufrn:subSistema /> > Lançamento Notas por Disciplina > Seleção de Disciplina </h2>
<a4j:keepAlive beanName="lancamentoNotasSemanais"/>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>

<style>
	tr.selecionada td { background: #C4D2EB; }
</style>


<f:view>
			<div class="descricaoOperacao">
				<p>Selecione a disciplina que deseja cadastrar as notas.</p>
			</div>
	<h:form>
			<div class="infoAltRem">
				
				<html:img page="/img/seta.gif" style="overflow: visible;"/>: Selecionar Disciplina
				
			</div>
			<table class="formulario" style="width: 100%">
				<caption>Disciplinas da Turma</caption>
					
					<c:forEach items="#{lancamentoNotasDisciplina.listaDisciplinas}" var="d" varStatus="i">
								<tr class="${i.count%2==0? 'linhaPar': 'linhaImpar' }" onMouseOver="$(this).addClassName('selecionada')"
 onMouseOut="$(this).removeClassName('selecionada')">
									<td>
										<h:outputText value="#{d.nome}"/>
									</td>
									
									<td>
										<h:commandLink action="#{lancamentoNotasDisciplina.lancarNotas}" >
 											<h:graphicImage value="/img/seta.gif" title="Selecionar Disciplina"/>
 											<f:param name="idDisciplina" value="#{d.id}" /> 
 										</h:commandLink>																 
									</td>
								
								</tr>												
					</c:forEach>
			  </table>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>
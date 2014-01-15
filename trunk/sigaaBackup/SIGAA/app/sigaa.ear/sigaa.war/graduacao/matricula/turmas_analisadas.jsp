<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<style>
	.

</style>

<f:view>
		<c:set value="orientacoes" var="pagina"></c:set>
		<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>

	<c:if test="${!consulta}">
		<%@ include file="/graduacao/matricula/cabecalho_botoes_superiores.jsp"%>
	</c:if>
	
	<div class="descricaoOperacao">
		<p> Caro Aluno(a), </p>

		<p> Você poderá acompanhar na lista abaixo as análises e observações realizadas acerca de suas solicitações de matrícula. </p>
		<p> Associada a cada solicitação você encontrará a situação atual da mesma e da matrícula correspondente, havendo, no
		caso de orientações negativas, uma observação específica para o componente solicitado. </p>
		<c:if test="${matriculaGraduacao.discente.stricto}">
		<p>A situação AGUARDANDO OUTRO PROGRAMA é para o caso de matrícula em disciplinas de outros programas. 
		Neste caso seu orientador ou o coordenador do seu programa aprovou a sua matrícula na disciplina porém, para que a matrícula seja efetivada é necessário
		que a coordenação do outro programa mantenedor da disciplina, que você deseja se matricular também aprove a matrícula.
		</p>
		</c:if>
	</div>

	<h:form>
	<table class="listagem" style="width: 100%">
		<caption>Solicitações de Matrícula Analisadas</caption>
		<thead style="font-size: xx-small;">
			<tr>
				<td width="8%"></td>
				<td>Componente Curricular</td>
				<td width="12%">Situação</td>
				<td width="10%" nowrap="nowrap">Submetida em</td>
				<td width="10%" nowrap="nowrap">Analisada em</td>
				<td>Analisada por</td>
				<td width="8%" ></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${matriculaGraduacao.solicitacoesMatriculas}" var="solicitacao" varStatus="status">
				<c:set var="analisePositiva" value="${solicitacao.atendida or solicitacao.vista or solicitacao.aguardandoOutroPrograma}" />				
				<c:set var="analiseNegativa" value="${solicitacao.negada}" />				

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center">
						<c:if test="${not empty solicitacao.turma}">
						<a href="javascript:void(0);" onclick="PainelTurma.show(${solicitacao.turma.id})" title="Ver Detalhes dessa turma">
							Turma ${solicitacao.turma.codigo}
						</a>
						</c:if>
					</td>
					<td>	
						<b>
							<a href="javascript:void(0);" onclick="PainelComponente.show(${solicitacao.componente.id})" title="Ver Detalhes do Componente Curricular">
							${solicitacao.componente.codigo}</a> - ${solicitacao.componente.nome}
						</b>
					</td>
					<td style="color: ${analisePositiva ? '#292' : (analiseNegativa ? '#922' : '') };"> 
						${solicitacao.statusDescricao}
					</td>
					<td><ufrn:format type="data" valor="${solicitacao.dataCadastro}" /></td>
					<td><ufrn:format type="data" valor="${solicitacao.dataAlteracao}" /></td>
					<td>${solicitacao.registroAlteracao.usuario.pessoa.nome}</td>
					<td align="center"> 
						<c:if test="${!solicitacao.discente.stricto or !solicitacao.negada}">
							${solicitacao.processamentoStatus} 
						</c:if>
					</td>
				</tr>
				<c:if test="${not empty solicitacao.observacao }">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
						<td> </td>
						<td colspan="6"><i>Observações:</i> ${solicitacao.observacao }</td>
					</tr>
					<c:if test="${solicitacao.discente.tecnico or solicitacao.discente.stricto}">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
						<td> </td>
						<td colspan="6"> <i>Analisada por ${solicitacao.registroAlteracao.usuario.nome}</i></td>
					</tr>
					</c:if>
				</c:if>
			</c:forEach>

			<tr>
				<td colspan="7"></td>
			</tr>

			<c:if test="${not empty matriculaGraduacao.orientacao}">
			<tr>
				<td colspan="7" class="subFormulario"> Orientação geral de matrícula </td>
			</tr>
			<tr>
				<td colspan="6" style="padding: 10px;">
					<i>${matriculaGraduacao.orientacao.orientacao}</i>
				</td>
			</tr>
			</c:if>
		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="7" align="center">
					<h:commandButton value="Cancelar" action="#{matriculaGraduacao.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br>
</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
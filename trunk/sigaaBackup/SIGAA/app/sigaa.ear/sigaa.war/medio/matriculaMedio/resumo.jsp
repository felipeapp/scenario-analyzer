<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<f:view>
<a4j:keepAlive beanName="matriculaMedio"/>
<h2> <ufrn:subSistema /> &gt; Matricular Aluno em Série</h2>

<h:form id="form">

	<div class="descricaoOperacao">
		<h4> Atenção! </h4>
		<p>
			Verifique se os dados abaixo estão corretos e clique no ícone 
			<b> Matricular Discente</b> para confirmar a operação.
		</p>
	</div>
	<c:set value="#{matriculaMedio.obj.discenteMedio }" var="discente" />
	<%@ include file="/medio/discente/info_discente.jsp"%>

	<c:if test="${not empty matriculaMedio.obj.turmaSerie.disciplinas}">
	<table class="listagem" style="width: 90%">
		<caption>Turmas</caption>
		<thead>
			<tr>
			<td width="5%">Série</td>
			<td width="5%">Turma</td>
			<td>Componente Curricular</td>
			<th width="30%">Docente(s)</th>
			<td width="10%">Local</td>
			<td width="10%">Horário</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{matriculaMedio.obj.turmaSerie.disciplinas}" var="linha" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center"> ${linha.turmaSerie.serie.numeroSerieOrdinal } </td>
					<td align="center">
						${linha.turma.codigo}
					</td>
					<td>
					<a href="javascript:noop();" onclick="PainelComponente.show(${linha.turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
						${linha.turma.disciplina.nome} (${linha.turma.disciplina.chTotal} h)
					</a>
					</td>
					<td id="colDocente">${empty linha.turma.docentesNomesCh ? linha.turma.situacaoTurma.descricao : linha.turma.docentesNomesCh}</td>
					<td>${linha.turma.local}</td>
					<td>${linha.turma.descricaoHorario}</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="6" align="center">
			   		<h:commandButton value="Matricular Discente" id="btnMatricularDiscente" action="#{ matriculaMedio.cadastrar }" />
					<h:commandButton value="<< Voltar" action="#{ matriculaMedio.selecionaDiscente}" id="btnVoltar"/>
					<h:commandButton value="Cancelar" id="btnCancelar" action="#{ matriculaMedio.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</c:if>
	<br>
</h:form>
</f:view>	
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<f:view>
<a4j:keepAlive beanName="matriculaMedio"/>
	<div class="descricaoOperacao">
		<h4> Atenção! </h4>
		<p>
			Verifique se as disciplinas de dependência abaixo estão corretas e clique no botão 
			<b> Matricular Dependência(s)</b> para confirmar a operação.
		</p>
	</div>
	<c:set value="#{matriculaMedio.obj.discenteMedio }" var="discente" />
	<%@ include file="/medio/discente/info_discente.jsp"%>
	<h:form id="form">
		<c:if test="${not empty matriculaMedio.disciplinas}">
		<table class="listagem" style="width: 90%">
			<caption>Turmas</caption>
			<thead>
				<tr>
				<td width="5%" align="center">Série</td>
				<td width="5%" align="center">Turma</td>
				<td>Componente Curricular</td>
				<th width="30%">Docente(s)</th>
				<td width="10%">Local</td>
				<td width="10%">Horário</td>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="#{matriculaMedio.disciplinas}" var="linha" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center"> ${linha.turmaSerie.serie.numeroSerieOrdinal } </td>
					<td align="center">
						<a href="javascript:noop();" onclick="PainelTurma.show(${linha.turma.id})" title="Ver Detalhes dessa turma">
						${linha.turma.codigo}</a>
					</td>
					<td>
						<a href="javascript:noop();" onclick="PainelComponente.show(${linha.turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
							${linha.turma.disciplina.codigo}</a> - ${linha.turma.disciplina.nome}
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
						<h:commandButton value="#{matriculaMedio.confirmButton}" action="#{matriculaMedio.cadastrarDependencias}" id="matricular" />
						<input type="button" onclick="javascript:history.go(-1);" value="<< Voltar"/>
						<h:commandButton value="Cancelar" action="#{matriculaMedio.cancelar}" onclick="#{confirm}" id="cancelar" />
					</td>
			   	</tr>
			</tfoot>
		</table>
		
		</c:if>
</h:form>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
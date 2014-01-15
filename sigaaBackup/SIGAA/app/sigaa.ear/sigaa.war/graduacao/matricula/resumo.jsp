<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<f:view>

	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<div class="descricaoOperacao">
		<h4> Atenção! </h4>
		<p>
			Verifique se os dados abaixo estão corretos e clique no ícone <b> Matricular Discente</b> para confirmar a operação.
		</p>
	</div>
	<%@ include file="_info_discente.jsp"%>


	<h:form id="form">
	<c:set value="#{matriculaGraduacao.discente }" var="discente" />
	<c:if test="${matriculaGraduacao.compulsoria or matriculaGraduacao.foraPrazo}">
		<table class="subFormulario" width="100%">
			<caption>Restrições</caption>
			<tr>
			<th width="35%"><b>Status Escolhido para as Matrículas: </b></th>
			<td>${matriculaGraduacao.situacao.descricao}</td>
			</tr>
			<c:if test="${matriculaGraduacao.compulsoria}">
			<tr>
			<th><b>Restrições a serem ignoradas: </b></th>
			<td>
			<ul>
				<c:if test="${!matriculaGraduacao.restricoes.preRequisitos}">
				<li>Não verificar pré-requisitos</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.coRequisitos}">
				<li>Não verificar co-requisitos</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.choqueHorarios}">
				<li>Não verificar choque de horários</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.mesmoComponente}">
				<li>Não verificar se o discente possui componente (ou equivalente) pago</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.limiteCreditosExtra}">
				<li>Não verificar créditos eletivos</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.limiteMaxCreditosSemestre}">
				<li>Não verificar créditos máximos por semestre</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.limiteMinCreditosSemestre}">
				<li>Não verificar créditos mínimos por semestre</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.alunoEspecial}">
				<li>Não verificar regras para aluno especial</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.alunoOutroCampus}">
				<li>Não verificar regras para discentes de outro campus</li>
				</c:if>
				<c:if test="${!matriculaGraduacao.restricoes.turmaFerias}">
				<li>Não verificar regras para turmas de férias</li>
				</c:if>
			</ul>

			</td>
			</tr>
			</c:if>
		</table>
		<Br>
	</c:if>
	<c:if test="${not empty matriculaGraduacao.turmas}">
	<table class="listagem" style="width: 100%">
		<caption>Turmas</caption>
		<thead>
			<tr>
			<td width="2%">Turma</td>
			<td>Componente Curricular</td>
			<td width="10%">Local</td>
			<td width="10%">Horário</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{matriculaGraduacao.turmas}" var="turma" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center">
					<a href="javascript:noop();" onclick="PainelTurma.show(${turma.id})" title="Ver Detalhes dessa turma">
					${turma.codigo}
					</a>
					</td>
					<td>
					<a href="javascript:noop();" onclick="PainelComponente.show(${turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
					${turma.disciplina.detalhes.codigo}
					</a> - ${turma.disciplina.nome} (${turma.disciplina.crTotal} crs.)
					</td>
					<td>${turma.local}</td>
					<td>${turma.descricaoHorario}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	<br>
<div id="wrapper-menu-matricula">
<table id="menu-matricula">
	<tr>
	<td>
		<table class="menuMatricula">
			<tr>
			<td class="botoes nao_salvar">
				<h:commandButton title="Ver as turmas selecionadas" image="/img/graduacao/matriculas/turmas_selecionadas.png"
					 action="#{matriculaGraduacao.telaSelecaoTurmas}" id="btnVerTS"/><br />
				 <h:commandLink title="Voltar à tela onde as turmas selecionadas podem ser modificadas" value="Ver as turmas selecionadas"
					 action="#{matriculaGraduacao.telaSelecaoTurmas}"  id="lnkVerTS" />
			</td>
			<td class="botoes">
				<h:commandButton title="Matricular Discente Nessas Turmas" image="/img/graduacao/matriculas/salvar.gif"
					 action="#{matriculaGraduacao.matricular}"  id="btnMatDNT"/><br />
				 <h:commandLink title="Confirmar a matrícula do discente nas turmas selecionadas" value="Matricular Discente"
					 action="#{matriculaGraduacao.matricular}"  id="lnkMatDNT"/>
			</td>
			<td class="botoes nao_salvar">
				<h:commandButton title="Cancelar" image="/img/graduacao/matriculas/cancelar.gif"
					onclick="#{confirm}" action="#{matriculaGraduacao.cancelarMatricula}"  id="btnCanc" /><br />
				<h:commandLink title="Sair sem salvar" value="Sair sem salvar" onclick="#{confirm}"
						 action="#{matriculaGraduacao.cancelarMatricula}"  id="lnkCanc"/>
			</td>
			</tr>
		</table>
	</td>
	</tr>
</table>
</div>
</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
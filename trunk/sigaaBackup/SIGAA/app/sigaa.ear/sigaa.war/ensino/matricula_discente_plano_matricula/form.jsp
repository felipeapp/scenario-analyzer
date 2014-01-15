<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Matricular Discente Usando Plano de Matrículas</h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Selecione um plano de matrícula dos abaixo relacionados. As
			turmas constantes no Plano de Matrícula serão exibidas de acordo com
			o plano selecioando.</p>
	</div>
	<br/>
	<h:form id="form">
		<table class="visualizacao" >
			<caption>Dados do Discente</caption>
			<tr>
				<th>CPF:</th>
				<td>
					<ufrn:format type="cpf" valor="${ matricularDiscentePlanoMatriculaMBean.obj.pessoa.cpf_cnpj }"></ufrn:format>
				</td>
			</tr>
			<tr>
				<th>Matrícula:</th>
				<td>
					${ matricularDiscentePlanoMatriculaMBean.obj.matricula }
				</td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td>${ matricularDiscentePlanoMatriculaMBean.obj.discente.pessoa.nome }</td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>${ matricularDiscentePlanoMatriculaMBean.obj.matrizCurricular.descricao }</td>
			</tr>
		</table>
		<br/>
		<table class="formulario" width="99%">
			<caption class="listagem">Selecione um Plano de Matrícula</caption>
			<tbody>
				<tr>
					<th class="obrigatorio" valign="top">Plano de Matrícula:</th>
					<td valign="top">
						<h:selectOneRadio id="planoMatricula" layout="pageDirection" onchange="submit()" onclick="submit()"
							valueChangeListener="#{matricularDiscentePlanoMatriculaMBean.carregaDadosPlanoMatricula }"
							value="#{matricularDiscentePlanoMatriculaMBean.planoMatriculaIngressante.id}">
							<f:selectItems value="#{matricularDiscentePlanoMatriculaMBean.planoMatriculaIngressanteCombo}" />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<td class="subFormulario" colspan="2">Turmas do Plano de Matrícula</td>
				</tr>
				<tr>
					<td colspan="2">
						<a4j:outputPanel id="dadosPlanoMatricula" >
							<table class="listagem" width="100%">
								<thead>
								<tr>
									<th width="8%">Código</th>
									<th>Disciplina</th>
									<th>Turma</th>
									<th>Horário</th>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="#{ matricularDiscentePlanoMatriculaMBean.planoMatriculaIngressante.turmas }" var="item">
									<tr>
										<td>${ item.disciplina.codigo }</td>
										<td>${ item.disciplina.nome }</td>
										<td>${ item.codigo }</td>
										<td>${ item.descricaoHorario }</td>
									</tr>
									</c:forEach>
								</tbody>
							</table>
						</a4j:outputPanel>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="<< Buscar Outro Discente" action="#{ matricularDiscentePlanoMatriculaMBean.iniciar }" id="voltar"/>
						<h:commandButton value="Cancelar" action="#{ matricularDiscentePlanoMatriculaMBean.cancelar }" id="cancelar" onclick="#{ confirm }"/>
						<h:commandButton value="Avançar >>" action="#{ matricularDiscentePlanoMatriculaMBean.confirmar }" id="confirmar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br />
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
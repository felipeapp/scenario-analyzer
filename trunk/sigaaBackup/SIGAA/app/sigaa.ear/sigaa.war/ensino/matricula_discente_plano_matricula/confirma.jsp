<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Matricular Discente Usando Plano de Matr�culas</h2>

	<h:form>
		<div class="descricaoOperacao">
			<p>Caro Usu�rio,</p>
			<p>Confirme os dados abaixo para concluir a matr�cula do discente usando um plano de matr�culas.</p> 
		</div>
		<br/>
		<table class="visualizacao" width="80%">
			<caption>Dados do Discente</caption>
			<tr>
				<th>CPF:</th>
				<td>
					<ufrn:format type="cpf" valor="${ matricularDiscentePlanoMatriculaMBean.obj.pessoa.cpf_cnpj }"></ufrn:format>
				</td>
			</tr>
			<tr>
				<th>Matr�cula:</th>
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
			<c:if test="${ not empty matricularDiscentePlanoMatriculaMBean.outrosVinculos }" >
				<tr>
					<th valign="top" class="rotulo">
						<b>
							<h:outputText value="ATEN��O! Ao final do cadastramento o discente ter� o v�nculo CANCELADO:" rendered="#{ matricularDiscentePlanoMatriculaMBean.planoMatriculaIngressante.id > 0 }" />
							<h:outputText value="Outros v�nculos do discente (n�o ser�o cancelados):" rendered="#{ matricularDiscentePlanoMatriculaMBean.planoMatriculaIngressante.id == 0 }" />
						</b>
					</th>
					<td>
						<table class="listagem">
						<thead>
							<th style="text-align: right;">Matr�cula</th>
							<th style="text-align: left;">Nome</th>
							<th style="text-align: left;">Matriz Curricular</th>
						</thead>
						<c:forEach items="#{ matricularDiscentePlanoMatriculaMBean.outrosVinculos }" var="item" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td style="text-align: right;">${item.matricula }</td>
								<td style="text-align: left;">${item.pessoa.nome }</td>
								<td style="text-align: left;">${item.matrizCurricular.descricao }</td>
							</tr>
						</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${ matricularDiscentePlanoMatriculaMBean.planoMatriculaIngressante.id > 0 }">
				<%@include file="/ensino/plano_matricula_ingressantes/_quadro_horario.jsp" %>
			</c:if>
			<tfoot class="formulario">
				<tr>
					<td colspan="2">
						<h:commandButton value="Cadastrar" action="#{ matricularDiscentePlanoMatriculaMBean.cadastrar }" id="cadastrar"/>
						<h:commandButton value="<< Voltar" action="#{ matricularDiscentePlanoMatriculaMBean.formPlanoMatricula }" id="voltar"/>
						<h:commandButton value="Cancelar" action="#{ matricularDiscentePlanoMatriculaMBean.cancelar }" id="cancelar" onclick="#{ confirm }"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
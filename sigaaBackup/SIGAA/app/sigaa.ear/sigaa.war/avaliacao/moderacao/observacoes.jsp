<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="form">

	<div class="descricaoOperacao">
	<c:if test="${moderadorObservacaoBean.discentes}">Edite os comentários dados pelos discentes ao docente nesta turma.</c:if>
	<c:if test="${not moderadorObservacaoBean.discentes}">Edite os comentários dados pelos docentes à esta turma.</c:if>
	<br/>
	Se desejar parar o trabalho e continuar em outro momento, utilize o botão <b>Gravar</b>.<br/>
	<b>Atenção!</b> Quando concluir a edição dos comentários, clicando em <b>Finalizar</b>, não será mais possível editar os comentários.</div>

	<h2><ufrn:subSistema /> > Moderação das Observações
		<c:if test="${moderadorObservacaoBean.discentes}"> para Docentes de Turmas</c:if>
		<c:if test="${not moderadorObservacaoBean.discentes}"> para Turmas</c:if>
	</h2>
	<a4j:keepAlive beanName="moderadorObservacaoBean"></a4j:keepAlive>
	<table class="visualizacao">
		<tr>
			<th>Docente:</th>
			<td>${moderadorObservacaoBean.docenteTurma.docenteDescricao}</td>
		</tr>
		<tr>
			<th>Turma:</th>
			<td>${moderadorObservacaoBean.docenteTurma.turma}</td>
		</tr>
	</table>
	<br/>
	<table class="formulario" width="99%">
		<caption>Edite os Comentários Destinados
			<c:if test="${moderadorObservacaoBean.discentes}"> para Docentes de Turmas</c:if>
			<c:if test="${not moderadorObservacaoBean.discentes}"> para a Turmas</c:if>
		</caption>
		<thead>
			<tr>
				<th style="text-align: center">
					Comentário Realizado
					<c:if test="${moderadorObservacaoBean.discentes}">pelo Discente</c:if>
					<c:if test="${not moderadorObservacaoBean.discentes}">pelo Docente</c:if> 
				</th>
				<th style="text-align: center">
					<h:commandLink value="Copiar Todos >>" action="#{ moderadorObservacaoBean.copiarTodasObservacaoDocenteTurma }" id="copiarTodos"/>
				</th>
				<th style="text-align: center">Comentário Moderado</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{moderadorObservacaoBean.observacoes}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td width="40%" style="text-align: center">
						<h:inputTextarea cols="65" rows="4" readonly="true" value="#{item.observacoes}" />
					</td>
					<td style="text-align: center">
						<h:commandLink value="Copiar >>" action="#{ moderadorObservacaoBean.copiarObservacaoDocenteTurma }" id="copiar" >
							<f:param id="idObservacao" name="idObservacao" value="#{item.id}" />
						</h:commandLink>
					</td>
					<td width="40%" style="text-align: center">
						<h:inputTextarea cols="65" rows="4" value="#{item.observacoesModeradas}" />
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Gravar" action="#{ moderadorObservacaoBean.gravarObservacoes }" id="gravar" />
					<h:commandButton value="Finalizar" action="#{ moderadorObservacaoBean.finalizarObservacoes }" id="finalizar"
					 onclick="return confirm('Atenção!\nUma vez finalizada a moderação de comentários, não será possível editá-la novamente!\n\nDeseja realmente Finalizar?');" />
					<h:commandButton value="<< Selecionar Outro" action="#{ moderadorObservacaoBean.buscar }" id="buscar" /> 
					<h:commandButton value="Cancelar" action="#{ moderadorObservacaoBean.cancelar }" id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
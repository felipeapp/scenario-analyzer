<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="form">

	<div class="descricaoOperacao">Edite os coment�rios dados pelos discentes sobre os trancamentos nesta turma.<br/>
	Se desejar parar o trabalho e continuar em outro momento, utilize o bot�o <b>Gravar</b>.<br/>
	<b>Aten��o!</b>Quando concluir a edi��o dos coment�rios, clicando em <b>Finalizar</b>, n�o ser� mais poss�vel editar os coment�rios.</div>

	<h2><ufrn:subSistema /> > Modera��o das Motivos de Trancamentos de Turmas</h2>
	<a4j:keepAlive beanName="moderadorObservacaoBean"></a4j:keepAlive>
	<table class="visualizacao">
		<tr>
			<th>Turma:</th>
			<td>${moderadorObservacaoBean.turma}</td>
		</tr>
		<tr>
			<th>Docentes:</th>
			<td>${moderadorObservacaoBean.turma.docentes}</td>
		</tr>
	</table>
	<br/>
	<table class="formulario" width="99%">
		<caption>Edite os Coment�rios Sobre o Motivo de Trancamento</caption>
		<thead>
			<tr>
				<th style="text-align: center">Coment�rio Realizado pelo Discente</th>
				<th style="text-align: center">
					<h:commandLink value="Copiar Todos >>" action="#{ moderadorObservacaoBean.copiarTodasObservacaoTrancamento }" id="copiarTodos"/>
				</th>
				<th style="text-align: center">Coment�rio Moderado</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{moderadorObservacaoBean.trancamentos}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td width="40%" style="text-align: center">
						<h:inputTextarea cols="65" rows="4" readonly="true" value="#{item.observacoes}" />
					</td>
					<td style="text-align: center">
						<h:commandLink value="Copiar >>" action="#{ moderadorObservacaoBean.copiarObservacaoTrancamento }" id="copiar" >
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
					<h:commandButton value="Gravar" action="#{ moderadorObservacaoBean.gravarTrancamentos }" id="gravar" />
					<h:commandButton value="Finalizar" action="#{ moderadorObservacaoBean.finalizarTrancamentos }" id="finalizar"
					 onclick="return confirm('Aten��o!\nUma vez finalizada a modera��o de coment�rios, n�o ser� poss�vel edit�-la novamente!\n\nDeseja realmente Finalizar?');" />
					<h:commandButton value="<< Selecionar Outro" action="#{ moderadorObservacaoBean.buscar }" id="buscar" /> 
					<h:commandButton value="Cancelar" action="#{ moderadorObservacaoBean.cancelar }" id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
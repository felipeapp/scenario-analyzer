<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.formulario p {
		padding: 2px 8px 10px;
		line-height: 1.2em;
	}
</style>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > Resumo do Cadastro do Plano de Trabalho </h2>


<h:form id="form">

<table class="formulario" width="100%">
	<caption> Plano de Trabalho </caption>
	<tbody>

		<tr>
			<th width="20%"><b> Código:</b> </th>
			<td> ${planoTrabalhoExtensao.obj.atividade.codigo} </td>
		</tr>

		<tr>
			<th width="20%"><b> Título da Ação:</b> </th>
			<td> ${planoTrabalhoExtensao.obj.atividade.titulo} </td>
		</tr>
		<tr>
			<th><b> Orientador(a):</b> </th>
			<td> ${planoTrabalhoExtensao.obj.orientador.pessoa.nome }</td>
		</tr>
		<c:if test="${not empty planoTrabalhoExtensao.obj.discenteExtensao}">
				<tr>
					<th><b> Discente:</b> </th>
					<td> ${planoTrabalhoExtensao.obj.discenteExtensao.discente.nome }</td>
				</tr>
		
				<tr>
					<th><b>Tipo de Vínculo:</b></th>
					<td><h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.tipoVinculo.descricao}" /></td>
				</tr>

				<tr>
					<th><b>Situação:</b></th>
					<td><h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.situacaoDiscenteExtensao.descricao}" /></td>
				</tr>
		</c:if>

		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center">Corpo do Plano de Trabalho</td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Período de execução:</b> </th>
		</tr>
		<tr>
			<td colspan="2" align="justify"> <p><fmt:formatDate value="${planoTrabalhoExtensao.obj.dataInicio}" pattern="dd/MM/yyyy"/> a <fmt:formatDate value="${planoTrabalhoExtensao.obj.dataFim}" pattern="dd/MM/yyyy"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Objetivos:</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="planoTrabalhoExtensao" property="obj.objetivo"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Justificativa:</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="planoTrabalhoExtensao" property="obj.justificativa"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Descrição das Ações:</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="planoTrabalhoExtensao" property="obj.descricaoAtividades"/></p></td>
		</tr>

		<tr>
			<th colspan="2" style="text-align: left;"> <b>Local de Trabalho do Discente:</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="planoTrabalhoExtensao" property="obj.localTrabalho"/></p></td>
		</tr>

		<tr> <td colspan="2" style="margin:0; padding: 0;">
			<div style="overflow: auto; width: 100%">
			<table id="cronograma" class="subFormulario" width="100%">
			<caption style="text-align: center"> Cronograma de Atividades </caption>
			<thead>
				<tr>
					<th width="30%" rowspan="2"> Atividade </th>
					<c:forEach items="${planoTrabalhoExtensao.telaCronograma.mesesAno}" var="ano">
					<th colspan="${fn:length(ano.value)}" style="text-align: center" class="inicioAno fimAno">
						${ano.key}
					</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${planoTrabalhoExtensao.telaCronograma.mesesAno}" var="ano">
						<c:forEach items="${ano.value}" var="mes" varStatus="status">
						<c:set var="classeCabecalho" value=""/>
						<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
						<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>

						<th class="${classeCabecalho}" style="text-align: center"> ${mes}	</th>
						</c:forEach>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:set var="numeroAtividades" value="${fn:length(planoTrabalhoExtensao.telaCronograma.cronogramas)}" />
				<c:set var="valoresCheckboxes" value=",${fn:join(planoTrabalhoExtensao.telaCronograma.calendario, ',')}" />
				<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
				<tr>
					<th> ${planoTrabalhoExtensao.telaCronograma.atividade[statusAtividades.index-1]} </th>
					<c:forEach items="${planoTrabalhoExtensao.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
						<c:forEach items="${ano.value}" var="mes">
							<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
							<c:set var="classeCelula" value=""/>
							<c:if test="${ fn:contains(valoresCheckboxes, valorCheckbox) }">
								<c:set var="classeCelula" value="selecionado"/>
							</c:if>
							<td align="center" class="${classeCelula}" >
								&nbsp;
							</td>
						</c:forEach>
					</c:forEach>
				</tr>
				</c:forEach>
			</tbody>
			</table>	
			 </div>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="Confirmar Cadastro e Enviar" action="#{planoTrabalhoExtensao.cadastrar}" id="btCadastrar" rendered="#{ !planoTrabalhoExtensao.obj.enviado && planoTrabalhoExtensao.confirmButton != 'Remover' }"/>
					<h:commandButton value="Salvar (Rascunho)" action="#{planoTrabalhoExtensao.salvar}" id="btSalvar" rendered="#{ planoTrabalhoExtensao.confirmButton != 'Remover' }" />
					<h:commandButton value="<< Voltar" action="#{planoTrabalhoExtensao.irCronograma}"  id="btVoltarCronograma" rendered="#{ planoTrabalhoExtensao.confirmButton != 'Remover' }" />
					
					<h:commandButton value="#{ planoTrabalhoExtensao.confirmButton }" action="#{planoTrabalhoExtensao.remover}" id="btRemover" rendered="#{ planoTrabalhoExtensao.confirmButton == 'Remover' }"/>
					<h:commandButton value="Cancelar" action="#{planoTrabalhoExtensao.cancelar}" onclick="#{confirm }" id="btCancelar"/>
			</td>
		</tr>
	</tfoot>
</table>

</h:form>

</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

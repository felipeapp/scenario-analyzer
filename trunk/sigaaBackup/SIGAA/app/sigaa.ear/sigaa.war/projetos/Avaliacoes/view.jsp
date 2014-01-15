<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2>Visualização da Avaliação do Projeto</h2>

<h:form id="form">
	<table class="formulario" width="90%">
		<caption>Dados do Projeto Avaliado</caption>
		<tr>
			<td>
				<table width="100%">
					<tr>
						<th class="rotulo" width="20%">Título Projeto:</th>
						<td>${avaliacaoProjetoBean.obj.projeto.anoTitulo}</td>							
					</tr>

					<tr>
						<th class="rotulo" width="20%">Área do CNPq:</th>
						<td>${avaliacaoProjetoBean.obj.projeto.areaConhecimentoCnpq.nome}</td>							
					</tr>

					<tr>
						<th class="rotulo" width="20%">Tipo de Avaliação:</th>
						<td>${avaliacaoProjetoBean.obj.distribuicao.modeloAvaliacao.tipoAvaliacao.descricao}</td>							
					</tr>
	
					<tr>
						<th class="rotulo" width="20%">Tipo de Avaliador:</th>
						<td>${avaliacaoProjetoBean.obj.distribuicao.tipoAvaliador.descricao}</td>							
					</tr>

					<tr>
						<th class="rotulo" width="20%">Situação da Avaliação:</th>
						<td>${avaliacaoProjetoBean.obj.situacao.descricao}</td>							
					</tr>

					<tr>
						<th class="rotulo">Data da Avaliação:</th>
						<td><fmt:formatDate value="${avaliacaoProjetoBean.obj.dataAvaliacao}" pattern="dd/MM/yyyy HH:mm:ss" /></td>							
					</tr>
				</table>
		  </td>
		</tr>
		<tr>
			<td>
				<table width="100%">
					<caption>${avaliacaoProjetoBean.obj.distribuicao.modeloAvaliacao.questionario.descricao}</caption>
					
					<thead>
						<tr>
							<td>Descrição do Item Avaliado</td>
							<td width="7%" style="text-align: right;">Máximo</td>
							<td width="7%" style="text-align: right;">Peso</td>
							<td width="7%" style="text-align: right;">Nota</td>
						</tr>	
					</thead>
				
					<c:forEach items="${ avaliacaoProjetoBean.obj.notas }" var="nota" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${ nota.itemAvaliacao.pergunta.descricao }</td>
							<td style="text-align: right;"><fmt:formatNumber pattern="#0.0" value="${ nota.itemAvaliacao.notaMaxima }"/></td>
							<td style="text-align: right;"><fmt:formatNumber pattern="#0.0" value="${ nota.itemAvaliacao.peso }"/></td>
							<td style="text-align: right;"><fmt:formatNumber pattern="#0.0" value="${ nota.nota }"/></td>
						</tr>
					</c:forEach>
					
					<tfoot>
						<tr>
							<td colspan="4" align="center"><strong>Total Avaliação: <span id="total"><fmt:formatNumber pattern="#0.0" value="${ avaliacaoProjetoBean.obj.nota }"/></span></strong></td>
						</tr>
					</tfoot>
				</table>
			</td>
		</tr>
			
		<tr>
			<td>
				<b>Parecer: </b><br/>
				<span style="text-align: justify;">${avaliacaoProjetoBean.obj.parecer}</span>
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="3">					
					<h:commandButton id="btConfirmarRemocao" value="#{avaliacaoProjetoBean.confirmButton}" action="#{buscaAvaliacoesProjetosBean.remover}" rendered="#{avaliacaoProjetoBean.confirmButton == 'Remover'}"/>
					<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" />
					<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoProjetoBean.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
		
	</table>
</h:form>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
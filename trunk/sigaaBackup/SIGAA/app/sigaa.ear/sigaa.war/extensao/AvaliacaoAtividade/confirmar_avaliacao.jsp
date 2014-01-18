<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria"%>


<f:view>

												

	<h2><ufrn:subSistema /> > Confirmação da Avaliação do Projeto</h2>
	<h:form id="formConfirmaAvaliacao">
	
	<table class="formulario" width="95%">
		<caption>Confirmação da Avaliação do Projeto</caption>
		<tr>
			<td>
				<table width="100%" class="formulario">
					<tr>
						<th><b>Título Projeto:</b></th>
						<td>${avaliacaoAtividade.obj.atividade.titulo}</td>							
					</tr>
	
					<tr>
						<th><b>Avaliador:</b></th>
						<td>
						    ${avaliacaoAtividade.obj.avaliadorAtividadeExtensao.servidor.nome}
						    ${avaliacaoAtividade.obj.membroComissao.servidor.nome}
						</td>
					</tr>
					
					<tr>
						<th><b>Tipo de Avaliação:</b></th>
						<td>${avaliacaoAtividade.obj.tipoAvaliacao.descricao}</td>							
					</tr>					
					
					<tr>
						<th><b>Data Avaliação:</b></th>
						<td><fmt:formatDate value="${dataAtual}" pattern="dd/MM/yyyy HH:mm:ss" /></td>							
					</tr>					
					
					
					<tr>
						<th><b>Situação Avaliação:</b></th>
						<td>${avaliacaoAtividade.obj.statusAvaliacao.descricao}</td>							
					</tr>
				</table>
		  </td>
		</tr>
		<c:if test="${avaliacaoAtividade.obj.tipoAvaliacao.id == 1}"> <%-- Avaliação ad hoc --%>
		<tr>
			<td>
					<table width="100%" class="subFormulario">
	
						<thead>
							<tr>
								<td>Descrição do Item Avaliado</td>
								<td width="7%" style="text-align: right;">Máximo</td>
								<td width="7%" style="text-align: right;">Peso</td>
								<td width="7%" style="text-align: right;">Nota</td>
							</tr>	
						</thead>
					
						<c:forEach items="${ avaliacaoAtividade.obj.notasItem }" var="nota" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>${ nota.itemAvaliacao.descricao }</td>
								<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${ nota.itemAvaliacao.notaMaxima }"/></td>
								<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${ nota.itemAvaliacao.peso }"/></td>
								<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${ nota.nota }"/></td>
							</tr>
						</c:forEach>
	
					</table>
					<br/>&nbsp;
				</td>
			</tr>
		</c:if>
		
		<tr bgcolor="#C8D5EC">
			<td colspan="2" align="center"><strong>Total Avaliação: <span id="total"><fmt:formatNumber pattern="#0.00" value="${ avaliacaoAtividade.obj.nota }"/></span></strong></td>
		</tr>
			
		<tr>
			<td>
				<b>Parecer:</b>${avaliacaoAtividade.obj.parecer.descricao}
			</td>
		</tr>
		
		<tr>
			<td>
				<b>Justificativa:</b>	<br/>
				<h:outputText value="#{avaliacaoAtividade.obj.justificativa}"/>
			</td>
		</tr>
		
		<tr>
			<td colspan="4" class="subFormulario">Confirmação de Senha</td>
		</tr>
		<tr>
			<td colspan="4">
				<div align="center">
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</div>
			</td>	
		</tr>
		
		<tfoot>
			<tr>
				<td>					
					<h:commandButton id="btConfirmarAvaliacao" value="Confirmar Avaliação"action="#{avaliacaoAtividade.confirmarAvaliacaoParecerista}"/>
					
					<c:if test="${avaliacaoAtividade.obj.tipoAvaliacao.id == 1}"> <%-- Avaliação ad hoc --%> 
						<h:commandButton id="btVoltar" value="<< Voltar"action="#{avaliacaoAtividade.redirecionaPaginaAvaliacao}"/>
					</c:if>
															
					<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoAtividade.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
		
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
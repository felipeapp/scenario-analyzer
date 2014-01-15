<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria"%>


<f:view>

	<c:set var="AVALIACAO_PROJETO_ENSINO" value="<%= String.valueOf(TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO) %>" scope="application"/>												

	<h2>Avalia��o do Projeto de Ensino</h2>
	<table class="tabelaRelatorio" width="100%">
	<caption>Avalia��o do Projeto de Ensino</caption>	
		
		<tr>
			<td colspan="2">
				<table width="100%" class="subFormulario">
						<tr>
							<th width="22%"><b>Ano:</b></th>
							<td>${avalProjetoMonitoria.obj.projetoEnsino.ano}</td>							
						</tr>
						<tr>
							<th><b>T�tulo Projeto:</b></th>
							<td>${avalProjetoMonitoria.obj.projetoEnsino.titulo}</td>							
						</tr>

					    <c:if test="${acesso.monitoria or acesso.comissaoMonitoria}">
							<tr>
								<th><b>Coordenador(a):</b></th>
								<td>${avalProjetoMonitoria.obj.projetoEnsino.coordenacao.pessoa.nome}</td>							
							</tr>
						</c:if>

					    <c:if test="${acesso.monitoria or acesso.comissaoMonitoria}">
							<tr>
								<th><b>Avaliador(a):</b></th>
								<td>${avalProjetoMonitoria.obj.avaliador.servidor.nome}</td>							
							</tr>
						</c:if>
						
						<tr>
							<th><b>Tipo de Avalia��o:</b></th>
							<td>${avalProjetoMonitoria.obj.tipoAvaliacao.descricao}</td>							
						</tr>					
						
						<tr>
							<th><b>Data Avalia��o:</b></th>
							<td><fmt:formatDate value="${avalProjetoMonitoria.obj.dataAvaliacao}" pattern="dd/MM/yyyy HH:mm:ss" /></td>							
						</tr>					
						
						
						<tr>
							<th><b>Situa��o Avalia��o:</b></th>
							<td>${avalProjetoMonitoria.obj.statusAvaliacao.descricao}</td>							
						</tr>					
				</table>
			</td>
		</tr>
	

	
	<c:if test="${avalProjetoMonitoria.obj.tipoAvaliacao.id == AVALIACAO_PROJETO_ENSINO}">
		
		<tr>
			<td colspan="2">
				<table width="100%" class="subFormulario">

					<thead>
						<tr><td width="80%">Descri��o do Item Avaliado</td><td width="10%" style="text-align: right;">Nota</td><td width="10%" style="text-align: right;">M�ximo</td></tr>
					</thead>
				
					<c:forEach items="${ avalProjetoMonitoria.obj.notasItem }" var="nota" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${ nota.itemAvaliacao.descricao }</td>
							<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${ nota.nota }"/></td>
							<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${ nota.itemAvaliacao.notaMaxima }"/></td>
						</tr>
					</c:forEach>

				</table>
				<br/>&nbsp;
			</td>
		</tr>
	
		<tr bgcolor="#C8D5EC">
			<td colspan="2" align="center"><strong>Total Avalia��o: <span id="total"><fmt:formatNumber pattern="#0.00" value="${ avalProjetoMonitoria.obj.notaAvaliacao }"/></span></strong></td>
		</tr>
		
	</c:if>
	
	<tr>
		<td>
		<b>Parecer:</b>	<br/>
		<h:outputText value="#{avalProjetoMonitoria.obj.parecer}"/></td>
	</tr>
	
	<tr><td colspan="2"></td></tr>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="solicitacaoReposicaoProva" />
	<h2><ufrn:subSistema /> > Reposi��o de Avalia��o > Exibir Solicita��es</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Aluno,</b></p>
		<p>Nesta tela ser�o exibidas todas as suas solicita��es de reposi��o de avalia��o.</p>
		<p>Voc� poder� cancelar uma solicita��o somente se estiver com status igual a CADASTRADA, ou seja, que n�o foi Analisada.</p>
	</div>
	
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicita��o
			<h:graphicImage value="/img/delete.png" style="overflow: visible;"/>: Cancelar Solicita��o
		</div>
	</center>		
	
	<h:form id="form">
		<table class="listagem">
			<caption>Lista de Solicita��es</caption>
			<thead>
				<tr>
					<th style="text-align: center;">Data da Solicita��o</th>
					<th>Discente</th>
					<th>Curso</th>
					<th>Avalia��o</th>
					<th style="text-align: center;">Data da Avalia��o</th>
					<th>Status</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<c:if test="${empty solicitacaoReposicaoProva.listaSolicitacoes}">		
				<tr>
					<td colspan="8" align="center"><i>Nenhum Solicita��o Cadastrada.</i></td>
				</tr>
			</c:if>			
			<c:set var="turma" value="0"/>
			<c:set var="aval" value="0"/>
			<c:if test="${not empty solicitacaoReposicaoProva.listaSolicitacoes}">		
				<c:forEach items="#{solicitacaoReposicaoProva.listaSolicitacoes}" var="linha" varStatus="loop">
					<c:if test="${turma != linha.turma.id}">
						<tr>
							<td colspan="8" style="background-color: #C8D5EC;">
								<c:set var="turma" value="#{linha.turma.id}"/>
								<b>${linha.turma.descricaoCodigo}</b>
							</td>
						</tr>		
					</c:if>	
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: center;"><ufrn:format type="dataHora" valor="${linha.dataCadastro}"/></td>
						<td>${linha.discente.matriculaNome}</td>
						<td>${linha.discente.curso.descricao}</td>
						<td>
						   ${linha.dataAvaliacao.descricao}
						</td>
						<td style="text-align: center;">
							<ufrn:format type="data" valor="${linha.dataAvaliacao.data}"/>
						</td>
						<td>${linha.descricaoStatus}</td>
						<td>
							<h:commandButton image="/img/view.gif" title="Visualizar Solicita��o"
									action="#{solicitacaoReposicaoProva.view}" styleClass="noborder">
									<f:setPropertyActionListener value="#{linha}" target="#{solicitacaoReposicaoProva.obj}"/>
							</h:commandButton>							
						</td>
						<td>
							<c:if test="${linha.cadastrada && linha.statusParecer == null}">								
								<h:commandLink title="Cancelar Solicita��o" action="#{solicitacaoReposicaoProva.cancelarSolicitacao}" onclick="#{confirmDelete}" immediate="true">
									<h:graphicImage value="/img/delete.png"/>
									<f:setPropertyActionListener value="#{linha}" target="#{solicitacaoReposicaoProva.obj}"/>
								</h:commandLink>									
							</c:if>			
						</td> 	
					</tr>				
				</c:forEach>
			</c:if>
		</table>
	</h:form>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	